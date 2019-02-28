/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.common.cards;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.common.cards.frame.CardFrameComponent;
import org.mockito.Mock;
import org.mockito.Mockito;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CardsGridComponentTest {

    @Mock
    private CardsGridComponent.View view;

    @Mock
    private ManagedInstance<CardFrameComponent> frames;

    private CardsGridComponent cardsGrid;

    @Before
    public void setup() {
        cardsGrid = Mockito.spy(new CardsGridComponent(view, frames));
    }

    @Test
    public void testInit() {
        cardsGrid.init();
        verify(view).init(cardsGrid);
    }

    @Test
    public void testSetupCards() {

        final CardComponent card1 = mock(CardComponent.class);
        final CardComponent card2 = mock(CardComponent.class);
        final CardFrameComponent frame1 = mock(CardFrameComponent.class);
        final CardFrameComponent frame2 = mock(CardFrameComponent.class);
        final HTMLElement htmlElement1 = mock(HTMLElement.class);
        final HTMLElement htmlElement2 = mock(HTMLElement.class);
        final List<CardComponent> cards = asList(card1, card2);

        when(frames.get()).thenReturn(frame1, frame2);
        when(frame1.getElement()).thenReturn(htmlElement1);
        when(frame2.getElement()).thenReturn(htmlElement2);

        cardsGrid.setupCards(cards);

        verify(frame1).initialize(card1);
        verify(frame2).initialize(card2);
        verify(view).appendCard(htmlElement1);
        verify(view).appendCard(htmlElement2);
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);
        when(view.getElement()).thenReturn(expectedElement);

        final HTMLElement actualElement = cardsGrid.getElement();

        assertEquals(expectedElement, actualElement);
    }
}