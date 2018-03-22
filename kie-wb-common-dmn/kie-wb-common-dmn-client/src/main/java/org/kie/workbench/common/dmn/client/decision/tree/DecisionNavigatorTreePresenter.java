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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Element;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem;
import org.uberfire.client.mvp.UberElemental;

import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.ROOT;

@Dependent
public class DecisionNavigatorTreePresenter {

    @Inject
    private View view;

    private List<DecisionNavigatorItem> items;

    private Map<String, DecisionNavigatorItem> indexedItems = new HashMap<>();

    private String activeParentUUID;

    public View getView() {
        return view;
    }

    @PostConstruct
    private void setup() {
        view.init(this);
    }

    public void setupItems(final List<DecisionNavigatorItem> items) {

        this.items = items;

        index(items);
        setupView();
    }

    private void index(final List<DecisionNavigatorItem> items) {
        items.forEach(this::index);
    }

    private void index(final DecisionNavigatorItem item) {
        indexedItems.put(item.getUUID(), item);
        index(item.getChildren());
    }

    private void setupView() {
        view.clean();
        view.setup(items);
    }

    // items not updated during the add and the remove process
    public void addOrUpdateItem(final DecisionNavigatorItem parent,
                                final DecisionNavigatorItem item) {

        index(new ArrayList<DecisionNavigatorItem>() {{
            add(parent);
            add(item);
        }});

        if (view.hasItem(item)) {
            view.update(parent, item);
        } else {
            view.addItem(parent, item);
        }
    }

    public void remove(final DecisionNavigatorItem item) {
        view.remove(item);
    }

    public void removeAllElements() {
        view.removeChildren(root());
    }

    public DecisionNavigatorItem root() {
        return items
                .stream()
                .filter(i -> i.getType() == ROOT)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public void selectItem(final String uuid) {
        view.select(uuid);
    }

    public void deselectItem() {
        view.deselect();
    }

    public List<DecisionNavigatorItem> getItems() {
        return items;
    }

    public DecisionNavigatorItem getItem(String uuid) {
        return indexedItems.get(uuid);
    }

    public void setActiveParentUUID(final String activeParentUUID) {
        this.activeParentUUID = activeParentUUID;
    }

    public DecisionNavigatorItem getActiveParent() {
        return indexedItems.get(activeParentUUID);
    }

    public interface View extends UberElemental<DecisionNavigatorTreePresenter> {

        void clean();

        void setup(final List<DecisionNavigatorItem> items);

        void addItem(final DecisionNavigatorItem parent,
                     final DecisionNavigatorItem item);

        boolean hasItem(final DecisionNavigatorItem item);

        Element findItem(DecisionNavigatorItem item);

        void update(final DecisionNavigatorItem parent, DecisionNavigatorItem item);

        void remove(DecisionNavigatorItem item);

        void removeChildren(DecisionNavigatorItem item);

        String getSelectedUUID();

        void select(String uuid);

        void deselect();
    }
}
