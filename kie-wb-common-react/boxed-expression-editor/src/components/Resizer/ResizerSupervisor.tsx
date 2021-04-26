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
import { useEffect, useCallback } from "react";
import * as _ from "lodash";

export interface ResizerSupervisorProps {
  children?: React.ReactElement;
}

interface Cell {
  element: HTMLElement;
  width: number;
  visited: boolean;
}

export const ResizerSupervisor: React.FunctionComponent<ResizerSupervisorProps> = ({ children }) => {
  useEffect(() => {
    const allCells = fetchResizableCells(document.body);

    function setCellWidth(cell: HTMLElement, width: number) {
      const id = getId(cell);
      document.dispatchEvent(new CustomEvent(id, { detail: { width } }));
      cell.style.width = width + "px";
    }

    // function getCellWidth(_cell: HTMLElement) {
    //   return 300; // if it doesn't have width already, otherwise use the existing one
    // }

    function getId(cell: HTMLElement): string {
      return _.first([].slice.call(cell.classList).filter((c: string) => c.match(/uuid-/g))) || "";
    }

    function fetchResizableCells(parent: HTMLElement): Cell[] {
      return [].slice.call(parent.querySelectorAll(".react-resizable")).map((c: HTMLElement) => {
        return {
          element: c,
          width: 0,
          visited: false,
        };
      });
    }

    function updateSize(cell: Cell) {
      if (cell.visited) {
        return;
      }

      const childCells = fetchResizableCells(cell.element);

      if (childCells.length > 0) {
        childCells.forEach((elem: Cell) => updateSize(elem));
      }

      const thead = cell.element.querySelector("thead");
      const cellX = cell.element.getBoundingClientRect().x;
      const padding = 14;
      const width = thead ? thead.getBoundingClientRect().width + padding : 250;
      const maxWidth = Math.max(
        ...allCells
          .filter((e) => cellX === e.element.getBoundingClientRect().x)
          .map((e) => e.element.getBoundingClientRect().width)
      );

      cell.width = maxWidth > width ? maxWidth : width;
      // cell.visited = true;

      console.log(cell.element.textContent);

      setCellWidth(cell.element, cell.width);
    }

    allCells.forEach((cell: Cell) => updateSize(cell));
    allCells.forEach((cell: Cell) => updateSize(cell));
  }, []);

  return <div>{children}</div>;
};
