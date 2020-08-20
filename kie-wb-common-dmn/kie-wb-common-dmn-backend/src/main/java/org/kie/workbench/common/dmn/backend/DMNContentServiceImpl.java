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

package org.kie.workbench.common.dmn.backend;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.dmn.api.DMNContentService;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class DMNContentServiceImpl extends KieService<String> implements DMNContentService {

    @Inject
    private CommentedOptionFactory commentedOptionFactory;

    @Override
    public String getContent(final Path path) {
        return getSource(path);
    }

    @Override
    public void saveContent(final Path path,
                            final String content,
                            final Metadata metadata,
                            final String comment) {

        try {
            ioService.write(Paths.convert(path),
                            content,
                            commentedOptionFactory.makeCommentedOption(comment));
        } catch (final Exception e) {
            logger.error("Error while saving diagram.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String constructContent(final Path path,
                                      final Overview overview) {
        return getSource(path);
    }
}
