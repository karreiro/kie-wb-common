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

  private id: string | undefined;
  private lastColumn: boolean | undefined;
  private rect: DOMRect | undefined;

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
      const parent = this.element.closest("tr");
      const isLast = parent?.lastChild == this.element.closest("th, td");
      this.lastColumn = isLast;
    }
    return this.lastColumn;
  }

  setWidth(width: number): void {
    const cellWidth = width < 100 ? 100 : width;

    // propagate to React state
    document.dispatchEvent(new CustomEvent(this.getId(), { detail: { width: cellWidth } }));

    // set on element to the "live" resize
    this.element.style.width = cellWidth + "px";
  }

  refreshWidthAsParent(): void {
    this.setWidth(this.fetchChildWidth() + Cell.PADDING);
  }

  refreshWidthAsLastColumn(): void {
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
