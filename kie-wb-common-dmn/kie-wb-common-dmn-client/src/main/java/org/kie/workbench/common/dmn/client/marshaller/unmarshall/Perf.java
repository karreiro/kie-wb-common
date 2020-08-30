/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.marshaller.unmarshall;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import elemental2.dom.DomGlobal;

@ApplicationScoped
public class Perf {

    private List<Double> checks = new ArrayList<>();
    private List<String> labels = new ArrayList<>();

    public void check(final String label) {
        checks.add(DomGlobal.performance.now());
        labels.add(label);
    }

    public void print() {

        if (checks.size() > 2) {
            for (int i = 1, checksSize = checks.size(); i < checksSize; i++) {
                final Double t0 = checks.get(i - 1);
                final Double t1 = checks.get(i);
                final String check = labels.get(i) == null ? String.valueOf(i) : labels.get(i);
                final double diff = t1 - t0;
                if (diff > 150) {
                    DomGlobal.console.log("MEASURE LOG (check: " + check + ") ~> " + diff + "ms");
                }
            }
        }

        final Double t0 = checks.size() > 0 ? checks.get(0) : 0.0;
        final Double t1 = checks.size() > 0 ? checks.get(checks.size() - 1) : 0.0;
        DomGlobal.console.log("MEASURE LOG (total) ~> " + (t1 - t0) + "ms");
    }
}
