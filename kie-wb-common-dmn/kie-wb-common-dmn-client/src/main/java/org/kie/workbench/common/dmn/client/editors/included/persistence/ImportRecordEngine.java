/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.included.persistence;

import java.util.List;
import java.util.Objects;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.Import;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.common.persistence.RecordEngine;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageState;
import org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsIndex;
import org.kie.workbench.common.dmn.client.editors.included.messages.IncludedModelErrorMessageFactory;

import static java.util.Collections.singletonList;

public class ImportRecordEngine implements RecordEngine<IncludedModel> {

    private final IncludedModelsPageState pageState;

    private final IncludedModelsIndex includedModelsIndex;

    private final IncludedModelErrorMessageFactory messageFactory;

    private final Event<FlashMessage> flashMessageEvent;

    @Inject
    public ImportRecordEngine(final IncludedModelsPageState pageState,
                              final IncludedModelsIndex includedModelsIndex,
                              final IncludedModelErrorMessageFactory messageFactory,
                              final Event<FlashMessage> flashMessageEvent) {
        this.pageState = pageState;
        this.includedModelsIndex = includedModelsIndex;
        this.messageFactory = messageFactory;
        this.flashMessageEvent = flashMessageEvent;
    }

    @Override
    public List<IncludedModel> update(final IncludedModel record) {
        if (!record.isValid()) {
            throw new UnsupportedOperationException("An invalid Included Model cannot be updated.");
        }
        getImport(record).setName(new Name(record.getName()));
        return singletonList(record);
    }

    @Override
    public List<IncludedModel> destroy(final IncludedModel record) {
        pageState.getImports().remove(getImport(record));
        return singletonList(record);
    }

    @Override
    public List<IncludedModel> create(final IncludedModel record) {
        // TODO
        return singletonList(record);
    }

    @Override
    public boolean isValid(final IncludedModel record) {
        final boolean isUnique = isUnique(record);
        if (!isUnique) {
            flashMessageEvent.fire(messageFactory.getNameIsNotUniqueFlashMessage(record));
        }
        return isUnique;
    }

    private boolean isUnique(final IncludedModel record) {
        return pageState
                .getImports()
                .stream()
                .noneMatch(anImport -> !sameImport(record, anImport) && sameName(record, anImport));
    }

    private boolean sameName(final IncludedModel record, final Import anImport) {
        return Objects.equals(record.getName(), anImport.getName().getValue());
    }

    private boolean sameImport(final IncludedModel record, final Import anImport) {
        final Import recordImport = getImport(record);
        return Objects.equals(recordImport, anImport);
    }

    private Import getImport(final IncludedModel record) {
        return includedModelsIndex.getImport(record);
    }
}
