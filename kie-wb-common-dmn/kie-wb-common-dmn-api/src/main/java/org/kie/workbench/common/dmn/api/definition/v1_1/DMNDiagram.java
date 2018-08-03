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
package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.definition.DMNDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class)
@FormDefinition(policy = FieldPolicy.ONLY_MARKED, defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)})
@CanContain(roles = {
        "input-data",
        "knowledge-source",
        "business-knowledge-model",
        "decision",
        "text-annotation",
        "association",
        "information-requirement",
        "knowledge-requirement",
        "authority-requirement"
})
public class DMNDiagram extends DMNModelInstrumentedBase implements DMNDefinition {

    @Category
    public static final transient String stunnerCategory = Categories.DIAGRAM;

    @Labels
    public static final Set<String> stunnerLabels = new HashSet<String>() {{
        add("dmn_diagram");
    }};

    protected Definitions definitions;

    @Valid
    @Property
    protected Id id;

    public DMNDiagram() {
        this(new Id(),
             new Definitions());
    }

    public DMNDiagram(final @MapsTo("id") Id id,
                      final @MapsTo("definitions") Definitions definitions) {
        this.id = id;
        this.definitions = definitions;
    }

    public Id getId() {
        return id;
    }

    public void setId(final Id id) {
        this.id = id;
    }

    public Definitions getDefinitions() {
        return definitions;
    }

    public void setDefinitions(final Definitions definitions) {
        this.definitions = definitions;
    }

    public String getStunnerCategory() {
        return stunnerCategory;
    }

    public Set<String> getStunnerLabels() {
        return stunnerLabels;
    }
}
