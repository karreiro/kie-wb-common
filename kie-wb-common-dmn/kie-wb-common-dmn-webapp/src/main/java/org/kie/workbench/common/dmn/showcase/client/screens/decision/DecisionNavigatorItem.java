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

package org.kie.workbench.common.dmn.showcase.client.screens.decision;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Event;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;

public class DecisionNavigatorItem {

    private String uuid;

    private String label;

    private Type type;

    private List<DecisionNavigatorItem> children = new ArrayList<>();
    private List<DecisionNavigatorItem> parents = new ArrayList<>();
    private EditExpressionEvent editExpressionEvent;
    private Event<EditExpressionEvent> eventEditExpressionEvent;

    public DecisionNavigatorItem(final String uuid,
                                 final String label,
                                 final Type type,
                                 final List<DecisionNavigatorItem> children) {
        this.uuid = uuid;
        this.label = label;
        this.type = type;
        this.children = children;
    }

    public DecisionNavigatorItem(final String uuid,
                                 final String label,
                                 final Type type) {
        this.uuid = uuid;
        this.label = label;
        this.type = type;
    }

    public String getUUID() {
        return uuid;
    }

    public String getLabel() {
        return label;
    }

    public Type getType() {
        return type;
    }

    public List<DecisionNavigatorItem> getChildren() {
        return children;
    }

    public List<DecisionNavigatorItem> getParents() {
        return parents;
    }

    public void onClick() {
        if (editExpressionEvent != null) {
            eventEditExpressionEvent.fire(editExpressionEvent);
        }
    }

    public void setEditExpressionEvent(final EditExpressionEvent editExpressionEvent) {
        this.editExpressionEvent = editExpressionEvent;
    }

    public void setEventEditExpressionEvent(final Event<EditExpressionEvent> eventEditExpressionEvent) {
        this.eventEditExpressionEvent = eventEditExpressionEvent;
    }

    public enum Type {

        ROOT,
        ITEM,
        COLUMNS,
        TABLE,
        SUB_ITEM

//        // caret-down"\f0d7", caret-right"\f0da"
//
//        ROOT("fa-share-alt"),
//
//        ITEM("fa-square"),
//
//        SUB_ITEM("fa-flickr"),
//
//        COLUMNS("fa-columns"),
//
//        TABLE("fa-table");
//
//        private final String cssClass;
//
//        Type(final String cssClass) {
//            this.cssClass = cssClass;
//        }
//
//        public String getCssClass() {
//            return cssClass;
//        }
    }
}
