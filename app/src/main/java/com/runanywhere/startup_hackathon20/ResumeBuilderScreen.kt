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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ResumeBuilderScreen() {
    val gradientColors = listOf(
        Color(0xFF000428), // Deep navy
        Color(0xFF004E92)  // Bright blue
    )

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
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        // 🌟 Floating glowing particles
        for (i in 1..25) {
            val randomX = remember { (0..1000).random().toFloat() }
            val randomY = remember { (0..1800).random().toFloat() }
            Canvas(
                modifier = Modifier
                    .size((6..10).random().dp)
                    .offset(
                        x = (randomX + particleShift / 10).dp,
                        y = (randomY - particleShift / 20).dp
                    )
                    .blur((6..12).random().dp)
            ) {
                drawCircle(Color.White.copy(alpha = 0.15f), style = Stroke(2f))
            }
        }

        // 🧩 Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // 🏁 Header Section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "💼 AI Resume Builder",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Let AI craft your perfect resume — effortlessly!",
                    color = Color(0xFFB3E5FC),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔤 Input Fields
            var name by remember { mutableStateOf("") }
            var skills by remember { mutableStateOf("") }
            var experience by remember { mutableStateOf("") }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "📝 Enter Your Details",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    ResumeTextField(value = name, onValueChange = { name = it }, label = "Full Name")
                    Spacer(modifier = Modifier.height(10.dp))
                    ResumeTextField(value = skills, onValueChange = { skills = it }, label = "Skills (comma separated)")
                    Spacer(modifier = Modifier.height(10.dp))
                    ResumeTextField(value = experience, onValueChange = { experience = it }, label = "Experience Summary")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 📄 Resume Preview Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "✨ Resume Preview",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = buildString {
                            append("👤 Name: $name\n\n")
                            append("💡 Skills: $skills\n\n")
                            append("🧩 Experience:\n$experience")
                        },
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ✨ Glowing Generate Button
            val shimmerAlpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    tween(1500, easing = LinearEasing),
                    RepeatMode.Reverse
                )
            )

            Button(
                onClick = { /* Add PDF Generation Logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .scale(1.05f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = shimmerAlpha)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "⚡ Generate AI Resume",
                    color = Color(0xFF001F54),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// 🔧 Reusable TextField Component
@Composable
fun ResumeTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFFB3E5FC)) },
        textStyle = LocalTextStyle.current.copy(color = Color.White),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.1f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
            focusedIndicatorColor = Color(0xFFB3E5FC),
            unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
}
