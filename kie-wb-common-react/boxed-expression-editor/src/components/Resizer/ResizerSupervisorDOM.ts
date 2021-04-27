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

import * as _ from "lodash";
import { element } from "prop-types";

interface Cell {
  element: HTMLElement;
  children: Cell[];
  depth: number;
  visited?: boolean;
}

/**
 * [TODO]
 * React state + hooks abstractions have a level of granularity that limits the
 * frame hate in the context of the boxed expression component.
 * Setting targets/dependencies for effects would generate a huge amount of
 * function calls.
 * This component intentionally access DOM logic, but it should be spread across
 * the component.
 */
export const applyDOMSupervisor = (): void => {
  new SupervisorExecution().execute();
};

class SupervisorExecution {
  domSession: DOMSession;

  constructor() {
    this.domSession = new DOMSession();
  }

  execute() {
    this.domSession.getCells();
    console.log("🤡🤡");
  }
}

class DOMSession {
  private static CELL_CSS_SELCTOR = ".react-resizable";

  private allCells: Cell[] = [];

  getCells() {
    // if (this.cells === undefined) {
    //   this.cells = this.fetchCells(document.body);
    // }
    // return this.cells;

    this.fetchCellElements(document.body).forEach((cellElement) => {
      this.buildCell(cellElement, 0);
    });

    this.allCells.forEach((a) => {
      let s = "  ";
      for (let i = 0; i < a.depth; i++) {
        s += "    ";
      }
      console.log(s + a.element.textContent);
    });
  }

  private fetchCellElements(parent: HTMLElement): HTMLElement[] {
    const htmlElements = parent.querySelectorAll(DOMSession.CELL_CSS_SELCTOR);
    return [].slice.call(htmlElements);
  }

  private buildCell(htmlElement: HTMLElement, depthLevel: number): Cell {
    const exitingElement = this.allCells.find((c) => c.element === htmlElement);
    if (exitingElement) {
      return exitingElement;
    }

    const cell: Cell = {
      element: htmlElement,
      children: this.fetchCellElements(htmlElement)
        .map((child) => this.buildCell(child, depthLevel + 1))
        .filter((c) => c.depth == depthLevel + 1),
      depth: depthLevel,
    };

    this.allCells.push(cell);

    return cell;
  }
}

//////////////////////////////
//////////////////////////////
//////////////////////////////
//////////////////////////////
//////////////////////////////
//////////////////////////////
//////////////////////////////
//////////////////////////////
//////////////////////////////
//////////////////////////////
//////////////////////////////
//////////////////////////////
//////////////////////////////
//////////////////////////////

function old() {
  const allCells = fetchResizableCells(document.body);

  function setCellWidth(cell: HTMLElement, rawWidth: number) {
    const width = rawWidth < 100 ? 100 : rawWidth;
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
    // if (cell.visited) {
    //   return;
    // }
    // const childCells = fetchResizableCells(cell.element);
    // if (childCells.length > 0) {
    //   childCells.forEach((elem: Cell) => updateSize(elem));
    // }
    // const thead = cell.element.querySelector("thead");
    // const cellX = cell.element.getBoundingClientRect().x;
    // const padding = 14;
    // const width = thead ? thead.getBoundingClientRect().width + padding : 250;
    // const maxWidth = Math.max(
    //   ...allCells
    //     .filter((e) => cellX === e.element.getBoundingClientRect().x)
    //     .map((e) => e.element.getBoundingClientRect().width)
    // );
    // cell.width = maxWidth > width ? maxWidth : width;
    // // cell.visited = true;
    // console.log(cell.element.textContent);
    // setCellWidth(cell.element, cell.width);
  }
}
