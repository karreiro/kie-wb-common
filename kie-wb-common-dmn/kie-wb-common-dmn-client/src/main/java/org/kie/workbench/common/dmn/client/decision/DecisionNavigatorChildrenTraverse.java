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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.Invocation;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Relation;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.COLUMNS;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.ITEM;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.ROOT;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.SUB_ITEM;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.TABLE;

@Dependent
public class DecisionNavigatorChildrenTraverse {

    private static final String NO_NAME = "- No name -";

    private final ChildrenTraverseProcessor traverseProcessor;

    private final TextPropertyProviderFactory textPropertyProviderFactory;

    private final DefinitionUtils definitionUtils;

    @Inject
    private SessionManager sessionManager;

    @Inject
    private Event<EditExpressionEvent> eventEditExpressionEvent;

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

    protected DecisionNavigatorItem makeItem(final Node<View, Edge> node,
                                             final DecisionNavigatorItem.Type type) {

        final String uuid = node.getUUID();
        final String label = getItemName(node);
        final List<DecisionNavigatorItem> nestedElements = getNestedElements(node);

        DecisionNavigatorItem decisionNavigatorItem = new DecisionNavigatorItem(uuid, label, type, nestedElements);

        nestedElements.forEach(n -> n.getParents().add(decisionNavigatorItem));

        return decisionNavigatorItem;
    }

    private List<DecisionNavigatorItem> getNestedElements(final Node<View, Edge> node) {

        final boolean hasNestedElement = getExpression(node).isPresent();

        try {
            if (hasNestedElement) {

                Expression expression = getExpression(node).get();
                Class<? extends Expression> expressionClass = expression.getClass();
                String label = expressionClass.getSimpleName();
                DecisionNavigatorItem.Type type = getType(expressionClass);

                final Object definition = getDefinition(node);

                final EditExpressionEvent editExpressionEvent;

                if (definition instanceof BusinessKnowledgeModel) {

                    BusinessKnowledgeModel bkm = (BusinessKnowledgeModel) definition;
                    editExpressionEvent = new EditExpressionEvent(sessionManager.getCurrentSession(), Optional.of(bkm), bkm.getEncapsulatedLogic(), node);
                } else if (definition instanceof Decision) {
                    Decision decision = (Decision) definition;
                    editExpressionEvent = new EditExpressionEvent(sessionManager.getCurrentSession(), Optional.of(decision), decision, node);
                } else {
                    editExpressionEvent = null;
                }

                HasExpression hasExpression = editExpressionEvent.getHasExpression();
                String uuid = ((DMNElement) hasExpression).getId().getValue();
                final DecisionNavigatorItem e = new DecisionNavigatorItem(uuid, label, type);
                e.setEditExpressionEvent(editExpressionEvent);
                e.setEventEditExpressionEvent(eventEditExpressionEvent);
                return new ArrayList<DecisionNavigatorItem>() {{

                    add(e);
                }};
            }
        } catch (Exception e) {
            GWT.log("--> ");
        }

        return new ArrayList<>();
    }

    private DecisionNavigatorItem.Type getType(final Class<? extends Expression> expressionClass) {
        Map<Class<? extends Expression>, DecisionNavigatorItem.Type> map = new HashMap<Class<? extends Expression>, DecisionNavigatorItem.Type>() {{
            put(Context.class, SUB_ITEM);
            put(DecisionTable.class, TABLE);
            put(FunctionDefinition.class, ITEM);
            put(Invocation.class, SUB_ITEM);
            put(org.kie.workbench.common.dmn.api.definition.v1_1.List.class, COLUMNS);
            put(LiteralExpression.class, SUB_ITEM);
            put(Relation.class, COLUMNS);
        }};
        return map.get(expressionClass);
    }

    private Optional<Expression> getExpression(final Node<View, Edge> node) {

        final Object definition = getDefinition(node);
        final Expression expression;

        if (definition instanceof BusinessKnowledgeModel) {
            expression = ((BusinessKnowledgeModel) definition).getEncapsulatedLogic().getExpression();
        } else if (definition instanceof Decision) {
            expression = ((Decision) definition).getExpression();
        } else {
            expression = null;
        }

        return Optional.ofNullable(expression);
    }

    private Object getDefinition(final Node<View, Edge> node) {

        final View content = node.getContent();

        return content.getDefinition();
    }

    private String getItemName(final Element<View> element) {

        final String name = getName(element);
        final String title = getTitle(element);

        if ((name == null || name.trim().equals("")) && title != null) {
            return title;
        }

        return (name != null ? name : NO_NAME);
    }

    private String getName(final Element<View> element) {
        final TextPropertyProvider provider = textPropertyProviderFactory.getProvider(element);
        return provider.getText(element);
    }

    private String getTitle(final Element<View> element) {

        final AdapterManager adapters = definitionUtils.getDefinitionManager().adapters();
        final DefinitionAdapter<Object> objectDefinitionAdapter = adapters.forDefinition();

        return objectDefinitionAdapter.getTitle(element.getContent().getDefinition());
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

            final DecisionNavigatorItem child = makeItem(node, ITEM);
            final List<DecisionNavigatorItem> parentItems = parentItems(parents);

            child.getParents().addAll(parentItems);
            parentItems.forEach(item -> item.getChildren().add(child));

            return true;
        }

        private List<DecisionNavigatorItem> parentItems(final List<Node<View, Edge>> parents) {

            final Stream<String> parentsUUIDs = parents.stream().map(Element::getUUID);
            final Predicate<DecisionNavigatorItem> byUUID = item -> parentsUUIDs.anyMatch(uuid -> uuid.equals(item.getUUID()));

            return items.stream().filter(byUUID).collect(Collectors.toList());
        }

        @Override
        public void startNodeTraversal(final Node<View, Edge> node) {

            super.startNodeTraversal(node);

            items.add(makeItem(node, ROOT));
        }
    }
}
