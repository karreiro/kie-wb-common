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

package org.kie.workbench.common.stunner.project.editor.impl;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.editor.ProjectDiagramResource;

import static org.kie.workbench.common.stunner.project.editor.ProjectDiagramResource.Type.DIAGRAM;
import static org.kie.workbench.common.stunner.project.editor.ProjectDiagramResource.Type.XML;

@Portable
public class ProjectDiagramResourceImpl implements ProjectDiagramResource {

    private ProjectDiagram projectDiagram;

    private String diagramXml;

    private Type type;

    public ProjectDiagramResourceImpl(final @MapsTo("projectDiagram") ProjectDiagram projectDiagram,
                                      final @MapsTo("diagramXml") String diagramXml,
                                      final @MapsTo("type") Type type) {
        this.projectDiagram = projectDiagram;
        this.diagramXml = diagramXml;
        this.type = type;
    }

    public ProjectDiagramResourceImpl(final ProjectDiagram projectDiagram) {
        this(projectDiagram, null, DIAGRAM);
    }

    public ProjectDiagramResourceImpl(final String diagramXml) {
        this(null, diagramXml, XML);
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ProjectDiagramResourceImpl that = (ProjectDiagramResourceImpl) o;

        if (projectDiagram != null ? !projectDiagram.equals(that.getProjectDiagram()) : that.getProjectDiagram() != null) {
            return false;
        }
        if (diagramXml != null ? !diagramXml.equals(that.getDiagramXml()) : that.getDiagramXml() != null) {
            return false;
        }

        return type == that.getType();
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(projectDiagram),
                                         Objects.hashCode(diagramXml),
                                         Objects.hashCode(type));
    }

    @Override
    public ProjectDiagram getProjectDiagram() {
        return projectDiagram;
    }

    @Override
    public String getDiagramXml() {
        return diagramXml;
    }

    @Override
    public Type getType() {
        return type;
    }
}
