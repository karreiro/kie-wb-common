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

package org.kie.workbench.common.dmn.client.commands.expressions.types.dtable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableDefaultValueUtilities;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapperHelper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.InputClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.OutputClauseColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AddOutputClauseCommandTest {

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private OutputClauseColumn uiOutputClauseColumn;

    @Mock
    private InputClauseColumn uiInputClauseColumn;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphCommandExecutionContext;

    private DecisionTable dtable;

    private OutputClause outputClause;

    private GridData uiModel;

    private DecisionTableUIModelMapper uiModelMapper;

    private AddOutputClauseCommand command;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    @Before
    public void setUp() throws Exception {
        this.dtable = new DecisionTable();
        this.uiModel = new DMNGridData();
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.outputClause = new OutputClause();
        this.uiModelMapper = new DecisionTableUIModelMapper(() -> uiModel,
                                                            () -> Optional.of(dtable),
                                                            listSelector);

        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiOutputClauseColumn).getIndex();
    }

    private void makeCommand(final int index) {
        this.command = spy(new AddOutputClauseCommand(dtable,
                                                      outputClause,
                                                      uiModel,
                                                      uiOutputClauseColumn,
                                                      index,
                                                      uiModelMapper,
                                                      canvasOperation));
    }

    @Test
    public void testGraphCommandAllow() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.allow(graphCommandExecutionContext));
    }

    @Test
    public void testGraphCommandCheck() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.allow(graphCommandExecutionContext));
    }

    @Test
    public void testGraphCommandExecute() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        dtable.getRule().add(new DecisionRule());
        dtable.getRule().add(new DecisionRule());
        assertEquals(0, dtable.getOutput().size());

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        // one new output column
        assertEquals(1, dtable.getOutput().size());
        assertEquals(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_PREFIX + "1",
                     dtable.getOutput().get(0).getName());

        // first rule
        final List<LiteralExpression> outputEntriesRuleOne = dtable.getRule().get(0).getOutputEntry();
        assertEquals(1, outputEntriesRuleOne.size());
        assertEquals(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT, outputEntriesRuleOne.get(0).getText());
        assertEquals(dtable.getRule().get(0), outputEntriesRuleOne.get(0).getParent());

        // second rule
        final List<LiteralExpression> outputEntriesRuleTwo = dtable.getRule().get(1).getOutputEntry();
        assertEquals(1, outputEntriesRuleTwo.size());
        assertEquals(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT, outputEntriesRuleTwo.get(0).getText());
        assertEquals(dtable.getRule().get(1), outputEntriesRuleTwo.get(0).getParent());

        assertEquals(dtable,
                     outputClause.getParent());
    }

    @Test
    public void testGraphCommandExecuteExistingNotAffected() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        final String ruleOneOldOutput = "old rule 1";
        final String ruleTwoOldOutput = "old rule 2";

        dtable.getOutput().add(new OutputClause());
        addRuleWithOutputClauseValues(ruleOneOldOutput);
        addRuleWithOutputClauseValues(ruleTwoOldOutput);

        assertEquals(1, dtable.getOutput().size());

        //Graph command will insert new OutputClause at index 0 of the OutputEntries
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertEquals(2, dtable.getOutput().size());
        assertEquals(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_PREFIX + "1",
                     dtable.getOutput().get(0).getName());
        assertEquals("",
                     dtable.getOutput().get(1).getName());

        // first rule
        final List<LiteralExpression> outputEntriesRuleOne = dtable.getRule().get(0).getOutputEntry();
        assertEquals(2, outputEntriesRuleOne.size());
        assertEquals(ruleOneOldOutput, outputEntriesRuleOne.get(1).getText());
        assertEquals(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT, outputEntriesRuleOne.get(0).getText());
        assertEquals(dtable.getRule().get(0), outputEntriesRuleOne.get(0).getParent());

        // second rule
        final List<LiteralExpression> outputEntriesRuleTwo = dtable.getRule().get(1).getOutputEntry();
        assertEquals(2, outputEntriesRuleTwo.size());
        assertEquals(ruleTwoOldOutput, outputEntriesRuleTwo.get(1).getText());
        assertEquals(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT, outputEntriesRuleTwo.get(0).getText());
        assertEquals(dtable.getRule().get(1), outputEntriesRuleTwo.get(0).getParent());

        assertEquals(dtable,
                     outputClause.getParent());
    }

    @Test
    public void testGraphCommandExecuteInsertMiddle() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT + 1);

        final String ruleOutputOne = "rule out 1";
        final String ruleOutputTwo = "rule out 2";

        dtable.getOutput().add(new OutputClause());
        dtable.getOutput().add(new OutputClause());
        addRuleWithOutputClauseValues(ruleOutputOne, ruleOutputTwo);

        assertEquals(2, dtable.getOutput().size());

        //Graph command will insert new OutputClause at index 1 of the OutputEntries
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertEquals(3, dtable.getOutput().size());
        assertEquals("",
                     dtable.getOutput().get(0).getName());
        assertEquals(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_PREFIX + "1",
                     dtable.getOutput().get(1).getName());
        assertEquals("",
                     dtable.getOutput().get(2).getName());

        final List<LiteralExpression> ruleOutputs = dtable.getRule().get(0).getOutputEntry();

        // first rule
        assertEquals(3, ruleOutputs.size());
        assertEquals(ruleOutputOne, ruleOutputs.get(0).getText());
        assertEquals(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT, ruleOutputs.get(1).getText());
        assertEquals(dtable.getRule().get(0), ruleOutputs.get(1).getParent());
        assertEquals(ruleOutputTwo, ruleOutputs.get(2).getText());

        assertEquals(dtable,
                     outputClause.getParent());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testGraphCommandUndoNoOutputClauseColumns() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        dtable.getRule().add(new DecisionRule());

        assertEquals(0, dtable.getOutput().size());

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.undo(graphCommandExecutionContext));
    }

    @Test
    public void testGraphCommandUndoJustLastOutputClauseColumn() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        final String ruleOneOldOutput = "old rule 1";
        final String ruleTwoOldOutput = "old rule 2";

        dtable.getOutput().add(new OutputClause());
        addRuleWithOutputClauseValues(ruleOneOldOutput);
        addRuleWithOutputClauseValues(ruleTwoOldOutput);

        assertEquals(1, dtable.getOutput().size());

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.undo(graphCommandExecutionContext));

        assertEquals(1, dtable.getOutput().size());

        // first rule
        assertEquals(1, dtable.getRule().get(0).getOutputEntry().size());
        assertEquals(ruleOneOldOutput, dtable.getRule().get(0).getOutputEntry().get(0).getText());

        // second rule
        assertEquals(1, dtable.getRule().get(1).getOutputEntry().size());
        assertEquals(ruleTwoOldOutput, dtable.getRule().get(1).getOutputEntry().get(0).getText());
    }

    @Test
    public void testCanvasCommandAllow() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        final Command<AbstractCanvasHandler, CanvasViolation> canvasCommand = command.newCanvasCommand(canvasHandler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.allow(canvasHandler));
    }

    @Test
    public void testCanvasCommandAddOutputClauseToRuleWithInputs() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT + 1);

        final String ruleInputValue = "in value";
        final String ruleOutputValue = "out value";

        dtable.getInput().add(new InputClause());
        dtable.getRule().add(new DecisionRule() {{
            getInputEntry().add(new UnaryTests() {{
                setText(ruleInputValue);
            }});
            getOutputEntry().add(new LiteralExpression() {{
                setText(ruleOutputValue);
            }});
        }});

        //Graph command populates OutputEntries so overwrite with test values
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);
        graphCommand.execute(graphCommandExecutionContext);
        dtable.getRule().get(0).getOutputEntry().get(0).setText(ruleOutputValue);

        doReturn(1).when(uiInputClauseColumn).getIndex();
        doReturn(2).when(uiOutputClauseColumn).getIndex();
        uiModel.appendColumn(uiInputClauseColumn);
        uiModel.appendRow(new BaseGridRow());

        uiModelMapper.fromDMNModel(0, 1);

        final Command<AbstractCanvasHandler, CanvasViolation> canvasAddOutputClauseCommand = command.newCanvasCommand(canvasHandler);
        canvasAddOutputClauseCommand.execute(canvasHandler);

        assertEquals(ruleInputValue, uiModel.getRow(0).getCells().get(1).getValue().getValue());
        assertEquals(ruleOutputValue, uiModel.getRow(0).getCells().get(2).getValue().getValue());

        assertEquals(3, uiModel.getColumnCount());
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasAddOutputClauseCommand.undo(canvasHandler));
        assertEquals(2, uiModel.getColumnCount());

        // one time in execute(), one time in undo()
        verify(canvasOperation, times(2)).execute();
        verify(command, times(2)).updateParentInformation();
    }

    @Test
    public void testCanvasCommandAddOutputClauseToRuleWithoutInputsThenUndo() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        final String ruleOneOutputValue = "one";
        final String ruleTwoOutputValue = "two";

        dtable.getRule().add(new DecisionRule());
        dtable.getRule().add(new DecisionRule());
        uiModel.appendRow(new BaseGridRow());
        uiModel.appendRow(new BaseGridRow());

        //Graph command populates OutputEntries so overwrite with test values
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);
        graphCommand.execute(graphCommandExecutionContext);
        dtable.getRule().get(0).getOutputEntry().get(0).setText(ruleOneOutputValue);
        dtable.getRule().get(1).getOutputEntry().get(0).setText(ruleTwoOutputValue);

        final Command<AbstractCanvasHandler, CanvasViolation> canvasAddOutputClauseCommand = command.newCanvasCommand(canvasHandler);
        canvasAddOutputClauseCommand.execute(canvasHandler);

        // first rule
        assertEquals(ruleOneOutputValue, uiModel.getRow(0).getCells().get(1).getValue().getValue());

        // second rule
        assertEquals(ruleTwoOutputValue, uiModel.getRow(1).getCells().get(1).getValue().getValue());

        assertEquals(2, uiModel.getColumnCount());
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasAddOutputClauseCommand.undo(canvasHandler));
        assertEquals(1, uiModel.getColumnCount());

        // one time in execute(), one time in undo()
        verify(canvasOperation, times(2)).execute();
        verify(command, times(2)).updateParentInformation();
    }

    @Test
    public void testCanvasCommandUndoWhenNothingBefore() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        final Command<AbstractCanvasHandler, CanvasViolation> canvasAddOutputClauseCommand = command.newCanvasCommand(canvasHandler);

        canvasAddOutputClauseCommand.undo(canvasHandler);
        // just row number column
        assertEquals(1, uiModel.getColumnCount());

        verify(canvasOperation).execute();
        verify(command).updateParentInformation();
    }

    private void addRuleWithOutputClauseValues(String... outputClauseValues) {
        dtable.getRule().add(new DecisionRule() {{
            Stream.of(outputClauseValues).forEach(oClause -> {
                getOutputEntry().add(new LiteralExpression() {{
                    setText(oClause);
                }});
            });
        }});
    }
}
