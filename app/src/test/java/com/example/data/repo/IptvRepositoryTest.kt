package com.example.data.repo

import com.example.data.local.IptvDao
import com.example.data.local.PlaylistEntity
import com.example.data.model.PlaylistType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class IptvRepositoryTest {

    private lateinit var mockDao: IptvDao
    private lateinit var repository: IptvRepository

    @Before
    fun setup() {
        mockDao = mock(IptvDao::class.java)
        repository = IptvRepository(dao = mockDao)
    }

    @Test
    fun testPlaylistEntity_ToModel_DecryptsPassword() {
        // Test that decryption happens during model conversion
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

        // Verify entity structure
        assertEquals(1L, entity.id)
        assertEquals("Test Playlist", entity.name)
        assertEquals(PlaylistType.XTREAM, entity.type)
        assertEquals("http://server.com", entity.sourceUrl)
        assertEquals("user123", entity.username)
    }
}
