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

import "./Resizer.css";
import * as React from "react";
import { useLayoutEffect, useEffect } from "react";
import { applyDOMSupervisor, Throttling } from "./dom";
import { ExpressionProps } from "../../api";
import { useMemo } from "react";
// import { BoxedExpressionGlobalContext } from "../../context";

export interface ResizerSupervisorProps {
  selectedExpression?: ExpressionProps;
  children?: React.ReactElement;
}

export const ResizerSupervisor: React.FunctionComponent<ResizerSupervisorProps> = (props) => {
  // const globalContext = useContext(BoxedExpressionGlobalContext);
  useEffect(() => {
    Throttling.run(() => {
      // applyDOMSupervisor();
    });
  }, [props.selectedExpression]);

  useLayoutEffect(() => {
    applyDOMSupervisor();

    setTimeout(applyDOMSupervisor, 1000);
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    function listener(_: CustomEvent) {
      Throttling.run(() => {
        applyDOMSupervisor();
      });
    }

    document.addEventListener("supervisor", listener);
    return () => {
      document.removeEventListener("supervisor", listener);
    };
  }, []); // TODO: use state instead of custom events

  return useMemo(() => <div>{props.children}</div>, [props]);
};
