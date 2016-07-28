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

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.test.TestFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import static junit.framework.TestCase.*;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ValidatorTest {

    @Mock
    Path path;

    private TestFileSystem testFileSystem;

    private Validator validator;

    @Before
    public void setUp() throws Exception {
        testFileSystem = new TestFileSystem();
        validator = new Validator( projectService(), buildService(), null );
    }

    @After
    public void tearDown() throws Exception {
        testFileSystem.tearDown();
    }

    @Test
    public void testValidateWhenIsValid() throws Throwable {
        Path path = path( "/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl" );

        List<ValidationMessage> errors = validator.validate( path );

        assertTrue( errors.isEmpty() );
    }

    @Test
    public void testValidateWhenTheresNoProject() throws Exception {
        Path path = path( "/META-INF/beans.xml" );

        List<ValidationMessage> errors = validator.validate( path );

        assertTrue( errors.isEmpty() );
    }

    @Test
    public void testAddMessageWhenMessageIsInvalid() throws Throwable {
        Path path = path( "/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl" );
        ValidationMessage errorMessage = errorMessage( path( "/GuvnorM2RepoDependencyExample1/src/main/resources/rule1.drl" ) );

        validator.addMessage( path, errorMessage );

        assertTrue( validator.validationMessages.isEmpty() );

    }

    @Test
    public void testAddMessageWhenMessageIsValid() throws Throwable {
        Path path = path( "/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl" );
        ValidationMessage errorMessage = errorMessage( path );

        validator.addMessage( path, errorMessage );

        assertFalse( validator.validationMessages.isEmpty() );

    }

    @Test
    public void testAddMessageWhenMessageIsBlank() throws Throwable {
        Path path = path( "/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl" );
        ValidationMessage errorMessage = errorMessage( null );

        validator.addMessage( path, errorMessage );

        assertFalse( validator.validationMessages.isEmpty() );

    }

    private ValidationMessage errorMessage( Path path ) {
        return new ValidationMessage( 0, Level.ERROR, path, 0, 0, null );
    }

    private Path path( final String resourceName ) throws URISyntaxException {
        final URL urlToValidate = this.getClass().getResource( resourceName );
        return Paths.convert( testFileSystem.fileSystemProvider.getPath( urlToValidate.toURI() ) );
    }

    private BuildService buildService() {
        return testFileSystem.getReference( BuildService.class );
    }

    private KieProjectService projectService() {
        return testFileSystem.getReference( KieProjectService.class );
    }
}
