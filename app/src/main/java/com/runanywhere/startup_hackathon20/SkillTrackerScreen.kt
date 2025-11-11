package com.runanywhere.startup_hackathon20

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

@Composable
fun SkillTrackerScreen(skillTrackerViewModel: SkillTrackerViewModel = viewModel()) {
    val skills by skillTrackerViewModel.skills.collectAsState()
    val suggestedSkills by skillTrackerViewModel.suggestedSkills.collectAsState()
    val error by skillTrackerViewModel.error.collectAsState()
    val isLoading by skillTrackerViewModel.isLoading.collectAsState()
    val learningPathAnalysis by skillTrackerViewModel.learningPathAnalysis.collectAsState()
    val skillTips by skillTrackerViewModel.skillTips.collectAsState()

    var interests by remember { mutableStateOf("") }

    val gradientColors = listOf(Color(0xFF283048), Color(0xFF859398))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(colors = gradientColors))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📊 AI Skill Tracker",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Track your growth with AI-powered insights",
                color = Color(0xFFD1E8FF),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Skills List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(skills) { skill ->
                    SkillProgressBar(
                        skill = skill,
                        onSkillClick = {
                            skillTrackerViewModel.getSkillDevelopmentTips(
                                skill.name,
                                skill.progress
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interest Input
            OutlinedTextField(
                value = interests,
                onValueChange = { interests = it },
                label = { Text("Enter your interests (e.g., 'web development')") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    cursorColor = Color.White
                ),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { skillTrackerViewModel.getSkillSuggestions(interests) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C6FF)),
                    enabled = !isLoading && interests.isNotBlank()
                ) {
                    Text("🎯 Suggest Skills")
                }

                Button(
                    onClick = { skillTrackerViewModel.getLearningPathAnalysis(interests) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                    enabled = !isLoading && interests.isNotBlank()
                ) {
                    Text("📚 Learning Path")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons Row 2
            Button(
                onClick = { skillTrackerViewModel.generateCareerRecommendations() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                enabled = !isLoading
            ) {
                Text("💼 Career Recommendations")
            }

            // Loading indicator
            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "AI is thinking...",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Handle suggested skills dialog
            if (suggestedSkills.isNotEmpty()) {
                AlertDialog(
                    onDismissRequest = { skillTrackerViewModel.clearSuggestions() },
                    title = {
                        Text(
                            "🎯 AI-Suggested Skills",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    text = {
                        Column {
                            Text(
                                "Based on your interests, here are skills you should learn:",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            suggestedSkills.forEachIndexed { index, skill ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    Text(
                                        "${index + 1}. ",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(skill, fontSize = 16.sp)
                                }
                                if (index < suggestedSkills.size - 1) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { skillTrackerViewModel.clearSuggestions() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C6FF))
                        ) {
                            Text("Got it!")
                        }
                    }
                )
            }

            // Handle learning path analysis dialog
            learningPathAnalysis?.let { analysis ->
                AlertDialog(
                    onDismissRequest = { skillTrackerViewModel.clearLearningPathAnalysis() },
                    title = {
                        Text(
                            "📚 Your Learning Path",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    text = {
                        LazyColumn {
                            item {
                                Text(
                                    analysis,
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { skillTrackerViewModel.clearLearningPathAnalysis() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
                        ) {
                            Text("Awesome!")
                        }
                    }
                )
            }

            // Handle skill tips dialog
            skillTips?.let { tips ->
                AlertDialog(
                    onDismissRequest = { skillTrackerViewModel.clearSkillTips() },
                    title = {
                        Text(
                            "💡 Skill Development Tips",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    text = {
                        LazyColumn {
                            item {
                                Text(
                                    tips,
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { skillTrackerViewModel.clearSkillTips() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Thanks!")
                        }
                    }
                )
            }

            // Handle error dialog
            error?.let {
                AlertDialog(
                    onDismissRequest = { skillTrackerViewModel.clearError() },
                    title = {
                        Text(
                            "⚠️ Notice",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    text = {
                        Text(
                            it,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = { skillTrackerViewModel.clearError() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))
                        ) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SkillProgressBar(skill: Skill, onSkillClick: () -> Unit) {
    var animatedProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(skill.progress) {
        delay(300)
        animate(
            initialValue = 0f,
            targetValue = skill.progress,
            animationSpec = tween(1500, easing = FastOutSlowInEasing)
        ) { value, _ ->
            animatedProgress = value
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSkillClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = skill.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(12.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(skill.color, skill.color.copy(alpha = 0.7f))
                            ),
                            shape = RoundedCornerShape(6.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "💡 Tap for AI tips",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}
