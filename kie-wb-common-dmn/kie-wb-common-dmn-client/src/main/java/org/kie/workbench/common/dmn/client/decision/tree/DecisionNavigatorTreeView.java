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

package org.kie.workbench.common.dmn.client.decision.tree;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import elemental2.dom.Node;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem;

@Templated
public class DecisionNavigatorTreeView implements DecisionNavigatorTreePresenter.View {

    @Inject
    @DataField("view")
    private HTMLDivElement view;

    @Inject
    private ManagedInstance<TreeItem> managedInstance;

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
        Element li = items.querySelector("[data-uuid=\"" + parent.getUUID() + "\"]");
        Element ul = items.querySelector("[data-uuid=\"" + parent.getUUID() + "\"] ul");

        if (ul == null || li == null) {
            return;
        }

        li.classList.add("parent-node");

        ul.appendChild(createLi(item));
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
    public void update(final DecisionNavigatorItem parent,
                       final DecisionNavigatorItem item) {

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
    public void select(final String uuid) {

        final Element newElement = items.querySelector("[data-uuid=\"" + uuid + "\"] div");
        final Element oldElement = selectedElement;

        deselect(oldElement);
        select(newElement);

        selectedElement = newElement;
    }

    @Override
    public void deselect() {
        deselect(selectedElement);
    }

    private void select(final Element element) {
        Optional.ofNullable(element).ifPresent(e -> e.classList.add("selected"));
    }

    private void deselect(final Element element) {
        Optional.ofNullable(element).ifPresent(e -> e.classList.remove("selected"));
    }

    @Override
    public String getSelectedUUID() {
        try {
            return selectedElement.parentNode.attributes.get("data-uuid").value;
        } catch (Exception e) {
            GWT.log("GLUP");
            return null;
        }
    }

    private Element createUl(final List<DecisionNavigatorItem> items) {

        final Element ul = DomGlobal.document.createElement("ul");

        items.forEach(i -> {
            ul.appendChild(createLi(i));
        });

        return ul;
    }

    private Element createLi(final DecisionNavigatorItem item) {

        final Element children = createUl(item.getChildren());
        final TreeItem setup = managedInstance.get().setup(item, children);

        return util.asHTMLElement(setup.getElement());
    }

    @Templated("DecisionNavigatorTreeView.html#item")
    public static class TreeItem implements IsElement {

        @Inject
        @DataField("text")
        private HTMLDivElement text;

        @Inject
        @Named("span")
        @DataField("icon")
        private HTMLElement icon;

        @Inject
        @DataField("sub-items")
        private HTMLUListElement subItems;

        private DecisionNavigatorItem item;

        @EventHandler("icon")
        public void onIconClick(final ClickEvent event) {
            toggle();
            event.stopPropagation();
        }

        @EventHandler("text")
        public void onTextClick(final ClickEvent event) {
            item.onClick();
        }

        public TreeItem setup(final DecisionNavigatorItem item,
                              final Element children) {

            this.item = item;

            updateDataUUID();
            updateTitle();
            updateCSSClass();
            updateLabel();
            updateSubItems(children);

            return this;
        }

        private void updateDataUUID() {
            getElement().setAttribute("data-uuid", item.getUUID());
        }

        private void updateTitle() {
            getElement().setAttribute("title", item.getLabel());
        }

        private void updateCSSClass() {
            getElement().getClassList().add(getCSSClass(item));

            if (item.getChildren().size() > 0) {
                getElement().getClassList().add("parent-node");
            }
        }

        private void updateLabel() {
            text.appendChild(DomGlobal.document.createTextNode(item.getLabel()));
        }

        private void updateSubItems(final Element children) {
            subItems.parentNode.replaceChild(children, subItems);
        }

        private void toggle() {
            getElement().getClassList().toggle("closed");
        }

        private String getCSSClass(final DecisionNavigatorItem i) {

            final String typeName = i.getType().name();

            return "kie-" + typeName.toLowerCase().replace('_', '-');
        }
    }
}
