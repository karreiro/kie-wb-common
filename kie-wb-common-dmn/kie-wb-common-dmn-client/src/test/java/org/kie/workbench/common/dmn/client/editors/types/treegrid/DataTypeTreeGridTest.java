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

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeTreeGridTest {

    @Mock
    private DataTypeTreeGrid.View view;

    @Mock
    private ManagedInstance<DataTypeTreeGridItem> treeGridItems;

    @Mock
    private DataTypeTreeGridItem.View gridItemView;

    @Mock
    private DataTypeSelect typeSelect;

    private DataTypeTreeGrid treeGrid;

    @Before
    public void setup() {
        treeGrid = spy(new DataTypeTreeGrid(view, treeGridItems) {

            @Override
            DataTypeTreeGridItem getGridItem() {
                return new DataTypeTreeGridItem(gridItemView, typeSelect, this);
            }
        });
    }

    @Test
    public void testSetupItems() {

        final DataType dataType = makeDataType("item", "iITem");
        final List<DataTypeTreeGridItem> gridItems = new ArrayList<>();

        doReturn(gridItems).when(treeGrid).makeTreeGridItems(dataType, 1);

        treeGrid.setupItems(dataType);

        verify(view).setupGridItems(gridItems);
    }

    @Test
    public void testMakeTreeGridItems() {

        final DataType item1 = makeDataType("item1", "iITem1");
        final DataType item2 = makeDataType("item2", "iITem2");
        final DataType item = makeDataType("item", "iITem", item1, item2);

        final List<DataTypeTreeGridItem> gridItems = treeGrid.makeTreeGridItems(item, 1);
        final DataTypeTreeGridItem gridItem0 = gridItems.get(0);
        final DataTypeTreeGridItem gridItem1 = gridItems.get(1);
        final DataTypeTreeGridItem gridItem2 = gridItems.get(2);

        assertEquals(3, gridItems.size());
        assertEquals(1, gridItem0.getLevel());
        assertEquals(2, gridItem1.getLevel());
        assertEquals(2, gridItem2.getLevel());
        assertEquals(item, gridItem0.getDataType());
        assertEquals(item1, gridItem1.getDataType());
        assertEquals(item2, gridItem2.getDataType());
    }

    private DataType makeDataType(final String name,
                                  final String type,
                                  final DataType... subDataTypes) {
        final DataType dataType = mock(DataType.class);

        when(dataType.getName()).thenReturn(name);
        when(dataType.getType()).thenReturn(type);
        when(dataType.getSubDataTypes()).thenReturn(asList(subDataTypes));

        return dataType;
    }
}
