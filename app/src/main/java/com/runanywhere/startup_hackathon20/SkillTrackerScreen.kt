package com.runanywhere.startup_hackathon20

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SkillTrackerScreen() {
    // 🌈 Background gradient setup
    val gradientColors = listOf(Color(0xFF283048), Color(0xFF859398))

    val infiniteTransition = rememberInfiniteTransition()
    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            tween(12000, easing = LinearEasing),
            RepeatMode.Reverse
        )
    )

    val particleAnim = rememberInfiniteTransition()
    val particleShift by particleAnim.animateFloat(
        initialValue = 0f, targetValue = 800f,
        animationSpec = infiniteRepeatable(
            tween(7000, easing = LinearEasing),
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
            .padding(24.dp)
    ) {
        // ✨ Moving glowing particles
        for (i in 1..30) {
            val randomX = remember { (0..1000).random().toFloat() }
            val randomY = remember { (0..1800).random().toFloat() }
            Canvas(
                modifier = Modifier
                    .size((6..10).random().dp)
                    .offset(
                        x = (randomX + particleShift / 12).dp,
                        y = (randomY - particleShift / 20).dp
                    )
                    .blur((6..12).random().dp)
            ) {
                drawCircle(Color.White.copy(alpha = 0.15f), style = Stroke(2f))
            }
        }

        // 🌟 Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "📊 Skill Progress Tracker",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Track your learning growth — visualize your success!",
                    color = Color(0xFFD1E8FF),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🎯 Animated progress bars for different skills
            SkillProgressBar("Python", Color(0xFF00C6FF), Color(0xFF0072FF), 0.85f)
            SkillProgressBar("Data Science", Color(0xFFFFA726), Color(0xFFFF7043), 0.7f)
            SkillProgressBar("UI/UX Design", Color(0xFFAB47BC), Color(0xFF8E24AA), 0.6f)
            SkillProgressBar("Machine Learning", Color(0xFF66BB6A), Color(0xFF43A047), 0.5f)
            SkillProgressBar("Communication", Color(0xFFF06292), Color(0xFFEC407A), 0.9f)

            Spacer(modifier = Modifier.height(32.dp))

            // 🔮 Motivational Message
            val fadeAnim = infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    tween(2000, easing = LinearEasing),
                    RepeatMode.Reverse
                )
            )

            Text(
                text = "\"Consistency turns skills into superpowers.\"",
                color = Color.White.copy(alpha = fadeAnim.value),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ✨ Action Button
            val shimmerAlpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    tween(1500, easing = LinearEasing),
                    RepeatMode.Reverse
                )
            )

            Button(
                onClick = { /* Add navigation or analytics */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = shimmerAlpha)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "🏆 View Achievement Summary",
                    color = Color(0xFF001F54),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// 🔹 Skill Progress Component
@Composable
fun SkillProgressBar(skill: String, startColor: Color, endColor: Color, progress: Float) {
    var animatedProgress by remember { mutableStateOf(0f) }

    // Animate progress smoothly
    LaunchedEffect(progress) {
        delay(300)
        animate(
            initialValue = 0f,
            targetValue = progress,
            animationSpec = tween(1500, easing = FastOutSlowInEasing)
        ) { value, _ ->
            animatedProgress = value
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = skill,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(10.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(18.dp)
                    .background(
                        brush = Brush.horizontalGradient(listOf(startColor, endColor)),
                        shape = RoundedCornerShape(10.dp)
                    )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 14.sp
        )
    }
}
