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

package org.kie.workbench.common.dmn.client.decision.factories;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.Invocation;
import org.kie.workbench.common.dmn.api.definition.v1_1.List;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Relation;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.COLUMNS;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.SUB_ITEM;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.TABLE;

@Dependent
public class DecisionNavigatorNestedItemFactory {

    private static final Map<Class<? extends Expression>, DecisionNavigatorItem.Type> ITEM_TYPE_BY_EXPRESSION =
            new HashMap<Class<? extends Expression>, DecisionNavigatorItem.Type>() {{
                put(Context.class, SUB_ITEM);
                put(DecisionTable.class, TABLE);
                put(FunctionDefinition.class, SUB_ITEM);
                put(Invocation.class, SUB_ITEM);
                put(List.class, COLUMNS);
                put(LiteralExpression.class, SUB_ITEM);
                put(Relation.class, COLUMNS);
            }};

    private final SessionManager sessionManager;

    private final Event<EditExpressionEvent> editExpressionEvent;

    @Inject
    public DecisionNavigatorNestedItemFactory(final SessionManager sessionManager,
                                              final Event<EditExpressionEvent> editExpressionEvent) {
        this.sessionManager = sessionManager;
        this.editExpressionEvent = editExpressionEvent;
    }

    public DecisionNavigatorItem makeItem(final Node<View, Edge> node) {

        final String uuid = getUUID(node);
        final DecisionNavigatorItem.Type type = getType(node);
        final String label = getLabel(node);
        final Command onClick = makeOnClickCommand(node);

        return new DecisionNavigatorItem(uuid, label, type, onClick);
    }

    public boolean hasNestedElement(final Node<View, Edge> node) {
        return getOptionalHasExpression(node).isPresent() && getOptionalExpression(node).isPresent();
    }

    Command makeOnClickCommand(final Node<View, Edge> node) {
        return () -> {
            editExpressionEvent.fire(makeEditExpressionEvent(node));
        };
    }

    EditExpressionEvent makeEditExpressionEvent(final Node<View, Edge> node) {

        final ClientSession currentSession = sessionManager.getCurrentSession();
        final Optional<HasName> hasName = Optional.of((HasName) getDefinition(node));
        final HasExpression hasExpression = getHasExpression(node);

        return new EditExpressionEvent(currentSession, node.getUUID(), hasExpression, hasName);
    }

    String getUUID(final Node<View, Edge> node) {
        final Expression expression = getExpression(node);
        return expression.getId().getValue();
    }

    String getLabel(final Node<View, Edge> node) {
        return getExpression(node).getClass().getSimpleName();
    }

    DecisionNavigatorItem.Type getType(final Node<View, Edge> node) {
        return ITEM_TYPE_BY_EXPRESSION.get(getExpression(node).getClass());
    }

    Optional<HasExpression> getOptionalHasExpression(final Node<View, Edge> node) {

        final Object definition = getDefinition(node);
        final HasExpression expression;

        if (definition instanceof BusinessKnowledgeModel) {
            expression = ((BusinessKnowledgeModel) definition).getEncapsulatedLogic();
        } else if (definition instanceof Decision) {
            expression = (Decision) definition;
        } else {
            expression = null;
        }

        return Optional.ofNullable(expression);
    }

    Optional<Expression> getOptionalExpression(final Node<View, Edge> node) {
        return Optional.ofNullable(getExpression(node));
    }

    Expression getExpression(final Node<View, Edge> node) {
        return getHasExpression(node).getExpression();
    }

    HasExpression getHasExpression(final Node<View, Edge> node) {
        return getOptionalHasExpression(node).orElseThrow(RuntimeException::new);
    }

    Object getDefinition(final Node<View, Edge> node) {
        return node.getContent().getDefinition();
    }
}
