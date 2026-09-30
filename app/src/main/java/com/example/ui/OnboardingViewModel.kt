package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "onboarding")
private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")

class OnboardingViewModel(private val context: Context) : ViewModel() {

    private val _hasCompletedOnboarding = MutableStateFlow(false)
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding

    init {
        checkOnboardingStatus()
    }

    private fun checkOnboardingStatus() {
        viewModelScope.launch {
            val preferences = context.dataStore.data.first()
            _hasCompletedOnboarding.value = preferences[ONBOARDING_COMPLETED_KEY] ?: false
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[ONBOARDING_COMPLETED_KEY] = true
            }
            _hasCompletedOnboarding.value = true
        }
    }
}

// Extension function for easier access
suspend fun Context.hasCompletedOnboarding(): Boolean {
    val preferences = this.dataStore.data.first()
    return preferences[ONBOARDING_COMPLETED_KEY] ?: false
}

suspend fun Context.markOnboardingAsCompleted() {
    this.dataStore.edit { preferences ->
        preferences[ONBOARDING_COMPLETED_KEY] = true
    }
}
