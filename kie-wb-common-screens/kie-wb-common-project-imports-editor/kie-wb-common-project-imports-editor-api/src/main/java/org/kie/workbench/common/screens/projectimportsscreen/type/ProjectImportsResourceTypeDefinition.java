/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.projectimportsscreen.type;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class ProjectImportsResourceTypeDefinition
        implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "project imports";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getPrefix() {
        return "project";
    }

    @Override
    public String getSuffix() {
        return "imports";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getSimpleWildcardPattern() {
        return getPrefix() + "." + getSuffix();
    }

    @Override
    public boolean accept( final Path path ) {
        return path.getFileName().endsWith( getPrefix() + "." + getSuffix() );
    }
}
