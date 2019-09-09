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
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model;

import java.util.ArrayList;
import java.util.List;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.ComponentWidths;

@JsType(namespace = JsPackage.GLOBAL)
public class ComponentsWidthsExtension {

    private List<ComponentWidths> widths;

    public ComponentsWidthsExtension() {
        this.widths = new ArrayList<>();
    }

    public List<ComponentWidths> getComponentsWidths() {
        return widths;
    }

    public void setComponentsWidths(final List<ComponentWidths> widths) {
        this.widths = widths;
    }
}