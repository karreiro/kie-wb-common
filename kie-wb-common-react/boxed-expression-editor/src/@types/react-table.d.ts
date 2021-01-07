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

import { DataType } from "../api";
import { TableResizerProps } from "react-table";

// Extending react-table definitions with missing and custom properties
declare module "react-table" {
  export interface TableOptions {
    onCellUpdate: (rowIndex: number, columnId: string, value: string) => void;
    getThProps: (
      columnIndex: number
    ) => {
      onContextMenu: (event) => void;
    };
    getTdProps: (
      columnIndex: number,
      rowIndex: number
    ) => {
      onContextMenu: (event) => void;
    };
  }

  export interface ColumnInstance {
    /** Column identifier */
    accessor: string;
    /** Column label */
    label: string;
    /** Column data type */
    dataType: DataType;
    /** When resizable, this function returns the resizer props  */
    getResizerProps: (props?: Partial<TableResizerProps>) => TableResizerProps;
    /** It tells whether column can resize or not */
    canResize: boolean;
  }
}
