package com.example.data.parser

import com.example.data.local.StreamEntity
import com.example.data.model.ChannelType
import org.junit.Assert.*
import org.junit.Test

class M3uParserTest {

    @Test
    fun testParseValidM3uPlaylist() {
        val m3uContent = """#EXTM3U
#EXTINF:-1 tvg-id="ch1" tvg-name="Channel 1" tvg-logo="https://example.com/logo.png" group-title="Live",Channel 1
http://example.com/stream1.m3u8
#EXTINF:-1 tvg-id="ch2" tvg-name="Channel 2" group-title="Live",Channel 2
http://example.com/stream2.m3u8
""".byteInputStream()

        val result = M3uParser.parse(m3uContent, 1L)

        assertEquals(2, result.size)
        assertEquals("Channel 1", result[0].name)
        assertEquals("ch1", result[0].streamId)
        assertEquals("http://example.com/stream1.m3u8", result[0].streamUrl)
        assertEquals("Live", result[0].category)
        assertEquals(ChannelType.LIVE, result[0].type)
        assertEquals("https://example.com/logo.png", result[0].logoUrl)
    }

    @Test
    fun testParseMovieDetection() {
        val m3uContent = """#EXTM3U
#EXTINF:-1 tvg-name="Movie 1" group-title="Movies",Movie Title
http://example.com/movie1.mp4
#EXTINF:-1 tvg-name="Movie 2" group-title="Films",Another Movie
http://example.com/movie2.mkv
""".byteInputStream()

        val result = M3uParser.parse(m3uContent, 1L)

        assertEquals(2, result.size)
        assertEquals(ChannelType.MOVIE, result[0].type)
        assertEquals(ChannelType.MOVIE, result[1].type)
    }

    @Test
    fun testParseSeriesDetection() {
        val m3uContent = """#EXTM3U
#EXTINF:-1 tvg-name="Breaking Bad S01E01" group-title="Series",Breaking Bad S01E01
http://example.com/bb_s01e01.m3u8
#EXTINF:-1 tvg-name="Game of Thrones S02E03" group-title="مسلسلات",Game of Thrones S02E03
http://example.com/got_s02e03.m3u8
""".byteInputStream()

        val result = M3uParser.parse(m3uContent, 1L)

        assertEquals(2, result.size)
        assertEquals(ChannelType.SERIES, result[0].type)
        assertEquals(1, result[0].seasonNumber)
        assertEquals(1, result[0].episodeNumber)
        assertEquals(ChannelType.SERIES, result[1].type)
        assertEquals(2, result[1].seasonNumber)
        assertEquals(3, result[1].episodeNumber)
    }

    @Test
    fun testParseEmptyPlaylist() {
        val m3uContent = "#EXTM3U".byteInputStream()
        val result = M3uParser.parse(m3uContent, 1L)
        assertEquals(0, result.size)
    }

    @Test
    fun testParseMalformedLines() {
        val m3uContent = """#EXTM3U
#EXTINF:-1,Valid Channel
http://example.com/stream1.m3u8
http://malformed.com/orphan
#EXTINF:-1,Another Channel
http://example.com/stream2.m3u8
""".byteInputStream()

        val result = M3uParser.parse(m3uContent, 1L)
        assertEquals(2, result.size)
    }
}
