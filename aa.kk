commit cf1ec283f40f475822e69ac5c0fb964967d0f0d6
Author: Guilherme Carreiro <karreiro@gmail.com>
Date:   Wed Jan 24 22:09:53 2018 -0200

    ..

diff --git a/kie-wb-common-forms/kie-wb-common-forms-editor/kie-wb-common-forms-editor-client/src/main/java/org/kie/workbench/common/forms/editor/client/editor/FormEditorPresenter.java b/kie-wb-common-forms/kie-wb-common-forms-editor/kie-wb-common-forms-editor-client/src/main/java/org/kie/workbench/common/forms/editor/client/editor/FormEditorPresenter.java
index a0f7b97a8..af2fb871e 100644
--- a/kie-wb-common-forms/kie-wb-common-forms-editor/kie-wb-common-forms-editor-client/src/main/java/org/kie/workbench/common/forms/editor/client/editor/FormEditorPresenter.java
+++ b/kie-wb-common-forms/kie-wb-common-forms-editor/kie-wb-common-forms-editor-client/src/main/java/org/kie/workbench/common/forms/editor/client/editor/FormEditorPresenter.java
@@ -19,6 +19,7 @@ package org.kie.workbench.common.forms.editor.client.editor;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Iterator;
+import java.util.function.Supplier;
 
 import javax.annotation.PreDestroy;
 import javax.enterprise.context.Dependent;
@@ -74,7 +75,7 @@ import org.uberfire.workbench.type.FileNameUtil;
 
 @Dependent
 @WorkbenchEditor(identifier = "FormEditor", supportedTypes = {FormDefinitionResourceType.class})
-public class FormEditorPresenter extends KieEditor {
+public class FormEditorPresenter extends KieEditor<FormModelerContent> {
 
     @Inject
     protected LayoutEditor layoutEditor;
@@ -137,6 +138,11 @@ public class FormEditorPresenter extends KieEditor {
                            getNoSuchFileExceptionErrorCallback()).loadContent(versionRecordManager.getCurrentPath());
     }
 
+    @Override
+    protected Supplier<FormModelerContent> getContentSupplier() {
+        return () -> editorHelper.getContent();
+    }
+
     @Override
     protected void save(String commitMessage) {
         synchronizeFormLayout();
diff --git a/kie-wb-common-forms/kie-wb-common-forms-editor/kie-wb-common-forms-editor-client/src/test/java/org/kie/workbench/common/forms/editor/client/editor/FormEditorPresenterAbstractTest.java b/kie-wb-common-forms/kie-wb-common-forms-editor/kie-wb-common-forms-editor-client/src/test/java/org/kie/workbench/common/forms/editor/client/editor/FormEditorPresenterAbstractTest.java
index 51e25afee..283a7b9e8 100644
--- a/kie-wb-common-forms/kie-wb-common-forms-editor/kie-wb-common-forms-editor-client/src/test/java/org/kie/workbench/common/forms/editor/client/editor/FormEditorPresenterAbstractTest.java
+++ b/kie-wb-common-forms/kie-wb-common-forms-editor/kie-wb-common-forms-editor-client/src/test/java/org/kie/workbench/common/forms/editor/client/editor/FormEditorPresenterAbstractTest.java
@@ -23,6 +23,7 @@ import java.util.List;
 import com.google.gwtmockito.GwtMock;
 import org.guvnor.common.services.project.client.security.ProjectController;
 import org.guvnor.common.services.project.context.ProjectContext;
+import org.guvnor.common.services.shared.metadata.model.Metadata;
 import org.guvnor.common.services.shared.metadata.model.Overview;
 import org.jboss.errai.common.client.dom.HTMLElement;
 import org.jboss.errai.ioc.client.api.ManagedInstance;
@@ -129,7 +130,7 @@ public class FormEditorPresenterAbstractTest {
     protected EventSourceMock<NotificationEvent> notificationEvent;
 
     @Mock
-    protected FileMenuBuilder menuBuilderMock;
+    protected FileMenuBuilder<FormModelerContent, Metadata> menuBuilderMock;
 
     @Mock
     protected ProjectController projectController;
diff --git a/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-api/src/main/java/org/kie/workbench/common/screens/datamodeller/service/PersistenceDescriptorEditorService.java b/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-api/src/main/java/org/kie/workbench/common/screens/datamodeller/service/PersistenceDescriptorEditorService.java
index d6fb0da7a..91fd341db 100644
--- a/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-api/src/main/java/org/kie/workbench/common/screens/datamodeller/service/PersistenceDescriptorEditorService.java
+++ b/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-api/src/main/java/org/kie/workbench/common/screens/datamodeller/service/PersistenceDescriptorEditorService.java
@@ -17,9 +17,11 @@
 package org.kie.workbench.common.screens.datamodeller.service;
 
 import org.guvnor.common.services.shared.file.SupportsUpdate;
+import org.guvnor.common.services.shared.metadata.model.Metadata;
 import org.jboss.errai.bus.server.annotations.Remote;
 import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorEditorContent;
 import org.uberfire.backend.vfs.Path;
+import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
 
 @Remote
 public interface PersistenceDescriptorEditorService extends SupportsUpdate<PersistenceDescriptorEditorContent> {
diff --git a/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-backend/src/main/java/org/kie/workbench/common/screens/datamodeller/backend/server/PersistenceDescriptorEditorServiceImpl.java b/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-backend/src/main/java/org/kie/workbench/common/screens/datamodeller/backend/server/PersistenceDescriptorEditorServiceImpl.java
index 9297f09a9..c8583f8ee 100644
--- a/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-backend/src/main/java/org/kie/workbench/common/screens/datamodeller/backend/server/PersistenceDescriptorEditorServiceImpl.java
+++ b/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-backend/src/main/java/org/kie/workbench/common/screens/datamodeller/backend/server/PersistenceDescriptorEditorServiceImpl.java
@@ -99,5 +99,4 @@ public class PersistenceDescriptorEditorServiceImpl
             return new Pair<Path, Boolean>( Paths.convert( Paths.convert( path ) ), false );
         }
     }
-
 }
diff --git a/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-client/pom.xml b/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-client/pom.xml
index 5d65eb024..89bbac9ca 100644
--- a/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-client/pom.xml
+++ b/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-client/pom.xml
@@ -87,6 +87,10 @@
       <groupId>org.uberfire</groupId>
       <artifactId>uberfire-security-api</artifactId>
     </dependency>
+    <dependency>
+      <groupId>org.uberfire</groupId>
+      <artifactId>uberfire-project-client</artifactId>
+    </dependency>
     <dependency>
       <groupId>org.uberfire</groupId>
       <artifactId>uberfire-client-api</artifactId>
diff --git a/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-client/src/main/java/org/kie/workbench/common/screens/datamodeller/client/DataModelerScreenPresenter.java b/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-client/src/main/java/org/kie/workbench/common/screens/datamodeller/client/DataModelerScreenPresenter.java
index ee04724d9..e8bf7b5f5 100644
--- a/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-client/src/main/java/org/kie/workbench/common/screens/datamodeller/client/DataModelerScreenPresenter.java
+++ b/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-client/src/main/java/org/kie/workbench/common/screens/datamodeller/client/DataModelerScreenPresenter.java
@@ -17,6 +17,8 @@
 package org.kie.workbench.common.screens.datamodeller.client;
 
 import java.util.List;
+import java.util.function.Supplier;
+
 import javax.enterprise.context.Dependent;
 import javax.enterprise.event.Event;
 import javax.enterprise.event.Observes;
@@ -96,11 +98,9 @@ import org.uberfire.workbench.events.NotificationEvent;
 import org.uberfire.workbench.model.menu.Menus;
 
 @Dependent
-@WorkbenchEditor(identifier = "DataModelerEditor",
-        supportedTypes = {JavaResourceType.class},
-        priority = Integer.MAX_VALUE)
+@WorkbenchEditor(identifier = "DataModelerEditor", supportedTypes = {JavaResourceType.class}, priority = 2)
 public class DataModelerScreenPresenter
-        extends KieEditor {
+        extends KieEditor<DataObject> {
 
     public interface DataModelerScreenView
             extends
@@ -770,6 +770,11 @@ public class DataModelerScreenPresenter
                 loadTypesInfo);
     }
 
+    @Override
+    protected Supplier<DataObject> getContentSupplier() {
+        return () -> null;
+    }
+
     private RemoteCallback<EditorModelContent> getLoadModelSuccessCallback() {
         return new RemoteCallback<EditorModelContent>() {
 
diff --git a/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-client/src/main/java/org/kie/workbench/common/screens/datamodeller/client/PersistenceDescriptorEditorPresenter.java b/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-client/src/main/java/org/kie/workbench/common/screens/datamodeller/client/PersistenceDescriptorEditorPresenter.java
index 017769c6b..36511b5a3 100644
--- a/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-client/src/main/java/org/kie/workbench/common/screens/datamodeller/client/PersistenceDescriptorEditorPresenter.java
+++ b/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-client/src/main/java/org/kie/workbench/common/screens/datamodeller/client/PersistenceDescriptorEditorPresenter.java
@@ -18,10 +18,13 @@ package org.kie.workbench.common.screens.datamodeller.client;
 
 import java.util.ArrayList;
 import java.util.List;
+import java.util.function.Supplier;
+
 import javax.enterprise.context.Dependent;
 import javax.inject.Inject;
 
 import com.google.gwt.user.client.ui.IsWidget;
+import org.guvnor.common.services.shared.metadata.model.Metadata;
 import org.guvnor.common.services.shared.validation.model.ValidationMessage;
 import org.jboss.errai.common.client.api.Caller;
 import org.jboss.errai.common.client.api.RemoteCallback;
@@ -69,7 +72,7 @@ import org.uberfire.workbench.model.menu.Menus;
         supportedTypes = { PersistenceDescriptorType.class },
         priority = Integer.MAX_VALUE )
 public class PersistenceDescriptorEditorPresenter
-        extends KieEditor
+        extends KieEditor<PersistenceDescriptorEditorContent>
         implements PersistenceDescriptorEditorView.Presenter {
 
     private PersistenceDescriptorEditorView view;
@@ -158,6 +161,11 @@ public class PersistenceDescriptorEditorPresenter
                 createIfNotExists );
     }
 
+    @Override
+    protected Supplier<PersistenceDescriptorEditorContent> getContentSupplier() {
+        return () -> null;
+    }
+
     @OnMayClose
     public boolean onMayClose() {
         if ( isDirty( getContent().hashCode() ) ) {
diff --git a/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-client/src/test/java/org/kie/workbench/common/screens/datamodeller/client/DataModelerScreenPresenterTestBase.java b/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-client/src/test/java/org/kie/workbench/common/screens/datamodeller/client/DataModelerScreenPresenterTestBase.java
index e3b6cd2c6..5add6c836 100644
--- a/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-client/src/test/java/org/kie/workbench/common/screens/datamodeller/client/DataModelerScreenPresenterTestBase.java
+++ b/kie-wb-common-screens/kie-wb-common-data-modeller/kie-wb-common-data-modeller-client/src/test/java/org/kie/workbench/common/screens/datamodeller/client/DataModelerScreenPresenterTestBase.java
@@ -28,6 +28,7 @@ import com.google.gwtmockito.GwtMockitoTestRunner;
 import org.guvnor.common.services.project.client.security.ProjectController;
 import org.guvnor.common.services.project.context.ProjectContext;
 import org.guvnor.common.services.shared.message.Level;
+import org.guvnor.common.services.shared.metadata.model.Metadata;
 import org.guvnor.common.services.shared.metadata.model.Overview;
 import org.guvnor.messageconsole.events.PublishBatchMessagesEvent;
 import org.guvnor.messageconsole.events.UnpublishMessagesEvent;
@@ -54,6 +55,7 @@ import org.kie.workbench.common.services.datamodeller.util.DriverUtils;
 import org.kie.workbench.common.services.refactoring.client.usages.ShowAssetUsagesDisplayer;
 import org.kie.workbench.common.services.shared.project.KieProject;
 import org.kie.workbench.common.services.shared.validation.ValidationService;
+import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
 import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
 import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
 import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
@@ -155,7 +157,7 @@ public abstract class DataModelerScreenPresenterTestBase {
 
     @Spy
     @InjectMocks
-    protected FileMenuBuilderImpl fileMenuBuilder;
+    protected FileMenuBuilder<DataObject, Metadata> fileMenuBuilder;
 
     @Mock
     protected ProjectController projectController;
diff --git a/kie-wb-common-screens/kie-wb-common-datasource-mgmt/kie-wb-common-datasource-mgmt-client/src/main/java/org/kie/workbench/common/screens/datasource/management/client/editor/datasource/DataSourceDefEditor.java b/kie-wb-common-screens/kie-wb-common-datasource-mgmt/kie-wb-common-datasource-mgmt-client/src/main/java/org/kie/workbench/common/screens/datasource/management/client/editor/datasource/DataSourceDefEditor.java
index 8e57cbe99..8e4db02c9 100644
--- a/kie-wb-common-screens/kie-wb-common-datasource-mgmt/kie-wb-common-datasource-mgmt-client/src/main/java/org/kie/workbench/common/screens/datasource/management/client/editor/datasource/DataSourceDefEditor.java
+++ b/kie-wb-common-screens/kie-wb-common-datasource-mgmt/kie-wb-common-datasource-mgmt-client/src/main/java/org/kie/workbench/common/screens/datasource/management/client/editor/datasource/DataSourceDefEditor.java
@@ -16,11 +16,14 @@
 
 package org.kie.workbench.common.screens.datasource.management.client.editor.datasource;
 
+import java.util.function.Supplier;
+
 import javax.annotation.PostConstruct;
 import javax.enterprise.context.Dependent;
 import javax.inject.Inject;
 
 import com.google.gwt.user.client.ui.IsWidget;
+import org.guvnor.common.services.shared.metadata.model.Metadata;
 import org.gwtbootstrap3.client.ui.constants.ButtonType;
 import org.jboss.errai.common.client.api.Caller;
 import org.jboss.errai.common.client.api.RemoteCallback;
@@ -56,7 +59,7 @@ import org.uberfire.workbench.events.NotificationEvent;
 @WorkbenchEditor(identifier = "DataSourceDefEditor",
         supportedTypes = {DataSourceDefType.class})
 public class DataSourceDefEditor
-        extends BaseEditor
+        extends BaseEditor<DataSourceDefEditorContent, Metadata>
         implements DataSourceDefEditorView.Presenter {
 
     private DataSourceDefEditorView view;
@@ -159,6 +162,11 @@ public class DataSourceDefEditor
                 versionRecordManager.getCurrentPath());
     }
 
+    @Override
+    protected Supplier<DataSourceDefEditorContent> getContentSupplier() {
+        return null;
+    }
+
     @Override
     public boolean mayClose(Integer currentHash) {
         return super.mayClose(currentHash);
diff --git a/kie-wb-common-screens/kie-wb-common-datasource-mgmt/kie-wb-common-datasource-mgmt-client/src/main/java/org/kie/workbench/common/screens/datasource/management/client/editor/driver/DriverDefEditor.java b/kie-wb-common-screens/kie-wb-common-datasource-mgmt/kie-wb-common-datasource-mgmt-client/src/main/java/org/kie/workbench/common/screens/datasource/management/client/editor/driver/DriverDefEditor.java
index dec5dd990..00d67ae78 100644
--- a/kie-wb-common-screens/kie-wb-common-datasource-mgmt/kie-wb-common-datasource-mgmt-client/src/main/java/org/kie/workbench/common/screens/datasource/management/client/editor/driver/DriverDefEditor.java
+++ b/kie-wb-common-screens/kie-wb-common-datasource-mgmt/kie-wb-common-datasource-mgmt-client/src/main/java/org/kie/workbench/common/screens/datasource/management/client/editor/driver/DriverDefEditor.java
@@ -16,10 +16,13 @@
 
 package org.kie.workbench.common.screens.datasource.management.client.editor.driver;
 
+import java.util.function.Supplier;
+
 import javax.enterprise.context.Dependent;
 import javax.inject.Inject;
 
 import com.google.gwt.user.client.ui.IsWidget;
+import org.guvnor.common.services.shared.metadata.model.Metadata;
 import org.gwtbootstrap3.client.ui.constants.ButtonType;
 import org.jboss.errai.common.client.api.Caller;
 import org.jboss.errai.common.client.api.RemoteCallback;
@@ -52,7 +55,7 @@ import org.uberfire.workbench.events.NotificationEvent;
 @WorkbenchEditor( identifier = "DriverDefEditor",
         supportedTypes = { DriverDefType.class } )
 public class DriverDefEditor
-        extends BaseEditor
+        extends BaseEditor<DriverDefEditorContent, Metadata>
         implements DriverDefEditorView.Presenter {
 
     private DriverDefEditorView view;
@@ -140,6 +143,11 @@ public class DriverDefEditor
                 versionRecordManager.getCurrentPath( ) );
     }
 
+    @Override
+    protected Supplier<DriverDefEditorContent> getContentSupplier() {
+        return () -> editorContent;
+    }
+
     @Override
     public boolean mayClose( Integer currentHash ) {
         return super.mayClose( currentHash );
@@ -309,4 +317,4 @@ public class DriverDefEditor
         this.editorHelper.setDriverDef( editorContent.getDef( ) );
         editorHelper.setValid( true );
     }
-}
\ No newline at end of file
+}
diff --git a/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-api/pom.xml b/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-api/pom.xml
index d1c959d41..ce3949470 100644
--- a/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-api/pom.xml
+++ b/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-api/pom.xml
@@ -40,6 +40,11 @@
       <artifactId>uberfire-api</artifactId>
     </dependency>
 
+    <dependency>
+      <groupId>org.uberfire</groupId>
+      <artifactId>uberfire-commons-editor-api</artifactId>
+    </dependency>
+
   </dependencies>
 
 </project>
\ No newline at end of file
diff --git a/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-api/src/main/java/org/kie/workbench/common/screens/defaulteditor/service/DefaultEditorService.java b/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-api/src/main/java/org/kie/workbench/common/screens/defaulteditor/service/DefaultEditorService.java
index 584f42952..26fc2e054 100644
--- a/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-api/src/main/java/org/kie/workbench/common/screens/defaulteditor/service/DefaultEditorService.java
+++ b/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-api/src/main/java/org/kie/workbench/common/screens/defaulteditor/service/DefaultEditorService.java
@@ -17,13 +17,14 @@
 package org.kie.workbench.common.screens.defaulteditor.service;
 
 import org.guvnor.common.services.shared.file.SupportsUpdate;
-import org.guvnor.common.services.shared.metadata.model.Overview;
+import org.guvnor.common.services.shared.metadata.model.Metadata;
 import org.jboss.errai.bus.server.annotations.Remote;
-import org.uberfire.backend.vfs.ObservablePath;
 import org.uberfire.backend.vfs.Path;
+import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
 
 @Remote
-public interface DefaultEditorService extends SupportsUpdate<String> {
+public interface DefaultEditorService extends SupportsUpdate<String>,
+                                              SupportsSaveAndRename<String, Metadata> {
 
     DefaultEditorContent loadContent(Path path);
 }
diff --git a/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-backend/pom.xml b/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-backend/pom.xml
index 6f86a9b89..9de3f8817 100644
--- a/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-backend/pom.xml
+++ b/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-backend/pom.xml
@@ -56,6 +56,15 @@
       <artifactId>kie-wb-common-services-backend</artifactId>
     </dependency>
 
+    <dependency>
+      <groupId>org.uberfire</groupId>
+      <artifactId>uberfire-commons-editor-api</artifactId>
+    </dependency>
+
+    <dependency>
+      <groupId>org.uberfire</groupId>
+      <artifactId>uberfire-commons-editor-backend</artifactId>
+    </dependency>
   </dependencies>
 
 </project>
\ No newline at end of file
diff --git a/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-backend/src/main/java/org/kie/workbench/common/screens/defaulteditor/backend/server/DefaultEditorServiceImpl.java b/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-backend/src/main/java/org/kie/workbench/common/screens/defaulteditor/backend/server/DefaultEditorServiceImpl.java
index 79263eacb..e7bfcfeb4 100644
--- a/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-backend/src/main/java/org/kie/workbench/common/screens/defaulteditor/backend/server/DefaultEditorServiceImpl.java
+++ b/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-backend/src/main/java/org/kie/workbench/common/screens/defaulteditor/backend/server/DefaultEditorServiceImpl.java
@@ -16,6 +16,7 @@
 
 package org.kie.workbench.common.screens.defaulteditor.backend.server;
 
+import javax.annotation.PostConstruct;
 import javax.enterprise.context.ApplicationScoped;
 import javax.inject.Inject;
 import javax.inject.Named;
@@ -30,6 +31,8 @@ import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorServi
 import org.kie.workbench.common.services.backend.service.KieService;
 import org.uberfire.backend.server.util.Paths;
 import org.uberfire.backend.vfs.Path;
+import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
+import org.uberfire.ext.editor.commons.service.RenameService;
 import org.uberfire.io.IOService;
 
 @Service
@@ -41,6 +44,17 @@ public class DefaultEditorServiceImpl
     @Inject
     CommentedOptionFactory commentedOptionFactory;
 
+    @Inject
+    SaveAndRenameServiceImpl<String, Metadata> saveAndRenameService;
+
+    @Inject
+    RenameService renameService;
+
+    @PostConstruct
+    public void init() {
+        saveAndRenameService.init(this);
+    }
+
     @Override
     public DefaultEditorContent loadContent(Path path) {
         return super.loadContent(path);
@@ -69,4 +83,20 @@ public class DefaultEditorServiceImpl
     protected DefaultEditorContent constructContent(Path path, Overview overview) {
         return new DefaultEditorContent(overview);
     }
+
+    @Override
+    public Path rename(final Path path,
+                       final String newName,
+                       final String comment) {
+        return renameService.rename(path, newName, comment);
+    }
+
+    @Override
+    public Path saveAndRename(final Path path,
+                              final String newFileName,
+                              final Metadata metadata,
+                              final String content,
+                              final String comment) {
+        return saveAndRenameService.saveAndRename(path, newFileName, metadata, content, comment);
+    }
 }
diff --git a/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-client/src/main/java/org/kie/workbench/common/screens/defaulteditor/client/editor/GuvnorDefaultEditorPresenter.java b/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-client/src/main/java/org/kie/workbench/common/screens/defaulteditor/client/editor/GuvnorDefaultEditorPresenter.java
index 45b5b0363..faae3545a 100644
--- a/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-client/src/main/java/org/kie/workbench/common/screens/defaulteditor/client/editor/GuvnorDefaultEditorPresenter.java
+++ b/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-client/src/main/java/org/kie/workbench/common/screens/defaulteditor/client/editor/GuvnorDefaultEditorPresenter.java
@@ -15,6 +15,8 @@
  */
 package org.kie.workbench.common.screens.defaulteditor.client.editor;
 
+import java.util.function.Supplier;
+
 import javax.enterprise.context.Dependent;
 import javax.enterprise.event.Event;
 import javax.inject.Inject;
@@ -47,7 +49,7 @@ import org.uberfire.workbench.model.menu.Menus;
 @Dependent
 @WorkbenchEditor(identifier = "GuvnorDefaultFileEditor", supportedTypes = {AnyResourceType.class}, priority = -1)
 public class GuvnorDefaultEditorPresenter
-        extends KieEditor {
+        extends KieEditor<String> {
 
     private final GuvnorDefaultEditorView view;
 
@@ -121,6 +123,11 @@ public class GuvnorDefaultEditorPresenter
                                   getNoSuchFileExceptionErrorCallback()).loadContent(versionRecordManager.getCurrentPath());
     }
 
+    @Override
+    protected Supplier<String> getContentSupplier() {
+        return () -> null;
+    }
+
     private RemoteCallback<DefaultEditorContent> getLoadSuccessCallback() {
         return new RemoteCallback<DefaultEditorContent>() {
             @Override
diff --git a/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-client/src/main/java/org/kie/workbench/common/screens/defaulteditor/client/editor/KieTextEditorPresenter.java b/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-client/src/main/java/org/kie/workbench/common/screens/defaulteditor/client/editor/KieTextEditorPresenter.java
index 47fd714b3..da5044795 100644
--- a/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-client/src/main/java/org/kie/workbench/common/screens/defaulteditor/client/editor/KieTextEditorPresenter.java
+++ b/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-client/src/main/java/org/kie/workbench/common/screens/defaulteditor/client/editor/KieTextEditorPresenter.java
@@ -15,21 +15,27 @@
  */
 package org.kie.workbench.common.screens.defaulteditor.client.editor;
 
+import java.util.function.Supplier;
+
 import javax.annotation.PostConstruct;
 import javax.inject.Inject;
 
 import com.google.gwt.user.client.ui.IsWidget;
+import org.guvnor.common.services.shared.metadata.model.Metadata;
 import org.jboss.errai.common.client.api.Caller;
 import org.jboss.errai.common.client.api.RemoteCallback;
 import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorContent;
 import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorService;
 import org.kie.workbench.common.widgets.metadata.client.KieEditor;
+import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
 import org.uberfire.backend.vfs.ObservablePath;
+import org.uberfire.backend.vfs.Path;
 import org.uberfire.client.annotations.WorkbenchMenu;
 import org.uberfire.client.annotations.WorkbenchPartTitle;
 import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
 import org.uberfire.client.mvp.PlaceManager;
 import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
+import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
 import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
 import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
 import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
@@ -41,7 +47,7 @@ import org.uberfire.mvp.PlaceRequest;
 import org.uberfire.workbench.model.menu.Menus;
 
 public abstract class KieTextEditorPresenter
-        extends KieEditor {
+        extends KieEditor<String> {
 
     protected KieTextEditorView view;
 
@@ -91,17 +97,43 @@ public abstract class KieTextEditorPresenter
                         }
                     }))
                     .addCopy(versionRecordManager.getCurrentPath(),
-                             assetUpdateValidator)
-                    .addRename(versionRecordManager.getPathToLatest(),
-                               assetUpdateValidator)
+                             getAssetUpdateValidator())
+                    .addRename(getPathSupplier(),
+                               getAssetUpdateValidator(),
+                               getSaveAndRenameServiceCaller(),
+                               getMetadataSupplier(),
+                               getContentSupplier(),
+                               isDirtySupplier())
                     .addDelete(versionRecordManager.getPathToLatest(),
-                               assetUpdateValidator);
+                               getAssetUpdateValidator());
         }
 
         fileMenuBuilder
                 .addNewTopLevelMenu(versionRecordManager.buildMenu());
     }
 
+    @Override
+    protected Caller<? extends SupportsSaveAndRename<String, Metadata>> getSaveAndRenameServiceCaller() {
+        return defaultEditorService;
+    }
+
+    @Override
+    protected Supplier<String> getContentSupplier() {
+        return () -> view.getContent();
+    }
+
+    private Supplier<Path> getPathSupplier() {
+        return () -> versionRecordManager.getPathToLatest();
+    }
+
+    private AssetUpdateValidator getAssetUpdateValidator() {
+        return assetUpdateValidator;
+    }
+
+    private Supplier<Metadata> getMetadataSupplier() {
+        return () -> metadata;
+    }
+
     @Override
     protected Command onValidate() {
         // not used
diff --git a/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-client/src/test/java/org/kie/workbench/common/screens/defaulteditor/client/editor/GuvnorDefaultEditorPresenterTest.java b/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-client/src/test/java/org/kie/workbench/common/screens/defaulteditor/client/editor/GuvnorDefaultEditorPresenterTest.java
index ef18bf4f3..9d4ca6163 100644
--- a/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-client/src/test/java/org/kie/workbench/common/screens/defaulteditor/client/editor/GuvnorDefaultEditorPresenterTest.java
+++ b/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-client/src/test/java/org/kie/workbench/common/screens/defaulteditor/client/editor/GuvnorDefaultEditorPresenterTest.java
@@ -20,10 +20,11 @@ import com.google.gwtmockito.GwtMockitoTestRunner;
 import org.guvnor.common.services.project.client.security.ProjectController;
 import org.guvnor.common.services.project.context.ProjectContext;
 import org.guvnor.common.services.project.model.Project;
+import org.guvnor.common.services.shared.metadata.model.Metadata;
 import org.junit.Before;
 import org.junit.Test;
 import org.junit.runner.RunWith;
-import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
+import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
 import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
@@ -46,7 +47,7 @@ public class GuvnorDefaultEditorPresenterTest {
 
     @Spy
     @InjectMocks
-    protected FileMenuBuilderImpl fileMenuBuilder;
+    protected FileMenuBuilder<String, Metadata> fileMenuBuilder;
 
     @Mock
     protected ProjectController projectController;
diff --git a/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-client/src/test/java/org/kie/workbench/common/screens/defaulteditor/client/editor/KieTextEditorPresenterTest.java b/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-client/src/test/java/org/kie/workbench/common/screens/defaulteditor/client/editor/KieTextEditorPresenterTest.java
index 800510b6b..dc347105c 100644
--- a/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-client/src/test/java/org/kie/workbench/common/screens/defaulteditor/client/editor/KieTextEditorPresenterTest.java
+++ b/kie-wb-common-screens/kie-wb-common-default-editor/kie-wb-common-default-editor-client/src/test/java/org/kie/workbench/common/screens/defaulteditor/client/editor/KieTextEditorPresenterTest.java
@@ -16,14 +16,17 @@
 
 package org.kie.workbench.common.screens.defaulteditor.client.editor;
 
+import java.util.function.Supplier;
+
 import com.google.gwtmockito.GwtMockitoTestRunner;
 import org.guvnor.common.services.project.client.security.ProjectController;
 import org.guvnor.common.services.project.context.ProjectContext;
 import org.guvnor.common.services.project.model.Project;
+import org.guvnor.common.services.shared.metadata.model.Metadata;
 import org.junit.Before;
 import org.junit.Test;
 import org.junit.runner.RunWith;
-import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
+import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
 import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
@@ -34,7 +37,10 @@ import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
 import org.uberfire.workbench.model.menu.MenuItem;
 
 import static org.mockito.Matchers.any;
-import static org.mockito.Mockito.*;
+import static org.mockito.Mockito.doReturn;
+import static org.mockito.Mockito.mock;
+import static org.mockito.Mockito.never;
+import static org.mockito.Mockito.verify;
 
 @RunWith(GwtMockitoTestRunner.class)
 public class KieTextEditorPresenterTest {
@@ -47,7 +53,7 @@ public class KieTextEditorPresenterTest {
 
     @Spy
     @InjectMocks
-    protected FileMenuBuilderImpl fileMenuBuilder;
+    protected FileMenuBuilder<String, Metadata> fileMenuBuilder;
 
     @Mock
     protected ProjectController projectController;
@@ -66,6 +72,10 @@ public class KieTextEditorPresenterTest {
                 workbenchContext = KieTextEditorPresenterTest.this.workbenchContext;
                 versionRecordManager = KieTextEditorPresenterTest.this.versionRecordManager;
             }
+
+            protected Supplier<String> getContentSupplier() {
+                return null;
+            }
         };
     }
 
diff --git a/kie-wb-common-screens/kie-wb-common-java-editor/kie-wb-common-java-editor-client/src/main/java/org/kie/workbench/common/screens/javaeditor/client/editor/JavaEditorPresenter.java b/kie-wb-common-screens/kie-wb-common-java-editor/kie-wb-common-java-editor-client/src/main/java/org/kie/workbench/common/screens/javaeditor/client/editor/JavaEditorPresenter.java
index cf7a87e9b..ff1bec3a3 100644
--- a/kie-wb-common-screens/kie-wb-common-java-editor/kie-wb-common-java-editor-client/src/main/java/org/kie/workbench/common/screens/javaeditor/client/editor/JavaEditorPresenter.java
+++ b/kie-wb-common-screens/kie-wb-common-java-editor/kie-wb-common-java-editor-client/src/main/java/org/kie/workbench/common/screens/javaeditor/client/editor/JavaEditorPresenter.java
@@ -16,6 +16,8 @@
 
 package org.kie.workbench.common.screens.javaeditor.client.editor;
 
+import java.util.function.Supplier;
+
 import javax.inject.Inject;
 
 import com.google.gwt.user.client.ui.IsWidget;
@@ -33,9 +35,8 @@ import org.uberfire.lifecycle.OnStartup;
 import org.uberfire.mvp.Command;
 import org.uberfire.mvp.PlaceRequest;
 
-@WorkbenchEditor(identifier = "JavaEditor", supportedTypes = {JavaResourceType.class})
-public class JavaEditorPresenter
-        extends KieEditor {
+@WorkbenchEditor(identifier = "JavaEditor", supportedTypes = {JavaResourceType.class}, priority = 3)
+public class JavaEditorPresenter extends KieEditor<String> {
 
     @Inject
     private Caller<VFSService> vfsServices;
@@ -82,6 +83,11 @@ public class JavaEditorPresenter
         }).readAllString(versionRecordManager.getCurrentPath());
     }
 
+    @Override
+    protected Supplier<String> getContentSupplier() {
+        return () -> null;
+    }
+
     @Override
     protected void makeMenuBar() {
         menuBuilder.addNewTopLevelMenu(versionRecordManager.buildMenu());
diff --git a/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-api/pom.xml b/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-api/pom.xml
index 55ed5f9b3..678fdc746 100644
--- a/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-api/pom.xml
+++ b/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-api/pom.xml
@@ -77,6 +77,11 @@
       <artifactId>uberfire-api</artifactId>
     </dependency>
 
+    <dependency>
+      <groupId>org.uberfire</groupId>
+      <artifactId>uberfire-commons-editor-api</artifactId>
+    </dependency>
+
   </dependencies>
 
 </project>
\ No newline at end of file
diff --git a/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-api/src/main/java/org/kie/workbench/common/screens/projecteditor/service/PomEditorService.java b/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-api/src/main/java/org/kie/workbench/common/screens/projecteditor/service/PomEditorService.java
index 894be1362..ae4548407 100644
--- a/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-api/src/main/java/org/kie/workbench/common/screens/projecteditor/service/PomEditorService.java
+++ b/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-api/src/main/java/org/kie/workbench/common/screens/projecteditor/service/PomEditorService.java
@@ -21,16 +21,16 @@ import org.guvnor.common.services.shared.metadata.model.Metadata;
 import org.jboss.errai.bus.server.annotations.Remote;
 import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorContent;
 import org.uberfire.backend.vfs.Path;
+import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
 
 @Remote
-public interface PomEditorService {
+public interface PomEditorService extends SupportsSaveAndRename<String, Metadata> {
 
-    DefaultEditorContent loadContent( final Path path );
-
-    Path save( final Path path,
-               final String content,
-               final Metadata metadata,
-               final String comment,
-               final DeploymentMode mode );
+    DefaultEditorContent loadContent(final Path path);
 
+    Path save(final Path path,
+              final String content,
+              final Metadata metadata,
+              final String comment,
+              final DeploymentMode mode);
 }
diff --git a/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-backend/pom.xml b/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-backend/pom.xml
index 3faa83ba8..91722f47a 100644
--- a/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-backend/pom.xml
+++ b/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-backend/pom.xml
@@ -157,6 +157,16 @@
       <artifactId>uberfire-backend-server</artifactId>
     </dependency>
 
+    <dependency>
+      <groupId>org.uberfire</groupId>
+      <artifactId>uberfire-commons-editor-api</artifactId>
+    </dependency>
+
+    <dependency>
+      <groupId>org.uberfire</groupId>
+      <artifactId>uberfire-commons-editor-backend</artifactId>
+    </dependency>
+
     <dependency>
       <groupId>org.jboss.spec.javax.servlet</groupId>
       <artifactId>jboss-servlet-api_3.1_spec</artifactId>
diff --git a/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-backend/src/main/java/org/kie/workbench/common/screens/projecteditor/backend/server/PomEditorServiceImpl.java b/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-backend/src/main/java/org/kie/workbench/common/screens/projecteditor/backend/server/PomEditorServiceImpl.java
index 9a5e124fd..df51c0535 100644
--- a/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-backend/src/main/java/org/kie/workbench/common/screens/projecteditor/backend/server/PomEditorServiceImpl.java
+++ b/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-backend/src/main/java/org/kie/workbench/common/screens/projecteditor/backend/server/PomEditorServiceImpl.java
@@ -18,6 +18,8 @@ package org.kie.workbench.common.screens.projecteditor.backend.server;
 
 import java.io.IOException;
 import java.util.Set;
+
+import javax.annotation.PostConstruct;
 import javax.enterprise.context.ApplicationScoped;
 import javax.inject.Inject;
 import javax.inject.Named;
@@ -46,6 +48,9 @@ import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.uberfire.backend.server.util.Paths;
 import org.uberfire.backend.vfs.Path;
+import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
+import org.uberfire.ext.editor.commons.service.RenameService;
+import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
 import org.uberfire.io.IOService;
 
 @Service
@@ -68,6 +73,8 @@ public class PomEditorServiceImpl implements PomEditorService {
     private POMContentHandler pomContentHandler;
     private ProjectRepositoryResolver repositoryResolver;
     private ProjectRepositoriesService projectRepositoriesService;
+    private RenameService renameService;
+    private SaveAndRenameServiceImpl<String, Metadata> saveAndRenameService;
 
     public PomEditorServiceImpl() {
         //Zero-parameter constructor for WELD proxies
@@ -81,7 +88,9 @@ public class PomEditorServiceImpl implements PomEditorService {
                                  final KieProjectService projectService,
                                  final POMContentHandler pomContentHandler,
                                  final ProjectRepositoryResolver repositoryResolver,
-                                 final ProjectRepositoriesService projectRepositoriesService ) {
+                                 final ProjectRepositoriesService projectRepositoriesService,
+                                 final RenameService renameService,
+                                 final SaveAndRenameServiceImpl<String, Metadata> saveAndRenameService) {
         this.ioService = ioService;
         this.defaultEditorService = defaultEditorService;
         this.metadataService = metadataService;
@@ -91,6 +100,14 @@ public class PomEditorServiceImpl implements PomEditorService {
         this.pomContentHandler = pomContentHandler;
         this.repositoryResolver = repositoryResolver;
         this.projectRepositoriesService = projectRepositoriesService;
+        this.renameService = renameService;
+        this.saveAndRenameService = saveAndRenameService;
+    }
+
+    @PostConstruct
+    public void init() {
+        final SupportsSaveAndRename<String, Metadata> updateService = this;
+        saveAndRenameService.init(updateService);
     }
 
     @Override
@@ -158,4 +175,27 @@ public class PomEditorServiceImpl implements PomEditorService {
         }
     }
 
+    @Override
+    public Path saveAndRename(final Path path,
+                              final String newFileName,
+                              final Metadata metadata,
+                              final String content,
+                              final String comment) {
+        return saveAndRenameService.saveAndRename(path, newFileName, metadata, content, comment);
+    }
+
+    @Override
+    public Path rename(final Path path,
+                       final String newName,
+                       final String comment) {
+        return renameService.rename(path, newName, comment);
+    }
+
+    @Override
+    public Path save(final Path path,
+                     final String content,
+                     final Metadata metadata,
+                     final String comment) {
+        return save(path, content, metadata, comment, DeploymentMode.FORCED);
+    }
 }
diff --git a/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-backend/src/test/java/org/kie/workbench/common/screens/projecteditor/backend/server/PomEditorServiceImplTest.java b/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-backend/src/test/java/org/kie/workbench/common/screens/projecteditor/backend/server/PomEditorServiceImplTest.java
index a24103256..603ebf8c6 100644
--- a/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-backend/src/test/java/org/kie/workbench/common/screens/projecteditor/backend/server/PomEditorServiceImplTest.java
+++ b/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-backend/src/test/java/org/kie/workbench/common/screens/projecteditor/backend/server/PomEditorServiceImplTest.java
@@ -49,12 +49,21 @@ import org.mockito.ArgumentCaptor;
 import org.mockito.Mock;
 import org.mockito.runners.MockitoJUnitRunner;
 import org.uberfire.backend.vfs.Path;
+import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
+import org.uberfire.ext.editor.commons.service.RenameService;
 import org.uberfire.io.IOService;
 import org.uberfire.java.nio.base.options.CommentedOption;
 import org.uberfire.java.nio.file.FileSystem;
 
-import static org.junit.Assert.*;
-import static org.mockito.Mockito.*;
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertNotNull;
+import static org.junit.Assert.fail;
+import static org.mockito.Mockito.any;
+import static org.mockito.Mockito.eq;
+import static org.mockito.Mockito.never;
+import static org.mockito.Mockito.times;
+import static org.mockito.Mockito.verify;
+import static org.mockito.Mockito.when;
 
 @RunWith(MockitoJUnitRunner.class)
 public class PomEditorServiceImplTest {
@@ -80,6 +89,12 @@ public class PomEditorServiceImplTest {
     @Mock
     private ProjectRepositoriesService projectRepositoriesService;
 
+    @Mock
+    private RenameService renameService;
+
+    @Mock
+    private SaveAndRenameServiceImpl<String, Metadata> saveAndRenameService;
+
     @Mock
     private Path pomPath;
 
@@ -130,14 +145,16 @@ public class PomEditorServiceImplTest {
 
     @Before
     public void setup() {
-        service = new PomEditorServiceImpl( ioService,
-                                            defaultEditorService,
-                                            metadataService,
-                                            commentedOptionFactory,
-                                            projectService,
-                                            pomContentHandler,
-                                            repositoryResolver,
-                                            projectRepositoriesService );
+        service = new PomEditorServiceImpl(ioService,
+                                           defaultEditorService,
+                                           metadataService,
+                                           commentedOptionFactory,
+                                           projectService,
+                                           pomContentHandler,
+                                           repositoryResolver,
+                                           projectRepositoriesService,
+                                           renameService,
+                                           saveAndRenameService);
 
         when( pomPath.toURI() ).thenReturn( pomPathUri );
         when( defaultEditorService.loadContent( pomPath ) ).thenReturn( content );
diff --git a/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-client/src/main/java/org/kie/workbench/common/screens/projecteditor/client/forms/KModuleEditorScreenPresenter.java b/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-client/src/main/java/org/kie/workbench/common/screens/projecteditor/client/forms/KModuleEditorScreenPresenter.java
index ef842bc68..6407b1c88 100644
--- a/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-client/src/main/java/org/kie/workbench/common/screens/projecteditor/client/forms/KModuleEditorScreenPresenter.java
+++ b/kie-wb-common-screens/kie-wb-common-project-editor/kie-wb-common-project-editor-client/src/main/java/org/kie/workbench/common/screens/projecteditor/client/forms/KModuleEditorScreenPresenter.java
@@ -32,7 +32,7 @@ import org.uberfire.lifecycle.OnStartup;
 import org.uberfire.mvp.PlaceRequest;
 import org.uberfire.workbench.model.menu.Menus;
 
-@WorkbenchEditor(identifier = "kmoduleScreen", supportedTypes = { KModuleResourceType.class })
+@WorkbenchEditor(identifier = "kmoduleScreen", supportedTypes = { KModuleResourceType.class }, priority = 2)
 public class KModuleEditorScreenPresenter
         extends KieTextEditorPresenter {
 
diff --git a/kie-wb-common-screens/kie-wb-common-project-imports-editor/kie-wb-common-project-imports-editor-client/src/main/java/org/kie/workbench/common/screens/projectimportsscreen/client/forms/ProjectImportsScreenPresenter.java b/kie-wb-common-screens/kie-wb-common-project-imports-editor/kie-wb-common-project-imports-editor-client/src/main/java/org/kie/workbench/common/screens/projectimportsscreen/client/forms/ProjectImportsScreenPresenter.java
index 4226f2f13..d13272306 100644
--- a/kie-wb-common-screens/kie-wb-common-project-imports-editor/kie-wb-common-project-imports-editor-client/src/main/java/org/kie/workbench/common/screens/projectimportsscreen/client/forms/ProjectImportsScreenPresenter.java
+++ b/kie-wb-common-screens/kie-wb-common-project-imports-editor/kie-wb-common-project-imports-editor-client/src/main/java/org/kie/workbench/common/screens/projectimportsscreen/client/forms/ProjectImportsScreenPresenter.java
@@ -16,6 +16,8 @@
 
 package org.kie.workbench.common.screens.projectimportsscreen.client.forms;
 
+import java.util.function.Supplier;
+
 import javax.inject.Inject;
 
 import com.google.gwt.user.client.ui.IsWidget;
@@ -46,7 +48,7 @@ import org.uberfire.workbench.model.menu.Menus;
 
 @WorkbenchEditor(identifier = "projectConfigScreen", supportedTypes = {ProjectImportsResourceType.class})
 public class ProjectImportsScreenPresenter
-        extends KieEditor {
+        extends KieEditor<ProjectImports> {
 
     private ProjectImportsScreenView view;
 
@@ -125,6 +127,11 @@ public class ProjectImportsScreenPresenter
                             new HasBusyIndicatorDefaultErrorCallback(view)).loadContent(versionRecordManager.getCurrentPath());
     }
 
+    @Override
+    protected Supplier<ProjectImports> getContentSupplier() {
+        return null;
+    }
+
     protected void save() {
         savePopUpPresenter.show(versionRecordManager.getCurrentPath(),
                                 new ParameterizedCommand<String>() {
diff --git a/kie-wb-common-stunner/kie-wb-common-stunner-extensions/kie-wb-common-stunner-project/kie-wb-common-stunner-project-client/src/main/java/org/kie/workbench/common/stunner/project/client/editor/AbstractProjectDiagramEditor.java b/kie-wb-common-stunner/kie-wb-common-stunner-extensions/kie-wb-common-stunner-project/kie-wb-common-stunner-project-client/src/main/java/org/kie/workbench/common/stunner/project/client/editor/AbstractProjectDiagramEditor.java
index cf509dcdb..d310a2623 100644
--- a/kie-wb-common-stunner/kie-wb-common-stunner-extensions/kie-wb-common-stunner-project/kie-wb-common-stunner-project-client/src/main/java/org/kie/workbench/common/stunner/project/client/editor/AbstractProjectDiagramEditor.java
+++ b/kie-wb-common-stunner/kie-wb-common-stunner-extensions/kie-wb-common-stunner-project/kie-wb-common-stunner-project-client/src/main/java/org/kie/workbench/common/stunner/project/client/editor/AbstractProjectDiagramEditor.java
@@ -21,6 +21,7 @@ import java.util.Collection;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Objects;
+import java.util.function.Supplier;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 
@@ -31,6 +32,8 @@ import javax.inject.Inject;
 
 import com.google.gwt.logging.client.LogConfiguration;
 import com.google.gwt.user.client.ui.IsWidget;
+import org.guvnor.common.services.shared.metadata.model.Metadata;
+import org.jboss.errai.common.client.api.Caller;
 import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
 import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenterFactory;
 import org.kie.workbench.common.stunner.core.client.api.SessionManager;
@@ -94,7 +97,7 @@ import org.uberfire.workbench.model.menu.Menus;
 import static java.util.logging.Level.FINE;
 
 // TODO: i18n.
-public abstract class AbstractProjectDiagramEditor<R extends ClientResourceType> extends KieEditor {
+public abstract class AbstractProjectDiagramEditor<R extends ClientResourceType> extends KieEditor<ProjectDiagram> {
 
     private static Logger LOGGER = Logger.getLogger(AbstractProjectDiagramEditor.class.getName());
     private static final String TITLE_FORMAT_TEMPLATE = "#title.#suffix - #type";
@@ -212,6 +215,11 @@ public abstract class AbstractProjectDiagramEditor<R extends ClientResourceType>
                                          });
     }
 
+    @Override
+    protected Supplier<ProjectDiagram> getContentSupplier() {
+        return () -> null;
+    }
+
     protected void open(final ProjectDiagram diagram) {
         showLoadingViews();
         final AbstractClientFullSession session = newSession(diagram);
diff --git a/kie-wb-common-stunner/kie-wb-common-stunner-extensions/kie-wb-common-stunner-project/kie-wb-common-stunner-project-client/src/test/java/org/kie/workbench/common/stunner/project/client/editor/AbstractProjectDiagramEditorTest.java b/kie-wb-common-stunner/kie-wb-common-stunner-extensions/kie-wb-common-stunner-project/kie-wb-common-stunner-project-client/src/test/java/org/kie/workbench/common/stunner/project/client/editor/AbstractProjectDiagramEditorTest.java
index ac55f2591..acb4bd253 100644
--- a/kie-wb-common-stunner/kie-wb-common-stunner-extensions/kie-wb-common-stunner-project/kie-wb-common-stunner-project-client/src/test/java/org/kie/workbench/common/stunner/project/client/editor/AbstractProjectDiagramEditorTest.java
+++ b/kie-wb-common-stunner/kie-wb-common-stunner-extensions/kie-wb-common-stunner-project/kie-wb-common-stunner-project-client/src/test/java/org/kie/workbench/common/stunner/project/client/editor/AbstractProjectDiagramEditorTest.java
@@ -20,6 +20,7 @@ import com.google.gwtmockito.GwtMockitoTestRunner;
 import org.guvnor.common.services.project.client.security.ProjectController;
 import org.guvnor.common.services.project.context.ProjectContext;
 import org.guvnor.common.services.project.model.Project;
+import org.guvnor.common.services.shared.metadata.model.Metadata;
 import org.junit.Before;
 import org.junit.Test;
 import org.junit.runner.RunWith;
@@ -40,6 +41,8 @@ import org.kie.workbench.common.stunner.core.client.session.command.impl.Validat
 import org.kie.workbench.common.stunner.core.client.session.command.impl.VisitGraphSessionCommand;
 import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
 import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
+import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
+import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
 import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
 import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
 import org.mockito.InjectMocks;
@@ -74,7 +77,7 @@ public class AbstractProjectDiagramEditorTest {
 
     @Spy
     @InjectMocks
-    protected FileMenuBuilderImpl fileMenuBuilder;
+    protected FileMenuBuilder<ProjectDiagram, Metadata> fileMenuBuilder;
 
     @Mock
     protected ProjectController projectController;
diff --git a/kie-wb-common-widgets/kie-wb-common-ui/pom.xml b/kie-wb-common-widgets/kie-wb-common-ui/pom.xml
index d7e6cc522..afe9b89ad 100644
--- a/kie-wb-common-widgets/kie-wb-common-ui/pom.xml
+++ b/kie-wb-common-widgets/kie-wb-common-ui/pom.xml
@@ -232,6 +232,11 @@
       <scope>test</scope>
     </dependency>
 
+    <dependency>
+      <groupId>org.kie.workbench.services</groupId>
+      <artifactId>kie-wb-common-services-backend</artifactId>
+    </dependency>
+
     <dependency>
       <groupId>org.uberfire</groupId>
       <artifactId>uberfire-backend-server</artifactId>
diff --git a/kie-wb-common-widgets/kie-wb-common-ui/src/main/java/org/kie/workbench/common/widgets/client/menu/FileMenuBuilder.java b/kie-wb-common-widgets/kie-wb-common-ui/src/main/java/org/kie/workbench/common/widgets/client/menu/FileMenuBuilder.java
index b39d9427b..9194cb187 100644
--- a/kie-wb-common-widgets/kie-wb-common-ui/src/main/java/org/kie/workbench/common/widgets/client/menu/FileMenuBuilder.java
+++ b/kie-wb-common-widgets/kie-wb-common-ui/src/main/java/org/kie/workbench/common/widgets/client/menu/FileMenuBuilder.java
@@ -15,60 +15,71 @@
 
 package org.kie.workbench.common.widgets.client.menu;
 
+import java.util.function.Supplier;
+
+import org.jboss.errai.common.client.api.Caller;
 import org.uberfire.backend.vfs.Path;
 import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
 import org.uberfire.ext.editor.commons.client.menu.HasLockSyncMenuStateHelper;
 import org.uberfire.ext.editor.commons.client.validation.Validator;
+import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
 import org.uberfire.mvp.Command;
 import org.uberfire.workbench.model.menu.MenuItem;
 import org.uberfire.workbench.model.menu.Menus;
 
-public interface FileMenuBuilder extends HasLockSyncMenuStateHelper {
+public interface FileMenuBuilder<T, M> extends HasLockSyncMenuStateHelper {
+
+    FileMenuBuilder<T, M> addSave(final MenuItem menuItem);
 
-    FileMenuBuilder addSave(final MenuItem menuItem);
+    FileMenuBuilder<T, M> addSave(final Command command);
 
-    FileMenuBuilder addSave(final Command command);
+    FileMenuBuilder<T, M> addNewTopLevelMenu(final MenuItem menu);
 
-    FileMenuBuilder addNewTopLevelMenu(final MenuItem menu);
+    FileMenuBuilder<T, M> addDelete(final Command command);
 
-    FileMenuBuilder addDelete(final Command command);
+    FileMenuBuilder<T, M> addDelete(final Path path);
 
-    FileMenuBuilder addDelete(final Path path);
+    FileMenuBuilder<T, M> addDelete(final Path path,
+                                    final Validator validator);
 
-    FileMenuBuilder addDelete(final Path path,
-                              final Validator validator);
+    FileMenuBuilder<T, M> addDelete(final BasicFileMenuBuilder.PathProvider provider);
 
-    FileMenuBuilder addDelete(final BasicFileMenuBuilder.PathProvider provider);
+    FileMenuBuilder<T, M> addDelete(final BasicFileMenuBuilder.PathProvider provider,
+                                    final Validator validator);
 
-    FileMenuBuilder addDelete(final BasicFileMenuBuilder.PathProvider provider,
-                              final Validator validator);
+    FileMenuBuilder<T, M> addRename(final Command command);
 
-    FileMenuBuilder addRename(final Command command);
+    FileMenuBuilder<T, M> addRename(final Path path);
 
-    FileMenuBuilder addRename(final Path path);
+    FileMenuBuilder<T, M> addRename(final Path path,
+                                    final Validator validator);
 
-    FileMenuBuilder addRename(final Path path,
-                              final Validator validator);
+    FileMenuBuilder<T, M> addRename(final BasicFileMenuBuilder.PathProvider provider,
+                                    final Validator validator);
 
-    FileMenuBuilder addRename(final BasicFileMenuBuilder.PathProvider provider,
-                              final Validator validator);
+    FileMenuBuilder<T, M> addRename(final Supplier<Path> pathSupplier,
+                                    final Validator validator,
+                                    final Caller<? extends SupportsSaveAndRename<T, M>> renameCaller,
+                                    final Supplier<M> metadataSupplier,
+                                    final Supplier<T> contentSupplier,
+                                    final Supplier<Boolean> isDirtySupplier);
 
-    FileMenuBuilder addCopy(final Command command);
+    FileMenuBuilder<T, M> addCopy(final Command command);
 
-    FileMenuBuilder addCopy(final Path path);
+    FileMenuBuilder<T, M> addCopy(final Path path);
 
-    FileMenuBuilder addCopy(final Path path,
-                            final Validator validator);
+    FileMenuBuilder<T, M> addCopy(final Path path,
+                                  final Validator validator);
 
-    FileMenuBuilder addCopy(final BasicFileMenuBuilder.PathProvider provider,
-                            final Validator validator);
+    FileMenuBuilder<T, M> addCopy(final BasicFileMenuBuilder.PathProvider provider,
+                                  final Validator validator);
 
-    FileMenuBuilder addValidate(final Command command);
+    FileMenuBuilder<T, M> addValidate(final Command command);
 
-    FileMenuBuilder addRestoreVersion(final Path path);
+    FileMenuBuilder<T, M> addRestoreVersion(final Path path);
 
-    FileMenuBuilder addCommand(final String caption,
-                               final Command command);
+    FileMenuBuilder<T, M> addCommand(final String caption,
+                                     final Command command);
 
     Menus build();
 }
diff --git a/kie-wb-common-widgets/kie-wb-common-ui/src/main/java/org/kie/workbench/common/widgets/client/menu/FileMenuBuilderImpl.java b/kie-wb-common-widgets/kie-wb-common-ui/src/main/java/org/kie/workbench/common/widgets/client/menu/FileMenuBuilderImpl.java
index 98a83e1a8..f3a7e97a2 100644
--- a/kie-wb-common-widgets/kie-wb-common-ui/src/main/java/org/kie/workbench/common/widgets/client/menu/FileMenuBuilderImpl.java
+++ b/kie-wb-common-widgets/kie-wb-common-ui/src/main/java/org/kie/workbench/common/widgets/client/menu/FileMenuBuilderImpl.java
@@ -16,6 +16,8 @@
 
 package org.kie.workbench.common.widgets.client.menu;
 
+import java.util.function.Supplier;
+
 import javax.enterprise.context.Dependent;
 import javax.inject.Inject;
 
@@ -26,15 +28,16 @@ import org.uberfire.ext.editor.commons.client.validation.Validator;
 import org.uberfire.ext.editor.commons.service.CopyService;
 import org.uberfire.ext.editor.commons.service.DeleteService;
 import org.uberfire.ext.editor.commons.service.RenameService;
+import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
 import org.uberfire.mvp.Command;
 import org.uberfire.workbench.model.menu.MenuItem;
 import org.uberfire.workbench.model.menu.Menus;
 
 @Dependent
-public class FileMenuBuilderImpl implements FileMenuBuilder {
+public class FileMenuBuilderImpl<T, M> implements FileMenuBuilder<T, M> {
 
     @Inject
-    private BasicFileMenuBuilder menuBuilder;
+    private BasicFileMenuBuilder<T, M> menuBuilder;
 
     @Inject
     private Caller<DeleteService> deleteService;
@@ -51,63 +54,63 @@ public class FileMenuBuilderImpl implements FileMenuBuilder {
     }
 
     @Override
-    public FileMenuBuilder addSave(final MenuItem menuItem) {
+    public FileMenuBuilder<T, M> addSave(final MenuItem menuItem) {
         menuBuilder.addSave(menuItem);
         return this;
     }
 
     @Override
-    public FileMenuBuilder addSave(final Command command) {
+    public FileMenuBuilder<T, M> addSave(final Command command) {
         menuBuilder.addSave(command);
         return this;
     }
 
     @Override
-    public FileMenuBuilder addDelete(final Command command) {
+    public FileMenuBuilder<T, M> addDelete(final Command command) {
         menuBuilder.addDelete(command);
         return this;
     }
 
     @Override
-    public FileMenuBuilder addRename(final Command command) {
+    public FileMenuBuilder<T, M> addRename(final Command command) {
         menuBuilder.addRename(command);
         return this;
     }
 
     @Override
-    public FileMenuBuilder addCopy(final Command command) {
+    public FileMenuBuilder<T, M> addCopy(final Command command) {
         menuBuilder.addCopy(command);
         return this;
     }
 
     @Override
-    public FileMenuBuilder addValidate(final Command command) {
+    public FileMenuBuilder<T, M> addValidate(final Command command) {
         menuBuilder.addValidate(command);
         return this;
     }
 
     @Override
-    public FileMenuBuilder addRestoreVersion(final Path path) {
+    public FileMenuBuilder<T, M> addRestoreVersion(final Path path) {
         menuBuilder.addRestoreVersion(path);
         return this;
     }
 
     @Override
-    public FileMenuBuilder addCommand(final String caption,
-                                      final Command command) {
+    public FileMenuBuilder<T, M> addCommand(final String caption,
+                                            final Command command) {
         menuBuilder.addCommand(caption,
                                command);
         return this;
     }
 
     @Override
-    public FileMenuBuilder addNewTopLevelMenu(final MenuItem menu) {
+    public FileMenuBuilder<T, M> addNewTopLevelMenu(final MenuItem menu) {
         menuBuilder.addNewTopLevelMenu(menu);
         return this;
     }
 
     @Override
-    public FileMenuBuilderImpl addDelete(final Path path) {
+    public FileMenuBuilder<T, M> addDelete(final Path path) {
         menuBuilder.addDelete(path,
                               deleteService);
 
@@ -115,8 +118,8 @@ public class FileMenuBuilderImpl implements FileMenuBuilder {
     }
 
     @Override
-    public FileMenuBuilderImpl addDelete(final Path path,
-                                         final Validator validator) {
+    public FileMenuBuilder<T, M> addDelete(final Path path,
+                                           final Validator validator) {
         menuBuilder.addDelete(path,
                               deleteService,
                               validator);
@@ -125,7 +128,7 @@ public class FileMenuBuilderImpl implements FileMenuBuilder {
     }
 
     @Override
-    public FileMenuBuilder addDelete(final BasicFileMenuBuilder.PathProvider provider) {
+    public FileMenuBuilder<T, M> addDelete(final BasicFileMenuBuilder.PathProvider provider) {
         menuBuilder.addDelete(provider,
                               deleteService);
 
@@ -133,8 +136,8 @@ public class FileMenuBuilderImpl implements FileMenuBuilder {
     }
 
     @Override
-    public FileMenuBuilder addDelete(final BasicFileMenuBuilder.PathProvider provider,
-                                     final Validator validator) {
+    public FileMenuBuilder<T, M> addDelete(final BasicFileMenuBuilder.PathProvider provider,
+                                           final Validator validator) {
         menuBuilder.addDelete(provider,
                               deleteService,
                               validator);
@@ -143,7 +146,7 @@ public class FileMenuBuilderImpl implements FileMenuBuilder {
     }
 
     @Override
-    public FileMenuBuilderImpl addRename(final Path path) {
+    public FileMenuBuilder<T, M> addRename(final Path path) {
         menuBuilder.addRename(path,
                               renameService);
 
@@ -151,8 +154,8 @@ public class FileMenuBuilderImpl implements FileMenuBuilder {
     }
 
     @Override
-    public FileMenuBuilderImpl addRename(final Path path,
-                                         final Validator validator) {
+    public FileMenuBuilder<T, M> addRename(final Path path,
+                                           final Validator validator) {
         menuBuilder.addRename(path,
                               validator,
                               renameService);
@@ -161,8 +164,8 @@ public class FileMenuBuilderImpl implements FileMenuBuilder {
     }
 
     @Override
-    public FileMenuBuilder addRename(final BasicFileMenuBuilder.PathProvider provider,
-                                     final Validator validator) {
+    public FileMenuBuilder<T, M> addRename(final BasicFileMenuBuilder.PathProvider provider,
+                                           final Validator validator) {
         menuBuilder.addRename(provider,
                               validator,
                               renameService);
@@ -171,7 +174,23 @@ public class FileMenuBuilderImpl implements FileMenuBuilder {
     }
 
     @Override
-    public FileMenuBuilderImpl addCopy(final Path path) {
+    public FileMenuBuilder<T, M> addRename(final Supplier<Path> pathSupplier,
+                                           final Validator validator,
+                                           final Caller<? extends SupportsSaveAndRename<T, M>> renameCaller,
+                                           final Supplier<M> metadataSupplier,
+                                           final Supplier<T> contentSupplier,
+                                           final Supplier<Boolean> isDirtySupplier) {
+        menuBuilder.addRename(pathSupplier,
+                              validator,
+                              renameCaller,
+                              metadataSupplier,
+                              contentSupplier,
+                              isDirtySupplier);
+        return this;
+    }
+
+    @Override
+    public FileMenuBuilder<T, M> addCopy(final Path path) {
         menuBuilder.addCopy(path,
                             copyService);
 
@@ -179,8 +198,8 @@ public class FileMenuBuilderImpl implements FileMenuBuilder {
     }
 
     @Override
-    public FileMenuBuilderImpl addCopy(final Path path,
-                                       final Validator validator) {
+    public FileMenuBuilder<T, M> addCopy(final Path path,
+                                         final Validator validator) {
         menuBuilder.addCopy(path,
                             validator,
                             copyService);
@@ -189,8 +208,8 @@ public class FileMenuBuilderImpl implements FileMenuBuilder {
     }
 
     @Override
-    public FileMenuBuilder addCopy(final BasicFileMenuBuilder.PathProvider provider,
-                                   final Validator validator) {
+    public FileMenuBuilder<T, M> addCopy(final BasicFileMenuBuilder.PathProvider provider,
+                                         final Validator validator) {
         menuBuilder.addCopy(provider,
                             validator,
                             copyService);
diff --git a/kie-wb-common-widgets/kie-wb-metadata-widget/src/main/java/org/kie/workbench/common/widgets/metadata/client/KieEditor.java b/kie-wb-common-widgets/kie-wb-metadata-widget/src/main/java/org/kie/workbench/common/widgets/metadata/client/KieEditor.java
index 664dc7015..e7b5e1919 100644
--- a/kie-wb-common-widgets/kie-wb-metadata-widget/src/main/java/org/kie/workbench/common/widgets/metadata/client/KieEditor.java
+++ b/kie-wb-common-widgets/kie-wb-metadata-widget/src/main/java/org/kie/workbench/common/widgets/metadata/client/KieEditor.java
@@ -49,8 +49,8 @@ import org.uberfire.mvp.PlaceRequest;
 import org.uberfire.workbench.events.NotificationEvent;
 import org.uberfire.workbench.model.menu.MenuItem;
 
-public abstract class KieEditor
-        extends BaseEditor
+public abstract class KieEditor<T>
+        extends BaseEditor<T, Metadata>
         implements KieEditorWrapperView.KieEditorWrapperPresenter {
 
     @Inject
@@ -60,7 +60,7 @@ public abstract class KieEditor
     protected OverviewWidgetPresenter overviewWidget;
 
     @Inject
-    protected FileMenuBuilder fileMenuBuilder;
+    protected FileMenuBuilder<T, Metadata> fileMenuBuilder;
 
     @Inject
     protected ProjectContext workbenchContext;
@@ -249,8 +249,12 @@ public abstract class KieEditor
                     .addSave(versionRecordManager.newSaveMenuItem(this::saveAction))
                     .addCopy(versionRecordManager.getCurrentPath(),
                              assetUpdateValidator)
-                    .addRename(versionRecordManager.getPathToLatest(),
-                               assetUpdateValidator)
+                    .addRename(() -> versionRecordManager.getPathToLatest(),
+                               assetUpdateValidator,
+                               getSaveAndRenameServiceCaller(),
+                               () -> metadata,
+                               getContentSupplier(),
+                               isDirtySupplier())
                     .addDelete(versionRecordManager.getPathToLatest(),
                                assetUpdateValidator);
         }
diff --git a/kie-wb-common-widgets/kie-wb-metadata-widget/src/test/java/org/kie/workbench/common/widgets/metadata/client/KieEditorTest.java b/kie-wb-common-widgets/kie-wb-metadata-widget/src/test/java/org/kie/workbench/common/widgets/metadata/client/KieEditorTest.java
index ace2be900..bdba308b5 100644
--- a/kie-wb-common-widgets/kie-wb-metadata-widget/src/test/java/org/kie/workbench/common/widgets/metadata/client/KieEditorTest.java
+++ b/kie-wb-common-widgets/kie-wb-metadata-widget/src/test/java/org/kie/workbench/common/widgets/metadata/client/KieEditorTest.java
@@ -16,13 +16,17 @@
 
 package org.kie.workbench.common.widgets.metadata.client;
 
+import java.util.function.Supplier;
+
 import com.google.gwtmockito.GwtMockitoTestRunner;
 import org.guvnor.common.services.project.client.security.ProjectController;
 import org.guvnor.common.services.project.context.ProjectContext;
 import org.guvnor.common.services.project.model.Project;
+import org.guvnor.common.services.shared.metadata.model.Metadata;
 import org.junit.Before;
 import org.junit.Test;
 import org.junit.runner.RunWith;
+import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
 import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
 import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
 import org.mockito.InjectMocks;
@@ -49,7 +53,7 @@ public class KieEditorTest {
 
     @Spy
     @InjectMocks
-    protected FileMenuBuilderImpl fileMenuBuilder;
+    protected FileMenuBuilder<String, Metadata> fileMenuBuilder;
 
     @Mock
     protected ProjectController projectController;
@@ -67,11 +71,11 @@ public class KieEditorTest {
     @InjectMocks
     protected AssetUpdateValidator assetUpdateValidator;
 
-    protected KieEditor presenter;
+    protected KieEditor<String> presenter;
 
     @Before
     public void setup() {
-        presenter = spy(new KieEditor() {
+        presenter = spy(new KieEditor<String>() {
             {
                 fileMenuBuilder = KieEditorTest.this.fileMenuBuilder;
                 projectController = KieEditorTest.this.projectController;
@@ -87,6 +91,11 @@ public class KieEditorTest {
 
             }
 
+            @Override
+            protected Supplier<String> getContentSupplier() {
+                return null;
+            }
+
             @Override
             protected void onSave() {
 
diff --git a/kie-wb-common-widgets/kie-wb-metadata-widget/src/test/java/org/kie/workbench/common/widgets/metadata/client/KieMultipleDocumentEditorTestBase.java b/kie-wb-common-widgets/kie-wb-metadata-widget/src/test/java/org/kie/workbench/common/widgets/metadata/client/KieMultipleDocumentEditorTestBase.java
index fa1557a9c..78dc17cdb 100644
--- a/kie-wb-common-widgets/kie-wb-metadata-widget/src/test/java/org/kie/workbench/common/widgets/metadata/client/KieMultipleDocumentEditorTestBase.java
+++ b/kie-wb-common-widgets/kie-wb-metadata-widget/src/test/java/org/kie/workbench/common/widgets/metadata/client/KieMultipleDocumentEditorTestBase.java
@@ -19,6 +19,7 @@ import java.lang.reflect.Field;
 
 import org.guvnor.common.services.project.client.security.ProjectController;
 import org.guvnor.common.services.project.context.ProjectContext;
+import org.guvnor.common.services.shared.metadata.model.Metadata;
 import org.guvnor.common.services.shared.metadata.model.Overview;
 import org.junit.Before;
 import org.kie.soup.project.datamodel.imports.Imports;
@@ -41,6 +42,7 @@ import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
 import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
 import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilderImpl;
 import org.uberfire.ext.editor.commons.client.menu.RestoreVersionCommandProvider;
+import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandFactory;
 import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
 import org.uberfire.ext.editor.commons.version.VersionService;
 import org.uberfire.ext.editor.commons.version.events.RestoreEvent;
@@ -52,8 +54,11 @@ import org.uberfire.mvp.PlaceRequest;
 import org.uberfire.workbench.events.NotificationEvent;
 import org.uberfire.workbench.model.menu.MenuItem;
 
-import static org.junit.Assert.*;
-import static org.mockito.Mockito.*;
+import static org.junit.Assert.fail;
+import static org.mockito.Mockito.any;
+import static org.mockito.Mockito.mock;
+import static org.mockito.Mockito.spy;
+import static org.mockito.Mockito.when;
 
 abstract class KieMultipleDocumentEditorTestBase {
 
@@ -102,6 +107,9 @@ abstract class KieMultipleDocumentEditorTestBase {
     @Mock
     private EventSourceMock<NotificationEvent> notification;
 
+    @Mock
+    private SaveAndRenameCommandFactory<String, Metadata> saveAndRenameCommandFactory;
+
     @Spy
     protected RestoreVersionCommandProvider restoreVersionCommandProvider = getRestoreVersionCommandProvider();
 
@@ -223,12 +231,13 @@ abstract class KieMultipleDocumentEditorTestBase {
     }
 
     private BasicFileMenuBuilder getBasicFileMenuBuilder() {
-        final BasicFileMenuBuilder basicFileMenuBuilder = new BasicFileMenuBuilderImpl(deletePopUpPresenter,
-                                                                                       copyPopUpPresenter,
-                                                                                       renamePopUpPresenter,
-                                                                                       busyIndicatorView,
-                                                                                       notification,
-                                                                                       restoreVersionCommandProvider);
+        final BasicFileMenuBuilder<String, Metadata> basicFileMenuBuilder = new BasicFileMenuBuilderImpl<>(deletePopUpPresenter,
+                                                                                                           copyPopUpPresenter,
+                                                                                                           renamePopUpPresenter,
+                                                                                                           busyIndicatorView,
+                                                                                                           notification,
+                                                                                                           restoreVersionCommandProvider,
+                                                                                                           saveAndRenameCommandFactory);
         setField(basicFileMenuBuilder,
                  "restoreVersionCommandProvider",
                  restoreVersionCommandProvider);
