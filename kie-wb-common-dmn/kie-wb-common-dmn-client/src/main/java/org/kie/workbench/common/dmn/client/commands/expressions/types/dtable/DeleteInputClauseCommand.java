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

package org.kie.workbench.common.dmn.client.commands.expressions.types.dtable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.commands.util.CommandUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapperHelper;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasGraphCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.command.impl.AbstractGraphCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

public class DeleteInputClauseCommand extends AbstractCanvasGraphCommand implements VetoExecutionCommand,
                                                                                    VetoUndoCommand {

    private final DecisionTable dtable;
    private final GridData uiModel;
    private final int uiColumnIndex;
    private final DecisionTableUIModelMapper uiModelMapper;
    private final org.uberfire.mvp.Command canvasOperation;

    private final InputClause oldInputClause;
    private final List<UnaryTests> oldColumnData;
    private final GridColumn<?> oldUiModelColumn;
    private final List<Double> oldColumnWidths;

    public DeleteInputClauseCommand(final DecisionTable dtable,
                                    final GridData uiModel,
                                    final int uiColumnIndex,
                                    final DecisionTableUIModelMapper uiModelMapper,
                                    final org.uberfire.mvp.Command canvasOperation) {
        this.dtable = dtable;
        this.uiModel = uiModel;
        this.uiColumnIndex = uiColumnIndex;
        this.uiModelMapper = uiModelMapper;
        this.canvasOperation = canvasOperation;

        this.oldInputClause = dtable.getInput().get(uiColumnIndex - DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);
        this.oldColumnData = extractColumnData();
        this.oldUiModelColumn = uiModel.getColumns().get(uiColumnIndex);
        this.oldColumnWidths = CommandUtils.extractColumnWidths(uiModel);
    }

    private List<UnaryTests> extractColumnData() {
        final int clauseIndex = getInputClauseIndex();
        return dtable.getRule()
                .stream()
                .map(row -> row.getInputEntry().get(clauseIndex))
                .collect(Collectors.toList());
    }

    private int getInputClauseIndex() {
        return uiColumnIndex - DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT;
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler handler) {
        return new AbstractGraphCommand() {
            @Override
            protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext gce) {
                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext gce) {
                final int clauseIndex = getInputClauseIndex();
                dtable.getRule().forEach(row -> row.getInputEntry().remove(clauseIndex));
                dtable.getInput().remove(clauseIndex);

                return GraphCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext gce) {
                final int clauseIndex = getInputClauseIndex();
                dtable.getInput().add(clauseIndex,
                                      oldInputClause);
                IntStream.range(0, dtable.getRule().size())
                        .forEach(rowIndex -> {
                            final UnaryTests value = oldColumnData.get(rowIndex);
                            dtable.getRule().get(rowIndex).getInputEntry().add(clauseIndex, value);
                        });

                return GraphCommandResultBuilder.SUCCESS;
            }
        };
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler handler) {
        return new AbstractCanvasCommand() {
            @Override
            public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler handler) {
                //Deleting the GridColumn also deletes the underlying data
                final GridColumn<?> gridColumn = uiModel.getColumns().get(uiColumnIndex);
                uiModel.deleteColumn(gridColumn);

                updateParentInformation();

                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler handler) {
                //Need to manually setup the old data when the column is restored
                uiModel.insertColumn(uiColumnIndex,
                                     oldUiModelColumn);
                for (int rowIndex = 0; rowIndex < dtable.getRule().size(); rowIndex++) {
                    uiModelMapper.fromDMNModel(rowIndex,
                                               uiColumnIndex);
                }

                updateParentInformation();
                restoreColumnWidths();

                canvasOperation.execute();

                return CanvasCommandResultBuilder.SUCCESS;
            }
        };
    }

    public void updateParentInformation() {
        CommandUtils.updateParentInformation(uiModel);
    }

    public void restoreColumnWidths() {
        CommandUtils.restoreColumnWidths(uiModel, oldColumnWidths);
    }
}
