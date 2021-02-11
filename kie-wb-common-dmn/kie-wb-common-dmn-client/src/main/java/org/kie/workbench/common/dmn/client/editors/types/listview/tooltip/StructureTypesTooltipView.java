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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLParagraphElement;
import elemental2.dom.HTMLUListElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.common.RemoveHelper;

import static org.kie.workbench.common.dmn.client.editors.common.RemoveHelper.removeChildren;

@ApplicationScoped
@Templated
public class StructureTypesTooltipView implements StructureTypesTooltip.View {

    @DataField("tooltip")
    private final HTMLDivElement tooltip;

    @DataField("data-type-name")
    private final HTMLHeadingElement dataTypeName;

    @DataField("description")
    private final HTMLParagraphElement description;

    @DataField("data-type-fields")
    private final HTMLUListElement dataTypeFields;

    @DataField("view-data-type-link")
    private final HTMLAnchorElement viewDataTypeLink;

    private final ManagedInstance<HTMLLIElement> htmlLiElements;

    private StructureTypesTooltip presenter;

    @Inject
    public StructureTypesTooltipView(final HTMLDivElement tooltip,
                                     final @Named("h3") HTMLHeadingElement dataTypeName,
                                     final HTMLParagraphElement description,
                                     final HTMLUListElement dataTypeFields,
                                     final HTMLAnchorElement viewDataTypeLink,
                                     final ManagedInstance<HTMLLIElement> htmlLiElements) {
        this.tooltip = tooltip;
        this.dataTypeName = dataTypeName;
        this.description = description;
        this.dataTypeFields = dataTypeFields;
        this.viewDataTypeLink = viewDataTypeLink;
        this.htmlLiElements = htmlLiElements;
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

    @Override
    public void setFields(final List<String> fields) {
        removeChildren(dataTypeFields);
        fields.forEach(field -> {
            dataTypeFields.appendChild(getLiElement(field));
        });
    }

    private HTMLLIElement getLiElement(final String field) {
        final HTMLLIElement htmlliElement = htmlLiElements.get();
        htmlliElement.textContent = field;
        return htmlliElement;
    }
}
