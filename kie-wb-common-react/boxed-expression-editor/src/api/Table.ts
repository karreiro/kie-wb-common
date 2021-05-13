/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { DataType } from "./DataType";
import * as React from "react";
import { Column as ReactTableColumn, DataRecord, Row as ReactTableRow } from "react-table";

export interface TableProps {
  /** Table identifier, useful for nested structures */
  tableId?: string;
  /** Optional children element to be appended below the table content */
  children?: React.ReactElement[];
  /** The prefix to be used for the column name */
  columnPrefix?: string;
  /** Optional label to be used for the edit popover that appears when clicking on column header */
  editColumnLabel?: string;
  /** For each column there is a default component to be used to render the related cell */
  defaultCell?: {
    [columnName: string]: React.FunctionComponent<CellProps>;
  };
  /** Table's columns */
  columns: ReactTableColumn[];
  /** Table's cells */
  rows: DataRecord[];
  /** Function to be executed when columns are modified */
  onColumnsUpdate?: (columns: ReactTableColumn[]) => void;
  /** Function to be executed when one or more rows are modified */
  onRowsUpdate?: (rows: DataRecord[]) => void;
  /** Function to be executed when adding a new row to the table */
  onRowAdding?: () => DataRecord;
  /** Custom configuration for the table handler */
  handlerConfiguration: TableHandlerConfiguration;
  /** The way in which the header will be rendered */
  headerVisibility?: TableHeaderVisibility;
  /** Number of levels in the header, 0-based */
  headerLevels?: number;
  /** True, for skipping the creation in the DOM of the last defined header group */
  skipLastHeaderGroup?: boolean;
  /** Custom function for getting row key prop, and avoid using the row index */
  getRowKey?: (row: ReactTableRow) => string;
  /** Custom function for getting column key prop, and avoid using the column index */
  getColumnKey?: (column: ReactTableColumn) => string;
  /** Custom function called for manually resetting a row */
  resetRowCustomFunction?: (row: DataRecord) => DataRecord;
}

/** Possible status for the visibility of the Table's Header */
export enum TableHeaderVisibility {
  Full,
  LastLevel,
  SecondToLastLevel,
  None,
}

/** Table allowed operations */
export enum TableOperation {
  ColumnInsertLeft,
  ColumnInsertRight,
  ColumnDelete,
  RowInsertAbove,
  RowInsertBelow,
  RowDelete,
  RowClear,
}

export interface GroupOperations {
  /** Name of the group (localized) */
  group: string;
  /** Collection of operations belonging to this group */
  items: {
    /** Name of the operation (localized) */
    name: string;
    /** Type of the operation */
    type: TableOperation;
  }[];
}

export type TableHandlerConfiguration = GroupOperations[];

export type AllowedOperations = TableOperation[];

export type Row = string[];

export type Rows = Row[];

export interface Column {
  /** Column name */
  name: string;
  /** Column data type */
  dataType: DataType;
  /** Column width */
  width?: string | number;
  /** Set column width */
  setWidth?: (width: string | number) => void; // TODO remove the ? (optional) from here
}

export type Columns = Column[];

export interface CellProps {
  /** Cell's row properties */
  row: { index: number };
  /** Cell's column properties */
  column: { id: string };
}
