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

package org.kie.workbench.common.dmn.client.editors.types.common;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.DataType;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;

@Dependent
public class DataTypeUtils {

    private final DataTypeFactory dataTypeFactory;

    private final DMNGraphUtils dmnGraphUtils;

    @Inject
    public DataTypeUtils(final DataTypeFactory dataTypeFactory,
                         final DMNGraphUtils dmnGraphUtils) {
        this.dataTypeFactory = dataTypeFactory;
        this.dmnGraphUtils = dmnGraphUtils;
    }

    public List<DataType> defaultDataTypes() {
        return Stream
                .of(BuiltInType.values())
                .map(dataTypeFactory::makeDataType)
                .sorted(Comparator.comparing(DataType::getType))
                .collect(Collectors.toList());
    }

    public List<DataType> customDataTypes() {

        final List<ItemDefinition> getItemDefinition = dmnGraphUtils.getDefinitions().getItemDefinition();

        return getItemDefinition
                .stream()
                .map(dataTypeFactory::makeDataType)
                .sorted(Comparator.comparing(DataType::getName))
                .collect(Collectors.toList());
    }
}
