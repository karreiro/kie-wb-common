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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem;
import org.uberfire.client.mvp.UberElemental;

import static java.util.Arrays.asList;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.ROOT;

@Dependent
public class DecisionNavigatorTreePresenter {

    private final View view;

    private final Map<String, DecisionNavigatorItem> indexedItems = new HashMap<>();

    private String activeParentUUID;

    @Inject
    public DecisionNavigatorTreePresenter(final View view) {
        this.view = view;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public View getView() {
        return view;
    }

    public void setupItems(final List<DecisionNavigatorItem> items) {
        setup(items);
    }

    public void addOrUpdateItem(final DecisionNavigatorItem parent,
                                final DecisionNavigatorItem item) {

        index(asList(parent, item));

        if (view.hasItem(item)) {
            view.update(item);
        } else {
            view.addItem(parent, item);
        }
    }

    public void remove(final DecisionNavigatorItem item) {
        view.remove(item);
    }

    public void removeAllItems() {
        final DecisionNavigatorItem root = findRoot();

        view.removeChildren(root);
        getIndexedItems().clear();
        index(root);
    }

    public DecisionNavigatorItem getActiveParent() {
        return getIndexedItems().get(activeParentUUID);
    }

    public void selectItem(final String uuid) {
        view.select(uuid);
    }

    public void deselectItem() {
        view.deselect();
    }

    public void setActiveParentUUID(final String activeParentUUID) {
        this.activeParentUUID = activeParentUUID;
    }

    void index(final List<DecisionNavigatorItem> items) {
        items.forEach(this::index);
    }

    void index(final DecisionNavigatorItem item) {
        getIndexedItems().put(item.getUUID(), item);
        index(item.getChildren());
    }

    DecisionNavigatorItem findRoot() {
        return getIndexedItems()
                .values()
                .stream()
                .filter(i -> i.getType() == ROOT)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    Map<String, DecisionNavigatorItem> getIndexedItems() {
        return indexedItems;
    }

    private void setup(final List<DecisionNavigatorItem> items) {
        index(items);
        view.clean();
        view.setup(items);
    }

    public interface View extends UberElemental<DecisionNavigatorTreePresenter> {

        void clean();

        void setup(final List<DecisionNavigatorItem> items);

        void addItem(final DecisionNavigatorItem parent,
                     final DecisionNavigatorItem item);

        boolean hasItem(final DecisionNavigatorItem item);

        void update(final DecisionNavigatorItem item);

        void remove(final DecisionNavigatorItem item);

        void removeChildren(final DecisionNavigatorItem item);

        void select(final String uuid);

        void deselect();
    }
}
