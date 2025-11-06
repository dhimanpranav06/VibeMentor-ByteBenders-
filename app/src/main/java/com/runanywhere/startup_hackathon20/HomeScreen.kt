package com.runanywhere.startup_hackathon20

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import java.util.Calendar

@Composable
fun VibeMentorHomeScreen(navController: NavHostController) {
    // Animated background gradient 
    val gradientColors = listOf(
        Color(0xFF6A11CB),  // Purple
        Color(0xFF2575FC),  // Blue
        Color(0xFF00C9FF),  // Aqua
        Color(0xFF92FE9D)   // Mint Green
    )

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val brush = Brush.linearGradient(
        colors = gradientColors,
        start = Offset(0f, animatedOffset),
        end = Offset(animatedOffset, 0f)
    )

    // Pulsing animation for logo/title 
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    // Android icon animations 
    val androidRotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    val androidScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "androidScale"
    )

    val androidOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    // Time-based greeting 
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour in 5..11 -> " Good Morning"
            hour in 12..16 -> " Good Afternoon"
            hour in 17..20 -> " Good Evening"
            else -> " Good Night"
        }
    }

    // Animated inspiring quotes 
    val inspiringQuotes = listOf(
        " Believe in yourself and all that you are",
        " Every expert was once a beginner",
        " Learning is a journey, not a destination",
        " Your potential is endless",
        " Success is the sum of small efforts",
        " You are capable of amazing things",
        " Dream big, work hard, stay focused",
        " The best time to start is now"
    )

    var currentQuoteIndex by remember { mutableStateOf(0) }

    // Auto-rotate quotes every 4 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            currentQuoteIndex = (currentQuoteIndex + 1) % inspiringQuotes.size
        }
    }

    // Fade-in animation for buttons 
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
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Top Section - Greeting
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 32.dp)
            ) {
                // Greeting Text
                Text(
                    text = greeting,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Android Icon
                Text(
                    text = "🤖",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .scale(androidScale)
                        .rotate(androidRotation)
                        .offset(y = androidOffset.dp)
                        .padding(vertical = 12.dp)
                )

                // App Title
                Text(
                    text = "VibeMentor",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .scale(scale)
                        .padding(top = 12.dp, bottom = 12.dp)
                )

                // Tagline
                Text(
                    text = "Learn • Create • Grow • Repeat",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier
                        .alpha(0.9f)
                        .padding(top = 4.dp)
                )
            }

            // Middle Section - Buttons
            AnimatedVisibility(visible = visible) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    VibrantButton("🧠 Start AI Chat", Color(0xFF00E0FF)) {
                        navController.navigate("chat")
                    }
                    VibrantButton("📚 Study Mode", Color(0xFFFFB74D)) {
                        navController.navigate("study")
                    }
                    VibrantButton("💼 Career Mode", Color(0xFF66BB6A)) {
                        navController.navigate("career")
                    }
                    VibrantButton("🎨 Creative Muse", Color(0xFFBA68C8)) {
                        navController.navigate("creative")
                    }
                }
            }

            // Bottom Section - Animated Inspiring Quotes
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                AnimatedContent(
                    targetState = inspiringQuotes[currentQuoteIndex],
                    transitionSpec = {
                        fadeIn(tween(800)) togetherWith fadeOut(tween(800))
                    },
                    label = "quoteAnimation"
                ) { quote ->
                    Text(
                        text = quote,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .alpha(0.95f)
                    )
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
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(60.dp)
    ) {
        Text(
            text = text,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = 0.5.sp
        )
    }
}
