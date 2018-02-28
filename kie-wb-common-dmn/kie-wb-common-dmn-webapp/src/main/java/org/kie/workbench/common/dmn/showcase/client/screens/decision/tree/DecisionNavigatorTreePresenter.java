/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.showcase.client.screens.decision.tree;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.showcase.client.screens.decision.DecisionNavigatorItem;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class DecisionNavigatorTreePresenter {

    @Inject
    private DecisionNavigatorTreeView view;

    private List<DecisionNavigatorItem> items;

    public DecisionNavigatorTreeView getView() {
        return view;
    }

    @PostConstruct
    private void setup() {
        view.init(this);
    }

    public void setupItems(final List<DecisionNavigatorItem> items) {

        this.items = items;

        setupView();
    }

    private void setupView() {
        view.clean();
        view.setup(items);
    }

    public interface View extends UberElemental<DecisionNavigatorTreePresenter> {

        void clean();

        void setup(List<DecisionNavigatorItem> items);
    }
}
