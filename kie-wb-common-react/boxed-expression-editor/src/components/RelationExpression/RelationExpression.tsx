/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import "./RelationExpression.css";
import * as React from "react";
import { useCallback, useEffect, useState } from "react";
import "@patternfly/patternfly/utilities/Text/text.css";
import { DataType, RelationProps, TableOperation } from "../../api";
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";

export const RelationExpression: React.FunctionComponent<RelationProps> = (relationProps: RelationProps) => {
  const FIRST_COLUMN_NAME = "column-1";
  const { i18n } = useBoxedExpressionEditorI18n();

  const [tableColumns, setTableColumns] = useState(
    relationProps.columns === undefined
      ? [{ name: FIRST_COLUMN_NAME, label: FIRST_COLUMN_NAME, dataType: DataType.Undefined }]
      : relationProps.columns
  );

  const [tableCells, setTableCells] = useState(relationProps.cells === undefined ? [{}] : relationProps.cells);

  const handlerConfiguration = [
    {
      group: i18n.columns,
      items: [
        { name: i18n.columnOperations.insertLeft, type: TableOperation.ColumnInsertLeft },
        { name: i18n.columnOperations.insertRight, type: TableOperation.ColumnInsertRight },
        { name: i18n.columnOperations.delete, type: TableOperation.ColumnDelete },
      ],
    },
    {
      group: i18n.rows,
      items: [
        { name: i18n.rowOperations.insertAbove, type: TableOperation.RowInsertAbove },
        { name: i18n.rowOperations.insertBelow, type: TableOperation.RowInsertBelow },
        { name: i18n.rowOperations.delete, type: TableOperation.RowDelete },
      ],
    },
  ];

  useEffect(() => {
    window.beeApi?.broadcastRelationExpressionDefinition?.({
      ...relationProps,
      columns: tableColumns,
      cells: tableCells,
    });
  }, [relationProps, tableColumns, tableCells]);

  const onColumnsUpdate = useCallback((columns) => setTableColumns(columns), []);
  const onCellsUpdate = useCallback((cells) => setTableCells(cells), []);

  return (
    <div className="relation-expression">
      <Table
        columnPrefix="column-"
        columns={tableColumns}
        cells={tableCells}
        onColumnsUpdate={onColumnsUpdate}
        onCellsUpdate={onCellsUpdate}
        handlerConfiguration={handlerConfiguration}
      />
    </div>
  );
};