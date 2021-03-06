/*
 * Copyright 2017 The Retrier AUTHORS.
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
package io.github.ravichaturvedi.retrier.handler.limit;

import io.github.ravichaturvedi.retrier.Handler;
import io.github.ravichaturvedi.retrier.handler.Traceable;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import static io.github.ravichaturvedi.retrier.helper.Ensurer.ensure;

/**
 * {@link TimeoutHandler} is a {@link Handler} implementation to make sure retry is happening within the max timeout provided.
 *
 * Since available time is computed on the calling thread when the Exception occurs so is not `exact`.
 */
public class TimeoutHandler extends Traceable implements Handler {

    // Timeout available for the handler.
    private final long timeoutInMillisec;

    // Keeping track of the start time in millisecond.
    private final AtomicLong startTimeInMillisec;

    public TimeoutHandler(long timeoutInMillisec) {
        ensure(timeoutInMillisec > 0, "Timeout should be positive.");
        this.timeoutInMillisec = timeoutInMillisec;
        this.startTimeInMillisec = new AtomicLong(0);
    }

    @Override
    public void handlePreExec() {
        startTimeInMillisec.compareAndSet(0, System.currentTimeMillis());
    }

    @Override
    public void handleException(Exception e) throws Exception {
        long elapsedTimeInMillisec = System.currentTimeMillis() - startTimeInMillisec.get();
        if (elapsedTimeInMillisec > timeoutInMillisec) {
            trace(() -> String.format("Exceeded Timeout of %s", Duration.ofMillis(timeoutInMillisec)));
            throw e;
        }

        trace(() -> String.format("Remaining time: %s", Duration.ofMillis(timeoutInMillisec - elapsedTimeInMillisec)));
    }
}
