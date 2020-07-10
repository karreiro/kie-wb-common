/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.example;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.uberfire.client.mvp.UberElemental;

// (>>>) This is the Presenter for our Elemental2 based component (in the MVP pattern - Model, View, Presenter)

public class Example {

    private View view;

    @Inject
    public Example(final View view) {
        this.view = view;
    }

    // (>>>) Initializing template
    @PostConstruct
    public void init() {
        view.init(this);
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public void ok() {
        // (>>>) ok logic goes here
        DomGlobal.alert("OK, from a Elemental2 component. This is a value comes the GWT input '" + view.getGWTValue() + "' and this value comes from the Elemental2 input '" + view.getElemental2Value() + "'" );
    }

    public void cancel() {
        // (>>>) cancel logic goes here
        DomGlobal.alert("OK, from a Elemental2 component.");
    }

    // (>>>) This is the presenter interface for our view
    public interface View extends UberElemental<Example>,
            // (>>>) This is the inter  face with a default implementation for the getElement method considering the Elemental2 based components
                                  IsElement {

        String getGWTValue();

        String getElemental2Value();
    }
}
