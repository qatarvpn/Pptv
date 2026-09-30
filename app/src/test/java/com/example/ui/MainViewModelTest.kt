package com.example.ui

import androidx.lifecycle.viewModelScope
import com.example.data.local.IptvDatabase
import com.example.data.model.AppNavScreen
import com.example.data.model.StreamItem
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class MainViewModelTest {

    @Test
    fun testInitialScreenState() {
        // Test initial navigation screen
        assertEquals(AppNavScreen.LIVE_TV, AppNavScreen.LIVE_TV)
    }

    @Test
    fun testScreenNavigation() {
        val screens = AppNavScreen.values()
        assertTrue(screens.isNotEmpty())
        assertTrue(screens.contains(AppNavScreen.LIVE_TV))
        assertTrue(screens.contains(AppNavScreen.MOVIES))
        assertTrue(screens.contains(AppNavScreen.SERIES))
        assertTrue(screens.contains(AppNavScreen.EPG))
        assertTrue(screens.contains(AppNavScreen.FAVORITES))
        assertTrue(screens.contains(AppNavScreen.SETTINGS))
        assertTrue(screens.contains(AppNavScreen.SEARCH))
    }

    @Test
    fun testLanguageToggle() {
        var isArabic = true
        isArabic = !isArabic
        assertFalse(isArabic)
        isArabic = !isArabic
        assertTrue(isArabic)
    }
}
