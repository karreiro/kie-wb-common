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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.showcase.client.screens.decision.tree.DecisionNavigatorTreePresenter;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

import static org.kie.workbench.common.dmn.showcase.client.screens.decision.DecisionNavigatorItem.Type.ITEM;

@ApplicationScoped
@WorkbenchScreen(identifier = DecisionNavigatorPresenter.SCREEN_ID)
public class DecisionNavigatorPresenter {

    public static final String SCREEN_ID = "org.kie.dmn.decision.navigator";

    @Inject
    private View view;

    @Inject
    private DecisionNavigatorTreePresenter treePresenter;

    @Inject
    private DecisionNavigatorObserver decisionNavigatorObserver;

    @Inject
    private DecisionNavigatorChildrenTraverse navigatorChildrenTraverse;

    private AbstractCanvasHandler handler;

    @WorkbenchPartView
    public View getView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Decision Navigator";
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return CompassPosition.WEST;
    }

    @PostConstruct
    private void setup() {
        view.init(this);
        decisionNavigatorObserver.init(this);
    }

    public DecisionNavigatorTreePresenter.View getTreeView() {
        return initTreePresenter().getView();
    }

    public DecisionNavigatorTreePresenter getTreePresenter() {
        return treePresenter;
    }

    private DecisionNavigatorTreePresenter initTreePresenter() {

        treePresenter.setupItems(getItems());

        return treePresenter;
    }

    private List<DecisionNavigatorItem> getItems() {

        if (handler == null) {
            return new ArrayList<>();
        }

        if (handler.getDiagram() == null) {
            return new ArrayList<>();
        }

        if (handler.getDiagram().getGraph() == null) {
            return new ArrayList<>();
        }

        return getItems(handler);
    }

    @SuppressWarnings("unchecked")
    private List<DecisionNavigatorItem> getItems(final AbstractCanvasHandler handler) {
        return navigatorChildrenTraverse.getItems(handler.getDiagram().getGraph());
    }

    public void setHandler(final AbstractCanvasHandler handler) {
        this.handler = handler;

        initTreePresenter();
    }

    public AbstractCanvasHandler getHandler() {
        return handler;
    }

    public void addElement(final Element<?> element) {

        final Element parentElement = GraphUtils.getParent((Node<?, ? extends Edge>) element);
        final DecisionNavigatorItem parent = makeItem(parentElement);
        final DecisionNavigatorItem item = makeItem(element);

        treePresenter.addOrUpdateItem(parent, item);
    }

    public void removeElement(final Element<?> element) {

        final DecisionNavigatorItem item = makeItem(element);

        treePresenter.remove(item);
    }

    private DecisionNavigatorItem makeItem(final Element<?> element) {

        final Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge> node =
                (Node<org.kie.workbench.common.stunner.core.graph.content.view.View, Edge>) element.asNode();

        return navigatorChildrenTraverse.makeItem(node, ITEM);
    }

    public void removeAllElements() {
        treePresenter.removeAllElements();
    }

    public interface View extends UberElemental<DecisionNavigatorPresenter> {

    }
}
