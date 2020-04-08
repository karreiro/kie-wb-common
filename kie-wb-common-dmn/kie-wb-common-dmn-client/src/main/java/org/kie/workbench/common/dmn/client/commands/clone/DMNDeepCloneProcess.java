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
package org.kie.workbench.common.dmn.client.commands.clone;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import org.apache.http.client.utils.CloneUtils;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.clone.DeepCloneProcess;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.ClassUtils;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@Alternative
public class DMNDeepCloneProcess extends DeepCloneProcess {
    protected DMNDeepCloneProcess() {
        this(null,
             null,
             null);
    }

    @Inject
    public DMNDeepCloneProcess(final FactoryManager factoryManager,
                            final AdapterManager adapterManager,
                            final ClassUtils classUtils) {
        super(factoryManager, adapterManager, classUtils);
    }

    @Override
    public <S, T> T clone(S source,
                          T target) {
        DomGlobal.console.log("Here we are!!!");
        final Node<View, Edge> sourceNode = (Node<View, Edge>) source;
        final Node<View, Edge> targetNode = (Node<View, Edge>) target;
        Object sourceDefinition = DefinitionUtils.getElementDefinition(sourceNode);
        try {
            Object targetDefinition = CloneUtils.clone(sourceDefinition);
            targetNode.getContent().setDefinition(targetDefinition);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return target;
    }
}
