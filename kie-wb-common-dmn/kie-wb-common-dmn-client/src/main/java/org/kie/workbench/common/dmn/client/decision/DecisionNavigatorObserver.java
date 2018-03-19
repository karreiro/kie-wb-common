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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;

@ApplicationScoped
public class DecisionNavigatorObserver {

    @Inject
    private SessionManager sessionManager;

    private DecisionNavigatorPresenter presenter;

    public void init(final DecisionNavigatorPresenter presenter) {
        this.presenter = presenter;
    }

    void onCanvasClearEvent(@Observes CanvasClearEvent event) {
        try {
            presenter.removeAllElements();
        } catch (Exception e) {
            GWT.log("==> TODO 1");
        }
    }

    void onCanvasElementAddedEvent(final @Observes CanvasElementAddedEvent event) {

        try {
            final Element<?> element = event.getElement();
            presenter.addElement(element);
        } catch (Exception e) {
            GWT.log("==> TODO 2");
        }
    }

    void onCanvasElementUpdatedEvent(final @Observes CanvasElementUpdatedEvent event) {

        try {
            final Element<?> element = event.getElement();
            presenter.addElement(element);
        } catch (Exception e) {
            GWT.log("==> TODO 3");
        }
    }

    void onCanvasElementRemovedEvent(final @Observes CanvasElementRemovedEvent event) {
        try {
            final Element<?> element = event.getElement();
            presenter.removeElement(element);
        } catch (Exception e) {
            GWT.log("==> TODO 1");
        }
    }

    void onExpressionEditorSelectedEvent(final @Observes EditExpressionEvent event) {
        try {
            HasExpression hasExpression = event.getHasExpression();
            Id id = ((Expression) hasExpression).getId();
            String value = id.getValue();
            DecisionNavigatorItem item = new DecisionNavigatorItem(value, null, null);

            item.getParents().add(presenter.getTreePresenter().getItem(event.getNode().getUUID()));

            presenter.getTreePresenter().selectItem(item);

            GWT.log("---> EditExpressionEvent :::" + value);
        } catch (Exception e) {
            GWT.log("==> TODO 4");
        }
    }

    void onExpressionEditorSelectedEvent(final @Observes ExpressionEditorChanged event) {

        try {
            String selectedUUID = presenter.getTreePresenter().getSelectedUUID();
            DecisionNavigatorItem item = presenter.getTreePresenter().getItem(selectedUUID);

            presenter
                    .getTreePresenter()
                    .getAffectedItems()
                    .stream()
                    .map(DecisionNavigatorItem::getUUID)
                    .forEach(i -> {
                        Node node = presenter.getHandler().getDiagram().getGraph().getNode(i);
                        presenter.addElement(node);
                    });

//            if (selectedUUID == null) {
//                GWT.log(" NULLLL ");
//            } else {
//
//                Node node = presenter.getHandler().getDiagram().getGraph().getNode(selectedUUID);
//
//                presenter.addElement(node);
//            }

        } catch (Exception e) {
            GWT.log("==> TODO 5");
        }
    }

    void onCanvasSelectionEvent(final @Observes CanvasSelectionEvent event) {
        GWT.log("---> CanvasSelectionEvent");
    }
}
