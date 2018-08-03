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

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import com.google.gwt.user.client.Timer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.util.TimerUtils;
import org.uberfire.backend.vfs.Path;

public abstract class AbstractExportSessionCommand extends AbstractClientSessionCommand<AbstractSession<AbstractCanvas, AbstractCanvasHandler>> {

    protected TimerUtils timer;

    public AbstractExportSessionCommand(final boolean enabled) {
        super(enabled);
        this.timer = new TimerUtils();
    }

    protected abstract void export(final String fileName);

    @Override
    public boolean accepts(final ClientSession session) {
        return true;
    }

    @Override
    public <T> void execute(final Callback<T> callback) {
        //prevents to render selection on canvas
        if(getSession() instanceof EditorSession){
            ((EditorSession) getSession()).getSelectionControl().clearSelection();
        }

        //This is a workaround to overcome the animations executed on canvas when clear selection
        //FIXME: remove the delay, handle this on the proper way, i.e perform the action on a static way
        timer.executeWithDelay(() -> {
            final String fileName = getFileName();
            export(fileName);
            callback.onSuccess();
        }, 300);
    }

    private void executeWithDelay(Runnable executeFunction, int delayMillis) {
        new Timer() {
            public void run() {
                executeFunction.run();
            }
        }.schedule(delayMillis);
    }

    private String getFileName() {
        final Path path = getSession().getCanvasHandler().getDiagram().getMetadata().getPath();
        return null != path ? path.getFileName() : getSession().getCanvasHandler().getDiagram().getGraph().getUUID();
    }

    protected void setTimer(TimerUtils timer) {
        this.timer = timer;
    }
}
