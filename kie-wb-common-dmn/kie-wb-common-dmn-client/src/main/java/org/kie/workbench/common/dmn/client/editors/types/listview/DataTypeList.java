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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponent;
import org.uberfire.client.mvp.UberElemental;

@ApplicationScoped
public class DataTypeList {

    private final View view;

    private final ManagedInstance<DataTypeListItem> listItems;

    private final DataTypeManager dataTypeManager;

    private List<DataTypeListItem> items;

    @Inject
    public DataTypeList(final DataTypeList.View view,
                        final ManagedInstance<DataTypeListItem> listItems,
                        final DataTypeManager dataTypeManager) {
        this.view = view;
        this.listItems = listItems;
        this.dataTypeManager = dataTypeManager;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public void setupItems(final List<DataType> dataTypes) {
        setListItems(makeDataTypeListItems(dataTypes));
        setupViewItems();
        collapseItemsInTheFirstLevel();
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
        final List<DataTypeListItem> gridItems = new ArrayList<>();

        for (final DataType subDataType : subDataTypes) {
            gridItems.addAll(makeTreeListItems(subDataType, level + 1));
        }

        refreshItemsList(subDataTypes, gridItems);

        view.addSubItems(dataType, gridItems);
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

        final double t0 = DomGlobal.performance.now();
        final DataTypeListItem listItem = listItems.get();
        final double t1 = DomGlobal.performance.now();

        GWT.log("=====> " + (t1 - t0));

        listItem.init(this);
        return listItem;
    }

    void refreshItemsList(final List<DataType> subDataTypes,
                          final List<DataTypeListItem> gridItems) {

        getItems().removeIf(item -> subDataTypes.stream().anyMatch(dataType -> {
            return Objects.equals(item.getDataType().getUUID(), dataType.getUUID());
        }));

        getItems().addAll(gridItems);
    }

    void removeItem(final DataType dataType) {
        view.removeItem(dataType);
    }

    void refreshItemsByUpdatedDataTypes(final List<DataType> updateDataTypes) {
        for (final DataType dataType : updateDataTypes) {
            findItem(dataType).ifPresent(listItem -> {
                listItem.refresh();
                refreshSubItemsFromListItem(listItem, dataType.getSubDataTypes());
            });
        }
    }

    Optional<DataTypeListItem> findItem(final DataType dataType) {
        return getItems()
                .stream()
                .filter(item -> Objects.equals(item.getDataType().getUUID(), dataType.getUUID()))
                .findFirst();
    }

    void setupViewItems() {
        view.setupListItems(getItems());
    }

    List<DataTypeListItem> getItems() {
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

        final DataType dataType = dataTypeManager.fromNew().get();
        final DataTypeListItem listItem = makeListItem(dataType);

        dataType.create();

        view.addSubItem(listItem);

        listItem.enableEditMode();
    }

    public void insertBelow(final DataType dataType,
                            final DataType reference) {
        view.insertBelow(makeListItem(dataType), reference);
    }

    public void insertAbove(final DataType dataType,
                            final DataType reference) {
        view.insertAbove(makeListItem(dataType), reference);
    }

    DataTypeListItem makeListItem(final DataType dataType) {

        final DataTypeListItem listItem = makeListItem();

        listItem.setupDataType(dataType, 1);
        getItems().add(listItem);

        return listItem;
    }

    public interface View extends UberElemental<DataTypeList>,
                                  IsElement {

        void setupListItems(final List<DataTypeListItem> treeGridItems);

        void addSubItems(final DataType dataType,
                         final List<DataTypeListItem> treeGridItems);

        void addSubItem(final DataTypeListItem listItem);

        void removeItem(final DataType dataType);

        void insertBelow(final DataTypeListItem listItem,
                         final DataType reference);

        void insertAbove(final DataTypeListItem listItem,
                         final DataType reference);
    }
}
