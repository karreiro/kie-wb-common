/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.client.marshaller.unmarshall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.namespace.QName;

import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.Association;
import org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.InformationRequirement;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.client.marshaller.common.DMNDiagramElementsUtils;
import org.kie.workbench.common.dmn.client.marshaller.common.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.marshaller.converters.DefinitionsConverter;
import org.kie.workbench.common.dmn.client.marshaller.converters.ItemDefinitionPropertyConverter;
import org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsHelperKogito;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.NodeEntriesFactory;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.NodeEntry;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIPoint;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.di.JSIDiagramElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElementReference;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionService;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDiagram;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNEdge;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNShape;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentsWidthsExtension;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.uberfire.client.promise.Promises;

import static org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils.upperLeftBound;
import static org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils.xOfBound;
import static org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils.yOfBound;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

@ApplicationScoped
public class DMNUnmarshaller {

    private static final String INFO_REQ_ID = getDefinitionId(InformationRequirement.class);
    private static final String KNOWLEDGE_REQ_ID = getDefinitionId(KnowledgeRequirement.class);
    private static final String AUTH_REQ_ID = getDefinitionId(AuthorityRequirement.class);
    private static final String ASSOCIATION_ID = getDefinitionId(Association.class);

    private static final double CENTRE_TOLERANCE = 1.0;

    private final FactoryManager factoryManager;
    private final DMNMarshallerImportsHelperKogito dmnMarshallerImportsHelper;

    private Promises promises;

    private final NodeEntriesFactory modelToStunnerConverter;
    private final DMNDiagramElementsUtils dmnDiagramElementsUtils;

    protected DMNUnmarshaller() {
        this(null, null, null, null, null);
    }

    @Inject
    public DMNUnmarshaller(final FactoryManager factoryManager,
                           final DMNMarshallerImportsHelperKogito dmnMarshallerImportsHelper,
                           final Promises promises,
                           final NodeEntriesFactory modelToStunnerConverter,
                           final DMNDiagramElementsUtils dmnDiagramElementsUtils) {
        this.factoryManager = factoryManager;
        this.dmnMarshallerImportsHelper = dmnMarshallerImportsHelper;
        this.promises = promises;
        this.modelToStunnerConverter = modelToStunnerConverter;
        this.dmnDiagramElementsUtils = dmnDiagramElementsUtils;
    }

    @PostConstruct
    public void init() {
        MainJs.initializeJsInteropConstructors(MainJs.getConstructorsMap());
    }

    public Promise<Graph> unmarshall(final Metadata metadata,
                                     final JSITDefinitions jsiDefinitions) {

        return getImportDefinitions(metadata, jsiDefinitions)
                .then(importDefinitions -> unmarshall(metadata,
                                                      jsiDefinitions,
                                                      importDefinitions));
    }

    private Promise<Map<JSITImport, JSITDefinitions>> getImportDefinitions(final Metadata metadata,
                                                                           final JSITDefinitions jsiDefinitions) {
        final List<JSITImport> imports = jsiDefinitions.getImport();
        return dmnMarshallerImportsHelper.getImportDefinitionsAsync(metadata, imports);
    }

    private Promise<Graph> unmarshall(final Metadata metadata,
                                      final JSITDefinitions dmnDefinitions,
                                      final Map<JSITImport, JSITDefinitions> importDefinitions) {

        final Map<String, HasComponentWidths> hasComponentWidthsMap = new HashMap<>();
        final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer = (uuid, hcw) -> {
            if (Objects.nonNull(uuid)) {
                hasComponentWidthsMap.put(uuid, hcw);
            }
        };

        // Get external PMML model information
        final Map<JSITImport, PMMLDocumentMetadata> pmmlDocuments = getPmmlDocuments(metadata, dmnDefinitions);

        ensureDRGElementExists(dmnDefinitions);

        final Definitions wbDefinitions = DefinitionsConverter.wbFromDMN(dmnDefinitions, importDefinitions, pmmlDocuments);
        final List<NodeEntry> nodeEntries = modelToStunnerConverter.makeNodes(dmnDefinitions, importDefinitions, hasComponentWidthsConsumer);
        final List<JSITDecisionService> dmnDecisionServices = getDecisionServices(nodeEntries);

        final Diagram diagram = factoryManager.newDiagram("prova",
                                                          BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class),
                                                          metadata);
        final Graph graph = diagram.getGraph();

        nodeEntries.forEach(nodeEntry -> graph.addNode(nodeEntry.getNode()));

        final Node<?, ?> dmnDiagramRoot = DMNGraphUtils.findDMNDiagramRoot(graph);

        loadImportedItemDefinitions(wbDefinitions, importDefinitions);

        ((View<DMNDiagram>) dmnDiagramRoot.getContent()).getDefinition().setDefinitions(wbDefinitions);

        //Only connect Nodes to the Diagram that are not referenced by DecisionServices
        final List<String> references = new ArrayList<>();
        final List<JSITDecisionService> lstDecisionServices = new ArrayList<>(dmnDecisionServices);
        for (int iDS = 0; iDS < lstDecisionServices.size(); iDS++) {
            final JSITDecisionService jsiDecisionService = Js.uncheckedCast(lstDecisionServices.get(iDS));
            final List<JSITDMNElementReference> jsiEncapsulatedDecisions = jsiDecisionService.getEncapsulatedDecision();
            if (Objects.nonNull(jsiEncapsulatedDecisions)) {
                for (int i = 0; i < jsiEncapsulatedDecisions.size(); i++) {
                    final JSITDMNElementReference jsiEncapsulatedDecision = Js.uncheckedCast(jsiEncapsulatedDecisions.get(i));
                    references.add(jsiEncapsulatedDecision.getHref());
                }
            }

            final List<JSITDMNElementReference> jsiOutputDecisions = jsiDecisionService.getOutputDecision();
            if (Objects.nonNull(jsiOutputDecisions)) {
                for (int i = 0; i < jsiOutputDecisions.size(); i++) {
                    final JSITDMNElementReference jsiOutputDecision = Js.uncheckedCast(jsiOutputDecisions.get(i));
                    references.add(jsiOutputDecision.getHref());
                }
            }
        }

//        final Map<JSITDRGElement, Node> elementsToConnectToRoot = new HashMap<>();
//        for (NodeEntry nodeEntry : entriesById.values()) {
//            final JSITDRGElement element = Js.uncheckedCast(nodeEntry.getDmnElement());
//
//            if (JSITTextAnnotation.instanceOf(element)) {
//                continue;
//            }
//
//            if (!references.contains("#" + element.getId())) {
//                elementsToConnectToRoot.put(element, nodeEntry.getNode());
//            }
//        }
//        entriesById.values().forEach(nodeEntry -> connectRootWithChild(dmnDiagramRoot, nodeEntry.getNode()));

        nodeEntries.forEach(ne -> {
            connectRootWithChild(dmnDiagramRoot, ne.getNode());
        });

        //Copy ComponentWidths information
        final List<JSITComponentsWidthsExtension> extensions = findComponentsWidthsExtensions(dmnDefinitions.getDMNDI().getDMNDiagram());

        extensions.forEach(componentsWidthsExtension -> {
            //This condition is required because a node with ComponentsWidthsExtension
            //can be imported from another diagram but the extension is not imported or present in this diagram.
            if (Objects.nonNull(componentsWidthsExtension.getComponentWidths())) {
                hasComponentWidthsMap.entrySet().forEach(es -> {
                    final List<JSITComponentWidths> jsiComponentWidths = componentsWidthsExtension.getComponentWidths();
                    for (int i = 0; i < jsiComponentWidths.size(); i++) {
                        final JSITComponentWidths jsiWidths = Js.uncheckedCast(jsiComponentWidths.get(i));
                        if (Objects.equals(jsiWidths.getDmnElementRef(), es.getKey())) {
                            final List<Double> widths = es.getValue().getComponentWidths();
                            if (Objects.nonNull(jsiWidths.getWidth())) {
                                widths.clear();
                                for (int w = 0; w < jsiWidths.getWidth().size(); w++) {
                                    final double width = jsiWidths.getWidth().get(w).doubleValue();
                                    widths.add(width);
                                }
                            }
                        }
                    }
                });
            }
        });

        return promises.resolve(graph);
    }

    private void ensureDRGElementExists(final JSITDefinitions dmnDefinitions) {
        dmnDiagramElementsUtils.ensureDRGElementExists(dmnDefinitions);
    }

    private List<JSITDecisionService> getDecisionServices(final List<NodeEntry> nodeEntries) {
        return nodeEntries
                .stream()
                .filter(nodeEntry -> JSITDecisionService.instanceOf(nodeEntry.getDmnElement()))
                .map(nodeEntry -> (JSITDecisionService) Js.uncheckedCast(nodeEntry.getDmnElement()))
                .collect(Collectors.toList());
    }

    private Map<JSITImport, PMMLDocumentMetadata> getPmmlDocuments(final Metadata metadata, final JSITDefinitions jsiDefinitions) {
        return dmnMarshallerImportsHelper.getPMMLDocuments(metadata, jsiDefinitions.getImport());
    }

    private Optional<JSIDMNDiagram> findJSIDiagram(final JSITDefinitions dmnXml) {

        if (Objects.isNull(dmnXml.getDMNDI())) {
            return Optional.empty();
        }
        final List<JSIDMNDiagram> elems = dmnXml.getDMNDI().getDMNDiagram();

        DomGlobal.console.log("()()()()()() ===========> ", elems.size());

        if (elems.size() != 1) {
            return Optional.empty();
        } else {
            return Optional.of(Js.uncheckedCast(elems.get(0)));
        }
    }

    private void removeDrgElementsWithoutShape(final List<JSITDRGElement> drgElements,
                                               final List<JSIDMNShape> dmnShapes) {
        DomGlobal.console.log("======================================= removeDrgElementsWithoutShape");
        // DMN 1.1 doesn't have DMNShape, so we include all DRGElements and create all the shapes.
        DomGlobal.console.log("========== " + dmnShapes.size());
        if (dmnShapes.isEmpty()) {
            return;
        }

        drgElements.removeIf(element -> dmnShapes.stream().noneMatch(s -> {
            final String id = element.getId();
            final String dmnElementRef = getDmnElementRef(s);
            final boolean equals = Objects.equals(dmnElementRef, id);
            final String p = equals ? "Remove: " : "Do not remove: ";
            DomGlobal.console.log("=======================================  " + p + element.getId());
            return equals;
        }));
    }

    private Node getRequiredNode(final Map<String, Entry<JSITDRGElement, Node>> elems,
                                 final String reqInputID) {
        if (elems.containsKey(reqInputID)) {
            final Entry<JSITDRGElement, Node> value = elems.get(reqInputID);
            return value.getValue();
        } else {
            final Optional<String> match = elems.keySet().stream()
                    .filter(k -> k.contains(reqInputID))
                    .findFirst();
            if (match.isPresent()) {
                return elems.get(match.get()).getValue();
            }
        }
        return null;
    }

    private List<JSITDRGElement> getImportedDrgElementsByShape(final List<JSIDMNShape> dmnShapes,
                                                               final Map<JSITImport, JSITDefinitions> importDefinitions) {

        final List<JSITDRGElement> importedDRGElements = dmnMarshallerImportsHelper.getImportedDRGElements(importDefinitions);

        final List<JSITDRGElement> elements = new ArrayList<>();
        for (int i = 0; i < dmnShapes.size(); i++) {
            final JSIDMNShape shape = Js.uncheckedCast(dmnShapes.get(i));
            final String dmnElementRef = getDmnElementRef(shape);

            getReference(importedDRGElements, dmnElementRef)
                    .ifPresent(ref -> elements.add(Js.uncheckedCast(ref)));
        }
        return elements;
    }

    private Optional<Object> getReference(final List<JSITDRGElement> importedDRGElements,
                                          final String dmnElementRef) {
        for (int i = 0; i < importedDRGElements.size(); i++) {
            final JSITDRGElement importedDRGElement = Js.uncheckedCast(importedDRGElements.get(i));
            final String importedDRGElementId = importedDRGElement.getId();
            if (dmnElementRef.endsWith(importedDRGElementId)) {
                return Optional.of(importedDRGElement);
            }
        }
        return Optional.empty();
    }

    private String getDmnElementRef(final JSIDMNShape dmnShape) {
        final QName elementRef = dmnShape.getDmnElementRef();
        if (Objects.nonNull(elementRef)) {
            return elementRef.getLocalPart();
        }
        return "";
    }

//    private List<JSIDMNShape> getUniqueDMNShapes(final JSIDMNDiagram dmnDDDiagram) {
//        final Map<String, JSIDMNShape> jsidmnShapes = new HashMap<>();
//        final List<JSIDiagramElement> unwrapped = dmnDDDiagram.getDMNDiagramElement();
//        for (int i = 0; i < unwrapped.size(); i++) {
//            final JSIDiagramElement jsiDiagramElement = Js.uncheckedCast(unwrapped.get(i));
//            if (JSIDMNShape.instanceOf(jsiDiagramElement)) {
//                final JSIDMNShape jsidmnShape = Js.uncheckedCast(jsiDiagramElement);
//                if (!jsidmnShapes.containsKey(jsidmnShape.getId())) {
//                    jsidmnShapes.put(jsidmnShape.getId(), jsidmnShape);
//                }
//            }
//        }
//        return new ArrayList<>(jsidmnShapes.values());
//    }

    /**
     * Stunner's factoryManager is only used to create Nodes that are considered part of a "Definition Set" (a collection of nodes visible to the User e.g. BPMN2 StartNode, EndNode and DMN's DecisionNode etc).
     * Relationships are not created with the factory.
     * This method specializes to connect with an Edge containing a Child relationship the target Node.
     */
    private void connectDSChildEdge(final Node dsNode,
                                    final Node requiredNode) {
        final String uuid = dsNode.getUUID() + "er" + requiredNode.getUUID();
        final Edge<Child, Node> myEdge = new EdgeImpl<>(uuid);
        myEdge.setContent(new Child());
        connectEdge(myEdge,
                    dsNode,
                    requiredNode);
    }

    private String idOfDMNorWBUUID(final JSITDMNElement dmn) {
        return Objects.nonNull(dmn.getId()) ? dmn.getId() : UUID.uuid();
    }

    private String getId(final JSITDMNElementReference er) {
        String href = er.getHref();
        return href.contains("#") ? href.substring(href.indexOf('#') + 1) : href;
    }

    private void connectEdgeToNodes(final String connectorTypeId,
                                    final JSITDMNElement jsiDMNElement,
                                    final JSITDMNElementReference jsiDMNElementReference,
                                    final Map<String, Entry<JSITDRGElement, Node>> elems,
                                    final JSITDefinitions jsiDefinitions,
                                    final Node currentNode) {
        if (Objects.nonNull(jsiDMNElementReference)) {
            final String reqInputID = getId(jsiDMNElementReference);
            final Node requiredNode = getRequiredNode(elems, reqInputID);
            final Edge myEdge = factoryManager.newElement(idOfDMNorWBUUID(jsiDMNElement),
                                                          connectorTypeId).asEdge();
            connectEdge(myEdge,
                        requiredNode,
                        currentNode);
            setConnectionMagnets(myEdge, jsiDMNElement.getId(), jsiDefinitions);
        }
    }

//    private Node dmnToStunner(final JSIDMNShape shape,
//                              final JSITDRGElement dmn,
//                              final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer,
//                              final List<JSITDRGElement> importedDrgElements) {
//
//        final Node node = createNode(shape,
//                                     dmn,
//                                     hasComponentWidthsConsumer);
//        return setAllowOnlyVisualChange(importedDrgElements, node);
//    }

//    private Node createNode(final JSIDMNShape shape,
//                            final JSITDRGElement dmn,
//                            final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
////        Node node;
////        if (JSITInputData.instanceOf(dmn)) {
////            node = inputDataConverter.nodeFromDMN(Js.uncheckedCast(dmn),
////                                                  hasComponentWidthsConsumer);
////        } else if (JSITDecision.instanceOf(dmn)) {
////            node = decisionConverter.nodeFromDMN(Js.uncheckedCast(dmn),
////                                                 hasComponentWidthsConsumer);
////        } else if (JSITBusinessKnowledgeModel.instanceOf(dmn)) {
////            node = bkmConverter.nodeFromDMN(Js.uncheckedCast(dmn),
////                                            hasComponentWidthsConsumer);
////        } else if (JSITKnowledgeSource.instanceOf(dmn)) {
////            node = knowledgeSourceConverter.nodeFromDMN(Js.uncheckedCast(dmn),
////                                                        hasComponentWidthsConsumer);
////        } else if (JSITDecisionService.instanceOf(dmn)) {
////            node = decisionServiceConverter.nodeFromDMN(Js.uncheckedCast(dmn),
////                                                        hasComponentWidthsConsumer);
////        } else {
////            throw new UnsupportedOperationException("Unsupported DRGElement type [" + dmn.getTYPE_NAME() + "]");
////        }
////        DomGlobal.console.log("Node 0 ========> ");
////        if (node instanceof NodeImpl) {
////            final Object content = node.getContent();
////            final List inEdges = node.getInEdges();
////            final List outEdges = node.getOutEdges();
////            final Set labels = node.getLabels();
////
////            node = new NodeImpl(shape.getId());
////            node.setContent(content);
////            node.getInEdges().addAll(inEdges);
////            node.getOutEdges().addAll(outEdges);
////            node.getLabels().addAll(labels);
////
////            final String uuid = node.getUUID();
////            final View<?> v = (View<?>) node.getContent();
////            final Object definition = v.getDefinition();
////            final String name;
////
////            if (definition instanceof NamedElement) {
////                name = ((NamedElement) definition).getName().getValue();
////            } else {
////                name = "no name";
////            }
////
////            DomGlobal.console.log("Node 1 ========> " + name + " => " + uuid);
////        } else {
////
////            DomGlobal.console.log("Node 2 ========> " + node.getClass().getSimpleName());
////        }
//        return null;
//    }

//    private Node setAllowOnlyVisualChange(final List<JSITDRGElement> importedDrgElements,
//                                          final Node node) {
//        getDRGElement(node).ifPresent(drgElement -> {
//            if (isImportedDRGElement(importedDrgElements, drgElement)) {
//                drgElement.setAllowOnlyVisualChange(true);
//            } else {
//                drgElement.setAllowOnlyVisualChange(false);
//            }
//        });
//
//        return node;
//    }

//    private Optional<DRGElement> getDRGElement(final Node node) {
//        final Object objectDefinition = DefinitionUtils.getElementDefinition(node);
//
//        if (objectDefinition instanceof DRGElement) {
//            return Optional.of((DRGElement) objectDefinition);
//        }
//
//        return Optional.empty();
//    }
//
//    private boolean isImportedDRGElement(final List<JSITDRGElement> importedDrgElements,
//                                         final JSITDRGElement drgElement) {
//        return isImportedIdNode(importedDrgElements, drgElement.getId());
//    }
//
//    private boolean isImportedDRGElement(final List<JSITDRGElement> importedDrgElements,
//                                         final DRGElement drgElement) {
//        return isImportedIdNode(importedDrgElements, drgElement.getId().getValue());
//    }
//
//    private boolean isImportedIdNode(final List<JSITDRGElement> importedDrgElements,
//                                     final String id) {
//        return importedDrgElements
//                .stream()
//                .anyMatch(drgElement -> Objects.equals(drgElement.getId(), id));
//    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void connectRootWithChild(final Node dmnDiagramRoot,
                                      final Node child) {
        final String uuid = UUID.uuid();
        final Edge<Child, Node> edge = new EdgeImpl<>(uuid);
        edge.setContent(new Child());
        connectEdge(edge, dmnDiagramRoot, child);
        final Definitions definitions = ((DMNDiagram) ((View) dmnDiagramRoot.getContent()).getDefinition()).getDefinitions();
        final DMNModelInstrumentedBase childDRG = (DMNModelInstrumentedBase) ((View) child.getContent()).getDefinition();
        childDRG.setParent(definitions);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void connectEdge(final Edge edge,
                             final Node source,
                             final Node target) {
        edge.setSourceNode(source);
        edge.setTargetNode(target);
        source.getOutEdges().add(edge);
        target.getInEdges().add(edge);
    }

    @SuppressWarnings("unchecked")
    private void setConnectionMagnets(final Edge edge,
                                      final String dmnEdgeElementRef,
                                      final JSITDefinitions dmnXml) {
        final ViewConnector connectionContent = (ViewConnector) edge.getContent();
        final Optional<JSIDMNDiagram> dmnDiagram = findJSIDiagram(dmnXml);

        Optional<JSIDMNEdge> dmnEdge = Optional.empty();
        if (dmnDiagram.isPresent()) {
            final JSIDMNDiagram jsiDiagram = Js.uncheckedCast(dmnDiagram.get());
            final List<JSIDiagramElement> jsiDiagramElements = jsiDiagram.getDMNDiagramElement();
            for (int i = 0; i < jsiDiagramElements.size(); i++) {
                final JSIDiagramElement jsiDiagramElement = Js.uncheckedCast(jsiDiagramElements.get(i));
                if (JSIDMNEdge.instanceOf(jsiDiagramElement)) {
                    final JSIDMNEdge jsiEdge = Js.uncheckedCast(jsiDiagramElement);
                    if (Objects.equals(jsiEdge.getDmnElementRef().getLocalPart(), dmnEdgeElementRef)) {
                        dmnEdge = Optional.of(jsiEdge);
                        break;
                    }
                }
            }
        }
        if (dmnEdge.isPresent()) {
            final JSIDMNEdge e = Js.uncheckedCast(dmnEdge.get());
            final JSIPoint source = Js.uncheckedCast(e.getWaypoint().get(0));
            final Node<View<?>, Edge> sourceNode = edge.getSourceNode();
            if (null != sourceNode) {
                setConnectionMagnet(sourceNode,
                                    source,
                                    connectionContent::setSourceConnection);
            }
            final JSIPoint target = Js.uncheckedCast(e.getWaypoint().get(e.getWaypoint().size() - 1));
            final Node<View<?>, Edge> targetNode = edge.getTargetNode();
            if (null != targetNode) {
                setConnectionMagnet(targetNode,
                                    target,
                                    connectionContent::setTargetConnection);
            }
            if (e.getWaypoint().size() > 2) {
                connectionContent.setControlPoints(e.getWaypoint()
                                                           .subList(1, e.getWaypoint().size() - 1)
                                                           .stream()
                                                           .map(p -> ControlPoint.build(PointUtils.dmndiPointToPoint2D(p)))
                                                           .toArray(ControlPoint[]::new));
            }
        } else {
            // Set the source connection, if any.
            final Node sourceNode = edge.getSourceNode();
            if (null != sourceNode) {
                connectionContent.setSourceConnection(MagnetConnection.Builder.atCenter(sourceNode));
            }
            // Set the target connection, if any.
            final Node targetNode = edge.getTargetNode();
            if (null != targetNode) {
                connectionContent.setTargetConnection(MagnetConnection.Builder.atCenter(targetNode));
            }
        }
    }

    private void setConnectionMagnet(final Node<View<?>, Edge> node,
                                     final JSIPoint magnetPoint,
                                     final Consumer<Connection> connectionConsumer) {
        final View<?> view = node.getContent();
        final double viewX = xOfBound(upperLeftBound(view));
        final double viewY = yOfBound(upperLeftBound(view));
        final double magnetRelativeX = magnetPoint.getX() - viewX;
        final double magnetRelativeY = magnetPoint.getY() - viewY;
        final double viewWidth = view.getBounds().getWidth();
        final double viewHeight = view.getBounds().getHeight();
        if (isCentre(magnetRelativeX,
                     magnetRelativeY,
                     viewWidth,
                     viewHeight)) {
            connectionConsumer.accept(MagnetConnection.Builder.atCenter(node));
        } else {
            connectionConsumer.accept(MagnetConnection.Builder.at(magnetRelativeX, magnetRelativeY).setAuto(true));
        }
    }

    private boolean isCentre(final double magnetRelativeX,
                             final double magnetRelativeY,
                             final double viewWidth,
                             final double viewHeight) {
        return Math.abs((viewWidth / 2) - magnetRelativeX) < CENTRE_TOLERANCE &&
                Math.abs((viewHeight / 2) - magnetRelativeY) < CENTRE_TOLERANCE;
    }

    private List<JSITComponentsWidthsExtension> findComponentsWidthsExtensions(final List<JSIDMNDiagram> dmnDDDiagrams) {

        final List<JSITComponentsWidthsExtension> componentsWidthsExtensions = new ArrayList<>();

        for (int index = 0, dmnDiagram1Size = dmnDDDiagrams.size(); index < dmnDiagram1Size; index++) {

            final JSIDMNDiagram jsiDiagram = Js.uncheckedCast(dmnDDDiagrams.get(index));
            final JSIDiagramElement.JSIExtension dmnDDExtensions = Js.uncheckedCast(jsiDiagram.getExtension());

            if (Objects.isNull(dmnDDExtensions)) {
                break;
            }
            if (Objects.isNull(dmnDDExtensions.getAny())) {
                break;
            }
            final List<Object> extensions = dmnDDExtensions.getAny();
            if (!Objects.isNull(extensions)) {
                for (int i = 0; i < extensions.size(); i++) {
                    final Object wrapped = extensions.get(i);
                    final Object extension = JsUtils.getUnwrappedElement(wrapped);
                    if (JSITComponentsWidthsExtension.instanceOf(extension)) {
                        final JSITComponentsWidthsExtension jsiExtension = Js.uncheckedCast(extension);
                        componentsWidthsExtensions.add(jsiExtension);
                    }
                }
            }
        }
        return componentsWidthsExtensions;
    }

    private void loadImportedItemDefinitions(final Definitions definitions,
                                             final Map<JSITImport, JSITDefinitions> importDefinitions) {
        definitions.getItemDefinition().addAll(getWbImportedItemDefinitions(importDefinitions));
    }

    private List<ItemDefinition> getWbImportedItemDefinitions(final Map<JSITImport, JSITDefinitions> importDefinitions) {
        final List<ItemDefinition> definitions = new ArrayList<>();
        final List<JSITItemDefinition> importedDefinitions = dmnMarshallerImportsHelper.getImportedItemDefinitions(importDefinitions);
        for (int i = 0; i < importedDefinitions.size(); i++) {
            final JSITItemDefinition definition = Js.uncheckedCast(importedDefinitions.get(i));
            final ItemDefinition converted = ItemDefinitionPropertyConverter.wbFromDMN(definition);
            converted.setAllowOnlyVisualChange(true);
            definitions.add(converted);
        }
        return definitions;
    }

//    private void ddExtAugmentStunner(final JSIDMNShape shape,
//                                     final Node currentNode) {
//
////        final JSIDMNDiagram jsiDiagram = Js.uncheckedCast(dmnDDDiagram);
////        final List<JSIDiagramElement> jsiDiagramElements = jsiDiagram.getDMNDiagramElement();
//
////        final List<JSIDMNShape> drgShapes = new ArrayList<>();
////        for (int i = 0; i < jsiDiagramElements.size(); i++) {
////            final JSIDiagramElement jsiDiagramElement = Js.uncheckedCast(jsiDiagramElements.get(i));
////            if (JSIDMNShape.instanceOf(jsiDiagramElement)) {
////                drgShapes.add(Js.uncheckedCast(jsiDiagramElement));
////            }
////        }
//        final View content = (View) currentNode.getContent();
//        final Bound ulBound = upperLeftBound(content);
//        final Bound lrBound = lowerRightBound(content);
//        if (content.getDefinition() instanceof Decision) {
//            final Decision d = (Decision) content.getDefinition();
//            internalAugment(shape,
//                            d.getId(),
//                            ulBound,
//                            d.getDimensionsSet(),
//                            lrBound,
//                            d.getBackgroundSet(),
//                            d::setFontSet,
//                            (line) -> {/*NOP*/});
//        } else if (content.getDefinition() instanceof InputData) {
//            final InputData d = (InputData) content.getDefinition();
//            internalAugment(shape,
//                            d.getId(),
//                            ulBound,
//                            d.getDimensionsSet(),
//                            lrBound,
//                            d.getBackgroundSet(),
//                            d::setFontSet,
//                            (line) -> {/*NOP*/});
//        } else if (content.getDefinition() instanceof BusinessKnowledgeModel) {
//            final BusinessKnowledgeModel d = (BusinessKnowledgeModel) content.getDefinition();
//            internalAugment(shape,
//                            d.getId(),
//                            ulBound,
//                            d.getDimensionsSet(),
//                            lrBound,
//                            d.getBackgroundSet(),
//                            d::setFontSet,
//                            (line) -> {/*NOP*/});
//        } else if (content.getDefinition() instanceof KnowledgeSource) {
//            final KnowledgeSource d = (KnowledgeSource) content.getDefinition();
//            internalAugment(shape,
//                            d.getId(),
//                            ulBound,
//                            d.getDimensionsSet(),
//                            lrBound,
//                            d.getBackgroundSet(),
//                            d::setFontSet,
//                            (line) -> {/*NOP*/});
//        } else if (content.getDefinition() instanceof TextAnnotation) {
//            final TextAnnotation d = (TextAnnotation) content.getDefinition();
//            internalAugment(shape,
//                            d.getId(),
//                            ulBound,
//                            d.getDimensionsSet(),
//                            lrBound,
//                            d.getBackgroundSet(),
//                            d::setFontSet,
//                            (line) -> {/*NOP*/});
//        } else if (content.getDefinition() instanceof DecisionService) {
//            final DecisionService d = (DecisionService) content.getDefinition();
//            internalAugment(shape,
//                            d.getId(),
//                            ulBound,
//                            d.getDimensionsSet(),
//                            lrBound,
//                            d.getBackgroundSet(),
//                            d::setFontSet,
//                            (dividerLineY) -> d.setDividerLineY(new DecisionServiceDividerLineY(dividerLineY - ulBound.getY())));
//        }
//    }

//    private void internalAugment(final JSIDMNShape drgShape1,
//                                 final Id id,
//                                 final Bound ulBound,
//                                 final RectangleDimensionsSet dimensionsSet,
//                                 final Bound lrBound,
//                                 final BackgroundSet bgset,
//                                 final Consumer<FontSet> fontSetSetter,
//                                 final Consumer<Double> decisionServiceDividerLineYSetter) {
//        //Lookup JSIDMNShape corresponding to DRGElement...
//        Optional<JSIDMNShape> drgShapeOpt = Optional.ofNullable(drgShape1);
////        for (int i = 0; i < drgShapes.size(); i++) {
////            final JSIDMNShape jsiShape = Js.uncheckedCast(drgShapes.get(i));
////            final QName dmnRef = jsiShape.getDmnElementRef();
////            if (dmnRef.getLocalPart().endsWith(id.getValue())) {
////                drgShapeOpt = Optional.of(jsiShape);
////            }
////        }
//        if (!drgShapeOpt.isPresent()) {
//            return;
//        }
//
//        //Augment Stunner Node with Shape data
//        final JSIDMNShape drgShape = Js.uncheckedCast(drgShapeOpt.get());
//
//        if (Objects.nonNull(ulBound)) {
//            ulBound.setX(xOfShape(drgShape));
//            ulBound.setY(yOfShape(drgShape));
//        }
//        dimensionsSet.setWidth(new Width(widthOfShape(drgShape)));
//        dimensionsSet.setHeight(new Height(heightOfShape(drgShape)));
//        if (Objects.nonNull(lrBound)) {
//            lrBound.setX(xOfShape(drgShape) + widthOfShape(drgShape));
//            lrBound.setY(yOfShape(drgShape) + heightOfShape(drgShape));
//        }
//
//        internalAugmentStyles(drgShape,
//                              bgset,
//                              fontSetSetter);
//
//        if (Objects.nonNull(drgShape.getDMNDecisionServiceDividerLine())) {
//            final JSIDMNDecisionServiceDividerLine divider = Js.uncheckedCast(drgShape.getDMNDecisionServiceDividerLine());
//            final List<JSIPoint> dividerPoints = divider.getWaypoint();
//            final JSIPoint dividerY = Js.uncheckedCast(dividerPoints.get(0));
//            decisionServiceDividerLineYSetter.accept(dividerY.getY());
//        }
//    }

//    private void internalAugmentStyles(final JSIDMNShape drgShape,
//                                       final BackgroundSet bgset,
//                                       final Consumer<FontSet> fontSetSetter) {
//        final JSIStyle jsiStyle = drgShape.getStyle();
//        if (Objects.isNull(jsiStyle)) {
//            return;
//        }
//
//        final JSIStyle drgStyle = Js.uncheckedCast(JsUtils.getUnwrappedElement(jsiStyle));
//        final JSIDMNStyle dmnStyleOfDrgShape = JSIDMNStyle.instanceOf(drgStyle) ? Js.uncheckedCast(drgStyle) : null;
//        if (Objects.nonNull(dmnStyleOfDrgShape)) {
//            if (Objects.nonNull(dmnStyleOfDrgShape.getFillColor())) {
//                bgset.setBgColour(new BgColour(ColorUtils.wbFromDMN(dmnStyleOfDrgShape.getFillColor())));
//            }
//            if (Objects.nonNull(dmnStyleOfDrgShape.getStrokeColor())) {
//                bgset.setBorderColour(new BorderColour(ColorUtils.wbFromDMN(dmnStyleOfDrgShape.getStrokeColor())));
//            }
//        }
//
//        final FontSet fontSet = new FontSet();
//        if (Objects.nonNull(dmnStyleOfDrgShape)) {
//            mergeFontSet(fontSet, FontSetPropertyConverter.wbFromDMN(dmnStyleOfDrgShape));
//        }
//
//        if (Objects.nonNull(drgShape.getDMNLabel())) {
//            final JSIDMNShape jsiLabel = Js.uncheckedCast(drgShape.getDMNLabel());
//            final JSIStyle jsiLabelStyle = jsiLabel.getStyle();
//            final Object jsiLabelSharedStyle = Js.uncheckedCast(jsiLabel.getSharedStyle());
//            if (Objects.nonNull(jsiLabelSharedStyle) && JSIDMNStyle.instanceOf(jsiLabelSharedStyle)) {
//                mergeFontSet(fontSet, FontSetPropertyConverter.wbFromDMN((Js.uncheckedCast(jsiLabelSharedStyle))));
//            }
//            if (Objects.nonNull(jsiLabelStyle) && JSIDMNStyle.instanceOf(jsiLabelStyle)) {
//                mergeFontSet(fontSet, FontSetPropertyConverter.wbFromDMN(Js.uncheckedCast(jsiLabelStyle)));
//            }
//        }
//        fontSetSetter.accept(fontSet);
//    }
//
//    private void mergeFontSet(final FontSet fontSet,
//                              final FontSet additional) {
//        if (Objects.nonNull(additional.getFontFamily())) {
//            fontSet.setFontFamily(additional.getFontFamily());
//        }
//        if (Objects.nonNull(additional.getFontSize())) {
//            fontSet.setFontSize(additional.getFontSize());
//        }
//        if (Objects.nonNull(additional.getFontColour())) {
//            fontSet.setFontColour(additional.getFontColour());
//        }
//    }
}
