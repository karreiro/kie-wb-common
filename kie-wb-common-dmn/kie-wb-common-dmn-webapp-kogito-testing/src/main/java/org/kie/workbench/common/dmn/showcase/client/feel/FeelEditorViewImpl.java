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
package org.kie.workbench.common.dmn.showcase.client.feel;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.Element;
import jsinterop.base.Js;
import org.uberfire.ext.widgets.common.client.ace.AceEditor;
import org.uberfire.ext.widgets.common.client.ace.AceEditorCursorPosition;
import org.uberfire.ext.widgets.common.client.ace.AceEditorTheme;

public class FeelEditorViewImpl
        implements FeelEditorView {

    private final String TOP_ROW_HEIGHT = "1000px";

    //    private TextArea text = new TextArea();
    private TextArea astDump = new TextArea();
    private TextArea evaluation = new TextArea();

    private TextBox row = new TextBox();
    private TextBox column = new TextBox();

    private TextArea c3 = new TextArea();
    private TextArea availableMethods = new TextArea();
    private FEELEditor presenter;

    private AceEditor aceEditor = new AceEditor();
    private TextBox caretIndex = new TextBox();

    @Override
    public AceEditor getAceEditor() {
        return aceEditor;
    }

    @Override
    public Widget asWidget() {

        aceEditor.startEditor();
//        drlEditor.setModeByName( "drools" );
        aceEditor.setTheme(AceEditorTheme.CHROME);

        astDump.setEnabled(false);
        astDump.setHeight(TOP_ROW_HEIGHT);
        astDump.setWidth("250px");
        aceEditor.setHeight(TOP_ROW_HEIGHT);
        aceEditor.setWidth("500px");
        c3.setEnabled(false);
        c3.setHeight(TOP_ROW_HEIGHT);
        c3.setWidth("300px");
        availableMethods.setEnabled(false);
        availableMethods.setHeight(TOP_ROW_HEIGHT);
        availableMethods.setWidth("250px");
        evaluation.setEnabled(false);
        evaluation.setHeight("50px");
        evaluation.setWidth("200px");

        final VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.addDomHandler(keyUpEvent -> presenter.onChange(aceEditor.getText()), KeyUpEvent.getType());

        verticalPanel.setWidth("100%");
        verticalPanel.setHeight("100%");
        verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        verticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        final HorizontalPanel topRow = new HorizontalPanel();
        topRow.add(astDump);
        topRow.add(aceEditor);
        topRow.add(c3);
        topRow.add(availableMethods);
        verticalPanel.add(topRow);

        HorizontalPanel middleRow = new HorizontalPanel();
        row.setValue("");
        column.setValue("");
        Element r = Js.uncheckedCast(row.getElement());
        Element c = Js.uncheckedCast(column.getElement());

        r.setAttribute("type", "number");
        c.setAttribute("type", "number");

        middleRow.add(row);
        middleRow.add(column);
        verticalPanel.add(middleRow);

        HorizontalPanel bottomRow = new HorizontalPanel();
        bottomRow.add(evaluation);
        bottomRow.add(caretIndex);
        verticalPanel.add(bottomRow);
        return verticalPanel;
    }

    @Override
    public void setPresenter(FEELEditor presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setASTDump(String result) {
        astDump.setText(result);
    }

    @Override
    public void setEvaluation(String result) {
        evaluation.setText(result);
    }

    @Override
    public void setC3(String result) {
        c3.setText(result);
    }

    @Override
    public void setAvailableMethods(String availableMethods) {
        this.availableMethods.setText(availableMethods);
    }

    @Override
    public int getCaretIndex() {
        try {
            return Integer.parseInt(caretIndex.getText());
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public AceEditorCursorPosition getCursor() {
        return aceEditor.getCursorPosition();
    }

    @Override
    public String getRow() {
        return row.getValue();
    }

    @Override
    public String getColumn() {
        return column.getValue();
    }
}
