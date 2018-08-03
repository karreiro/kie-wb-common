/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.events;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Unmarshalling;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBase;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller;
import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller.OLD;

@RunWith(Parameterized.class)
public abstract class EndEvent<T extends BaseEndEvent> extends BPMNDiagramMarshallerBase {

    static final String EMPTY_VALUE = "";
    static final boolean HAS_INCOME_EDGE = true;
    static final boolean HAS_NO_INCOME_EDGE = false;

    protected DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> marshaller = null;

    @Parameterized.Parameters
    public static List<Object[]> marshallers() {
        return Arrays.asList(new Object[][]{
                // New (un)marshaller is disabled for now due to found incompleteness
                {OLD}//, {NEW}
        });
    }

    EndEvent(Marshaller marshallerType) {
        super.init();
        switch (marshallerType) {
            case OLD:
                marshaller = oldMarshaller;
                break;
            case NEW:
                marshaller = newMarshaller;
                break;
        }
    }

    @Ignore
    @Test
    public void testMigration() throws Exception {
        Diagram<Graph, Metadata> oldDiagram = Unmarshalling.unmarshall(oldMarshaller, getBpmnEndEventFilePath());
        Diagram<Graph, Metadata> newDiagram = Unmarshalling.unmarshall(newMarshaller, getBpmnEndEventFilePath());

        // Doesn't work, due to old Marshaller and new Marshaller have different BPMNDefinitionSet uuids
        // assertEquals(oldDiagram.getGraph(), newDiagram.getGraph());

        // Let's check nodes only.
        assertDiagramEquals(oldDiagram, newDiagram, getBpmnEndEventFilePath());
    }

    @Test
    public void testMarshallTopLevelEventFilledProperties() throws Exception {
        checkEventMarshalling(getFilledTopLevelEventId(), HAS_NO_INCOME_EDGE);
    }

    @Test
    public void testMarshallTopLevelEventEmptyProperties() throws Exception {
        checkEventMarshalling(getEmptyTopLevelEventId(), HAS_NO_INCOME_EDGE);
    }

    @Test
    public void testMarshallSubprocessLevelEventFilledProperties() throws Exception {
        checkEventMarshalling(getFilledSubprocessLevelEventId(), HAS_NO_INCOME_EDGE);
    }

    @Test
    public void testMarshallSubprocessLevelEventEmptyProperties() throws Exception {
        checkEventMarshalling(getEmptySubprocessLevelEventId(), HAS_NO_INCOME_EDGE);
    }

    @Test
    public void testMarshallTopLevelEventWithIncomeFilledProperties() throws Exception {
        checkEventMarshalling(getFilledTopLevelEventWithIncomeId(), HAS_INCOME_EDGE);
    }

    @Test
    public void testMarshallTopLevelventWithIncomeEmptyProperties() throws Exception {
        checkEventMarshalling(getEmptyTopLevelEventWithIncomeId(), HAS_INCOME_EDGE);
    }

    @Test
    public void testMarshallSubprocessLevelEventWithIncomeFilledProperties() throws Exception {
        checkEventMarshalling(getFilledSubprocessLevelEventWithIncomeId(), HAS_INCOME_EDGE);
    }

    @Test
    public void testMarshallSubprocessLevelEventWithIncomeEmptyProperties() throws Exception {
        checkEventMarshalling(getEmptySubprocessLevelEventWithIncomeId(), HAS_INCOME_EDGE);
    }

    public abstract void testUnmarshallTopLevelEventFilledProperties() throws Exception;

    public abstract void testUnmarshallTopLevelEmptyEventProperties() throws Exception;

    public abstract void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception;

    public abstract void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception;

    public abstract void testUnmarshallTopLevelEventWithIncomeFilledProperties() throws Exception;

    public abstract void testUnmarshallTopLevelEventWithIncomeEmptyProperties() throws Exception;

    public abstract void testUnmarshallSubprocessLevelEventWithIncomeEmptyProperties() throws Exception;

    public abstract void testUnmarshallSubprocessLevelEventWithIncomeFilledProperties() throws Exception;

    abstract String getBpmnEndEventFilePath();

    abstract Class<T> getEndEventType();

    abstract String getFilledTopLevelEventId();

    abstract String getEmptyTopLevelEventId();

    abstract String getFilledSubprocessLevelEventId();

    abstract String getEmptySubprocessLevelEventId();

    abstract String getFilledTopLevelEventWithIncomeId();

    abstract String getEmptyTopLevelEventWithIncomeId();

    abstract String getFilledSubprocessLevelEventWithIncomeId();

    abstract String getEmptySubprocessLevelEventWithIncomeId();

    private void assertNodesEqualsAfterMarshalling(Diagram<Graph, Metadata> before, Diagram<Graph, Metadata> after, String nodeId, boolean hasIncomeEdge) {
        T nodeBeforeMarshalling = getEndNodeById(before, nodeId, hasIncomeEdge);
        T nodeAfterMarshalling = getEndNodeById(after, nodeId, hasIncomeEdge);
        assertEquals(nodeBeforeMarshalling, nodeAfterMarshalling);
    }

    @SuppressWarnings("unchecked")
    T getEndNodeById(Diagram<Graph, Metadata> diagram, String id, boolean hasIncomeEdge) {
        Node<? extends Definition, ?> node = diagram.getGraph().getNode(id);
        assertNotNull(node);

        int incomeEdges = hasIncomeEdge ? 2 : 1;
        assertEquals(incomeEdges, node.getInEdges().size());

        assertEquals(0, node.getOutEdges().size());
        return getEndEventType().cast(node.getContent().getDefinition());
    }

    @SuppressWarnings("unchecked")
    void checkEventMarshalling(String nodeID, boolean hasIncomeEdge) throws Exception {
        Diagram<Graph, Metadata> initialDiagram = unmarshall(marshaller, getBpmnEndEventFilePath());
        final int AMOUNT_OF_NODES_IN_DIAGRAM = getNodes(initialDiagram).size();
        String resultXml = marshaller.marshall(initialDiagram);

        Diagram<Graph, Metadata> marshalledDiagram = unmarshall(marshaller, getStream(resultXml));
        assertDiagram(marshalledDiagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        assertNodesEqualsAfterMarshalling(initialDiagram, marshalledDiagram, nodeID, hasIncomeEdge);
    }

    void assertGeneralSet(BPMNGeneralSet generalSet, String nodeName, String documentation) {
        assertNotNull(generalSet);
        assertNotNull(generalSet.getName());
        assertNotNull(generalSet.getDocumentation());
        assertEquals(nodeName, generalSet.getName().getValue());
        assertEquals(documentation, generalSet.getDocumentation().getValue());
    }

    void assertDataIOSet(DataIOSet dataIOSet, String value) {
        assertNotNull(dataIOSet);
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertNotNull(assignmentsInfo);
        assertEquals(value, assignmentsInfo.getValue());
    }
}
