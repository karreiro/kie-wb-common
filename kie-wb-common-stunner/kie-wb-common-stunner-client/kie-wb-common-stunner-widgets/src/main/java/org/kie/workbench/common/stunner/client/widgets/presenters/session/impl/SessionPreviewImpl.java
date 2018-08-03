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

package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import java.lang.annotation.Annotation;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import com.ait.tooling.common.api.java.util.function.Predicate;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.DiagramViewer;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl.DiagramPreviewProxy;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.BaseCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SingleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.controls.zoom.ZoomControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasUndoCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.client.session.impl.InstanceUtils;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * A generic session's preview instance for subtypes of <code>AbstractClientSession</code>.
 * It aggregates a custom diagram preview type which provides binds the editors's diagram instance
 * with the diagram and controls for the given session. It also scales the view to the given
 * size for the preview.
 */
@Dependent
@Typed(SessionDiagramPreview.class)
@Default
public class SessionPreviewImpl<S extends AbstractSession>
        extends AbstractSessionViewer<S>
        implements SessionDiagramPreview<S> {

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;

    private final DefinitionUtils definitionUtils;
    private final GraphUtils graphUtils;
    private final ShapeManager shapeManager;
    private final TextPropertyProviderFactory textPropertyProviderFactory;
    private final ManagedInstance<AbstractCanvas> canvases;
    private final ManagedInstance<BaseCanvasHandler> canvasHandlers;
    private final ManagedInstance<ZoomControl<AbstractCanvas>> zoomControls;
    private final ManagedInstance<SelectionControl<AbstractCanvasHandler, Element>> selectionControls;
    private final ManagedInstance<CanvasCommandFactory> canvasCommandFactories;
    private final ManagedInstance<CanvasCommandManager<AbstractCanvasHandler>> canvasCommandManagers;

    private AbstractCanvas canvas;
    private SessionPreviewCanvasHandlerProxy canvasHandler;
    private ZoomControl<AbstractCanvas> zoomControl;
    private SelectionControl<AbstractCanvasHandler, Element> selectionControl;
    private CanvasCommandFactory commandFactory;
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;
    private DiagramPreviewProxy<Diagram> diagramPreview;
    private Predicate<Command<AbstractCanvasHandler, CanvasViolation>> isCommandAllowed;

    @Inject
    @SuppressWarnings("unchecked")
    public SessionPreviewImpl(final DefinitionUtils definitionUtils,
                              final GraphUtils graphUtils,
                              final ShapeManager shapeManager,
                              final TextPropertyProviderFactory textPropertyProviderFactory,
                              final @Any ManagedInstance<AbstractCanvas> canvases,
                              final @Any ManagedInstance<BaseCanvasHandler> canvasHandlers,
                              final @Any ManagedInstance<ZoomControl<AbstractCanvas>> zoomControls,
                              final @Any @SingleSelection ManagedInstance<SelectionControl<AbstractCanvasHandler, Element>> selectionControls,
                              final @Any ManagedInstance<CanvasCommandFactory> canvasCommandFactories,
                              final @Any ManagedInstance<CanvasCommandManager<AbstractCanvasHandler>> canvasCommandManagers,
                              final WidgetWrapperView view,
                              final StunnerPreferencesRegistries preferencesRegistries) {
        this.definitionUtils = definitionUtils;
        this.graphUtils = graphUtils;
        this.shapeManager = shapeManager;
        this.textPropertyProviderFactory = textPropertyProviderFactory;
        this.canvases = canvases;
        this.canvasHandlers = canvasHandlers;
        this.zoomControls = zoomControls;
        this.selectionControls = selectionControls;
        this.canvasCommandFactories = canvasCommandFactories;
        this.canvasCommandManagers = canvasCommandManagers;
        this.isCommandAllowed = c -> true;
        this.diagramPreview =
                new DiagramPreviewProxy<Diagram>(view,
                                                 preferencesRegistries) {
                    @Override
                    public SelectionControl<AbstractCanvasHandler, Element> getSelectionControl() {
                        return selectionControl;
                    }

                    @Override
                    public <C extends Canvas> ZoomControl<C> getZoomControl() {
                        return (ZoomControl<C>) zoomControl;
                    }

                    @Override
                    protected int getWidth() {
                        return DEFAULT_WIDTH;
                    }

                    @Override
                    protected int getHeight() {
                        return DEFAULT_HEIGHT;
                    }

                    @Override
                    protected void onOpen(final Diagram diagram) {
                        SessionPreviewImpl.this.onOpen(diagram);
                    }

                    @Override
                    protected AbstractCanvas getCanvas() {
                        return canvas;
                    }

                    @Override
                    protected CanvasCommandFactory getCanvasCommandFactory() {
                        return commandFactory;
                    }

                    @Override
                    protected BaseCanvasHandler<Diagram, ?> getCanvasHandler() {
                        return canvasHandler;
                    }

                    @Override
                    protected void enableControls() {
                        zoomControl.init(canvas);
                        zoomControl.setMinScale(0);
                        zoomControl.setMaxScale(1);
                    }

                    @Override
                    protected void destroyControls() {
                        zoomControl.destroy();
                    }

                    @Override
                    protected void destroyInstances() {
                        SessionPreviewImpl.this.destroyInstances();
                    }
                };
        this.canvas = null;
        this.zoomControl = null;
    }

    public SessionPreviewImpl setCommandAllowed(final Predicate<Command<AbstractCanvasHandler, CanvasViolation>> isCommandAllowed) {
        this.isCommandAllowed = isCommandAllowed;
        return this;
    }

    @SuppressWarnings("unchecked")
    private void onOpen(final Diagram diagram) {
        final Annotation qualifier = definitionUtils.getQualifier(diagram.getMetadata().getDefinitionSetId());
        final BaseCanvasHandler delegate = InstanceUtils.lookup(canvasHandlers, qualifier);
        canvas = InstanceUtils.lookup(canvases, qualifier);
        canvasHandler = new SessionPreviewCanvasHandlerProxy(delegate,
                                                             definitionUtils.getDefinitionManager(),
                                                             graphUtils,
                                                             shapeManager,
                                                             textPropertyProviderFactory);
        zoomControl = InstanceUtils.lookup(zoomControls, qualifier);
        selectionControl = InstanceUtils.lookup(selectionControls, qualifier);
        commandFactory = InstanceUtils.lookup(canvasCommandFactories, qualifier);
        commandManager = InstanceUtils.lookup(canvasCommandManagers, qualifier);
    }

    private void destroyInstances() {
        canvases.destroy(canvas);
        canvases.destroyAll();
        canvasHandlers.destroy(canvasHandler.getWrapped());
        canvasHandlers.destroyAll();
        zoomControls.destroy(zoomControl);
        zoomControls.destroyAll();
        selectionControls.destroy(selectionControl);
        selectionControls.destroyAll();
        canvasCommandFactories.destroy(commandFactory);
        canvasCommandFactories.destroyAll();
        canvasCommandManagers.destroy(commandManager);
        canvasCommandManagers.destroyAll();
        canvas = null;
        canvasHandler = null;
        zoomControl = null;
        selectionControl = null;
        commandFactory = null;
        commandManager = null;
        diagramPreview = null;
        isCommandAllowed = null;
    }

    public CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return commandManager;
    }

    public SessionPreviewCanvasHandlerProxy getCanvasHandler() {
        return canvasHandler;
    }

    @Override
    public AbstractCanvas getCanvas() {
        return canvas;
    }

    public CanvasCommandFactory getCommandFactory() {
        return commandFactory;
    }

    @Override
    protected DiagramViewer<Diagram, AbstractCanvasHandler> getDiagramViewer() {
        return diagramPreview;
    }

    @Override
    protected Diagram getDiagram() {
        return null != getSessionHandler() ? getSessionHandler().getDiagram() : null;
    }

    @SuppressWarnings("unchecked")
    void commandExecutedFired(@Observes CanvasCommandExecutedEvent commandExecutedEvent) {
        checkNotNull("commandExecutedEvent",
                     commandExecutedEvent);
        final Command<AbstractCanvasHandler, CanvasViolation> command = commandExecutedEvent.getCommand();
        if (isCommandAllowed.test(command)) {
            final AbstractCanvasHandler context = (AbstractCanvasHandler) commandExecutedEvent.getCanvasHandler();
            final CommandResult<CanvasViolation> result = commandExecutedEvent.getResult();
            onExecute(context,
                      command,
                      result);
        }
    }

    @SuppressWarnings("unchecked")
    void commandUndoExecutedFired(@Observes CanvasUndoCommandExecutedEvent commandUndoExecutedEvent) {
        checkNotNull("commandUndoExecutedEvent",
                     commandUndoExecutedEvent);
        final Command<AbstractCanvasHandler, CanvasViolation> command = commandUndoExecutedEvent.getCommand();
        if (isCommandAllowed.test(command)) {
            final AbstractCanvasHandler context = (AbstractCanvasHandler) commandUndoExecutedEvent.getCanvasHandler();
            final CommandResult<CanvasViolation> result = commandUndoExecutedEvent.getResult();
            onUndo(context,
                   command,
                   result);
        }
    }

    private void onExecute(final AbstractCanvasHandler context,
                           final Command<AbstractCanvasHandler, CanvasViolation> command,
                           final CommandResult<CanvasViolation> result) {
        if (isOperationAllowed(context,
                               result)) {
            getCommandManager().execute(getDiagramViewer().getHandler(),
                                        command);
        }
    }

    private void onUndo(final AbstractCanvasHandler context,
                        final Command<AbstractCanvasHandler, CanvasViolation> command,
                        final CommandResult<CanvasViolation> result) {
        if (isOperationAllowed(context,
                               result)) {
            getCommandManager().undo(getDiagramViewer().getHandler(),
                                     command);
        }
    }

    private boolean isOperationAllowed(final AbstractCanvasHandler sessionHandlerContext,
                                       final CommandResult<CanvasViolation> result) {
        return isSameContext(sessionHandlerContext) && !CommandUtils.isError(result);
    }

    private boolean isSameContext(final AbstractCanvasHandler sessionHandlerContext) {
        return null != getSessionHandler() &&
                getSessionHandler().equals(sessionHandlerContext);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ZoomControl<AbstractCanvas> getZoomControl() {
        return zoomControl;
    }

    /**
     * For preview purposes, make more visible the decorator for the canvas, so update it once
     * the canvas has been initialized.
     */
    @Override
    protected DiagramViewer.DiagramViewerCallback<Diagram> buildCallback(final SessionViewerCallback<Diagram> callback) {
        return new DiagramViewer.DiagramViewerCallback<Diagram>() {
            @Override
            public void onOpen(final Diagram diagram) {
                callback.onOpen(diagram);
            }

            @Override
            public void afterCanvasInitialized() {
                checkNotNull("canvas",
                             canvas);
                updateCanvasDecorator(canvas.getView());
                callback.afterCanvasInitialized();
            }

            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                callback.onError(error);
            }
        };
    }

    /**
     * Updates the canvas decorator for preview purposes using
     * a higher width and darker color for the line stroke.
     */
    private void updateCanvasDecorator(final AbstractCanvas.View canvasView) {
        canvasView.setDecoratorStrokeWidth(2);
        canvasView.setDecoratorStrokeAlpha(0.8);
        canvasView.setDecoratorStrokeColor("#404040");
    }
}
