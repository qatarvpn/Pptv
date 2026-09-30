package com.example.data.xtream

import com.example.data.local.StreamEntity
import com.example.data.model.ChannelType
import org.junit.Assert.*
import org.junit.Test

class XtreamClientTest {

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
        val stream = StreamEntity(
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
