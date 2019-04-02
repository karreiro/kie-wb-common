/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
 */

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker;

import java.util.Objects;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.i18n.client.DateTimeFormat;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.FocusEvent;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.views.pfly.selectpicker.ElementHelper;
import org.uberfire.client.views.pfly.widgets.Moment;

import static org.uberfire.client.views.pfly.widgets.Moment.Builder.moment;

@Dependent
public class TimePicker {

    static final DateTimeFormat TIME_FORMAT = DateTimeFormat.getFormat("HH:mm:ss");

    private final View view;

    private HTMLInputElement inputBind;

    private Element.OnblurCallbackFn previousCallback;

    private Consumer<Moment> onDateChanged;

    @Inject
    public TimePicker(final View view) {
        this.view = view;
    }

    public void bind(final HTMLInputElement input) {

        this.inputBind = input;

        final HTMLElement viewElement = view.getElement();
        ElementHelper.insertAfter(viewElement, input);

        input.onclick = this::inputOnClick;
        previousCallback = input.onblur;
        input.onblur = this::inputOnBlur;

        viewElement.scrollTop = input.scrollTop;
        viewElement.scrollLeft = input.scrollLeft;

        view.setOnBlur(this::onViewElementBlur);
        view.setOnDateChanged(this::onDateChanged);
    }

    HTMLInputElement getInputBind() {
        return this.inputBind;
    }

    void refreshDateInPopup() {

        if (isDateSetInInput()) {
            final Moment moment = getDateInInput();
            if (moment.isValid()) {
                view.setDate(moment);
            }
        } else {
            setDefaultData();
        }
    }

    Moment getDateInInput(){
        return moment(getInputBind().value, "HH:mm:ss");
    }

    boolean isDateSetInInput() {
        return !StringUtils.isEmpty(getInputBind().value)
                   && !StringUtils.isEmpty(getInputBind().value.trim())
                   && getInputBind().value.contains(":");
    }

    void setDefaultData() {
        final Moment now = moment();
        view.setDate(now);
    }

    void onDateChanged(final Moment nativeDate) {

        getInputBind().value = nativeDate.format("HH:mm:ss");
        if (!Objects.isNull(onDateChanged)) {
            onDateChanged.accept(nativeDate);
        }
    }

    public void setOnDateChanged(final Consumer<Moment> onDateChanged) {
        this.onDateChanged = onDateChanged;
    }

    Object inputOnClick(final Event event) {
        refreshDateInPopup();
        HiddenHelper.show(view.getElement());
        return this;
    }

    private Object onViewElementBlur(final Event event) {

        final FocusEvent focusEvent = (FocusEvent) event;
        if (!Objects.equals(focusEvent.relatedTarget, getInputBind())
                && !isChildrenOfView((Element) focusEvent.relatedTarget)) {

            HiddenHelper.hide(view.getElement());
            if (!Objects.isNull(previousCallback)) {
                previousCallback.onInvoke(event);
            }
        }

        return this;
    }

    private Object inputOnBlur(final Event event) {

        final FocusEvent focusEvent = (FocusEvent) event;

        if (!Objects.equals(focusEvent.relatedTarget, view.getElement())
                && !isChildrenOfView((Element) focusEvent.relatedTarget)) {

            HiddenHelper.hide(view.getElement());
            if (!Objects.isNull(previousCallback)) {
                previousCallback.onInvoke(event);
            }
        }

        refreshDateInPopup();
        return null;
    }

    boolean isChildrenOfView(final Element element) {

        final Element viewElement = view.getElement();
        return viewElement.contains(element);
    }

    public String getValue() {

        final Moment currentDate = view.getDate();
        if (Objects.isNull(currentDate)) {
            return "";
        }

        return currentDate.format("HH:mm:ss");
    }

    public void setValue(final String value) {

        final Moment moment = moment(value, "HH:mm:ss");
        if (moment.isValid()) {
            view.setDate(moment);
        }
    }

    public interface View extends UberElemental<TimePickerView>,
                                  IsElement {

        void setDate(final Moment moment);

        void setOnDateChanged(final Consumer<Moment> onDateChanged);

        Moment getDate();

        void setOnBlur(final Consumer<Event> onViewElementBlur);
    }
}
