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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.rotate as drawRotate
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Shadow

@Composable
fun VibeMentorHomeScreen(navController: NavHostController) {
    // Enhanced animated background with multiple gradient layers
    val infiniteTransition = rememberInfiniteTransition(label = "")

    // Primary gradient animation
    val animatedOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset1"
    )

    // Secondary gradient animation (faster)
    val animatedOffset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -1500f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset2"
    )

    // Radial gradient animation
    val radiusAnimation by infiniteTransition.animateFloat(
        initialValue = 800f,
        targetValue = 1400f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radius"
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

    // Cute icon animations - bouncing effect
    val iconBounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconScale"
    )

    val iconRotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    // Sparkle animation for decorative stars
    val sparkleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkleRotation"
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
            .drawBehind {
                // Multi-layer animated gradient background
                val gradientColors1 = listOf(
                    Color(0xFF6A11CB),
                    Color(0xFF2575FC),
                    Color(0xFF00C9FF)
                )

                val gradientColors2 = listOf(
                    Color(0xFF92FE9D),
                    Color(0xFFFFB74D),
                    Color(0xFFBA68C8)
                )

                // First layer - linear gradient
                val brush1 = Brush.linearGradient(
                    colors = gradientColors1,
                    start = Offset(0f, animatedOffset1),
                    end = Offset(size.width + animatedOffset1, size.height)
                )
                drawRect(brush = brush1)

                // Second layer - radial gradient with opacity
                val brush2 = Brush.radialGradient(
                    colors = gradientColors2.map { it.copy(alpha = 0.6f) },
                    center = Offset(size.width / 2 + animatedOffset2, size.height / 2),
                    radius = radiusAnimation
                )
                drawRect(brush = brush2)

                // Third layer - diagonal sweep
                val brush3 = Brush.linearGradient(
                    colors = listOf(
                        Color(0x33FFFFFF),
                        Color(0x00FFFFFF),
                        Color(0x33FFFFFF)
                    ),
                    start = Offset(animatedOffset1 / 2, 0f),
                    end = Offset(size.width, size.height + animatedOffset1 / 2)
                )
                drawRect(brush = brush3)
            },
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

                // Cute animated icon section with sparkles
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .size(120.dp)
                ) {
                    // Decorative sparkles around the main icon
                    Text(
                        text = "✨",
                        fontSize = 24.sp,
                        modifier = Modifier
                            .offset(x = (-40).dp, y = (-30).dp)
                            .rotate(sparkleRotation)
                            .scale((iconScale - 0.2f).coerceAtLeast(0.8f))
                    )

                    Text(
                        text = "✨",
                        fontSize = 24.sp,
                        modifier = Modifier
                            .offset(x = 40.dp, y = (-30).dp)
                            .rotate(-sparkleRotation)
                            .scale((iconScale - 0.2f).coerceAtLeast(0.8f))
                    )

                    Text(
                        text = "⭐",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .offset(x = (-45).dp, y = 25.dp)
                            .rotate(sparkleRotation / 2)
                            .scale((iconScale - 0.3f).coerceAtLeast(0.7f))
                    )

                    Text(
                        text = "⭐",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .offset(x = 45.dp, y = 25.dp)
                            .rotate(-sparkleRotation / 2)
                            .scale((iconScale - 0.3f).coerceAtLeast(0.7f))
                    )

                    // Main cute AI bot icon - using a cute robot/AI representation
                    VShapeIcon(
                        modifier = Modifier
                            .offset(y = iconBounce.dp),
                        scale = iconScale,
                        rotation = iconRotation
                    )
                }

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
fun VShapeIcon(
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    rotation: Float = 0f
) {
    Canvas(
        modifier = modifier
            .size(100.dp)
            .scale(scale)
            .rotate(rotation)
    ) {
        val width = size.width
        val height = size.height

        // Create the V shape path
        val vPath = Path().apply {
            // Start from top-left
            moveTo(width * 0.2f, height * 0.15f)
            // Line to bottom center
            lineTo(width * 0.5f, height * 0.85f)
            // Line to top-right
            lineTo(width * 0.8f, height * 0.15f)
        }

        // Draw outer glow effect (multiple layers)
        for (i in 3 downTo 1) {
            drawPath(
                path = vPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFD700).copy(alpha = 0.1f * i),
                        Color(0xFFFF69B4).copy(alpha = 0.1f * i),
                        Color(0xFF00E0FF).copy(alpha = 0.1f * i)
                    )
                ),
                style = Stroke(
                    width = (28f + i * 8f),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        // Draw main V with gradient
        drawPath(
            path = vPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFD700), // Gold
                    Color(0xFFFF69B4), // Pink
                    Color(0xFF00E0FF)  // Cyan
                )
            ),
            style = Stroke(
                width = 24f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Draw inner highlight
        drawPath(
            path = vPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.8f),
                    Color.White.copy(alpha = 0.3f),
                    Color.White.copy(alpha = 0.0f)
                )
            ),
            style = Stroke(
                width = 8f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Add sparkle dots around the V
        val sparklePositions = listOf(
            Offset(width * 0.15f, height * 0.2f),
            Offset(width * 0.85f, height * 0.2f),
            Offset(width * 0.5f, height * 0.9f),
            Offset(width * 0.3f, height * 0.5f),
            Offset(width * 0.7f, height * 0.5f)
        )

        sparklePositions.forEach { pos ->
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFFFD700).copy(alpha = 0.5f),
                        Color.Transparent
                    )
                ),
                radius = 4f,
                center = pos
            )
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
