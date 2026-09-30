package com.example.data.parser

import com.example.data.local.EpgProgramEntity
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream

class XmltvParserTest {

    @Test
    fun testParseValidXmltvDocument() {
        val xmlContent = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE tv SYSTEM "xmltv.dtd">
<tv>
  <programme start="20231201120000 +0000" stop="20231201130000 +0000" channel="ch1">
    <title>Morning Show</title>
    <desc>A great morning show</desc>
  </programme>
  <programme start="20231201130000 +0000" stop="20231201140000 +0000" channel="ch1">
    <title>News</title>
  </programme>
</tv>
"""
        val result = XmltvParser.parse(ByteArrayInputStream(xmlContent.toByteArray()))

        assertEquals(2, result.size)
        assertEquals("ch1", result[0].channelId)
        assertEquals("Morning Show", result[0].title)
        assertEquals("A great morning show", result[0].description)
        assertEquals("News", result[1].title)
    }

    @Test
    fun testParseXmltvEmptyDocument() {
        val xmlContent = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE tv SYSTEM "xmltv.dtd">
<tv>
</tv>
"""
        val result = XmltvParser.parse(ByteArrayInputStream(xmlContent.toByteArray()))
        assertEquals(0, result.size)
    }

    @Test
    fun testParseXmltvMissingTitle() {
        val xmlContent = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE tv SYSTEM "xmltv.dtd">
<tv>
  <programme start="20231201120000 +0000" stop="20231201130000 +0000" channel="ch1">
    <desc>No title for this one</desc>
  </programme>
</tv>
"""
        val result = XmltvParser.parse(ByteArrayInputStream(xmlContent.toByteArray()))
        assertEquals(0, result.size) // Should be skipped if no title
    }

    @Test
    fun testParseXmltvMultipleChannels() {
        val xmlContent = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE tv SYSTEM "xmltv.dtd">
<tv>
  <programme start="20231201120000 +0000" stop="20231201130000 +0000" channel="ch1">
    <title>Show A</title>
  </programme>
  <programme start="20231201120000 +0000" stop="20231201130000 +0000" channel="ch2">
    <title>Show B</title>
  </programme>
</tv>
"""
        val result = XmltvParser.parse(ByteArrayInputStream(xmlContent.toByteArray()))
        assertEquals(2, result.size)
        assertEquals("ch1", result[0].channelId)
        assertEquals("ch2", result[1].channelId)
    }
}
