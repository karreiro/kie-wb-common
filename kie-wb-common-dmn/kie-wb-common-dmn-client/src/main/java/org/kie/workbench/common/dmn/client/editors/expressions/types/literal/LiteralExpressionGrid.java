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

package org.kie.workbench.common.dmn.client.editors.expressions.types.literal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.event.Event;

import com.ait.lienzo.shared.core.types.EventPropagationMode;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

public class LiteralExpressionGrid extends BaseExpressionGrid<LiteralExpression, LiteralExpressionUIModelMapper> implements HasListSelectorControl {

    public static final double PADDING = 0.0;

    private final ListSelectorView.Presenter listSelector;

    public LiteralExpressionGrid(final GridCellTuple parent,
                                 final HasExpression hasExpression,
                                 final Optional<LiteralExpression> expression,
                                 final Optional<HasName> hasName,
                                 final DMNGridPanel gridPanel,
                                 final DMNGridLayer gridLayer,
                                 final SessionManager sessionManager,
                                 final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                 final Event<ExpressionEditorChanged> editorSelectedEvent,
                                 final CellEditorControlsView.Presenter cellEditorControls,
                                 final TranslationService translationService,
                                 final ListSelectorView.Presenter listSelector,
                                 final boolean isNested) {
        super(parent,
              hasExpression,
              expression,
              hasName,
              gridPanel,
              gridLayer,
              new LiteralExpressionGridRenderer(isNested),
              sessionManager,
              sessionCommandManager,
              editorSelectedEvent,
              cellEditorControls,
              translationService,
              isNested);
        this.listSelector = listSelector;

        setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

        super.doInitialisation();
    }

    @Override
    protected void doInitialisation() {
        // Defer initialisation until after the constructor completes as
        // LiteralExpressionUIModelMapper needs ListSelector to have been set
    }

    @Override
    public LiteralExpressionUIModelMapper makeUiModelMapper() {
        return new LiteralExpressionUIModelMapper(this::getModel,
                                                  () -> expression,
                                                  listSelector);
    }

    @Override
    protected void initialiseUiColumns() {
        final TextAreaSingletonDOMElementFactory factory = new TextAreaSingletonDOMElementFactory(gridPanel,
                                                                                                  gridLayer,
                                                                                                  this,
                                                                                                  sessionManager,
                                                                                                  sessionCommandManager,
                                                                                                  newCellHasNoValueCommand(),
                                                                                                  newCellHasValueCommand());
        final TextBoxSingletonDOMElementFactory headerFactory = new TextBoxSingletonDOMElementFactory(gridPanel,
                                                                                                      gridLayer,
                                                                                                      this,
                                                                                                      sessionManager,
                                                                                                      sessionCommandManager,
                                                                                                      newHeaderHasNoValueCommand(),
                                                                                                      newHeaderHasValueCommand());
        final GridColumn literalExpressionColumn = new LiteralExpressionColumn(new LiteralExpressionColumnHeaderMetaData(() -> hasName.orElse(HasName.NOP).getName().getValue(),
                                                                                                                         (s) -> hasName.orElse(HasName.NOP).getName().setValue(s),
                                                                                                                         headerFactory),
                                                                               factory,
                                                                               this);

        model.appendColumn(literalExpressionColumn);
    }

    @Override
    protected void initialiseUiModel() {
        expression.ifPresent(e -> {
            model.appendRow(new DMNGridRow());
            uiModelMapper.fromDMNModel(0,
                                       0);
        });
    }

    @Override
    public double getPadding() {
        return findParentGrid().isPresent() ? PADDING : DEFAULT_PADDING;
    }

    @Override
    @SuppressWarnings("unused")
    public List<ListSelectorItem> getItems(final int uiRowIndex,
                                           final int uiColumnIndex) {
        final List<ListSelectorItem> items = new ArrayList<>();
        final Optional<BaseExpressionGrid> parent = findParentGrid();
        parent.ifPresent(grid -> {
            if (grid instanceof HasListSelectorControl) {
                final int parentUiRowIndex = getParentInformation().getRowIndex();
                final int parentUiColumnIndex = getParentInformation().getColumnIndex();
                final GridCell<?> parentCell = getParentInformation().getGridWidget().getModel().getCell(parentUiRowIndex, parentUiColumnIndex);

                if (parentCell instanceof HasCellEditorControls) {
                    final List<ListSelectorItem> parentItems = ((HasListSelectorControl) grid).getItems(parentUiRowIndex,
                                                                                                        parentUiColumnIndex);
                    if (!parentItems.isEmpty()) {
                        items.addAll(parentItems);
                    }
                }
            }
        });

        return items;
    }

    @Override
    public void onItemSelected(final ListSelectorItem item) {
        final ListSelectorTextItem li = (ListSelectorTextItem) item;
        li.getCommand().execute();
    }
}
