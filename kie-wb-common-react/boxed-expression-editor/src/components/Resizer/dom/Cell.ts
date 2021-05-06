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

export class Cell {
  private static DEFAULT_WIDTH = 250; // TODO pick width from React
  private static PADDING = 14; // TODO: we could get it via JS, performance reasons we don't

  private id?: string;
  private lastColumn?: boolean;
  private rect?: DOMRect;
  private parentRow?: HTMLTableRowElement | null;

  constructor(public element: HTMLElement, public children: Cell[], public depth: number) {}

  getId(): string {
    if (this.id === undefined) {
      this.id = _.first([].slice.call(this.element.classList).filter((c: string) => c.match(/uuid-/g))) || "";
    }
    return this.id;
  }

  getRect(): DOMRect {
    if (this.rect === undefined) {
      this.rect = this.element.getBoundingClientRect();
    }
    return this.rect;
  }

  isLastColumn(): boolean {
    if (this.lastColumn === undefined) {
      this.lastColumn = this.getParentRow()?.lastChild == this.element.closest("th, td");
    }
    return this.lastColumn;
  }

  setWidth(width: number): void {
    const cellWidth = parseInt((width < 100 ? 100 : width) + "");

    // propagate to React state
    document.dispatchEvent(new CustomEvent(this.getId(), { detail: { width: cellWidth } }));

    // set on element to the "live" resize
    this.element.style.width = cellWidth + "px";
  }

  refreshWidthAsParent(): void {
    this.setWidth(this.fetchChildWidth());
  }

  refreshWidthAsLastColumn(): void {
    if (!this.isLastColumn()) {
      return;
    }

    const parentRect = this.getParentRow()?.getBoundingClientRect();

    if (parentRect === undefined) {
      return;
    }

    const cellRect = this.element.getBoundingClientRect();
    const width = Math.round(parentRect.right) - Math.round(cellRect.x) - 15;

    this.setWidth(width + 14);
  }

  private getParentRow() {
    if (this.parentRow === undefined) {
      this.parentRow = this.element.closest("tr");
    }
    return this.parentRow;
  }

  /**
   * [TODO]
   * We cannot calculate as css styles may change
   */
  private fetchChildWidth() {
    const thead = this.element.querySelector("thead, tbody");
    const size = thead?.getBoundingClientRect().width;
    return Math.round(size ? parseInt(size + "") : Cell.DEFAULT_WIDTH) + 14;
  }
}