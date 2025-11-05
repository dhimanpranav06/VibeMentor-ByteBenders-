package com.runanywhere.startup_hackathon20

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    // Animated background gradient 🌈
    val gradientColors = listOf(
        Color(0xFF6A11CB),  // Purple
        Color(0xFF2575FC),  // Blue
        Color(0xFF00C9FF),  // Aqua
        Color(0xFF92FE9D)   // Mint Green
    )

    val infiniteTransition = rememberInfiniteTransition()
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val brush = Brush.linearGradient(
        colors = gradientColors,
        start = androidx.compose.ui.geometry.Offset(0f, animatedOffset),
        end = androidx.compose.ui.geometry.Offset(animatedOffset, 0f)
    )

    // Pulsing animation for logo/title 💫
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Fade-in animation for buttons ✨
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "VibeMentor",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .scale(scale)
                    .padding(bottom = 12.dp)
            )

            Text(
                text = "Learn • Create • Grow • Repeat",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 48.dp)
                    .alpha(0.9f)
            )

            // Fade-in buttons
            AnimatedVisibility(visible = visible) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    VibrantButton("🧠 Start AI Chat", Color(0xFF00E0FF)) {
                        navController.navigate("chat")
                    }
                    VibrantButton("📚 Study Mode", Color(0xFFFFB74D)) {}
                    VibrantButton("💼 Career Mode", Color(0xFF66BB6A)) {}
                    VibrantButton("🎨 Creative Muse", Color(0xFFBA68C8)) {}
                }
            }
        }
    }
}

@Composable
fun VibrantButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(55.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}