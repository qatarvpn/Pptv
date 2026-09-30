package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onboarding.OnboardingScreen
import com.example.splash.SplashScreen
import com.example.ui.IptvMainApp
import com.example.ui.MainViewModel
import com.example.ui.OnboardingViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var onboardingViewModel: OnboardingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        onboardingViewModel = OnboardingViewModel(this)

        setContent {
            MyApplicationTheme {
                val hasCompletedOnboarding by onboardingViewModel.hasCompletedOnboarding.collectAsState()
                var showSplash by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }

                when {
                    showSplash -> {
                        SplashScreen(
                            onSplashComplete = { showSplash = false }
                        )
                    }
                    !hasCompletedOnboarding -> {
                        OnboardingScreen(
                            onCompleted = {
                                onboardingViewModel.completeOnboarding()
                            }
                        )
                    }
                    else -> {
                        IptvMainApp(viewModel = mainViewModel)
                    }
                }
            }
        }
    }
}
