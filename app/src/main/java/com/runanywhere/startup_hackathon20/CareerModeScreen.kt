package com.runanywhere.startup_hackathon20

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun CareerModeScreen(navController: NavController) {
    val gradientColors = listOf(
        Color(0xFF1A2980),
        Color(0xFF26D0CE)
    )

    val infiniteTransition = rememberInfiniteTransition()
    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            tween(10000, easing = LinearEasing),
            RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = gradientColors,
                    start = androidx.compose.ui.geometry.Offset(gradientShift, 0f),
                    end = androidx.compose.ui.geometry.Offset(0f, gradientShift)
                )
            )
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "💼 Career Catalyst",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Empower your career with AI tools built to help you grow.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 🧩 Feature cards
            CareerFeatureCard(
                title = "📄 Smart Resume Builder",
                description = "Automatically generate and update your resume as you learn new skills. Export with one tap!",
                gradient = listOf(Color(0xFF43CEA2), Color(0xFF185A9D))
            ) { navController.navigate("career_resume") }

            Spacer(modifier = Modifier.height(20.dp))

            CareerFeatureCard(
                title = "📊 Skill Progress Tracker",
                description = "Visualize your learning journey with AI insights and dynamic graphs.",
                gradient = listOf(Color(0xFFFF9966), Color(0xFFFF5E62))
            ) { navController.navigate("career_tracker") }

            Spacer(modifier = Modifier.height(20.dp))

            CareerFeatureCard(
                title = "🎤 AI Mock Interview Coach",
                description = "Practice interviews with real-time AI feedback and confidence analytics.",
                gradient = listOf(Color(0xFF8360C3), Color(0xFF2EBF91))
            ) { navController.navigate("career_interview") }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer
            Text(
                text = "Powered by VibeMentor ⚡",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun CareerFeatureCard(
    title: String,
    description: String,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.05f else 1f,
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "scale"
    )

    // Reset press effect after animation
    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(150)
            pressed = false
        }
    }

    Box(
        modifier = Modifier
            .scale(scale)
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(gradient),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                pressed = true
                onClick()
            }
            .padding(20.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 15.sp
            )
        }
    }
}