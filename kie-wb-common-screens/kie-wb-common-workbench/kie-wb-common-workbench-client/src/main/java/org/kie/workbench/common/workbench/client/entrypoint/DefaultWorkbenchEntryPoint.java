/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.workbench.client.entrypoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.services.shared.service.PlaceManagerActivityService;
import org.kie.workbench.common.widgets.client.resources.RoundedCornersResource;
import org.uberfire.client.mvp.ActivityBeansCache;

public abstract class DefaultWorkbenchEntryPoint {

    protected Caller<AppConfigService> appConfigService;

    protected Caller<PlaceManagerActivityService> pmas;

    protected ActivityBeansCache activityBeansCache;

    @Inject
    public DefaultWorkbenchEntryPoint( Caller<AppConfigService> appConfigService,
                                       Caller<PlaceManagerActivityService> pmas,
                                       ActivityBeansCache activityBeansCache ) {
        this.appConfigService = appConfigService;
        this.pmas = pmas;
        this.activityBeansCache = activityBeansCache;
    }

    protected abstract void setupMenu();

    @AfterInitialization
    public void startDefaultWorkbench() {
        initializeWorkbench();

        pmas.call().initActivities( activityBeansCache.getActivitiesById() );
    }

    void loadPreferences() {
        appConfigService.call( new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback( final Map<String, String> response ) {
                ApplicationPreferences.setUp( response );
                setupMenu();
            }
        } ).loadPreferences();
    }

    void loadStyles() {
        RoundedCornersResource.INSTANCE.roundCornersCss().ensureInjected();
    }

    public void hideLoadingPopup() {
        @SuppressWarnings("GwtToHtmlReferences")
        final Element e = RootPanel.get( "loading" ).getElement();

        new Animation() {
            @Override
            protected void onUpdate( double progress ) {
                e.getStyle().setOpacity( 1.0 - progress );
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility( Style.Visibility.HIDDEN );
            }
        }.run( 500 );
    }

    private void initializeWorkbench() {
        loadPreferences();
        loadStyles();
        hideLoadingPopup();
    }
}
