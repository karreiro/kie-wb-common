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

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLParagraphElement;
import elemental2.dom.HTMLUListElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.spy;

@RunWith(GwtMockitoTestRunner.class)
public class StructureTypesTooltipViewTest {

    @Mock
    private HTMLDivElement tooltip;

    @Mock
    private HTMLButtonElement close;

    @Mock
    private HTMLHeadingElement dataTypeName;

    @Mock
    private HTMLParagraphElement description;

    @Mock
    private HTMLUListElement dataTypeFields;

    @Mock
    private HTMLLIElement htmlLiElement;

    @Mock
    private HTMLElement htmlSpanElement;

    @Mock
    private HTMLAnchorElement viewDataTypeLink;

    @Mock
    private TranslationService translationService;

    private StructureTypesTooltipView view;

    @Before
    public void setup() {
        view = spy(new StructureTypesTooltipView(tooltip, close, dataTypeName, description, dataTypeFields, htmlLiElement, htmlSpanElement, viewDataTypeLink, translationService));
    }

    @Test
    public void testSetup() {
        view.setup();
    }

    @Test
    public void testInit() {
    }

    @Test
    public void testGetTooltip() {
    }

    @Test
    public void testTestSetup() {
    }

    @Test
    public void testOnClose() {
    }

    @Test
    public void testOnViewDataTypeLink() {
    }

    @Test
    public void testShow() {
    }

    @Test
    public void testHide() {
    }

    @Test
    public void testMakeHTMLLIElement() {
    }
}