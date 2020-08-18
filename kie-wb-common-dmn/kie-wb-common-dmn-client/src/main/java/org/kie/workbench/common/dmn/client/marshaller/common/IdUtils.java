/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.marshaller.common;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.util.UUID;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public class IdUtils {

    private static String SEPARATOR = "#";

    public static String getPrefixedId(final String prefixId,
                                       final String rawId) {
        return Stream.of(prefixId, rawId)
                .filter(s -> !isEmpty(s))
                .collect(Collectors.joining(SEPARATOR));
    }

    public static String getRawId(final String prefixedId) {
        if (isEmpty(prefixedId)) {
            return "";
        }

        final String[] parts = prefixedId.split(SEPARATOR);

        switch (parts.length) {
            case 1:
                return parts[0];
            case 2:
                return parts[1];
            default:
                return "";
        }
    }

    public static String uniqueId() {
        return UUID.uuid();
    }
}
