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

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class ExampleView implements Example.View {

    private Example presenter;

    @Inject
    private Elemental2DomUtil elemental2DomUtil;

    @Inject
    @DataField("gwt-component")
    private HTMLDivElement gwtComponent;

    @Inject
    @DataField("my-input")
    private HTMLInputElement myInput;

    @Inject
    @DataField("my-ok-button")
    private HTMLButtonElement myOkButton;

    @Inject
    @DataField("my-cancel-button")
    private HTMLButtonElement myCancelButton;

    private MyGwtComponent myGwtComponent;

    @Override
    public void init(final Example presenter) {
        this.presenter = presenter;

        // (>>>) Setup the gwt element
        myGwtComponent = new MyGwtComponent();

        final Widget widget = myGwtComponent.asWidget();
        final HTMLElement newChild = elemental2DomUtil.asHTMLElement(widget.getElement());

        gwtComponent.appendChild(newChild);
    }

    @EventHandler("my-ok-button")
    public void onOkClick(final ClickEvent e) {
        presenter.ok();
    }

    @EventHandler("my-cancel-button")
    public void onCancelClick(final ClickEvent e) {
        presenter.cancel();
    }

    @Override
    public String getGWTValue() {
        return myGwtComponent.getValue();
    }

    @Override
    public String getElemental2Value() {
        return myInput.value;
    }

    // (>>>) A generic GWT based component
    class MyGwtComponent implements IsWidget {

        private final Form form;
        private final TextBox input;
        private final Button ok;
        private final Button cancel;

        MyGwtComponent() {
            form = new Form();
            input = new TextBox();
            ok = new Button("OK");
            cancel = new Button("Cancel");

            form.add(input);
            form.add(ok);
            form.add(cancel);
        }

        String getValue() {
            return input.getValue();
        }

        @Override
        public Widget asWidget() {
            return form;
        }
    }
}
