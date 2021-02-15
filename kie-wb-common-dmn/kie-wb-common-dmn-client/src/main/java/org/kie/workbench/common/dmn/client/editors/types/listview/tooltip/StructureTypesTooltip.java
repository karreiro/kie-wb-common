/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.listview.tooltip;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.uberfire.client.mvp.UberElemental;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
public class StructureTypesTooltip {

    private final View view;

    private final ItemDefinitionUtils itemDefinitionUtils;

    private final DataTypeList dataTypeList;

    private final DataTypeManager dataTypeManager;

    private String typeName;

    @Inject
    public StructureTypesTooltip(final View view,
                                 final ItemDefinitionUtils itemDefinitionUtils,
                                 final DataTypeList dataTypeList,
                                 final DataTypeManager dataTypeManager) {
        this.view = view;
        this.itemDefinitionUtils = itemDefinitionUtils;
        this.dataTypeList = dataTypeList;
        this.dataTypeManager = dataTypeManager;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public void show(final HTMLElement refElement,
                     final String typeName) {
        this.typeName = typeName;
        view.show(refElement, typeName);
    }

    HTMLElement getListItems() {
        return dataTypeList.getListItems();
    }

    List<String> getTypeFields(final String typeName) {
        return itemDefinitionUtils
                .findByName(typeName)
                .map(this::getItemDefinitionFields)
                .orElse(Collections.emptyList());
    }

    void goToDataType() {
        dataTypeManager
                .getTopLevelDataTypeWithName(getTypeName())
                .flatMap(dataTypeList::findItem)
                .ifPresent(DataTypeListItem::enableShortcutsHighlight);
    }

    String getTypeName() {
        return typeName;
    }

    private List<String> getItemDefinitionFields(final ItemDefinition itemDefinition) {
        return itemDefinition
                .getItemComponent()
                .stream()
                .map(e -> e.getName().getValue())
                .collect(toList());
    }

    public interface View extends UberElemental<StructureTypesTooltip>,
                                  IsElement {

        HTMLDivElement getTooltip();

        void setup(final String name,
                   final List<String> fields);

        void show(HTMLElement refElement,
                  String typeName);
    }
}
