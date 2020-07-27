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

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter;

import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.hide;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.show;

@Templated
public class DecisionNavigatorView implements DecisionNavigatorPresenter.View {

    @DataField("switch-drds")
    private final HTMLDivElement switchDRDs;

    @DataField("main-tree")
    private final HTMLDivElement mainTree;

    @DataField("decision-components-container")
    private final HTMLDivElement decisionComponentsContainer;

    @DataField("decision-components")
    private final HTMLDivElement decisionComponents;

    private final GraphDRDSwitchPOC switchPOC;

    private DecisionNavigatorPresenter presenter;

    @Inject
    public DecisionNavigatorView(final HTMLDivElement switchDRDs,
                                 final HTMLDivElement mainTree,
                                 final HTMLDivElement decisionComponentsContainer,
                                 final HTMLDivElement decisionComponents,
                                 final GraphDRDSwitchPOC switchPOC) {
        this.switchDRDs = switchDRDs;
        this.mainTree = mainTree;
        this.decisionComponentsContainer = decisionComponentsContainer;
        this.decisionComponents = decisionComponents;
        this.switchPOC = switchPOC;
    }

    @Override
    public void init(final DecisionNavigatorPresenter presenter) {
        this.presenter = presenter;

        for (final DMNDiagramElement drd : switchPOC.getDRDs()) {
            final Element button = DomGlobal.document.createElement("button");

            button.classList.add("button-poc");
            button.textContent = drd.getName().getValue();
            button.onclick = e -> {
                DomGlobal.console.log("[MULTIPLE DRDS - WORK IN PROGRESS] Show " + drd.getName().getValue());
                switchPOC.show(drd);
                return false;
            };

            switchDRDs.appendChild(button);
        }
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
