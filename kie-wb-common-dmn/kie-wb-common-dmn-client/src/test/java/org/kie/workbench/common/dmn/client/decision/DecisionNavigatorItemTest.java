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

import java.util.Collections;
import java.util.TreeSet;

import org.junit.Test;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.ITEM;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.SUB_ITEM;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class DecisionNavigatorItemTest {

    @Test
    public void testEqualsWhenItemsAreEqual() {

        final DecisionNavigatorItem item1 = new DecisionNavigatorItem("123");
        final DecisionNavigatorItem item2 = new DecisionNavigatorItem("123");

        assertEquals(item1, item2);
    }

    @Test
    public void testEqualsWhenItemsHaveDifferentUUIDs() {

        final DecisionNavigatorItem item1 = new DecisionNavigatorItem("123");
        final DecisionNavigatorItem item2 = new DecisionNavigatorItem("456");

        assertNotEquals(item1, item2);
    }

    @Test
    public void testEqualsWhenItemsHaveDifferentParentUUIDs() {

        final DecisionNavigatorItem item1 = new DecisionNavigatorItem("123", null, null, null, "456");
        final DecisionNavigatorItem item2 = new DecisionNavigatorItem("123", null, null, null, "789");

        assertNotEquals(item1, item2);
    }

    @Test
    public void testEqualsWhenItemsHaveDifferentTypes() {

        final DecisionNavigatorItem item1 = new DecisionNavigatorItem("123", null, ITEM, null, null);
        final DecisionNavigatorItem item2 = new DecisionNavigatorItem("123", null, SUB_ITEM, null, null);

        assertNotEquals(item1, item2);
    }

    @Test
    public void testEqualsWhenItemsHaveDifferentLabels() {

        final DecisionNavigatorItem item1 = new DecisionNavigatorItem("123", "Node1", null, null, null);
        final DecisionNavigatorItem item2 = new DecisionNavigatorItem("123", "Node0", null, null, null);

        assertNotEquals(item1, item2);
    }

    @Test
    public void testOnClick() {

        final Command command = mock(Command.class);
        final DecisionNavigatorItem item = new DecisionNavigatorItem("uuid", "label", ITEM, command, null);

        item.onClick();

        verify(command).execute();
    }

    @Test
    public void testRemoveChild() {

        final DecisionNavigatorItem item = spy(new DecisionNavigatorItem("item"));
        final DecisionNavigatorItem child = spy(new DecisionNavigatorItem("child"));
        final TreeSet<DecisionNavigatorItem> children = spy(new TreeSet<DecisionNavigatorItem>() {{
            add(child);
        }});

        doReturn(children).when(item).getChildren();

        item.removeChild(child);

        verify(children).removeIf(any());
        assertEquals(Collections.emptySet(), children);
    }

    @Test
    public void testAddChild() {

        final DecisionNavigatorItem item = spy(new DecisionNavigatorItem("item"));
        final DecisionNavigatorItem child = spy(new DecisionNavigatorItem("child"));
        final TreeSet<DecisionNavigatorItem> children = spy(new TreeSet<>());
        final TreeSet<DecisionNavigatorItem> expectedChildren = new TreeSet<DecisionNavigatorItem>() {{
            add(child);
        }};

        doReturn(children).when(item).getChildren();

        item.addChild(child);

        verify(item).removeChild(child);
        verify(children).add(child);
        assertEquals(expectedChildren, children);
    }

    @Test
    public void testCompareToWhenObjectIsNotADecisionNavigatorItem() {

        final DecisionNavigatorItem item = spy(new DecisionNavigatorItem("123"));
        final Object object = null;

        final int result = item.compareTo(object);

        assertTrue(result > 0);
    }

    @Test
    public void testCompareToWhenItemAndObjectAreEqual() {

        final DecisionNavigatorItem item = spy(new DecisionNavigatorItem("123"));
        final Object object = spy(new DecisionNavigatorItem("123"));

        final int result = item.compareTo(object);

        assertEquals(0, result);
    }

    @Test
    public void testCompareToWhenItemOrderingNameIsGreaterThanObjectOrderingName() {

        final DecisionNavigatorItem item = spy(new DecisionNavigatorItem("123", "Hat", null, null, null));
        final Object object = spy(new DecisionNavigatorItem("456", "Red", null, null, null));

        final int result = item.compareTo(object);

        assertTrue(result < 0);
    }

    @Test
    public void testCompareToWhenItemOrderingNameIsLessThanObjectOrderingName() {

        final DecisionNavigatorItem item = spy(new DecisionNavigatorItem("123", "Red", null, null, null));
        final Object object = spy(new DecisionNavigatorItem("456", "Hat", null, null, null));

        final int result = item.compareTo(object);

        assertTrue(result > 0);
    }
}
