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

package org.kie.workbench.common.dmn.client.decision.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.ITEM;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.ROOT;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.TABLE;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionNavigatorTreePresenterTest {

    @Mock
    private DecisionNavigatorTreePresenter.View view;

    @Mock
    private Map<String, DecisionNavigatorItem> indexedItems;

    private DecisionNavigatorTreePresenter presenter;

    @Before
    public void setup() {
        presenter = spy(new DecisionNavigatorTreePresenter(view));
        doReturn(indexedItems).when(presenter).getIndexedItems();
    }

    @Test
    public void testSetup() {

        presenter.setup();

        verify(view).init(presenter);
    }

    @Test
    public void testSetupItems() {

        final ArrayList<DecisionNavigatorItem> items = new ArrayList<>();

        doNothing().when(presenter).index(items);

        presenter.setupItems(items);

        verify(presenter).index(items);
        verify(view).clean();
        verify(view).setup(items);
    }

    @Test
    public void testAddOrUpdateItemWhenViewHasTheItem() {

        final DecisionNavigatorItem parent = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);

        when(view.hasItem(item)).thenReturn(true);

        presenter.addOrUpdateItem(parent, item);

        verify(presenter).index(asList(parent, item));
        verify(view).update(item);
        verify(view, never()).addItem(parent, item);
    }

    @Test
    public void testAddOrUpdateItemWhenViewDoesNotHaveTheItem() {

        final DecisionNavigatorItem parent = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);

        when(view.hasItem(item)).thenReturn(false);

        presenter.addOrUpdateItem(parent, item);

        verify(presenter).index(asList(parent, item));
        verify(view).addItem(parent, item);
        verify(view, never()).update(item);
    }

    @Test
    public void testRemove() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);

        presenter.remove(item);

        verify(view).remove(item);
    }

    @Test
    public void testRemoveAllItems() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);

        doReturn(item).when(presenter).findRoot();

        presenter.removeAllItems();

        verify(view).removeChildren(item);
        verify(indexedItems).clear();
        verify(presenter).index(item);
    }

    @Test
    public void testGetActiveParent() {

        final DecisionNavigatorItem expectedItem = mock(DecisionNavigatorItem.class);
        final String uuid = "uuid";

        when(indexedItems.get(uuid)).thenReturn(expectedItem);
        presenter.setActiveParentUUID(uuid);

        final DecisionNavigatorItem actualItem = presenter.getActiveParent();

        assertEquals(expectedItem, actualItem);
    }

    @Test
    public void testSelectItem() {

        final String uuid = "uuid";

        presenter.selectItem(uuid);

        verify(view).select(uuid);
    }

    @Test
    public void testDeselectItem() {

        presenter.deselectItem();

        verify(view).deselect();
    }

    @Test
    public void testListIndex() {

        final DecisionNavigatorItem item1 = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem item2 = mock(DecisionNavigatorItem.class);
        final List<DecisionNavigatorItem> items = Arrays.asList(item1, item2);

        presenter.index(items);

        verify(presenter).index(item1);
        verify(presenter).index(item2);
    }

    @Test
    public void testItemIndex() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem child1 = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem child2 = mock(DecisionNavigatorItem.class);
        final List<DecisionNavigatorItem> children = Arrays.asList(child1, child2);
        final String uuid = "uuid";

        when(item.getUUID()).thenReturn(uuid);
        when(item.getChildren()).thenReturn(children);

        presenter.index(item);

        verify(indexedItems).put(uuid, item);
        verify(presenter).index(children);
    }

    @Test
    public void testFindRoot() {

        final DecisionNavigatorItem expectedRoot = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem item1 = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem item2 = mock(DecisionNavigatorItem.class);
        final List<DecisionNavigatorItem> values = Arrays.asList(expectedRoot, item1, item2);

        when(expectedRoot.getType()).thenReturn(ROOT);
        when(item1.getType()).thenReturn(ITEM);
        when(item2.getType()).thenReturn(TABLE);
        when(indexedItems.values()).thenReturn(values);

        final DecisionNavigatorItem actualRoot = presenter.findRoot();

        assertEquals(expectedRoot, actualRoot);
    }
}
