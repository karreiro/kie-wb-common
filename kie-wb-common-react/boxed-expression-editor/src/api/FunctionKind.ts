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

import { ExpressionProps } from "./ExpressionProps";

export enum FunctionKind {
  Feel = "FEEL",
  Java = "JAVA",
  Pmml = "PMML",
}

export interface FeelFunctionProps {
  /** Feel Function */
  functionKind: FunctionKind.Feel;
  /** The Expression related to the function */
  expression?: ExpressionProps;
}

export interface JavaFunctionProps {
  /** Java Function */
  functionKind: FunctionKind.Java;
  /** Java class */
  class?: string;
  /** Method signature */
  method?: string;
}

export interface PmmlFunctionProps {
  /** Pmml Function */
  functionKind: FunctionKind.Pmml;
  /** PMML document */
  document?: string;
  /** PMML model */
  model?: string;
}