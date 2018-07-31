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

package org.kie.workbench.common.dmn.client.editors.types.treegrid;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.uberfire.client.mvp.UberElemental;

import static org.kie.workbench.common.dmn.client.editors.types.treegrid.common.HiddenHelper.hide;
import static org.kie.workbench.common.dmn.client.editors.types.treegrid.common.HiddenHelper.show;

public class DataTypeTreeGridItem {

    private final View view;

    private final DataTypeTreeGrid treeGrid;

    private final DataTypeSelect typeSelect;

    private DataType dataType;

    private int level;

    @Inject
    public DataTypeTreeGridItem(final View view,
                                final DataTypeSelect typeSelect,
                                final DataTypeTreeGrid treeGrid) {
        this.view = view;
        this.typeSelect = typeSelect;
        this.treeGrid = treeGrid;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public DataTypeTreeGridItem setupDataType(final DataType dataType,
                                              final int level) {
        this.dataType = dataType;
        this.level = level;

        setupSelectComponent();
        setupView();

        return this;
    }

    void setupSelectComponent() {
        typeSelect.init(getDataType());
    }

    void setupView() {
        view.setupSelectComponent(typeSelect);
        view.setDataType(getDataType());
    }

    DataType getDataType() {
        return dataType;
    }

    public int getLevel() {
        return level;
    }

    void expandOrCollapseSubTypes() {

        final HTMLElement arrow = view.getArrow();

        if (isCollapsed(arrow)) {
            CssHelper.asDownArrow(arrow);
            expandSubDataTypes(getDataType());
        } else {
            CssHelper.asRightArrow(arrow);
            collapseSubDataTypes(getDataType());
        }
    }

    void expandSubDataTypes(final DataType dataType) {
        dataType.getSubDataTypes().forEach(subType -> {

            final Element subDataTypeRow = getRowElement(subType);
            final Element arrow = getArrowElement(subDataTypeRow);

            show(subDataTypeRow);

            if (!isCollapsed(arrow)) {
                expandSubDataTypes(subType);
            }
        });
    }

    void collapseSubDataTypes(final DataType dataType) {
        dataType.getSubDataTypes().forEach(subType -> {

            final Element subDataTypeRow = getRowElement(subType);

            hide(subDataTypeRow);

            collapseSubDataTypes(subType);
        });
    }

    boolean isCollapsed(final Element arrow) {
        return CssHelper.isRightArrow(arrow);
    }

    Element getArrowElement(final Element subDataTypeRow) {
        return subDataTypeRow.querySelector("[data-field='arrow-button']");
    }

    Element getRowElement(final DataType dataType) {
        return treeGrid.getElement().querySelector("[data-row-uuid='" + dataType.getUUID() + "']");
    }

    public interface View extends UberElemental<DataTypeTreeGridItem> {

        void setDataType(final DataType dataType);

        HTMLElement getArrow();

        void setupSelectComponent(final DataTypeSelect typeSelect);
    }

    static class CssHelper {

        static void asRightArrow(final Element element) {
            element.classList.add("fa-angle-right");
            element.classList.remove("fa-angle-down");
        }

        static void asDownArrow(final Element element) {
            element.classList.remove("fa-angle-right");
            element.classList.add("fa-angle-down");
        }

        static boolean isRightArrow(final Element element) {
            return element.classList.contains("fa-angle-right");
        }
    }
}
