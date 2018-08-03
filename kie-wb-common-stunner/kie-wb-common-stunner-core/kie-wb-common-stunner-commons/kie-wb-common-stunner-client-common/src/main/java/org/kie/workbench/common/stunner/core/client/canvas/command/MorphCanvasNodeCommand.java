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
package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.List;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

public class MorphCanvasNodeCommand extends AbstractCanvasCommand {

    private Node<? extends Definition<?>, Edge> candidate;
    private MorphDefinition morphDefinition;
    private String shapeSetId;

    public MorphCanvasNodeCommand(final Node<? extends Definition<?>, Edge> candidate,
                                  final MorphDefinition morphDefinition,
                                  final String shapeSetId) {
        this.candidate = candidate;
        this.morphDefinition = morphDefinition;
        this.shapeSetId = shapeSetId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        final Optional<Node> dockParentOptional = GraphUtils.getDockParent(candidate);
        final Optional<Node> parentOptional = getParent();

        // Removing parent from morphed node
        if (dockParentOptional.isPresent()) {
            context.undock(dockParentOptional.get(), candidate);
        } else {
            parentOptional.ifPresent(parent -> context.removeChild(parent, candidate));
        }

        // Deregister the existing shape.
        context.deregister(candidate);

        // Register the shape for the new morphed element.
        context.register(shapeSetId, candidate);

        context.applyElementMutation(candidate, MutationContext.STATIC);

        // Update incoming connections for new shape ( so magnets, connectors, etc on view side ).
        final List<Edge> inEdges = candidate.getInEdges();
        if (null != inEdges && !inEdges.isEmpty()) {
            for (final Edge inEdge : inEdges) {
                if (isViewEdge(inEdge)) {
                    final Node inNode = inEdge.getSourceNode();
                    updateConnections(context,
                                      inEdge,
                                      inNode,
                                      candidate);
                }
            }
        }

        // Update outgoing connections as well for new shape.
        final List<Edge> outEdges = candidate.getOutEdges();
        if (null != outEdges && !outEdges.isEmpty()) {
            for (final Edge outEdge : outEdges) {
                if (isViewEdge(outEdge)) {
                    final Node targetNode = outEdge.getTargetNode();
                    updateConnections(context,
                                      outEdge,
                                      candidate,
                                      targetNode);
                }
            }
        }

        // Adding parent to the new morphed node
        if (dockParentOptional.isPresent()) {
            context.dock(dockParentOptional.get(), candidate);
        } else {
            parentOptional.ifPresent(parent -> context.addChild(parent, candidate));
        }

        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return execute(context);
    }

    @SuppressWarnings("unchecked")
    private void updateConnections(final AbstractCanvasHandler context,
                                   final Edge edge,
                                   final Node sourceNode,
                                   final Node targetNode) {
        if (null != edge && null != sourceNode && null != targetNode) {
            final EdgeShape edgeShape = (EdgeShape) context.getCanvas().getShape(edge.getUUID());
            final Shape sourceNodeShape = context.getCanvas().getShape(sourceNode.getUUID());
            final Shape targetNodeShape = context.getCanvas().getShape(targetNode.getUUID());
            edgeShape.applyConnections(edge,
                                       sourceNodeShape.getShapeView(),
                                       targetNodeShape.getShapeView(),
                                       MutationContext.STATIC);
        }
    }

    private Optional<Node> getParent() {
        List<Edge> inEdges = candidate.getInEdges();
        if (null != inEdges && !inEdges.isEmpty()) {
            for (final Edge edge : inEdges) {
                if (isChildEdge(edge) || isDockEdge(edge)) {
                    return Optional.ofNullable(edge.getSourceNode());
                }
            }
        }
        return Optional.empty();
    }

    private boolean isChildEdge(final Edge edge) {
        return edge.getContent() instanceof Child;
    }

    private boolean isDockEdge(final Edge edge) {
        return edge.getContent() instanceof Dock;
    }

    private boolean isViewEdge(final Edge edge) {
        return edge.getContent() instanceof View;
    }

    @Override
    public String toString() {
        final Node parent = getParent().orElse(null);
        return getClass().getName() +
                " [parent=" + (null != parent ? parent.getUUID() : "null") + "," +
                " candidate=" + getUUID(candidate) + "," +
                " shapeSet=" + shapeSetId + "]";
    }
}
