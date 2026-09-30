package com.example.ui.i18n

object AppStrings {
    fun get(isArabic: Boolean) = if (isArabic) Arabic else English

    interface Strings {
        val appName: String
        val navLive: String
        val navMovies: String
        val navSeries: String
        val navEpg: String
        val navFavorites: String
        val navSettings: String
        val searchHint: String
        val allCategories: String
        val noChannelsFound: String
        val noPlaylists: String
        val noPlaylistsDesc: String
        val addPlaylist: String
        val loadSample: String
        val m3uTab: String
        val xtreamTab: String
        val playlistName: String
        val m3uUrl: String
        val serverUrl: String
        val username: String
        val password: String
        val cancel: String
        val save: String
        val loading: String
        val activePlaylist: String
        val switchPlaylist: String
        val deletePlaylist: String
        val language: String
        val arabic: String
        val english: String
        val liveNow: String
        val upcoming: String
        val resumePlaying: String
        val watchHistory: String
        val favorites: String
        val playNow: String
        val episodes: String
        val season: String
        val episode: String
    }

    object English : Strings {
        override val appName = "IPTV Player"
        override val navLive = "Live TV"
        override val navMovies = "Movies"
        override val navSeries = "Series"
        override val navEpg = "TV Guide"
        override val navFavorites = "Favorites"
        override val navSettings = "Settings"
        override val searchHint = "Search channels, movies, series..."
        override val allCategories = "All Channels"
        override val noChannelsFound = "No channels match your filter"
        override val noPlaylists = "No Playlists Added"
        override val noPlaylistsDesc = "Import an M3U playlist or Xtream Codes subscription to start watching."
        override val addPlaylist = "Add Playlist"
        override val loadSample = "Load Free Sample Playlist"
        override val m3uTab = "M3U URL"
        override val xtreamTab = "Xtream Codes"
        override val playlistName = "Playlist Name"
        override val m3uUrl = "M3U Playlist URL"
        override val serverUrl = "Server URL (http://...)"
        override val username = "Username"
        override val password = "Password"
        override val cancel = "Cancel"
        override val save = "Connect & Save"
        override val loading = "Importing channels..."
        override val activePlaylist = "Active Playlist"
        override val switchPlaylist = "Switch Playlist"
        override val deletePlaylist = "Delete Playlist"
        override val language = "Language"
        override val arabic = "العربية"
        override val english = "English"
        override val liveNow = "LIVE NOW"
        override val upcoming = "Upcoming"
        override val resumePlaying = "Continue Watching"
        override val watchHistory = "Watch History"
        override val favorites = "Favorites"
        override val playNow = "Play Now"
        override val episodes = "Episodes"
        override val season = "Season"
        override val episode = "Episode"
    }

    object Arabic : Strings {
        override val appName = "مشغل IPTV"
        override val navLive = "بث مباشر"
        override val navMovies = "أفلام"
        override val navSeries = "مسلسلات"
        override val navEpg = "دليل البرامج"
        override val navFavorites = "المفضلة"
        override val navSettings = "الإعدادات"
        override val searchHint = "ابحث عن القنوات، الأفلام، المسلسلات..."
        override val allCategories = "كل القنوات"
        override val noChannelsFound = "لا توجد قنوات تطابق البحث"
        override val noPlaylists = "لا توجد قوائم تشغيل"
        override val noPlaylistsDesc = "أضف رابط M3U أو بيانات اشتراك Xtream Codes لبدء المشاهدة."
        override val addPlaylist = "إضافة قائمة تشغيل"
        override val loadSample = "تحميل باقة تجريبية مجانية"
        override val m3uTab = "رابط M3U"
        override val xtreamTab = "اشتراك Xtream"
        override val playlistName = "اسم القائمة"
        override val m3uUrl = "رابط ملف M3U"
        override val serverUrl = "رابط السيرفر (Server URL)"
        override val username = "اسم المستخدم"
        override val password = "كلمة المرور"
        override val cancel = "إلغاء"
        override val save = "اتصال وحفظ"
        override val loading = "جارٍ استيراد القنوات..."
        override val activePlaylist = "القائمة النشطة"
        override val switchPlaylist = "تبديل القائمة"
        override val deletePlaylist = "حذف القائمة"
        override val language = "اللغة"
        override val arabic = "العربية"
        override val english = "English"
        override val liveNow = "يعرض الآن"
        override val upcoming = "التالي"
        override val resumePlaying = "متابعة المشاهدة"
        override val watchHistory = "سجل المشاهدة"
        override val favorites = "المفضلة"
        override val playNow = "تشغيل الآن"
        override val episodes = "الحلقات"
        override val season = "الموسم"
        override val episode = "الحلقة"
    }
}
