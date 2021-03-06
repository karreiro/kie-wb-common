/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.projecteditor.client.menu;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.projecteditor.client.validation.ProjectNameValidator;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.widgets.client.resources.i18n.ToolsMenuConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;

@ApplicationScoped
public class ProjectMenu {

    @Inject
    private PlaceManager placeManager;

    @Inject
    protected Caller<KieProjectService> projectService;

    @Inject
    protected Caller<RenameService> renameService;

    @Inject
    protected Caller<DeleteService> deleteService;

    @Inject
    protected Caller<CopyService> copyService;

    @Inject
    protected CopyPopUpPresenter copyPopUpPresenter;

    @Inject
    protected RenamePopUpPresenter renamePopUpPresenter;

    @Inject
    protected DeletePopUpPresenter deletePopUpPresenter;

    @Inject
    protected ProjectContext context;

    @Inject
    protected ProjectNameValidator projectNameValidator;

    private MenuItem projectScreen = MenuFactory.newSimpleItem( ToolsMenuConstants.INSTANCE.ProjectEditor() ).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    placeManager.goTo( "projectScreen" );
                }
            } ).endMenu().build().getItems().get( 0 );

    private MenuItem projectStructureScreen = MenuFactory.newSimpleItem( ToolsMenuConstants.INSTANCE.RepositoryStructure() ).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    placeManager.goTo( "repositoryStructureScreen" );
                }
            } ).endMenu().build().getItems().get( 0 );

    private MenuItem copyProject = MenuFactory.newSimpleItem( ToolsMenuConstants.INSTANCE.CopyProject() ).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    final Path path = context.getActiveProject().getRootPath();
                    copyPopUpPresenter.show( path,
                                             projectNameValidator,
                                             new CommandWithFileNameAndCommitMessage() {
                                                 @Override
                                                 public void execute( FileNameAndCommitMessage payload ) {
                                                     copyService.call( new RemoteCallback<Void>() {

                                                         @Override
                                                         public void callback( final Void o ) {
                                                             copyPopUpPresenter.getView().hide();
                                                         }
                                                     }, new ErrorCallback<Void>() {

                                                         @Override
                                                         public boolean error( final Void o,
                                                                               final Throwable throwable ) {
                                                             copyPopUpPresenter.getView().hide();
                                                             return false;
                                                         }
                                                     } ).copy( path,
                                                               payload.getNewFileName(),
                                                               payload.getCommitMessage() );
                                                 }
                                             } );
                }
            } ).endMenu().build().getItems().get( 0 );

    private MenuItem renameProject = MenuFactory.newSimpleItem( ToolsMenuConstants.INSTANCE.RenameProject() ).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    final Path path = context.getActiveProject().getRootPath();
                    renamePopUpPresenter.show( path,
                                               projectNameValidator,
                                               new CommandWithFileNameAndCommitMessage() {
                                                   @Override
                                                   public void execute( FileNameAndCommitMessage payload ) {
                                                       renameService.call( new RemoteCallback<Void>() {

                                                           @Override
                                                           public void callback( final Void o ) {
                                                               renamePopUpPresenter.getView().hide();
                                                           }
                                                       }, new ErrorCallback<Void>() {

                                                           @Override
                                                           public boolean error( final Void o,
                                                                                 final Throwable throwable ) {
                                                               renamePopUpPresenter.getView().hide();
                                                               return false;
                                                           }
                                                       } ).rename( path,
                                                                   payload.getNewFileName(),
                                                                   payload.getCommitMessage() );
                                                   }
                                               } );

                }
            } ).endMenu().build().getItems().get( 0 );

    private MenuItem removeProject = MenuFactory.newSimpleItem( ToolsMenuConstants.INSTANCE.RemoveProject() ).respondsWith(
            new Command() {
                @Override
                public void execute() {
                    deletePopUpPresenter.show( new ParameterizedCommand<String>() {
                        @Override
                        public void execute( String payload ) {
                            deleteService.call().delete(
                                    context.getActiveProject().getRootPath(),
                                    payload );
                        }
                    } );

                }
            } ).endMenu().build().getItems().get( 0 );

    public List<MenuItem> getMenuItems() {
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();

        menuItems.add( projectScreen );

        menuItems.add( projectStructureScreen );

//        menuItems.add(removeProject);
//        menuItems.add(renameProject);
//        menuItems.add(copyProject);

        return menuItems;
    }

    public void onProjectContextChanged( @Observes final ProjectContextChangeEvent event ) {
        enableToolsMenuItems( (KieProject) event.getProject() );
    }

    private void enableToolsMenuItems( final KieProject project ) {
        final boolean enabled = ( project != null );
        projectScreen.setEnabled( enabled );
    }

}
