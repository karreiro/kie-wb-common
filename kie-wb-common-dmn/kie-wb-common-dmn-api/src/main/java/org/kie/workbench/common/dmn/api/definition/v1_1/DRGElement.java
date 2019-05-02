/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.Arrays;

import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;

public abstract class DRGElement extends NamedElement implements DynamicReadOnly {

    private static final String[] READONLY_FIELDS = {
        "Name",
        "AllowedAnswers",
        "Description",
        "Question",
        "DataType",
        "SourceType",
        "LocationURI"};

    protected boolean allowOnlyVisualChange;

    public DRGElement() {
    }

    public DRGElement(final Id id,
                      final Description description,
                      final Name name) {
        super(id,
              description,
              name);
    }

    @Override
    public void setAllowOnlyVisualChange(final boolean allowOnlyVisualChange) {
        this.allowOnlyVisualChange = allowOnlyVisualChange;
    }

    @Override
    public boolean isAllowOnlyVisualChange() {
        return allowOnlyVisualChange;
    }

    @Override
    public ReadOnly getReadOnly(final String fieldName) {
        if (!isAllowOnlyVisualChange()) {
            return ReadOnly.NOT_SET;
        }

        if (isReadonlyField(fieldName)) {
            return ReadOnly.TRUE;
        }

        return ReadOnly.FALSE;
    }

    protected boolean isReadonlyField(final String fieldName) {
        return Arrays.stream(READONLY_FIELDS).anyMatch(f -> f.equalsIgnoreCase(fieldName));
    }
}
