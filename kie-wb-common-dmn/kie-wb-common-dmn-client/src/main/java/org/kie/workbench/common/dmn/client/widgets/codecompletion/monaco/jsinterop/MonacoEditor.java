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

package org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco.jsinterop;

import com.google.gwt.core.client.JavaScriptObject;
import elemental2.dom.Element;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class MonacoEditor {

    public native void defineTheme(final String themeId,
                                   final JavaScriptObject themeData);

    public native MonacoStandaloneCodeEditor create(final Element themeId,
                                                    final JavaScriptObject options);

    public native void setTheme(final String feelThemeId);
}