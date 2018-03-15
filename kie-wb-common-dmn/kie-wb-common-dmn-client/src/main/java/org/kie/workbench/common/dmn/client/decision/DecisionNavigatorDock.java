/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.decision;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
public class DecisionNavigatorDock {

    private static final int DOCK_SIZE = 400;

    private static final String DOCK_LABEL = "Decision Navigator";

    private final UberfireDocks uberfireDocks;

    private UberfireDock uberfireDock;

    private DecisionNavigatorPresenter decisionNavigatorPresenter;

    private boolean isOpened = false;

    private String associatedPerspective;

    @Inject
    public DecisionNavigatorDock(final UberfireDocks uberfireDocks,
                                 final DecisionNavigatorPresenter decisionNavigatorPresenter) {
        this.uberfireDocks = uberfireDocks;
        this.decisionNavigatorPresenter = decisionNavigatorPresenter;
    }

    public void init(final String associatedPerspective) {
        this.associatedPerspective = associatedPerspective;
        this.uberfireDock = makeUberfireDock();
    }

    public void setupContent(final AbstractCanvasHandler handler) {
        decisionNavigatorPresenter.setHandler(handler);
    }

    public void resetContent() {
        decisionNavigatorPresenter.removeAllElements();
    }

    public void open() {

        if (isOpened()) {
            return;
        }

        isOpened = true;
        uberfireDocks.add(uberfireDock);
        uberfireDocks.show(position(), perspective());
        uberfireDocks.open(uberfireDock);
    }

    public void close() {

        if (!isOpened()) {
            return;
        }

        isOpened = false;
        uberfireDocks.close(uberfireDock);
        uberfireDocks.remove(uberfireDock);
    }

    private boolean isOpened() {
        return isOpened;
    }

    private UberfireDock makeUberfireDock() {

        final UberfireDock uberfireDock = new UberfireDock(position(), icon(), placeRequest(), perspective());

        return uberfireDock.withSize(DOCK_SIZE).withLabel(DOCK_LABEL);
    }

    private String perspective() {
        return associatedPerspective;
    }

    private String icon() {
        return IconType.MAP.toString();
    }

    private UberfireDockPosition position() {
        return UberfireDockPosition.WEST;
    }

    private DefaultPlaceRequest placeRequest() {
        return new DefaultPlaceRequest(DecisionNavigatorPresenter.SCREEN_ID);
    }
}
