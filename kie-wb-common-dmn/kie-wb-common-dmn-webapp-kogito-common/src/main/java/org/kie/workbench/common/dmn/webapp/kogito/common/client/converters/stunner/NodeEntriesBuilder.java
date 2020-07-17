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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.stunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITTextAnnotation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNShape;
import org.kie.workbench.common.stunner.core.graph.Node;

class NodeEntriesBuilder {

    private final Map<JSIDMNShape, String> shapesByDiagramId = new HashMap<>();

    private final List<JSITDRGElement> drgElements = new ArrayList<>();

    private final List<JSITDRGElement> includedDRGElements = new ArrayList<>();

    private final List<JSITTextAnnotation> textAnnotations = new ArrayList<>();

    private final StunnerNodeFactory nodeFactory;

    private BiConsumer<String, HasComponentWidths> componentWidthsConsumer;

    NodeEntriesBuilder(final StunnerNodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    List<NodeEntry> buildEntries() {
        return shapesByDiagramId
                .entrySet()
                .stream()
                .map(entry -> {
                    final String diagramId = entry.getValue();
                    final JSIDMNShape shape = Js.uncheckedCast(entry.getKey());
                    return makeEntry(diagramId, shape);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    NodeEntriesBuilder withShapesByDiagramId(final Map<JSIDMNShape, String> shapesByDiagramId) {
        this.shapesByDiagramId.putAll(shapesByDiagramId);
        return this;
    }

    NodeEntriesBuilder withDRGElements(final List<JSITDRGElement> drgElements) {
        this.drgElements.addAll(drgElements);
        return this;
    }

    NodeEntriesBuilder withTextAnnotations(final List<JSITTextAnnotation> textAnnotations) {
        this.textAnnotations.addAll(textAnnotations);
        return this;
    }

    NodeEntriesBuilder withIncludedDRGElements(final List<JSITDRGElement> includedDRGElements) {
        this.includedDRGElements.addAll(includedDRGElements);
        return this;
    }

    NodeEntriesBuilder withComponentWidthsConsumer(final BiConsumer<String, HasComponentWidths> componentWidthsConsumer) {
        this.componentWidthsConsumer = componentWidthsConsumer;
        return this;
    }

    private Optional<NodeEntry> makeEntry(final String diagramId,
                                          final JSIDMNShape shape) {
        return getDMNElement(shape)
                .map(dmnElement -> {

                    final boolean isIncluded = isIncluded(dmnElement);
                    final NodeEntry nodeEntry = new NodeEntry(diagramId, shape, dmnElement, isIncluded, componentWidthsConsumer);
                    final Node node = nodeFactory.make(nodeEntry);

                    nodeEntry.setNode(node);

                    return nodeEntry;
                });
    }

    private boolean isIncluded(final JSITDMNElement dmnElement) {
        return includedDRGElements.contains(dmnElement);
    }

    private Optional<JSITDMNElement> getDMNElement(final JSIDMNShape shape) {
        return Stream
                .of(drgElements, includedDRGElements, textAnnotations)
                .<JSITDMNElement>flatMap(Collection::stream)
                .filter(dmnElement -> {
                    final QName dmnElementRef = shape.getDmnElementRef();
                    final String dmnElementId = dmnElement.getId();
                    return dmnElementRef.getLocalPart().endsWith(dmnElementId);
                })
                .findFirst();
    }
}
