/*
 * Copyright 2017 Ravi Chaturvedi
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
package io.retrier.handler.limit;


import io.retrier.handler.Handler;
import io.retrier.utils.Preconditions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


public class CompositeLimitHandler implements LimitHandler {

    private final List<Handler> handlers;

    public CompositeLimitHandler(LimitHandler... limitHandlers) {
        Stream.of(limitHandlers).forEach(handler -> Preconditions.ensureNotNull(handler, "LimitHandler cannot be null."));
        this.handlers = Collections.unmodifiableList(Arrays.asList(limitHandlers));
    }

    @Override
    public void handlePreExec() {
        handlers.forEach(Handler::handlePreExec);
    }

    @Override
    public void handleException(Exception e) throws Exception {
        // Make sure all the limit checks are successful.
        for (Handler handler : handlers) {
            handler.handleException(e);
        }
    }
}
