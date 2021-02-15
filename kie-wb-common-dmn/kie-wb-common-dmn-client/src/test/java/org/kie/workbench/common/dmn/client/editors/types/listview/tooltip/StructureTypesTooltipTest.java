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

import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class StructureTypesTooltipTest extends TestCase {

    @Mock
    private StructureTypesTooltip.View view;

    @Mock
    private ItemDefinitionUtils itemDefinitionUtils;

    @Mock
    private DataTypeList dataTypeList;

    @Mock
    private DataTypeManager dataTypeManager;

    private StructureTypesTooltip presenter;

    @Before
    public void setup() {
        presenter = spy(new StructureTypesTooltip(view, itemDefinitionUtils, dataTypeList, dataTypeManager));
    }

    @Test
    public void testSetup() {
        presenter.setup();
        verify(view).init(presenter);
    }

    @Test
    public void testShow() {
        final HTMLElement refElement = mock(HTMLElement.class);
        final String typeName = "string";

        presenter.show(refElement, typeName);

        verify(view).show(refElement, typeName);
        assertEquals(typeName, presenter.getTypeName());
    }

    @Test
    public void testGetListItems() {
        final HTMLElement expected = mock(HTMLElement.class);
        when(dataTypeList.getListItems()).thenReturn(expected);

        final HTMLElement actual = presenter.getListItems();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetTypeFields() {
        final String typeName = "tPerson";
        final String[] typeFields = {"name", "age"};
        final Optional<ItemDefinition> itemDefinition = makeItemDefinition(typeName, typeFields);
        final List<String> expected = asList(typeFields);

        when(itemDefinitionUtils.findByName(typeName)).thenReturn(itemDefinition);
        final List<String> actual = presenter.getTypeFields(typeName);

        assertEquals(expected, actual);
    }

    @Test
    public void testGoToDataType() {

        final String typeName = "tPerson";
        final HTMLElement refElement = mock(HTMLElement.class);
        final DataType dataType = mock(DataType.class);
        final Optional<DataType> optDataType = Optional.of(dataType);
        final DataTypeListItem dataTypeListItem = mock(DataTypeListItem.class);
        final Optional<DataTypeListItem> optDataTypeListItem = Optional.of(dataTypeListItem);

        when(dataTypeManager.getTopLevelDataTypeWithName(typeName)).thenReturn(optDataType);
        when(dataTypeList.findItem(dataType)).thenReturn(optDataTypeListItem);

        presenter.show(refElement, typeName);
        presenter.goToDataType();

        verify(dataTypeListItem).enableShortcutsHighlight();
    }

    private Optional<ItemDefinition> makeItemDefinition(final String itemDefinitionName,
                                                        final String... itemComponents) {

        final ItemDefinition itemDefinition = new ItemDefinition();
        itemDefinition.setName(new Name(itemDefinitionName));

        for (final String itemComponent : itemComponents) {
            itemDefinition.getItemComponent().add(makeItemDefinition(itemComponent).orElse(null));
        }

        return Optional.of(itemDefinition);
    }
}
