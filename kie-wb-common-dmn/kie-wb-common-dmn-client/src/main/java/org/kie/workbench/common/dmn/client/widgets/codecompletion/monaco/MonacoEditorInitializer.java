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

package org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco;

import java.util.function.Consumer;

import com.google.gwt.core.client.JsArrayString;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco.jsinterop.Monaco;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco.jsinterop.MonacoLoader;

public class MonacoEditorInitializer {

    public void require(final Consumer<Monaco> monacoConsumer) {

        switchADMLoaderFromDefaultToMonaco();

        require(monacoModule(), monaco -> {
            monacoConsumer.accept(monaco);
            switchADMLoaderFromMonacoToDefault();
        });
    }

    private JsArrayString monacoModule() {
        return makeMonacoPropertiesFactory().monacoModule();
    }

    void require(final JsArrayString modules,
                 final MonacoLoader.CallbackFunction callback) {
        MonacoLoader.require(modules, callback);
    }

    void switchADMLoaderFromDefaultToMonaco() {
        nativeSwitchADMLoaderFromDefaultToMonaco();
    }

    void switchADMLoaderFromMonacoToDefault() {
        nativeSwitchADMLoaderFromMonacoToDefault();
    }

    MonacoPropertiesFactory makeMonacoPropertiesFactory() {
        return new MonacoPropertiesFactory();
    }

    private native void nativeSwitchADMLoaderFromDefaultToMonaco() /*-{

        // Store current definition of 'define' and 'require'
        $wnd.__GLOBAL_DEFINE__ = $wnd.define;
        $wnd.__GLOBAL_REQUIRE__ = $wnd.require;

        // Set Monaco AMD Loader definition of 'define' and 'require'
        $wnd.define = $wnd.__MonacoAMDLoader.define;
        $wnd.require = $wnd.__MonacoAMDLoader.require;
    }-*/;

    private native void nativeSwitchADMLoaderFromMonacoToDefault() /*-{
        // Reset the definition of 'define' and 'require'
        $wnd.define = $wnd.__GLOBAL_DEFINE__;
        $wnd.require = $wnd.__GLOBAL_REQUIRE__;
    }-*/;
}
