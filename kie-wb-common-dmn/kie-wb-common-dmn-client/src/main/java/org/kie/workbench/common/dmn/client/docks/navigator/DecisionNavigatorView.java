/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.docks.navigator;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter;

import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.hide;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.show;

@Templated
public class DecisionNavigatorView implements DecisionNavigatorPresenter.View {

    @DataField("switch-to-global")
    private final HTMLDivElement switchToGlobal;

    @DataField("switch-to-1")
    private final HTMLDivElement switchTo1;

    @DataField("switch-to-2")
    private final HTMLDivElement switchTo2;

    @DataField("switch-to-3")
    private final HTMLDivElement switchTo3;

    @DataField("main-tree")
    private final HTMLDivElement mainTree;

    @DataField("decision-components-container")
    private final HTMLDivElement decisionComponentsContainer;

    @DataField("decision-components")
    private final HTMLDivElement decisionComponents;

    private final GraphDRDSwitchPOC switchPOC;

    private DecisionNavigatorPresenter presenter;

    @Inject
    public DecisionNavigatorView(final HTMLDivElement switchToGlobal,
                                 final HTMLDivElement switchTo1,
                                 final HTMLDivElement switchTo2,
                                 final HTMLDivElement switchTo3,
                                 final HTMLDivElement mainTree,
                                 final HTMLDivElement decisionComponentsContainer,
                                 final HTMLDivElement decisionComponents,
                                 final GraphDRDSwitchPOC switchPOC) {
        this.switchToGlobal = switchToGlobal;
        this.switchTo1 = switchTo1;
        this.switchTo2 = switchTo2;
        this.switchTo3 = switchTo3;
        this.mainTree = mainTree;
        this.decisionComponentsContainer = decisionComponentsContainer;
        this.decisionComponents = decisionComponents;
        this.switchPOC = switchPOC;
    }

    @EventHandler("switch-to-global")
    public void switchToGlobal(final ClickEvent event) {
        switchPOC.hideNodes(0);
    }

    @EventHandler("switch-to-1")
    public void switchTo1(final ClickEvent event) {
        switchPOC.hideNodes(1);
    }

    @EventHandler("switch-to-2")
    public void switchTo2(final ClickEvent event) {
        switchPOC.hideNodes(2);
    }

    @EventHandler("switch-to-3")
    public void switchTo3(final ClickEvent event) {
        switchPOC.hideNodes(3);
    }

    @Override
    public void init(final DecisionNavigatorPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setupMainTree(final DecisionNavigatorTreePresenter.View mainTreeComponent) {
        mainTree.appendChild(mainTreeComponent.getElement());
    }

    @Override
    public void setupDecisionComponents(final DecisionComponents.View decisionComponentsComponent) {
        decisionComponents.appendChild(decisionComponentsComponent.getElement());
    }

    @Override
    public void showDecisionComponentsContainer() {
        show(decisionComponentsContainer);
    }

    @Override
    public void hideDecisionComponentsContainer() {
        hide(decisionComponentsContainer);
    }
}
