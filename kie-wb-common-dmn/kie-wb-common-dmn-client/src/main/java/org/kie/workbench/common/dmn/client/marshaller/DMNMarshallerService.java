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

package org.kie.workbench.common.dmn.client.marshaller;

import java.util.Map;
import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.xml.namespace.QName;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import jsinterop.base.Js;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.dmn.api.DMNContentService;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.factory.DMNDiagramFactory;
import org.kie.workbench.common.dmn.client.DMNShapeSet;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DRGDiagramUtils;
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
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.promise.Promises;

@Dependent
public class DMNMarshallerService {

    private static final String DIAGRAMS_PATH = "diagrams";
    private static final String ROOT = "default://master@system/stunner/" + DIAGRAMS_PATH;

    private final DMNUnmarshaller dmnUnmarshaller;

    private final DMNMarshaller dmnMarshaller;

    private final DMNDiagramFactory dmnDiagramFactory;

    private final DefinitionManager definitionManager;

    private final Promises promises;

    private final DMNDiagramsSession dmnDiagramsSession;

    private ServiceCallback<Diagram> onDiagramLoad = emptyService();

    @Inject
    public DMNMarshallerService(final DMNUnmarshaller dmnUnmarshaller,
                                final DMNMarshaller dmnMarshaller,
                                final DMNDiagramFactory dmnDiagramFactory,
                                final DefinitionManager definitionManager,
                                final Promises promises,
                                final DMNDiagramsSession dmnDiagramsSession) {
        this.dmnUnmarshaller = dmnUnmarshaller;
        this.dmnMarshaller = dmnMarshaller;
        this.dmnDiagramFactory = dmnDiagramFactory;
        this.definitionManager = definitionManager;
        this.promises = promises;
        this.dmnDiagramsSession = dmnDiagramsSession;
    }

    public void unmarshall(final Path path,
                           final Caller<? extends DMNContentService> contentServiceCaller,
                           final ServiceCallback<Diagram> callback) {

        final Metadata metadata = buildMetadataInstance(path);
        setOnDiagramLoad(callback);
        contentServiceCaller.call((final String xml) -> {
            try {
                final DMN12UnmarshallCallback jsCallback = dmn12 -> {
                    final JSITDefinitions definitions = Js.uncheckedCast(JsUtils.getUnwrappedElement(dmn12));
                    dmnUnmarshaller.unmarshall(metadata, definitions).then(graph -> {
                        onDiagramLoad(dmnDiagramFactory.build(DRGDiagramUtils.DRG, metadata, graph));
                        return promises.resolve();
                    });
                };
                MainJs.unmarshall(xml, "", jsCallback);
            } catch (final Exception e) {
                GWT.log(e.getMessage(), e);
                callback.onError(new ClientRuntimeError(new DiagramParsingException(metadata, xml)));
            }
        }).getContent(path);
    }

    public void marshall(final Diagram diagram,
                         final ServiceCallback<String> contentServiceCallback) {
        final DMN12MarshallCallback jsCallback = result -> {
            final String xml;
            final String prefix = "<?xml version=\"1.0\" ?>";
            if (result.startsWith(prefix)) {
                xml = result;
            } else {
                xml = prefix + result;
            }
            contentServiceCallback.onSuccess(xml);
        };

        if (Objects.isNull(diagram)) {
            contentServiceCallback.onError(new ClientRuntimeError("Diagram cannot be null.")); // TODO better error message
            return;
        }

        final Graph graph = diagram.getGraph();
        if (Objects.isNull(graph)) {
            contentServiceCallback.onError(new ClientRuntimeError("Graph cannot be null.")); // TODO better error message
            return;
        }

        try {
            final JSITDefinitions jsitDefinitions = dmnMarshaller.marshall(graph);
            final DMN12 dmn12 = Js.uncheckedCast(JsUtils.newWrappedInstance());
            JsUtils.setNameOnWrapped(dmn12, makeJSINameForDMN12());
            JsUtils.setValueOnWrapped(dmn12, jsitDefinitions);

            final JavaScriptObject namespaces = createNamespaces(jsitDefinitions.getOtherAttributes(),
                                                                 jsitDefinitions.getNamespace());
            MainJs.marshall(dmn12, namespaces, jsCallback);
        } catch (final Exception e) {
            contentServiceCallback.onError(new ClientRuntimeError("Marshaller error.")); // TODO better error message
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

        if (belongsToCurrentSessionState) {

            final String diagramId = dmnDiagramElement.getId().getValue();
            final String diagramName = dmnDiagramElement.getName().getValue();
            final Diagram stunnerDiagram = dmnDiagramsSession.getDiagram(diagramId);
            final org.kie.workbench.common.stunner.core.diagram.Metadata metadata = buildMetadataInstance(stunnerDiagram.getMetadata().getPath());
            final Diagram diagram = dmnDiagramFactory.build(diagramName, metadata, stunnerDiagram.getGraph());

            onDiagramLoad(diagram);
        }
    }

    private org.kie.workbench.common.stunner.core.diagram.Metadata buildMetadataInstance(final Path path) {
        final String defSetId = BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class);
        final String shapeSetId = BindableAdapterUtils.getShapeSetId(DMNShapeSet.class);
        return new MetadataImpl.MetadataImplBuilder(defSetId,
                                                    definitionManager)
                .setRoot(PathFactory.newPath(".", ROOT))
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

    private void setOnDiagramLoad(final ServiceCallback<Diagram> onDiagramLoad) {
        this.onDiagramLoad = onDiagramLoad;
    }

    private void onDiagramLoad(final Diagram diagram) {
        updateClientShapeSetId(diagram);
        onDiagramLoad.onSuccess(diagram);
    }

    private ServiceCallback<Diagram> emptyService() {
        return new ServiceCallback<Diagram>() {
            @Override
            public void onSuccess(final Diagram item) {
                // empty.
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                // empty.
            }
        };
    }
}
