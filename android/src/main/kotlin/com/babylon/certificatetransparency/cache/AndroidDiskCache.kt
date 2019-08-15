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

package com.babylon.certificatetransparency.cache

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.babylon.certificatetransparency.loglist.RawLogListResult
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.IOException
import java.util.Date
import kotlin.coroutines.CoroutineContext

/**
 * A default log list and signature cache implementation for Android.
 *
 * The private storage directory of the application is used to store the log list and its signature.
 * The last write date is stored alongside in shared preferences in order to track cache expiry.
 */
class AndroidDiskCache @JvmOverloads constructor(
        context: Context,
        private val diskCachePolicy: DiskCachePolicy = DefaultDiskCachePolicy()
) : DiskCache {
    private val cacheDirPath = "${context.filesDir.path}/cache/certificate-transparency-android"
    private val prefs = context.getSharedPreferences("certificate-transparency", MODE_PRIVATE)

    override suspend fun get(): RawLogListResult? {
        return try {

            val jsonFile = File(cacheDirPath, LOG_LIST_FILE)
            val sigFile = File(cacheDirPath, SIG_FILE)
            val logList = jsonFile.readText()
            val signature = sigFile.readBytes()

            val result = RawLogListResult.Success(logList, signature)

            if (isValid(result)) {
                result
            } else {
                jsonFile.delete()
                sigFile.delete()

                prefs.edit()
                        .clear()
                        .apply()
                null
            }
        } catch (e: IOException) {
            null
        }
    }

    override suspend fun set(value: RawLogListResult) {
        if (value is RawLogListResult.Success) {
            try {
                File(cacheDirPath).mkdirs()

                val jsonFile = File(cacheDirPath, LOG_LIST_FILE)
                jsonFile.writeText(value.logList)
                val sigFile = File(cacheDirPath, SIG_FILE)
                sigFile.writeBytes(value.signature)

                prefs.edit()
                        .putLong(LAST_WRITE, System.currentTimeMillis())
                        .apply()
            } catch (e: IOException) {
                // non fatal
            }
        }
    }

    override suspend fun isValid(value: RawLogListResult?): Boolean {
        return value is RawLogListResult.Success &&
                !diskCachePolicy.isExpired(
                        lastWriteDate = Date(prefs.getLong(LAST_WRITE, System.currentTimeMillis())),
                        currentDate = Date()
                )
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    companion object {
        const val LOG_LIST_FILE = "loglist.json"
        const val SIG_FILE = "loglist.sig"
        const val LAST_WRITE = "last_write"
    }
}