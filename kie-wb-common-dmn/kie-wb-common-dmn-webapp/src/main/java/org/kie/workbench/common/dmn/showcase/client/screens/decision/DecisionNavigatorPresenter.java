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

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.dmn.showcase.client.screens.decision.tree.DecisionNavigatorTreePresenter;
import org.kie.workbench.common.dmn.showcase.client.screens.decision.tree.DecisionNavigatorTreeView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

@ApplicationScoped
@WorkbenchScreen(identifier = "org.kie.dmn.decision.navigator")
public class DecisionNavigatorPresenter {

    @Inject
    private View view;

    @Inject
    private DecisionNavigatorTreePresenter treePresenter;

    @Inject
    private DecisionNavigatorChildrenTraverse navigatorChildrenTraverse;

    private AbstractCanvasHandler handler;

    @WorkbenchPartView
    public UberElemental<DecisionNavigatorPresenter> getView() {
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
    }

    public DecisionNavigatorTreeView getTreeView() {
        return initTreePresenter().getView();
    }

    private DecisionNavigatorTreePresenter initTreePresenter() {
        final DecisionNavigatorTreePresenter treePresenter = this.treePresenter;
        treePresenter.setupItems(getItems());
        return treePresenter;
    }

    private List<DecisionNavigatorItem> getItems() {

        if (handler == null) {
            GWT.log("=====> handler == null");
            return new ArrayList<>();
        }

        if (handler.getDiagram() == null) {
            GWT.log("=====> handler.getDiagram() == null");
            return new ArrayList<>();
        }

        if (handler.getDiagram().getGraph() == null) {
            GWT.log("=====> handler.getDiagram().getGraph() == null");
            return new ArrayList<>();
        }

        GWT.log("=====> shh" + navigatorChildrenTraverse.getItems(handler.getDiagram().getGraph()).size());
//        return new ArrayList<>();

        return this.navigatorChildrenTraverse.getItems(handler.getDiagram().getGraph());
    }

    public void setHandler(final AbstractCanvasHandler handler) {
        this.handler = handler;

        initTreePresenter();
    }

    public interface View extends UberElemental<DecisionNavigatorPresenter> {

    }
}
