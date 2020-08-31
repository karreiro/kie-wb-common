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

package org.kie.workbench.common.dmn.client.commands.factory.canvas;

import java.util.Collection;

import org.kie.workbench.common.dmn.client.commands.factory.graph.DMNDeleteElementsGraphCommand;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.DeleteElementsCommand;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.diagram.SelectedDiagramProvider;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class DMNDeleteElementsCommand extends DeleteElementsCommand {

    private final SelectedDiagramProvider selectedDiagramProvider;

    public DMNDeleteElementsCommand(final Collection<Element> elements,
                                    final SelectedDiagramProvider selectedDiagramProvider) {
        super(elements);
        this.selectedDiagramProvider = selectedDiagramProvider;
    }

    public SelectedDiagramProvider getSelectedDiagramProvider() {
        return selectedDiagramProvider;
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new DMNDeleteElementsGraphCommand(() -> elements,
                                                 new CanvasMultipleDeleteProcessor(),
                                                 getSelectedDiagramProvider());
    }
}
