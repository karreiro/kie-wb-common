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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.Node;
import elemental2.dom.NodeList;
import org.jboss.aesh.cl.Option;
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

    private Element selectedElement;

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

    @Override
    public void addItem(final DecisionNavigatorItem parent,
                        final DecisionNavigatorItem item) {
        Element ul = items.querySelector("[data-uuid=\"" + parent.getUUID() + "\"] ul");

        if (ul == null) {
            return;
        }
        createLi(ul, item);
    }

    @Override
    public boolean hasItem(final DecisionNavigatorItem item) {
        return findItem(item) != null;
    }

    @Override
    public Element findItem(final DecisionNavigatorItem item) {
        return items.querySelector("[data-uuid=\"" + item.getUUID() + "\"]");
    }

    @Override
    public void update(final DecisionNavigatorItem item) {
        Element oldLi = findItem(item);
        Element newLi = createLi(item);
        Node parentNode = oldLi.parentNode;

        parentNode.replaceChild(newLi, oldLi);
    }

    @Override
    public void remove(final DecisionNavigatorItem item) {

        Element oldLi = findItem(item);
        oldLi.remove();
    }

    @Override
    public void removeChildren(DecisionNavigatorItem item) {

        Element element = items.querySelector("[data-uuid=\"" + item.getUUID() + "\"]");
        Element ul = items.querySelector("[data-uuid=\"" + item.getUUID() + "\"] ul");
        ul.remove();

        element.appendChild(DomGlobal.document.createElement("ul"));
    }

    @Override
    public void select(final DecisionNavigatorItem item) {
        if (item == null) {
            return;
        }
        Optional<Element> selectedElement = Optional.ofNullable(this.selectedElement);

        Element span = items.querySelector("[data-uuid=\"" + item.getUUID() + "\"] span");

        if (selectedElement.isPresent()) {
            selectedElement.get().classList.remove("selected");
        }

        span.classList.add("selected");
        this.selectedElement = span;
    }

    @Override
    public String getSelectedUUID() {
        try {
            return selectedElement.parentNode.attributes.get("data-uuid").value;
        } catch (Exception e) {
            return null;
        }
    }

    private Element createUl(final List<DecisionNavigatorItem> items) {
        Element ul = DomGlobal.document.createElement("ul");

        items.forEach(i -> {
            createLi(ul, i);
        });

        return ul;
    }

    private void createLi(final Element ul, final DecisionNavigatorItem i) {
        Element li = createLi(i);
        ul.appendChild(li);
    }

    private Element createLi(final DecisionNavigatorItem i) {
        Element li = DomGlobal.document.createElement("li");
        Element span = DomGlobal.document.createElement("span");

        li.setAttribute("data-uuid", i.getUUID());
        li.setAttribute("title", i.getLabel());
        li.classList.add(getCssClass(i));

        span.textContent = i.getLabel();
        li.appendChild(span);
        li.appendChild(createUl(i.getChildren()));

        span.onclick = (e) -> {
            i.onClick();
            return null;
        };

        if (i.getChildren().size() > 0) {
            li.classList.add("parent-node");
        }

        setupCollapse(li);
        return li;
    }

    private void setupCollapse(final Element li) {
        li.onclick = i -> {

            toggle(li);

            i.stopPropagation();

            return null;
        };
    }

    private Object toggle(final Element li) {

        li.classList.toggle("closed");

        return null;
    }

    private String getCssClass(final DecisionNavigatorItem i) {

        final String typeName = i.getType().name();

        return "kie-" + typeName.toLowerCase().replace('_', '-');
    }
}
