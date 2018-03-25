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

import org.junit.Test;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.ITEM;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DecisionNavigatorItemTest {

    @Test
    public void testEqualsWhenItemsAreEqual() {

        final DecisionNavigatorItem item1 = new DecisionNavigatorItem("123");
        final DecisionNavigatorItem item2 = new DecisionNavigatorItem("123");

        assertEquals(item1, item2);
    }

    @Test
    public void testEqualsWhenItemsAreNotEqual() {

        final DecisionNavigatorItem item1 = new DecisionNavigatorItem("123");
        final DecisionNavigatorItem item2 = new DecisionNavigatorItem("456");

        assertNotEquals(item1, item2);
    }

    @Test
    public void testOnClick() {

        final Command command = mock(Command.class);
        final DecisionNavigatorItem item = new DecisionNavigatorItem("uuid", "label", ITEM, command);

        item.onClick();

        verify(command).execute();
    }
}
