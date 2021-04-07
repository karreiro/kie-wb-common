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

import { LogicType } from "./LogicType";
import { DataType } from "./DataType";
import { Columns, Rows } from "./Table";
import { ContextEntries } from "./ContextEntry";
import { HitPolicy } from "./HitPolicy";
import { BuiltinAggregation } from "./BuiltinAggregation";
import { Clause, DecisionTableRule } from "./DecisionTableRule";

export interface ExpressionProps {
  /** Unique identifier used to identify the expression */
  uid?: string;
  /** Expression name (which, in DMN world, is equal to the Decision node's name) */
  name?: string;
  /** Expression data type */
  dataType?: DataType;
  /** Optional callback executed to update expression's name and data type */
  onUpdatingNameAndDataType?: (updatedName: string, updatedDataType: DataType) => void;
  /** Logic type should not be defined at this stage */
  logicType?: LogicType;
  /** True to have no header for this specific expression component, used in a recursive expression */
  isHeadless?: boolean;
  /** When a component is headless, it will call this function to pass its most updated expression definition */
  onUpdatingRecursiveExpression?: (expression: ExpressionProps) => void;
}

export interface LiteralExpressionProps extends ExpressionProps {
  /** Logic type must be LiteralExpression */
  logicType: LogicType.LiteralExpression;
  /** Optional content to display for this literal expression */
  content?: string;
  /** Optional width for this literal expression */
  width?: number;
}

export interface RelationProps extends ExpressionProps {
  /** Logic type must be Relation */
  logicType: LogicType.Relation;
  /** Each column has a name and a data type. Their order is from left to right */
  columns?: Columns;
  /** Rows order is from top to bottom. Each row has a collection of cells, one for each column */
  rows?: Rows;
}

export interface ContextProps extends ExpressionProps {
  /** Logic type must be Context */
  logicType: LogicType.Context;
  /** Collection of context entries */
  contextEntries?: ContextEntries;
  /** Context result */
  result?: ExpressionProps;
  /** Entry info width */
  entryInfoWidth?: number;
  /** Entry expression width */
  entryExpressionWidth?: number;
}

export interface DecisionTableProps extends ExpressionProps {
  /** Logic type must be Decision Table */
  logicType: LogicType.DecisionTable;
  /** Hit policy for this particular decision table */
  hitPolicy?: HitPolicy;
  /** Aggregation policy, when the hit policy supports it */
  aggregation?: BuiltinAggregation;
  /** Annotation columns names */
  annotations: string[];
  /** Input columns definition */
  input: Clause[];
  /** Output columns definition */
  output: Clause[];
  /** Rules represent rows values */
  rules: DecisionTableRule[];
}

export interface ListProps extends ExpressionProps {
  /** Logic type must be List */
  logicType: LogicType.List;
  /** List items */
  items?: ExpressionProps[];
  /** Optional width for this list expression */
  width?: number;
}

export interface InvocationProps extends ExpressionProps {
  /** Logic type must be Invocation */
  logicType: LogicType.Invocation;
  /** Function to be invoked */
  invokedFunction?: string;
  /** Collection of parameters used to invoke the function */
  bindingEntries?: ContextEntries;
  /** Entry info width */
  entryInfoWidth?: number;
  /** Entry expression width */
  entryExpressionWidth?: number;
}