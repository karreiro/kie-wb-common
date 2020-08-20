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
import org.kie.workbench.common.dmn.api.DMNContentService;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.factory.DMNDiagramFactory;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.DMNShapeSet;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.marshaller.marshall.DMNMarshaller;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.DMNUnmarshaller;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12MarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12UnmarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.DMN12;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JSIName;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.promise.Promises;

@DMNEditor
public class DMNClientProjectDiagramService extends ClientProjectDiagramService {

    @Inject
    private Caller<DMNContentService> dmnContentServiceCaller;

    @Inject
    private DMNUnmarshaller dmnMarshallerKogitoUnmarshaller;

    @Inject
    private DMNMarshaller dmnMarshallerKogitoMarshaller;

    @Inject
    private DMNDiagramFactory dmnDiagramFactory;

    @Inject
    private DefinitionManager definitionManager;

    @Inject
    private Promises promises;

    @Inject
    private DMNDiagramsSession dmnDiagramsSession;

    @Inject
    private DMNGraphUtils dmnGraphUtils;

    private ServiceCallback<ProjectDiagram> onLoadDiagramCallback;

    @Override
    public void getByPath(final Path path,
                          final ServiceCallback<ProjectDiagram> callback) {

        DomGlobal.console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> GET BY PATH");

        dmnContentServiceCaller.call(new RemoteCallback<String>() {
            @Override
            public void callback(final String xml) {

                try {
                    final DMN12UnmarshallCallback jsCallback = dmn12 -> {
                        final JSITDefinitions definitions = Js.uncheckedCast(JsUtils.getUnwrappedElement(dmn12));

                        DMNClientProjectDiagramService.this.onLoadDiagramCallback = callback;

                        org.kie.workbench.common.stunner.core.diagram.Metadata metadata = buildMetadataInstance(path);

                        dmnMarshallerKogitoUnmarshaller.unmarshall(metadata, definitions).then(_graph -> {

                            final ProjectDiagram diagram = (ProjectDiagram) dmnDiagramFactory.build("DRG", metadata, _graph);

                            updateClientShapeSetId(diagram);
                            onLoadDiagramCallback.onSuccess(diagram);


//                            super.getByPath(path, callback);

                            return promises.resolve();
                        });
                    };

                    MainJs.unmarshall(xml, "", jsCallback);
                } catch (Exception e) {
                    GWT.log(e.getMessage(), e);
                    callback.onError(new ClientRuntimeError(new DiagramParsingException(null, xml)));
                }
            }
        }).getContent(path);
    }

    @Override
    public void saveOrUpdate(final Path path,
                             final ProjectDiagram diagram,
                             final Metadata metadata,
                             final String comment,
                             final ServiceCallback<ProjectDiagram> callback) {

        DomGlobal.console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> saveOrUpdate");

        final String xml111 = "";

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
            final org.kie.workbench.common.stunner.core.diagram.Metadata metadata = buildMetadataInstance(stunnerDiagram.getMetadata().getPath());
            final ProjectDiagram diagram = (ProjectDiagram) dmnDiagramFactory.build(diagramName, metadata, stunnerDiagram.getGraph());

            updateClientShapeSetId(diagram);
            getOnLoadDiagramCallback().get().onSuccess(diagram);
        }
    }

    private Optional<ServiceCallback<ProjectDiagram>> getOnLoadDiagramCallback() {
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
