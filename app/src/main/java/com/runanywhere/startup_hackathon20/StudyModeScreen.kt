package com.runanywhere.startup_hackathon20

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyModeScreen(navController: NavController) {
    var topic by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var shouldGenerate by remember { mutableStateOf(false) }

    // Handle summary generation
    LaunchedEffect(shouldGenerate) {
        if (shouldGenerate && topic.isNotBlank()) {
            isGenerating = true
            summary = ""
            delay(2000)
            summary = "Here's a quick summary of \"$topic\":\n\n" +
                    "This topic explores key principles and real-world applications in a simple, easy-to-understand way..."
            isGenerating = false
            shouldGenerate = false
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF2E0249), Color(0xFF570A57), Color(0xFFA91079))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(20.dp)
    ) {
        // 🌌 Particle background
        StudyParticleAnimation()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "📘 Study Mode",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Learn smarter with AI! Enter any topic to generate instant summaries.",
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            // Topic Input
            TextField(
                value = topic,
                onValueChange = { topic = it },
                placeholder = { Text("Enter topic here...", color = Color.White.copy(0.7f)) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(0.15f),
                    unfocusedContainerColor = Color.White.copy(0.15f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                shape = RoundedCornerShape(20.dp),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (topic.isNotBlank()) shouldGenerate = true
                    }
                )
            )

            // Generate Button
            Button(
                onClick = {
                    if (topic.isNotBlank()) {
                        shouldGenerate = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .height(55.dp)
                    .width(220.dp)
                    .shadow(8.dp, RoundedCornerShape(30.dp))
            ) {
                Text(
                    text = if (isGenerating) "Analyzing..." else "Generate Summary",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isGenerating) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            // Summary Section
            AnimatedContent(targetState = summary, label = "") { text ->
                if (text.isNotBlank()) {
                    Surface(
                        color = Color.White.copy(0.15f),
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(
                            text = text,
                            color = Color.White,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Glowing Back Button
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = BorderStroke(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF7F00FF), Color(0xFFE100FF))
                        )
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .height(52.dp)
                        .width(240.dp)
                        .shadow(8.dp, RoundedCornerShape(50))
                ) {
                    Text(
                        text = "← Back to Dashboard",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "🔥 Study Streak: 5 Days",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Animated Quote
                val quotes = listOf(
                    "Consistency creates mastery 🌟",
                    "Stay curious, keep learning 💡",
                    "Your focus is your superpower ⚡",
                    "Every page you read adds to your future ✨"
                )
                var currentQuoteIndex by remember { mutableStateOf(0) }

                LaunchedEffect(Unit) {
                    while (true) {
                        delay(4000)
                        currentQuoteIndex = (currentQuoteIndex + 1) % quotes.size
                    }
                }

                AnimatedContent(targetState = currentQuoteIndex, label = "") { index ->
                    Text(
                        text = quotes[index],
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.alpha(0.9f)
                    )
                }
            }
        }
    }
}

@Composable
fun StudyParticleAnimation() {
    val particles = remember {
        List(30) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 4 + 1,
                speedY = Random.nextFloat() * 0.002f + 0.001f,
                alpha = Random.nextFloat() * 0.6f + 0.2f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        particles.forEach { particle ->
            val newY = (particle.y + particle.speedY * offsetY) % 1f
            drawCircle(
                color = Color.White.copy(alpha = particle.alpha),
                radius = particle.radius,
                center = Offset(particle.x * canvasWidth, newY * canvasHeight)
            )
        }
    }
}

data class Particle(
    val x: Float,
    val y: Float,
    val radius: Float,
    val speedY: Float,
    val alpha: Float
)
