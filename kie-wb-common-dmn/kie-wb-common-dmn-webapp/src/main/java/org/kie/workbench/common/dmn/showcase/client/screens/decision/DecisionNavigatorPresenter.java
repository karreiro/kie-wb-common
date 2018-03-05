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

package org.kie.workbench.common.dmn.showcase.client.screens.decision;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.showcase.client.screens.decision.tree.DecisionNavigatorTreePresenter;
import org.kie.workbench.common.dmn.showcase.client.screens.decision.tree.DecisionNavigatorTreeView;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

import static org.kie.workbench.common.dmn.showcase.client.screens.decision.DecisionNavigatorItem.Type.COLUMNS;
import static org.kie.workbench.common.dmn.showcase.client.screens.decision.DecisionNavigatorItem.Type.ITEM;
import static org.kie.workbench.common.dmn.showcase.client.screens.decision.DecisionNavigatorItem.Type.ROOT;
import static org.kie.workbench.common.dmn.showcase.client.screens.decision.DecisionNavigatorItem.Type.SUB_ITEM;
import static org.kie.workbench.common.dmn.showcase.client.screens.decision.DecisionNavigatorItem.Type.TABLE;

@ApplicationScoped
@WorkbenchScreen(identifier = "org.kie.dmn.decision.navigator")
public class DecisionNavigatorPresenter {

    @Inject
    private View view;

    @Inject
    private DecisionNavigatorTreePresenter treePresenter;

    @WorkbenchPartView
    public UberElemental<DecisionNavigatorPresenter> getView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Decision Navigator";
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return CompassPosition.WEST;
    }

    @PostConstruct
    private void setup() {
        view.init(this);
    }

    public DecisionNavigatorTreeView getTreeView() {
        return initTreePresenter().getView();
    }

    private DecisionNavigatorTreePresenter initTreePresenter() {
        final DecisionNavigatorTreePresenter treePresenter = this.treePresenter;
        treePresenter.setupItems(getItems());
        return treePresenter;
    }

    private List<DecisionNavigatorItem> getItems() {

        return new ArrayList<DecisionNavigatorItem>() {{

            final List<DecisionNavigatorItem> goOutChildren = new ArrayList<DecisionNavigatorItem>() {{
                add(makeItem("Preferred restaurants", COLUMNS));
            }};

            final List<DecisionNavigatorItem> dineAtHomeChildren = new ArrayList<DecisionNavigatorItem>() {{
                add(makeItem("Ingredients", TABLE));
            }};

            final List<DecisionNavigatorItem> planningChildren = new ArrayList<DecisionNavigatorItem>() {{
                add(makeItem("cookbook", ITEM));
                add(makeItem("Go out", ITEM, goOutChildren));
                add(makeItem("Zagat's guide", ITEM));
                add(makeItem("Big name, big name, big name, big name, big name, big name, big name, big name, big name, big name", ITEM));
                add(makeItem("Number of diners", SUB_ITEM));
                add(makeItem("What's in the fridge", SUB_ITEM));
                add(makeItem("Dine at home", ITEM, dineAtHomeChildren));
            }};

            add(makeItem("Planning", ROOT, planningChildren));
        }};
    }

    private DecisionNavigatorItem makeItem(final String label,
                                           final DecisionNavigatorItem.Type subItem) {
        return new DecisionNavigatorItem(label, subItem);
    }

    private DecisionNavigatorItem makeItem(final String label,
                                           final DecisionNavigatorItem.Type item,
                                           final List<DecisionNavigatorItem> children) {
        return new DecisionNavigatorItem(label, item, children);
    }

    public interface View extends UberElemental<DecisionNavigatorPresenter> {

    }
}
