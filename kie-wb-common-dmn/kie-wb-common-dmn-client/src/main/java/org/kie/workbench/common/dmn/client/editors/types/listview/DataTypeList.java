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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeStackHash;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandler;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponent;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar;
import org.uberfire.client.mvp.UberElemental;

import static java.util.Collections.singletonList;

@ApplicationScoped
public class DataTypeList {

    private final View view;

    private final ManagedInstance<DataTypeListItem> listItems;

    private final DataTypeManager dataTypeManager;

    private final DataTypeSearchBar searchBar;

    private final DataTypeStackHash dataTypeStackHash;

    private final DNDListComponent dndListComponent;

    private final DNDDataTypesHandler dndDataTypesHandler;

    private Consumer<DataTypeListItem> onDataTypeListItemUpdate = (e) -> { /* Nothing. */ };

    private List<DataTypeListItem> items = new ArrayList<>();

    private DataTypeListItem currentEditingItem;

    @Inject
    public DataTypeList(final View view,
                        final ManagedInstance<DataTypeListItem> listItems,
                        final DataTypeManager dataTypeManager,
                        final DataTypeSearchBar searchBar,
                        final DNDListComponent dndListComponent,
                        final DataTypeStackHash dataTypeStackHash,
                        final DNDDataTypesHandler dndDataTypesHandler) {
        this.view = view;
        this.listItems = listItems;
        this.dataTypeManager = dataTypeManager;
        this.searchBar = searchBar;
        this.dndListComponent = dndListComponent;
        this.dataTypeStackHash = dataTypeStackHash;
        this.dndDataTypesHandler = dndDataTypesHandler;
    }

    @PostConstruct
    void setup() {
        view.init(this);
        dndDataTypesHandler.init(this);
        dndListComponent.setOnDropItem(getOnDropDataType());
    }

    BiConsumer<Element, Element> getOnDropDataType() {
        return dndDataTypesHandler::onDropDataType;
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public void setupItems(final List<DataType> dataTypes) {
        setupItemsView(dataTypes);
        setupViewElements();
        collapseItemsInTheFirstLevel();
    }

    private void setupItemsView(final List<DataType> dataTypes) {
        getDNDListComponent().clear();
        setListItems(makeDataTypeListItems(dataTypes));
        getDNDListComponent().refreshItemsPosition();
    }

    private void setupViewElements() {
        view.showOrHideNoCustomItemsMessage();
        view.showReadOnlyMessage(hasReadOnlyDataTypes());
    }

    List<DataTypeListItem> makeDataTypeListItems(final List<DataType> dataTypes) {
        final List<DataTypeListItem> listItems = new ArrayList<>();
        dataTypes.forEach(dt -> listItems.addAll(makeTreeListItems(dt, 1)));
        return listItems;
    }

    void refreshSubItemsFromListItem(final DataTypeListItem listItem,
                                     final List<DataType> subDataTypes) {

        final DataType dataType = listItem.getDataType();
        final int level = listItem.getLevel();
        final List<DataTypeListItem> listItems = new ArrayList<>();

        for (final DataType subDataType : subDataTypes) {
            listItems.addAll(makeTreeListItems(subDataType, level + 1));
        }

        cleanAndUnIndex(dataType);
        addNewSubItems(dataType, listItems);
        listItems.forEach(this::reIndexDataTypes);

        getItems().addAll(listItems);
    }

    private void reIndexDataTypes(final DataTypeListItem listItem) {
        dataTypeManager.from(listItem.getDataType()).withIndexedItemDefinition();
    }

    private void addNewSubItems(final DataType dataType, final List<DataTypeListItem> gridItems) {
        view.addSubItems(dataType, gridItems);
    }

    private void cleanAndUnIndex(final DataType dataType) {
        view.cleanSubTypes(dataType);
    }

    List<DataTypeListItem> makeTreeListItems(final DataType dataType,
                                             final int level) {

        final DataTypeListItem listItem = makeListItem();
        final List<DataType> subDataTypes = dataType.getSubDataTypes();
        final List<DataTypeListItem> gridItems = new ArrayList<>();

        listItem.setupDataType(dataType, level);
        gridItems.add(listItem);

        for (final DataType subDataType : subDataTypes) {
            gridItems.addAll(makeTreeListItems(subDataType, level + 1));
        }

        return gridItems;
    }

    DataTypeListItem makeListItem() {
        final DataTypeListItem listItem = listItems.get();
        listItem.init(this);
        return listItem;
    }

    void removeItem(final DataType dataType) {
        removeItem(dataType.getUUID());
        view.removeItem(dataType);
    }

    void removeItem(final String uuid) {
        getItems().removeIf(listItem -> Objects.equals(uuid, listItem.getDataType().getUUID()));
    }

    void refreshItemsByUpdatedDataTypes(final List<DataType> updateDataTypes) {
        for (final DataType dataType : updateDataTypes) {
            findItem(dataType).ifPresent(listItem -> {
                listItem.refresh();
                refreshSubItemsFromListItem(listItem, dataType.getSubDataTypes());
            });
        }
        refreshDragAndDropList();
        refreshSearchBar();
    }

    public Optional<DataTypeListItem> findItem(final DataType dataType) {
        return getItems()
                .stream()
                .filter(item -> Objects.equals(item.getDataType().getUUID(), dataType.getUUID()))
                .findFirst();
    }

    public DNDListComponent getDNDListComponent() {
        return dndListComponent;
    }

    private boolean hasReadOnlyDataTypes() {
        return getItems()
                .stream()
                .anyMatch(DataTypeListItem::isReadOnly);
    }

    public List<DataTypeListItem> getItems() {
        return items;
    }

    void setListItems(final List<DataTypeListItem> items) {
        this.items = items;
    }

    void collapseItemsInTheFirstLevel() {
        getItems()
                .stream()
                .filter(typeListItem -> typeListItem.getLevel() == 1)
                .forEach(DataTypeListItem::collapse);
    }

    void addDataType() {

        resetSearchBar();

        final DataType dataType = dataTypeManager.fromNew().get();
        final DataTypeListItem listItem = makeListItem(dataType);

        dataType.create();

        showListItems();
        listItem.refresh();
        listItem.enableEditMode();
        refreshItemsCSSAndHTMLPosition();
    }

    void insertBelow(final DataType dataType,
                     final DataType reference) {
        final DataTypeListItem listItem = makeListItem(dataType);
        view.insertBelow(listItem, reference);
        refreshItemsByUpdatedDataTypes(singletonList(listItem.getDataType()));
    }

    void insertAbove(final DataType dataType,
                     final DataType reference) {
        view.insertAbove(makeListItem(dataType), reference);
        refreshDragAndDropList();
    }

    public void showNoDataTypesFound() {
        view.showNoDataTypesFound();
    }

    public void showListItems() {
        view.showOrHideNoCustomItemsMessage();
    }

    DataTypeListItem makeListItem(final DataType dataType) {
        final List<DataTypeListItem> items = makeTreeListItems(dataType, 1);
        getItems().addAll(items);
        return items.get(0);
    }

    void expandAll() {
        if (!getSearchBar().isEnabled()) {
            getItems().forEach(DataTypeListItem::expand);
        }
    }

    public void collapseAll() {
        if (!getSearchBar().isEnabled()) {
            getItems().forEach(DataTypeListItem::collapse);
        }
    }

    DataTypeSearchBar getSearchBar() {
        return searchBar;
    }

    public void enableEditMode(final String dataTypeHash) {
        findItemByDataTypeHash(dataTypeHash).ifPresent(DataTypeListItem::enableEditMode);
    }

    public void registerDataTypeListItemUpdateCallback(final Consumer<DataTypeListItem> onDataTypeListItemUpdate) {
        this.onDataTypeListItemUpdate = onDataTypeListItemUpdate;
    }

    void insertNestedField(final String dataTypeHash) {
        findItemByDataTypeHash(dataTypeHash).ifPresent(DataTypeListItem::insertNestedField);
    }

    void fireOnDataTypeListItemUpdateCallback(final String dataTypeHash) {
        findItemByDataTypeHash(dataTypeHash).ifPresent(this::fireOnDataTypeListItemUpdateCallback);
    }

    void fireOnDataTypeListItemUpdateCallback(final DataTypeListItem listItem) {
        onDataTypeListItemUpdate.accept(listItem);
    }

    public Optional<DataTypeListItem> findItemByDataTypeHash(final String dataTypeHash) {
        return getItems()
                .stream()
                .filter(item -> Objects.equals(calculateHash(item.getDataType()), dataTypeHash))
                .findFirst();
    }

    String calculateParentHash(final DataType dataType) {
        return dataTypeStackHash.calculateParentHash(dataType);
    }

    public String calculateHash(final DataType dataType) {
        return dataTypeStackHash.calculateHash(dataType);
    }

    public void onDataTypeEditModeToggle(final @Observes DataTypeEditModeToggleEvent event) {
        resetSearchBar();
        if (event.isEditModeEnabled()) {
            if (getCurrentEditingItem() != null && getItems().contains(getCurrentEditingItem())) {
                getCurrentEditingItem().disableEditMode();
            }
            setCurrentEditingItem(event.getItem());
        } else if (Objects.equals(event.getItem(), getCurrentEditingItem())) {
            setCurrentEditingItem(null);
        }
    }

    void refreshDragAndDropList() {
        getDNDListComponent().consolidateYPosition();
        getDNDListComponent().refreshItemsPosition();
    }

    DataTypeListItem getCurrentEditingItem() {
        return currentEditingItem;
    }

    void setCurrentEditingItem(final DataTypeListItem currentEditingItem) {
        this.currentEditingItem = currentEditingItem;
    }

    void refreshItemsCSSAndHTMLPosition() {
        dndListComponent.refreshItemsCSSAndHTMLPosition();
    }

    public HTMLElement getListItems() {
        return view.getListItems();
    }

    private void resetSearchBar() {
        searchBar.reset();
    }

    private void refreshSearchBar() {
        searchBar.refresh();
    }

    public interface View extends UberElemental<DataTypeList>,
                                  IsElement {

        void showOrHideNoCustomItemsMessage();

        void addSubItems(final DataType dataType,
                         final List<DataTypeListItem> treeGridItems);

        void removeItem(final DataType dataType);

        void cleanSubTypes(final DataType dataType);

        void insertBelow(final DataTypeListItem listItem,
                         final DataType reference);

        void insertAbove(final DataTypeListItem listItem,
                         final DataType reference);

        void showNoDataTypesFound();

        void showReadOnlyMessage(final boolean show);

        HTMLElement getListItems();
    }
}
