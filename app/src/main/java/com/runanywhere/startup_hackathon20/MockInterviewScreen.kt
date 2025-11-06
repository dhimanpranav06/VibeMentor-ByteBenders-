package com.runanywhere.startup_hackathon20

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MockInterviewScreen(navController: NavController) {
    val gradient = listOf(Color(0xFF8360C3), Color(0xFF2EBF91))
    val infiniteTransition = rememberInfiniteTransition()
    val offset by infiniteTransition.animateFloat(0f, 1000f, infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Reverse))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(gradient, start = androidx.compose.ui.geometry.Offset(offset, 0f), end = androidx.compose.ui.geometry.Offset(0f, offset))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🎤 AI Mock Interview Coach", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            Text("Answer AI questions and get instant performance feedback 🧠", color = Color.White.copy(alpha = 0.9f), fontSize = 18.sp)
            Spacer(modifier = Modifier.height(40.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("⬅ Back", color = Color.White)
            }
        }
    }
}
