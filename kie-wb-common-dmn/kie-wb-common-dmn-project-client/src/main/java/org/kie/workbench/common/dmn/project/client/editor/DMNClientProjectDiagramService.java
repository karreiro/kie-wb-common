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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.xml.namespace.QName;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.dmn.api.DMNContentResource;
import org.kie.workbench.common.dmn.api.DMNContentService;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.DMNShapeSet;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DRGDiagramUtils;
import org.kie.workbench.common.dmn.client.marshaller.marshall.DMNMarshaller;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.DMNUnmarshaller;
import org.kie.workbench.common.dmn.project.api.factory.impl.DMNProjectDiagramFactory;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12MarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12UnmarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.DMN12;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JSIName;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramSavedEvent;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.service.DiagramLookupService;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
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
                                          final DMNDiagramsSession dmnDiagramsSession) {

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
    }

    @PostConstruct
    public void init() {
        MainJs.initializeJsInteropConstructors(MainJs.getConstructorsMap());
    }

    @Override
    public void getByPath(final Path path,
                          final ServiceCallback<ProjectDiagram> callback) {

        DomGlobal.console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> GET BY PATH");
        final String defSetId = BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class);

        dmnContentServiceCaller.call(new RemoteCallback<DMNContentResource>() {
            @Override
            public void callback(final DMNContentResource resource) {

                try {
                    final DMN12UnmarshallCallback jsCallback = dmn12 -> {
                        final JSITDefinitions definitions = Js.uncheckedCast(JsUtils.getUnwrappedElement(dmn12));

                        DMNClientProjectDiagramService.this.onLoadDiagramCallback = new ServiceCallback<Diagram>() {
                            @Override
                            public void onSuccess(final Diagram diagram) {
                                final ProjectDiagram projectDiagram = (ProjectDiagram) diagram;
                                callback.onSuccess(projectDiagram);
                            }

                            @Override
                            public void onError(final ClientRuntimeError error) {
                                callback.onError(error);
                            }
                        };

                        final ProjectMetadata metadata = (ProjectMetadata) resource.getMetadata();// buildMetadataInstance(path);

                        if (Objects.nonNull(metadata) && StringUtils.isEmpty(metadata.getShapeSetId())) {
                            final String shapeSetId = BindableAdapterUtils.getShapeSetId(DMNShapeSet.class);
                            metadata.setShapeSetId(shapeSetId);
                        }

                        dmnMarshallerKogitoUnmarshaller.unmarshall(metadata, definitions).then(_graph -> {

                            final Diagram diagram = dmnDiagramFactory.build(DRGDiagramUtils.DRG, metadata, _graph);

//                            updateClientShapeSetId(diagram);
                            onLoadDiagramCallback.onSuccess(diagram);

//                            super.getByPath(path, callback);

                            return promises.resolve();
                        });
                    };

                    MainJs.unmarshall(resource.getContent(), "", jsCallback);
                } catch (Exception e) {
                    GWT.log(e.getMessage(), e);
                    callback.onError(new ClientRuntimeError(new DiagramParsingException(null, resource.getContent())));
                }
            }
        }).getProjectContent(path, defSetId);
    }

    @Override
    public void saveOrUpdate(final Path path,
                             final ProjectDiagram diagram,
                             final Metadata metadata,
                             final String comment,
                             final ServiceCallback<ProjectDiagram> callback) {

        DomGlobal.console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> saveOrUpdate");

        final ServiceCallback<String> onSaveAsXmlComplete = new ServiceCallback<String>() {
            public void onSuccess(final String xml) {
                callback.onSuccess(diagram);
            }

            public void onError(final ClientRuntimeError e) {
                DomGlobal.console.error(e.getMessage(), e);
            }
        };
        final ServiceCallback<String> onMarshallerComplete = new ServiceCallback<String>() {
            public void onSuccess(final String xml) {
                DMNClientProjectDiagramService.super.saveAsXml(path, xml, metadata, comment, onSaveAsXmlComplete);
            }

            public void onError(final ClientRuntimeError e) {
                DomGlobal.console.error(e.getMessage(), e);
            }
        };

        final DMN12MarshallCallback jsCallback = result -> {
            String xml = result;
            if (!xml.startsWith("<?xml version=\"1.0\" ?>")) {
                xml = "<?xml version=\"1.0\" ?>" + xml;
            }
            onMarshallerComplete.onSuccess(xml);
        };

        if (Objects.isNull(diagram)) {
            return;
        }
        final Graph graph = diagram.getGraph();
        if (Objects.isNull(graph)) {
            return;
        }

        try {
            final JSITDefinitions jsitDefinitions = dmnMarshallerKogitoMarshaller.marshall(graph);
            final DMN12 dmn12 = Js.uncheckedCast(JsUtils.newWrappedInstance());
            JsUtils.setNameOnWrapped(dmn12, makeJSINameForDMN12());
            JsUtils.setValueOnWrapped(dmn12, jsitDefinitions);

            final JavaScriptObject namespaces = createNamespaces(jsitDefinitions.getOtherAttributes(),
                                                                 jsitDefinitions.getNamespace());
            MainJs.marshall(dmn12, namespaces, jsCallback);
        } catch (final Exception e) {
            DomGlobal.console.error(e.getMessage(), e);
        }
    }

    private JavaScriptObject createNamespaces(final Map<QName, String> otherAttributes,
                                              final String defaultNamespace) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(defaultNamespace, new JSONString(""));
        otherAttributes.forEach((key, value) -> jsonObject.put(value, new JSONString(key.getLocalPart())));
        return jsonObject.getJavaScriptObject();
    }

    private JSIName makeJSINameForDMN12() {
        final org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JSIName jsiName = JSITDefinitions.getJSIName();
        jsiName.setPrefix("dmn");
        jsiName.setLocalPart("definitions");
        final String key = "{" + jsiName.getNamespaceURI() + "}" + jsiName.getLocalPart();
        final String keyString = "{" + jsiName.getNamespaceURI() + "}" + jsiName.getPrefix() + ":" + jsiName.getLocalPart();
        jsiName.setKey(key);
        jsiName.setString(keyString);
        return jsiName;
    }

    public void switchToDMNDiagramElement(final @Observes DMNDiagramSelected selected) {

        final DMNDiagramElement dmnDiagramElement = selected.getDiagramElement();
        final boolean belongsToCurrentSessionState = dmnDiagramsSession.belongsToCurrentSessionState(dmnDiagramElement);

        if (belongsToCurrentSessionState && getOnLoadDiagramCallback().isPresent()) {

            final String diagramId = dmnDiagramElement.getId().getValue();
            final String diagramName = dmnDiagramElement.getName().getValue();
            final Diagram stunnerDiagram = dmnDiagramsSession.getDiagram(diagramId);
            final org.kie.workbench.common.stunner.core.diagram.Metadata metadata = dmnDiagramsSession.getDRGDiagram().getMetadata();
//
//            final ProjectMetadata metadata = getMetadata(buildMetadataInstance(stunnerDiagram.getMetadata().getPath()));
            final Diagram diagram = dmnDiagramFactory.build(diagramName, (ProjectMetadata) metadata, stunnerDiagram.getGraph());

            updateClientShapeSetId(diagram);
            getOnLoadDiagramCallback().get().onSuccess(diagram);
        }
    }

    private ProjectMetadata getMetadata(final org.kie.workbench.common.stunner.core.diagram.Metadata metadata) {
//        final ProjectMetadata projectMetadata = new ProjectMetadata();
        return (ProjectMetadata) metadata;
    }

    private Optional<ServiceCallback<Diagram>> getOnLoadDiagramCallback() {
        return Optional.ofNullable(onLoadDiagramCallback);
    }

    private org.kie.workbench.common.stunner.core.diagram.Metadata buildMetadataInstance(final Path path) {
        final String defSetId = BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class);
        final String shapeSetId = BindableAdapterUtils.getShapeSetId(DMNShapeSet.class);
        return new MetadataImpl.MetadataImplBuilder(defSetId,
                                                    definitionManager)
//                .setRoot(PathFactory.newPath(".", ROOT))
                .setPath(path)
                .setShapeSetId(shapeSetId)
                .build();
    }

    private void updateClientShapeSetId(final Diagram diagram) {
        if (Objects.nonNull(diagram)) {
            final org.kie.workbench.common.stunner.core.diagram.Metadata metadata = diagram.getMetadata();
            if (Objects.nonNull(metadata) && StringUtils.isEmpty(metadata.getShapeSetId())) {
                final String shapeSetId = BindableAdapterUtils.getShapeSetId(DMNShapeSet.class);
                metadata.setShapeSetId(shapeSetId);
            }
        }
    }
}
