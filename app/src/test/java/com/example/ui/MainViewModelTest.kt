package com.example.ui

import org.junit.Assert.assertTrue
import org.junit.Test

class MainViewModelTest {

    @Test
    fun testNavigationEnumContainsCoreScreens() {
        val screens = AppNavScreen.values()

        assertTrue(screens.contains(AppNavScreen.LIVE_TV))
        assertTrue(screens.contains(AppNavScreen.MOVIES))
        assertTrue(screens.contains(AppNavScreen.SERIES))
        assertTrue(screens.contains(AppNavScreen.EPG))
        assertTrue(screens.contains(AppNavScreen.FAVORITES))
        assertTrue(screens.contains(AppNavScreen.SETTINGS))
        assertTrue(screens.contains(AppNavScreen.SEARCH))
    }
}
