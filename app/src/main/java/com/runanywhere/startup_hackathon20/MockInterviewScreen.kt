package com.runanywhere.startup_hackathon20

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.navigation.NavController
import com.runanywhere.startup_hackathon20.api.NvidiaService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class InterviewQuestion(
    val question: String,
    val expectedPoints: List<String> = emptyList()
)

data class InterviewResult(
    val question: String,
    val answer: String,
    val feedback: String,
    val score: Int // out of 10
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockInterviewScreen(navController: NavController) {
    val gradient = listOf(Color(0xFF8360C3), Color(0xFF2EBF91))
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val offset by infiniteTransition.animateFloat(
        0f, 1000f,
        infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Reverse),
        label = ""
    )

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var interviewStarted by remember { mutableStateOf(false) }
    var role by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var questions by remember { mutableStateOf<List<InterviewQuestion>>(emptyList()) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var currentAnswer by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<InterviewResult>>(emptyList()) }
    var isGenerating by remember { mutableStateOf(false) }
    var isEvaluating by remember { mutableStateOf(false) }
    var showResults by remember { mutableStateOf(false) }

    // Generate interview questions
    suspend fun generateInterviewQuestions(role: String, experience: String) {
        isGenerating = true
        try {
            val response = NvidiaService.generateInterviewQuestions(role, experience)

            questions = parseInterviewQuestions(response, role)
            if (questions.isEmpty()) {
                questions = getDefaultQuestions(role)
            }
            interviewStarted = true
        } catch (e: Exception) {
            questions = getDefaultQuestions(role)
            interviewStarted = true
        } finally {
            isGenerating = false
        }
    }

    // Evaluate answer with AI
    suspend fun evaluateAnswer(question: String, answer: String): InterviewResult {
        isEvaluating = true
        var feedback = ""
        var score = 5

        try {
            val (aiScore, aiFeedback) = NvidiaService.evaluateInterviewAnswer(question, answer)
            score = aiScore
            feedback = aiFeedback
        } catch (e: Exception) {
            feedback = "Good effort! Focus on being more specific and providing concrete examples."
            score = 6
        } finally {
            isEvaluating = false
        }

        return InterviewResult(question, answer, feedback, score)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    gradient,
                    start = androidx.compose.ui.geometry.Offset(offset, 0f),
                    end = androidx.compose.ui.geometry.Offset(0f, offset)
                )
            )
    ) {
        when {
            !interviewStarted && !showResults -> {
                // Setup screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "🎤 AI Mock Interview Coach",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Practice interviews with AI and get instant feedback!",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // Role Input
                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = { Text("Job Role/Position", color = Color.White.copy(0.8f)) },
                        placeholder = {
                            Text(
                                "e.g., Software Engineer, Product Manager",
                                color = Color.White.copy(0.5f)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(0.15f),
                            unfocusedContainerColor = Color.White.copy(0.1f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedIndicatorColor = Color(0xFF00E0FF),
                            unfocusedIndicatorColor = Color.White.copy(0.3f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Experience Input
                    OutlinedTextField(
                        value = experience,
                        onValueChange = { experience = it },
                        label = { Text("Experience Level", color = Color.White.copy(0.8f)) },
                        placeholder = {
                            Text(
                                "e.g., Entry Level, 2-3 years, Senior",
                                color = Color.White.copy(0.5f)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(0.15f),
                            unfocusedContainerColor = Color.White.copy(0.1f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedIndicatorColor = Color(0xFF00E0FF),
                            unfocusedIndicatorColor = Color.White.copy(0.3f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    if (isGenerating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "🤖 Preparing your interview questions...",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    } else {
                        Button(
                            onClick = {
                                if (role.isNotBlank()) {
                                    coroutineScope.launch {
                                        generateInterviewQuestions(role, experience)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E0FF)),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text(
                                "Start AI Interview 🚀",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(onClick = { navController.popBackStack() }) {
                            Text("← Back", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            }

            interviewStarted && !showResults -> {
                // Interview in progress
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(scrollState)
                ) {
                    // Progress
                    Text(
                        "Question ${currentQuestionIndex + 1}/${questions.size}",
                        color = Color.White.copy(0.8f),
                        fontSize = 16.sp
                    )
                    LinearProgressIndicator(
                        progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = Color(0xFF00E0FF)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Question
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.15f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                "Question:",
                                color = Color(0xFF00E0FF),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                questions[currentQuestionIndex].question,
                                color = Color.White,
                                fontSize = 18.sp,
                                lineHeight = 26.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Answer input
                    OutlinedTextField(
                        value = currentAnswer,
                        onValueChange = { currentAnswer = it },
                        label = { Text("Your Answer", color = Color.White.copy(0.8f)) },
                        placeholder = {
                            Text(
                                "Type your response here...",
                                color = Color.White.copy(0.5f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 150.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(0.15f),
                            unfocusedContainerColor = Color.White.copy(0.1f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedIndicatorColor = Color(0xFF00E0FF),
                            unfocusedIndicatorColor = Color.White.copy(0.3f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        minLines = 5
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isEvaluating) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Evaluating your answer...", color = Color.White, fontSize = 16.sp)
                        }
                    } else {
                        Button(
                            onClick = {
                                if (currentAnswer.isNotBlank()) {
                                    coroutineScope.launch {
                                        val result = evaluateAnswer(
                                            questions[currentQuestionIndex].question,
                                            currentAnswer
                                        )
                                        results = results + result
                                        currentAnswer = ""

                                        if (currentQuestionIndex < questions.size - 1) {
                                            currentQuestionIndex++
                                        } else {
                                            showResults = true
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = currentAnswer.isNotBlank() && !isEvaluating,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text(
                                if (currentQuestionIndex < questions.size - 1) "Next Question →" else "Finish Interview ✓",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            showResults -> {
                // Results screen
                InterviewResultsScreen(
                    results = results,
                    onRetry = {
                        interviewStarted = false
                        showResults = false
                        currentQuestionIndex = 0
                        currentAnswer = ""
                        results = emptyList()
                        questions = emptyList()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun InterviewResultsScreen(
    results: List<InterviewResult>,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val averageScore = if (results.isNotEmpty()) results.map { it.score }.average() else 0.0
    val confidence = when {
        averageScore >= 8.5 -> "Excellent! 🏆"
        averageScore >= 7.0 -> "Great! 🌟"
        averageScore >= 5.5 -> "Good! 👍"
        else -> "Keep Practicing! 💪"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Interview Complete!",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Overall score card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.2f)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    confidence,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Overall Score: %.1f/10".format(averageScore),
                    fontSize = 28.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Confidence Level: ${(averageScore * 10).toInt()}%",
                    fontSize = 18.sp,
                    color = Color.White.copy(0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Detailed Feedback",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Individual question feedback
        results.forEachIndexed { index, result ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.15f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Q${index + 1}",
                            color = Color(0xFF00E0FF),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${result.score}/10",
                            color = Color(0xFFFFC107),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        result.feedback,
                        color = Color.White.copy(0.9f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons
        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E0FF)),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                "🔄 Try Another Interview",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onBack) {
            Text("← Back to Career Mode", color = Color.White, fontSize = 16.sp)
        }
    }
}

// Helper functions
fun parseInterviewQuestions(response: String, role: String): List<InterviewQuestion> {
    val questions = mutableListOf<InterviewQuestion>()
    try {
        val lines = response.lines().filter { it.trim().isNotEmpty() }
        lines.forEach { line ->
            val trimmed = line.trim()
            // Match patterns like "1. Question" or "1) Question" or "Q1: Question"
            if (trimmed.matches(Regex("^\\d+[.):]\\s*.+")) || trimmed.startsWith("Q")) {
                val questionText =
                    trimmed.replaceFirst(Regex("^\\d+[.):]\\s*|^Q\\d+:\\s*"), "").trim()
                if (questionText.length > 10) {
                    questions.add(InterviewQuestion(questionText))
                }
            }
        }
    } catch (e: Exception) {
        // Return empty, will use defaults
    }
    return questions
}

fun generateFallbackQuestions(role: String): String {
    return """
1. Tell me about yourself and your experience relevant to this $role position.
2. What are your key strengths that make you suitable for this $role role?
3. Describe a challenging situation you faced and how you resolved it.
4. Where do you see yourself in 3-5 years in your career as a $role?
5. Why are you interested in this $role position and what unique value can you bring?
""".trim()
}

fun getDefaultQuestions(role: String): List<InterviewQuestion> {
    return listOf(
        InterviewQuestion("Tell me about yourself and your experience relevant to this $role position."),
        InterviewQuestion("What are your key strengths that make you suitable for this $role role?"),
        InterviewQuestion("Describe a challenging situation you faced and how you resolved it."),
        InterviewQuestion("Where do you see yourself in 3-5 years in your career?"),
        InterviewQuestion("Why are you interested in this $role position and what unique value can you bring?")
    )
}
