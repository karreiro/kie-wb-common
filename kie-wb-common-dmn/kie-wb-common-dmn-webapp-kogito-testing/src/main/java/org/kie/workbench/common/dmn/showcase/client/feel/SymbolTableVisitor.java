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

import elemental2.dom.DomGlobal;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Visitor;

public class SymbolTableVisitor extends AbstractParseTreeVisitor<SymbolTable> implements FEEL_1_1Visitor<SymbolTable> {

    private final SymbolTable symbolTable;
    private final Scope scope;

    public SymbolTableVisitor() {
        DomGlobal.console.log("~~~>>Visitor() {");
        symbolTable = new SymbolTable();
        scope = symbolTable.getGlobalScope();
    }

    @Override
    public SymbolTable visitCompilation_unit(final FEEL_1_1Parser.Compilation_unitContext ctx) {
        DomGlobal.console.log("~~~>> visitCompilation_unit");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitExpressionTextual(final FEEL_1_1Parser.ExpressionTextualContext ctx) {
//        ctx.expr.
//        int start =
//        int stop = ctx.expr.getStop().getType();
//        symbolTable.addNewSymbolOfType(null, String.valueOf(start));
//        symbolTable.addNewSymbolOfType(null, String.valueOf(stop));
        DomGlobal.console.log("~~~>> ", ctx.expr.getParent().getRuleContext().getRuleIndex());
//        extracted(ctx.expr.getParent().getRuleContext().getRuleIndex());
        extracted(ctx.expr.getStart().getType());
        extracted(ctx.expr.getStop().getType());
        extracted(ctx.expr.getAltNumber());
//        extracted(ctx.expr.start());
        return defaultVisit();
    }

    private void extracted(final int i) {
        symbolTable.addNewSymbolOfType(null, String.valueOf(i));
    }

    @Override
    public SymbolTable visitTextualExpression(final FEEL_1_1Parser.TextualExpressionContext ctx) {
        DomGlobal.console.log("~~~>> visitTextualExpression");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitParametersEmpty(final FEEL_1_1Parser.ParametersEmptyContext ctx) {
        DomGlobal.console.log("~~~>> visitParametersEmpty");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitParametersNamed(final FEEL_1_1Parser.ParametersNamedContext ctx) {
        DomGlobal.console.log("~~~>> visitParametersNamed");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitParametersPositional(final FEEL_1_1Parser.ParametersPositionalContext ctx) {
        DomGlobal.console.log("~~~>> visitParametersPositional");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitNamedParameters(final FEEL_1_1Parser.NamedParametersContext ctx) {
        DomGlobal.console.log("~~~>> visitNamedParameters");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitNamedParameter(final FEEL_1_1Parser.NamedParameterContext ctx) {
        DomGlobal.console.log("~~~>> visitNamedParameter");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPositionalParameters(final FEEL_1_1Parser.PositionalParametersContext ctx) {
        DomGlobal.console.log("~~~>> visitPositionalParameters");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitForExpression(final FEEL_1_1Parser.ForExpressionContext ctx) {
        DomGlobal.console.log("~~~>> visitForExpression");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitIterationContexts(final FEEL_1_1Parser.IterationContextsContext ctx) {
        DomGlobal.console.log("~~~>> visitIterationContexts");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitIterationContext(final FEEL_1_1Parser.IterationContextContext ctx) {
        DomGlobal.console.log("~~~>> visitIterationContext");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitIfExpression(final FEEL_1_1Parser.IfExpressionContext ctx) {
        DomGlobal.console.log("~~~>> visitIfExpression");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitQuantExprSome(final FEEL_1_1Parser.QuantExprSomeContext ctx) {
        DomGlobal.console.log("~~~>> visitQuantExprSome");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitQuantExprEvery(final FEEL_1_1Parser.QuantExprEveryContext ctx) {
        DomGlobal.console.log("~~~>> visitQuantExprEvery");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitListType(final FEEL_1_1Parser.ListTypeContext ctx) {
        DomGlobal.console.log("~~~>> visitListType");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitContextType(final FEEL_1_1Parser.ContextTypeContext ctx) {
        DomGlobal.console.log("~~~>> visitContextType");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitQnType(final FEEL_1_1Parser.QnTypeContext ctx) {
        DomGlobal.console.log("~~~>> visitQnType");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitFunctionType(final FEEL_1_1Parser.FunctionTypeContext ctx) {
        DomGlobal.console.log("~~~>> visitFunctionType");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitList(final FEEL_1_1Parser.ListContext ctx) {
        DomGlobal.console.log("~~~>> visitList");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitFunctionDefinition(final FEEL_1_1Parser.FunctionDefinitionContext ctx) {
        DomGlobal.console.log("~~~>> visitFunctionDefinition");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitFormalParameters(final FEEL_1_1Parser.FormalParametersContext ctx) {
        DomGlobal.console.log("~~~>> visitFormalParameters");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitFormalParameter(final FEEL_1_1Parser.FormalParameterContext ctx) {
        DomGlobal.console.log("~~~>> visitFormalParameter");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitContext(final FEEL_1_1Parser.ContextContext ctx) {
        DomGlobal.console.log("~~~>> visitContext");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitContextEntries(final FEEL_1_1Parser.ContextEntriesContext ctx) {
        DomGlobal.console.log("~~~>> visitContextEntries");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitContextEntry(final FEEL_1_1Parser.ContextEntryContext ctx) {
        DomGlobal.console.log("~~~>> visitContextEntry");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitKeyName(final FEEL_1_1Parser.KeyNameContext ctx) {
        DomGlobal.console.log("~~~>> visitKeyName");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitKeyString(final FEEL_1_1Parser.KeyStringContext ctx) {
        DomGlobal.console.log("~~~>> visitKeyString");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitNameDefinition(final FEEL_1_1Parser.NameDefinitionContext ctx) {
        DomGlobal.console.log("~~~>> visitNameDefinition");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitNameDefinitionWithEOF(final FEEL_1_1Parser.NameDefinitionWithEOFContext ctx) {
        DomGlobal.console.log("~~~>> visitNameDefinitionWithEOF");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitNameDefinitionTokens(final FEEL_1_1Parser.NameDefinitionTokensContext ctx) {
        DomGlobal.console.log("~~~>> visitNameDefinitionTokens");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitIterationNameDefinition(final FEEL_1_1Parser.IterationNameDefinitionContext ctx) {
        DomGlobal.console.log("~~~>> visitIterationNameDefinition");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitIterationNameDefinitionTokens(final FEEL_1_1Parser.IterationNameDefinitionTokensContext ctx) {
        DomGlobal.console.log("~~~>> visitIterationNameDefinitionTokens");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitAdditionalNameSymbol(final FEEL_1_1Parser.AdditionalNameSymbolContext ctx) {
        DomGlobal.console.log("~~~>> visitAdditionalNameSymbol");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitCondOr(final FEEL_1_1Parser.CondOrContext ctx) {
        DomGlobal.console.log("~~~>> visitCondOr");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitCondOrAnd(final FEEL_1_1Parser.CondOrAndContext ctx) {
        DomGlobal.console.log("~~~>> visitCondOrAnd");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitCondAndComp(final FEEL_1_1Parser.CondAndCompContext ctx) {
        DomGlobal.console.log("~~~>> visitCondAndComp");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitCondAnd(final FEEL_1_1Parser.CondAndContext ctx) {
        DomGlobal.console.log("~~~>> visitCondAnd");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitCompExpression(final FEEL_1_1Parser.CompExpressionContext ctx) {
        DomGlobal.console.log("~~~>> visitCompExpression");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitCompExpressionRel(final FEEL_1_1Parser.CompExpressionRelContext ctx) {
        DomGlobal.console.log("~~~>> visitCompExpressionRel");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitRelExpressionBetween(final FEEL_1_1Parser.RelExpressionBetweenContext ctx) {
        DomGlobal.console.log("~~~>> visitRelExpressionBetween");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitRelExpressionValue(final FEEL_1_1Parser.RelExpressionValueContext ctx) {
        DomGlobal.console.log("~~~>> visitRelExpressionValue");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitRelExpressionTestList(final FEEL_1_1Parser.RelExpressionTestListContext ctx) {
        DomGlobal.console.log("~~~>> visitRelExpressionTestList");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitRelExpressionAdd(final FEEL_1_1Parser.RelExpressionAddContext ctx) {
        DomGlobal.console.log("~~~>> visitRelExpressionAdd");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitRelExpressionInstanceOf(final FEEL_1_1Parser.RelExpressionInstanceOfContext ctx) {
        DomGlobal.console.log("~~~>> visitRelExpressionInstanceOf");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitExpressionList(final FEEL_1_1Parser.ExpressionListContext ctx) {
        DomGlobal.console.log("~~~>> visitExpressionList");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitAddExpressionMult(final FEEL_1_1Parser.AddExpressionMultContext ctx) {
        DomGlobal.console.log("~~~>> visitAddExpressionMult");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitAddExpression(final FEEL_1_1Parser.AddExpressionContext ctx) {
        DomGlobal.console.log("~~~>> visitAddExpression");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitMultExpressionPow(final FEEL_1_1Parser.MultExpressionPowContext ctx) {
        DomGlobal.console.log("~~~>> visitMultExpressionPow");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitMultExpression(final FEEL_1_1Parser.MultExpressionContext ctx) {
        DomGlobal.console.log("~~~>> visitMultExpression");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPowExpressionUnary(final FEEL_1_1Parser.PowExpressionUnaryContext ctx) {
        DomGlobal.console.log("~~~>> visitPowExpressionUnary");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPowExpression(final FEEL_1_1Parser.PowExpressionContext ctx) {
        DomGlobal.console.log("~~~>> visitPowExpression");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitFilterPathExpression(final FEEL_1_1Parser.FilterPathExpressionContext ctx) {
        DomGlobal.console.log("~~~>> visitFilterPathExpression");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitSignedUnaryExpressionPlus(final FEEL_1_1Parser.SignedUnaryExpressionPlusContext ctx) {
        DomGlobal.console.log("~~~>> visitSignedUnaryExpressionPlus");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitSignedUnaryExpressionMinus(final FEEL_1_1Parser.SignedUnaryExpressionMinusContext ctx) {
        DomGlobal.console.log("~~~>> visitSignedUnaryExpressionMinus");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitFnInvocation(final FEEL_1_1Parser.FnInvocationContext ctx) {
        DomGlobal.console.log("~~~>> visitFnInvocation");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitNonSignedUnaryExpression(final FEEL_1_1Parser.NonSignedUnaryExpressionContext ctx) {
        DomGlobal.console.log("~~~>> visitNonSignedUnaryExpression");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitUenpmPrimary(final FEEL_1_1Parser.UenpmPrimaryContext ctx) {
        DomGlobal.console.log("~~~>> visitUenpmPrimary");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPrimaryLiteral(final FEEL_1_1Parser.PrimaryLiteralContext ctx) {
        DomGlobal.console.log("~~~>> visitPrimaryLiteral");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPrimaryForExpression(final FEEL_1_1Parser.PrimaryForExpressionContext ctx) {
        DomGlobal.console.log("~~~>> visitPrimaryForExpression");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPrimaryQuantifiedExpression(final FEEL_1_1Parser.PrimaryQuantifiedExpressionContext ctx) {
        DomGlobal.console.log("~~~>> visitPrimaryQuantifiedExpression");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPrimaryIfExpression(final FEEL_1_1Parser.PrimaryIfExpressionContext ctx) {
        DomGlobal.console.log("~~~>> visitPrimaryIfExpression");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPrimaryInterval(final FEEL_1_1Parser.PrimaryIntervalContext ctx) {
        DomGlobal.console.log("~~~>> visitPrimaryInterval");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPrimaryList(final FEEL_1_1Parser.PrimaryListContext ctx) {
        DomGlobal.console.log("~~~>> visitPrimaryList");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPrimaryContext(final FEEL_1_1Parser.PrimaryContextContext ctx) {
        DomGlobal.console.log("~~~>> visitPrimaryContext");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPrimaryParens(final FEEL_1_1Parser.PrimaryParensContext ctx) {
        DomGlobal.console.log("~~~>> visitPrimaryParens");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPrimaryUnaryTest(final FEEL_1_1Parser.PrimaryUnaryTestContext ctx) {
        DomGlobal.console.log("~~~>> visitPrimaryUnaryTest");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPrimaryName(final FEEL_1_1Parser.PrimaryNameContext ctx) {
        DomGlobal.console.log("~~~>> visitPrimaryName");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitNumberLiteral(final FEEL_1_1Parser.NumberLiteralContext ctx) {
        DomGlobal.console.log("~~~>> visitNumberLiteral");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitBoolLiteral(final FEEL_1_1Parser.BoolLiteralContext ctx) {
        DomGlobal.console.log("~~~>> visitBoolLiteral");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitAtLiteralLabel(final FEEL_1_1Parser.AtLiteralLabelContext ctx) {
        DomGlobal.console.log("~~~>> visitAtLiteralLabel");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitStringLiteral(final FEEL_1_1Parser.StringLiteralContext ctx) {
        DomGlobal.console.log("~~~>> visitStringLiteral");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitNullLiteral(final FEEL_1_1Parser.NullLiteralContext ctx) {
        DomGlobal.console.log("~~~>> visitNullLiteral");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitAtLiteral(final FEEL_1_1Parser.AtLiteralContext ctx) {
        DomGlobal.console.log("~~~>> visitAtLiteral");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitAtLiteralValue(final FEEL_1_1Parser.AtLiteralValueContext ctx) {
        DomGlobal.console.log("~~~>> visitAtLiteralValue");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPositiveUnaryTestIneqInterval(final FEEL_1_1Parser.PositiveUnaryTestIneqIntervalContext ctx) {
        DomGlobal.console.log("~~~>> visitPositiveUnaryTestIneqInterval");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPositiveUnaryTestIneq(final FEEL_1_1Parser.PositiveUnaryTestIneqContext ctx) {
        DomGlobal.console.log("~~~>> visitPositiveUnaryTestIneq");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPositiveUnaryTestInterval(final FEEL_1_1Parser.PositiveUnaryTestIntervalContext ctx) {
        DomGlobal.console.log("~~~>> visitPositiveUnaryTestInterval");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitSimplePositiveUnaryTests(final FEEL_1_1Parser.SimplePositiveUnaryTestsContext ctx) {
        DomGlobal.console.log("~~~>> visitSimplePositiveUnaryTests");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPositiveSimplePositiveUnaryTests(final FEEL_1_1Parser.PositiveSimplePositiveUnaryTestsContext ctx) {
        DomGlobal.console.log("~~~>> visitPositiveSimplePositiveUnaryTests");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitNegatedSimplePositiveUnaryTests(final FEEL_1_1Parser.NegatedSimplePositiveUnaryTestsContext ctx) {
        DomGlobal.console.log("~~~>> visitNegatedSimplePositiveUnaryTests");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPositiveUnaryTestDash(final FEEL_1_1Parser.PositiveUnaryTestDashContext ctx) {
        DomGlobal.console.log("~~~>> visitPositiveUnaryTestDash");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPositiveUnaryTest(final FEEL_1_1Parser.PositiveUnaryTestContext ctx) {
        DomGlobal.console.log("~~~>> visitPositiveUnaryTest");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitPositiveUnaryTests(final FEEL_1_1Parser.PositiveUnaryTestsContext ctx) {
        DomGlobal.console.log("~~~>> visitPositiveUnaryTests");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitUnaryTestsRoot(final FEEL_1_1Parser.UnaryTestsRootContext ctx) {
        DomGlobal.console.log("~~~>> visitUnaryTestsRoot");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitUnaryTests_negated(final FEEL_1_1Parser.UnaryTests_negatedContext ctx) {
        DomGlobal.console.log("~~~>> visitUnaryTests_negated");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitUnaryTests_positive(final FEEL_1_1Parser.UnaryTests_positiveContext ctx) {
        DomGlobal.console.log("~~~>> visitUnaryTests_positive");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitUnaryTests_empty(final FEEL_1_1Parser.UnaryTests_emptyContext ctx) {
        DomGlobal.console.log("~~~>> visitUnaryTests_empty");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitEndpoint(final FEEL_1_1Parser.EndpointContext ctx) {
        DomGlobal.console.log("~~~>> visitEndpoint");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitInterval(final FEEL_1_1Parser.IntervalContext ctx) {
        DomGlobal.console.log("~~~>> visitInterval");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitQualifiedName(final FEEL_1_1Parser.QualifiedNameContext ctx) {
        DomGlobal.console.log("~~~>> visitQualifiedName");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitNameRef(final FEEL_1_1Parser.NameRefContext ctx) {
        DomGlobal.console.log("~~~>> visitNameRef");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitNameRefOtherToken(final FEEL_1_1Parser.NameRefOtherTokenContext ctx) {
        DomGlobal.console.log("~~~>> visitNameRefOtherToken");
        return defaultVisit();
    }

    @Override
    public SymbolTable visitReusableKeywords(final FEEL_1_1Parser.ReusableKeywordsContext ctx) {
        DomGlobal.console.log("~~~>> visitReusableKeywords");
        return defaultVisit();
    }

    private SymbolTable defaultVisit() {
        DomGlobal.console.log("private SymbolTable defaultVisit() {");
        return symbolTable;
    }
}
