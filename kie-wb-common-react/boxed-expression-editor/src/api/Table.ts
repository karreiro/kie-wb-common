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

/** Table allowed operations */
export enum TableOperation {
  ColumnInsertLeft,
  ColumnInsertRight,
  ColumnDelete,
  RowInsertAbove,
  RowInsertBelow,
  RowDelete,
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

export interface RowObject {
  /** Dynamic fields, optionally one for each column identifier */
  [columnId: string]: string;
}

export type Cells = RowObject[];

export interface ColumnObject {
  /** Relation's column name */
  name: string;
  /** Relation's column label (the one shown in the page) */
  label: string;
  /** Relation's column data type */
  dataType: DataType;
}

export type Columns = ColumnObject[];