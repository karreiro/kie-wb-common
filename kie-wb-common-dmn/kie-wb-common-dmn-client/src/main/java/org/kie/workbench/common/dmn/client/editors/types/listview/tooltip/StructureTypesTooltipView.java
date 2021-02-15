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

@ApplicationScoped
@Templated
public class StructureTypesTooltipView implements StructureTypesTooltip.View {

    private final EventListener SCROLL_LISTENER = event -> hide();

    private final EventListener CLICK_LISTENER = event -> {
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

    @DataField("view-data-type-link")
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
        DomGlobal.document.body.appendChild(getElement());
        getTooltip().style.display = "none";
    }

    @Override
    public void init(final StructureTypesTooltip presenter) {
        this.presenter = presenter;
    }

    HTMLDivElement getTooltip() {
        return tooltip;
    }

    public void setContent(final String name,
                           final List<DataType> fields) {

        description.textContent = translationService.format(StructureTypesTooltipView_Description, name, fields.size());
        dataTypeName.textContent = name;

        removeChildren(dataTypeFields);
        fields.forEach(field -> dataTypeFields.appendChild(makeFieldElement(field)));
    }

    @EventHandler("close")
    public void onClose(final ClickEvent e) {
        hide();
    }

    @EventHandler("view-data-type-link")
    public void onViewDataTypeLink(final ClickEvent e) {
        e.preventDefault();
        e.stopPropagation();
        presenter.goToDataType();
    }

    @Override
    public void show(final HTMLElement refElement) {

        final HTMLDivElement tooltip = getTooltip();
        final DOMRect refRect = refElement.getBoundingClientRect();
        final int PADDING = 20;

        tooltip.style.top = refRect.y + "px";
        tooltip.style.left = PADDING + (refRect.x + refRect.width) + "px";
        tooltip.style.display = "block";

        setContent(presenter.getTypeName(), presenter.getTypeFields());

        setupListeners();
    }

    public void hide() {
        getTooltip().style.display = "none";
        teardownListeners();
    }

    private void setupListeners() {
        presenter.getListItems().addEventListener(SCROLL, SCROLL_LISTENER);
        DomGlobal.document.body.addEventListener(CLICK, CLICK_LISTENER);
    }

    private void teardownListeners() {
        presenter.getListItems().removeEventListener(SCROLL, SCROLL_LISTENER);
        DomGlobal.document.body.removeEventListener(CLICK, CLICK_LISTENER);
    }

    private boolean isOutside(final Event event) {
        final HTMLElement element = getElement();
        final HTMLElement target = (HTMLElement) event.target;
        return !element.contains(target) && getTooltip().style.display.equals("block");
    }

    private HTMLLIElement makeFieldElement(final DataType field) {

        final HTMLLIElement htmlLiElement = makeHTMLLIElement();
        final String name = field.getName();
        final HTMLElement type = makeTypeElement(field);

        htmlLiElement.textContent = name;
        htmlLiElement.appendChild(type);

        return htmlLiElement;
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
        return (HTMLElement) htmlLiElement.cloneNode(false);
    }
}
