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
// import * as _ from "lodash";
import { Cell, DOMSession } from "./dom";

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
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  minWidth,
  onHorizontalResizeStop,
  width,
}) => {
  const targetHeight = height === "100%" ? 0 : height;
  const resizerHandler = useMemo(() => <div className="pf-c-drawer">{DRAWER_SPLITTER_ELEMENT}</div>, []);
  const id = useMemo(() => `uuid-${uuid()}`, []);
  const [w, setW] = useState(width || 100);
  const x: Cell[] = [];
  const [cells, setCells] = useState(x);
  const [initalW, setInitialW] = useState(0);
  const [lastPos, setLastPos] = useState(-1);

  const widthCached = useCallback(() => {
    return w;
  }, [w]);

  useLayoutEffect(() => {
    function listener(event: CustomEvent) {
      const width = parseInt(event.detail.width + "");
      // console.log(">> id event");

      // console.log("- event >>> " + width + "  >>  " + w.width);

      // if (width !== widthCached()) {
      //   setW(width || 100);
      //   // console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + id + " - " + w + " - " + width);

      // }

      setW(width);
      onHorizontalResizeStop(widthCached());

      // setW(width);
      // TODO triggers only if the size is different
      // console.log(event.type == id, id);
      // if (!w.visited) {
      //   onHorizontalResizeStop(w);
      //   w.visited = true;
      // }
    }

    document.addEventListener(id, listener);
    return () => {
      document.removeEventListener(id, listener);
    };
  }, [id, onHorizontalResizeStop, widthCached]);

  const onResizeStart = useCallback(() => {
    const applicableCells: Cell[] = [];
    const allCells = new DOMSession().getCells();
    const currentCell = allCells.find((c) => c.getId() === id)!;
    const initialWidth = currentCell.getRect().width;
    const parent = currentCell.element.closest("table");

    let someLast = false;

    if (currentCell.isLastColumn()) {
      allCells
        .filter((cell) => cell.isLastColumn())
        .forEach((cell) => {
          applicableCells.push(cell);
        });
    } else {
      allCells.forEach((cell) => {
        const cellContainsCurrent =
          cell.getRect().x <= currentCell.getRect().x && cell.getRect().right >= currentCell.getRect().right;

        if ((parent?.contains(cell.element) || cell.element?.contains(currentCell.element)) && cellContainsCurrent) {
          applicableCells.push(cell);
          if (cell.isLastColumn()) {
            someLast = true;
          }
        }
      });

      if (someLast) {
        allCells
          .filter((cell) => cell.isLastColumn() && !parent?.contains(cell.element))
          .forEach((cell) => {
            applicableCells.push(cell);
          });
      }
    }

    applicableCells.forEach((cell) => {
      cell.element.setAttribute("data-initial-w", cell.element.style.width);
    });

    setCells(applicableCells);
    setInitialW(initialWidth);
  }, [id]);

  const onResize = useCallback(
    (_e, data) => {
      const newWidth = parseInt(data.size.width + "");
      const width = newWidth < 100 ? 100 : newWidth;
      if (lastPos !== newWidth) {
        cells.forEach((c) => {
          const delta = width - initalW;
          if (c.getId() !== id) {
            c.element.style.width = parseInt(c.element.getAttribute("data-initial-w") || "") + delta + "px";
          }
        });
        setLastPos(newWidth);
      }
    },
    [cells, id, initalW, lastPos]
  );

  const onResizeStop = useCallback(
    (_e, data) => {
      const width = data.size.width < 100 ? 100 : data.size.width;
      cells.forEach((c) => {
        const delta = width - initalW;
        c.setWidth(parseInt(parseInt(c.element.getAttribute("data-initial-w") || "") + delta + ""));
      });

      document.dispatchEvent(
        new CustomEvent("supervisor", {
          detail: {},
        })
      );
    },
    [cells, initalW]
  );

  // if (width !== 300) {
  //   // console.log(`>>>>>>>>>>>>>> ${width}`);
  // }

  return useMemo(() => {
    // console.log("RENDER" + id);
    return (
      <ResizableBox
        className={`${height === "100%" ? "height-based-on-content" : ""} ${id}`}
        width={widthCached()}
        height={targetHeight}
        minConstraints={[100, minHeight]}
        axis="x"
        onResizeStop={onResizeStop}
        onResize={onResize}
        onResizeStart={onResizeStart}
        handle={resizerHandler}
      >
        {children}
      </ResizableBox>
    );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [onResize, children]);
};
