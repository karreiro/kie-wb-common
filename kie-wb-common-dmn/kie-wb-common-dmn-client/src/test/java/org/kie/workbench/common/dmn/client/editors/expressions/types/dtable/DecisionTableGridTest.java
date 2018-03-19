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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.v1_1.HitPolicy;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddDecisionRuleCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddInputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.AddOutputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.DeleteDecisionRuleCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.DeleteInputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.DeleteOutputClauseCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.SetBuiltinAggregatorCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.SetHitPolicyCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.dtable.SetOrientationCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyEditorView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DecisionTableGridTest {

    private final static int DEFAULT_INSERT_RULE_ABOVE = 0;

    private final static int DEFAULT_INSERT_RULE_BELOW = 1;

    private final static int DEFAULT_DELETE_RULE = 2;

    private final static int INSERT_COLUMN_BEFORE = 0;

    private final static int INSERT_COLUMN_AFTER = 1;

    private final static int DELETE_COLUMN = 2;

    private final static int DIVIDER = 3;

    private static final String HASNAME_NAME = "name";

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private ClientSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphCommandContext;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private Supplier<ExpressionEditorDefinitions> supplementaryEditorDefinitionsSupplier;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private TranslationService translationService;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private HitPolicyEditorView.Presenter hitPolicyEditor;

    @Mock
    private GridCellTuple parent;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private Command command;

    @Mock
    private Event<ExpressionEditorChanged> editorSelectedEvent;

    @Captor
    private ArgumentCaptor<AddInputClauseCommand> addInputClauseCommandCaptor;

    @Captor
    private ArgumentCaptor<DeleteInputClauseCommand> deleteInputClauseCommandCaptor;

    @Captor
    private ArgumentCaptor<AddOutputClauseCommand> addOutputClauseCommandCaptor;

    @Captor
    private ArgumentCaptor<DeleteOutputClauseCommand> deleteOutputClauseCommandCaptor;

    @Captor
    private ArgumentCaptor<AddDecisionRuleCommand> addDecisionRuleCommandCaptor;

    @Captor
    private ArgumentCaptor<DeleteDecisionRuleCommand> deleteDecisionRuleCommandCaptor;

    @Captor
    private ArgumentCaptor<CompositeCommand<AbstractCanvasHandler, CanvasViolation>> setHitPolicyCommandCaptor;

    @Captor
    private ArgumentCaptor<SetBuiltinAggregatorCommand> setBuiltInAggregatorCommandCaptor;

    @Captor
    private ArgumentCaptor<SetOrientationCommand> setOrientationCommandCaptor;

    private DecisionTableEditorDefinition definition;

    private Optional<DecisionTable> expression;

    private DecisionTableGrid grid;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.definition = new DecisionTableEditorDefinition(gridPanel,
                                                            gridLayer,
                                                            sessionManager,
                                                            sessionCommandManager,
                                                            editorSelectedEvent,
                                                            cellEditorControls,
                                                            translationService,
                                                            listSelector,
                                                            hitPolicyEditor);

        expression = definition.getModelClass();

        doReturn(session).when(sessionManager).getCurrentSession();
        doReturn(canvasHandler).when(session).getCanvasHandler();
        doReturn(graphCommandContext).when(canvasHandler).getGraphExecutionContext();

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).format(anyString());
    }

    private void setupGrid(final Optional<HasName> hasName) {
        this.grid = spy((DecisionTableGrid) definition.getEditor(parent,
                                                                 hasExpression,
                                                                 expression,
                                                                 hasName,
                                                                 false).get());
    }

    private Optional<HasName> makeHasNameForDecision() {
        final Decision decision = new Decision();
        decision.setName(new Name(HASNAME_NAME));
        return Optional.of(decision);
    }

    @Test
    public void testInitialSetupFromDefinition() {
        setupGrid(makeHasNameForDecision());

        final GridData uiModel = grid.getModel();
        assertTrue(uiModel instanceof DecisionTableGridData);

        assertEquals(4,
                     uiModel.getColumnCount());
        assertTrue(uiModel.getColumns().get(0) instanceof DecisionTableRowNumberColumn);
        assertTrue(uiModel.getColumns().get(1) instanceof InputClauseColumn);
        assertTrue(uiModel.getColumns().get(2) instanceof OutputClauseColumn);
        assertTrue(uiModel.getColumns().get(3) instanceof DescriptionColumn);

        assertEquals(1,
                     uiModel.getRowCount());

        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals(DecisionTableEditorDefinition.INPUT_CLAUSE_UNARY_TEST_TEXT,
                     uiModel.getCell(0, 1).getValue().getValue());
        assertEquals(DecisionTableEditorDefinition.OUTPUT_CLAUSE_EXPRESSION_TEXT,
                     uiModel.getCell(0, 2).getValue().getValue());
        assertEquals(DecisionTableEditorDefinition.RULE_DESCRIPTION,
                     uiModel.getCell(0, 3).getValue().getValue());
    }

    @Test
    public void testColumn0MetaData() {
        setupGrid(makeHasNameForDecision());

        final GridColumn<?> column = grid.getModel().getColumns().get(0);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(1,
                     header.size());
        assertTrue(header.get(0) instanceof RowNumberColumnHeaderMetaData);

        final RowNumberColumnHeaderMetaData md = (RowNumberColumnHeaderMetaData) header.get(0);
        expression.get().setHitPolicy(HitPolicy.FIRST);
        assertEquals("F",
                     md.getTitle());

        expression.get().setHitPolicy(HitPolicy.ANY);
        assertEquals("A",
                     md.getTitle());
    }

    @Test
    public void testColumn1MetaData() {
        setupGrid(makeHasNameForDecision());

        final GridColumn<?> column = grid.getModel().getColumns().get(1);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(1,
                     header.size());
        assertTrue(header.get(0) instanceof InputClauseColumnHeaderMetaData);

        final InputClauseColumnHeaderMetaData md = (InputClauseColumnHeaderMetaData) header.get(0);
        assertEquals(DecisionTableEditorDefinition.INPUT_CLAUSE_EXPRESSION_TEXT,
                     md.getTitle());
    }

    @Test
    public void testColumn2MetaData() {
        setupGrid(makeHasNameForDecision());

        final GridColumn<?> column = grid.getModel().getColumns().get(2);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(1,
                     header.size());
        assertTrue(header.get(0) instanceof OutputClauseColumnExpressionNameHeaderMetaData);

        final OutputClauseColumnExpressionNameHeaderMetaData md = (OutputClauseColumnExpressionNameHeaderMetaData) header.get(0);
        assertEquals(HASNAME_NAME,
                     md.getTitle());
    }

    @Test
    public void testColumn2MetaDataWithoutHasName() {
        setupGrid(Optional.empty());

        final GridColumn<?> column = grid.getModel().getColumns().get(2);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(1,
                     header.size());
        assertTrue(header.get(0) instanceof BaseHeaderMetaData);

        final BaseHeaderMetaData md = (BaseHeaderMetaData) header.get(0);
        assertEquals(DMNEditorConstants.DecisionTableEditor_OutputClauseHeader,
                     md.getTitle());
    }

    @Test
    public void testColumn3MetaData() {
        setupGrid(makeHasNameForDecision());

        final GridColumn<?> column = grid.getModel().getColumns().get(3);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(1,
                     header.size());
        assertTrue(header.get(0) instanceof BaseHeaderMetaData);

        final BaseHeaderMetaData md = (BaseHeaderMetaData) header.get(0);
        assertEquals(DMNEditorConstants.DecisionTableEditor_DescriptionColumnHeader,
                     md.getTitle());
    }

    @Test
    public void testGetItemsRowNumberColumn() {
        setupGrid(makeHasNameForDecision());

        assertDefaultListItems(grid.getItems(0, 0));
    }

    @Test
    public void testGetItemsInputClauseColumn() {
        setupGrid(makeHasNameForDecision());

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 1);

        assertThat(items.size()).isEqualTo(7);
        assertDefaultListItems(items.subList(4, 7));

        assertListSelectorItem(items.get(INSERT_COLUMN_BEFORE),
                               DMNEditorConstants.DecisionTableEditor_InsertInputClauseBefore);
        assertListSelectorItem(items.get(INSERT_COLUMN_AFTER),
                               DMNEditorConstants.DecisionTableEditor_InsertInputClauseAfter);
        assertListSelectorItem(items.get(DELETE_COLUMN),
                               DMNEditorConstants.DecisionTableEditor_DeleteInputClause);
        assertThat(items.get(DIVIDER)).isInstanceOf(HasListSelectorControl.ListSelectorDividerItem.class);

        grid.onItemSelected(items.get(INSERT_COLUMN_BEFORE));
        verify(grid).addInputClause(eq(1));

        grid.onItemSelected(items.get(INSERT_COLUMN_AFTER));
        verify(grid).addInputClause(eq(2));

        grid.onItemSelected(items.get(DELETE_COLUMN));
        verify(grid).deleteInputClause(eq(1));
    }

    @Test
    public void testGetItemsOutputClauseColumn() {
        setupGrid(makeHasNameForDecision());

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 2);

        assertThat(items.size()).isEqualTo(7);
        assertDefaultListItems(items.subList(4, 7));

        assertListSelectorItem(items.get(INSERT_COLUMN_BEFORE),
                               DMNEditorConstants.DecisionTableEditor_InsertOutputClauseBefore);
        assertListSelectorItem(items.get(INSERT_COLUMN_AFTER),
                               DMNEditorConstants.DecisionTableEditor_InsertOutputClauseAfter);
        assertListSelectorItem(items.get(DELETE_COLUMN),
                               DMNEditorConstants.DecisionTableEditor_DeleteOutputClause);
        assertThat(items.get(DIVIDER)).isInstanceOf(HasListSelectorControl.ListSelectorDividerItem.class);

        grid.onItemSelected(items.get(INSERT_COLUMN_BEFORE));
        verify(grid).addOutputClause(eq(2));

        grid.onItemSelected(items.get(INSERT_COLUMN_AFTER));
        verify(grid).addOutputClause(eq(3));

        grid.onItemSelected(items.get(DELETE_COLUMN));
        verify(grid).deleteOutputClause(eq(2));
    }

    @Test
    public void testGetItemsDescriptionColumn() {
        setupGrid(makeHasNameForDecision());

        assertDefaultListItems(grid.getItems(0, 3));
    }

    private void assertDefaultListItems(final List<HasListSelectorControl.ListSelectorItem> items) {
        assertThat(items.size()).isEqualTo(3);
        assertListSelectorItem(items.get(DEFAULT_INSERT_RULE_ABOVE),
                               DMNEditorConstants.DecisionTableEditor_InsertDecisionRuleAbove);
        assertListSelectorItem(items.get(DEFAULT_INSERT_RULE_BELOW),
                               DMNEditorConstants.DecisionTableEditor_InsertDecisionRuleBelow);
        assertListSelectorItem(items.get(DEFAULT_DELETE_RULE),
                               DMNEditorConstants.DecisionTableEditor_DeleteDecisionRule);
    }

    private void assertListSelectorItem(final HasListSelectorControl.ListSelectorItem item,
                                        final String text) {
        assertThat(item).isInstanceOf(HasListSelectorControl.ListSelectorTextItem.class);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) item;
        assertThat(ti.getText()).isEqualTo(text);
    }

    @Test
    public void testOnItemSelected() {
        setupGrid(makeHasNameForDecision());

        final Command command = mock(Command.class);
        final HasListSelectorControl.ListSelectorTextItem listSelectorItem = mock(HasListSelectorControl.ListSelectorTextItem.class);
        when(listSelectorItem.getCommand()).thenReturn(command);

        grid.onItemSelected(listSelectorItem);

        verify(command).execute();
    }

    @Test
    public void testOnItemSelectedInsertRowAbove() {
        setupGrid(makeHasNameForDecision());

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(DEFAULT_INSERT_RULE_ABOVE);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).addDecisionRule(eq(0));
    }

    @Test
    public void testOnItemSelectedInsertRowBelow() {
        setupGrid(makeHasNameForDecision());

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(DEFAULT_INSERT_RULE_BELOW);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).addDecisionRule(eq(1));
    }

    @Test
    public void testOnItemSelectedDeleteRow() {
        setupGrid(makeHasNameForDecision());

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(DEFAULT_DELETE_RULE);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).deleteDecisionRule(eq(0));
    }

    @Test
    public void testAddInputClause() {
        setupGrid(makeHasNameForDecision());

        grid.addInputClause(1);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              addInputClauseCommandCaptor.capture());

        final AddInputClauseCommand addInputClauseCommand = addInputClauseCommandCaptor.getValue();
        addInputClauseCommand.execute(canvasHandler);

        verify(parent).assertWidth(eq(grid.getWidth() + grid.getPadding() * 2));
        verifyGridPanelRefresh();
    }

    @Test
    public void testDeleteInputClause() {
        setupGrid(makeHasNameForDecision());

        grid.deleteInputClause(1);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              deleteInputClauseCommandCaptor.capture());

        final DeleteInputClauseCommand deleteInputClauseCommand = deleteInputClauseCommandCaptor.getValue();
        deleteInputClauseCommand.execute(canvasHandler);

        verify(parent).assertWidth(eq(grid.getWidth() + grid.getPadding() * 2));
        verifyGridPanelRefresh();
    }

    @Test
    public void testAddOutputClause() {
        setupGrid(makeHasNameForDecision());

        grid.addOutputClause(2);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              addOutputClauseCommandCaptor.capture());

        final AddOutputClauseCommand addOutputClauseCommand = addOutputClauseCommandCaptor.getValue();
        addOutputClauseCommand.execute(canvasHandler);

        verify(parent).assertWidth(eq(grid.getWidth() + grid.getPadding() * 2));
        verifyGridPanelRefresh();
    }

    @Test
    public void testDeleteOutputClause() {
        setupGrid(makeHasNameForDecision());

        grid.deleteOutputClause(2);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              deleteOutputClauseCommandCaptor.capture());

        final DeleteOutputClauseCommand deleteOutputClauseCommand = deleteOutputClauseCommandCaptor.getValue();
        deleteOutputClauseCommand.execute(canvasHandler);

        verify(parent).assertWidth(eq(grid.getWidth() + grid.getPadding() * 2));
        verifyGridPanelRefresh();
    }

    @Test
    public void testAddDecisionRule() {
        setupGrid(makeHasNameForDecision());

        grid.addDecisionRule(0);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              addDecisionRuleCommandCaptor.capture());

        final AddDecisionRuleCommand addDecisionRuleCommand = addDecisionRuleCommandCaptor.getValue();
        addDecisionRuleCommand.execute(canvasHandler);

        verifyGridPanelRefresh();
    }

    @Test
    public void testDeleteDecisionRule() {
        setupGrid(makeHasNameForDecision());

        grid.deleteDecisionRule(0);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              deleteDecisionRuleCommandCaptor.capture());

        final DeleteDecisionRuleCommand deleteDecisionRuleCommand = deleteDecisionRuleCommandCaptor.getValue();
        deleteDecisionRuleCommand.execute(canvasHandler);

        verifyGridPanelRefresh();
    }

    @Test
    public void testSetHitPolicy() {
        final HitPolicy hitPolicy = HitPolicy.ANY;

        setupGrid(makeHasNameForDecision());

        grid.setHitPolicy(hitPolicy,
                          command);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              setHitPolicyCommandCaptor.capture());

        final CompositeCommand<AbstractCanvasHandler, CanvasViolation> setHitPolicyCommand = setHitPolicyCommandCaptor.getValue();
        assertEquals(1,
                     setHitPolicyCommand.getCommands().size());
        assertTrue(setHitPolicyCommand.getCommands().get(0) instanceof SetHitPolicyCommand);

        setHitPolicyCommand.execute(canvasHandler);

        verify(gridLayer, atLeast(1)).batch();
        verify(command).execute();
    }

    @Test
    public void testSetHitPolicyRequiresBuiltInAggregator() {
        final HitPolicy hitPolicy = HitPolicy.COLLECT;
        final BuiltinAggregator aggregator = BuiltinAggregator.SUM;

        setupGrid(makeHasNameForDecision());

        expression.get().setAggregation(aggregator);

        grid.setHitPolicy(hitPolicy,
                          command);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              setHitPolicyCommandCaptor.capture());

        final CompositeCommand<AbstractCanvasHandler, CanvasViolation> setHitPolicyCommand = setHitPolicyCommandCaptor.getValue();
        assertEquals(2,
                     setHitPolicyCommand.getCommands().size());
        assertTrue(setHitPolicyCommand.getCommands().get(0) instanceof SetBuiltinAggregatorCommand);
        assertTrue(setHitPolicyCommand.getCommands().get(1) instanceof SetHitPolicyCommand);

        setHitPolicyCommand.execute(canvasHandler);

        assertEquals(hitPolicy,
                     expression.get().getHitPolicy());
        assertEquals(aggregator,
                     expression.get().getAggregation());
    }

    @Test
    public void testSetHitPolicyRequiresBuiltInAggregatorUseDefaultWhenNotSet() {
        final HitPolicy hitPolicy = HitPolicy.COLLECT;

        setupGrid(makeHasNameForDecision());

        grid.setHitPolicy(hitPolicy,
                          command);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              setHitPolicyCommandCaptor.capture());

        final CompositeCommand<AbstractCanvasHandler, CanvasViolation> setHitPolicyCommand = setHitPolicyCommandCaptor.getValue();
        assertEquals(2,
                     setHitPolicyCommand.getCommands().size());
        assertTrue(setHitPolicyCommand.getCommands().get(0) instanceof SetBuiltinAggregatorCommand);
        assertTrue(setHitPolicyCommand.getCommands().get(1) instanceof SetHitPolicyCommand);

        setHitPolicyCommand.execute(canvasHandler);

        assertEquals(hitPolicy,
                     expression.get().getHitPolicy());
        assertEquals(DecisionTableGrid.DEFAULT_AGGREGATOR,
                     expression.get().getAggregation());
    }

    @Test
    public void testSetBuiltInAggregator() {
        final BuiltinAggregator aggregator = BuiltinAggregator.SUM;

        setupGrid(makeHasNameForDecision());

        grid.setBuiltinAggregator(aggregator);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              setBuiltInAggregatorCommandCaptor.capture());

        final SetBuiltinAggregatorCommand setBuiltinAggregatorCommand = setBuiltInAggregatorCommandCaptor.getValue();
        setBuiltinAggregatorCommand.execute(canvasHandler);

        verify(gridLayer).batch();
    }

    @Test
    public void testSetDecisionTableOrientation() {
        final DecisionTableOrientation orientation = DecisionTableOrientation.RULE_AS_ROW;

        setupGrid(makeHasNameForDecision());

        grid.setDecisionTableOrientation(orientation);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              setOrientationCommandCaptor.capture());

        final SetOrientationCommand setOrientationCommand = setOrientationCommandCaptor.getValue();
        setOrientationCommand.execute(canvasHandler);

        verify(gridLayer).batch();
    }

    private void verifyGridPanelRefresh() {
        verify(gridLayer).batch(any(GridLayerRedrawManager.PrioritizedCommand.class));
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
    }
}
