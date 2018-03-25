/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.decision;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.decision.factories.DecisionNavigatorItemFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;

@Dependent
public class DecisionNavigatorChildrenTraverse {

    private final ChildrenTraverseProcessor traverseProcessor;

    private final DecisionNavigatorItemFactory itemFactory;

    @Inject
    public DecisionNavigatorChildrenTraverse(final ChildrenTraverseProcessor traverseProcessor,
                                             final DecisionNavigatorItemFactory itemFactory) {
        this.traverseProcessor = traverseProcessor;
        this.itemFactory = itemFactory;
    }

    public List<DecisionNavigatorItem> getItems(final Graph graph) {

        final TraverseCallback traverseCallback = makeTraverseCallback();

        traverseProcessor.traverse(graph, traverseCallback);

        return traverseCallback.getItems();
    }

    TraverseCallback makeTraverseCallback() {
        return new TraverseCallback();
    }

    class TraverseCallback extends AbstractChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>> {

        private List<DecisionNavigatorItem> items;

        TraverseCallback() {
            this.items = new ArrayList<>();
        }

        List<DecisionNavigatorItem> getItems() {
            return items;
        }

        @Override
        public boolean startNodeTraversal(final List<Node<View, Edge>> parents,
                                          final Node<View, Edge> node) {

            super.startNodeTraversal(parents, node);

            final DecisionNavigatorItem item = itemFactory.makeItem(node);
            final List<DecisionNavigatorItem> parentItems = findItems(parents);

            item.getParents().addAll(parentItems);
            parentItems.forEach(parentItem -> parentItem.getChildren().add(item));

            return true;
        }

        @Override
        public void startNodeTraversal(final Node<View, Edge> node) {
            super.startNodeTraversal(node);
            getItems().add(itemFactory.makeRoot(node));
        }

        List<DecisionNavigatorItem> findItems(final List<Node<View, Edge>> nodes) {

            final List<String> nodesUUIDs = nodesUUIDs(nodes);

            return getItems()
                    .stream()
                    .filter(item -> nodesUUIDs.contains(item.getUUID()))
                    .collect(Collectors.toList());
        }

        private List<String> nodesUUIDs(final List<Node<View, Edge>> nodes) {
            return nodes
                    .stream()
                    .map(Element::getUUID)
                    .collect(Collectors.toList());
        }
    }
}
