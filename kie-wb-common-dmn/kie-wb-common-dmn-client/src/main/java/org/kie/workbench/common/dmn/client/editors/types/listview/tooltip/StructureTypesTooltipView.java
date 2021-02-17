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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.DOMRect;
import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLBodyElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLParagraphElement;
import elemental2.dom.HTMLUListElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.SCROLL;
import static org.kie.workbench.common.dmn.client.editors.common.RemoveHelper.removeChildren;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.StructureTypesTooltipView_Description;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.StructureTypesTooltipView_DescriptionEmptyState;

@ApplicationScoped
@Templated
public class StructureTypesTooltipView implements StructureTypesTooltip.View {

    private static final String DISPLAY_BLOCK = "block";

    private static final String DISPLAY_NONE = "none";

    final EventListener SCROLL_LISTENER = event -> hide();

    final EventListener CLICK_LISTENER = event -> {
        if (isOutside(event)) {
            hide();
        }
    };

    @DataField("tooltip")
    private final HTMLDivElement tooltip;

    @DataField("close")
    private final HTMLButtonElement close;

    @DataField("data-type-name")
    private final HTMLHeadingElement dataTypeName;

    @DataField("description")
    private final HTMLParagraphElement description;

    @DataField("data-type-fields")
    private final HTMLUListElement dataTypeFields;

    @DataField("data-type-field")
    private final HTMLLIElement htmlLiElement;

    @DataField("data-type-field-type")
    private final HTMLElement htmlSpanElement;

    @DataField("view-data-type-link")
    private final HTMLAnchorElement viewDataTypeLink;

    private final TranslationService translationService;

    private StructureTypesTooltip presenter;

    @Inject
    public StructureTypesTooltipView(final HTMLDivElement tooltip,
                                     final HTMLButtonElement close,
                                     final @Named("h3") HTMLHeadingElement dataTypeName,
                                     final HTMLParagraphElement description,
                                     final HTMLUListElement dataTypeFields,
                                     final HTMLLIElement htmlLiElement,
                                     final @Named("span") HTMLElement htmlSpanElement,
                                     final HTMLAnchorElement viewDataTypeLink,
                                     final TranslationService translationService) {
        this.tooltip = tooltip;
        this.close = close;
        this.dataTypeName = dataTypeName;
        this.description = description;
        this.dataTypeFields = dataTypeFields;
        this.htmlLiElement = htmlLiElement;
        this.htmlSpanElement = htmlSpanElement;
        this.viewDataTypeLink = viewDataTypeLink;
        this.translationService = translationService;
    }

    @PostConstruct
    void setup() {
        getBody().appendChild(getElement());
        tooltip.style.display = "none";
    }

    @Override
    public void init(final StructureTypesTooltip presenter) {
        this.presenter = presenter;
    }

    @EventHandler("close")
    public void onClose(final ClickEvent e) {
        hide();
    }

    @EventHandler("view-data-type-link")
    public void onViewDataTypeLink(final ClickEvent event) {
        presenter.goToDataType();
        event.stopPropagation();
        event.preventDefault();
    }

    @Override
    public void show(final HTMLElement refElement) {
        // toggle
        updateTooltipPosition(refElement);
        updateTooltipAsVisible();
        updateContent();
        updateTooltipClass();
        setupListeners();
    }

    public void hide() {
        updateTooltipAsHidden();
        teardownListeners();
    }

    private void updateContent() {

        final String typeName = presenter.getTypeName();
        final List<DataType> typeFields = presenter.getTypeFields();

        description.textContent = getDescriptionText(typeName, typeFields);
        dataTypeName.textContent = typeName;

        removeChildren(dataTypeFields);
        typeFields.forEach(field -> dataTypeFields.appendChild(makeFieldElement(field)));
    }

    private String getDescriptionText(final String typeName, final List<DataType> typeFields) {
        final int numberOfFields = typeFields.size();
        if (numberOfFields == 0) {
            return translationService.format(StructureTypesTooltipView_DescriptionEmptyState, typeName);
        } else {
            return translationService.format(StructureTypesTooltipView_Description, typeName, numberOfFields);
        }
    }

    private void setupListeners() {
        getListItems().addEventListener(SCROLL, SCROLL_LISTENER);
        getBody().addEventListener(CLICK, CLICK_LISTENER);
    }

    private void teardownListeners() {
        getListItems().removeEventListener(SCROLL, SCROLL_LISTENER);
        getBody().removeEventListener(CLICK, CLICK_LISTENER);
    }

    private boolean isOutside(final Event event) {
        final HTMLElement element = getElement();
        final HTMLElement target = (HTMLElement) event.target;
        return !element.contains(target) && isTooltipVisible();
    }

    private boolean isTooltipVisible() {
        return tooltip.style.display.equals(DISPLAY_BLOCK);
    }

    private void updateTooltipAsVisible() {
        tooltip.style.display = DISPLAY_BLOCK;
    }

    private void updateTooltipAsHidden() {
        tooltip.style.display = DISPLAY_NONE;
    }

    private void updateTooltipPosition(final HTMLElement refElement) {

        final DOMRect refRect = refElement.getBoundingClientRect();
        final int PADDING = 20;
        final double x = PADDING + refRect.x + refRect.width;
        final double y = refRect.y;

        tooltip.style.top = y + "px";
        tooltip.style.left = x + "px";
    }

    private void updateTooltipClass() {
        tooltip.classList.toggle("overflow", isTooltipOverflowing());
    }

    private boolean isTooltipOverflowing() {
        final DOMRect dataTypesListRect = getListItems().getBoundingClientRect();
        final DOMRect tooltipRect = tooltip.getBoundingClientRect();
        return tooltipRect.y + tooltipRect.height > dataTypesListRect.y + dataTypesListRect.height;
    }

    private HTMLLIElement makeFieldElement(final DataType field) {

        final HTMLLIElement htmlLiElement = makeHTMLLIElement();
        final String name = field.getName();
        final HTMLElement type = makeTypeElement(field);

        htmlLiElement.textContent = name;
        htmlLiElement.appendChild(type);

        return htmlLiElement;
    }

    private HTMLBodyElement getBody() {
        return DomGlobal.document.body;
    }

    private HTMLElement getListItems() {
        return presenter.getListItems();
    }

    private HTMLElement makeTypeElement(final DataType field) {
        final HTMLElement htmlSpanElement = makeHTMLElement();
        htmlSpanElement.textContent = field.getType();
        return htmlSpanElement;
    }

    HTMLLIElement makeHTMLLIElement() {
        // This is a workaround for an issue on Errai (ERRAI-1114) related to 'ManagedInstance' + 'HTMLLIElement'.
        return (HTMLLIElement) htmlLiElement.cloneNode(false);
    }

    HTMLElement makeHTMLElement() {
        // This is a workaround for an issue on Errai (ERRAI-1114) related to 'ManagedInstance' + 'HTMLElement'.
        return (HTMLElement) htmlSpanElement.cloneNode(false);
    }
}
