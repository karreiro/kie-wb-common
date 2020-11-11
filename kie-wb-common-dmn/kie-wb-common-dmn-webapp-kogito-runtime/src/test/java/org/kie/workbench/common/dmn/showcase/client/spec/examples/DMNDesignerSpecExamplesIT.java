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

package org.kie.workbench.common.dmn.showcase.client.spec.examples;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlunit.assertj.XmlAssert;

import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.copyFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class DMNDesignerSpecExamplesIT {

    private static final Logger LOG = LoggerFactory.getLogger(DMNDesignerSpecExamplesIT.class);

    private static final Boolean HEADLESS = Boolean.valueOf(System.getProperty("org.kie.dmn.kogito.browser.headless"));

    private static final String INDEX_HTML = "target/kie-wb-common-dmn-webapp-kogito-runtime/index.html";

    private static final String INDEX_HTML_PATH = "file:///" + new File(INDEX_HTML).getAbsolutePath();

    private static final String SCREENSHOTS_DIR = System.getProperty("org.kie.dmn.kogito.screenshots.dir");

    private static final String DECISON_NAVIGATOR_EXPAND = "qe-docks-item-W-org.kie.dmn.decision.navigator";

    private static final String PROPERTIES_PANEL = "qe-docks-item-E-DiagramEditorPropertiesScreen";

    private static final String SET_CONTENT_TEMPLATE =
            "gwtEditorBeans.get(\"DMNDiagramEditor\").get().setContent(\"\",\'%s\')";

    private static final String GET_CONTENT_TEMPLATE =
            "return gwtEditorBeans.get(\"DMNDiagramEditor\").get().getContent()";

    private final File screenshotDirectory = initScreenshotDirectory();

    private WebDriver driver;

    private WebElement decisionNavigatorExpandButton;

    private WebElement propertiesPanel;

    @BeforeClass
    public static void setupClass() {
        WebDriverManager.firefoxdriver().setup();
    }

    @Before
    public void openDMNDesigner() {

        final FirefoxOptions firefoxOptions = new FirefoxOptions();
//        firefoxOptions.setHeadless(HEADLESS);
        driver = new FirefoxDriver(firefoxOptions);
        driver.manage().window().maximize();

        driver.get(INDEX_HTML_PATH);

        decisionNavigatorExpandButton = waitOperation()
                .withMessage("Presence of decision navigator expand button is prerequisite for all tests")
                .until(visibilityOfElementLocated(className(DECISON_NAVIGATOR_EXPAND)));

        propertiesPanel = waitOperation()
                .withMessage("Presence of properties panel expand button is prerequisite for all tests")
                .until(visibilityOfElementLocated(className(PROPERTIES_PANEL)));
    }

    @Rule
    public TestWatcher takeScreenShotAndCleanUp = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            final File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            final String testClassName = description.getTestClass().getSimpleName();
            final String testMethodName = description.getMethodName();
            final String filename = testClassName + "_" + testMethodName;
            try {
                copyFile(screenshotFile, new File(screenshotDirectory, filename + ".png"));
            } catch (IOException ioe) {
                LOG.error("Unable to take screenshot", ioe);
            }
        }

        @Override
        protected void finished(Description description) {
            if (driver != null) {
                driver.quit();
            }
        }
    };

    @Test
    public void testOpen_dmn11_0001_filter() throws Exception {
        executeDMNTestCase("11", "11.dmn");
    }

    private void executeDMNTestCase(final String directory,
                                    final String file) throws IOException {
        final String expected = loadResource(directory + "-expected/" + file);
        final List<String> ignoredAttributes = asList("id", "dmnElementRef");

        setContent(loadResource(directory + "/" + file));

        final String actual = getContent();
        assertThat(actual).isNotBlank();

        XmlAssert.assertThat(actual)
                .and(expected)
                .ignoreComments()
                .ignoreWhitespace()
                .withAttributeFilter(attr -> !ignoredAttributes.contains(attr.getName()))
                .areSimilar();
    }

    /**
     * Use this for loading DMN model placed in src/test/resources
     * @param filename
     * @return Text content of the file
     * @throws IOException
     */
    private String loadResource(final String filename) throws IOException {
        return IOUtils.readLines(this.getClass().getResourceAsStream(filename), StandardCharsets.UTF_8)
                .stream()
                .collect(Collectors.joining(""));
    }

    private void setContent(final String xml) {
        ((JavascriptExecutor) driver).executeScript(String.format(SET_CONTENT_TEMPLATE, xml));
        waitOperation()
                .withMessage("Designer was not loaded")
                .until(visibilityOfElementLocated(className("uf-multi-page-editor")));
    }

    private String getContent() {
        final Object result = ((JavascriptExecutor) driver).executeScript(String.format(GET_CONTENT_TEMPLATE));
        assertThat(result).isInstanceOf(String.class);
        return (String) result;
    }

    private WebDriverWait waitOperation() {
        return new WebDriverWait(driver, Duration.ofSeconds(10).getSeconds());
    }

    private File initScreenshotDirectory() {
        if (SCREENSHOTS_DIR == null) {
            throw new IllegalStateException(
                    "Property org.kie.dmn.kogito.screenshots.dir (where screenshot taken by WebDriver will be put) was null");
        }
        File scd = new File(SCREENSHOTS_DIR);
        if (!scd.exists()) {
            boolean mkdirSuccess = scd.mkdir();
            if (!mkdirSuccess) {
                throw new IllegalStateException("Creation of screenshots dir failed " + scd);
            }
        }
        if (!scd.canWrite()) {
            throw new IllegalStateException("The screenshotDir must be writable" + scd);
        }
        return scd;
    }
}
