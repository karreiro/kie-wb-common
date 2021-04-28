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

import { DOMSession, Cell } from "./";

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
  throttling(() => {
    new SupervisorExecution().execute();
  });
};

class SupervisorExecution {
  domSession: DOMSession;

  constructor() {
    this.domSession = new DOMSession();
  }

  updateSize(cell: Cell) {
    if (cell.children.length > 0) {
      cell.refreshWidthAsParent();
    }
  }

  updateLastColumns(cell: Cell) {
    cell.refreshWidthAsLastColumn();
  }

  execute() {
    const cells = this.domSession.getCells();

    const p1 = performance.now();
    for (let index = 0; index < 10; index++) {
      cells.sort((c1, c2) => c2.depth - c1.depth).forEach(this.updateSize);
      cells.sort((c1, c2) => c1.depth - c2.depth).forEach(this.updateLastColumns);
    }
    const p2 = performance.now();
    console.log(p2 - p1 + "ms");
  }
}

let latestCall = 0;

function throttling(fn: () => void) {
  if (latestCall === 0) {
    fn();
  }

  latestCall = window.setTimeout(() => {
    window.clearInterval(latestCall);
    latestCall = 0;
  }, 100);
}
