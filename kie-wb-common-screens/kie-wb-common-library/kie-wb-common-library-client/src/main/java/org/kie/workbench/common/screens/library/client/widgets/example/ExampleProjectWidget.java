/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.kie.workbench.common.screens.library.client.widgets.example;

import java.util.List;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.examples.model.ExampleProjectError;
import org.kie.workbench.common.screens.library.client.widgets.example.errors.ExampleProjectErrorPresenter;
import org.kie.workbench.common.screens.library.client.widgets.example.errors.ExampleProjectOkPresenter;
import org.uberfire.client.mvp.UberElemental;

public class ExampleProjectWidget {

    public interface View extends UberElemental<ExampleProjectWidget>,
                                  IsElement {

        void setup(String name,
                   String description,
                   HTMLElement errors);

        void setActive();

        void unsetActive();

        void setDisabled();
    }

    private ExampleProject model;
    private boolean selected;

    private final ExampleProjectWidget.View view;
    private final ExampleProjectOkPresenter exampleProjectOkPresenter;
    private final ExampleProjectErrorPresenter exampleProjectErrorPresenter;

    @Inject
    public ExampleProjectWidget(final ExampleProjectWidget.View view,
                                final ExampleProjectOkPresenter exampleProjectOkPresenter,
                                final ExampleProjectErrorPresenter exampleProjectErrorPresenter) {
        this.view = view;
        this.exampleProjectOkPresenter = exampleProjectOkPresenter;
        this.exampleProjectErrorPresenter = exampleProjectErrorPresenter;
    }

    public void init(final ExampleProject exampleProject) {
        this.view.init(this);
        this.model = exampleProject;
        view.setup(exampleProject.getName(),
                   exampleProject.getDescription(),
                   this.buildErrors(exampleProject));
        this.disableViewIfHasErrors();
    }

    private void disableViewIfHasErrors() {
        if (this.hasErrors()) {
            this.view.setDisabled();
        }
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return this.model.getName();
    }

    public void select() {
        if (!this.hasErrors()) {
            if (!this.isSelected()) {
                this.setSelected(true);
                this.getView().setActive();
            } else {
                this.setSelected(false);
                this.getView().unsetActive();
            }
        }
    }

    private HTMLElement buildErrors(ExampleProject exampleProject) {
        List<ExampleProjectError> errors = exampleProject.getErrors();
        if (errors.isEmpty()) {
            return this.exampleProjectOkPresenter.getView().getElement();
        } else {
            this.exampleProjectErrorPresenter.initialize(errors);
            return this.exampleProjectErrorPresenter.getView().getElement();
        }
    }

    private boolean hasErrors() {
        return !this.model.getErrors().isEmpty();
    }

    public ExampleProjectWidget.View getView() {
        return view;
    }
}
