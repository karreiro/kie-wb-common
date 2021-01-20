/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.showcase.client.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.DomGlobal;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.kie.dmn.feel.gwt.functions.api.FunctionOverrideVariation;
import org.kie.dmn.feel.gwt.functions.client.FEELFunctionProvider;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.views.pfly.widgets.Moment;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = FEELEditor.EDITOR_ID)
public class FEELEditor {

    public static final String EDITOR_ID = "test.FEELEditor";
    private FeelEditorView view;

    @Inject
    public FEELEditor(FeelEditorView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @PostConstruct
    public void init() {
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {

        FEELFunctionProvider functionProvider = GWT.create(FEELFunctionProvider.class);
        StringBuilder builder = new StringBuilder();
        for (final FunctionOverrideVariation definition : functionProvider.getDefinitions()) {

            builder.append(definition.getReturnType());
            builder.append(" - ");
            builder.append(definition.toHumanReadableStrings().getTemplate());
            builder.append("\n");
        }
        view.setAvailableMethods(builder.toString());
//        Window.alert("testing " + o.getDefinitions().size());
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "FEEL Editor";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view.asWidget();
    }

    public void onClick(String text) {
    }

    private void dump(final StringBuilder stringBuilder,
                      final ASTNode expr) {
        stringBuilder.append(expr.toString());
        stringBuilder.append("\n");
        for (ASTNode astNode : expr.getChildrenNode()) {
            dump(stringBuilder, astNode);
        }
    }

    public void onChange(String text) {
        final FEEL_1_1Parser parser = FEELParser.parse(null,
                                                       text,
                                                       Collections.emptyMap(),
                                                       Collections.emptyMap(),
                                                       Collections.emptyList(),
                                                       Collections.emptyList(),
                                                       null);

        final ParseTree tree = parser.expression();
        final ASTBuilderVisitor v = new ASTBuilderVisitor(Collections.emptyMap(),
                                                          null);
        final BaseNode expr = v.visit(tree);

        final StringBuilder stringBuilder = new StringBuilder();
        dump(stringBuilder, expr);
        view.setASTDump(stringBuilder.toString());

//        view.setEvaluation(expr.accept(new DMNDTAnalyserValueFromNodeVisitor(Collections.EMPTY_LIST)).toString());

        CodeCompletionCore core = new CodeCompletionCore(parser, null, null);

        int caretPositionLine = -1;
        try {
            caretPositionLine = view.getCursor().getRow();
        } catch (Exception e) {
            DomGlobal.console.log("______>> 1");
        }
        int caretPositionColumn = -1;
        try {
            caretPositionColumn = view.getCursor().getColumn();
        } catch (Exception e) {
            DomGlobal.console.log("______>> 2");
        }

        int caretIndex = computeTokenIndex(tree, caretPositionLine, caretPositionColumn);
        DomGlobal.console.log("Line => " + caretPositionLine + ", Column => " + caretPositionColumn);

        CodeCompletionCore.CandidatesCollection candidates = core.collectCandidates(caretIndex, parser.getContext());

        List<String> keywords = new ArrayList<>();
        String test = "===" + Moment.Builder.moment().format("HH:mm:ss.SSS") + "===" + caretIndex;
        test += "-------";
        for (Map.Entry<Integer, List<Integer>> entry : candidates.tokens.entrySet()) {
            Integer key = entry.getKey();
            List<Integer> value = entry.getValue();
            String reduce = value.stream().map(Object::toString).reduce((i, j) -> {
                return i.toString() + ", " + j.toString();
            }).orElse("");
            String displayName = parser.getVocabulary().getDisplayName(key) + ", key =>" + key.toString() + ", value =>" + reduce;
            ;
//            keywords.add(displayName);]
            if (displayName != null) {
                test = test + "\n " + displayName;
            }
        }
        test += "-------" + candidates.rules.keySet().size();
        for (Integer integer : candidates.rules.keySet()) {
            String displayName = parser.getVocabulary().getSymbolicName(integer) + " - " + parser.getVocabulary().getDisplayName(integer) + " - " + parser.getVocabulary().getLiteralName(integer);
            test = test + "\n " + displayName;
//            test = test + "\n " + displayName + " -- " + candidates.rules.get(integer);
        }

//        DomGlobal.console.log("\n\n===== SUGGESTIONS");
//        DomGlobal.console.log(test);

//        view.setC3("caretIndex [ " + caretIndex + " ] " + core.candidates.toString());
        view.setC3(test);
    }

    private Integer computeTokenIndex(ParseTree parseTree, int caretPositionLine, int caretPositionColumn) {
        if (parseTree instanceof TerminalNode) {
            return computeTokenIndexOfTerminalNode((TerminalNode) parseTree, caretPositionLine, caretPositionColumn);
        } else {
            return computeTokenIndexOfChildNode(parseTree, caretPositionLine, caretPositionColumn);
        }
    }

    private Integer computeTokenIndexOfTerminalNode(TerminalNode parseTree, int caretPositionLine, int caretPositionColumn) {
        int start = parseTree.getSymbol().getCharPositionInLine();
        int stop = parseTree.getSymbol().getCharPositionInLine() + parseTree.getText().length();
        if (parseTree.getSymbol().getLine() == caretPositionLine && start <= caretPositionColumn && stop >= caretPositionColumn) {
            return parseTree.getSymbol().getTokenIndex();
        } else {
            return null;
        }
    }

    private Integer computeTokenIndexOfChildNode(ParseTree parseTree, int caretPositionLine, int caretPositionColumn) {
        for (int i = 0; i < parseTree.getChildCount(); i++) {
            Integer index = computeTokenIndex(parseTree.getChild(i), caretPositionLine, caretPositionColumn);
            if (index != null) {
                return index;
            }
        }
        return null;
    }
}
