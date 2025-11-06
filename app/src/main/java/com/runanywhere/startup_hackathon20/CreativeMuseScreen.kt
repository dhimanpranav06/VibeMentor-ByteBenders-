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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
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
    var idea by remember { mutableStateOf("✨ Tap the button to spark your next idea!") }
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
        // Soft moving light particles (smaller, smoother)
        repeat(20) {
            val x = remember { Random.nextInt(0, 900).toFloat() }
            val y = remember { Random.nextInt(0, 1600).toFloat() }
            val alphaAnim = infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 0.8f,
                animationSpec = infiniteRepeatable(
                    tween((4000..8000).random(), easing = LinearEasing),
                    RepeatMode.Reverse
                ),
                label = "particleAlpha$it"
            )

            Canvas(
                modifier = Modifier
                    .offset(x.dp, y.dp)
                    .size((6..12).random().dp)
                    .blur(16.dp)
                    .alpha(alphaAnim.value)
            ) {
                drawCircle(Color.White.copy(alpha = 0.15f))
            }
        }

        // 🌟 Floating glow behind header
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopCenter)
                .offset(y = 100.dp)
                .blur(180.dp)
                .background(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFFE1BEE7).copy(alpha = 0.4f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        // 🌈 Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🎨 Creative Muse",
                    color = Color.White,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Fuel your imagination — AI ideas, slogans & palettes just for you!",
                    color = Color(0xFFECEAFF),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            // 💡 Idea Display Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp)
                    .padding(horizontal = 10.dp)
                    .background(Color.Transparent),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    AnimatedContent(
                        targetState = idea,
                        transitionSpec = {
                            fadeIn(tween(400)) togetherWith fadeOut(tween(400))
                        },
                        label = "ideaAnim"
                    ) { text ->
                        Text(
                            text = text,
                            color = Color.White,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 🚀 Spark Button with pulse
            var pressed by remember { mutableStateOf(false) }
            val pulse by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.08f,
                animationSpec = infiniteRepeatable(
                    tween(1000, easing = FastOutSlowInEasing),
                    RepeatMode.Reverse
                ),
                label = "pulse"
            )

            Box(
                modifier = Modifier
                    .scale(if (pressed) 0.95f else pulse)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        pressed = true
                        coroutineScope.launch {
                            isGenerating = true
                            idea = "✨ Generating your creative spark..."
                            val ideas = listOf(
                                "🌈 Design concept: ‘Dreams in Motion’",
                                "🎬 Reel Idea: ‘A Frame of Inspiration’",
                                "🖌️ Palette: Coral Blush + Deep Violet + Sky Glow",
                                "✨ Quote: ‘Where AI meets imagination’",
                                "📖 Story Hook: ‘The Code That Painted Emotions’",
                                "🚀 Brand Name: ‘InspiroLab – Crafting Ideas’"
                            )
                            delay(1500)
                            idea = ideas.random()
                            isGenerating = false
                            pressed = false
                        }
                    }
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFFDA4453), Color(0xFF89216B))
                        ),
                        shape = RoundedCornerShape(25.dp)
                    )
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isGenerating) "✨ Creating..." else "🚀 Spark an Idea!",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Tap to explore infinite imagination ✨",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp
            )
        }
    }
}
