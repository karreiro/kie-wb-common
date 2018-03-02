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

package org.kie.workbench.common.dmn.showcase.client.screens.decision;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.workbench.common.dmn.showcase.client.screens.decision.DecisionNavigatorItem.Type.ITEM;

@Dependent
public class DecisionNavigatorChildrenTraverse {

    private static final String NO_NAME = "- No name -";

    private final ChildrenTraverseProcessor traverseProcessor;

    private final TextPropertyProviderFactory textPropertyProviderFactory;

    private final DefinitionUtils definitionUtils;

    @Inject
    public DecisionNavigatorChildrenTraverse(final ChildrenTraverseProcessor traverseProcessor,
                                             final TextPropertyProviderFactory textPropertyProviderFactory,
                                             final DefinitionUtils definitionUtils) {
        this.traverseProcessor = traverseProcessor;
        this.textPropertyProviderFactory = textPropertyProviderFactory;
        this.definitionUtils = definitionUtils;
    }

    public List<DecisionNavigatorItem> getItems(final Graph<View, Node<View, Edge>> graph) {

        final TraverseCallback traverseCallback = makeTraverseCallback();

        traverseProcessor.traverse(graph, traverseCallback);

        return traverseCallback.getItems();
    }

    private TraverseCallback makeTraverseCallback() {
        return new TraverseCallback();
    }

    private String getItemName(final Element<View> item) {

        try {
            final TextPropertyProvider provider = textPropertyProviderFactory.getProvider(item);
            final String name = provider.getText(item);


            DefinitionManager definitionManager = definitionUtils.getDefinitionManager();

            AdapterManager adapters = definitionManager.adapters();

            DefinitionAdapter<Object> objectDefinitionAdapter = adapters.forDefinition();

            View content = item.getContent();

            Object definition = content.getDefinition();

            final String title = objectDefinitionAdapter.getTitle(definition);

//            GWT.log("----------------> " + objectDefinitionAdapter.getProperties(definition));
//            Set<?> properties = objectDefinitionAdapter.getMetaProperty(definition);

            if ((name == null || name.trim().equals("")) && title != null) {
                GWT.log(title + "------------------------------------------------>");
                GWT.log(definition.toString());

                try {
                    DMNViewDefinition definition1 = (DMNViewDefinition) definition;
                    GWT.log("============> " + String.join(", ", definition1.getStunnerLabels()));
                } catch (Exception e) {
                    GWT.log("============> e r r o r");
                }

                GWT.log(title + "------------------------------------------------<");
                return title;
            }

            GWT.log(name + "------------------------------------------------>");
            GWT.log(definition.toString());
            try {
                DMNViewDefinition definition1 = (DMNViewDefinition) definition;
                GWT.log("============> " + String.join(", ", definition1.getStunnerLabels()));
            } catch (Exception e) {
                GWT.log("============> e r r o r");
            }
            GWT.log(name + "------------------------------------------------<");

            return (name != null ? name : NO_NAME);
        } catch (Exception e) {
            return NO_NAME + "---";
        }
    }

    private class TraverseCallback extends AbstractChildrenTraverseCallback<Node<View, Edge>, Edge<Child, Node>> {

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

            List<String> uuids = parents.stream().map((viewEdgeNode) -> viewEdgeNode.getUUID()).collect(Collectors.toList());
            DecisionNavigatorItem child = new DecisionNavigatorItem(node.getUUID(), getItemName(node), ITEM);

            items.stream().filter(i -> uuids.contains(i.getUUID())).collect(Collectors.toList()).forEach(item -> {
                item.getChildren().add(child);
            });

            items.add(child);

//        addItem(parents.get(parents.size() - 1),
//                node,
//                expand,
//                false);
            return true;
        }

        @Override
        public void startNodeTraversal(final Node<View, Edge> node) {

            super.startNodeTraversal(node);

            items.add(new DecisionNavigatorItem(node.getUUID(), getItemName(node), ITEM));
        }
    }
}
