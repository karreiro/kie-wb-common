/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.showcase.client.alternatives;

public class DMNClientModels {

    public static final String BASE_FILE = "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<dmn:definitions xmlns:dmn=\"http://www.omg.org/spec/DMN/20180521/MODEL/\" xmlns=\"https://kiegroup.org/dmn/_C314453D-065F-4E41-920B-3B2E6E39A258\" xmlns:di=\"http://www.omg.org/spec/DMN/20180521/DI/\" xmlns:kie=\"http://www.drools.org/kie/dmn/1.2\" xmlns:feel=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" xmlns:dmndi=\"http://www.omg.org/spec/DMN/20180521/DMNDI/\" xmlns:dc=\"http://www.omg.org/spec/DMN/20180521/DC/\" id=\"_F17400FA-111C-4F39-A2A2-74B624F1B7E6\" name=\"Base Model\" expressionLanguage=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" typeLanguage=\"http://www.omg.org/spec/DMN/20180521/FEEL/\" namespace=\"https://kiegroup.org/dmn/_C314453D-065F-4E41-920B-3B2E6E39A258\">&#xd;\n" +
            "  <dmn:extensionElements/>&#xd;\n" +
            "  <dmn:itemDefinition id=\"_F0729FCC-906F-43BF-BDC8-7ABBD096E2E5\" name=\"tMyCustomDataType\" isCollection=\"false\">&#xd;\n" +
            "    <dmn:itemComponent id=\"_3E5B0592-B4C3-4E97-BDC9-189BCC155EBB\" name=\"customProperty\" isCollection=\"false\">&#xd;\n" +
            "      <dmn:typeRef>string</dmn:typeRef>&#xd;\n" +
            "    </dmn:itemComponent>&#xd;\n" +
            "  </dmn:itemDefinition>&#xd;\n" +
            "  <dmn:inputData id=\"_8245A539-04DA-4F79-B11A-0E3ED4869F93\" name=\"My Input\">&#xd;\n" +
            "    <dmn:extensionElements/>&#xd;\n" +
            "    <dmn:variable id=\"_977104AF-D216-4610-9970-2019F6AADEF7\" name=\"My Input\" typeRef=\"tMyCustomDataType\"/>&#xd;\n" +
            "  </dmn:inputData>&#xd;\n" +
            "  <dmndi:DMNDI>&#xd;\n" +
            "    <dmndi:DMNDiagram>&#xd;\n" +
            "      <di:extension>&#xd;\n" +
            "        <kie:ComponentsWidthsExtension/>&#xd;\n" +
            "      </di:extension>&#xd;\n" +
            "      <dmndi:DMNShape id=\"dmnshape-_8245A539-04DA-4F79-B11A-0E3ED4869F93\" dmnElementRef=\"_8245A539-04DA-4F79-B11A-0E3ED4869F93\" isCollapsed=\"false\">&#xd;\n" +
            "        <dmndi:DMNStyle>&#xd;\n" +
            "          <dmndi:FillColor red=\"255\" green=\"255\" blue=\"255\"/>&#xd;\n" +
            "          <dmndi:StrokeColor red=\"0\" green=\"0\" blue=\"0\"/>&#xd;\n" +
            "          <dmndi:FontColor red=\"0\" green=\"0\" blue=\"0\"/>&#xd;\n" +
            "        </dmndi:DMNStyle>&#xd;\n" +
            "        <dc:Bounds x=\"251\" y=\"174\" width=\"100\" height=\"50\"/>&#xd;\n" +
            "        <dmndi:DMNLabel/>&#xd;\n" +
            "      </dmndi:DMNShape>&#xd;\n" +
            "    </dmndi:DMNDiagram>&#xd;\n" +
            "  </dmndi:DMNDI>&#xd;\n" +
            "</dmn:definitions>";

    public static final String MODEL_WITH_IMPORTS = "<!!!invalid!!!>";
}
