/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.common;

/**
 * Exception raised when something bad happens when calling methods from Providers.
 */
public class ProviderException extends Exception {

    public ProviderException() {
    }

    public ProviderException(final String message) {
        super(message);
    }

    public ProviderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ProviderException(final Throwable cause) {
        super(cause);
    }

    public ProviderException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
