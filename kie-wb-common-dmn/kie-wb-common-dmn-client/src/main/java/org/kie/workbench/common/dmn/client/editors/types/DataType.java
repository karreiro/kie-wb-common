/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.List;

import org.uberfire.commons.uuid.UUID;

public class DataType {

    private final String uuid;

    private final String name;

    private final String type;

    private final List<DataType> subDataTypes;

    private final boolean isBasic;

    private final boolean isExternal;

    private final boolean isDefault;

    public DataType(final String name,
                    final String type,
                    final List<DataType> subDataTypes,
                    final boolean isBasic,
                    final boolean isExternal,
                    final boolean isDefault) {
        this.uuid = UUID.uuid();
        this.name = name;
        this.type = type;
        this.isBasic = isBasic;
        this.subDataTypes = subDataTypes;
        this.isExternal = isExternal;
        this.isDefault = isDefault;
    }

    public String getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<DataType> getSubDataTypes() {
        return subDataTypes;
    }

    public boolean isBasic() {
        return isBasic;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public boolean isExternal() {
        return isExternal;
    }

    public boolean hasSubDataTypes() {
        return !subDataTypes.isEmpty();
    }
}
