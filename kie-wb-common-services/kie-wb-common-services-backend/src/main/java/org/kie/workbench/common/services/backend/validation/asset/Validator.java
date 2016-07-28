/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.validation.asset;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

public class Validator {

    protected List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
    private final static String ERROR_CLASS_NOT_FOUND = "Definition of class \"{0}\" was not found. Consequentially validation cannot be performed.\nPlease check the necessary external dependencies for this project are configured correctly.";

    private final IOService ioService;

    private final ProjectService projectService;
    private final BuildService buildService;

    public Validator( final ProjectService projectService,
                      final BuildService buildService,
                      final IOService ioService ) {
        this.projectService = projectService;
        this.buildService = buildService;
        this.ioService = ioService;
    }

    public List<ValidationMessage> validate(  final Path resourcePath, final InputStream bla  ) {

        BufferedInputStream stream = new BufferedInputStream( ioService.newInputStream( Paths.convert( resourcePath ) ) );
        Resource resource = KieServices.Factory.get().getResources().newInputStreamResource( stream );

//        ioService.write( Paths.convert( resourcePath ),  );
//        KieServices.Factory.get().getResources().newInputStreamResource( new BufferedInputStream( resource )
//        return validate( resource.ge );
        return null;
    }

    public List<ValidationMessage> validate( Path resourcePath ) {
        try {
            validationMessages = new ArrayList<ValidationMessage>();

            for ( final ValidationMessage message : buildIncrementally( resourcePath ) ) {
                addMessage( resourcePath, message );
            }

        } catch ( NoProjectException e ) {
            return new ArrayList<ValidationMessage>();
        } catch ( NoClassDefFoundError e ) {
            validationMessages.add( new ValidationMessage( Level.ERROR, MessageFormat.format( ERROR_CLASS_NOT_FOUND, e.getLocalizedMessage() ) ) );
        } catch ( Throwable e ) {
            validationMessages.add( new ValidationMessage( Level.ERROR, e.getLocalizedMessage() ) );
        }

        return validationMessages;
    }

    protected void addMessage( final Path path,
                               final ValidationMessage message ) throws NoProjectException {
        final String destinationURI = removeFileExtension( path.toURI() );
        final String messageURI = messageURI( message );

//        if ( messageURI == null || "".equals( messageURI ) || destinationURI.endsWith( messageURI ) ) {
            validationMessages.add( message );
//        }
    }

    private List<ValidationMessage> buildIncrementally( final Path resourcePath ) throws NoProjectException {
        List<BuildMessage> buildMessages = new ArrayList<>();
        List<BuildMessage> incrementalBuildMessages;

        if ( !buildService.isBuilt( project( resourcePath ) ) ) {
            buildMessages = build( resourcePath ).getMessages();
        }

        incrementalBuildMessages = updateTheBuild( resourcePath ).getAddedMessages();

        return formatResults( buildMessages, incrementalBuildMessages );
    }

    private List<ValidationMessage> formatResults( final List<BuildMessage> buildMessages,
                                                   final List<BuildMessage> incrementalBuildMessages ) {
        List<ValidationMessage> validationMessages = new ArrayList<>();

        validationMessages.addAll( filterValidationMessages( buildMessages ) );
        validationMessages.addAll( filterValidationMessages( incrementalBuildMessages ) );

        return validationMessages;
    }

    private IncrementalBuildResults updateTheBuild( final Path resourcePath ) {
        return buildService.updatePackageResource( resourcePath );
    }

    private BuildResults build( final Path resourcePath ) throws NoProjectException {
        return buildService.build( project( resourcePath ) );
    }

    private Project project( final Path resourcePath ) throws NoProjectException {
        Project project = projectService.resolveProject( resourcePath );

        if ( project == null ) {
            throw new NoProjectException();
        }

        return project;
    }

    private String messageURI( final ValidationMessage message ) {
        return message.getPath() != null ? removeFileExtension( message.getPath().toURI() ) : null;
    }

    private String removeFileExtension( final String pathURI ) {
        if ( pathURI != null && pathURI.contains( "." ) ) {
            return pathURI.substring( 0, pathURI.lastIndexOf( "." ) );
        }

        return pathURI;
    }

    private List<ValidationMessage> filterValidationMessages( List<BuildMessage> messages ) {
        return messages
                .stream()
                .filter( message -> message.getLevel() == Level.ERROR )
                .map( message -> new ValidationMessage( message.getId(),
                                                        message.getLevel(),
                                                        message.getPath(),
                                                        message.getLine(),
                                                        message.getColumn(),
                                                        ":))" + message.getText() ) )
                .collect( Collectors.toList() );
    }
}
