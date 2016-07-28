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
import java.io.InputStream;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.validation.GenericValidator;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

/**
 * Validator capable of validating generic Kie assets (i.e those that are handled by KieBuilder)
 */
public class DefaultGenericKieValidator implements GenericValidator {

    private BuildService buildService;

    private KieProjectService projectService;

    private IOService ioService;

    public DefaultGenericKieValidator() {
    }

    @Inject
    public DefaultGenericKieValidator( final KieProjectService projectService,
                                       final BuildService buildService,
                                       @Named("ioStrategy") IOService ioService) {
        this.projectService = projectService;
        this.buildService = buildService;
        this.ioService = ioService;
    }

    @Override
    public List<ValidationMessage> validate( final Path resourcePath ) {
        return validator( resourcePath ).validate( resourcePath );
    }

    @Override
    public List<ValidationMessage> validate(  final Path resourcePath, final InputStream byteArrayInputStream ) {


        return validator( resourcePath ).validate( resourcePath, byteArrayInputStream );
    }

    private Validator validator( final Path resourcePath ) {
        return new Validator( projectService, buildService, ioService );
    }
}
