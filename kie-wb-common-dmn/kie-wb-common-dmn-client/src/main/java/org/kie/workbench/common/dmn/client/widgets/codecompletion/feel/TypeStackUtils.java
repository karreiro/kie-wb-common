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
import java.util.Optional;
import java.util.function.BiConsumer;

import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.feel.FEELLanguageService.Position;

public class TypeStackUtils {

    public static List<Type> getTypeStack(final ASTNode node,
                                          final Position position) {
        return getTypeStack(node, null, position);
    }

    public static List<Type> getTypeStack(final ASTNode currentNode,
                                          final ASTNode nextNode,
                                          final Position position) {

        final List<Type> typeStack = new ArrayList<>();

        if (currentNode == null) {
            return typeStack;
        }

        if (isSamePosition(currentNode, nextNode, position)) {
            typeStack.add(currentNode.getResultType());
        }

        try {
            forEach(getChildren(currentNode), (current, next) -> {
                typeStack.addAll(getTypeStack(current, next, position));
            });
        } catch (final Exception e) {
            // Ignore errors during node inspection.
        }

        return typeStack;
    }

    private static boolean isSamePosition(final ASTNode node,
                                          final ASTNode next,
                                          final Position position) {

        final boolean isSameLine = isSameLine(node, position.line);
        final boolean isSameColumn = isSameColumn(node, next, position.column);

        return isSameLine && isSameColumn;
    }

    private static boolean isSameLine(final ASTNode node,
                                      final int line) {

        final int stop = node.getEndLine();
        final int start = node.getStartLine();

        return line >= start && line <= stop;
    }

    private static boolean isSameColumn(final ASTNode node,
                                        final ASTNode next,
                                        final int column) {

        final Optional<ASTNode> nextNode = Optional.ofNullable(next);
        final int nextStartColumn = nextNode.map(ASTNode::getStartColumn).orElse(0);
        final boolean isMultiline = node.getEndLine() > node.getStartLine();
        final boolean hasGapBetweenNodes = nextStartColumn - node.getEndColumn() > 0;
        final int start = isMultiline ? 0 : node.getStartColumn();
        final int stop = hasGapBetweenNodes ? nextStartColumn : node.getEndColumn();

        return column >= start && (column <= stop || !nextNode.isPresent());
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
