package com.babylon.certificatetransparency.internal.loglist.parser

import com.babylon.certificatetransparency.internal.loglist.JsonFormat
import com.babylon.certificatetransparency.internal.utils.Base64
import com.babylon.certificatetransparency.loglist.LogListResult
import com.babylon.certificatetransparency.utils.TestData
import com.babylon.certificatetransparency.utils.assertIsA
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class LogListJsonParserV1Test {


    @Test
    fun `verifies signature`() = runBlocking {
        // given we have a valid json file and signature

        // when we ask for data
        val result = LogListJsonParserV1().parseJson(json)

        // then 3 items are returned (many ignored as invalid states)
        require(result is LogListResult.Valid)
        assertEquals(32, result.servers.size)
        assertEquals("pFASaQVaFVReYhGrN7wQP2KuVXakXksXFEU+GyIQaiU=", Base64.toBase64String(result.servers[0].id))
    }

    @Test
    fun `returns Invalid if json incomplete`() = runBlocking {
        // given we have a valid json file and signature

        // when we ask for data
        val result = LogListJsonParserV1().parseJson(jsonIncomplete)

        // then invalid is returned
        assertIsA<JsonFormat>(result)
    }

    @Test
    fun `validUntil null when not disqualified or no FinalTreeHead`() = runBlocking {
        // given we have a valid json file and signature

        // when we ask for data
        val result = LogListJsonParserV1().parseJson(jsonValidUntil)

        // then validUntil is set to the the STH timestamp
        require(result is LogListResult.Valid)
        val logServer = result.servers[0]
        assertNull(logServer.validUntil)
    }

    @Test
    fun `validUntil set from Sth`() = runBlocking {
        // given we have a valid json file and signature

        // when we ask for data
        val result = LogListJsonParserV1().parseJson(jsonValidUntil)

        // then validUntil is set to the the STH timestamp
        require(result is LogListResult.Valid)
        val logServer = result.servers[2]
        assertNotNull(logServer.validUntil)
        assertEquals(1480512258330, logServer.validUntil)
    }

    @Test
    fun `validUntil set from disqualified`() = runBlocking {
        // given we have a valid json file and signature

        // when we ask for data
        val result = LogListJsonParserV1().parseJson(jsonValidUntil)

        // then validUntil is set to the the STH timestamp
        require(result is LogListResult.Valid)
        val logServer = result.servers[1]
        assertNotNull(logServer.validUntil)
        assertEquals(1475637842000, logServer.validUntil)
    }


    companion object {
        private val json = TestData.file(TestData.TEST_LOG_LIST_JSON).readText()
        private val jsonIncomplete = TestData.file(TestData.TEST_LOG_LIST_JSON_INCOMPLETE).readText()
        private val jsonValidUntil = TestData.file(TestData.TEST_LOG_LIST_JSON_VALID_UNTIL).readText()
    }
}