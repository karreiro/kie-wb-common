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
package org.kie.workbench.common.widgets.client.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.widgets.client.widget.KieSelectElement;

@Templated
public class PackageListBoxViewImpl
        implements PackageListBoxView {

    private KieSelectElement kieSelectElement;

    @Inject
    @DataField("packageSelectContainer")
    HTMLDivElement packageSelectContainer;

    private PackageListBox presenter;

    @Inject
    public PackageListBoxViewImpl(final KieSelectElement kieSelectElement) {
        this.kieSelectElement = kieSelectElement;
    }

    @Override
    public void setPresenter(final PackageListBox presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setUp(final String activePackage,
                      final Map<String, String> packageNames) {

        kieSelectElement.setup(packageSelectContainer,
                               buildOptions(packageNames),
                               activePackage,
                               s -> onSelectionChange());
    }

    private List<KieSelectElement.Option> buildOptions(Map<String, String> packageNames) {

        final ArrayList<KieSelectElement.Option> options = new ArrayList<>();

        for (Map.Entry<String, String> entry : packageNames.entrySet()) {
            options.add(newOption(entry.getKey(), entry.getValue()));
        }
        return options;
    }

    KieSelectElement.Option newOption(final String name, final String value) {
        return new KieSelectElement.Option(name, value);
    }

    private void onSelectionChange() {
        presenter.onPackageSelected(kieSelectElement.getValue());

        kieSelectElement.setValue(kieSelectElement.getValue());
    }
}
