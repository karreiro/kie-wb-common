/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import elemental2.dom.NodeList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.DATA_X_POSITION;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.DATA_Y_POSITION;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.DRAGGABLE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DNDListComponentViewTest {

    @Mock
    private HTMLDivElement dragArea;

    @Mock
    private DNDListComponent presenter;

    @Mock
    private HTMLElement htmlElement;

    private DNDListComponentView view;

    @Before
    public void setup() {
        view = spy(new DNDListComponentView(dragArea));
        view.init(presenter);
    }

    @Test
    public void testInit() {
        // init is called by @Before
        verify(view).setupDragAreaHandlers();
    }

    @Test
    public void testRegisterItem() {

        final HTMLElement expectedItem = mock(HTMLElement.class);

        doReturn(expectedItem).when(view).createItem(htmlElement);
        doReturn(2).when(view).getMaxPositionY();

        final HTMLElement actualItem = view.registerItem(htmlElement);

        verify(actualItem).setAttribute(DATA_Y_POSITION, 3);
        verify(actualItem).setAttribute(DATA_X_POSITION, 0);
        verify(dragArea).appendChild(actualItem);
        assertEquals(expectedItem, actualItem);
    }

    @Test
    public void testGetMaxPositionY() {

        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);

        when(element0.getAttribute(DATA_Y_POSITION)).thenReturn("2");
        when(element1.getAttribute(DATA_Y_POSITION)).thenReturn("4");
        when(element2.getAttribute(DATA_Y_POSITION)).thenReturn("8");

        mockDragAreaWithChildren(element0, element1, element2);

        final int expectedMaxPosition = 8;
        final int actualMaxPosition = view.getMaxPositionY();

        assertEquals(expectedMaxPosition, actualMaxPosition);
    }

    @Test
    public void testRefreshItemsPosition() {

        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);

        element0.style = mock(CSSStyleDeclaration.class);
        element1.style = mock(CSSStyleDeclaration.class);
        element2.style = mock(CSSStyleDeclaration.class);

        dragArea.style = mock(CSSStyleDeclaration.class);

        when(presenter.getItemHeight()).thenReturn(50);
        when(presenter.getIndentationSize()).thenReturn(75);

        when(element0.getAttribute(DATA_Y_POSITION)).thenReturn("0");
        when(element1.getAttribute(DATA_Y_POSITION)).thenReturn("1");
        when(element2.getAttribute(DATA_Y_POSITION)).thenReturn("2");

        when(element0.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(element1.getAttribute(DATA_X_POSITION)).thenReturn("1");
        when(element2.getAttribute(DATA_X_POSITION)).thenReturn("1");

        mockDragAreaWithChildren(element0, element1, element2);

        view.refreshItemsPosition();

        verify(element0.style).setProperty("top", "0px");
        verify(element1.style).setProperty("top", "50px");
        verify(element2.style).setProperty("top", "100px");

        verify(element0.style).setProperty("width", "calc(100% - 0px)");
        verify(element1.style).setProperty("width", "calc(100% - 75px)");
        verify(element2.style).setProperty("width", "calc(100% - 75px)");

        verify(dragArea.style).setProperty("height", "151px");
    }

    @Test
    public void testRefreshItemsHTML() {

        final HTMLElement element = mock(HTMLElement.class);
        dragArea.firstChild = element;

        mockDragAreaWithChildren(element);
        when(dragArea.removeChild(element)).then(a -> {
            dragArea.firstChild = null;
            return element;
        });

        view.refreshItemsHTML();

        verify(dragArea).removeChild(element);
        verify(dragArea).appendChild(element);
    }

    @Test
    public void testConsolidateHierarchicalLevel() {

        final HTMLElement element0 = mock(HTMLElement.class);
        final HTMLElement element1 = mock(HTMLElement.class);
        final HTMLElement element2 = mock(HTMLElement.class);

        element0.style = mock(CSSStyleDeclaration.class);
        element1.style = mock(CSSStyleDeclaration.class);
        element2.style = mock(CSSStyleDeclaration.class);

        dragArea.style = mock(CSSStyleDeclaration.class);

        when(element0.getAttribute(DATA_Y_POSITION)).thenReturn("0");
        when(element1.getAttribute(DATA_Y_POSITION)).thenReturn("2");
        when(element2.getAttribute(DATA_Y_POSITION)).thenReturn("1");

        when(element0.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(element1.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(element2.getAttribute(DATA_X_POSITION)).thenReturn("3");

        when(dragArea.querySelector(".kie-dnd-draggable[data-y-position=\"0\"]")).thenReturn(element0);
        when(dragArea.querySelector(".kie-dnd-draggable[data-y-position=\"2\"]")).thenReturn(element1);
        when(dragArea.querySelector(".kie-dnd-draggable[data-y-position=\"1\"]")).thenReturn(element2);

        mockDragAreaWithChildren(element0, element1, element2);

        view.consolidateHierarchicalLevel();

        verify(element0).setAttribute(DATA_Y_POSITION, 0);
        verify(element2, times(2)).setAttribute(DATA_Y_POSITION, 1);
        verify(element1).setAttribute(DATA_Y_POSITION, 2);

        verify(element0).setAttribute(DATA_X_POSITION, 0);
        verify(element2).setAttribute(DATA_X_POSITION, 1);
        verify(element1, never()).setAttribute(anyString(), anyString());
    }

    @Test
    public void testConsolidateHierarchicalLevelWhenListHasOneInvalidItem() {

        final HTMLElement element = mock(HTMLElement.class);

        element.style = mock(CSSStyleDeclaration.class);
        dragArea.style = mock(CSSStyleDeclaration.class);

        when(element.getAttribute(DATA_Y_POSITION)).thenReturn("0");
        when(element.getAttribute(DATA_X_POSITION)).thenReturn("9");

        mockDragAreaWithChildren(element);

        view.consolidateHierarchicalLevel();

        verify(element).setAttribute(DATA_X_POSITION, 0);
    }

    @Test
    public void testConsolidateHierarchicalLevelWhenListHasOneValidItem() {

    }

    @Test
    public void testCreateItem() {

        final HTMLDocument document = mock(HTMLDocument.class);
        final HTMLElement expectedItem = mock(HTMLElement.class);
        final HTMLElement grip = mock(HTMLElement.class);
        final HTMLElement i0 = mock(HTMLElement.class);
        final HTMLElement i1 = mock(HTMLElement.class);

        DNDListDOMHelper.Factory.DOCUMENT = document;
        expectedItem.classList = mock(DOMTokenList.class);
        grip.classList = mock(DOMTokenList.class);
        i0.classList = mock(DOMTokenList.class);
        i1.classList = mock(DOMTokenList.class);
        when(document.createElement("div")).thenReturn(expectedItem, grip);
        when(document.createElement("i")).thenReturn(i0, i1);

        final HTMLElement actualItem = view.createItem(htmlElement);

        verify(actualItem).appendChild(grip);
        verify(actualItem).appendChild(htmlElement);
        verify(actualItem.classList).add(DRAGGABLE);

        assertEquals(expectedItem, actualItem);
    }

    @Test
    public void testSetupDragAreaHandlers() {

        final Event event = mock(Event.class);

        doNothing().when(view).onStartDrag(any());
        doNothing().when(view).onDrag(any());
        doNothing().when(view).onDrop();

        dragArea.onmousedown.onInvoke(event);
        dragArea.onmousemove.onInvoke(event);
        dragArea.onmouseup.onInvoke(event);
        dragArea.onmouseout.onInvoke(event);

        final InOrder inOrder = Mockito.inOrder(view);

        inOrder.verify(view).onStartDrag(event);
        inOrder.verify(view).onDrag(event);
        inOrder.verify(view, times(2)).onDrop();
    }

    private void mockDragAreaWithChildren(final HTMLElement... children) {

        final NodeList<Element> nodeList = spy(new NodeList<>());

        nodeList.length = children.length;
        for (int i = 0; i < children.length; i++) {
            doReturn(children[i]).when(nodeList).getAt(i);
        }

        when(dragArea.querySelectorAll(any())).thenReturn(nodeList);
    }
}
