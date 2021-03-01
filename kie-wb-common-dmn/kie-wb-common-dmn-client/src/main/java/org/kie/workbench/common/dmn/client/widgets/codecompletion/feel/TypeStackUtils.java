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

package org.kie.workbench.common.dmn.client.widgets.codecompletion.feel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.FEELLanguageService.Position;

public class TypeStackUtils {

    public static final List<String> ALLOWED_TYPE_NAMES = Arrays.stream(BuiltInType.values()).flatMap(builtInType -> Arrays.stream(builtInType.getNames())).collect(Collectors.toList());

    public static List<Type> getTypeStack(final ASTNode node,
                                          final Position position) {
        return getTypeStack(node, null, position, false);
    }

    private static List<Type> getTypeStack(final ASTNode currentNode,
                                           final ASTNode nextNode,
                                           final Position position,
                                           final boolean isParentEligibleType) {

        final List<Type> typeStack = new ArrayList<>();

        if (currentNode == null) {
            return typeStack;
        }

        final boolean isEligibleType = isEligibleType(currentNode, nextNode, position, isParentEligibleType);

        if (isEligibleType) {
            typeStack.add(currentNode.getResultType());
        }

        try {
            forEach(getChildren(currentNode), (current, next) -> {
                typeStack.addAll(getTypeStack(current, next, position, isEligibleType));
            });
        } catch (final Exception e) {
            // Ignore errors during node inspection.
        }

        return typeStack;
    }

    private static boolean isEligibleType(final ASTNode node,
                                          final ASTNode next,
                                          final Position position,
                                          final boolean isParentEligibleType) {

        final boolean isAllowedType = isAllowedType(node);
        final boolean isEligibleLine = isEligibleLine(node, position.line);
        final boolean isEligibleColumn = isEligibleColumn(node, next, position.column, isParentEligibleType);

        return isAllowedType && isEligibleLine && isEligibleColumn;
    }

    private static boolean isAllowedType(final ASTNode node) {
        return ALLOWED_TYPE_NAMES.contains(node.getResultType().getName());
    }

    private static boolean isEligibleLine(final ASTNode node,
                                          final int line) {

        final int stop = node.getEndLine();
        final int start = node.getStartLine();

        return line >= start && line <= stop;
    }

    private static boolean isEligibleColumn(final ASTNode node,
                                            final ASTNode next,
                                            final int column,
                                            final boolean isParentEligibleType) {

        return isEligibleColumnStart(node, column) &&
                isEligibleColumnEnd(node, next, column, isParentEligibleType);
    }

    private static boolean isEligibleColumnEnd(final ASTNode node,
                                               final ASTNode next,
                                               final int column,
                                               final boolean isParentEligibleType) {

        final boolean hasNextNode = next != null;
        final int startNextNodeColumn = hasNextNode ? next.getStartColumn() : 0;
        final int endNodeColumn = node.getEndColumn() + countExtraChar(node);

        final boolean hasGapBetweenNodes = startNextNodeColumn - endNodeColumn > 0;
        final int stop = hasGapBetweenNodes ? startNextNodeColumn : endNodeColumn;

        return column <= stop || !isParentEligibleType && !hasNextNode;
    }

    private static boolean isEligibleColumnStart(final ASTNode node,
                                                 final int column) {

        final int startNodeColumn = node.getStartColumn();
        final boolean isMultiline = node.getEndLine() > node.getStartLine();
        final int start = isMultiline ? 0 : startNodeColumn;

        return column >= start;
    }

    private static int countExtraChar(final ASTNode node) {
        final Type resultType = node.getResultType();
        if (resultType.conformsTo(BuiltInType.LIST) || resultType.conformsTo(BuiltInType.RANGE)) {
            return 1;
        }
        return 0;
    }

    private static Iterator<ASTNode> getChildren(final ASTNode currentNode) {
        return Arrays.stream(currentNode.getChildrenNode()).iterator();
    }

    private static void forEach(final Iterator<ASTNode> iterator,
                                final BiConsumer<ASTNode, ASTNode> consumer) {

        if (!iterator.hasNext()) {
            return;
        }

        ASTNode current = iterator.next();

        while (iterator.hasNext()) {
            final ASTNode next = iterator.next();
            consumer.accept(current, next);
            current = next;
        }

        consumer.accept(current, null);
    }
}
