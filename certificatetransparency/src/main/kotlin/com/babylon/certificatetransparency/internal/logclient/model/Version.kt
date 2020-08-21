/*
 * Copyright 2019 Babylon Partners Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.babylon.certificatetransparency.internal.logclient.model

/**
 * @property UNKNOWN_VERSION Not part of the I-D, and outside the valid range.
 */
// Numbers part of specification
@Suppress("MagicNumber")
internal enum class Version(val number: Int) {
    V1(0),
    UNKNOWN_VERSION(256);

    companion object {
        fun forNumber(number: Int) = values().firstOrNull { it.number == number } ?: UNKNOWN_VERSION
    }
}
