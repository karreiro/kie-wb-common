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

package org.kie.workbench.common.dmn.client.session;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.CanvasInPlaceTextEditorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.SingleLineTextEditorBox;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.ElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.Observer;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.containment.ContainmentAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.docking.DockingAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.drag.LocationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.resize.ResizeControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistry;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNEditorSessionTest extends BaseDMNSessionTest<DMNEditorSession> {

    @Mock
    private RegistryFactory registryFactory;

    @Mock
    private CommandRegistry commandRegistry;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> requestCommandManager;

    @Mock
    private StunnerPreferencesRegistry stunnerPreferencesRegistry;

    @Mock
    private ResizeControl resizeControl;

    @Mock
    private ConnectionAcceptorControl connectionAcceptorControl;

    @Mock
    private ContainmentAcceptorControl containmentAcceptorControl;

    @Mock
    private DockingAcceptorControl dockingAcceptorControl;

    @Mock
    private CanvasInPlaceTextEditorControl canvasInPlaceTextEditorControl;

    @Mock
    private LocationControl locationControl;

    @Mock
    private ToolboxControl toolboxControl;

    @Mock
    private ElementBuilderControl elementBuilderControl;

    @Mock
    private NodeBuilderControl nodeBuilderControl;

    @Mock
    private EdgeBuilderControl edgeBuilderControl;

    @Mock
    private KeyboardControl keyboardControl;

    @Mock
    private ClipboardControl clipboardControl;

    @Before
    @Override
    @SuppressWarnings("unchecked")
    public void setup() {
        when(registryFactory.newCommandRegistry()).thenReturn(commandRegistry);
        super.setup();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected DMNEditorSession getSession() {
        final DMNEditorSession session = new DMNEditorSession(managedSession,
                                                              registryFactory,
                                                              canvasCommandManager,
                                                              sessionCommandManager,
                                                              requestCommandManager,
                                                              stunnerPreferencesRegistry);
        session.constructInstance();
        return session;
    }

    @Override
    protected Map<CanvasControl, Class> getCanvasControlRegistrations() {
        final HashMap<CanvasControl, Class> canvasControls = new HashMap<>();
        canvasControls.put(keyboardControl, KeyboardControl.class);
        canvasControls.put(clipboardControl, ClipboardControl.class);
        return canvasControls;
    }

    @Override
    protected Map<CanvasControl, Class> getCanvasHandlerControlRegistrations() {
        final HashMap<CanvasControl, Class> canvasHandlerControls = new HashMap<>();
        canvasHandlerControls.put(resizeControl, ResizeControl.class);
        canvasHandlerControls.put(connectionAcceptorControl, ConnectionAcceptorControl.class);
        canvasHandlerControls.put(containmentAcceptorControl, ContainmentAcceptorControl.class);
        canvasHandlerControls.put(dockingAcceptorControl, DockingAcceptorControl.class);
        canvasHandlerControls.put(canvasInPlaceTextEditorControl, CanvasInPlaceTextEditorControl.class);
        canvasHandlerControls.put(locationControl, LocationControl.class);
        canvasHandlerControls.put(toolboxControl, ToolboxControl.class);
        canvasHandlerControls.put(elementBuilderControl, ElementBuilderControl.class);
        canvasHandlerControls.put(nodeBuilderControl, NodeBuilderControl.class);
        canvasHandlerControls.put(edgeBuilderControl, EdgeBuilderControl.class);
        return canvasHandlerControls;
    }

    @Override
    protected void assertInitQualifiers() {
        super.assertInitQualifiers();
        verify(managedSession).registerCanvasHandlerControl(eq(CanvasInPlaceTextEditorControl.class), eq(SingleLineTextEditorBox.class));
        verify(managedSession).registerCanvasHandlerControl(eq(ElementBuilderControl.class), eq(Observer.class));
    }
}
