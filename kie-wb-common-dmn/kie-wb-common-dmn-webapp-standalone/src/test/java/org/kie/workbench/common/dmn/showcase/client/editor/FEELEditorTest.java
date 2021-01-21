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

package org.kie.workbench.common.dmn.showcase.client.editor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;

import static org.junit.Assert.*;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.ADD;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.AND;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.ANY_OTHER_CHAR;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.AT;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.BANG;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.BETWEEN;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.BooleanLiteral;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.COLON;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.COMMA;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.COMMENT;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.DIV;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.DOT;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.ELIPSIS;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.ELSE;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.EQUAL;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.EVERY;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.EXTERNAL;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.FALSE;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.FOR;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.FUNCTION;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.FloatingPointLiteral;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.GE;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.GT;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.IF;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.IN;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.INSTANCE;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.Identifier;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.IntegerLiteral;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.LBRACE;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.LBRACK;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.LE;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.LINE_COMMENT;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.LPAREN;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.LT;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.MUL;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.NOT;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.NOTEQUAL;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.NULL;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.OF;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.OR;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.POW;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.QUOTE;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.RARROW;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.RBRACE;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.RBRACK;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.RETURN;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.RPAREN;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.SATISFIES;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.SOME;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.SUB;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.StringLiteral;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.THEN;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.TRUE;
import static org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.WS;

public class FEELEditorTest {

    @Test
    public void as() {

        Set<Integer> ignoredTokens = getIntegers();

        final FEEL_1_1Parser parser = FEELParser.parse(null,
                                                       "",
                                                       Collections.emptyMap(),
                                                       Collections.emptyMap(),
                                                       Collections.emptyList(),
                                                       Collections.emptyList(),
                                                       null);

        final ParseTree tree = parser.expression();

        for (Integer ignoredToken : ignoredTokens) {
            System.out.println(parser.getVocabulary().getLiteralName(ignoredToken) + " === " + parser.getVocabulary().getDisplayName(ignoredToken) + " === " + parser.getVocabulary().getSymbolicName(ignoredToken));
        }


    }

    private Set<Integer> getIntegers() {
        Set<Integer> ignoredTokens = new HashSet<>();
        ignoredTokens.add(BooleanLiteral);
        ignoredTokens.add(FOR);
        ignoredTokens.add(RETURN);
        ignoredTokens.add(IN);
        ignoredTokens.add(IF);
        ignoredTokens.add(THEN);
        ignoredTokens.add(ELSE);
        ignoredTokens.add(SOME);
        ignoredTokens.add(EVERY);
        ignoredTokens.add(SATISFIES);
        ignoredTokens.add(INSTANCE);
        ignoredTokens.add(OF);
        ignoredTokens.add(FUNCTION);
        ignoredTokens.add(EXTERNAL);
        ignoredTokens.add(OR);
        ignoredTokens.add(AND);
        ignoredTokens.add(BETWEEN);
        ignoredTokens.add(NULL);
        ignoredTokens.add(TRUE);
        ignoredTokens.add(FALSE);
        ignoredTokens.add(QUOTE);
        ignoredTokens.add(IntegerLiteral);
        ignoredTokens.add(FloatingPointLiteral);
        ignoredTokens.add(StringLiteral);
        ignoredTokens.add(LPAREN);
        ignoredTokens.add(RPAREN);
        ignoredTokens.add(LBRACE);
        ignoredTokens.add(RBRACE);
        ignoredTokens.add(LBRACK);
        ignoredTokens.add(RBRACK);
        ignoredTokens.add(COMMA);
        ignoredTokens.add(ELIPSIS);
        ignoredTokens.add(DOT);
        ignoredTokens.add(EQUAL);
        ignoredTokens.add(GT);
        ignoredTokens.add(LT);
        ignoredTokens.add(LE);
        ignoredTokens.add(GE);
        ignoredTokens.add(NOTEQUAL);
        ignoredTokens.add(COLON);
        ignoredTokens.add(RARROW);
        ignoredTokens.add(POW);
        ignoredTokens.add(ADD);
        ignoredTokens.add(SUB);
        ignoredTokens.add(MUL);
        ignoredTokens.add(DIV);
        ignoredTokens.add(BANG);
        ignoredTokens.add(NOT);
        ignoredTokens.add(AT);
        ignoredTokens.add(Identifier);
        ignoredTokens.add(WS);
        ignoredTokens.add(COMMENT);
        ignoredTokens.add(LINE_COMMENT);
        ignoredTokens.add(ANY_OTHER_CHAR);
        return ignoredTokens;
    }
}