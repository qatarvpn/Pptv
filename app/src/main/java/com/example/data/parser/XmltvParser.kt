package com.example.data.parser

import android.util.Xml
import com.example.data.local.EpgProgramEntity
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object XmltvParser {

    private val epgDateFormat = SimpleDateFormat("yyyyMMddHHmmss Z", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun parse(inputStream: InputStream): List<EpgProgramEntity> {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(inputStream, "UTF-8")

        val programs = mutableListOf<EpgProgramEntity>()
        var eventType = parser.eventType

        var currentChannelId: String? = null
        var currentStart: Long = 0
        var currentEnd: Long = 0
        var currentTitle: String? = null
        var currentDesc: String? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val name = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (name) {
                        "programme" -> {
                            val startStr = parser.getAttributeValue(null, "start")
                            val stopStr = parser.getAttributeValue(null, "stop")
                            currentChannelId = parser.getAttributeValue(null, "channel")

                            currentStart = parseDate(startStr)
                            currentEnd = parseDate(stopStr)
                            currentTitle = null
                            currentDesc = null
                        }
                        "title" -> {
                            currentTitle = parser.nextText()
                        }
                        "desc" -> {
                            currentDesc = parser.nextText()
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (name == "programme" && currentChannelId != null && currentTitle != null) {
                        programs.add(
                            EpgProgramEntity(
                                channelId = currentChannelId,
                                title = currentTitle,
                                description = currentDesc,
                                startEpoch = currentStart,
                                endEpoch = currentEnd
                            )
                        )
                    }
                }
            }
            eventType = parser.next()
        }

        return programs
    }

    private fun parseDate(dateStr: String?): Long {
        if (dateStr.isNullOrBlank()) return System.currentTimeMillis()
        return try {
            val clean = if (dateStr.contains(" ")) dateStr else "$dateStr +0000"
            epgDateFormat.parse(clean)?.time ?: System.currentTimeMillis()
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    }
}
