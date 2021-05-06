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
import { useLayoutEffect } from "react";
import { applyDOMSupervisor, Throttling } from "./dom";
import { ExpressionProps } from "../../api";
import { useMemo } from "react";
import { useState } from "react";
// import { BoxedExpressionGlobalContext } from "../../context";

export interface ResizerSupervisorProps {
  selectedExpression?: ExpressionProps;
  children?: React.ReactElement;
}

export const ResizerSupervisor: React.FunctionComponent<ResizerSupervisorProps> = (props) => {
  // const globalContext = useContext(BoxedExpressionGlobalContext);

  // useState(props.selectedExpression)

  const [def, setDef] = useState("");

  // useEffect(() => {
  //   console.log(">>>> " + JSON.stringify(props.selectedExpression));
  //   setU(JSON.stringify(props.selectedExpression));
  // }, [props.selectedExpression]);

  // useEffect(() => {
  //   Throttling.run(() => {
  //     applyDOMSupervisor();
  //   });
  // }, [u]);

  useLayoutEffect(() => {
    applyDOMSupervisor();

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    function listener(e: CustomEvent) {
      const newDef = JSON.stringify(e.detail.definition);

      setDef(newDef);
      if (def !== newDef) {
        applyDOMSupervisor();
      }
    }

    document.addEventListener("supervisor", listener);
    return () => {
      document.removeEventListener("supervisor", listener);
    };
  }, [def]); // TODO: use state instead of custom events

  return useMemo(() => <div>{props.children}</div>, [props]);
};
