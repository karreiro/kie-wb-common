/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.core.RegExp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco.jsenumerations.MonacoCompletionItemInsertTextRule;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco.jsenumerations.MonacoCompletionItemKind;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco.jsinterop.MonacoLanguages.ProvideCompletionItemsFunction;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco.MonacoPropertiesFactory.FEEL_LANGUAGE_ID;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco.MonacoPropertiesFactory.FEEL_THEME_ID;
import static org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco.MonacoPropertiesFactory.VS_EDITOR_EDITOR_MAIN_MODULE;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MonacoPropertiesFactoryTest {

    private MonacoPropertiesFactory factory;

    @Before
    public void setup() {
        factory = spy(new MonacoPropertiesFactory());
    }

    @Test
    public void testGetConstructionOptions() {

        final JSONObject options = mock(JSONObject.class);
        final JSONObject scrollbar = mock(JSONObject.class);
        final JSONObject miniMap = mock(JSONObject.class);
        final JSONString language = mock(JSONString.class);
        final JSONString theme = mock(JSONString.class);
        final JSONString renderLineHighlight = mock(JSONString.class);
        final JSONString lineNumbers = mock(JSONString.class);
        final JSONBoolean overviewRulerBorder = mock(JSONBoolean.class);
        final JSONBoolean scrollBeyondLastLine = mock(JSONBoolean.class);
        final JSONBoolean snippetSuggestions = mock(JSONBoolean.class);
        final JSONBoolean useTabStops = mock(JSONBoolean.class);
        final JSONBoolean contextmenu = mock(JSONBoolean.class);
        final JSONBoolean folding = mock(JSONBoolean.class);
        final JSONBoolean enabled = mock(JSONBoolean.class);
        final JSONBoolean useShadows = mock(JSONBoolean.class);
        final JSONValue fontSize = mock(JSONValue.class);
        final JSONValue lineNumbersMinChars = mock(JSONValue.class);
        final JSONValue lineDecorationsWidth = mock(JSONValue.class);
        final JSONBoolean automaticLayout = mock(JSONBoolean.class);
        final JSONBoolean renderWhitespace = mock(JSONBoolean.class);
        final JSONBoolean hideCursorInOverviewRuler = mock(JSONBoolean.class);
        final JavaScriptObject expectedOptions = mock(JavaScriptObject.class);

        doReturn(language).when(factory).makeJSONString(FEEL_LANGUAGE_ID);
        doReturn(theme).when(factory).makeJSONString(FEEL_THEME_ID);
        doReturn(renderLineHighlight).when(factory).makeJSONString("none");
        doReturn(lineNumbers).when(factory).makeJSONString("off");
        doReturn(fontSize).when(factory).makeJSONNumber(14);

        when(options.getJavaScriptObject()).thenReturn(expectedOptions);

        // mocking spy with when(...).thenReturn(...) because the many calls are being mocked.
        when(factory.makeJSONObject()).thenReturn(options, scrollbar, miniMap);
        when(factory.makeJSONNumber(1)).thenReturn(lineNumbersMinChars, lineDecorationsWidth);
        when(factory.makeJSONBoolean(false)).thenReturn(overviewRulerBorder, scrollBeyondLastLine, snippetSuggestions, useTabStops, contextmenu, folding, enabled, useShadows);
        when(factory.makeJSONBoolean(true)).thenReturn(automaticLayout, renderWhitespace, hideCursorInOverviewRuler);

        final JavaScriptObject actualOptions = factory.getConstructionOptions();

        verify(options).put("language", language);
        verify(options).put("theme", theme);
        verify(options).put("renderLineHighlight", renderLineHighlight);
        verify(options).put("fontSize", fontSize);
        verify(options).put("lineNumbersMinChars", lineNumbersMinChars);
        verify(options).put("lineDecorationsWidth", lineDecorationsWidth);
        verify(options).put("overviewRulerBorder", overviewRulerBorder);
        verify(options).put("scrollBeyondLastLine", scrollBeyondLastLine);
        verify(options).put("snippetSuggestions", snippetSuggestions);
        verify(options).put("useTabStops", useTabStops);
        verify(options).put("contextmenu", contextmenu);
        verify(options).put("folding", folding);
        verify(miniMap).put("enabled", enabled);
        verify(scrollbar).put("useShadows", useShadows);
        verify(options).put("automaticLayout", automaticLayout);
        verify(options).put("renderWhitespace", renderWhitespace);
        verify(options).put("hideCursorInOverviewRuler", hideCursorInOverviewRuler);

        assertEquals(expectedOptions, actualOptions);
    }

    @Test
    public void testGetThemeData() {

        final JSONObject themeDefinition = mock(JSONObject.class);
        final JSONObject colors = mock(JSONObject.class);
        final JSONString colorHEXCode = mock(JSONString.class);
        final JSONString base = mock(JSONString.class);
        final JSONBoolean inherit = mock(JSONBoolean.class);
        final JSONArray rules = mock(JSONArray.class);
        final JavaScriptObject expectedEditorThemeData = mock(JavaScriptObject.class);

        doReturn(colorHEXCode).when(factory).makeJSONString("#000000");
        doReturn(base).when(factory).makeJSONString("vs");
        doReturn(inherit).when(factory).makeJSONBoolean(false);
        doReturn(rules).when(factory).getRules();

        when(themeDefinition.getJavaScriptObject()).thenReturn(expectedEditorThemeData);

        // mocking spy with when(...).thenReturn(...) because the many calls are being mocked.
        when(factory.makeJSONObject()).thenReturn(themeDefinition, colors);

        final JavaScriptObject actualEditorThemeData = factory.getThemeData();

        verify(colors).put("editorLineNumber.foreground", colorHEXCode);
        verify(themeDefinition).put("base", base);
        verify(themeDefinition).put("inherit", inherit);
        verify(themeDefinition).put("rules", rules);
        verify(themeDefinition).put("colors", colors);

        assertEquals(expectedEditorThemeData, actualEditorThemeData);
    }

    @Test
    public void testGetRules() {

        final JSONObject rule1 = mock(JSONObject.class);
        final JSONObject rule2 = mock(JSONObject.class);
        final JSONObject rule3 = mock(JSONObject.class);
        final JSONObject rule4 = mock(JSONObject.class);
        final JSONObject rule5 = mock(JSONObject.class);
        final JSONString token1 = mock(JSONString.class);
        final JSONString foreground1 = mock(JSONString.class);
        final JSONString fontStyle1 = mock(JSONString.class);
        final JSONString token2 = mock(JSONString.class);
        final JSONString foreground2 = mock(JSONString.class);
        final JSONString token3 = mock(JSONString.class);
        final JSONString foreground3 = mock(JSONString.class);
        final JSONString token4 = mock(JSONString.class);
        final JSONString foreground4 = mock(JSONString.class);
        final JSONString token5 = mock(JSONString.class);
        final JSONString foreground5 = mock(JSONString.class);
        final JSONArray expectedRules = mock(JSONArray.class);

        doReturn(token1).when(factory).makeJSONString("feel-keyword");
        doReturn(foreground1).when(factory).makeJSONString("ec5b69");
        doReturn(fontStyle1).when(factory).makeJSONString("bold");
        doReturn(token2).when(factory).makeJSONString("feel-numeric");
        doReturn(foreground2).when(factory).makeJSONString("005cc5");
        doReturn(token3).when(factory).makeJSONString("feel-boolean");
        doReturn(foreground3).when(factory).makeJSONString("d73a49");
        doReturn(token4).when(factory).makeJSONString("feel-string");
        doReturn(foreground4).when(factory).makeJSONString("22863a");
        doReturn(token5).when(factory).makeJSONString("feel-function");
        doReturn(foreground5).when(factory).makeJSONString("6f42c1");
        doReturn(expectedRules).when(factory).makeJSONArray();

        // mocking spy with when(...).thenReturn(...) because the many calls are being mocked.
        when(factory.makeJSONObject()).thenReturn(rule1, rule2, rule3, rule4, rule5);

        final JSONArray actualRules = factory.getRules();

        verify(rule1).put("token", token1);
        verify(rule1).put("foreground", foreground1);
        verify(rule1).put("fontStyle", fontStyle1);
        verify(rule2).put("token", token2);
        verify(rule2).put("foreground", foreground2);
        verify(rule3).put("token", token3);
        verify(rule3).put("foreground", foreground3);
        verify(rule4).put("token", token4);
        verify(rule4).put("foreground", foreground4);
        verify(rule5).put("token", token5);
        verify(rule5).put("foreground", foreground5);
        verify(factory).push(expectedRules, rule1);
        verify(factory).push(expectedRules, rule2);
        verify(factory).push(expectedRules, rule3);
        verify(factory).push(expectedRules, rule4);
        verify(factory).push(expectedRules, rule5);
        assertEquals(expectedRules, actualRules);
    }

    @Test
    public void testGetSuggestions() {

        final JSONArray expectedSuggestions = mock(JSONArray.class);
        final JSONValue function1 = mock(JSONValue.class);
        final JSONValue function2 = mock(JSONValue.class);
        final JSONValue function3 = mock(JSONValue.class);
        final JSONValue function4 = mock(JSONValue.class);
        final JSONValue function5 = mock(JSONValue.class);
        final JSONValue function6 = mock(JSONValue.class);
        final JSONValue function7 = mock(JSONValue.class);
        final JSONValue function8 = mock(JSONValue.class);
        final JSONValue function9 = mock(JSONValue.class);
        final JSONValue function10 = mock(JSONValue.class);
        final JSONValue function11 = mock(JSONValue.class);
        final JSONValue function12 = mock(JSONValue.class);
        final JSONValue function13 = mock(JSONValue.class);
        final JSONValue function14 = mock(JSONValue.class);
        final JSONValue function15 = mock(JSONValue.class);
        final JSONValue function16 = mock(JSONValue.class);
        final JSONValue function17 = mock(JSONValue.class);
        final JSONValue function18 = mock(JSONValue.class);
        final JSONValue function19 = mock(JSONValue.class);
        final JSONValue function20 = mock(JSONValue.class);
        final JSONValue function21 = mock(JSONValue.class);
        final JSONValue function22 = mock(JSONValue.class);
        final JSONValue function23 = mock(JSONValue.class);
        final JSONValue function24 = mock(JSONValue.class);
        final JSONValue function25 = mock(JSONValue.class);
        final JSONValue function26 = mock(JSONValue.class);
        final JSONValue function27 = mock(JSONValue.class);
        final JSONValue function28 = mock(JSONValue.class);
        final JSONValue function29 = mock(JSONValue.class);
        final JSONValue function30 = mock(JSONValue.class);
        final JSONValue function31 = mock(JSONValue.class);
        final JSONValue function32 = mock(JSONValue.class);
        final JSONValue function33 = mock(JSONValue.class);
        final JSONValue function34 = mock(JSONValue.class);
        final JSONValue function35 = mock(JSONValue.class);
        final JSONValue function36 = mock(JSONValue.class);
        final JSONValue function37 = mock(JSONValue.class);
        final JSONValue function38 = mock(JSONValue.class);
        final JSONValue function39 = mock(JSONValue.class);
        final JSONValue function40 = mock(JSONValue.class);
        final JSONValue function41 = mock(JSONValue.class);
        final JSONValue function42 = mock(JSONValue.class);
        final JSONValue function43 = mock(JSONValue.class);
        final JSONValue function44 = mock(JSONValue.class);
        final JSONValue function45 = mock(JSONValue.class);

        doReturn(expectedSuggestions).when(factory).makeJSONArray();
        doReturn(function1).when(factory).getFunctionSuggestion("substring(string, start position, length?)", "substring($1, $2, $3)");
        doReturn(function2).when(factory).getFunctionSuggestion("string length(string)", "string length($1)");
        doReturn(function3).when(factory).getFunctionSuggestion("upper case(string)", "upper case($1)");
        doReturn(function4).when(factory).getFunctionSuggestion("lower case(string)", "lower case($1)");
        doReturn(function5).when(factory).getFunctionSuggestion("substring before(string, match)", "substring before($1, $2)");
        doReturn(function6).when(factory).getFunctionSuggestion("substring after(string, match)", "substring after($1, $2)");
        doReturn(function7).when(factory).getFunctionSuggestion("replace(input, pattern, replacement, flags?)", "replace($1, $2, $3, $4)");
        doReturn(function8).when(factory).getFunctionSuggestion("contains(string, match)", "contains($1, $2)");
        doReturn(function9).when(factory).getFunctionSuggestion("starts with(string, match)", "starts with($1, $2)");
        doReturn(function10).when(factory).getFunctionSuggestion("ends with(string, match)", "ends with($1, $2)");
        doReturn(function11).when(factory).getFunctionSuggestion("matches(input, pattern, flags?)", "matches($1, $2, $3)");
        doReturn(function12).when(factory).getFunctionSuggestion("split(string, delimiter)", "split($1, $2)");
        doReturn(function13).when(factory).getFunctionSuggestion("list contains(list, element)", "list contains($1, $2)");
        doReturn(function14).when(factory).getFunctionSuggestion("count(list)", "count($1)");
        doReturn(function15).when(factory).getFunctionSuggestion("min(list)", "min($1)");
        doReturn(function16).when(factory).getFunctionSuggestion("max(list)", "max($1)");
        doReturn(function17).when(factory).getFunctionSuggestion("sum(list)", "sum($1)");
        doReturn(function18).when(factory).getFunctionSuggestion("mean(list)", "mean($1)");
        doReturn(function19).when(factory).getFunctionSuggestion("and(list)", "and($1)");
        doReturn(function20).when(factory).getFunctionSuggestion("or(list)", "or($1)");
        doReturn(function21).when(factory).getFunctionSuggestion("sublist(list, start position, length?)", "sublist($1, $2, $3)");
        doReturn(function22).when(factory).getFunctionSuggestion("append(list, item...)", "append($1, $2)");
        doReturn(function23).when(factory).getFunctionSuggestion("concatenate(list...)", "concatenate($1)");
        doReturn(function24).when(factory).getFunctionSuggestion("insert before(list, position, newItem)", "insert before($1, $2, $3)");
        doReturn(function25).when(factory).getFunctionSuggestion("remove(list, position)", "remove($1, $2)");
        doReturn(function26).when(factory).getFunctionSuggestion("reverse(list)", "remove($1)");
        doReturn(function27).when(factory).getFunctionSuggestion("index of(list, match)", "index of($1, $2)");
        doReturn(function28).when(factory).getFunctionSuggestion("union(list...)", "union($1)");
        doReturn(function29).when(factory).getFunctionSuggestion("distinct values(list)", "distinct values($1)");
        doReturn(function30).when(factory).getFunctionSuggestion("flatten(list)", "flatten($1)");
        doReturn(function31).when(factory).getFunctionSuggestion("product(list)", "product($1)");
        doReturn(function32).when(factory).getFunctionSuggestion("median(list)", "median($1)");
        doReturn(function33).when(factory).getFunctionSuggestion("stddev(list)", "stddev($1)");
        doReturn(function34).when(factory).getFunctionSuggestion("mode(list)", "mode($1)");
        doReturn(function35).when(factory).getFunctionSuggestion("decimal(n, scale)", "decimal($1, $2)");
        doReturn(function36).when(factory).getFunctionSuggestion("floor(n)", "floor($1)");
        doReturn(function37).when(factory).getFunctionSuggestion("ceiling(n)", "ceiling($1)");
        doReturn(function38).when(factory).getFunctionSuggestion("abs(n)", "abs($1)");
        doReturn(function39).when(factory).getFunctionSuggestion("modulo(dividend, divisor)", "modulo($1, $2)");
        doReturn(function40).when(factory).getFunctionSuggestion("sqrt(number)", "sqrt($1)");
        doReturn(function41).when(factory).getFunctionSuggestion("log(number)", "log($1)");
        doReturn(function42).when(factory).getFunctionSuggestion("exp(number)", "exp($1)");
        doReturn(function43).when(factory).getFunctionSuggestion("odd(number)", "odd($1)");
        doReturn(function44).when(factory).getFunctionSuggestion("even(number)", "even($1)");
        doReturn(function45).when(factory).getFunctionSuggestion("not(negand)", "not($1)");

        final JSONArray actualSuggestions = factory.getSuggestions();

        verify(factory).push(expectedSuggestions, function1);
        verify(factory).push(expectedSuggestions, function2);
        verify(factory).push(expectedSuggestions, function3);
        verify(factory).push(expectedSuggestions, function4);
        verify(factory).push(expectedSuggestions, function5);
        verify(factory).push(expectedSuggestions, function6);
        verify(factory).push(expectedSuggestions, function7);
        verify(factory).push(expectedSuggestions, function8);
        verify(factory).push(expectedSuggestions, function9);
        verify(factory).push(expectedSuggestions, function10);
        verify(factory).push(expectedSuggestions, function11);
        verify(factory).push(expectedSuggestions, function12);
        verify(factory).push(expectedSuggestions, function13);
        verify(factory).push(expectedSuggestions, function14);
        verify(factory).push(expectedSuggestions, function15);
        verify(factory).push(expectedSuggestions, function16);
        verify(factory).push(expectedSuggestions, function17);
        verify(factory).push(expectedSuggestions, function18);
        verify(factory).push(expectedSuggestions, function19);
        verify(factory).push(expectedSuggestions, function20);
        verify(factory).push(expectedSuggestions, function21);
        verify(factory).push(expectedSuggestions, function22);
        verify(factory).push(expectedSuggestions, function23);
        verify(factory).push(expectedSuggestions, function24);
        verify(factory).push(expectedSuggestions, function25);
        verify(factory).push(expectedSuggestions, function26);
        verify(factory).push(expectedSuggestions, function27);
        verify(factory).push(expectedSuggestions, function28);
        verify(factory).push(expectedSuggestions, function29);
        verify(factory).push(expectedSuggestions, function30);
        verify(factory).push(expectedSuggestions, function31);
        verify(factory).push(expectedSuggestions, function32);
        verify(factory).push(expectedSuggestions, function33);
        verify(factory).push(expectedSuggestions, function34);
        verify(factory).push(expectedSuggestions, function35);
        verify(factory).push(expectedSuggestions, function36);
        verify(factory).push(expectedSuggestions, function37);
        verify(factory).push(expectedSuggestions, function38);
        verify(factory).push(expectedSuggestions, function39);
        verify(factory).push(expectedSuggestions, function40);
        verify(factory).push(expectedSuggestions, function41);
        verify(factory).push(expectedSuggestions, function42);
        verify(factory).push(expectedSuggestions, function43);
        verify(factory).push(expectedSuggestions, function44);
        verify(factory).push(expectedSuggestions, function45);
        assertEquals(expectedSuggestions, actualSuggestions);
    }

    @Test
    public void testGetFunctionSuggestion() {

        final String label = "label";
        final String insertText = "insertText";
        final JSONValue kind = mock(JSONValue.class);
        final JSONValue insertTextRules = mock(JSONValue.class);
        final JSONObject expectedSuggestion = mock(JSONObject.class);
        final JSONString labelString = mock(JSONString.class);
        final JSONString insertTextString = mock(JSONString.class);

        doReturn(expectedSuggestion).when(factory).makeJSONObject();
        doReturn(kind).when(factory).makeJSONNumber(MonacoCompletionItemKind.Function);
        doReturn(insertTextRules).when(factory).makeJSONNumber(MonacoCompletionItemInsertTextRule.InsertAsSnippet);
        doReturn(labelString).when(factory).makeJSONString(label);
        doReturn(insertTextString).when(factory).makeJSONString(insertText);

        final JSONValue actualSuggestion = factory.getFunctionSuggestion(label, insertText);

        verify(expectedSuggestion).put("kind", kind);
        verify(expectedSuggestion).put("insertTextRules", insertTextRules);
        verify(expectedSuggestion).put("label", labelString);
        verify(expectedSuggestion).put("insertText", insertTextString);
        assertEquals(expectedSuggestion, actualSuggestion);
    }

    @Test
    public void testRow() {

        final String pattern = "pattern";
        final String name = "name";
        final RegExp regExp = mock(RegExp.class);
        final JSONObject jsonRegExp = mock(JSONObject.class);
        final JSONString jsonName = mock(JSONString.class);
        final JSONArray expectedRow = mock(JSONArray.class);

        doReturn(regExp).when(factory).makeRegExp(pattern);
        doReturn(jsonRegExp).when(factory).makeJSONObject(regExp);
        doReturn(jsonName).when(factory).makeJSONString(name);
        doReturn(expectedRow).when(factory).makeJSONArray();

        final JSONValue actualRow = factory.row(pattern, name);

        verify(expectedRow).set(0, jsonRegExp);
        verify(expectedRow).set(1, jsonName);
        assertEquals(expectedRow, actualRow);
    }

    @Test
    public void testGetLanguage() {

        final JSONString languageId = mock(JSONString.class);
        final JavaScriptObject expectedLanguage = mock(JavaScriptObject.class);

        doReturn(languageId).when(factory).makeJSONString(FEEL_LANGUAGE_ID);
        doReturn(expectedLanguage).when(factory).makeJavaScriptObject("id", languageId);

        final JavaScriptObject actualLanguage = factory.getLanguage();

        assertEquals(expectedLanguage, actualLanguage);
    }

    @Test
    public void testMonacoModule() {

        final JsArrayString expectedModules = mock(JsArrayString.class);

        doReturn(expectedModules).when(factory).makeJsArrayString();

        final JsArrayString actualModules = factory.monacoModule();

        verify(expectedModules).push(VS_EDITOR_EDITOR_MAIN_MODULE);
        assertEquals(expectedModules, actualModules);
    }

    @Test
    public void testGetCompletionItemProvider() {

        final ProvideCompletionItemsFunction provideCompletionItemsFunction = mock(ProvideCompletionItemsFunction.class);
        final JSONObject functionObject = mock(JSONObject.class);
        final JavaScriptObject expectedCompletionItemProvider = mock(JavaScriptObject.class);

        doReturn(provideCompletionItemsFunction).when(factory).getProvideCompletionItemsFunction();
        doReturn(functionObject).when(factory).makeJSONObject(provideCompletionItemsFunction);
        doReturn(expectedCompletionItemProvider).when(factory).makeJavaScriptObject("provideCompletionItems", functionObject);

        final JavaScriptObject actualCompletionItemProvider = factory.getCompletionItemProvider();

        assertEquals(expectedCompletionItemProvider, actualCompletionItemProvider);
    }

    @Test
    public void testGetProvideCompletionItemsFunction() {

        final JavaScriptObject expectedSuggestions = mock(JavaScriptObject.class);
        final JSONObject expectedJSONObjectSuggestions = mock(JSONObject.class);
        final JSONArray suggestions = mock(JSONArray.class);

        doReturn(expectedJSONObjectSuggestions).when(factory).makeJSONObject();
        doReturn(suggestions).when(factory).getSuggestions();
        when(expectedJSONObjectSuggestions.getJavaScriptObject()).thenReturn(expectedSuggestions);

        final JavaScriptObject actualSuggestions = factory.getProvideCompletionItemsFunction().call();

        verify(expectedJSONObjectSuggestions).put("suggestions", suggestions);
        assertEquals(expectedSuggestions, actualSuggestions);
    }

    @Test
    public void testGetLanguageDefinition() {

        final JSONValue tokenizer = mock(JSONValue.class);
        final JavaScriptObject expectedLanguageDefinition = mock(JavaScriptObject.class);

        doReturn(expectedLanguageDefinition).when(factory).makeJavaScriptObject("tokenizer", tokenizer);
        doReturn(tokenizer).when(factory).getTokenizer();

        final JavaScriptObject actualLanguageDefinition = factory.getLanguageDefinition();

        assertEquals(expectedLanguageDefinition, actualLanguageDefinition);
    }

    @Test
    public void testGetTokenizer() {

        final JSONObject expectedTokenizer = mock(JSONObject.class);
        final JSONArray root = mock(JSONArray.class);

        doReturn(expectedTokenizer).when(factory).makeJSONObject();
        doReturn(root).when(factory).getRoot();

        final JSONValue actualTokenizer = factory.getTokenizer();

        verify(expectedTokenizer).put("root", root);
        assertEquals(expectedTokenizer, actualTokenizer);
    }

    @Test
    public void testGetRoot() {

        final JSONArray expectedRoot = mock(JSONArray.class);
        final JSONArray row1 = mock(JSONArray.class);
        final JSONArray row2 = mock(JSONArray.class);
        final JSONArray row3 = mock(JSONArray.class);
        final JSONArray row4 = mock(JSONArray.class);
        final JSONArray row5 = mock(JSONArray.class);
        final JSONArray row6 = mock(JSONArray.class);

        doReturn(expectedRoot).when(factory).makeJSONArray();
        doReturn(row1).when(factory).row("(?:true|false)", "feel-boolean");
        doReturn(row2).when(factory).row("[0-9]+", "feel-numeric");
        doReturn(row3).when(factory).row("(?:\\\"(?:.*?)\\\")", "feel-string");
        doReturn(row4).when(factory).row("(?:(?:[a-z ]+\\()|(?:\\()|(?:\\)))", "feel-function");
        doReturn(row5).when(factory).row("(?:if|then|else)", "feel-keyword");
        doReturn(row6).when(factory).row("(?:for|in|return)", "feel-keyword");

        final JSONArray actualRoot = factory.getRoot();

        verify(factory).push(expectedRoot, row1);
        verify(factory).push(expectedRoot, row2);
        verify(factory).push(expectedRoot, row3);
        verify(factory).push(expectedRoot, row4);
        verify(factory).push(expectedRoot, row5);
        verify(factory).push(expectedRoot, row6);
        assertEquals(expectedRoot, actualRoot);
    }
}
