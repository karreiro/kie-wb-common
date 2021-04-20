/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import "./Table.css";
import {
  Column,
  ColumnInstance,
  ContextMenuEvent,
  DataRecord,
  useBlockLayout,
  useResizeColumns,
  useTable,
} from "react-table";
import { TableComposable } from "@patternfly/react-table";
import * as React from "react";
import { useCallback, useContext, useEffect, useRef, useState } from "react";
import { EditableCell } from "./EditableCell";
import { TableOperation, TableProps } from "../../api";
import * as _ from "lodash";
import { TableBody } from "./TableBody";
import { TableHandler } from "./TableHandler";
import { TableHeader } from "./TableHeader";
import { BoxedExpressionGlobalContext } from "../../context";

export const NO_TABLE_CONTEXT_MENU_CLASS = "no-table-context-menu";

export const Table: React.FunctionComponent<TableProps> = ({
  tableId,
  children,
  columnPrefix = "column-",
  editColumnLabel,
  onColumnsUpdate,
  onRowsUpdate,
  onRowAdding = () => ({}),
  defaultCell,
  rows,
  columns,
  handlerConfiguration,
  headerVisibility,
  headerLevels = 0,
  skipLastHeaderGroup = false,
  getRowKey = (row) => row.id as string,
  getColumnKey = (column) => column.id as string,
  resetRowCustomFunction,
}: TableProps) => {
  const NUMBER_OF_ROWS_COLUMN = "#";
  const NUMBER_OF_ROWS_SUBCOLUMN = "0";

  const tableRef = useRef<HTMLTableElement>(null);

  const globalContext = useContext(BoxedExpressionGlobalContext);

  const generateNumberOfRowsSubColumnRecursively: (column: ColumnInstance, headerLevels: number) => void = (
    column,
    headerLevels
  ) => {
    if (headerLevels > 0) {
      _.assign(column, {
        columns: [
          {
            label: NUMBER_OF_ROWS_SUBCOLUMN,
            accessor: NUMBER_OF_ROWS_SUBCOLUMN,
            minWidth: 60,
            width: 60,
            disableResizing: true,
            isCountColumn: true,
            hideFilter: true,
          },
        ],
      });

      generateNumberOfRowsSubColumnRecursively(column.columns[0], headerLevels - 1);
    }
  };

  const numberOfRowsColumn = {
    label: NUMBER_OF_ROWS_COLUMN,
    accessor: NUMBER_OF_ROWS_COLUMN,
    width: 60,
    minWidth: 60,
    isCountColumn: true,
  } as ColumnInstance;
  generateNumberOfRowsSubColumnRecursively(numberOfRowsColumn, headerLevels);
  const tableColumns = useRef<Column[]>([numberOfRowsColumn, ...columns]);
  const tableRows = useRef<DataRecord[]>(rows);
  const [showTableHandler, setShowTableHandler] = useState(false);
  const [tableHandlerTarget, setTableHandlerTarget] = useState(document.body);
  const [tableHandlerAllowedOperations, setTableHandlerAllowedOperations] = useState(
    _.values(TableOperation).map((operation) => parseInt(operation.toString()))
  );
  const [lastSelectedColumnIndex, setLastSelectedColumnIndex] = useState(-1);
  const [lastSelectedRowIndex, setLastSelectedRowIndex] = useState(-1);

  const onColumnsUpdateCallback = useCallback(
    (columns: Column[]) => {
      tableColumns.current = columns;
      onColumnsUpdate?.(columns.slice(1)); //Removing "# of rows" column
    },
    [onColumnsUpdate]
  );

  const onRowsUpdateCallback = useCallback(
    (rows: DataRecord[]) => {
      tableRows.current = rows;
      onRowsUpdate?.(rows);
    },
    [onRowsUpdate]
  );

  const onCellUpdate = useCallback(
    (rowIndex: number, columnId: string, value: string) => {
      const updatedTableCells = [...tableRows.current];
      updatedTableCells[rowIndex][columnId] = value;
      onRowsUpdateCallback(updatedTableCells);
    },
    [onRowsUpdateCallback]
  );

  const onRowUpdate = useCallback(
    (rowIndex: number, updatedRow: DataRecord) => {
      const updatedRows = [...tableRows.current];
      updatedRows[rowIndex] = updatedRow;
      onRowsUpdateCallback(updatedRows);
    },
    [onRowsUpdateCallback]
  );

  const defaultColumn = {
    minWidth: 150,
    width: 150,
    Cell: useCallback((cellRef) => {
      const column = cellRef.column as ColumnInstance;
      if (column.isCountColumn) {
        return cellRef.value;
      } else {
        return defaultCell ? defaultCell[column.id](cellRef) : EditableCell(cellRef);
      }
      // Table performance optimization: no need to re-render cells, since nested component themselves will re-render
      // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []),
  };

  const contextMenuIsAvailable = (target: HTMLElement) => {
    const targetIsContainedInCurrentTable = target.closest("table") === tableRef.current;
    const contextMenuAvailableForTarget = !target.classList.contains(NO_TABLE_CONTEXT_MENU_CLASS);
    return targetIsContainedInCurrentTable && contextMenuAvailableForTarget;
  };

  const tableHandlerStateUpdate = (target: HTMLElement, columnIndex: number) => {
    setTableHandlerTarget(target);
    globalContext.currentlyOpenedHandlerCallback?.(false);
    setShowTableHandler(true);
    globalContext.setCurrentlyOpenedHandlerCallback?.(() => setShowTableHandler);
    setLastSelectedColumnIndex(columnIndex);
  };

  const getThProps = (column: ColumnInstance, columnIndex: number) => ({
    onContextMenu: (e: ContextMenuEvent) => {
      const target = e.target as HTMLElement;
      const handlerOnHeaderIsAvailable = !column.disableHandlerOnHeader;
      if (contextMenuIsAvailable(target) && handlerOnHeaderIsAvailable) {
        e.preventDefault();
        setTableHandlerAllowedOperations([
          TableOperation.ColumnInsertLeft,
          TableOperation.ColumnInsertRight,
          ...(tableColumns.current.length > 2 && columnIndex > 0 ? [TableOperation.ColumnDelete] : []),
        ]);
        tableHandlerStateUpdate(target, columnIndex);
      }
    },
  });

  const getTdProps = (columnIndex: number, rowIndex: number) => ({
    onContextMenu: (e: ContextMenuEvent) => {
      const target = e.target as HTMLElement;
      if (contextMenuIsAvailable(target)) {
        e.preventDefault();
        setTableHandlerAllowedOperations([
          TableOperation.ColumnInsertLeft,
          TableOperation.ColumnInsertRight,
          ...(tableColumns.current.length > 2 && columnIndex > 0 ? [TableOperation.ColumnDelete] : []),
          TableOperation.RowInsertAbove,
          TableOperation.RowInsertBelow,
          ...(tableRows.current.length > 1 ? [TableOperation.RowDelete] : []),
          TableOperation.RowClear,
        ]);
        tableHandlerStateUpdate(target, columnIndex);
        setLastSelectedRowIndex(rowIndex);
      }
    },
  });

  const tableInstance = useTable(
    {
      columns: tableColumns.current,
      data: tableRows.current,
      defaultColumn,
      onCellUpdate,
      onRowUpdate,
      getThProps,
      getTdProps,
    },
    useBlockLayout,
    useResizeColumns
  );

  const resizeNestedColumns = (columns: ColumnInstance[], accessor: string, updatedWidth: number) => {
    const columnIndex = _.findIndex(columns, { accessor });
    if (columnIndex >= 0) {
      const updatedColumn = { ...columns[columnIndex] };
      updatedColumn.width = updatedWidth;
      columns.splice(columnIndex, 1, updatedColumn);
    } else {
      _.forEach(columns, (column) => resizeNestedColumns(column.columns, accessor, updatedWidth));
    }
  };

  const finishedResizing =
    tableInstance.state.columnResizing.isResizingColumn === null &&
    !_.isEmpty(tableInstance.state.columnResizing.columnWidths);
  useEffect(() => {
    if (finishedResizing) {
      _.forEach(tableInstance.state.columnResizing.columnWidths, (updatedColumnWidth, accessor) =>
        resizeNestedColumns(tableColumns.current as ColumnInstance[], accessor, updatedColumnWidth)
      );
      onColumnsUpdateCallback(tableColumns.current);
    }
    // Need to consider a change only when resizing is finished (no other dependencies to consider for this useEffect)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [finishedResizing]);

  return (
    <div className={`table-component ${tableId}`}>
      <TableComposable variant="compact" {...tableInstance.getTableProps()} ref={tableRef}>
        <TableHeader
          tableInstance={tableInstance}
          editColumnLabel={editColumnLabel}
          headerVisibility={headerVisibility}
          skipLastHeaderGroup={skipLastHeaderGroup}
          tableRows={tableRows}
          onRowsUpdate={onRowsUpdateCallback}
          tableColumns={tableColumns}
          getColumnKey={getColumnKey}
          onColumnsUpdate={onColumnsUpdateCallback}
        />
        <TableBody
          tableInstance={tableInstance}
          getRowKey={getRowKey}
          getColumnKey={getColumnKey}
          headerVisibility={headerVisibility}
        >
          {children}
        </TableBody>
      </TableComposable>
      {showTableHandler ? (
        <TableHandler
          tableColumns={tableColumns}
          columnPrefix={columnPrefix}
          handlerConfiguration={handlerConfiguration}
          lastSelectedColumnIndex={lastSelectedColumnIndex}
          lastSelectedRowIndex={lastSelectedRowIndex}
          tableRows={tableRows}
          onRowsUpdate={onRowsUpdateCallback}
          onRowAdding={onRowAdding}
          showTableHandler={showTableHandler}
          setShowTableHandler={setShowTableHandler}
          tableHandlerAllowedOperations={tableHandlerAllowedOperations}
          tableHandlerTarget={tableHandlerTarget}
          resetRowCustomFunction={resetRowCustomFunction}
          onColumnsUpdate={onColumnsUpdateCallback}
        />
      ) : null}
    </div>
  );
};