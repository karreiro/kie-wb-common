/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop;

import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.uberfire.client.mvp.UberElemental;

public class DragAndDropComponent {

    private final int INDENTATION_SIZE = 60;

    private final View view;

    private Consumer<Item> onDropItem = (item) -> {/* Nothing. */};

    @Inject
    public DragAndDropComponent(final View view) {
        this.view = view;
    }

    @PostConstruct
    void setup() {
        view.init(this);

        registerItem(createElement(0));
        registerItem(createElement(1));
        registerItem(createElement(2));
        registerItem(createElement(3));
        registerItem(createElement(4));
        registerItem(createElement(5));
        registerItem(createElement(6));
        registerItem(createElement(7));
    }

    private HTMLElement createElement(final int n) {
        final Element div = DomGlobal.document.createElement("div");
        div.textContent = "Item" + n;
        return (HTMLElement) div;
    }

    public void registerItem(final HTMLElement htmlElement) {
        view.registerItem(htmlElement);
    }

    public HTMLElement getViewElement() {
        return view.getElement();
    }

    public void setOnDropItem(final Consumer<Item> onDropItem) {
        this.onDropItem = onDropItem;
    }

    class Item {

    }

    public interface View extends UberElemental<DragAndDropComponent>,
                                  IsElement {

        void registerItem(final HTMLElement htmlElement);
    }
}
