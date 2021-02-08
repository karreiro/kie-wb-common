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

package org.kie.workbench.common.dmn.client.editors.types.listview.tooltip;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLHeadingElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@ApplicationScoped
@Templated
public class StructureTypesTooltipView implements StructureTypesTooltip.View {

    @DataField("tooltip")
    private final HTMLDivElement tooltip;

    @DataField("data-type-name")
    private final HTMLHeadingElement dataTypeName;

    private StructureTypesTooltip presenter;

    @Inject
    public StructureTypesTooltipView(final HTMLDivElement tooltip,
                                     final @Named("h3") HTMLHeadingElement dataTypeName) {
        this.tooltip = tooltip;
        this.dataTypeName = dataTypeName;
    }

    @Override
    public void init(final StructureTypesTooltip presenter) {
        this.presenter = presenter;
    }

    @Override
    public HTMLDivElement getTooltip() {
        return tooltip;
    }

    @Override
    public void setName(final String name) {
        dataTypeName.textContent = name;
    }
}
