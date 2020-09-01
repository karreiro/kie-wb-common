/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.project.client.editor;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.dmn.api.DMNContentResource;
import org.kie.workbench.common.dmn.api.DMNContentService;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.marshaller.DMNMarshallerService;
import org.kie.workbench.common.dmn.client.marshaller.marshall.DMNMarshaller;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.DMNUnmarshaller;
import org.kie.workbench.common.dmn.project.api.factory.impl.DMNProjectDiagramFactory;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramSavedEvent;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.service.DiagramLookupService;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectDiagramImpl;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.promise.Promises;

@DMNEditor
public class DMNClientProjectDiagramService extends ClientProjectDiagramService {

    private final Caller<DMNContentService> dmnContentServiceCaller;
    private final DMNUnmarshaller dmnMarshallerKogitoUnmarshaller;
    private final DMNMarshaller dmnMarshallerKogitoMarshaller;
    private final DMNProjectDiagramFactory dmnDiagramFactory;
    private final DefinitionManager definitionManager;
    private final Promises promises;
    private final DMNDiagramsSession dmnDiagramsSession;
    private final DMNMarshallerService dmnMarshallerService;

    private ServiceCallback<Diagram> onLoadDiagramCallback;

    @Inject
    public DMNClientProjectDiagramService(final ShapeManager shapeManager,
                                          final SessionManager sessionManager,
                                          final Caller<ProjectDiagramService> diagramServiceCaller,
                                          final Caller<DiagramLookupService> diagramLookupServiceCaller,
                                          final Event<SessionDiagramSavedEvent> saveEvent,
                                          final Caller<DMNContentService> dmnContentServiceCaller,
                                          final DMNUnmarshaller dmnMarshallerKogitoUnmarshaller,
                                          final DMNMarshaller dmnMarshallerKogitoMarshaller,
                                          final DMNProjectDiagramFactory dmnDiagramFactory,
                                          final DefinitionManager definitionManager,
                                          final Promises promises,
                                          final DMNDiagramsSession dmnDiagramsSession,
                                          final DMNMarshallerService dmnMarshallerService) {

        super(shapeManager,
              sessionManager,
              diagramServiceCaller,
              diagramLookupServiceCaller,
              saveEvent);

        this.dmnContentServiceCaller = dmnContentServiceCaller;
        this.dmnMarshallerKogitoUnmarshaller = dmnMarshallerKogitoUnmarshaller;
        this.dmnMarshallerKogitoMarshaller = dmnMarshallerKogitoMarshaller;
        this.dmnDiagramFactory = dmnDiagramFactory;
        this.definitionManager = definitionManager;
        this.promises = promises;
        this.dmnDiagramsSession = dmnDiagramsSession;
        this.dmnMarshallerService = dmnMarshallerService;
    }

    @Override
    public void getByPath(final Path path,
                          final ServiceCallback<ProjectDiagram> callback) {

        final String defSetId = BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class);

        dmnContentServiceCaller.call((final DMNContentResource resource) -> {

            final ServiceCallback<Diagram> callback1 = new ServiceCallback<Diagram>() {
                @Override
                public void onSuccess(final Diagram diagram) {

                    final String name = diagram.getName();
                    final Graph graph = diagram.getGraph();
//                    final org.kie.workbench.common.stunner.core.diagram.Metadata metadata = resource.getMetadata();

//                    final String name = null;
//                    final Graph<DefinitionSet, ?> graph = null;
                    final ProjectMetadata metadata = (ProjectMetadata) resource.getMetadata();
//                    final ProjectDiagram projectDiagram = (ProjectDiagram) diagram;
                    callback.onSuccess(new ProjectDiagramImpl(name, graph, metadata));
                }

                @Override
                public void onError(final ClientRuntimeError error) {
                    callback.onError(error);
                }
            };

            dmnMarshallerService.unmarshall(resource.getMetadata(),
                                            resource.getContent(),
                                            callback1);
        }).getProjectContent(path, defSetId);
    }

    @Override
    public void saveOrUpdate(final Path path,
                             final ProjectDiagram diagram,
                             final Metadata metadata,
                             final String comment,
                             final ServiceCallback<ProjectDiagram> callback) {

        dmnMarshallerService.marshall(diagram, new ServiceCallback<String>() {

            @Override
            public void onSuccess(final String xml) {
                DMNClientProjectDiagramService.super.saveAsXml(path, xml, metadata, comment, onSaveAsXmlComplete(diagram, callback));
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                callback.onError(error);
            }
        });
    }

    private ServiceCallback<String> onSaveAsXmlComplete(final ProjectDiagram diagram,
                                                        final ServiceCallback<ProjectDiagram> callback) {

        return new ServiceCallback<String>() {

            @Override
            public void onSuccess(final String xml) {
                callback.onSuccess(diagram);
            }

            @Override
            public void onError(final ClientRuntimeError e) {
                DomGlobal.console.error(e.getMessage(), e);
            }
        };
    }
}
