/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.marshaller.common;

import org.junit.Test;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDiagram;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.getComposedId;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.getEdgeId;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.getPrefixedId;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.getRawId;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.getShapeId;
import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.uniqueId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IdUtilsTest {

    @Test
    public void testGetPrefixedId() {
        assertEquals("1111#2222", getPrefixedId("1111", "2222"));
        assertEquals("2222", getPrefixedId("", "2222"));
        assertEquals("2222", getPrefixedId(null, "2222"));
    }

    @Test
    public void testGetRawId() {
        assertEquals("2222", getRawId("1111#2222"));
        assertEquals("2222", getRawId("#2222"));
        assertEquals("2222", getRawId("2222"));
    }

    @Test
    public void testUniqueId() {
        assertNotEquals(uniqueId(), uniqueId());
    }

    @Test
    public void testGetComposedId() {
        assertEquals("dmnshape-page-1-_1111-2222", getComposedId("dmnshape", "page 1", "_1111-2222"));
        assertEquals("dmnshape-page-1-_1111-2222", getComposedId("dmnshape", "page   1   ", "_1111-2222"));
        assertEquals("dmnshape-_1111-2222", getComposedId("dmnshape", "", "_1111-2222"));
        assertEquals("dmnshape-_1111-2222", getComposedId("dmnshape", "_1111-2222"));
    }

    @Test
    public void testGetShapeId() {
        final JSIDMNDiagram diagram = mock(JSIDMNDiagram.class);
        when(diagram.getName()).thenReturn("DRG");
        assertEquals("dmnshape-drg-_1111-2222", getShapeId(diagram, "_1111-2222"));
    }

    @Test
    public void testGetEdgeId() {
        final JSIDMNDiagram diagram = mock(JSIDMNDiagram.class);
        when(diagram.getName()).thenReturn("DRG");
        assertEquals("dmnedge-drg-_1111-2222", getEdgeId(diagram, "_1111-2222"));
    }

    @Test
    public void testGetShapeIdWhenDiagramNameIsNull() {
        final JSIDMNDiagram diagram = mock(JSIDMNDiagram.class);
        assertEquals("dmnshape-_1111-2222", getShapeId(diagram, "_1111-2222"));
    }

    @Test
    public void testGetEdgeIdWhenDiagramNameIsNull() {
        final JSIDMNDiagram diagram = mock(JSIDMNDiagram.class);
        assertEquals("dmnedge-_1111-2222", getEdgeId(diagram, "_1111-2222"));
    }
}
