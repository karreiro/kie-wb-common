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

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.mockito.InOrder;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeTreeGridItemTest {

    @Mock
    private DataTypeTreeGridItem.View view;

    @Mock
    private DataTypeSelect typeSelect;

    @Mock
    private DataTypeTreeGrid treeGrid;

    @Mock
    private DataType dataType;

    private DataTypeTreeGridItem gridItem;

    @Before
    public void setup() {
        gridItem = spy(new DataTypeTreeGridItem(view, typeSelect, treeGrid));
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);

        when(view.getElement()).thenReturn(expectedElement);

        HTMLElement actualElement = gridItem.getElement();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testSetupDataType() {

        final DataType expectedDataType = this.dataType;
        final int expectedLevel = 1;

        gridItem.setupDataType(expectedDataType, expectedLevel);

        final InOrder inOrder = inOrder(gridItem);
        inOrder.verify(gridItem).setupSelectComponent();
        inOrder.verify(gridItem).setupView();

        assertEquals(expectedDataType, gridItem.getDataType());
        assertEquals(expectedLevel, gridItem.getLevel());
    }

    @Test
    public void testSetupSelectComponent() {

        final DataType dataType = mock(DataType.class);
        when(gridItem.getDataType()).thenReturn(dataType);

        gridItem.setupSelectComponent();

        verify(typeSelect).init(dataType);
    }

    @Test
    public void testSetupView() {

        final DataType dataType = mock(DataType.class);
        when(gridItem.getDataType()).thenReturn(dataType);

        gridItem.setupView();

        verify(view).setupSelectComponent(typeSelect);
        verify(view).setDataType(dataType);
    }

    @Test
    public void testIsCollapsedWhenArrowIsARightArrow() {

        final DOMTokenList classList = mock(DOMTokenList.class);
        final HTMLElement arrow = mock(HTMLElement.class);
        arrow.classList = classList;
        when(classList.contains("fa-angle-right")).thenReturn(true);

        assertTrue(gridItem.isCollapsed(arrow));
    }

    @Test
    public void testIsCollapsedWhenArrowIsNotARightArrow() {

        final DOMTokenList classList = mock(DOMTokenList.class);
        final HTMLElement arrow = mock(HTMLElement.class);
        arrow.classList = classList;
        when(classList.contains("fa-angle-right")).thenReturn(false);

        assertFalse(gridItem.isCollapsed(arrow));
    }

    @Test
    public void testExpandOrCollapseSubTypesWhenSubDataTypesAreCollapsed() {

        final DOMTokenList classList = mock(DOMTokenList.class);
        final HTMLElement arrow = mock(HTMLElement.class);
        arrow.classList = classList;

        when(view.getArrow()).thenReturn(arrow);
        doReturn(dataType).when(gridItem).getDataType();
        doReturn(true).when(gridItem).isCollapsed(arrow);
        doNothing().when(gridItem).expandSubDataTypes(any());

        gridItem.expandOrCollapseSubTypes();

        verify(classList).remove("fa-angle-right");
        verify(classList).add("fa-angle-down");
        verify(gridItem).expandSubDataTypes(dataType);
    }

    @Test
    public void testExpandOrCollapseSubTypesWhenSubDataTypesAreNotCollapsed() {

        final DOMTokenList classList = mock(DOMTokenList.class);
        final HTMLElement arrow = mock(HTMLElement.class);
        arrow.classList = classList;

        when(view.getArrow()).thenReturn(arrow);
        doReturn(dataType).when(gridItem).getDataType();
        doReturn(false).when(gridItem).isCollapsed(arrow);
        doNothing().when(gridItem).collapseSubDataTypes(any());

        gridItem.expandOrCollapseSubTypes();

        verify(classList).remove("fa-angle-down");
        verify(classList).add("fa-angle-right");
        verify(gridItem).collapseSubDataTypes(dataType);
    }

    @Test
    public void testCollapseSubDataTypes() {

        final DOMTokenList classList1 = mock(DOMTokenList.class);
        final DOMTokenList classList2 = mock(DOMTokenList.class);
        final DataType subDataType1 = mock(DataType.class);
        final DataType subDataType2 = mock(DataType.class);
        final HTMLElement row1 = mock(HTMLElement.class);
        final HTMLElement row2 = mock(HTMLElement.class);

        when(dataType.getSubDataTypes()).thenReturn(asList(subDataType1, subDataType2));
        doReturn(row1).when(gridItem).getRowElement(subDataType1);
        doReturn(row2).when(gridItem).getRowElement(subDataType2);
        row1.classList = classList1;
        row2.classList = classList2;

        gridItem.collapseSubDataTypes(dataType);

        classList1.add("hidden");
        classList2.add("hidden");
        verify(gridItem).collapseSubDataTypes(subDataType1);
        verify(gridItem).collapseSubDataTypes(subDataType2);
    }

    @Test
    public void testExpandSubDataTypes() {

        final DOMTokenList classList1 = mock(DOMTokenList.class);
        final DOMTokenList classList2 = mock(DOMTokenList.class);
        final DataType subDataType1 = mock(DataType.class);
        final DataType subDataType2 = mock(DataType.class);
        final HTMLElement row1 = mock(HTMLElement.class);
        final HTMLElement row2 = mock(HTMLElement.class);
        final HTMLElement arrow1 = mock(HTMLElement.class);
        final HTMLElement arrow2 = mock(HTMLElement.class);

        when(dataType.getSubDataTypes()).thenReturn(asList(subDataType1, subDataType2));
        doReturn(row1).when(gridItem).getRowElement(subDataType1);
        doReturn(row2).when(gridItem).getRowElement(subDataType2);
        doReturn(arrow1).when(gridItem).getArrowElement(row1);
        doReturn(arrow2).when(gridItem).getArrowElement(row2);
        doReturn(true).when(gridItem).isCollapsed(arrow1);
        doReturn(false).when(gridItem).isCollapsed(arrow2);
        row1.classList = classList1;
        row2.classList = classList2;

        gridItem.expandSubDataTypes(dataType);

        classList1.remove("hidden");
        classList2.remove("hidden");
        verify(gridItem, never()).expandSubDataTypes(subDataType1);
        verify(gridItem).expandSubDataTypes(subDataType2);
    }
}
