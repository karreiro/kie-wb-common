/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.gwtbootstrap3.extras.notify.client.ui.NotifySettings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.presenters.AbstractCanvasHandlerViewerTest;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasFocusedShapeEvent;
import org.mockito.Mock;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(NotifySettings.class)
public class SessionPresenterViewTest extends AbstractCanvasHandlerViewerTest {

    @Mock
    private ContextMenuEvent contextMenuEvent;

    @Mock
    private SessionPresenterView tested;

    @Mock
    private NotifySettings settings;

    @Mock
    private ScrollEvent scrollEvent;

    @Mock
    private Element element;

    @Mock
    private FlowPanel palettePanel;

    @Mock
    private SessionContainer sessionContainer;

    @Mock
    private com.google.gwt.user.client.Element sessionContainerElement;

    @Mock
    private Style sessionContainerElementStyle;

    private ContextMenuHandler handler;

    @Mock
    private com.google.gwt.user.client.Element paletteElement;

    @Mock
    private com.google.gwt.dom.client.Style paletteStyle;

    @Before
    public void setup() throws Exception {
        super.init();

        doAnswer((invocation) -> {
            setFinal(tested,
                     SessionPresenterView.class.getDeclaredField("settings"),
                     settings);
            setFinal(tested,
                     SessionPresenterView.class.getDeclaredField("palettePanel"),
                     palettePanel);
            invocation.callRealMethod();
            return null;
        }).when(tested).init();

        doAnswer((invocation -> {
            invocation.callRealMethod();
            return null;
        })).when(tested).fireEvent(any());

        doAnswer((invocation -> {
            invocation.callRealMethod();
            return null;
        })).when(tested).onScroll(scrollEvent);

        doAnswer((invocation -> {
            setFinal(tested,
                     SessionPresenterView.class.getDeclaredField("sessionContainer"),
                     sessionContainer);
            invocation.callRealMethod();
            return null;
        })).when(tested).setContentScrollType(any(SessionPresenter.View.ScrollType.class));

        when(tested.addDomHandler(any(),
                                  any())).thenAnswer((invocation -> {
            handler = invocation.getArgumentAt(0,
                                               ContextMenuHandler.class);
            return null;
        }));

        when(scrollEvent.getRelativeElement()).thenReturn(element);
        when(palettePanel.getElement()).thenReturn(paletteElement);
        when(paletteElement.getStyle()).thenReturn(paletteStyle);

        doReturn(sessionContainerElement).when(sessionContainer).getElement();
        doReturn(sessionContainerElementStyle).when(sessionContainerElement).getStyle();

        tested.init();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNoContextMenu() {
        verify(tested).addDomHandler(any(),
                                     any());
        assertNotNull("Handler was null!",
                      handler);
        handler.onContextMenu(contextMenuEvent);
        verify(contextMenuEvent).preventDefault();
        verify(contextMenuEvent).stopPropagation();
    }

    private static void setFinal(Object instance,
                                 Field field,
                                 Object newValue) throws Exception {
        field.setAccessible(true);
        // remove final modifier from field
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field,
                              field.getModifiers() & ~Modifier.FINAL);
        field.set(instance,
                  newValue);
    }

    @Test
    public void testOnScroll() {
        reset(element);

        when(element.getScrollTop()).thenReturn(100);
        when(element.getScrollLeft()).thenReturn(200);

        tested.onScroll(scrollEvent);

        verify(paletteStyle, times(1)).setTop(100, Style.Unit.PX);
        verify(paletteStyle, times(1)).setLeft(200, Style.Unit.PX);
    }

    @Test
    public void testSetContentScrollTypeAuto() {
        tested.setContentScrollType(SessionPresenter.View.ScrollType.AUTO);

        verify(sessionContainerElementStyle).setOverflow(Style.Overflow.AUTO);
    }

    @Test
    public void testSetContentScrollTypeCustom() {
        tested.setContentScrollType(SessionPresenter.View.ScrollType.CUSTOM);

        verify(sessionContainerElementStyle).setOverflow(Style.Overflow.HIDDEN);
    }

    @Test
    public void testOnCanvasFocusedSelectionEvent() {

        final SessionPresenterView view = spy(new SessionPresenterView());
        final CanvasFocusedShapeEvent event = mock(CanvasFocusedShapeEvent.class);
        final com.google.gwt.user.client.Element element = mock(com.google.gwt.user.client.Element.class);
        final int eventX = 101;
        final int eventY = 110;

        when(event.getX()).thenReturn(eventX);
        when(event.getY()).thenReturn(eventY);
        when(sessionContainer.getElement()).thenReturn(element);
        doReturn(sessionContainer).when(view).getSessionContainer();

        view.onCanvasFocusedSelectionEvent(event);

        verify(element).setScrollLeft(eventX);
        verify(element).setScrollTop(eventY);
    }
}
