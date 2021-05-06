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

export class Throttling {
  private static instance: Throttling;
  private latestCall = 0;

  // eslint-disable-next-line @typescript-eslint/no-empty-function
  private constructor() {}

  static run(fn: () => void): void {
    this.clearTimeout();
    this.setTimeout(fn);
  }

  private static setTimeout(fn: () => void): void {
    this.getInstance().latestCall = window.setTimeout(fn, 500);
  }

  private static clearTimeout() {
    const instance = this.getInstance();
    window.clearTimeout(instance.latestCall);
  }

  private static getInstance(): Throttling {
    if (!Throttling.instance) {
      Throttling.instance = new Throttling();
    }
    return Throttling.instance;
  }
}
