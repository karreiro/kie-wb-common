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

package org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom;

import java.util.function.Consumer;
import java.util.function.Function;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco.MonacoEditorInitializer;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco.MonacoPropertiesFactory;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco.jsinterop.Monaco;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco.jsinterop.MonacoEditor;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco.jsinterop.MonacoLanguages;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static com.google.gwt.dom.client.Style.Unit.PCT;
import static com.google.gwt.dom.client.Style.Unit.PX;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco.MonacoPropertiesFactory.FEEL_LANGUAGE_ID;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco.MonacoPropertiesFactory.FEEL_THEME_ID;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class MonacoEditorDOMElementTest extends BaseDOMElementTest<MonacoEditorWidget, MonacoEditorDOMElementTest.MonacoEditorDOMElementFake> {

    @Mock
    private MonacoEditorWidget monacoEditorWidget;

    @Override
    protected MonacoEditorWidget getWidget() {
        return monacoEditorWidget;
    }

    @Override
    protected MonacoEditorDOMElementFake getDomElement() {
        return spy(new MonacoEditorDOMElementFake(widget,
                                                  gridLayer,
                                                  gridWidget,
                                                  sessionManager,
                                                  sessionCommandManager,
                                                  (gc) -> new DeleteCellValueCommand(gc,
                                                                                     () -> uiModelMapper,
                                                                                     gridLayer::batch),
                                                  (gcv) -> new SetCellValueCommand(gcv,
                                                                                   () -> uiModelMapper,
                                                                                   gridLayer::batch)));
    }

    @Test
    public void testSetValue() {
        domElement.setValue(VALUE);
        verify(widget).setValue(VALUE);
    }

    @Test
    public void testGetValue() {
        domElement.getValue();
        verify(widget).getValue();
    }

    @Test
    public void testSetupElements() {

        doNothing().when(domElement).setupContainerComponent();
        doNothing().when(domElement).setupInternalComponent();

        domElement.setupElements();

        verify(domElement).setupContainerComponent();
        verify(domElement).setupInternalComponent();
    }

    @Test
    public void testSetupContainerComponent() {

        final SimplePanel container = mock(SimplePanel.class);
        final Element element = mock(Element.class);
        final Style style = mock(Style.class);

        doReturn(container).when(domElement).getContainer();
        when(container.getElement()).thenReturn(element);
        when(element.getStyle()).thenReturn(style);

        domElement.setupContainerComponent();

        verify(style).setPaddingLeft(5, PX);
        verify(style).setPaddingRight(5, PX);
        verify(style).setPaddingTop(5, PX);
        verify(style).setPaddingBottom(5, PX);
        verify(container).setWidget(widget);
    }

    @Test
    public void testSetupInternalComponent() {

        final MonacoEditorInitializer editorInitializer = mock(MonacoEditorInitializer.class);
        final Element element = mock(Element.class);
        final Style style = mock(Style.class);
        final Consumer<Monaco> onMonacoLoaded = m -> {/* Nothing. */};

        doReturn(editorInitializer).when(domElement).makeMonacoEditorInitializer();
        doReturn(onMonacoLoaded).when(domElement).onMonacoLoaded();
        when(widget.getElement()).thenReturn(element);
        when(element.getStyle()).thenReturn(style);

        domElement.setupInternalComponent();

        verify(style).setWidth(100, PCT);
        verify(style).setHeight(100, PCT);
        verify(editorInitializer).require(onMonacoLoaded);
    }

    @Test
    public void testOnMonacoLoaded() {

        final MonacoPropertiesFactory properties = mock(MonacoPropertiesFactory.class);
        final MonacoLanguages languages = mock(MonacoLanguages.class);
        final JavaScriptObject language = mock(JavaScriptObject.class);
        final JavaScriptObject languageDefinition = mock(JavaScriptObject.class);
        final JavaScriptObject completionItemProvider = mock(JavaScriptObject.class);
        final JavaScriptObject themeData = mock(JavaScriptObject.class);
        final JavaScriptObject constructionOptions = mock(JavaScriptObject.class);
        final Monaco monaco = mock(Monaco.class);
        final MonacoEditor editor = mock(MonacoEditor.class);
        final com.google.gwt.user.client.Element element = mock(com.google.gwt.user.client.Element.class);
        final elemental2.dom.Element elemental2Element = mock(elemental2.dom.Element.class);

        monaco.languages = languages;
        monaco.editor = editor;

        when(properties.getLanguage()).thenReturn(language);
        when(properties.getLanguageDefinition()).thenReturn(languageDefinition);
        when(properties.getCompletionItemProvider()).thenReturn(completionItemProvider);
        when(properties.getThemeData()).thenReturn(themeData);
        when(properties.getConstructionOptions()).thenReturn(constructionOptions);
        when(properties.getConstructionOptions()).thenReturn(constructionOptions);
        when(widget.getElement()).thenReturn(element);
        doReturn(properties).when(domElement).makeMonacoPropertiesFactory();
        doReturn(elemental2Element).when(domElement).uncheckedCast(element);

        domElement.onMonacoLoaded().accept(monaco);

        verify(languages).register(language);
        verify(languages).setMonarchTokensProvider(FEEL_LANGUAGE_ID, languageDefinition);
        verify(languages).registerCompletionItemProvider(FEEL_LANGUAGE_ID, completionItemProvider);
        verify(languages).register(language);
        verify(editor).defineTheme(FEEL_THEME_ID, themeData);
        verify(editor).create(elemental2Element, constructionOptions);
    }

    @Test
    public void testInitialise() {
        final GridBodyCellRenderContext context = mock(GridBodyCellRenderContext.class);
        domElement.initialise(context);
        verify(domElement).transform(context);
    }

    class MonacoEditorDOMElementFake extends MonacoEditorDOMElement {

        MonacoEditorDOMElementFake(final MonacoEditorWidget widget,
                                   final GridLayer gridLayer,
                                   final GridWidget gridWidget,
                                   final SessionManager sessionManager,
                                   final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                   final Function<GridCellTuple, Command> hasNoValueCommand,
                                   final Function<GridCellValueTuple, Command> hasValueCommand) {
            super(widget, gridLayer, gridWidget, sessionManager, sessionCommandManager, hasNoValueCommand, hasValueCommand);
        }

        @Override
        protected SimplePanel getContainer() {
            return super.getContainer();
        }

        @Override
        protected void transform(final GridBodyCellRenderContext context) {
            // empty
        }
    }
}
