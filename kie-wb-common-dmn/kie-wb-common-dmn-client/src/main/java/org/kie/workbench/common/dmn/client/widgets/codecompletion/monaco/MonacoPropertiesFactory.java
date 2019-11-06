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
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import elemental2.core.RegExp;
import jsinterop.base.Js;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco.jsenumerations.MonacoCompletionItemInsertTextRule;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco.jsenumerations.MonacoCompletionItemKind;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.monaco.jsinterop.MonacoLanguages;

public class MonacoPropertiesFactory {

    public static final String FEEL_LANGUAGE_ID = "feel-language";

    public static final String FEEL_THEME_ID = "feel-theme";

    static final String VS_EDITOR_EDITOR_MAIN_MODULE = "vs/editor/editor.main";

    public JavaScriptObject getConstructionOptions() {

        final JSONObject options = makeJSONObject();
        final JSONObject scrollbar = makeJSONObject();
        final JSONObject miniMap = makeJSONObject();

        options.put("language", makeJSONString(FEEL_LANGUAGE_ID));
        options.put("theme", makeJSONString(FEEL_THEME_ID));

        options.put("renderLineHighlight", makeJSONString("none"));
        options.put("lineNumbers", makeJSONString("off"));

        options.put("fontSize", makeJSONNumber(14));
        options.put("lineNumbersMinChars", makeJSONNumber(1));
        options.put("lineDecorationsWidth", makeJSONNumber(1));

        options.put("overviewRulerBorder", makeJSONBoolean(false));
        options.put("scrollBeyondLastLine", makeJSONBoolean(false));
        options.put("snippetSuggestions", makeJSONBoolean(false));
        options.put("useTabStops", makeJSONBoolean(false));
        options.put("contextmenu", makeJSONBoolean(false));
        options.put("folding", makeJSONBoolean(false));
        miniMap.put("enabled", makeJSONBoolean(false));
        scrollbar.put("useShadows", makeJSONBoolean(false));

        options.put("automaticLayout", makeJSONBoolean(true));
        options.put("renderWhitespace", makeJSONBoolean(true));
        options.put("hideCursorInOverviewRuler", makeJSONBoolean(true));

        options.put("scrollbar", scrollbar);
        options.put("minimap", miniMap);

        return options.getJavaScriptObject();
    }

    public JavaScriptObject getThemeData() {

        final JSONObject themeDefinition = makeJSONObject();
        final JSONObject colors = makeJSONObject();
        final JSONString colorHEXCode = makeJSONString("#000000");
        final JSONString base = makeJSONString("vs");
        final JSONBoolean inherit = makeJSONBoolean(false);
        final JSONArray rules = getRules();

        colors.put("editorLineNumber.foreground", colorHEXCode);
        themeDefinition.put("base", base);
        themeDefinition.put("inherit", inherit);
        themeDefinition.put("rules", rules);
        themeDefinition.put("colors", colors);

        return themeDefinition.getJavaScriptObject();
    }

    public JSONArray getRules() {

        final JSONObject rule1 = makeJSONObject();
        final JSONObject rule2 = makeJSONObject();
        final JSONObject rule3 = makeJSONObject();
        final JSONObject rule4 = makeJSONObject();
        final JSONObject rule5 = makeJSONObject();
        final JSONArray rules = makeJSONArray();

        rule1.put("token", makeJSONString("feel-keyword"));
        rule1.put("foreground", makeJSONString("ec5b69"));
        rule1.put("fontStyle", makeJSONString("bold"));

        rule2.put("token", makeJSONString("feel-numeric"));
        rule2.put("foreground", makeJSONString("005cc5"));

        rule3.put("token", makeJSONString("feel-boolean"));
        rule3.put("foreground", makeJSONString("d73a49"));

        rule4.put("token", makeJSONString("feel-string"));
        rule4.put("foreground", makeJSONString("22863a"));

        rule5.put("token", makeJSONString("feel-function"));
        rule5.put("foreground", makeJSONString("6f42c1"));

        push(rules, rule1);
        push(rules, rule2);
        push(rules, rule3);
        push(rules, rule4);
        push(rules, rule5);

        return rules;
    }

    public JavaScriptObject getCompletionItemProvider() {
        return makeJavaScriptObject("provideCompletionItems", makeJSONObject(getProvideCompletionItemsFunction()));
    }

    public MonacoLanguages.ProvideCompletionItemsFunction getProvideCompletionItemsFunction() {
        return () -> {
            final JSONObject suggestions = makeJSONObject();
            suggestions.put("suggestions", getSuggestions());
            return suggestions.getJavaScriptObject();
        };
    }

    public JavaScriptObject getLanguageDefinition() {
        return makeJavaScriptObject("tokenizer", getTokenizer());
    }

    public JSONValue getTokenizer() {
        final JSONObject tokenizer = makeJSONObject();
        tokenizer.put("root", getRoot());
        return tokenizer;
    }

    public JSONArray getRoot() {
        final JSONArray root = makeJSONArray();
        push(root, row("(?:true|false)", "feel-boolean"));
        push(root, row("[0-9]+", "feel-numeric"));
        push(root, row("(?:\\\"(?:.*?)\\\")", "feel-string"));
        push(root, row("(?:(?:[a-z ]+\\()|(?:\\()|(?:\\)))", "feel-function"));
        push(root, row("(?:if|then|else)", "feel-keyword"));
        push(root, row("(?:for|in|return)", "feel-keyword"));
        return root;
    }

    public JSONArray getSuggestions() {

        final JSONArray suggestionTypes = makeJSONArray();

        push(suggestionTypes, getFunctionSuggestion("substring(string, start position, length?)", "substring($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("string length(string)", "string length($1)"));
        push(suggestionTypes, getFunctionSuggestion("upper case(string)", "upper case($1)"));
        push(suggestionTypes, getFunctionSuggestion("lower case(string)", "lower case($1)"));
        push(suggestionTypes, getFunctionSuggestion("substring before(string, match)", "substring before($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("substring after(string, match)", "substring after($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("replace(input, pattern, replacement, flags?)", "replace($1, $2, $3, $4)"));
        push(suggestionTypes, getFunctionSuggestion("contains(string, match)", "contains($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("starts with(string, match)", "starts with($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("ends with(string, match)", "ends with($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("matches(input, pattern, flags?)", "matches($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("split(string, delimiter)", "split($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("list contains(list, element)", "list contains($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("count(list)", "count($1)"));
        push(suggestionTypes, getFunctionSuggestion("min(list)", "min($1)"));
        push(suggestionTypes, getFunctionSuggestion("max(list)", "max($1)"));
        push(suggestionTypes, getFunctionSuggestion("sum(list)", "sum($1)"));
        push(suggestionTypes, getFunctionSuggestion("mean(list)", "mean($1)"));
        push(suggestionTypes, getFunctionSuggestion("and(list)", "and($1)"));
        push(suggestionTypes, getFunctionSuggestion("or(list)", "or($1)"));
        push(suggestionTypes, getFunctionSuggestion("sublist(list, start position, length?)", "sublist($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("append(list, item...)", "append($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("concatenate(list...)", "concatenate($1)"));
        push(suggestionTypes, getFunctionSuggestion("insert before(list, position, newItem)", "insert before($1, $2, $3)"));
        push(suggestionTypes, getFunctionSuggestion("remove(list, position)", "remove($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("reverse(list)", "remove($1)"));
        push(suggestionTypes, getFunctionSuggestion("index of(list, match)", "index of($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("union(list...)", "union($1)"));
        push(suggestionTypes, getFunctionSuggestion("distinct values(list)", "distinct values($1)"));
        push(suggestionTypes, getFunctionSuggestion("flatten(list)", "flatten($1)"));
        push(suggestionTypes, getFunctionSuggestion("product(list)", "product($1)"));
        push(suggestionTypes, getFunctionSuggestion("median(list)", "median($1)"));
        push(suggestionTypes, getFunctionSuggestion("stddev(list)", "stddev($1)"));
        push(suggestionTypes, getFunctionSuggestion("mode(list)", "mode($1)"));
        push(suggestionTypes, getFunctionSuggestion("decimal(n, scale)", "decimal($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("floor(n)", "floor($1)"));
        push(suggestionTypes, getFunctionSuggestion("ceiling(n)", "ceiling($1)"));
        push(suggestionTypes, getFunctionSuggestion("abs(n)", "abs($1)"));
        push(suggestionTypes, getFunctionSuggestion("modulo(dividend, divisor)", "modulo($1, $2)"));
        push(suggestionTypes, getFunctionSuggestion("sqrt(number)", "sqrt($1)"));
        push(suggestionTypes, getFunctionSuggestion("log(number)", "log($1)"));
        push(suggestionTypes, getFunctionSuggestion("exp(number)", "exp($1)"));
        push(suggestionTypes, getFunctionSuggestion("odd(number)", "odd($1)"));
        push(suggestionTypes, getFunctionSuggestion("even(number)", "even($1)"));
        push(suggestionTypes, getFunctionSuggestion("not(negand)", "not($1)"));

        return suggestionTypes;
    }

    JSONValue getFunctionSuggestion(final String label,
                                    final String insertText) {

        final JSONObject suggestion = makeJSONObject();

        suggestion.put("kind", makeJSONNumber(MonacoCompletionItemKind.Function));
        suggestion.put("insertTextRules", makeJSONNumber(MonacoCompletionItemInsertTextRule.InsertAsSnippet));
        suggestion.put("label", makeJSONString(label));
        suggestion.put("insertText", makeJSONString(insertText));

        return suggestion;
    }

    public JSONArray row(final String pattern,
                         final String name) {
        final JSONArray row = makeJSONArray();
        row.set(0, makeJSONObject(makeRegExp(pattern)));
        row.set(1, makeJSONString(name));
        return row;
    }

    public JavaScriptObject getLanguage() {
        return makeJavaScriptObject("id", makeJSONString(FEEL_LANGUAGE_ID));
    }

    public JsArrayString monacoModule() {
        final JsArrayString modules = makeJsArrayString();
        modules.push(VS_EDITOR_EDITOR_MAIN_MODULE);
        return modules;
    }

    JavaScriptObject makeJavaScriptObject(final String property,
                                          final JSONValue value) {
        final JSONObject jsonObject = makeJSONObject();
        jsonObject.put(property, value);
        return jsonObject.getJavaScriptObject();
    }

    JsArrayString makeJsArrayString() {
        return (JsArrayString) JsArrayString.createArray();
    }

    RegExp makeRegExp(final String pattern) {
        return new RegExp(pattern);
    }

    JSONArray makeJSONArray() {
        return new JSONArray();
    }

    JSONBoolean makeJSONBoolean(final boolean value) {
        return JSONBoolean.getInstance(value);
    }

    JSONString makeJSONString(final String value) {
        return new JSONString(value);
    }

    JSONValue makeJSONNumber(final int value) {
        return new JSONNumber(value);
    }

    JSONObject makeJSONObject(final Object obj) {
        return new JSONObject(Js.uncheckedCast(obj));
    }

    JSONObject makeJSONObject() {
        return new JSONObject();
    }

    void push(final JSONArray jsonArray,
              final JSONValue jsonValue) {
        jsonArray.set(jsonArray.size(), jsonValue);
    }
}
