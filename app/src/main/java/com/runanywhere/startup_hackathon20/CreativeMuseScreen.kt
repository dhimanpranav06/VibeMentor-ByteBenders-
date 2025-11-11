package com.runanywhere.startup_hackathon20

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runanywhere.startup_hackathon20.api.NvidiaService
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CreativeMuseScreen() {
    val gradientColors = listOf(
        Color(0xFF6A11CB),
        Color(0xFF2575FC)
    )

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            tween(12000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "gradientShift"
    )

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var idea by remember { mutableStateOf("✨ Tap the button below to spark your next creative idea!") }
    var isGenerating by remember { mutableStateOf(false) }

    // ✨ Animated background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(gradientShift, 0f),
                    end = Offset(0f, gradientShift)
                )
            )
    ) {
        // Soft moving light particles
        repeat(15) {
            val x = remember { Random.nextInt(0, 900).toFloat() }
            val y = remember { Random.nextInt(0, 1600).toFloat() }
            val alphaAnim = infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 0.6f,
                animationSpec = infiniteRepeatable(
                    tween((4000..8000).random(), easing = LinearEasing),
                    RepeatMode.Reverse
                ),
                label = "particleAlpha$it"
            )

            Canvas(
                modifier = Modifier
                    .offset(x.dp, y.dp)
                    .size((8..14).random().dp)
                    .blur(20.dp)
                    .alpha(alphaAnim.value)
            ) {
                drawCircle(Color.White.copy(alpha = 0.2f))
            }
        }

        // 🌈 Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "🎨",
                    fontSize = 56.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Creative Muse",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Fuel your imagination with AI-powered ideas",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 💡 Idea Display Card - Improved Design
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 500.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.5f),
                                    Color.White.copy(alpha = 0.2f)
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(24.dp)
                ) {
                    AnimatedContent(
                        targetState = idea,
                        transitionSpec = {
                            fadeIn(tween(500)) togetherWith fadeOut(tween(500))
                        },
                        label = "ideaAnim"
                    ) { text ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = text,
                                color = Color.White,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 26.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 🚀 Spark Button - Improved Design
            var pressed by remember { mutableStateOf(false) }
            val pulse by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    tween(1200, easing = FastOutSlowInEasing),
                    RepeatMode.Reverse
                ),
                label = "pulse"
            )

            Button(
                onClick = {
                    pressed = true
                    coroutineScope.launch {
                        isGenerating = true
                        idea = "✨ Generating your creative spark..."

                        // Generate AI-powered creative idea
                        try {
                            val creativePrompts = listOf(
                                "Generate a unique and inspiring design concept name with a brief tagline (2-3 lines total)",
                                "Create an innovative brand name and slogan for a creative startup (2-3 lines total)",
                                "Suggest a beautiful color palette with 3-4 colors, their hex codes, and emotional meanings",
                                "Write an inspiring quote about creativity and innovation (1-2 lines)",
                                "Create a compelling story hook for a creative project (2-3 lines)",
                                "Design a catchy social media campaign idea with hashtag and brief description"
                            )

                            val randomPrompt = creativePrompts.random()
                            val response = NvidiaService.generateCompletion(
                                "You are a creative genius who inspires innovation. $randomPrompt. Be concise, inspiring, and format it beautifully with emojis where appropriate."
                            )

                            if (response.isNotBlank() && !response.startsWith("ERROR") && !response.startsWith(
                                    "NVIDIA API not configured"
                                )
                            ) {
                                idea = response
                            } else {
                                // Fallback to local ideas if API fails
                                val fallbackIdeas = listOf(
                                    "🌈 Design Concept: \"Aurora Canvas\"\n\n✨ Where light meets creativity - a platform that transforms data into visual poetry, bridging analytics with artistry.",
                                    "🚀 Brand Name: \"Sparkflow\"\n\n💫 Where creativity flows freely\n\nPerfect for creative studios, design agencies, and innovation labs.",
                                    "🎨 Color Palette: \"Midnight Dreams\"\n\n• Midnight Blue (#2C3E50) - Depth & Trust\n• Golden Hour (#F39C12) - Warmth & Optimism  \n• Rose Quartz (#F8B4D9) - Softness & Creativity\n• Mint Fresh (#A8E6CF) - Renewal & Growth",
                                    "✨ \"The best ideas are born when imagination dances with possibility.\"\n\n💡 Let your creativity flow without boundaries.",
                                    "📖 Story Hook:\n\n\"The artist who painted with code - A tale of technology meeting soul, where every algorithm tells a human story.\"",
                                    "📱 Campaign Idea: #CreateDaily\n\n✨ Share one creative moment every day\n🎨 Build a community of makers\n💡 Inspire through consistent creation"
                                )
                                idea = fallbackIdeas.random()
                            }
                        } catch (e: Exception) {
                            idea =
                                "✨ Keep creating! Your next big idea is just around the corner.\n\n💡 Innovation happens when we dare to imagine."
                        }

                        isGenerating = false
                        pressed = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .scale(if (pressed) 0.96f else if (!isGenerating) pulse else 1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(32.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 12.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFF6B6B),
                                    Color(0xFFEE5A6F),
                                    Color(0xFFC44569)
                                )
                            ),
                            shape = RoundedCornerShape(32.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGenerating) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )
                            Text(
                                text = "Creating Magic...",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✨",
                                fontSize = 24.sp
                            )
                            Text(
                                text = "Spark an Idea",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Info text
            Text(
                text = "💡 Tap to generate fresh creative ideas",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
