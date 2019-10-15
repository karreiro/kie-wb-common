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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import org.kie.workbench.common.dmn.api.definition.model.ConstraintType;
import org.kie.workbench.common.dmn.client.editors.types.DataTypeChangedEvent;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.confirmation.DataTypeConfirmation;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraint;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.validation.DataTypeNameFormatValidator;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.BOOLEAN;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.CONTEXT;
import static org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType.ABOVE;
import static org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType.BELOW;
import static org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType.NESTED;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Dependent
public class DataTypeListItem {

    private final View view;

    private final DataTypeSelect dataTypeSelectComponent;

    private final DataTypeConstraint dataTypeConstraintComponent;

    private final SmallSwitchComponent dataTypeListComponent;

    private final DataTypeManager dataTypeManager;

    private final DataTypeConfirmation confirmation;

    private final Event<DataTypeEditModeToggleEvent> editModeToggleEvent;

    private final DataTypeNameFormatValidator nameFormatValidator;

    private final Event<DataTypeChangedEvent> dataTypeChangedEvent;

    private DataType dataType;

    private int level;

    private DataTypeList dataTypeList;

    private String oldName;

    private String oldType;

    private String oldConstraint;

    private boolean oldIsList;

    private ConstraintType oldConstraintType;

    private String[] canNotHaveConstraintTypes;

    private HTMLElement dragAndDropElement;

    @Inject
    public DataTypeListItem(final View view,
                            final DataTypeSelect dataTypeSelectComponent,
                            final DataTypeConstraint dataTypeConstraintComponent,
                            final SmallSwitchComponent dataTypeListComponent,
                            final DataTypeManager dataTypeManager,
                            final DataTypeConfirmation confirmation,
                            final DataTypeNameFormatValidator nameFormatValidator,
                            final Event<DataTypeEditModeToggleEvent> editModeToggleEvent,
                            final Event<DataTypeChangedEvent> dataTypeChangedEvent) {
        this.view = view;
        this.dataTypeSelectComponent = dataTypeSelectComponent;
        this.dataTypeConstraintComponent = dataTypeConstraintComponent;
        this.dataTypeListComponent = dataTypeListComponent;
        this.dataTypeManager = dataTypeManager;
        this.confirmation = confirmation;
        this.nameFormatValidator = nameFormatValidator;
        this.editModeToggleEvent = editModeToggleEvent;
        this.dataTypeChangedEvent = dataTypeChangedEvent;
        this.dataTypeListComponent.setOnValueChanged(value -> refreshConstraintComponent());
    }

    @PostConstruct
    void setup() {
        view.init(this);
        canNotHaveConstraintTypes = new String[]{
                BOOLEAN.getName(),
                dataTypeManager.structure(),
                CONTEXT.getName()
        };
    }

    public void init(final DataTypeList dataTypeList) {
        this.dataTypeList = dataTypeList;
    }

    void setupDataType(final DataType dataType,
                       final int level) {

        this.dataType = dataType;
        this.level = level;

        setupDragAndDropComponent();
        setupSelectComponent();
        setupListComponent();
        setupConstraintComponent();
        setupView();
    }

    private void setupDragAndDropComponent() {

        final DNDListComponent dragAndDropComponent = dataTypeList.getDndListComponent();
        final HTMLElement dragAndDropElement = dragAndDropComponent.registerNewItem(getContentElement());

        this.dragAndDropElement = dragAndDropElement;
    }

    void setupListComponent() {
        dataTypeListComponent.setValue(getDataType().isList());
        refreshListYesLabel();
    }

    void setupConstraintComponent() {
        dataTypeConstraintComponent.init(this);
        refreshConstraintComponent();
    }

    void setupSelectComponent() {
        dataTypeSelectComponent.init(this, getDataType());
    }

    void setupView() {
        view.setupSelectComponent(dataTypeSelectComponent);
        view.setupConstraintComponent(dataTypeConstraintComponent);
        view.setupListComponent(dataTypeListComponent);
        setupDataType();
    }

    public void setupDataType() {
        view.setDataType(getDataType());
    }

    public HTMLElement getDragAndDropElement() {
        return dragAndDropElement;
    }

    public HTMLElement getContentElement() {
        return view.getElement();
    }

    HTMLElement getListElement() {
        return dataTypeList.getDndListComponent().getElement();
    }

    void refresh() {
        dataTypeSelectComponent.refresh();
        dataTypeSelectComponent.init(this, getDataType());
        dataTypeConstraintComponent.refreshView();
        view.setName(getDataType().getName());
        setupListComponent();
        setupConstraintComponent();
    }

    public DataType getDataType() {
        return dataType;
    }

    public boolean isReadOnly() {
        return getDataType().isReadOnly();
    }

    public int getLevel() {
        return level;
    }

    public String[] getCanNotHaveConstraintTypes() {
        return canNotHaveConstraintTypes;
    }

    void expandOrCollapseSubTypes() {
        if (isCollapsed()) {
            expand();
        } else {
            collapse();
        }
    }

    boolean isCollapsed() {
        return view.isCollapsed();
    }

    public void expand() {
        view.expand();
    }

    public void collapse() {
        view.collapse();
    }

    void refreshSubItems(final List<DataType> dataTypes) {

        dataTypeList.refreshSubItemsFromListItem(this, dataTypes);

        dataTypeList.getDndListComponent().consolidateHierarchicalLevel2();
        dataTypeList.getDndListComponent().refreshItemsPosition();

        view.enableFocusMode();
        view.toggleArrow(!dataTypes.isEmpty());
    }

    public void enableEditMode() {

        if (view.isOnFocusMode()) {
            return;
        }

        oldName = getDataType().getName();
        oldType = getDataType().getType();
        oldConstraint = getDataType().getConstraint();
        oldIsList = getDataType().isList();
        oldConstraintType = getDataType().getConstraintType();

        view.showSaveButton();
        view.showDataTypeNameInput();
        view.showListContainer();
        view.hideKebabMenu();
        view.hideListYesLabel();
        view.enableFocusMode();

        dataTypeSelectComponent.enableEditMode();
        dataTypeConstraintComponent.enableEditMode();

        editModeToggleEvent.fire(new DataTypeEditModeToggleEvent(true, this));
    }

    public void disableEditMode() {
        if (view.isOnFocusMode()) {
            discardNewDataType();
            closeEditMode();
        }
    }

    public void saveAndCloseEditMode() {

        final DataType updatedDataType = updateProperties(getDataType());

        if (updatedDataType.isValid()) {
            confirmation.ifDataTypeDoesNotHaveLostSubDataTypes(updatedDataType, doValidateDataTypeNameAndSave(updatedDataType), doDisableEditMode());
        } else {
            discardDataTypeProperties();
        }
    }

    Command doDisableEditMode() {
        return this::disableEditMode;
    }

    Command doValidateDataTypeNameAndSave(final DataType dataType) {
        return () -> nameFormatValidator.ifIsValid(dataType, doSaveAndCloseEditMode(dataType));
    }

    Command doSaveAndCloseEditMode(final DataType dataType) {
        return () -> {
            final String referenceDataTypeHash = dataTypeList.calculateParentHash(dataType);
            dataTypeList.refreshItemsByUpdatedDataTypes(persist(dataType));
            closeEditMode();

            final String newDataTypeHash = getNewDataTypeHash(dataType, referenceDataTypeHash);
            dataTypeList.fireOnDataTypeListItemUpdateCallback(newDataTypeHash);
            insertNewFieldIfDataTypeIsStructure(newDataTypeHash);
            fireDataChangedEvent();
        };
    }

    void fireDataChangedEvent() {
        dataTypeChangedEvent.fire(new DataTypeChangedEvent());
    }

    void insertNewFieldIfDataTypeIsStructure(final String hash) {

        final Optional<DataTypeListItem> updatedItem = dataTypeList.findItemByDataTypeHash(hash);

        updatedItem.ifPresent(item -> {
            if (item.isStructureType() && !item.getDataType().hasSubDataTypes()) {
                dataTypeList.insertNestedField(hash);
            }
        });
    }

    List<DataType> persist(final DataType dataType) {
        return dataTypeManager
                .from(dataType)
                .withSubDataTypes(dataTypeSelectComponent.getSubDataTypes())
                .get()
                .update();
    }

    void discardNewDataType() {

        final DataType oldDataType = discardDataTypeProperties();

        view.setDataType(oldDataType);

        setupListComponent();
        setupSelectComponent();
        setupConstraintComponent();
        refreshSubItems(oldDataType.getSubDataTypes());
    }

    DataType discardDataTypeProperties() {
        return dataTypeManager
                .withDataType(getDataType())
                .withName(getOldName())
                .withType(getOldType())
                .withConstraint(getOldConstraint())
                .withConstraintType(getOldConstraintType())
                .asList(getOldIsList())
                .get();
    }

    void closeEditMode() {

        view.hideDataTypeNameInput();
        view.hideListContainer();
        view.showEditButton();
        view.showKebabMenu();
        view.disableFocusMode();

        refreshListYesLabel();

        dataTypeSelectComponent.disableEditMode();
        dataTypeConstraintComponent.disableEditMode();

        editModeToggleEvent.fire(new DataTypeEditModeToggleEvent(false, this));
    }

    void refreshListYesLabel() {
        if (getDataType().isList()) {
            view.showListYesLabel();
        } else {
            view.hideListYesLabel();
        }
    }

    public void remove() {
        confirmation.ifIsNotReferencedDataType(getDataType(), destroy());
    }

    public Command destroy() {
        return () -> {

            final List<DataType> destroyedDataTypes = getDataType().destroy();
            final List<DataType> removedDataTypes = removeTopLevelDataTypes(destroyedDataTypes);

            destroyedDataTypes.removeAll(removedDataTypes);

            dataTypeList.refreshItemsByUpdatedDataTypes(destroyedDataTypes);

            fireDataChangedEvent();
        };
    }

    List<DataType> removeTopLevelDataTypes(final List<DataType> destroyedDataTypes) {
        return destroyedDataTypes.stream()
                .filter(dataType -> dataType.isTopLevel() && (isDestroyedDataType(dataType) || isAReferenceToDestroyedDataType(dataType)))
                .peek(dataTypeList::removeItem)
                .collect(Collectors.toList());
    }

    private boolean isDestroyedDataType(final DataType dataType) {
        return Objects.equals(dataType.getUUID(), getDataType().getUUID());
    }

    private boolean isAReferenceToDestroyedDataType(final DataType dataType) {
        return getDataType().isTopLevel() && Objects.equals(dataType.getType(), getDataType().getName());
    }

    DataType updateProperties(final DataType dataType) {
        return dataTypeManager
                .from(dataType)
                .withName(getName())
                .withType(getType())
                .withConstraint(getConstraint())
                .withConstraintType(getConstraintType())
                .asList(isList())
                .get();
    }

    private String getConstraintType() {
        final ConstraintType constraint = dataTypeConstraintComponent.getConstraintType();
        if (constraint == null) {
            return "";
        }
        return constraint.value();
    }

    private String getName() {
        return view.getName();
    }

    public String getType() {
        return dataTypeSelectComponent.getValue();
    }

    private String getConstraint() {
        return dataTypeConstraintComponent.getValue();
    }

    private boolean isList() {
        return dataTypeListComponent.getValue();
    }

    String getOldName() {
        return oldName;
    }

    String getOldType() {
        return oldType;
    }

    String getOldConstraint() {
        return oldConstraint;
    }

    String getOldConstraintType() {
        if (oldConstraintType == null) {
            return "";
        }
        return oldConstraintType.value();
    }

    boolean getOldIsList() {
        return oldIsList;
    }

    DataTypeList getDataTypeList() {
        return dataTypeList;
    }

    public void insertFieldAbove() {

        closeEditMode();

        final DataType newDataType = newDataType();
        final String referenceDataTypeHash = dataTypeList.calculateParentHash(getDataType());
        final List<DataType> updatedDataTypes = newDataType.create(getDataType(), ABOVE);

        if (newDataType.isTopLevel()) {
            dataTypeList.insertAbove(newDataType, getDataType());
        } else {
            dataTypeList.refreshItemsByUpdatedDataTypes(updatedDataTypes);
        }

        dataTypeList.getDndListComponent().refreshItemsPositionAndHTML();

        enableEditModeAndUpdateCallbacks(getNewDataTypeHash(newDataType, referenceDataTypeHash));
    }

    public void insertFieldAbove(final DataType dataType) {

        final String referenceDataTypeHash = dataTypeList.calculateParentHash(getDataType());
        final List<DataType> updatedDataTypes = dataType.create(getDataType(), ABOVE);

        if (dataType.isTopLevel()) {
            dataTypeList.insertAbove(dataType, getDataType());
        } else {
            dataTypeList.refreshItemsByUpdatedDataTypes(updatedDataTypes);
        }

        String newDataTypeHash = getNewDataTypeHash(dataType, referenceDataTypeHash);
        dataTypeList.findItemByDataTypeHash(newDataTypeHash).ifPresent(e -> e.expand());
    }

    public void insertFieldBelow() {

        closeEditMode();

        final DataType newDataType = newDataType();
        final String referenceDataTypeHash = dataTypeList.calculateParentHash(getDataType());
        final List<DataType> updatedDataTypes = newDataType.create(getDataType(), BELOW);

        if (newDataType.isTopLevel()) {
            dataTypeList.insertBelow(newDataType, getDataType());
        } else {
            dataTypeList.refreshItemsByUpdatedDataTypes(updatedDataTypes);
        }

        dataTypeList.getDndListComponent().refreshItemsPositionAndHTML();

        enableEditModeAndUpdateCallbacks(getNewDataTypeHash(newDataType, referenceDataTypeHash));
    }

    public void insertFieldBelow(final DataType dataType) {

        final List<DataType> updatedDataTypes = dataType.create(getDataType(), BELOW);

        if (dataType.isTopLevel()) {
            dataTypeList.insertBelow(dataType, getDataType());
        } else {
            dataTypeList.refreshItemsByUpdatedDataTypes(updatedDataTypes);
        }
    }

    public void insertNestedField() {

        closeEditMode();
        expand();

        final DataType newDataType = newDataType();
        final String referenceDataTypeHash = dataTypeList.calculateHash(getDataType());
        final List<DataType> updatedDataTypes = newDataType.create(getDataType(), NESTED);

        dataTypeList.refreshItemsByUpdatedDataTypes(updatedDataTypes);

        enableEditModeAndUpdateCallbacks(getNewDataTypeHash(newDataType, referenceDataTypeHash));
    }

    public void insertNestedField(final DataType dataType) {

        closeEditMode();
        expand();

        final List<DataType> updatedDataTypes = dataType.create(getDataType(), NESTED);
        dataTypeList.refreshItemsByUpdatedDataTypes(updatedDataTypes.stream().distinct().collect(Collectors.toList()));
    }

    void enableEditModeAndUpdateCallbacks(final String dataTypeHash) {
        dataTypeList.enableEditMode(dataTypeHash);
        dataTypeList.fireOnDataTypeListItemUpdateCallback(dataTypeHash);
    }

    String getNewDataTypeHash(final DataType newDataType,
                              final String referenceDataTypeHash) {
        return Stream
                .of(referenceDataTypeHash, newDataType.getName())
                .filter(s -> !isEmpty(s))
                .collect(Collectors.joining("."));
    }

    private DataType newDataType() {
        return dataTypeManager.fromNew().get();
    }

    void refreshConstraintComponent() {
        if (canNotHaveConstraint()) {
            dataTypeConstraintComponent.disable();
        } else {
            dataTypeConstraintComponent.enable();
        }
    }

    private boolean canNotHaveConstraint() {
        if (isList()) {
            return true;
        }

        if (Stream.of(getCanNotHaveConstraintTypes()).anyMatch(type -> Objects.equals(type, getType()))) {
            return true;
        }

        if (isIndirectCanNotHaveConstraintType()) {
            return true;
        }

        return false;
    }

    boolean isStructureType() {
        return Objects.equals(dataTypeManager.structure(), getType());
    }

    boolean isIndirectCanNotHaveConstraintType() {
        final String currentValue = dataTypeSelectComponent.getValue();
        return isIndirectTypeOf(currentValue, getCanNotHaveConstraintTypes());
    }

    private boolean isIndirectTypeOf(final String currentValue, final String... types) {
        final List<DataType> customDataTypes = dataTypeSelectComponent.getCustomDataTypes();
        final Optional<DataType> customType = customDataTypes.stream()
                .filter(d -> d.getName().equals(currentValue))
                .findFirst();

        if (customType.isPresent()) {
            final String type = customType.get().getType();
            return Arrays.stream(types).anyMatch(t -> t.equals(type)) || isIndirectTypeOf(type, types);
        }

        return false;
    }

    public void refreshDNDPosition() {
        dataTypeList.refreshDNDPosition();
    }

    public void setPositionX(final Element element,
                             final double positionX) {
        dataTypeList.getDndListComponent().setPositionX(element, positionX);
    }

    public void setPositionY(final Element element,
                             final double positionY) {
        dataTypeList.getDndListComponent().setPositionY(element, positionY);
    }

    public int getPositionY(final Element element) {
        return dataTypeList.getDndListComponent().getPositionY(element);
    }

    public interface View extends UberElemental<DataTypeListItem> {

        void setDataType(final DataType dataType);

        void toggleArrow(final boolean show);

        void expand();

        void collapse();

        void showEditButton();

        void showSaveButton();

        void setupSelectComponent(final DataTypeSelect typeSelect);

        void setupConstraintComponent(final DataTypeConstraint dataTypeConstraintComponent);

        void setupListComponent(final SmallSwitchComponent dataTypeListComponent);

        void showListContainer();

        void hideListContainer();

        void showListYesLabel();

        void hideListYesLabel();

        boolean isCollapsed();

        void hideDataTypeNameInput();

        void showDataTypeNameInput();

        void enableFocusMode();

        void disableFocusMode();

        boolean isOnFocusMode();

        String getName();

        void setName(final String name);

        void hideKebabMenu();

        void showKebabMenu();
    }
}
