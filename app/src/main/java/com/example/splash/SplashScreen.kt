package com.example.splash

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.TextPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleAnimation = remember { Animatable(0.5f) }
    val alphaAnimation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Icon scale animation
        scaleAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        // Text fade animation
        alphaAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )
        // Wait before transitioning
        delay(2200)
        onSplashComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.foundation.background(
                    color = DarkBg
                ).brush ?: androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(DarkBg, DarkBg)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Animated Icon Background
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scaleAnimation.value),
                shape = CircleShape,
                color = PrimaryBlue.copy(alpha = 0.15f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.LiveTv,
                        contentDescription = "IPTV Player",
                        modifier = Modifier.size(60.dp),
                        tint = PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name with fade animation
            Text(
                text = "IPTV Player",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary.copy(alpha = alphaAnimation.value),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your Ultimate Streaming Platform",
                fontSize = 14.sp,
                color = TextPrimary.copy(alpha = alphaAnimation.value * 0.7f),
                fontWeight = FontWeight.Light
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading Indicator
            LoadingDots()
        }
    }
}

@Composable
fun LoadingDots(
    modifier: Modifier = Modifier
) {
    val delay1 = remember { Animatable(0f) }
    val delay2 = remember { Animatable(0f) }
    val delay3 = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay1.animateTo(
                1f,
                animationSpec = tween(durationMillis = 600)
            )
            delay1.animateTo(
                0f,
                animationSpec = tween(durationMillis = 600)
            )
        }
    }

    LaunchedEffect(Unit) {
        delay(150)
        while (true) {
            delay2.animateTo(
                1f,
                animationSpec = tween(durationMillis = 600)
            )
            delay2.animateTo(
                0f,
                animationSpec = tween(durationMillis = 600)
            )
        }
    }

    LaunchedEffect(Unit) {
        delay(300)
        while (true) {
            delay3.animateTo(
                1f,
                animationSpec = tween(durationMillis = 600)
            )
            delay3.animateTo(
                0f,
                animationSpec = tween(durationMillis = 600)
            )
        }
    }

    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .size(8.dp)
                .scale(0.5f + delay1.value * 0.5f),
            shape = CircleShape,
            color = PrimaryBlue
        ) {}

        Surface(
            modifier = Modifier
                .size(8.dp)
                .scale(0.5f + delay2.value * 0.5f),
            shape = CircleShape,
            color = PrimaryBlue
        ) {}

        Surface(
            modifier = Modifier
                .size(8.dp)
                .scale(0.5f + delay3.value * 0.5f),
            shape = CircleShape,
            color = PrimaryBlue
        ) {}
    }
}
