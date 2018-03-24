/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.decision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.decision.factories.DecisionNavigatorItemFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DecisionNavigatorChildrenTraverseTest {

    @Mock
    private ChildrenTraverseProcessor traverseProcessor;

    @Mock
    private DecisionNavigatorItemFactory itemFactory;

    @Mock
    private Node<View, Edge> node;

    @Mock
    private List<Node<View, Edge>> nodes;

    private DecisionNavigatorChildrenTraverse traverse;

    @Before
    public void setup() {
        traverse = spy(new DecisionNavigatorChildrenTraverse(traverseProcessor, itemFactory));
    }

    @Test
    public void testGetItems() {

        final Graph graph = mock(Graph.class);
        final DecisionNavigatorChildrenTraverse.TraverseCallback traverseCallback =
                mock(DecisionNavigatorChildrenTraverse.TraverseCallback.class);
        final List<DecisionNavigatorItem> expectedItems = new ArrayList<>();

        doReturn(traverseCallback).when(traverse).makeTraverseCallback();
        when(traverseCallback.getItems()).thenReturn(expectedItems);

        final List<DecisionNavigatorItem> actualItems = traverse.getItems(graph);

        verify(traverseProcessor).traverse(graph, traverseCallback);
        assertEquals(expectedItems, actualItems);
    }

    @Test
    public void testTraverseCallbackStartNodeTraversalWithoutParents() {

        final DecisionNavigatorChildrenTraverse.TraverseCallback traverseCallback = spy(traverse.makeTraverseCallback());
        final DecisionNavigatorItem item = makeItem("item");
        final List<DecisionNavigatorItem> items = new ArrayList<>();

        doReturn(items).when(traverseCallback).getItems();
        when(itemFactory.makeRoot(node)).thenReturn(item);

        traverseCallback.startNodeTraversal(node);

        assertEquals(items, Collections.singletonList(item));
    }

    @Test
    public void testTraverseCallbackStartNodeTraversalWithParents() {

        final DecisionNavigatorChildrenTraverse.TraverseCallback traverseCallback = spy(traverse.makeTraverseCallback());
        final DecisionNavigatorItem item = makeItem("item");
        final DecisionNavigatorItem parent1 = makeItem("parent1");
        final DecisionNavigatorItem parent2 = makeItem("parent2");
        final List<DecisionNavigatorItem> parentItems = Arrays.asList(parent1, parent2);

        doReturn(parentItems).when(traverseCallback).findItems(nodes);
        when(itemFactory.makeItem(node)).thenReturn(item);

        traverseCallback.startNodeTraversal(nodes, node);

        assertEquals(item.getParents(), Arrays.asList(parent1, parent2));
        assertEquals(parent1.getChildren(), Collections.singletonList(item));
        assertEquals(parent2.getChildren(), Collections.singletonList(item));
    }

    @Test
    public void testFindItems() {

        final DecisionNavigatorChildrenTraverse.TraverseCallback traverseCallback = spy(traverse.makeTraverseCallback());
        final Node<View, Edge> node1 = makeNode("123");
        final Node<View, Edge> node2 = makeNode("456");
        final Node<View, Edge> node3 = makeNode("789");
        final List<Node<View, Edge>> nodes = Arrays.asList(node1, node2, node3);
        final DecisionNavigatorItem item1 = makeItem("123");
        final DecisionNavigatorItem item2 = makeItem("ABC");
        final DecisionNavigatorItem item3 = makeItem("456");
        final List<DecisionNavigatorItem> items = Arrays.asList(item1, item2, item3);

        doReturn(items).when(traverseCallback).getItems();

        final List<DecisionNavigatorItem> actualItems = traverseCallback.findItems(nodes);
        final List<DecisionNavigatorItem> expectedItems = Arrays.asList(item1, item3);

        assertEquals(expectedItems, actualItems);
    }

    private DecisionNavigatorItem makeItem(final String uuid) {
        return new DecisionNavigatorItem(uuid);
    }

    private Node<View, Edge> makeNode(final String uuid) {
        return new Node<View, Edge>() {
            @Override
            public List<Edge> getInEdges() {
                return null;
            }

            @Override
            public List<Edge> getOutEdges() {
                return null;
            }

            @Override
            public String getUUID() {
                return uuid;
            }

            @Override
            public Set<String> getLabels() {
                return null;
            }

            @Override
            public View getContent() {
                return null;
            }

            @Override
            public void setContent(final View content) {

            }

            @Override
            public Node<View, Edge> asNode() {
                return null;
            }

            @Override
            public Edge<View, Node> asEdge() {
                return null;
            }
        };
    }
}
