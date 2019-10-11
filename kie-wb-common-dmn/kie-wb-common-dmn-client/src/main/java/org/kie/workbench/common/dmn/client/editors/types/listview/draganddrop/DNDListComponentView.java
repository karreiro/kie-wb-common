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

package org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.Factory;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.Position;

import static java.util.Collections.emptyList;
import static org.kie.workbench.common.dmn.client.editors.common.RemoveHelper.removeChildren;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.asDraggable;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.asDragging;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.asHover;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.asNonDragging;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.asNonHover;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.getCSSMargin;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.getCSSTop;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.isGrip;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.querySelector;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.setCSSMargin;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.setCSSTop;

@Templated
public class DNDListComponentView implements DNDListComponent.View {

    @DataField("drag-area")
    private final HTMLDivElement dragArea;

    private List<HTMLElement> dependentElements = emptyList();

    private HTMLElement dragging;

    private DNDListComponent presenter;

    @Inject
    public DNDListComponentView(final HTMLDivElement dragArea) {
        this.dragArea = dragArea;
    }

    @Override
    public void init(final DNDListComponent presenter) {

        this.presenter = presenter;

        setupDragAreaHandlers();
    }

    @Override
    public HTMLElement registerItem(final HTMLElement htmlElement) {

        final HTMLElement item = createItem(htmlElement);

        Position.setY(item, getMaxPositionY() + 1);
        Position.setX(item, 0);

        dragArea.appendChild(item);

        return item;
    }

    @Override
    public void refreshItemsPosition() {

        int numberOfVisibleElements = 0;

        for (final HTMLElement draggable : querySelector(dragArea).getDraggableElements()) {

            final int positionY = Position.getY(draggable);
            final int positionX = Position.getX(draggable);
            final int top = positionY * getItemHeight();
            final int margin = positionX * getLevelSize();

            setCSSTop(draggable, top);
            setCSSMargin(draggable, margin);

            if (positionY > -1) {
                numberOfVisibleElements++;
            }
        }

        refreshDragAreaSize(numberOfVisibleElements);
    }

    @Override
    public void refreshItemsHTML() {

        final List<HTMLElement> draggableElements = querySelector(dragArea).getSortedDraggableElements();

        removeChildren(dragArea);
        draggableElements.forEach(dragArea::appendChild);
    }

    @Override
    public void consolidateHierarchicalLevel() {

        final List<HTMLElement> draggableElements = querySelector(dragArea).getVisibleAndSortedDraggableElements();

        if (!draggableElements.isEmpty()) {
            Position.setX(draggableElements.get(0), 0);
        }

        if (draggableElements.size() < 2) {
            return;
        }

        for (int i = 0; i < draggableElements.size() - 1; i++) {

            final HTMLElement current = draggableElements.get(i);
            final HTMLElement next = draggableElements.get(i + 1);
            final int currentXPosition = Position.getX(current);
            final int minimalLevel = currentXPosition + 1;
            final int nextElementLevel = Position.getX(next);
            final int numberOfExtraLevels = nextElementLevel - minimalLevel;

            if (nextElementLevel > minimalLevel) {
                fixChildrenPosition(minimalLevel, numberOfExtraLevels, getDependentElements(current));
            }

            Position.setY(current, i);
            Position.setY(next, i + 1);
        }
    }

    @Override
    public void consolidatePositionY() {

        final List<HTMLElement> draggableElements = querySelector(dragArea).getVisibleDraggableElements();

        for (int i = 0; i < draggableElements.size(); i++) {
            Position.setY(draggableElements.get(i), i);
        }
    }

    @Override
    public void clear() {
        removeChildren(dragArea);
    }

    @Override
    public Element getPreviousElement(final Element reference) {
        final int positionY = Position.getY(reference);
        return querySelector(dragArea).getDraggableElement(positionY - 1);
    }

    void setupDragAreaHandlers() {
        dragArea.onmousedown = (e) -> {
            onStartDrag(e);
            return true;
        };
        dragArea.onmousemove = (e) -> {
            onDrag(e);
            return true;
        };
        dragArea.onmouseup = (e) -> {
            onDrop();
            return true;
        };
        dragArea.onmouseout = (e) -> {
            onDrop();
            return true;
        };
    }

    // -- Drag and Drop Handlers

    void onStartDrag(final Event event) {

        final HTMLElement target = (HTMLElement) event.target;
        final HTMLElement parent = (HTMLElement) target.parentNode;

        if (isGrip(target)) {
            holdDraggingElement(parent);
        }
    }

    void onDrag(final Event event) {

        if (isNotDragging()) {
            return;
        }

        updateDraggingElementY(event);
        updateDraggingElementX(event);
        updateHoverElement();
        updateDependentsPosition();
    }

    void onDrop() {

        if (isNotDragging()) {
            return;
        }

        updateDraggingElementsPosition();
        executeOnDropItemCallback();
        releaseDraggingElement();
        consolidateHierarchicalLevel();
        refreshItemsPosition();
        refreshItemsHTML();
        clearHover();
    }

    // ---

    private void updateDraggingElementsPosition() {

        final int currentXPosition = getCurrentXPosition(dragging);
        final int currentYPosition = Position.getY(dragging);

        final Element previousElement = querySelector(dragArea).getDraggableElement(currentYPosition - 1);

        final boolean hasChildren = hasChildren(previousElement);
        final int inc = hasChildren ? 1 : 0;

        Position.setX(dragging, currentXPosition + inc);
        dependentElements.forEach(el -> Position.setX(el, inc + getCurrentXPosition(el)));
    }

    private void refreshDragAreaSize(final int numberOfElements) {

        final int border = 1;
        final int elementHeight = getItemHeight();
        final int height = numberOfElements * elementHeight + border;

        dragArea.style.setProperty("height", height + "px");
    }

    int getMaxPositionY() {
        return querySelector(dragArea)
                .getDraggableElements()
                .stream()
                .mapToInt(Position::getY)
                .max()
                .orElse(-1);
    }

    private int getCurrentXPosition(final HTMLElement element) {
        final int margin = getCSSMargin(element) / getLevelSize();
        return margin > 0 ? margin : 0;
    }

    private void executeOnDropItemCallback() {

        final HTMLElement hoverElement = querySelector(dragArea).getHoverElement();

        if (hoverElement != null) {

            final int currentXPosition = Position.getX(hoverElement);
            final int minimalLevel = currentXPosition + 1;
            final int numberOfExtraLevels = Position.getX(dragging) - minimalLevel;

            final List<HTMLElement> children = new ArrayList<HTMLElement>(dependentElements) {{
                add(dragging);
            }};

            Position.setX(dragging, currentXPosition + 1);

            fixChildrenPosition(minimalLevel, numberOfExtraLevels, children);
        }

        executeOnDropItemCallback(dragging, hoverElement);
    }

    private void executeOnDropItemCallback(final Element current,
                                           final Element hover) {
        presenter.executeOnDropItemCallback(current, hover);
    }

    private int getDraggingYCoordinate() {
        return (int) (dragging.offsetTop + (getItemHeight() / 2));
    }

    private void hover(final int hoverPosition) {

        final HTMLElement hoverElement = querySelector(dragArea).getDraggableElement(hoverPosition);
        final boolean notDragging = hoverElement != null && !DNDListDOMHelper.isDraggingElement(hoverElement);

        clearHover();

        if (notDragging) {
            asHover(hoverElement);
        }
    }

    private void clearHover() {
        final HTMLElement element = querySelector(dragArea).getHoverElement();
        if (element != null) {
            asNonHover(element);
        }
    }

    private void updateDependentsPosition() {

        final List<HTMLElement> elements = Optional.ofNullable(dependentElements).orElse(emptyList());

        for (int i = 0; i < elements.size(); i++) {

            final HTMLElement dependent = elements.get(i);
            final int dependentTop = getCSSTop(dragging) + (presenter.getItemHeight() * (i + 1));
            final int dependentMargin = getCSSMargin(dragging) + ((Position.getX(dependent) - Position.getX(dragging)) * presenter.getIndentationSize());

            setCSSTop(dependent, dependentTop);
            setCSSMargin(dependent, dependentMargin);
        }
    }

    private boolean hasChildren(final Element element) {

        if (element == null) {
            return false;
        }

        final Element next = getNextElement(element, nextElement -> {
            final boolean isNotDragging = !Objects.equals(nextElement, dragging);
            final boolean isNotDependentElement = !dependentElements.contains(nextElement);
            return isNotDragging && isNotDependentElement;
        });

        if (next == null) {
            return false;
        }

        final int currentPositionX = Position.getX(element);
        final int nextPositionX = Position.getX(next);

        return currentPositionX == nextPositionX - 1;
    }

    private Element getNextElement(final Element element,
                                   final Function<HTMLElement, Boolean> function) {

        final int nextElementPosition = Position.getY(element) + 1;
        final HTMLElement next = querySelector(dragArea).getDraggableElement(nextElementPosition);

        if (function.apply(next) || next == null) {
            return next;
        }

        return getNextElement(next, function);
    }

    private void fixChildrenPosition(final int minimalXPosition,
                                     final int numberOfExtraLevels,
                                     final List<HTMLElement> children) {

        for (int i = 0; i < children.size(); i++) {

            final HTMLElement dependentElement = children.get(i);
            final int elementPosition = Position.getX(dependentElement);
            final boolean isElementPositionValid = i > 0 && elementPosition >= Position.getX(children.get(i - 1));

            if (!isElementPositionValid) {
                final int positionX = elementPosition - numberOfExtraLevels;
                final int newElementPosition = positionX < minimalXPosition ? minimalXPosition : positionX;
                Position.setX(dependentElement, newElementPosition);
            }
        }
    }

    private DNDMinMaxTuple getMinMaxDraggingYCoordinates() {

        /*
         * The sibling element has some padding to allow the dragging element hovered. And, only when the dragging
         * element overcomes the padding level, the sibling element changes its position.
         *  _____________________________________
         * |   _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _   | <= min
         * |                                     |
         * |          DRAGGING ELEMENT           |
         * |   _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _   | <= max
         * |_____________________________________|
         *      _____________________________________
         *     |                                     |
         *     |                                     |
         *     |          DRAGGING ELEMENT           |
         *     |                                     |
         *     |_____________________________________|
         *
         */

        final int draggingYCoordinate = getDraggingYCoordinate();
        final int padding = getDragPadding();
        final int max = (draggingYCoordinate + padding) / getItemHeight();
        final int min = (draggingYCoordinate - padding) / getItemHeight();

        return new DNDMinMaxTuple(max, min);
    }

    private void updateHoverElement() {

        final int draggingYPosition = Position.getY(dragging);
        final int hoverPosition = getDraggingYCoordinate() / getItemHeight();
        final DNDMinMaxTuple minMaxTuple = getMinMaxDraggingYCoordinates();

        if (draggingYPosition < minMaxTuple.max || draggingYPosition > minMaxTuple.min) {
            hover(hoverPosition);
        }
    }

    private void updateDraggingElementY(final Event event) {

        final int draggingYPosition = Position.getY(dragging);
        final int mouseYPosition = getDraggingYCoordinate() / getItemHeight();
        final DNDMinMaxTuple minMaxTuple = getMinMaxDraggingYCoordinates();

        if (draggingYPosition < minMaxTuple.min) {
            updateDraggingElementY(mouseYPosition, draggingYPosition, mouseYPosition + dependentElements.size());
        }

        if (draggingYPosition > minMaxTuple.max) {
            updateDraggingElementY(mouseYPosition, mouseYPosition + dependentElements.size() + 1, mouseYPosition);
        }

        setCSSTop(dragging, getNewDraggingYPosition(event));
    }

    private void updateDraggingElementX(final Event event) {
        setCSSMargin(dragging, getNewDraggingXPosition(event));
    }

    private void updateDraggingElementY(final int mouseYPosition,
                                        final int newSiblingYPosition,
                                        final int oldSiblingYPosition) {

        final Element siblingElement = querySelector(dragArea).getDraggableElement(oldSiblingYPosition);

        if (siblingElement == null) {
            return;
        }

        Position.setY(siblingElement, newSiblingYPosition);
        Position.setY(dragging, mouseYPosition);

        clearHover();

        for (int i = 0; i < dependentElements.size(); i++) {
            Position.setY(dependentElements.get(i), mouseYPosition + i + 1);
        }

        refreshItemsPosition();
    }

    private int getNewDraggingYPosition(final Event event) {

        final Double absoluteMouseY = getAbsoluteMouseY(event);
        final Double newYPosition = absoluteMouseY - (getItemHeight() / 2d);
        final Double maxYPosition = getDragAreaY() + dragArea.offsetHeight;

        if (newYPosition < 0) {
            return newYPosition.intValue();
        }

        if (newYPosition > maxYPosition) {
            return maxYPosition.intValue();
        }

        return newYPosition.intValue();
    }

    private int getNewDraggingXPosition(final Event event) {

        // Represents the "padding" between the dragging element border and the cursor
        final int padding = 10;
        final double absoluteMouseX = getAbsoluteMouseX(event);
        final int newXPosition = (int) absoluteMouseX - padding;
        final int maxXPosition = getItemWidth() - getLevelSize();

        if (newXPosition < 0) {
            return 0;
        }

        if (newXPosition > maxXPosition) {
            return maxXPosition;
        }

        return newXPosition;
    }

    private boolean isNotDragging() {
        return !Optional.ofNullable(dragging).isPresent();
    }

    private double getAbsoluteMouseX(final Event event) {
        final MouseEvent mouseEvent = (MouseEvent) event;
        return mouseEvent.x - getDragAreaX();
    }

    private double getAbsoluteMouseY(final Event event) {
        final MouseEvent mouseEvent = (MouseEvent) event;
        return mouseEvent.y - getDragAreaY();
    }

    private void holdDraggingElement(final HTMLElement element) {
        this.dragging = element;
        dependentElements = getDependentElements(element);

        asDragging(dragging);
        dependentElements.forEach(DNDListDOMHelper::asDragging);
    }

    private void releaseDraggingElement() {

        asNonDragging(dragging);
        dependentElements.forEach(DNDListDOMHelper::asNonDragging);

        dependentElements = emptyList();
        this.dragging = null;
    }

    List<HTMLElement> getDependentElements(final HTMLElement element) {

        final int minimalLevel = Position.getX(element) + 1;
        final List<HTMLElement> initial = new ArrayList<>();

        return getNextDependents(initial, element, minimalLevel);
    }

    private List<HTMLElement> getNextDependents(final List<HTMLElement> dependents,
                                                final Element element,
                                                final int minimalLevel) {

        final int positionY = Position.getY(element);
        final HTMLElement next = querySelector(dragArea).getDraggableElement(positionY + 1);

        if (next == null || Position.getX(next) < minimalLevel) {
            return dependents;
        } else {
            dependents.add(next);
            return getNextDependents(dependents, next, minimalLevel);
        }
    }

    private int getItemWidth() {
        return (int) dragArea.offsetWidth;
    }

    private double getDragAreaY() {
        return dragArea.getBoundingClientRect().top;
    }

    private double getDragAreaX() {
        return dragArea.getBoundingClientRect().left;
    }

    private int getLevelSize() {
        return presenter.getIndentationSize();
    }

    private int getItemHeight() {
        return presenter.getItemHeight();
    }

    private int getDragPadding() {
        return getItemHeight() / 3;
    }

    HTMLElement createItem(final HTMLElement htmlElement) {

        final HTMLElement item = Factory.createDiv();
        final HTMLElement gripElement = Factory.createGripElement();

        item.appendChild(gripElement);
        item.appendChild(htmlElement);

        return asDraggable(item);
    }

    private class DNDMinMaxTuple {

        private final int min;
        private final int max;

        DNDMinMaxTuple(final int max,
                       final int min) {
            this.max = max;
            this.min = min;
        }
    }
}
