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
import { useCallback, useMemo, useState, useLayoutEffect } from "react";
import { ResizableBox } from "react-resizable";
import { v4 as uuid } from "uuid";
import * as _ from "lodash";

class Column {
  public constructor(public start: number, public end: number) {}
  includes(col: Column): boolean {
    return this.start <= col.start && this.end >= col.end;
  }

  getX() {
    return this.start;
  }
}

class Cell {
  public constructor(public column: Column, public cell: HTMLElement, public isLastCell?: boolean) {}

  asLastCell() {
    this.isLastCell = true;
  }

  isLast() {
    const elems = this.cell.querySelectorAll(".pf-c-drawer");

    if (elems.length > 0) {
      const dip = window.getComputedStyle(elems[elems.length - 1]).display;
      return dip === "none";
    }
    return false;
  }
}

export interface ResizerProps {
  width: number;
  height: number | "100%";
  minWidth: number;
  minHeight?: number;
  onHorizontalResizeStop: (width: number) => void;
  children?: React.ReactElement;
}

export const DRAWER_SPLITTER_ELEMENT = (
  <div className="pf-c-drawer__splitter pf-m-vertical">
    <div className="pf-c-drawer__splitter-handle" />
  </div>
);

export const Resizer: React.FunctionComponent<ResizerProps> = ({
  children,
  height,
  minHeight = 0,
  minWidth,
  onHorizontalResizeStop,
  width,
}) => {
  const targetHeight = height === "100%" ? 0 : height;
  const resizerHandler = useMemo(() => <div className="pf-c-drawer">{DRAWER_SPLITTER_ELEMENT}</div>, []);
  const id = useMemo(() => `uuid-${uuid()}`, []);
  const [w, setW] = useState(300);
  const x: Cell[] = [];
  const [cells, setCells] = useState(x);
  const [initalW, setInitialW] = useState(0);

  useLayoutEffect(() => {
    // console.log("=>>>>>>>>>>>>>>>>>>>>>>>> " + id);
    function listener(event: CustomEvent) {
      const width = event.detail.width;
      setW(width);
    }

    document.addEventListener(id, listener);
    return () => {
      document.removeEventListener(id, listener);
    };
  }, [id]);

  const onResizeStart = useCallback(() => {
    const c = document.querySelector(`.${id}`);
    const rect = c?.getBoundingClientRect();
    const w = rect?.width || 0;
    const x = rect?.x || 0;
    const y = rect?.y || 0;

    const domCells: HTMLElement[] = [].slice.call(document.querySelectorAll(".react-resizable"));
    const columns: number[] = [];

    const tableCells: Cell[] = domCells.map((dc) => {
      const rect = dc.getBoundingClientRect();
      if (!columns.includes(rect.x)) {
        columns.push(rect.x);
      }
      const current: Cell = new Cell(new Column(rect.x, rect.x + rect.width), dc);
      return current;
    });

    const currentColumn = new Column(x, w + x);
    // const currentColumn = columns.indexOf(x);
    // const currentRow = rows.indexOf(y);

    // const rowCells = tableCells.filter((d: Cell) => d.row === c.row) // cache it
    // if (rowCells  )
    // if (currentColumn > columns.length - 1) {

    // }

    const applicableCells: Cell[] = tableCells.filter((c: Cell) => {
      if (c.column.includes(currentColumn)) {
        return true;
      }
      if (currentColumn.includes(c.column) && c.isLast()) {
        return true;
      }
      return false;
    });

    // const applicableRows: Cell[] = tableCells.filter((c: Cell) => {
    //   if (c.row === currentRow) {
    //     return true;
    //   }
    // });

    // const applicableCells = applicableCols.concat(applicableRows);

    // applicableCells.forEach((e) => console.log(e.cell.textContent));

    // const cells1: HTMLElement[] = [].slice
    //   .call(document.querySelectorAll(".react-resizable"))
    //   .filter((e: HTMLElement) => {
    //     return (
    //       x === e.getBoundingClientRect().x || x + w === e.getBoundingClientRect().x + e.getBoundingClientRect().width
    //     );
    //   });

    // console.log(w + " >>> A " + cells1.length);
    applicableCells.forEach((r) => {
      r.cell.setAttribute("data-initial-w", r.cell.style.width);
    });
    // console.log(x + " >>> B ");

    setCells(applicableCells);
    setInitialW(w);
  }, [id]);

  const onResize = useCallback(
    (_e, data) => {
      cells.forEach((c) => {
        const delta = data.size.width - initalW;
        const a: string = _.first([].slice.call(c.cell.classList).filter((c: string) => c.match(/uuid-/g))) || "";
        if (a !== id) {
          c.cell.style.width = parseInt(c.cell.getAttribute("data-initial-w") || "") + delta + "px";
        }
      }, []);
    },
    [cells, id, initalW]
  );

  const onResizeStop = useCallback(
    (_e, data) => {
      cells.forEach((c) => {
        const delta = data.size.width - initalW;
        const a: string = _.first([].slice.call(c.cell.classList).filter((c: string) => c.match(/uuid-/g))) || "";
        document.dispatchEvent(
          new CustomEvent(a, { detail: { width: parseInt(c.cell.getAttribute("data-initial-w") || "") + delta } })
        );
      }, []);
    },
    [cells, initalW]
  );

  return useMemo(
    () => (
      <ResizableBox
        className={`${height === "100%" ? "height-based-on-content" : ""} ${id}`}
        width={w}
        height={targetHeight}
        minConstraints={[minWidth, minHeight]}
        axis="x"
        onResizeStop={onResizeStop}
        onResize={onResize}
        onResizeStart={onResizeStart}
        handle={resizerHandler}
      >
        {children}
      </ResizableBox>
    ),
    [height, id, w, targetHeight, minWidth, minHeight, onResizeStop, onResize, onResizeStart, resizerHandler, children]
  );
};
