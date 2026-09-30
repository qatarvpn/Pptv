package com.example.data.xtream

import com.example.data.model.ChannelType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class XtreamClientTest {

    private val client = XtreamClient()

    @Test
    fun testAuthenticateWithValidCredentials() = runTest {
        // Note: This test would need mocking for real usage
        // For now, we just test the data class structure
        val result = XtreamClient.AuthResult(
            success = true,
            serverInfo = "http://xtream.server.com",
            expiryDate = "2024-12-31",
            maxConnections = "5"
        )

        assertTrue(result.success)
        assertEquals("http://xtream.server.com", result.serverInfo)
        assertEquals("2024-12-31", result.expiryDate)
        assertEquals("5", result.maxConnections)
    }

    @Test
    fun testAuthenticateFailure() {
        val result = XtreamClient.AuthResult(
            success = false,
            message = "Invalid credentials"
        )

        assertFalse(result.success)
        assertEquals("Invalid credentials", result.message)
        assertNull(result.serverInfo)
    }

    @Test
    fun testStreamEntityCreation() {
        val stream = com.example.data.local.StreamEntity(
            playlistId = 1L,
            streamId = "stream123",
            name = "Test Channel",
            streamUrl = "http://example.com/stream.m3u8",
            logoUrl = "http://example.com/logo.png",
            category = "Live",
            type = ChannelType.LIVE,
            epgChannelId = "epg_ch1"
        )

        assertEquals(1L, stream.playlistId)
        assertEquals("stream123", stream.streamId)
        assertEquals("Test Channel", stream.name)
        assertEquals(ChannelType.LIVE, stream.type)
        assertEquals("epg_ch1", stream.epgChannelId)
    }
}
