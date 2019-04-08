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

package org.kie.workbench.common.dmn.backend.editors.types;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.dmn.api.definition.v1_1.Import;
import org.kie.workbench.common.dmn.api.editors.types.DMNIncludeModel;
import org.kie.workbench.common.dmn.api.editors.types.DMNIncludeModelsService;
import org.kie.workbench.common.dmn.api.editors.types.DMNIncludedNode;
import org.kie.workbench.common.dmn.backend.editors.types.common.DMNIncludeModelFactory;
import org.kie.workbench.common.dmn.backend.editors.types.common.DMNNodesTransform;
import org.kie.workbench.common.dmn.backend.editors.types.exceptions.DMNIncludeModelCouldNotBeCreatedException;
import org.kie.workbench.common.dmn.backend.editors.types.query.DMNValueFileExtensionIndexTerm;
import org.kie.workbench.common.dmn.backend.editors.types.query.DMNValueRepositoryRootIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.query.RefactoringQueryServiceImpl;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramRepresentation;
import org.kie.workbench.common.stunner.core.service.DiagramLookupService;
import org.uberfire.backend.vfs.Path;

import static java.lang.Boolean.TRUE;
import static org.kie.workbench.common.dmn.backend.editors.types.query.FindAllDmnAssetsQuery.NAME;

@Service
public class DMNIncludeModelsServiceImpl implements DMNIncludeModelsService {

    private static Logger LOGGER = Logger.getLogger(DMNIncludeModelsServiceImpl.class.getName());

    private final RefactoringQueryServiceImpl refactoringQueryService;

    private final DiagramLookupService diagramLookupService;

    private final DMNIncludeModelFactory includeModelFactory;

    private final DMNNodesTransform nodesTransform;

    @Inject
    public DMNIncludeModelsServiceImpl(final RefactoringQueryServiceImpl refactoringQueryService,
                                       final DiagramLookupService diagramLookupService,
                                       final DMNIncludeModelFactory includeModelFactory,
                                       final DMNNodesTransform nodesTransform) {
        this.refactoringQueryService = refactoringQueryService;
        this.diagramLookupService = diagramLookupService;
        this.includeModelFactory = includeModelFactory;
        this.nodesTransform = nodesTransform;
    }

    @Override
    public List<DMNIncludeModel> loadModels(final WorkspaceProject workspaceProject) {
        return getPaths(workspaceProject)
                .stream()
                .map(getPathDMNIncludeModelFunction())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<DMNIncludedNode> loadNodesByNamespaces(final WorkspaceProject workspaceProject,
                                                       final List<String> namespaces) {
        List<DMNIncludedNode> collect = getPaths(workspaceProject)
                .stream()
                .map(path -> nodesTransform.getNodes(path, namespaces))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return collect;
    }

    private Function<Path, DMNIncludeModel> getPathDMNIncludeModelFunction() {
        return (Path path) -> {
            try {
                return includeModelFactory.create(path);
            } catch (final DMNIncludeModelCouldNotBeCreatedException e) {
                LOGGER.warning("The 'DMNIncludeModel' could not be created for " + path.toURI());
                return null;
            }
        };
    }

    private List<Path> getPaths(final WorkspaceProject workspaceProject) {
        if (workspaceProject != null) {
            return getPathsByWorkspaceProject(workspaceProject);
        } else {
            return getStandalonePaths();
        }
    }

    private List<Path> getStandalonePaths() {
        final DiagramLookupRequest request = new DiagramLookupRequest.Builder().build();
        return diagramLookupService
                .lookup(request)
                .getResults()
                .stream()
                .map(DiagramRepresentation::getPath)
                .collect(Collectors.toList());
    }

    private List<Path> getPathsByWorkspaceProject(final WorkspaceProject workspaceProject) {
        final RefactoringPageRequest request = buildRequest(workspaceProject.getRootPath().toURI());
        return refactoringQueryService
                .query(request)
                .getPageRowList()
                .stream()
                .map(row -> (Path) row.getValue())
                .collect(Collectors.toList());
    }

    private RefactoringPageRequest buildRequest(final String rootPath) {
        return new RefactoringPageRequest(NAME, queryTerms(rootPath), 0, 1000, TRUE);
    }

    private Set<ValueIndexTerm> queryTerms(final String rootPath) {

        final Set<ValueIndexTerm> queryTerms = new HashSet<>();

        queryTerms.add(new DMNValueRepositoryRootIndexTerm(rootPath));
        queryTerms.add(new DMNValueFileExtensionIndexTerm());

        return queryTerms;
    }
}
