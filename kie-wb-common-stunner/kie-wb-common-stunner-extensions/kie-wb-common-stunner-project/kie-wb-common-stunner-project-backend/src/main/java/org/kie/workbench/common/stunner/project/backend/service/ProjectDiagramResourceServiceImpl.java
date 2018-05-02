/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.project.backend.service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.project.editor.ProjectDiagramResource;
import org.kie.workbench.common.stunner.project.editor.ProjectDiagramResource.Type;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramResourceService;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.service.RenameService;

import static org.kie.workbench.common.stunner.project.editor.ProjectDiagramResource.Type.DIAGRAM;
import static org.kie.workbench.common.stunner.project.editor.ProjectDiagramResource.Type.XML;

@Service
@ApplicationScoped
public class ProjectDiagramResourceServiceImpl implements ProjectDiagramResourceService {

    private final ProjectDiagramService projectDiagramService;

    private final RenameService renameService;

    private final SaveAndRenameServiceImpl<ProjectDiagramResource, Metadata> saveAndRenameService;

    public ProjectDiagramResourceServiceImpl() {
        this(null, null, null);
    }

    @Inject
    public ProjectDiagramResourceServiceImpl(final ProjectDiagramService projectDiagramService,
                                             final RenameService renameService,
                                             final SaveAndRenameServiceImpl<ProjectDiagramResource, Metadata> saveAndRenameService) {
        this.projectDiagramService = projectDiagramService;
        this.renameService = renameService;
        this.saveAndRenameService = saveAndRenameService;
    }

    @PostConstruct
    public void init() {
        saveAndRenameService.init(this);
    }

    @Override
    public Path save(final Path path,
                     final ProjectDiagramResource resource,
                     final Metadata metadata,
                     final String comment) {

        final Type type = resource.getType();
        final Map<Type, Function<ProjectDiagramResource, Path>> saveOperations = new HashMap<>();

        saveOperations.put(DIAGRAM, (r) -> projectDiagramService.save(path, r.getProjectDiagram(), metadata, comment));
        saveOperations.put(XML, (r) -> projectDiagramService.saveAsXml(path, r.getDiagramXml(), metadata, comment));

        return saveOperations.get(type).apply(resource);
    }

    @Override
    public Path rename(final Path path,
                       final String newName,
                       final String comment) {
        return renameService.rename(path, newName, comment);
    }

    @Override
    public Path saveAndRename(final Path path,
                              final String newFileName,
                              final Metadata metadata,
                              final ProjectDiagramResource resource,
                              final String comment) {
        return saveAndRenameService.saveAndRename(path, newFileName, metadata, resource, comment);
    }
}
