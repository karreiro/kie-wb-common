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

package org.kie.workbench.common.dmn.showcase.client.backward.compatibility;

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

public class DMNDesignerBackwardCompatibilitySeleniumIT {

    private static final Logger LOG = LoggerFactory.getLogger(DMNDesignerBackwardCompatibilitySeleniumIT.class);

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
        executeDMNTestCase("dmn11", "0001-filter.dmn");
    }

    @Test
    public void testOpen_dmn11_0001_input_data_string() throws Exception {
        executeDMNTestCase("dmn11", "0001-input-data-string.dmn");
    }

    @Test
    public void testOpen_dmn11_0002_input_data_number() throws Exception {
        executeDMNTestCase("dmn11", "0002-input-data-number.dmn");
    }

    @Test
    public void testOpen_dmn11_0002_string_functions() throws Exception {
        executeDMNTestCase("dmn11", "0002-string-functions.dmn");
    }

    @Test
    public void testOpen_dmn11_0003_input_data_string_allowed_values() throws Exception {
        executeDMNTestCase("dmn11", "0003-input-data-string-allowed-values.dmn");
    }

    @Test
    public void testOpen_dmn11_0003_iteration() throws Exception {
        executeDMNTestCase("dmn11", "0003-iteration.dmn");
    }

    @Test
    public void testOpen_dmn11_0004_lending() throws Exception {
        executeDMNTestCase("dmn11", "0004-lending.dmn");
    }

    @Test
    public void testOpen_dmn11_0004_simpletable_U() throws Exception {
        executeDMNTestCase("dmn11", "0004-simpletable-U.dmn");
    }

    @Test
    public void testOpen_dmn11_0005_literal_invocation() throws Exception {
        executeDMNTestCase("dmn11", "0005-literal-invocation.dmn");
    }

    @Test
    public void testOpen_dmn11_0005_simpletable_A() throws Exception {
        executeDMNTestCase("dmn11", "0005-simpletable-A.dmn");
    }

    @Test
    public void testOpen_dmn11_0006_join() throws Exception {
        executeDMNTestCase("dmn11", "0006-join.dmn");
    }

    @Test
    public void testOpen_dmn11_0006_simpletable_P1() throws Exception {
        executeDMNTestCase("dmn11", "0006-simpletable-P1.dmn");
    }

    @Test
    public void testOpen_dmn11_0007_date_time() throws Exception {
        executeDMNTestCase("dmn11", "0007-date-time.dmn");
    }

    @Test
    public void testOpen_dmn11_0007_simpletable_P2() throws Exception {
        executeDMNTestCase("dmn11", "0007-simpletable-P2.dmn");
    }

    @Test
    public void testOpen_dmn11_0008_listGen() throws Exception {
        executeDMNTestCase("dmn11", "0008-listGen.dmn");
    }

    @Test
    public void testOpen_dmn11_0008_LX_arithmetic() throws Exception {
        executeDMNTestCase("dmn11", "0008-LX-arithmetic.dmn");
    }

    @Test
    public void testOpen_dmn11_0009_append_flatten() throws Exception {
        executeDMNTestCase("dmn11", "0009-append-flatten.dmn");
    }

    @Test
    public void testOpen_dmn11_0009_invocation_arithmetic() throws Exception {
        executeDMNTestCase("dmn11", "0009-invocation-arithmetic.dmn");
    }

    @Test
    public void testOpen_dmn11_0010_concatenate() throws Exception {
        executeDMNTestCase("dmn11", "0010-concatenate.dmn");
    }

    @Test
    public void testOpen_dmn11_0010_multi_output_U() throws Exception {
        executeDMNTestCase("dmn11", "0010-multi-output-U.dmn");
    }

    @Test
    public void testOpen_dmn11_0011_insert_remove() throws Exception {
        executeDMNTestCase("dmn11", "0011-insert-remove.dmn");
    }

    @Test
    public void testOpen_dmn11_0012_list_functions() throws Exception {
        executeDMNTestCase("dmn11", "0012-list-functions.dmn");
    }

    @Test
    public void testOpen_dmn11_0013_sort() throws Exception {
        executeDMNTestCase("dmn11", "0013-sort.dmn");
    }

    @Test
    public void testOpen_dmn11_0014_loan_comparison() throws Exception {
        executeDMNTestCase("dmn11", "0014-loan-comparison.dmn");
    }

    @Test
    public void testOpen_dmn11_0015_all_any() throws Exception {
        executeDMNTestCase("dmn11", "0015-all-any.dmn");
    }

    @Test
    public void testOpen_dmn11_0016_some_every() throws Exception {
        executeDMNTestCase("dmn11", "0016-some-every.dmn");
    }

    @Test
    public void testOpen_dmn11_0017_tableTests() throws Exception {
        executeDMNTestCase("dmn11", "0017-tableTests.dmn");
    }

    @Test
    public void testOpen_dmn11_0019_flight_rebooking() throws Exception {
        executeDMNTestCase("dmn11", "0019-flight-rebooking.dmn");
    }

    @Test
    public void testOpen_dmn12_0001_filter() throws Exception {
        executeDMNTestCase("dmn12", "0001-filter.dmn");
    }

    @Test
    public void testOOO() throws Exception {
        executeDMNTestCase("dmn12", "0001-filter.dmn");
    }
//
//    @Test
//    public void testOpen_dmn12_0001_input_data_string() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0002_input_data_number() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0002_string_functions() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0003_input_data_string_allowed_values() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0003_iteration() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0004_lending() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0004_simpletable_U() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0005_literal_invocation() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0005_simpletable_A() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0006_join() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0006_simpletable_P1() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0007_date_time() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0007_simpletable_P2() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0008_listGen() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0008_LX_arithmetic() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0009_append_flatten() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0009_invocation_arithmetic() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0010_concatenate() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0010_multi_output_U() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0011_insert_remove() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0012_list_functions() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0013_sort() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0014_loan_comparison() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0016_some_every() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0017_tableTests() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0020_vacation_days() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0021_singleton_list() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0030_user_defined_functions() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0031_user_defined_functions() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0032_conditionals() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0033_for_loops() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0034_drg_scopes() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0035_test_structure_output() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0036_dt_variable_input() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0037_dt_on_bkm_implicit_params() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0038_dt_on_bkm_explicit_params() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0039_dt_list_semantics() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0040_singlenestedcontext() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0041_multiple_nestedcontext() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0100_feel_constants() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0101_feel_constants() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0102_feel_constants() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0105_feel_math() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0106_feel_ternary_logic() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0107_feel_ternary_logic_not() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0108_first_hitpolicy() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0109_ruleOrder_hitpolicy() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0110_outputOrder_hitpolicy() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0111_first_hitpolicy_singleoutputcol() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0112_ruleOrder_hitpolicy_singleinoutcol() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0113_outputOrder_hitpolicy_singleinoutcol() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0114_min_collect_hitpolicy() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0115_sum_collect_hitpolicy() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0116_count_collect_hitpolicy() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0117_multi_any_hitpolicy() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0118_multi_priority_hitpolicy() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_0119_multi_collect_hitpolicy() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_1100_feel_decimal_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_1101_feel_floor_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_1102_feel_ceiling_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_1103_feel_substring_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_1104_feel_string_length_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_1105_feel_upper_case_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_1106_feel_lower_case_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_1107_feel_substring_before_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_1108_feel_substring_after_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_1109_feel_replace_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_1110_feel_contains_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_1115_feel_date_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_1116_feel_time_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_1117_feel_date_and_time_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_1120_feel_duration_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn12_1121_feel_years_and_months_duration_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0001_filter() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0001_input_data_string() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0002_input_data_number() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0002_string_functions() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0003_input_data_string_allowed_values() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0003_iteration() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0004_lending() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0004_simpletable_U() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0005_literal_invocation() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0005_simpletable_A() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0006_join() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0006_simpletable_P1() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0007_date_time() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0007_simpletable_P2() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0008_listGen() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0008_LX_arithmetic() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0009_append_flatten() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0009_invocation_arithmetic() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0010_concatenate() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0010_multi_output_U() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0011_insert_remove() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0012_list_functions() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0013_sort() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0014_loan_comparison() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0016_some_every() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0017_tableTests() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0020_vacation_days() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0021_singleton_list() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0030_user_defined_functions() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0031_user_defined_functions() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0032_conditionals() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0033_for_loops() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0034_drg_scopes() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0035_test_structure_output() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0036_dt_variable_input() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0037_dt_on_bkm_implicit_params() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0038_dt_on_bkm_explicit_params() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0039_dt_list_semantics() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0040_singlenestedcontext() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0041_multiple_nestedcontext() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0050_feel_abs_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0051_feel_sqrt_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0052_feel_exp_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0053_feel_log_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0054_feel_even_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0055_feel_odd_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0056_feel_modulo_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0057_feel_context() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0058_feel_number_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0059_feel_all_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0060_feel_any_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0061_feel_median_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0062_feel_mode_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0063_feel_stddev_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0064_feel_conjunction() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0065_feel_disjunction() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0066_feel_negation() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0067_feel_split_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0068_feel_equality() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0069_feel_list() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0070_feel_instance_of() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0071_feel_between() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0072_feel_in() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0073_feel_comments() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0074_feel_properties() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0075_feel_exponent() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0076_feel_external_java() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0077_feel_nan() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0078_feel_infinity() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0080_feel_getvalue_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0081_feel_getentries_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0082_feel_coercion() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0083_feel_unicode() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0084_feel_for_loops() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0085_decision_services() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0086_import() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0087_chapter_11_example() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0088_no_decision_logic() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0089_nested_inputdata_imports() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0090_feel_paths() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0100_feel_constants() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0101_feel_constants() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0102_feel_constants() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0105_feel_math() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0106_feel_ternary_logic() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0107_feel_ternary_logic_not() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0108_first_hitpolicy() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0109_ruleOrder_hitpolicy() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0110_outputOrder_hitpolicy() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0111_first_hitpolicy_singleoutputcol() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0112_ruleOrder_hitpolicy_singleinoutcol() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0113_outputOrder_hitpolicy_singleinoutcol() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0114_min_collect_hitpolicy() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0115_sum_collect_hitpolicy() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0116_count_collect_hitpolicy() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0117_multi_any_hitpolicy() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0118_multi_priority_hitpolicy() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_0119_multi_collect_hitpolicy() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_1100_feel_decimal_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_1101_feel_floor_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_1102_feel_ceiling_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_1103_feel_substring_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_1104_feel_string_length_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_1105_feel_upper_case_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_1106_feel_lower_case_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_1107_feel_substring_before_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_1108_feel_substring_after_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_1109_feel_replace_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_1110_feel_contains_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_1115_feel_date_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_1116_feel_time_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_1117_feel_date_and_time_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_1120_feel_duration_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_1121_feel_years_and_months_duration_function() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_Imported_Model() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_Model_B() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_Model_B2() throws Exception {
//    }
//
//    @Test
//    public void testOpen_dmn13_Say_hello_1ID1D() throws Exception {
//    }

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
