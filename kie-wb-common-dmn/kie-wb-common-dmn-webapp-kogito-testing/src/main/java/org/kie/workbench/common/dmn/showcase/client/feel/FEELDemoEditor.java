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

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.dmn.feel.gwt.functions.api.FunctionOverrideVariation;
import org.kie.dmn.feel.gwt.functions.client.FEELFunctionProvider;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.Candidate;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.FEELLanguageService;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.FEELLanguageService.Position;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

@ApplicationScoped
@WorkbenchScreen(identifier = FEELDemoEditor.EDITOR_ID)
public class FEELDemoEditor {

    public static final String EDITOR_ID = "org.kie.workbench.common.dmn.showcase.client.feel.FEELDemoEditor";

    private static final String CURSOR_CHARACTER = "|";

    private final View view;

    private final FEELLanguageService feelLanguageService;

    @Inject
    public FEELDemoEditor(final View view,
                          final FEELLanguageService feelLanguageService) {
        this.view = view;
        this.feelLanguageService = feelLanguageService;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        view.setFunctions(getFunctions());
    }

    public void onTextChange(final String text) {

        final Position position = getPosition(text);
        final String expression = text.replaceAll("\\|", "");
        final FEEL_1_1Parser parser = FEELParser.parse(null,
                                                       expression,
                                                       Collections.emptyMap(),
                                                       Collections.emptyMap(),
                                                       Collections.emptyList(),
                                                       Collections.emptyList(),
                                                       null);
        final ParseTree tree = parser.expression();
        final ASTBuilderVisitor astBuilderVisitor = new ASTBuilderVisitor(emptyMap(), null);
        final BaseNode baseNode = astBuilderVisitor.visit(tree);

        view.setSuggestions(getSuggestions(expression, position));
        view.setNodes(getNodesString(baseNode));
        view.setEvaluation(getEvaluation(baseNode));
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "FEEL Editor";
    }

    @WorkbenchPartView
    public View getWidget() {
        return view;
    }

    private String getSuggestions(final String expression,
                                  final Position position) {
        try {

            final StringBuilder str = new StringBuilder();
            for (final Candidate candidate : feelLanguageService.getCandidates(expression, emptyList(), position)) {
                str.append(candidate.getLabel());
                str.append(": ");
                str.append(candidate.getKind().toString());
                str.append("\n");
            }

            return str.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String getFunctions() {

        final StringBuilder str = new StringBuilder();
        final List<FunctionOverrideVariation> definitions = getFunctionOverrideVariations();

        for (final FunctionOverrideVariation definition : definitions) {
            str.append(definition.toHumanReadableStrings().getHumanReadable());
            str.append(": ");
            str.append(definition.getReturnType().getName());
            str.append("\n");
        }

        return str.toString();
    }

    List<FunctionOverrideVariation> getFunctionOverrideVariations() {
        final FEELFunctionProvider functionProvider = GWT.create(FEELFunctionProvider.class);
        return functionProvider.getDefinitions();
    }

    private String getEvaluation(final BaseNode baseNode) {
        try {
            final DMNDTAnalyserValueFromNodeVisitor visitor = new DMNDTAnalyserValueFromNodeVisitor(emptyList());
            return "Evaluation result: " + baseNode.accept(visitor).toString() + "\n" +
                    "Evaluation type:   " + baseNode.getResultType().getName();
        } catch (final Exception e) {
            return "Evaluation error.";
        }
    }

    private Position getPosition(final String text) {

        final String expression = text.contains(CURSOR_CHARACTER) ? text : text + "|";
        final int line = expression.substring(0, expression.indexOf(CURSOR_CHARACTER)).split("\n").length;
        final int column = expression.split("\n")[line - 1].indexOf(CURSOR_CHARACTER);

        return new Position(line, column);
    }

    private String getNodesString(final ASTNode expr) {
        final StringBuilder str = new StringBuilder();
        getNodesString(str, expr, 0);
        return str.toString();
    }

    private void getNodesString(final StringBuilder str,
                                final ASTNode expr,
                                final int level) {
        if (expr == null) {
            return;
        }

        str.append(spaces(level));
        str.append(String.join("", expr.getText().split("\n")).trim().replaceAll(" +", " "));
        str.append(": ");
        str.append(expr.getResultType().getName());
        str.append("\n");

        for (ASTNode astNode : expr.getChildrenNode()) {
            getNodesString(str, astNode, level + 1);
        }
    }

    private String spaces(final int n) {
        final StringBuilder append = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            append.append("  ");
        }
        return append.toString();
    }

    public interface View extends UberElemental<FEELDemoEditor>,
                                  IsElement {

        void setText(String text);

        void setNodes(String nodes);

        void setEvaluation(String evaluation);

        void setFunctions(String functions);

        void setSuggestions(String suggestions);
    }
}
