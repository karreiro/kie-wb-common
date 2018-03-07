/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.AbstractCanvasHandlerEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.AbstractCanvasShapeEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.components.toolbox.Toolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.graph.Element;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public abstract class AbstractToolboxControl
        implements ToolboxControl<AbstractCanvasHandler, Element> {

    private final ToolboxControlImpl<ActionsToolboxFactory> toolboxControl;
    private final SingleItemSelectedShowPredicate toolboxShowPredicate;

    // It makes the toolbox appear only if single selection.
    private static class SingleItemSelectedShowPredicate implements Predicate<String> {

        private String id;
        private int count;

        @Override
        public boolean test(String s) {
            return (null == id && count == 0) || (null != id && count == 1 && id.equals(s));
        }
    }

    protected abstract List<ActionsToolboxFactory> getFactories();

    @Inject
    public AbstractToolboxControl() {
        this.toolboxShowPredicate = new SingleItemSelectedShowPredicate();
        this.toolboxControl = new ToolboxControlImpl<>(this::getFactories,
                                                       toolboxShowPredicate);
    }

    AbstractToolboxControl(final ToolboxControlImpl<ActionsToolboxFactory> toolboxControl) {
        this.toolboxShowPredicate = new SingleItemSelectedShowPredicate();
        this.toolboxControl = toolboxControl;
    }

    @Override
    public void enable(final AbstractCanvasHandler context) {
        toolboxControl.enable(context);
    }

    @Override
    public void disable() {
        toolboxControl.disable();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(final Element element) {
        toolboxControl.register(element);
    }

    @Override
    public void deregister(final Element element) {
        toolboxControl.deregister(element);
    }

    @Override
    public Iterator<Toolbox<?>> getToolboxes(final Element element) {
        return toolboxControl.getToolboxes(element);
    }

    void onCanvasSelectionEvent(final @Observes CanvasSelectionEvent event) {
        checkNotNull("event",
                     event);
        //GWT.log("========> 123");
        handleCanvasSelectionEvent(event);
    }

    void onCanvasClearSelectionEvent(final @Observes CanvasClearSelectionEvent event) {
        checkNotNull("event", 
                     event);
        handleCanvasClearSelectionEvent(event);
    }

    void onCanvasShapeRemovedEvent(final @Observes CanvasShapeRemovedEvent event) {
        checkNotNull("event",
                     event);
        handleCanvasShapeRemovedEvent(event);
    }

    protected void handleCanvasSelectionEvent(final CanvasSelectionEvent event) {
        //GWT.log("========> 456");
        if (checkEventContext(event)) {
            if (1 == event.getIdentifiers().size()) {
                final String uuid = event.getIdentifiers().iterator().next();
                show(uuid);
            } else {
                Collection<String> identifiers = event.getIdentifiers();
                showMultiple(identifiers);
            }
        }
    }

    protected void handleCanvasClearSelectionEvent(final CanvasClearSelectionEvent event) {
        if (checkEventContext(event)) {
            destroy();
        }
    }

    protected void handleCanvasShapeRemovedEvent(final CanvasShapeRemovedEvent event) {
        if (checkEventContext(event)) {
            destroy();
        }
    }

    private void show(final String uuid) {
        toolboxShowPredicate.id = uuid;
        toolboxShowPredicate.count = 1;
        toolboxControl.show(uuid);
    }

    private void showMultiple(final Collection<String> ids) {
        toolboxShowPredicate.id = ids.iterator().next();
        toolboxShowPredicate.count = ids.size();
        toolboxControl.destroy();
    }

    private void destroy() {
        toolboxShowPredicate.id = null;
        toolboxShowPredicate.count = 0;
        toolboxControl.destroy();
    }

    private boolean checkEventContext(final AbstractCanvasHandlerEvent canvasHandlerEvent) {
        final CanvasHandler _canvasHandler = canvasHandlerEvent.getCanvasHandler();
        return toolboxControl.getCanvasHandler() != null
                && toolboxControl.getCanvasHandler().equals(_canvasHandler);
    }

    private boolean checkEventContext(final AbstractCanvasShapeEvent canvasShapeEvent) {
        return toolboxControl.getCanvasHandler() != null
                && toolboxControl.getCanvasHandler().getCanvas().equals(canvasShapeEvent.getCanvas())
                && toolboxControl.isActive(canvasShapeEvent.getShape().getUUID());
    }
}
