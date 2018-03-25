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

package org.kie.workbench.common.dmn.client.decision;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.uberfire.mvp.Command;

public class DecisionNavigatorItem {

    private String uuid;

    private String label;

    private Type type;

    private Command onClick;

    private List<DecisionNavigatorItem> children = new ArrayList<>();

    private List<DecisionNavigatorItem> parents = new ArrayList<>();

    public DecisionNavigatorItem(final String uuid,
                                 final String label,
                                 final Type type,
                                 final Command onClick,
                                 final List<DecisionNavigatorItem> children) {
        this.uuid = uuid;
        this.label = label;
        this.type = type;
        this.onClick = onClick;
        this.children = children;
    }

    public DecisionNavigatorItem(final String uuid,
                                 final String label,
                                 final Type type,
                                 final Command onClick) {
        this.uuid = uuid;
        this.label = label;
        this.type = type;
        this.onClick = onClick;
    }

    public DecisionNavigatorItem(final String uuid) {
        this.uuid = uuid;
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
        onClick.execute();
    }

    public Command getOnClick() {
        return onClick;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DecisionNavigatorItem that = (DecisionNavigatorItem) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public enum Type {
        ROOT,
        ITEM,
        COLUMNS,
        TABLE,
        SUB_ITEM
    }
}
