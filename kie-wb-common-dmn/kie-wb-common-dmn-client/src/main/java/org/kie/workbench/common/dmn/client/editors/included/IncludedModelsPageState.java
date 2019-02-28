/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.included;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.Import;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

import static java.util.Collections.emptyList;

@ApplicationScoped
public class IncludedModelsPageState {

    private final DMNGraphUtils dmnGraphUtils;

    private Diagram diagram;

    @Inject
    public IncludedModelsPageState(final DMNGraphUtils dmnGraphUtils) {
        this.dmnGraphUtils = dmnGraphUtils;
    }

    public void setDiagram(final Diagram diagram) {
        this.diagram = diagram;
    }

    public List<Import> getImports() {
        return getDiagram()
                .map(this::getImports)
                .orElse(emptyList());
    }

    private List<Import> getImports(final Diagram diagram) {
        return dmnGraphUtils
                .getDefinitions(diagram)
                .getImport();
    }

    private Optional<Diagram> getDiagram() {
        return Optional.ofNullable(diagram);
    }
}