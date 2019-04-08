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

package org.kie.workbench.common.dmn.client.decision.included.components;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Import;
import org.kie.workbench.common.dmn.api.editors.types.DMNIncludedNode;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class DecisionComponents {

    private final View view;

    private final DMNGraphUtils graphUtils;

    private final DMNIncludeModelsClient client;

    private final ManagedInstance<DecisionComponentsItem> itemManagedInstance;

    private final List<DecisionComponentsItem> decisionComponentsItems = new ArrayList<>();

    private final DecisionComponentFilter filter = new DecisionComponentFilter();

    @Inject
    public DecisionComponents(final View view,
                              final DMNGraphUtils graphUtils,
                              final DMNIncludeModelsClient client,
                              final ManagedInstance<DecisionComponentsItem> itemManagedInstance) {
        this.view = view;
        this.graphUtils = graphUtils;
        this.client = client;
        this.itemManagedInstance = itemManagedInstance;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public View getView() {
        return view;
    }

    public void refresh(final Diagram diagram) {
        clearComponents();
        view.showLoading();
        view.disableFilterInputs();
        client.loadNodesByNamespaces(getNamespaces(diagram), nodes -> {
            view.hideLoading();
            if (!nodes.isEmpty()) {
                nodes.forEach(this::addComponent);
                view.enableFilterInputs();
            } else {
                view.showEmptyState();
            }
        });
    }

    private void addComponent(final DMNIncludedNode node) {
        final DecisionComponentsItem item = makeDecisionComponentsItem(node);
        decisionComponentsItems.add(item);
        view.addListItem(item.getView().getElement());
    }

    private void clearComponents() {
        decisionComponentsItems.clear();
        view.clear();
    }

    private void applyFilter() {
        hideAllItems();
        showFilterdItems();
    }

    private void showFilterdItems() {
        filter.query(decisionComponentsItems.stream()).forEach(DecisionComponentsItem::show);
    }

    private void hideAllItems() {
        decisionComponentsItems.forEach(DecisionComponentsItem::hide);
    }

    void applyTermFilter(final String value) {
        filter.setTerm(value);
        applyFilter();
    }

    void applyDrgElementFilterFilter(final String value) {
        filter.setDrgElement(value);
        applyFilter();
    }

    private List<String> getNamespaces(final Diagram diagram) {
        return getImports(diagram)
                .stream()
                .map(Import::getNamespace)
                .collect(Collectors.toList());
    }

    private DecisionComponentsItem makeDecisionComponentsItem(final DMNIncludedNode node) {
        final DecisionComponentsItem item = itemManagedInstance.get();
        item.setDecisionComponent(makeDecisionComponent(node));
        return item;
    }

    private DecisionComponent makeDecisionComponent(final DMNIncludedNode node) {
        return new DecisionComponent(node.getModelName(), node.getDrgElementId(), node.getDrgElementName(), node.getDrgElementClass());
    }

    private List<Import> getImports(final Diagram diagram) {
        return graphUtils.getDefinitions(diagram).getImport();
    }

    public void removeAllItems() {
        clearComponents();
    }

    public interface View extends UberElemental<DecisionComponents>,
                                  IsElement {

        void clear();

        void addListItem(final HTMLElement htmlElement);

        void showEmptyState();

        void showLoading();

        void hideLoading();

        void disableFilterInputs();

        void enableFilterInputs();
    }
}
