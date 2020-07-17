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

package org.kie.workbench.common.dmn.client.session;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.session.impl.ManagedSession;
import org.kie.workbench.common.stunner.core.client.session.impl.SessionLoader;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@DMNEditor
@Dependent
public class DMNManagedSession extends ManagedSession {

    @Inject
    public DMNManagedSession(final DefinitionUtils definitionUtils,
                             final SessionLoader sessionLoader,
                             final ManagedInstance<AbstractCanvas> canvasInstances,
                             final @DMNEditor ManagedInstance<AbstractCanvasHandler> canvasHandlerInstances,
                             final ManagedInstance<CanvasControl<AbstractCanvas>> canvasControlInstances,
                             final ManagedInstance<CanvasControl<AbstractCanvasHandler>> canvasHandlerControlInstances) {
        super(definitionUtils, sessionLoader, canvasInstances, canvasHandlerInstances, canvasControlInstances, canvasHandlerControlInstances);
    }
}
