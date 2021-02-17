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

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.ext.widgets.common.client.ace.AceEditor;
import org.uberfire.ext.widgets.common.client.ace.AceEditorCursorPosition;

public interface FeelEditorView
        extends IsWidget {

    AceEditor getAceEditor();

    void setPresenter(FEELEditor feelEditor);

    void setASTDump(String result);

    void setEvaluation(String result);

    void setC3(String result);

    void setAvailableMethods(String availableMethods);

    int getCaretIndex();

    AceEditorCursorPosition getCursor();

    String getRow();

    String getColumn();
}