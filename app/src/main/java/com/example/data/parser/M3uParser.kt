package com.example.data.parser

import com.example.data.local.StreamEntity
import com.example.data.model.ChannelType
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.regex.Pattern

object M3uParser {

    private val TVG_ID_REGEX = Pattern.compile("tvg-id=\"([^\"]*)\"")
    private val TVG_NAME_REGEX = Pattern.compile("tvg-name=\"([^\"]*)\"")
    private val TVG_LOGO_REGEX = Pattern.compile("tvg-logo=\"([^\"]*)\"")
    private val GROUP_TITLE_REGEX = Pattern.compile("group-title=\"([^\"]*)\"")

    fun parse(inputStream: InputStream, playlistId: Long): List<StreamEntity> {
        val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
        val result = mutableListOf<StreamEntity>()

        var currentExtInf: String? = null
        var lineIndex = 0

        reader.forEachLine { line ->
            val trimmed = line.trim()
            if (trimmed.startsWith("#EXTINF:", ignoreCase = true)) {
                currentExtInf = trimmed
            } else if (!trimmed.startsWith("#") && trimmed.isNotBlank() && currentExtInf != null) {
                val stream = parseEntry(currentExtInf!!, trimmed, playlistId, lineIndex++)
                if (stream != null) {
                    result.add(stream)
                }
                currentExtInf = null
            }
        }

        return result
    }

    private fun parseEntry(
        extInf: String,
        url: String,
        playlistId: Long,
        index: Int
    ): StreamEntity? {
        val tvgId = extractAttribute(TVG_ID_REGEX, extInf)
        val tvgName = extractAttribute(TVG_NAME_REGEX, extInf)
        val tvgLogo = extractAttribute(TVG_LOGO_REGEX, extInf)
        val groupTitle = extractAttribute(GROUP_TITLE_REGEX, extInf) ?: "General"

        // The channel title is after the last comma in #EXTINF
        val commaIndex = extInf.lastIndexOf(',')
        val displayName = if (commaIndex != -1 && commaIndex < extInf.length - 1) {
            extInf.substring(commaIndex + 1).trim()
        } else {
            tvgName ?: "Channel ${index + 1}"
        }

        val type = detectType(groupTitle, url, displayName)

        // Parse series/season/episode if applicable
        var seasonNum: Int? = null
        var epNum: Int? = null
        var seriesId: String? = null

        if (type == ChannelType.SERIES) {
            val seMatch = Regex("""[Ss](\d{1,2})[Ee](\d{1,2})""").find(displayName)
            if (seMatch != null) {
                seasonNum = seMatch.groupValues[1].toIntOrNull()
                epNum = seMatch.groupValues[2].toIntOrNull()
                seriesId = displayName.substring(0, seMatch.range.first).trim()
            }
        }

        return StreamEntity(
            playlistId = playlistId,
            streamId = tvgId ?: "stream_$index",
            name = displayName.ifBlank { "Channel ${index + 1}" },
            streamUrl = url,
            logoUrl = tvgLogo,
            category = groupTitle.ifBlank { "General" },
            type = type,
            epgChannelId = tvgId,
            seasonNumber = seasonNum,
            episodeNumber = epNum,
            seriesId = seriesId
        )
    }

    private fun extractAttribute(pattern: Pattern, text: String): String? {
        val matcher = pattern.matcher(text)
        return if (matcher.find()) matcher.group(1)?.trim() else null
    }

    private fun detectType(category: String, url: String, name: String): ChannelType {
        val lowerCat = category.lowercase()
        val lowerUrl = url.lowercase()
        val lowerName = name.lowercase()

        // Series detection
        if (lowerCat.contains("series") || lowerCat.contains("مسلسلات") ||
            lowerUrl.contains("/series/") ||
            Regex("""[Ss]\d{1,2}[Ee]\d{1,2}""").containsMatchIn(name)
        ) {
            return ChannelType.SERIES
        }

        // Movies detection
        if (lowerCat.contains("movie") || lowerCat.contains("film") || lowerCat.contains("cinema") ||
            lowerCat.contains("vod") || lowerCat.contains("أفلام") ||
            lowerUrl.contains("/movie/") ||
            lowerUrl.endsWith(".mp4") || lowerUrl.endsWith(".mkv") || lowerUrl.endsWith(".avi")
        ) {
            return ChannelType.MOVIE
        }

        return ChannelType.LIVE
    }
}
