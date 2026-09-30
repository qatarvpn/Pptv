package com.example.data.repo

import com.example.data.local.PlaylistEntity
import com.example.data.model.PlaylistType
import org.junit.Assert.assertEquals
import org.junit.Test

class IptvRepositoryTest {

    @Test
    fun testPlaylistEntityKeepsCoreFields() {
        val entity = PlaylistEntity(
            id = 1L,
            name = "Test Playlist",
            type = PlaylistType.XTREAM,
            sourceUrl = "http://server.com",
            username = "user123",
            password = "encrypted_password",
            channelCount = 50,
            movieCount = 100,
            seriesCount = 10
        )

        assertEquals(1L, entity.id)
        assertEquals("Test Playlist", entity.name)
        assertEquals(PlaylistType.XTREAM, entity.type)
        assertEquals("http://server.com", entity.sourceUrl)
        assertEquals("user123", entity.username)
        assertEquals("encrypted_password", entity.password)
    }
}
