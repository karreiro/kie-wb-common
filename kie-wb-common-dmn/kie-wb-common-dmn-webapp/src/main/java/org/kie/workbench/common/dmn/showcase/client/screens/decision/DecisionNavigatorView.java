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

import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.showcase.client.screens.decision.tree.DecisionNavigatorTreePresenter;
import org.kie.workbench.common.dmn.showcase.client.screens.decision.tree.DecisionNavigatorTreeView;
import org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorer;

@Templated
public class DecisionNavigatorView implements DecisionNavigatorPresenter.View {

    @Inject
    @DataField("view")
    private HTMLDivElement view;

    @Inject
    @DataField("main-tree")
    private HTMLDivElement mainTree;

    @Inject
    private Elemental2DomUtil util;

    private DecisionNavigatorPresenter presenter;

    @Override
    public void init(final DecisionNavigatorPresenter presenter) {
        this.presenter = presenter;

        setupMainTree();
    }

    private void setupMainTree() {
        mainTree.appendChild(getTreeView().getElement());
    }

    private DecisionNavigatorTreePresenter.View getTreeView() {
        return presenter.getTreeView();
    }

    @Override
    public HTMLElement getElement() {
        return view;
    }
}
