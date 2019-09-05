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

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class DragAndDropComponentView implements DragAndDropComponent.View {

    private static final String DRAGGABLE = "draggable";

    private DragAndDropComponent presenter;

    private ManagedInstance<HTMLDivElement> divElements;

    @Override
    public void init(final DragAndDropComponent presenter) {
        this.presenter = presenter;
    }

    @Override
    public void registerItem(final HTMLElement htmlElement) {
        final HTMLDivElement item = createItem(htmlElement);
        getElement().appendChild(item);
    }

    private HTMLDivElement createItem(final HTMLElement htmlElement) {

        final HTMLDivElement item = divElements.get(); // Measure performance: DomGlobal.document.createElement("div");

        item.appendChild(htmlElement);
        item.classList.add(DRAGGABLE);

        return item;
    }
}
