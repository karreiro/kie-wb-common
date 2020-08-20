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

package org.kie.workbench.common.dmn.client.editors.drd;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramTuple;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

@ApplicationScoped
public class DRDContextMenuService {

    private static String NEW_DIAGRAM_NAME = "new-diagram";

    @Inject
    private DMNDiagramsSession dmnDiagramsSession;

    @Inject
    private FactoryManager factoryManager;

    @Inject
    private Event<DMNDiagramSelected> selectedEvent;

    @Inject
    private DMNDiagramUtils dmnDiagramUtils;

    public List<DMNDiagramTuple> getDiagrams() {
        return dmnDiagramsSession.getDMNDiagrams();
    }

    public void addToNewDRD(final Collection<Node<? extends Definition<?>, Edge>> selectedNodes) {

        final DMNDiagramElement dmnElement = makeDmnDiagramElement();
        final Diagram stunnerElement = buildStunnerElement(dmnElement);

        addDmnDiagramElementToDRG(dmnElement);
        selectedNodes.forEach(node -> {
            final Definition<?> content = node.getContent();
            final Object definition = content.getDefinition();
//
            if (definition instanceof DRGElement) {
                final DRGElement drgElement = (DRGElement) definition;
                drgElement.setDiagramId(dmnElement.getId().getValue());

//                final String nodeId = new Id().getValue();
//                final Node<? extends Definition<DRGElement>, Edge> clone = (Node<? extends Definition<DRGElement>, Edge>) factoryManager.newElement(nodeId,
//                                                                                                                                                    getDefinitionId(Decision.class)).asNode();
//
//                final Definition<DRGElement> cloneContent = clone.getContent();
//
//                drgElement.setDiagramId(dmnElement.getId().getValue());
//                cloneContent.setDefinition(drgElement);
//
//                clone.getOutEdges().clear();
//                clone.getInEdges().clear();
//                clone.getLabels().clear();
//
//                node.getOutEdges().forEach(edge -> clone.getOutEdges().add(edge));
//                node.getInEdges().forEach(edge -> clone.getInEdges().add(edge));
//                node.getLabels().forEach(label -> clone.getLabels().add(label));
            }

            if (definition instanceof TextAnnotation) {
                final TextAnnotation textAnnotation = (TextAnnotation) definition;
                textAnnotation.setDiagramId(dmnElement.getId().getValue());
            }

            stunnerElement.getGraph().addNode(node);
        });

        dmnDiagramsSession.add(dmnElement, stunnerElement);
        selectedEvent.fire(new DMNDiagramSelected(dmnElement));
    }

    public void addToExistingDRD(final DMNDiagramTuple dmnDiagram,
                                 final Collection<Node<? extends Definition<?>, Edge>> selectedNodes) {

        dmnDiagramsSession
                .getDMNDiagrams()
                .stream()
                .filter(e -> {
                    final String diagramId = e.getDMDNDiagram().getId().getValue();
                    final String selectedDiagramId = dmnDiagram.getDMDNDiagram().getId().getValue();
                    return Objects.equals(diagramId, selectedDiagramId);
                })
                .findFirst()
                .ifPresent(diagram -> {
                    selectedNodes.forEach(node -> diagram.getStunnerDiagram().getGraph().addNode(node));
                    dmnDiagramsSession.add(diagram.getDMDNDiagram(), diagram.getStunnerDiagram());
                    selectedEvent.fire(new DMNDiagramSelected(dmnDiagram.getDMDNDiagram()));
                });
    }

    private void addDmnDiagramElementToDRG(final DMNDiagramElement dmnElement) {
        dmnDiagramUtils
                .getDefinitions(dmnDiagramsSession.getDRGDiagram())
                .getDmnDiagramElements()
                .add(dmnElement);
    }

    private Diagram buildStunnerElement(final DMNDiagramElement dmnElement) {
        final String diagramId = dmnElement.getId().getValue();
        return factoryManager.newDiagram(diagramId,
                                         BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class),
                                         getMetadata());
    }

    private DMNDiagramElement makeDmnDiagramElement() {
        final DMNDiagramElement diagramElement = new DMNDiagramElement();
        diagramElement.getName().setValue(getUniqueName());
        return diagramElement;
    }

    private String getUniqueName() {
        final List<String> currentDiagramNames = dmnDiagramsSession.getDMNDiagrams().stream().map(e -> e.getDMDNDiagram().getName().getValue()).collect(Collectors.toList());

        if (currentDiagramNames.contains(NEW_DIAGRAM_NAME)) {
            return getUniqueName(2, currentDiagramNames);
        }

        return NEW_DIAGRAM_NAME;
    }

    private String getUniqueName(final int seeds,
                                 final List<String> currentDiagramNames) {

        final String newDiagramName = NEW_DIAGRAM_NAME + "-" + seeds;

        if (currentDiagramNames.contains(newDiagramName)) {
            return getUniqueName(seeds + 1, currentDiagramNames);
        }

        return newDiagramName;
    }

    private Metadata getMetadata() {
        return dmnDiagramsSession.getDRGDiagram().getMetadata();
    }
}
