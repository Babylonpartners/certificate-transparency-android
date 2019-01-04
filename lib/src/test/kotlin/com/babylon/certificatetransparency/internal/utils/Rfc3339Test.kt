/*
 * Copyright 2018 Babylon Healthcare Services Limited
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

package com.babylon.certificatetransparency.internal.utils

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class Rfc3339Test {

    @get:Rule
    var thrown: ExpectedException = ExpectedException.none()

    @Parameterized.Parameter(0)
    lateinit var input: String

    @Parameterized.Parameter(1)
    lateinit var expected: String


    @Test
    fun test() {
        if (expected == "fail") {
            thrown.expect(NumberFormatException::class.java)
        }

        assertEquals(expected.toLong(), input.toRfc3339Long())
    }

    companion object {

        @JvmStatic
        @Parameterized.Parameters(name = "{0} -> {1}")
        fun data() = arrayOf(
            // success cases
            arrayOf("2018-04-16T10:04:55Z", "1523873095000"),
            arrayOf("2012-11-06T12:10:44.000-08:00", "1352232644000"),
            arrayOf("2012-11-06T16:10:44.000-04:00", "1352232644000"),
            arrayOf("2012-11-06T17:10:44.000-03:00", "1352232644000"),
            arrayOf("2012-11-06T20:10:44.001Z", "1352232644001"),
            arrayOf("2012-11-06T20:10:44.01Z", "1352232644010"),
            arrayOf("2012-11-06T20:10:44.1Z", "1352232644100"),
            arrayOf("2012-11-06", "1352160000000"),

            // failure cases
            arrayOf("abc", "fail"),
            arrayOf("2013-01-01 09:00:02", "fail"),
            // missing time
            arrayOf("2013-01-01T", "fail"),
            // invalid month
            arrayOf("1937--3-55T12:00:27+00:20", "fail"),
            // can't have time zone shift without time
            arrayOf("2013-01-01Z", "fail")
        )
    }
}