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

public class DecisionNavigatorItem {

    private String uuid;

    private String label;

    private Type type;

    private List<DecisionNavigatorItem> children;

    public DecisionNavigatorItem(final String uuid,
                                 final String label,
                                 final Type type) {
        this.uuid = uuid;
        this.label = label;
        this.type = type;
        this.children = new ArrayList<>();
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

    public enum Type {

        ROOT,
        ITEM,
        COLUMNS,
        TABLE,
        SUB_ITEM

//        // caret-down, caret-right
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
