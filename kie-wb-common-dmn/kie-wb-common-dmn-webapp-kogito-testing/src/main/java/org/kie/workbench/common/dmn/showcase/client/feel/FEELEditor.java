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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.vmware.antlr4c3.CodeCompletionCore;
import elemental2.dom.DomGlobal;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.kie.dmn.feel.gwt.functions.api.FunctionOverrideVariation;
import org.kie.dmn.feel.gwt.functions.client.FEELFunctionProvider;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.lang.types.FEELTypeRegistry;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.ADD;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.ANY_OTHER_CHAR;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.AT;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.BANG;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.BooleanLiteral;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.COLON;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.COMMA;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.COMMENT;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.DIV;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.DOT;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.ELIPSIS;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.EQUAL;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.FloatingPointLiteral;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.GE;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.GT;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.Identifier;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.IntegerLiteral;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.LBRACE;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.LBRACK;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.LE;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.LINE_COMMENT;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.LPAREN;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.LT;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.MUL;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.NOTEQUAL;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.POW;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.QUOTE;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.RARROW;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.RBRACE;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.RBRACK;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.RPAREN;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.SUB;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.StringLiteral;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.WS;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Dependent
@WorkbenchScreen(identifier = FEELEditor.EDITOR_ID)
public class FEELEditor {

    public static final String EDITOR_ID = "org.kie.workbench.common.dmn.showcase.client.editor.FEELEditor";
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

        StringBuilder builder = new StringBuilder();
        for (final FunctionOverrideVariation definition : getFunctionOverrideVariations()) {
            builder.append(definition.getReturnType());
            builder.append(" == ");
            builder.append(definition.toHumanReadableStrings().getTemplate());
            builder.append("\n");
        }
        view.setAvailableMethods(builder.toString());
        // Window.alert("testing " + o.getDefinitions().size());
    }

    private List<FunctionOverrideVariation> getFunctionOverrideVariations() {
        FEELFunctionProvider functionProvider = GWT.create(FEELFunctionProvider.class);
        return functionProvider.getDefinitions();
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
        if (expr == null) {
            return;
        }
        stringBuilder.append(expr.toString());
        stringBuilder.append("\n");
        for (ASTNode astNode : expr.getChildrenNode()) {
            dump(stringBuilder, astNode);
        }
    }

    public void onChange(String text) {

        final int caretPositionLine;
        final int caretPositionColumn;
        if (!isEmpty(view.getRow()) || !isEmpty(view.getColumn())) {
            caretPositionLine = getInt(view.getRow());
            caretPositionColumn = getInt(view.getColumn());
        } else {
            caretPositionLine = view.getCursor().getRow() + 1;
            caretPositionColumn = view.getCursor().getColumn();
        }

        final List<String> keywords = getCandidates(text, caretPositionLine, caretPositionColumn);

        final String suggestions = String.join("\n", keywords);
        view.setC3(suggestions);
    }

    List<String> getCandidates(final String text, final int caretPositionLine, final int caretPositionColumn) {
        // Parser
        final FEELEventListenersManager eventsManager = null;
        final Map<String, Type> inputVariableTypes = emptyMap();
        final Map<String, Object> inputVariables = emptyMap();
        final List<FEELProfile> profiles = new ArrayList<>();
//        profiles.add(new KieExtendedFEELProfile(getFunctionOverrideVariations()));
//        KieExtendedFEELProfile e = new KieExtendedFEELProfile();
//        final List<FEELFunction> additionalFunctions = e.getFEELFunctions();
//        profiles.add(e);
        final FEELTypeRegistry typeRegistry = null;
        final FEEL_1_1Parser parser = FEELParser.parse(eventsManager, text, inputVariableTypes, inputVariables, emptyList(), profiles, typeRegistry);

        final ParseTree tree = parser.expression();
        final Map<String, Type> inputTypes = emptyMap();
        final FEELTypeRegistry typeRegistry1 = null;
        final ASTBuilderVisitor astBuilderVisitor = new ASTBuilderVisitor(inputTypes, typeRegistry1);
        final BaseNode expr = astBuilderVisitor.visit(tree);
        final StringBuilder stringBuilder = new StringBuilder();

        dump(stringBuilder, expr);
        view.setASTDump(stringBuilder.toString());
        try {
            DMNDTAnalyserValueFromNodeVisitor v = new DMNDTAnalyserValueFromNodeVisitor(profiles);
            view.setEvaluation(expr.accept(v).toString());
        } catch (Exception e) {
            view.setEvaluation("ERROR EVAL");
        }
        final CodeCompletionCore core = new CodeCompletionCore(parser, null, ignoredTokens());

        DomGlobal.console.log(caretPositionLine + " ___ " + caretPositionColumn);

//        let symbolTable = new SymbolTableVisitor().visit(parseTree);
//        completions.push(...suggestVariables(symbolTable));

        final int caretIndex = computeTokenIndex(tree, caretPositionLine, caretPositionColumn);
        final CodeCompletionCore.CandidatesCollection candidates = core.collectCandidates(caretIndex, parser.getContext());
        final List<String> keywords = new ArrayList<>();
        keywords.add(new Date().toString());
        keywords.addAll(getKeywords(parser, candidates));
        return keywords;
    }

    private int getInt(String i) {
        try {
            return Integer.parseInt(i);
        } catch (Exception e) {
            return -1;
        }
    }

    private List<String> getKeywords(final FEEL_1_1Parser parser,
                                     final CodeCompletionCore.CandidatesCollection candidates) {

        final List<String> keywords = new ArrayList<>();

        for (final Integer integer : candidates.tokens.keySet()) {
//            String reduce = entry.getValue().stream().map(Object::toString).reduce((i, j) -> i + ", " + j).orElse("");
            final String displayName = getDisplayName(parser, integer);
            keywords.add(displayName);
        }

        for (Integer integer : candidates.rules.keySet()) {
            final String displayName = getDisplayName(parser, integer);
            keywords.add(displayName);
        }

        for (Integer integer : candidates.rulePositions.keySet()) {
            final String displayName = getDisplayName(parser, integer);
            keywords.add(displayName);
        }
        return keywords;
    }

    private String getDisplayName(final FEEL_1_1Parser parser, final Integer integer) {
        return parser.getVocabulary().getDisplayName(integer).replace("'", "");
    }

    private Set<Integer> ignoredTokens() {
        Set<Integer> ignoredTokens = new HashSet<>();
        ignoredTokens.add(ADD);
        ignoredTokens.add(ANY_OTHER_CHAR);
        ignoredTokens.add(AT);
        ignoredTokens.add(BANG);
        ignoredTokens.add(BooleanLiteral);
        ignoredTokens.add(COLON);
        ignoredTokens.add(COMMA);
        ignoredTokens.add(COMMENT);
        ignoredTokens.add(DIV);
        ignoredTokens.add(DOT);
        ignoredTokens.add(ELIPSIS);
        ignoredTokens.add(EQUAL);
        ignoredTokens.add(FloatingPointLiteral);
        ignoredTokens.add(GE);
        ignoredTokens.add(GT);
        ignoredTokens.add(Identifier);
        ignoredTokens.add(IntegerLiteral);
        ignoredTokens.add(LBRACE);
        ignoredTokens.add(LBRACK);
        ignoredTokens.add(LE);
        ignoredTokens.add(LINE_COMMENT);
        ignoredTokens.add(LPAREN);
        ignoredTokens.add(LT);
        ignoredTokens.add(MUL);
        ignoredTokens.add(NOTEQUAL);
        ignoredTokens.add(POW);
        ignoredTokens.add(QUOTE);
        ignoredTokens.add(RARROW);
        ignoredTokens.add(RBRACE);
        ignoredTokens.add(RBRACK);
        ignoredTokens.add(RPAREN);
        ignoredTokens.add(StringLiteral);
        ignoredTokens.add(SUB);
        ignoredTokens.add(WS);
        return ignoredTokens;
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
            return 0;
        }
    }

    private Integer computeTokenIndexOfChildNode(ParseTree parseTree, int caretPositionLine, int caretPositionColumn) {
        for (int i = 0; i < parseTree.getChildCount(); i++) {
            Integer index = computeTokenIndex(parseTree.getChild(i), caretPositionLine, caretPositionColumn);
            if (index != 0) {
                return index;
            }
        }
        return 0;
    }
}