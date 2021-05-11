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

/*
 * =============================================================================
 * React state + hooks abstractions have a level of granularity that limits the
 * frame hate in the context of resizable boxed expression cells.
 *
 * This component intentionally accesses DOM elements, but it also propagates
 * new width information across React component once users commit an action.
 * =============================================================================
 */
export const applyDOMSupervisor = (): void => {
  new SupervisorExecution().execute();
};

class SupervisorExecution {
  domSession: DOMSession;

  constructor() {
    this.domSession = new DOMSession();
  }

  refreshWidthAsParent(cell: Cell) {
    cell.refreshWidthAsParent();
  }

  refreshWidthAsLastColumn(cell: Cell) {
    cell.refreshWidthAsLastColumn();
  }

  execute() {
    const p1 = performance.now();
    const cells = this.domSession.getCells();
    cells.sort((c1, c2) => c2.depth - c1.depth).forEach(this.refreshWidthAsParent);
    cells.sort((c1, c2) => c1.depth - c2.depth).forEach(this.refreshWidthAsLastColumn);
    const p2 = performance.now();

    console.log("All: " + (p2 - p1) + "ms");
  }
}
