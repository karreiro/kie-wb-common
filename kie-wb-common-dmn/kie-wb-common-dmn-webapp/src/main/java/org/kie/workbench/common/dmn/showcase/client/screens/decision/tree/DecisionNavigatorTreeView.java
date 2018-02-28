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

package org.kie.workbench.common.dmn.showcase.client.screens.decision.tree;

import java.util.List;

import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.showcase.client.screens.decision.DecisionNavigatorItem;

@Templated
public class DecisionNavigatorTreeView implements DecisionNavigatorTreePresenter.View {

    @Inject
    @DataField("view")
    private HTMLDivElement view;

    @Inject
    @DataField("items")
    private HTMLDivElement items;

    @Inject
    private Elemental2DomUtil util;

    private DecisionNavigatorTreePresenter presenter;

    @Override
    public void init(final DecisionNavigatorTreePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public HTMLElement getElement() {
        return view;
    }

    @Override
    public void clean() {
        items.innerHTML = "";
    }

    @Override
    public void setup(final List<DecisionNavigatorItem> items) {

        Element ul = createUl(items);

        this.items.appendChild(ul);
    }

    private Element createUl(final List<DecisionNavigatorItem> items) {
        Element ul = DomGlobal.document.createElement("ul");

        items.forEach(i -> {
            Element li = DomGlobal.document.createElement("li");

            li.textContent = i.getLabel();
            li.appendChild(createUl(i.getChildren()));

            ul.appendChild(li);
        });

        return ul;
    }
}
