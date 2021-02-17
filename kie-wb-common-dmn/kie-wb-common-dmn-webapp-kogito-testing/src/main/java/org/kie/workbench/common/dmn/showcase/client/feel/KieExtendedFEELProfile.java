/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.showcase.client.feel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.dmn.feel.gwt.functions.api.FunctionOverrideVariation;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.lang.Symbol;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.runtime.FEELFunction;

class KieExtendedFEELProfile implements FEELProfile {

    private final List<FunctionOverrideVariation> functionOverrideVariations;

    private final Scope scope;

    public KieExtendedFEELProfile(final List<FunctionOverrideVariation> functionOverrideVariations) {
        this.functionOverrideVariations = functionOverrideVariations;

        this.scope = buildScope();
    }

    @Override
    public List<FEELFunction> getFEELFunctions() {
        return functionOverrideVariations
                .stream()
                .map(v -> {
                    return new FEELFunction() {
                        @Override
                        public String getName() {
                            return v.toHumanReadableStrings().getHumanReadable();
                        }

                        @Override
                        public Symbol getSymbol() {
                            return new Symbol() {
                                @Override
                                public String getId() {
                                    return v.toString();
                                }

                                @Override
                                public Type getType() {
                                    return v.getReturnType();
                                }

                                @Override
                                public Scope getScope() {
                                    return scope;
                                }
                            };
                        }

                        @Override
                        public List<List<Param>> getParameters() {
                            final List<List<Param>> lists = new ArrayList<>();
                            lists.add(
                                    v.getParameters()
                                            .stream()
                                            .map(p -> new Param(p.getName(), p.getType()))
                                            .collect(Collectors.toList())
                            );
                            return lists;
                        }

                        @Override
                        public Object invokeReflectively(final EvaluationContext ctx, final Object[] params) {
                            return null;
                        }
                    };
                })
                .collect(Collectors.toList());
    }

    private Scope buildScope() {
        return new Scope() {
            @Override
            public String getName() {
                return Scope.GLOBAL;
            }

            @Override
            public Scope getParentScope() {
                return null;
            }

            @Override
            public void addChildScope(final Scope scope) {

            }

            @Override
            public Map<String, Scope> getChildScopes() {
                return null;
            }

            @Override
            public boolean define(final Symbol symbol) {
                return false;
            }

            @Override
            public Symbol resolve(final String id) {
                return null;
            }

            @Override
            public Symbol resolve(final String[] qualifiedName) {
                return null;
            }

            @Override
            public void start(final String token) {

            }

            @Override
            public boolean followUp(final String token, final boolean isPredict) {
                return false;
            }

            @Override
            public Map<String, Symbol> getSymbols() {
                return null;
            }

            @Override
            public Type getType() {
                return null;
            }
        };
    }
}
