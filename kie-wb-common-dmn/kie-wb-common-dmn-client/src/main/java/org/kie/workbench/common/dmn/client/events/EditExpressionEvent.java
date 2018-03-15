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

package org.kie.workbench.common.dmn.client.events;

import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.AbstractSessionEvent;
import org.kie.workbench.common.stunner.core.graph.Node;

@NonPortable
public class EditExpressionEvent extends AbstractSessionEvent {

    private final Node node;
    private Optional<HasName> hasName;
    private HasExpression hasExpression;

    public EditExpressionEvent(final ClientSession session,
                               final Optional<HasName> hasName,
                               final HasExpression hasExpression,
                               final Node node) {
        super(session);
        this.hasName = hasName;
        this.hasExpression = hasExpression;
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public Optional<HasName> getHasName() {
        return hasName;
    }

    public HasExpression getHasExpression() {
        return hasExpression;
    }
}
