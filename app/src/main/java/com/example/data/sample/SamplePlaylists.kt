package com.example.data.sample

import com.example.data.local.EpgProgramEntity
import com.example.data.local.PlaylistEntity
import com.example.data.local.StreamEntity
import com.example.data.model.ChannelType
import com.example.data.model.PlaylistType

object SamplePlaylists {

    fun createSamplePlaylist(): PlaylistEntity {
        return PlaylistEntity(
            id = 1,
            name = "Free World & Cinema IPTV",
            type = PlaylistType.M3U_URL,
            sourceUrl = "https://iptv-org.github.io/iptv/index.m3u",
            channelCount = 18,
            movieCount = 6,
            seriesCount = 4,
            isActive = true
        )
    }

    fun createSampleStreams(playlistId: Long): List<StreamEntity> {
        val list = mutableListOf<StreamEntity>()

        // 1. Live TV - News (أخبار)
        list.add(
            StreamEntity(
                playlistId = playlistId,
                streamId = "aljazeera_ar",
                name = "Al Jazeera Arabic (الجزيرة)",
                streamUrl = "https://live-hls-web-aje.getaj.net/AJE/01.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/en/thumb/f/f2/Al_Jazeera_logo.svg/320px-Al_Jazeera_logo.svg.png",
                category = "News (أخبار)",
                type = ChannelType.LIVE,
                epgChannelId = "aljazeera_ar"
            )
        )
        list.add(
            StreamEntity(
                playlistId = playlistId,
                streamId = "france24_ar",
                name = "France 24 Arabic (فرانس 24)",
                streamUrl = "https://static.france24.com/live/F24_AR_LO_HLS/live_tv.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/France_24_logo.svg/320px-France_24_logo.svg.png",
                category = "News (أخبار)",
                type = ChannelType.LIVE,
                epgChannelId = "france24_ar"
            )
        )
        list.add(
            StreamEntity(
                playlistId = playlistId,
                streamId = "dw_arabic",
                name = "DW Arabic (دويتشه فيله)",
                streamUrl = "https://dwstream4-lh.akamaihd.net/i/dwarabic_0@412702/master.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/75/Deutsche_Welle_logo.svg/320px-Deutsche_Welle_logo.svg.png",
                category = "News (أخبار)",
                type = ChannelType.LIVE,
                epgChannelId = "dw_arabic"
            )
        )
        list.add(
            StreamEntity(
                playlistId = playlistId,
                streamId = "euronews_en",
                name = "Euronews World Live",
                streamUrl = "https://euronews-euronews-world-1-eu.rakuten.wurl.tv/playlist.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4c/Euronews_2016_logo.svg/320px-Euronews_2016_logo.svg.png",
                category = "News (أخبار)",
                type = ChannelType.LIVE,
                epgChannelId = "euronews_en"
            )
        )

        // 2. Science & Nature (علوم وطبيعة)
        list.add(
            StreamEntity(
                playlistId = playlistId,
                streamId = "nasa_tv",
                name = "NASA TV Public HD",
                streamUrl = "https://ntv1.akamaized.net/hls/live/2014075/NASA-NTV1-HLS/master.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e5/NASA_logo.svg/320px-NASA_logo.svg.png",
                category = "Documentary (وثائقي)",
                type = ChannelType.LIVE,
                epgChannelId = "nasa_tv"
            )
        )
        list.add(
            StreamEntity(
                playlistId = playlistId,
                streamId = "wild_earth",
                name = "WildEarth Live Safari",
                streamUrl = "https://wildearth-samsungau.amagi.tv/playlist.m3u8",
                logoUrl = "https://wildearth.tv/wp-content/themes/wildearth/assets/images/logo.png",
                category = "Documentary (وثائقي)",
                type = ChannelType.LIVE,
                epgChannelId = "wild_earth"
            )
        )

        // 3. Sports & Entertainment (رياضة وترفيه)
        list.add(
            StreamEntity(
                playlistId = playlistId,
                streamId = "redbull_tv",
                name = "Red Bull TV Live",
                streamUrl = "https://rbmn-live.akamaized.net/hls/live/590964/BoRB-AT/master.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/en/thumb/f/f5/Red_Bull_TV_logo.svg/320px-Red_Bull_TV_logo.svg.png",
                category = "Sports (رياضة)",
                type = ChannelType.LIVE,
                epgChannelId = "redbull_tv"
            )
        )
        list.add(
            StreamEntity(
                playlistId = playlistId,
                streamId = "bloomberg_tv",
                name = "Bloomberg Quicktake News",
                streamUrl = "https://bloomberg.com/media-manifest/streams/us.m3u8",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5a/Bloomberg_Television_logo.svg/320px-Bloomberg_Television_logo.svg.png",
                category = "Business (اقتصاد)",
                type = ChannelType.LIVE,
                epgChannelId = "bloomberg_tv"
            )
        )

        // 4. Movies / VOD (أفلام)
        list.add(
            StreamEntity(
                playlistId = playlistId,
                streamId = "vod_big_buck",
                name = "Big Buck Bunny (4K Remaster)",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/Big_buck_bunny_poster_big.jpg/320px-Big_buck_bunny_poster_big.jpg",
                category = "Animation (رسوم متحركة)",
                type = ChannelType.MOVIE,
                rating = "8.2",
                releaseDate = "2024",
                plot = "A giant rabbit with a heart of gold takes revenge on bullies in the forest with comedy and heart."
            )
        )
        list.add(
            StreamEntity(
                playlistId = playlistId,
                streamId = "vod_tears_of_steel",
                name = "Tears of Steel (Sci-Fi 1080p)",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Tears_of_Steel_poster.jpg/320px-Tears_of_Steel_poster.jpg",
                category = "Sci-Fi (خيال علمي)",
                type = ChannelType.MOVIE,
                rating = "7.8",
                releaseDate = "2023",
                plot = "A dystopian future where a group of warriors and scientists try to save the planet from killer robots."
            )
        )
        list.add(
            StreamEntity(
                playlistId = playlistId,
                streamId = "vod_sintel",
                name = "Sintel (Fantasy Adventure)",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8f/Sintel_poster.jpg/320px-Sintel_poster.jpg",
                category = "Fantasy (فانتازيا)",
                type = ChannelType.MOVIE,
                rating = "8.4",
                releaseDate = "2022",
                plot = "A lonely young woman searches the dangerous wilderness for a baby dragon she befriended."
            )
        )
        list.add(
            StreamEntity(
                playlistId = playlistId,
                streamId = "vod_elephants_dream",
                name = "Elephants Dream (Open Cinema)",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                logoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0c/Elephants_Dream_poster.jpg/320px-Elephants_Dream_poster.jpg",
                category = "Drama (دراما)",
                type = ChannelType.MOVIE,
                rating = "7.5",
                releaseDate = "2021",
                plot = "An old man and his apprentice explore the strange surreal machine world of thoughts."
            )
        )

        // 5. Series (مسلسلات)
        list.add(
            StreamEntity(
                playlistId = playlistId,
                streamId = "series_cosmos_e1",
                name = "Cosmos Discovery - S01E01",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                logoUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=400",
                category = "Space Series (مسلسلات فضاء)",
                type = ChannelType.SERIES,
                seasonNumber = 1,
                episodeNumber = 1,
                seriesId = "cosmos_discovery",
                rating = "9.1",
                plot = "Journey into the mysterious deep space and the birth of stars and distant galaxies."
            )
        )
        list.add(
            StreamEntity(
                playlistId = playlistId,
                streamId = "series_cosmos_e2",
                name = "Cosmos Discovery - S01E02",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                logoUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=400",
                category = "Space Series (مسلسلات فضاء)",
                type = ChannelType.SERIES,
                seasonNumber = 1,
                episodeNumber = 2,
                seriesId = "cosmos_discovery",
                rating = "9.0",
                plot = "Black holes, event horizons, and the mysteries of time distortion in the cosmos."
            )
        )
        list.add(
            StreamEntity(
                playlistId = playlistId,
                streamId = "series_ocean_e1",
                name = "Ocean Abyss - S01E01",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                logoUrl = "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=400",
                category = "Wildlife Series (مسلسلات طبيعة)",
                type = ChannelType.SERIES,
                seasonNumber = 1,
                episodeNumber = 1,
                seriesId = "ocean_abyss",
                rating = "8.9",
                plot = "Exploring the uncharted depths of the Mariana Trench and bioluminescent life."
            )
        )

        return list
    }

    fun createSampleEpg(): List<EpgProgramEntity> {
        val now = System.currentTimeMillis()
        val oneHour = 3600_000L
        val list = mutableListOf<EpgProgramEntity>()

        val channels = listOf(
            "aljazeera_ar" to listOf("نشرة الأخبار الرئيسية", "ما وراء الخبر", "الحصاد الإخباري", "المسائية", "نوافذ"),
            "france24_ar" to listOf("أخبار العالم 24", "نقاش اليوم", "مراسلون حول العالم", "ثقافة وفنون", "حوار الأسبوع"),
            "nasa_tv" to listOf("ISS Expedition Live Stream", "Artemis Lunar Update", "Hubble Deep Field Insights", "Space Station Science", "Mars Rover Chronicles"),
            "redbull_tv" to listOf("Cliff Diving World Series", "Downhill MTB Championship", "Rampage Highlights", "Surf Pioneers", "Aviation Air Race"),
            "dw_arabic" to listOf("الأخبار المسائية", "جعفر توك", "صحتك أولاً", "وثائقيات الغد", "عالم السرعة")
        )

        channels.forEach { (channelId, programs) ->
            var startTime = now - (oneHour / 2) // current program started 30 mins ago
            programs.forEach { title ->
                val endTime = startTime + oneHour
                list.add(
                    EpgProgramEntity(
                        channelId = channelId,
                        title = title,
                        description = "بث حي مباشر ومناقشات تفصيلية حول أهم الأحداث والتغطيات الحصرية.",
                        startEpoch = startTime,
                        endEpoch = endTime
                    )
                )
                startTime = endTime
            }
        }

        return list
    }
}
