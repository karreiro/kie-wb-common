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

class Cell {
  private static DEFAULT_WIDTH = 250;
  private static DRAGGBLE_ELEMENT = ".pf-c-drawer";
  private static PADDING = 14; // TODO: we could get it via JS, performance reasons we don't

  private id: string | undefined;
  private visited = false;
  private lastColumn: boolean | undefined = undefined;

  constructor(public element: HTMLElement, public children: Cell[], public depth: number) {}

  getId() {
    if (this.id === undefined) {
      this.id = _.first([].slice.call(this.element.classList).filter((c: string) => c.match(/uuid-/g))) || "";
    }
    return this.id;
  }

  isVisited() {
    return this.visited;
  }

  isLastColumn() {
    if (this.lastColumn === undefined) {
      const parent = this.element.closest("tr");
      const isLast = parent?.lastChild == this.element.closest("th, td");
      this.lastColumn = isLast;
    }
    return this.lastColumn;
  }

  setWidth(width: number) {
    const cellWidth = width < 100 ? 100 : width;

    // propagate to React state
    document.dispatchEvent(new CustomEvent(this.getId(), { detail: { width: cellWidth } }));

    // set on element to the "live" resize
    this.element.style.width = cellWidth + "px";
  }

  refreshWidthAsParent() {
    this.setWidth(this.fetchChildWidth() + Cell.PADDING);
  }

  refreshWidthAsLastColumn() {
    if (!this.isLastColumn()) {
      return;
    }

    const parentRect = this.element.closest("tr")?.getBoundingClientRect();
    if (parentRect === undefined) {
      return;
    }

    const cellRect = this.element.getBoundingClientRect();
    const width = parentRect.right - cellRect.x - 15;

    this.setWidth(width + Cell.PADDING);
  }

  /**
   * [TODO]
   * We cannot calculate as css styles may change
   */
  private fetchChildWidth() {
    const thead = this.element.querySelector("thead");
    return thead?.getBoundingClientRect().width || Cell.DEFAULT_WIDTH;
  }
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

  updateSize(cell: Cell) {
    if (cell.isVisited()) {
      return;
    }

    if (cell.children.length > 0) {
      cell.refreshWidthAsParent();
    }
  }

  updateLastColumns(cell: Cell) {
    cell.refreshWidthAsLastColumn();
  }

  execute() {
    const cells = this.domSession.getCells();

    // const p1 = performance.now();
    // for (let index = 0; index < 10; index++) {
    cells.sort((c1, c2) => c2.depth - c1.depth).forEach(this.updateSize);
    cells.sort((c1, c2) => c1.depth - c2.depth).forEach(this.updateLastColumns);
    // }
    // const p2 = performance.now();
    // console.log("🤡🤡" + (p2 - p1) + "ms");
    console.log("🤡🤡");
  }
}

class DOMSession {
  private static CELL_CSS_SELCTOR = ".react-resizable";

  private cells: Cell[] | undefined;

  getCells() {
    if (this.cells === undefined) {
      this.cells = this.buildCells();
    }
    return this.cells;
  }

  private buildCells() {
    const cells: Cell[] = [];
    this.fetchCellElements(document.body).forEach((cellElement) => {
      this.buildCell(cellElement, cells, 0);
    });
    return cells;
  }

  private buildCell(htmlElement: HTMLElement, cells: Cell[], depthLevel: number): Cell {
    const exitingElement = cells.find((c) => c.element === htmlElement);
    if (exitingElement) {
      return exitingElement;
    }

    const cell = new Cell(
      htmlElement,
      this.fetchCellElements(htmlElement)
        .map((child) => this.buildCell(child, cells, depthLevel + 1))
        .filter((c) => c.depth == depthLevel + 1),
      depthLevel
    );

    cells.push(cell);

    return cell;
  }

  private fetchCellElements(parent: HTMLElement): HTMLElement[] {
    const htmlElements = parent.querySelectorAll(DOMSession.CELL_CSS_SELCTOR);
    return [].slice.call(htmlElements);
  }
}
