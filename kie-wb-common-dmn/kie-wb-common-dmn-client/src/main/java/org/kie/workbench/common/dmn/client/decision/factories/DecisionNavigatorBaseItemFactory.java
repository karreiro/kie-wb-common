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

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorPresenter;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasFocusedShapeEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.mvp.Command;

@Dependent
public class DecisionNavigatorBaseItemFactory {

    private static final String NO_NAME = "- No name -";

    private DecisionNavigatorNestedItemFactory nestedItemFactory;

    private DecisionNavigatorPresenter decisionNavigatorPresenter;

    private TextPropertyProviderFactory textPropertyProviderFactory;

    private Event<CanvasFocusedShapeEvent> canvasFocusedSelectionEvent;

    private Event<CanvasSelectionEvent> canvasSelectionEvent;

    private DefinitionUtils definitionUtils;

    @Inject
    public DecisionNavigatorBaseItemFactory(final DecisionNavigatorNestedItemFactory nestedItemFactory,
                                            final DecisionNavigatorPresenter decisionNavigatorPresenter,
                                            final TextPropertyProviderFactory textPropertyProviderFactory,
                                            final Event<CanvasFocusedShapeEvent> canvasFocusedSelectionEvent,
                                            final Event<CanvasSelectionEvent> canvasSelectionEvent,
                                            final DefinitionUtils definitionUtils) {
        this.nestedItemFactory = nestedItemFactory;
        this.decisionNavigatorPresenter = decisionNavigatorPresenter;
        this.textPropertyProviderFactory = textPropertyProviderFactory;
        this.canvasFocusedSelectionEvent = canvasFocusedSelectionEvent;
        this.canvasSelectionEvent = canvasSelectionEvent;
        this.definitionUtils = definitionUtils;
    }

    DecisionNavigatorItem makeItem(final Node<View, Edge> node,
                                   final DecisionNavigatorItem.Type type) {

        final String uuid = node.getUUID();
        final String label = getLabel(node);
        final Command onClick = onClickCommand(node);
        final List<DecisionNavigatorItem> nestedItems = makeNestedItems(node);

        final DecisionNavigatorItem item = new DecisionNavigatorItem(uuid, label, type, onClick, nestedItems);

        nestedItems.forEach(nestedItem -> {
            nestedItem.getParents().add(item);
        });

        return item;
    }

    private Command onClickCommand(final Node<View, Edge> node) {

        final CanvasHandler canvas = decisionNavigatorPresenter.getHandler();
        final String uuid = node.getUUID();

        return () -> {
            canvasSelectionEvent.fire(new CanvasSelectionEvent(canvas, uuid));
            canvasFocusedSelectionEvent.fire(new CanvasFocusedShapeEvent(canvas, uuid));
        };
    }

    private String getLabel(final Element<View> element) {

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

    private List<DecisionNavigatorItem> makeNestedItems(final Node<View, Edge> node) {
        return new ArrayList<DecisionNavigatorItem>() {{
            if (hasNestedElement(node)) {
                add(nestedItemFactory.makeItem(node));
            }
        }};
    }

    private boolean hasNestedElement(final Node<View, Edge> node) {
        return nestedItemFactory.hasNestedElement(node);
    }
}
