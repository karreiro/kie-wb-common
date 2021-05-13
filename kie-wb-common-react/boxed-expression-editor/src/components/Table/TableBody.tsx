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

import * as React from "react";
import { useCallback, useMemo } from "react";
import { Tbody, Td, Tr } from "@patternfly/react-table";
import { TableHeaderVisibility } from "../../api";
import { Cell, DataRecord, Row, TableInstance, Column } from "react-table";
import { Resizer } from "../Resizer";

export interface TableBodyProps {
  /** Table instance */
  tableInstance: TableInstance;
  /** The way in which the header will be rendered */
  headerVisibility?: TableHeaderVisibility;
  /** Optional children element to be appended below the table content */
  children?: React.ReactElement[];
  /** Custom function for getting row key prop, and avoid using the row index */
  getRowKey: (row: Row) => string;
  /** Custom function for getting column key prop, and avoid using the column index */
  getColumnKey: (column: Column) => string;
  /** Function to be executed when columns are modified */
  onColumnsUpdate?: (columns: Column[]) => void;
  /** Function to be executed when one or more rows are modified */
  onRowsUpdate?: (rows: DataRecord[]) => void;
}

export const TableBody: React.FunctionComponent<TableBodyProps> = ({
  tableInstance,
  children,
  headerVisibility = TableHeaderVisibility.Full,
  getRowKey,
  getColumnKey,
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  onColumnsUpdate,
  onRowsUpdate,
}) => {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const onHorizontalResizeStop = useCallback((width) => {
    // onColumnsUpdate({})
  }, []);

  const renderCell = useCallback(
    (cellIndex: number, cell: Cell, rowIndex: number) => {
      const cellType = cellIndex === 0 ? "counter-cell" : "data-cell";
      // const canResize = "has-resizer";
      const column = tableInstance.allColumns[cellIndex] as Column;
      const cellWidth = column?.width;
      const width = typeof cellWidth === "number" ? cellWidth : 250;
      const onHorizontalResizeStop = (width: number) => {
        const c = column as any;
        if (c.setWidth) {
          c.setWidth(width);
          tableInstance.allColumns[cellIndex].width = width; // prevent resize on blur bug
          onColumnsUpdate?.(tableInstance.columns);
        }
        // console.log("......", column);
        // column?.setWidth(width);
        // if (onColumnsUpdate) {
        //   column.width;
        //   column.width;
        //   console.log("________________", column);
        //   // onColumnsUpdate([a]);
        // }
      };
      const cellTemplate =
        cellIndex === 0 ? (
          <>{rowIndex + 1}</>
        ) : (
          <Resizer width={width} height="100%" minWidth={10} onHorizontalResizeStop={onHorizontalResizeStop}>
            <>{cell.render("Cell")}</>
          </Resizer>
        );
      const tdProps = tableInstance.getTdProps(cellIndex, rowIndex);

      // if (cellIndex !== 0) {
      //   tdProps.style = { width: "initial", maxWidth: "initial", minWidth: "initial" };
      // }

      // conso

      return (
        <Td
          {...tdProps}
          key={`${getColumnKey(cell.column)}-${cellIndex}`}
          data-ouia-component-id={"expression-column-" + cellIndex}
          className={`${cellType}`}
        >
          {cellTemplate}
        </Td>
      );
    },
    [getColumnKey, tableInstance]
  );

  const renderBodyRow = useCallback(
    (row: Row, rowIndex: number) => {
      const rowProps = { ...row.getRowProps(), style: {} };
      return (
        <Tr
          className="table-row"
          {...rowProps}
          key={`${getRowKey(row)}-${rowIndex}`}
          ouiaId={"expression-row-" + rowIndex}
        >
          {row.cells.map((cell: Cell, cellIndex: number) => renderCell(cellIndex, cell, rowIndex))}
        </Tr>
      );
    },
    [getRowKey, renderCell]
  );

  const renderAdditiveRow = useMemo(
    () => (
      <Tr className="table-row additive-row">
        <Td role="cell" className="empty-cell">
          <br />
        </Td>
        {children?.map((child, childIndex) => {
          return (
            <Td
              role="cell"
              key={childIndex}
              className="row-remainder-content"
              style={
                {
                  // width: tableInstance.allColumns[childIndex + 1].width,
                  // minWidth: tableInstance.allColumns[childIndex + 1].minWidth,
                }
              }
            >
              {child}
            </Td>
          );
        })}
      </Tr>
    ),
    [children]
  );

  return (
    <Tbody
      className={`${headerVisibility === TableHeaderVisibility.None ? "missing-header" : ""}`}
      {...tableInstance.getTableBodyProps()}
    >
      {tableInstance.rows.map((row: Row, rowIndex: number) => {
        tableInstance.prepareRow(row);
        return renderBodyRow(row, rowIndex);
      })}
      {children ? renderAdditiveRow : null}
    </Tbody>
  );
};
