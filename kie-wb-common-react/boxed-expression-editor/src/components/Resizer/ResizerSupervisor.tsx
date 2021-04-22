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
import { useEffect } from "react";
import * as _ from "lodash";

export interface ResizerSupervisorProps {
  children?: React.ReactElement;
}

export const ResizerSupervisor: React.FunctionComponent<ResizerSupervisorProps> = ({ children }) => {
  useEffect(() => {
    const cells: HTMLElement[] = _.reverse([].slice.call(document.querySelectorAll(".react-resizable")));

    function setCellWidth(cell: HTMLElement, width: number) {
      const a = getId(cell);
      document.dispatchEvent(new CustomEvent(a, { detail: { width } }));
      cell.style.width = width + "px";
    }

    function getId(cell: HTMLElement): string {
      return _.first([].slice.call(cell.classList).filter((c: string) => c.match(/uuid-/g))) || "";
    }

    cells.forEach((cell) => {
      const isFirstCell = cells.indexOf(cell) === cells.length - 1;
      if (isFirstCell) {
        const row = cell.parentElement?.parentElement?.getBoundingClientRect().width || 0;
        setCellWidth(cell, row - 62);
        return;
      }

      const table = cell.querySelector("table");
      const cellX = cell.getBoundingClientRect().x;
      const padding = 14;
      const width = table ? table.getBoundingClientRect().width + padding : 250;
      const maxWidth = Math.max(
        ...cells
          .filter((e) => isFirstCell || cellX === e.getBoundingClientRect().x)
          .map((e) => e.getBoundingClientRect().width)
      );

      setCellWidth(cell, maxWidth > width ? maxWidth : width);
    });
  }, []);

  return <div>{children}</div>;
};
