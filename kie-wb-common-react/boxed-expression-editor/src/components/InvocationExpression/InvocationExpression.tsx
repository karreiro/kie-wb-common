/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import "./InvocationExpression.css";
import * as React from "react";
import { ChangeEvent, useCallback, useEffect, useRef, useState } from "react";
import {
  ContextEntries,
  DataType,
  DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
  DEFAULT_ENTRY_INFO_MIN_WIDTH,
  generateNextAvailableEntryName,
  getEntryKey,
  getHandlerConfiguration,
  InvocationProps,
  resetEntry,
  TableHeaderVisibility,
} from "../../api";
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { ColumnInstance, DataRecord } from "react-table";
import { ContextEntryExpressionCell, ContextEntryInfoCell } from "../ContextExpression";
import * as _ from "lodash";
import { setWith } from "lodash";

const DEFAULT_PARAMETER_NAME = "p-1";
const DEFAULT_PARAMETER_DATA_TYPE = DataType.Undefined;

export const InvocationExpression: React.FunctionComponent<InvocationProps> = ({
  bindingEntries,
  dataType = DEFAULT_PARAMETER_DATA_TYPE,
  entryInfoWidth = DEFAULT_ENTRY_INFO_MIN_WIDTH,
  entryExpressionWidth = DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
  invokedFunction = "",
  isHeadless,
  logicType,
  name = DEFAULT_PARAMETER_NAME,
  onUpdatingNameAndDataType,
  onUpdatingRecursiveExpression,
  uid,
}: InvocationProps) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const [rows, setRows] = useState(
    bindingEntries || [
      {
        entryInfo: {
          name: DEFAULT_PARAMETER_NAME,
          dataType: DEFAULT_PARAMETER_DATA_TYPE,
        },
        entryExpression: {},
        editInfoPopoverLabel: i18n.editParameter,
      } as DataRecord,
    ]
  );

  const functionDefinition = useRef<string>(invokedFunction);

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [infoWidth, setInfoWidth] = useState(entryInfoWidth);
  const [expressionWidth, setExpressionWidth] = useState(entryExpressionWidth);

  const spreadInvocationExpressionDefinition = useCallback(() => {
    const [expressionColumn] = columns.current;

    const updatedDefinition: InvocationProps = {
      uid,
      logicType,
      name: expressionColumn.accessor,
      dataType: expressionColumn.dataType,
      bindingEntries: rows as ContextEntries,
      invokedFunction: functionDefinition.current,
      entryInfoWidth: infoWidth,
      entryExpressionWidth: expressionWidth,
    };
    isHeadless
      ? onUpdatingRecursiveExpression?.(_.omit(updatedDefinition, ["name", "dataType"]))
      : window.beeApi?.broadcastInvocationExpressionDefinition?.(updatedDefinition);
  }, [expressionWidth, infoWidth, isHeadless, logicType, onUpdatingRecursiveExpression, rows, uid]);

  const onFunctionDefinitionChange = useCallback((e: ChangeEvent<HTMLInputElement>) => {
    functionDefinition.current = e.target.value;
  }, []);

  const onFunctionDefinitionBlur = useCallback(() => {
    spreadInvocationExpressionDefinition();
  }, [spreadInvocationExpressionDefinition]);

  const headerCellElement = (
    <div className="function-definition-container">
      <input
        className="function-definition pf-u-text-truncate"
        type="text"
        placeholder={i18n.enterFunction}
        onChange={onFunctionDefinitionChange}
        onBlur={onFunctionDefinitionBlur}
        defaultValue={functionDefinition.current}
      />
    </div>
  );

  const columns = useRef([
    {
      label: name,
      accessor: name,
      dataType,
      disableHandlerOnHeader: true,
      columns: [
        {
          headerCellElement,
          accessor: "functionDefinition",
          disableHandlerOnHeader: true,
          columns: [
            {
              accessor: "entryInfo",
              disableHandlerOnHeader: true,
              width: infoWidth,
              setWidth: setInfoWidth,
              minWidth: DEFAULT_ENTRY_INFO_MIN_WIDTH,
            },
            {
              accessor: "entryExpression",
              disableHandlerOnHeader: true,
              // width: "inital",
              width: expressionWidth,
              setWidth: setExpressionWidth,
              minWidth: DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
            },
          ],
        },
      ],
    },
  ]);

  const onColumnsUpdate = useCallback(
    ([expressionColumn]: [ColumnInstance]) => {
      console.log(expressionColumn);
      onUpdatingNameAndDataType?.(expressionColumn.label, expressionColumn.dataType);
      // // console.log(_.find(expressionColumn.columns, { accessor: "entryInfo" })?.width as number);
      // // console.log(_.find(expressionColumn.columns, { accessor: "entryExpression" })?.width as number);
      // const [updatedExpressionColumn] = columns.current;
      // updatedExpressionColumn.label = expressionColumn.label;
      // updatedExpressionColumn.accessor = expressionColumn.accessor;
      // updatedExpressionColumn.dataType = expressionColumn.dataType;
      spreadInvocationExpressionDefinition();
    },
    [onUpdatingNameAndDataType, spreadInvocationExpressionDefinition]
  );

  const onRowAdding = useCallback(
    () => ({
      entryInfo: {
        name: generateNextAvailableEntryName(rows as ContextEntries, "p"),
        dataType: DEFAULT_PARAMETER_DATA_TYPE,
      },
      entryExpression: {},
      editInfoPopoverLabel: i18n.editParameter,
    }),
    [i18n.editParameter, rows]
  );

  const getHeaderVisibility = useCallback(() => {
    return isHeadless ? TableHeaderVisibility.SecondToLastLevel : TableHeaderVisibility.Full;
  }, [isHeadless]);

  useEffect(() => {
    /** Everytime the list of items or the function definition change, we need to spread expression's updated definition */
    spreadInvocationExpressionDefinition();
  }, [rows, spreadInvocationExpressionDefinition]);

  const setRowsCallback = useCallback((entries) => {
    console.log("entries ", entries);
    setRows(entries);
  }, []);

  return (
    <div className={`invocation-expression ${uid}`}>
      <Table
        tableId={uid}
        headerLevels={2}
        headerVisibility={getHeaderVisibility()}
        skipLastHeaderGroup
        defaultCell={{ entryInfo: ContextEntryInfoCell, entryExpression: ContextEntryExpressionCell }}
        columns={columns.current}
        rows={rows as DataRecord[]}
        onColumnsUpdate={onColumnsUpdate}
        onRowAdding={onRowAdding}
        onRowsUpdate={setRowsCallback}
        handlerConfiguration={getHandlerConfiguration(i18n, i18n.parameters)}
        getRowKey={useCallback(getEntryKey, [])}
        resetRowCustomFunction={useCallback(resetEntry, [])}
      />
    </div>
  );
};
