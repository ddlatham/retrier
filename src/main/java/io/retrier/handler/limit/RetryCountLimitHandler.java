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


import io.retrier.handler.AbstractTraceable;
import io.retrier.utils.Preconditions;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * {@link RetryCountLimitHandler} is a {@link LimitHandler} implementation to make sure retry is happening within the max retries limit.
 */
public class RetryCountLimitHandler extends AbstractTraceable implements LimitHandler {

    private final int maxRetries;
    private final AtomicInteger retryCount;

    public RetryCountLimitHandler(int maxRetries) {
        Preconditions.ensure(maxRetries > 0, "Max retry count should be positive.");
        this.maxRetries = maxRetries;
        this.retryCount = new AtomicInteger();
    }

    @Override
    public void handlePreExec() {
        retryCount.incrementAndGet();
    }

    @Override
    public void handleException(Exception e) throws Exception {
        if (retryCount.get() >= maxRetries) {
            trace(() -> String.format("Exceeded Max Retries: %s", maxRetries));
            throw e;
        }

        trace(() -> String.format("Retry Count: %s/%s", retryCount.get() + 1, maxRetries));
    }
}
