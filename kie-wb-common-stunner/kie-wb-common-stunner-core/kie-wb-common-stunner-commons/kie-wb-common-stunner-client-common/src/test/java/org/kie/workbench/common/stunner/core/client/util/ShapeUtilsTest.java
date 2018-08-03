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

package org.kie.workbench.common.stunner.core.client.util;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.impl.ConnectorShape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShapeUtilsTest {

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Index graphIndex;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private ConnectorShape edge1Shape;

    @Mock
    private ShapeView edge1ShapeView;

    @Mock
    private ConnectorShape edge2Shape;

    @Mock
    private ShapeView edge2ShapeView;

    private TestingGraphInstanceBuilder.TestGraph2 instance2;

    private ControlPoint controlPoint1;

    private List<ControlPoint> controlPointList;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        final TestingGraphMockHandler graphTestHandler = new TestingGraphMockHandler();
        instance2 = TestingGraphInstanceBuilder.newGraph2(graphTestHandler);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(graphIndex.getGraph()).thenReturn(instance2.graph);
        final String e1 = instance2.edge1.getUUID();
        final String e2 = instance2.edge2.getUUID();
        when(canvas.getShape(eq(e1))).thenReturn(edge1Shape);
        when(canvas.getShape(eq(e2))).thenReturn(edge2Shape);
        when(edge1Shape.getShapeView()).thenReturn(edge1ShapeView);
        when(edge2Shape.getShapeView()).thenReturn(edge2ShapeView);

        controlPoint1 = ControlPoint.build(0, 0);
        controlPointList = Arrays.asList(controlPoint1);
        when(edge1Shape.getControlPoints()).thenReturn(controlPointList);
        when(edge1Shape.addControlPoints(controlPoint1)).thenReturn(controlPointList);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testApplyConnections() {
        final Shape startNodeShape = mock(Shape.class);
        final ShapeView startNodeShapeView = mock(ShapeView.class);
        final Shape intermNodeShape = mock(Shape.class);
        final ShapeView intermNodeShapeView = mock(ShapeView.class);
        final EdgeShape edge1EdgeShape = mock(EdgeShape.class);
        when(canvas.getShape(eq(instance2.startNode.getUUID()))).thenReturn(startNodeShape);
        when(canvas.getShape(eq(instance2.intermNode.getUUID()))).thenReturn(intermNodeShape);
        when(canvas.getShape(eq(instance2.edge1.getUUID()))).thenReturn(edge1EdgeShape);
        when(startNodeShape.getShapeView()).thenReturn(startNodeShapeView);
        when(intermNodeShape.getShapeView()).thenReturn(intermNodeShapeView);
        when(edge1EdgeShape.getShapeView()).thenReturn(edge1ShapeView);
        ShapeUtils.applyConnections(instance2.edge1,
                                    canvasHandler,
                                    MutationContext.STATIC);
        verify(edge1EdgeShape,
               times(1)).applyConnections(eq(instance2.edge1),
                                          eq(startNodeShapeView),
                                          eq(intermNodeShapeView),
                                          eq(MutationContext.STATIC));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMoveViewConnectorsToTop() {
        ShapeUtils.moveViewConnectorsToTop(canvasHandler,
                                           instance2.parentNode);
        verify(edge1ShapeView,
               times(1)).moveToTop();
        verify(edge2ShapeView,
               times(1)).moveToTop();
    }

    @Test
    public void testAddControlPoints() {
        List<ControlPoint> addedControlPoints = ShapeUtils.addControlPoints(instance2.edge1, canvasHandler, controlPoint1);
        verify(edge1Shape).addControlPoints(controlPoint1);
        assertEquals(addedControlPoints, controlPointList);
    }

    @Test
    public void testRemoveControlPoints() {
        ShapeUtils.removeControlPoints(instance2.edge1, canvasHandler, controlPoint1);
        verify(edge1Shape).removeControlPoints(controlPoint1);
    }

    @Test
    public void testGetControlPoints() {
        List<ControlPoint> controlPoints = ShapeUtils.getControlPoints(instance2.edge1, canvasHandler);
        verify(edge1Shape).getControlPoints();
        assertEquals(controlPoints, controlPointList);
    }
}
