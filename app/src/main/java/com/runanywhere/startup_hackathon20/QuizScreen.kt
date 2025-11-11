package com.runanywhere.startup_hackathon20

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.sdk.public.RunAnywhere
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlin.random.Random
import com.runanywhere.startup_hackathon20.api.WikipediaService

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: Int
)

@Composable
fun QuizScreen(navController: NavController, topic: String) {
    var questions by remember { mutableStateOf<List<QuizQuestion>>(emptyList()) }
    var isGenerating by remember { mutableStateOf(false) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableStateOf(0) }
    var showResult by remember { mutableStateOf(false) }
    var quizStarted by remember { mutableStateOf(false) }
    var wikipediaContent by remember { mutableStateOf("") }

    // Generate quiz questions using Wikipedia API
    suspend fun generateQuiz(topic: String) {
        isGenerating = true
        try {
            // STEP 1: Fetch comprehensive Wikipedia content
            val wikiResult = WikipediaService.getTopicSummary(topic)

            if (wikiResult.success && wikiResult.summary.isNotEmpty()) {
                wikipediaContent = wikiResult.summary

                // STEP 2: Use AI to generate quiz questions BASED ON Wikipedia content
                // This ensures questions are factual and based on real information
                val prompt =
                    """You are an expert educator. Based on the following Wikipedia article about "$topic", generate 5 multiple choice quiz questions.

IMPORTANT: 
- Only create questions about facts and information present in the Wikipedia article below
- Ensure all questions are directly answerable from the article content
- Make questions progressively challenging
- Ensure questions are clear and specific
- Make all options plausible

Wikipedia Article:
${wikipediaContent.take(3000)}

Format EXACTLY as shown:
Q1: [First question based on the article]
A) [First option]
B) [Second option]
C) [Third option]
D) [Fourth option]
ANSWER: [A/B/C/D]

Q2: [Second question based on the article]
A) [option]
B) [option]
C) [option]
D) [option]
ANSWER: [A/B/C/D]

Continue for all 5 questions. Make them educational and based ONLY on the Wikipedia content provided!"""

                var response = ""
                var hasAIResponse = false

                try {
                    RunAnywhere.generateStream(prompt)
                        .catch { e ->
                            hasAIResponse = false
                        }
                        .collect { token ->
                            response += token
                            hasAIResponse = true
                        }
                } catch (e: Exception) {
                    hasAIResponse = false
                }

                // Parse AI response
                if (hasAIResponse && response.isNotBlank()) {
                    questions = parseQuizQuestions(response)
                }

                // If parsing failed, generate questions from Wikipedia content
                if (questions.size < 3) {
                    questions = generateQuestionsFromWikipedia(topic, wikipediaContent)
                }
            } else {
                // If Wikipedia fails, generate basic questions about the topic
                questions = generateBasicTopicQuestions(topic)
            }

            quizStarted = true
        } catch (e: Exception) {
            // Fallback to basic questions
            questions = generateBasicTopicQuestions(topic)
            quizStarted = true
        } finally {
            isGenerating = false
        }
    }

    LaunchedEffect(Unit) {
        generateQuiz(topic)
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF2E0249), Color(0xFF570A57), Color(0xFFA91079))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        // Particle animation background
        QuizParticleAnimation()

        if (isGenerating) {
            // Loading state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "🧠 Generating Quiz Questions...",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Fetching \"$topic\" from Wikipedia and creating questions...",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else if (showResult) {
            // Results screen
            QuizResultScreen(
                score = score,
                totalQuestions = questions.size,
                topic = topic,
                onRetry = {
                    currentQuestionIndex = 0
                    selectedAnswer = null
                    score = 0
                    showResult = false
                    quizStarted = true
                },
                onBackToStudy = {
                    navController.popBackStack()
                }
            )
        } else if (quizStarted && questions.isNotEmpty()) {
            // Quiz in progress
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📝 Quiz Time!",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Topic: $topic",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "📚 Questions based on Wikipedia",
                        color = Color(0xFFFFD700).copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question ${currentQuestionIndex + 1}/${questions.size}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Score: $score",
                            color = Color(0xFFFFD700),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Progress bar
                    LinearProgressIndicator(
                        progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .height(8.dp),
                        color = Color(0xFFFFC107),
                        trackColor = Color.White.copy(alpha = 0.2f),
                    )
                }

                // Question card
                AnimatedContent(
                    targetState = currentQuestionIndex,
                    transitionSpec = {
                        fadeIn(tween(400)) togetherWith fadeOut(tween(400))
                    },
                    label = "questionAnim"
                ) { index ->
                    val question = questions[index]

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Question text
                        Surface(
                            color = Color.White.copy(0.15f),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp)
                        ) {
                            Text(
                                text = question.question,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.padding(20.dp)
                            )
                        }

                        // Options
                        question.options.forEachIndexed { optionIndex, option ->
                            QuizOptionButton(
                                option = option,
                                optionLabel = ('A' + optionIndex).toString(),
                                isSelected = selectedAnswer == optionIndex,
                                onClick = { selectedAnswer = optionIndex }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                // Bottom buttons
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            selectedAnswer?.let { answer ->
                                if (answer == questions[currentQuestionIndex].correctAnswer) {
                                    score++
                                }

                                if (currentQuestionIndex < questions.size - 1) {
                                    currentQuestionIndex++
                                    selectedAnswer = null
                                } else {
                                    showResult = true
                                }
                            }
                        },
                        enabled = selectedAnswer != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFC107),
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(30.dp))
                    ) {
                        Text(
                            text = if (currentQuestionIndex < questions.size - 1) "Next Question →" else "Submit Quiz",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = { navController.popBackStack() }) {
                        Text(
                            text = "← Exit Quiz",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuizOptionButton(
    option: String,
    optionLabel: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF00E0FF) else Color.White.copy(0.15f)
    val borderColor = if (isSelected) Color(0xFF00E0FF) else Color.White.copy(0.3f)

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (isSelected) Color.White else Color(0xFFFFC107),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = optionLabel,
                        color = if (isSelected) Color(0xFF00E0FF) else Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = option,
                color = if (isSelected) Color.Black else Color.White,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun QuizResultScreen(
    score: Int,
    totalQuestions: Int,
    topic: String,
    onRetry: () -> Unit,
    onBackToStudy: () -> Unit
) {
    val percentage = (score.toFloat() / totalQuestions * 100).toInt()
    val grade = when {
        percentage >= 90 -> "🏆 Outstanding!"
        percentage >= 70 -> "🌟 Great Job!"
        percentage >= 50 -> "👍 Good Effort!"
        else -> "💪 Keep Learning!"
    }

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val celebrationScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            tween(1000, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = ""
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = grade,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD700),
            modifier = Modifier.scale(celebrationScale)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            color = Color.White.copy(0.15f),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Score",
                    color = Color.White.copy(0.8f),
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$score / $totalQuestions",
                    color = Color.White,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$percentage% Correct",
                    color = Color(0xFFFFC107),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "📚 Quiz based on Wikipedia: $topic",
                    color = Color.White.copy(0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        val message = when {
            percentage >= 90 -> "Excellent! You've mastered this topic! 🎓"
            percentage >= 70 -> "Great work! Keep it up! 📚"
            percentage >= 50 -> "Good start! Review the Wikipedia article for better results. 💡"
            else -> "Don't give up! Study the Wikipedia content and try again! 🚀"
        }

        Text(
            text = message,
            color = Color.White.copy(0.9f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Retry button
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E0FF)),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(8.dp, RoundedCornerShape(30.dp))
        ) {
            Text(
                text = "🔄 Try Again",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Back button
        Button(
            onClick = onBackToStudy,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = BorderStroke(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF7F00FF), Color(0xFFE100FF))
                )
            ),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(8.dp, RoundedCornerShape(30.dp))
        ) {
            Text(
                text = "← Back to Study Mode",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun QuizParticleAnimation() {
    val particles = remember {
        List(25) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 3 + 1,
                speedY = Random.nextFloat() * 0.002f + 0.001f,
                alpha = Random.nextFloat() * 0.5f + 0.2f
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
        ),
        label = ""
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

// Helper functions
fun parseQuizQuestions(response: String): List<QuizQuestion> {
    val questions = mutableListOf<QuizQuestion>()
    try {
        val questionBlocks = response.split(Regex("Q\\d*:|Q:")).filter { it.trim().isNotEmpty() }

        questionBlocks.forEach { block ->
            val lines = block.lines().filter { it.trim().isNotEmpty() }
            if (lines.size >= 6) {
                val questionText = lines[0].trim()
                val options = lines.subList(1, 5).map {
                    it.replace(Regex("^[A-D]\\)\\s*"), "").trim()
                }
                val answerLine = lines.find { it.contains("ANSWER", ignoreCase = true) }
                val correctAnswer = when {
                    answerLine?.contains("A", ignoreCase = true) == true -> 0
                    answerLine?.contains("B", ignoreCase = true) == true -> 1
                    answerLine?.contains("C", ignoreCase = true) == true -> 2
                    answerLine?.contains("D", ignoreCase = true) == true -> 3
                    else -> 0
                }

                if (options.size == 4 && questionText.isNotEmpty()) {
                    questions.add(QuizQuestion(questionText, options, correctAnswer))
                }
            }
        }
    } catch (e: Exception) {
        // Return empty list, will fall back to Wikipedia-based questions
    }
    return questions
}

/**
 * Generate quiz questions directly from Wikipedia content
 * This creates factual questions based on the article text
 */
fun generateQuestionsFromWikipedia(topic: String, content: String): List<QuizQuestion> {
    val questions = mutableListOf<QuizQuestion>()

    // Extract key sentences from Wikipedia content
    val sentences = content.split(". ").filter {
        it.length > 50 && !it.contains("━") && !it.contains("**") && !it.contains("http")
    }.take(10)

    if (sentences.size >= 5) {
        // Create fill-in-the-blank style questions from sentences
        sentences.take(5).forEachIndexed { index, sentence ->
            val words = sentence.split(" ").filter { it.length > 4 }
            if (words.size > 5) {
                // Find a significant word to make into a question
                val keywordIndex = words.size / 2
                val keyword = words[keywordIndex].replace(Regex("[^A-Za-z]"), "")

                if (keyword.isNotEmpty()) {
                    val questionText = sentence.replace(keyword, "____")

                    // Generate plausible wrong answers
                    val wrongOptions = generateWrongOptions(keyword, topic)
                    val allOptions = (wrongOptions + keyword).shuffled()
                    val correctAnswer = allOptions.indexOf(keyword)

                    if (correctAnswer >= 0 && questionText.contains("____")) {
                        questions.add(
                            QuizQuestion(
                                "Complete the following statement from the Wikipedia article: $questionText",
                                allOptions,
                                correctAnswer
                            )
                        )
                    }
                }
            }
        }
    }

    // If we couldn't generate enough questions, add basic topic questions
    if (questions.size < 3) {
        questions.addAll(generateBasicTopicQuestions(topic))
    }

    return questions.take(5)
}

/**
 * Generate plausible but incorrect options for quiz questions
 */
fun generateWrongOptions(correctWord: String, topic: String): List<String> {
    val alternatives = listOf(
        "process", "system", "method", "theory", "principle", "concept",
        "technique", "approach", "structure", "function", "element", "component",
        "factor", "aspect", "feature", "property", "characteristic", "attribute",
        "mechanism", "operation", "procedure", "strategy", "framework", "model"
    )

    return alternatives
        .filter { it != correctWord.lowercase() }
        .shuffled()
        .take(3)
}

/**
 * Generate basic knowledge-testing questions about any topic
 * Used as ultimate fallback when Wikipedia content is insufficient
 */
fun generateBasicTopicQuestions(topic: String): List<QuizQuestion> {
    return listOf(
        QuizQuestion(
            "What field or domain is \"$topic\" primarily associated with?",
            listOf(
                "Science and Technology",
                "Arts and Humanities",
                "Social Sciences",
                "Interdisciplinary Studies"
            ),
            0
        ),
        QuizQuestion(
            "When studying \"$topic\", which approach is most effective?",
            listOf(
                "Memorization only",
                "Understanding concepts and practicing application",
                "Ignoring theoretical foundations",
                "Learning without examples"
            ),
            1
        ),
        QuizQuestion(
            "What is a key characteristic of \"$topic\"?",
            listOf(
                "It has no practical applications",
                "It is well-documented and studied",
                "It is only theoretical with no real-world use",
                "It requires no prior knowledge"
            ),
            1
        ),
        QuizQuestion(
            "Why is it important to study \"$topic\"?",
            listOf(
                "It has no relevance to modern times",
                "It builds foundational knowledge and understanding",
                "It is outdated information",
                "Only for entertainment purposes"
            ),
            1
        ),
        QuizQuestion(
            "How can you best master \"$topic\"?",
            listOf(
                "Avoid reading and research",
                "Only watch videos without practice",
                "Combine studying theory with practical application",
                "Rely solely on others' explanations"
            ),
            2
        )
    )
}

fun generateMockQuestions(topic: String): String {
    return """
Q: What is the main concept of $topic?
A) A fundamental principle
B) An advanced technique
C) A basic operation
D) A complex theory
ANSWER: A

Q: How is $topic commonly applied?
A) In theoretical scenarios
B) In practical applications
C) Only in research
D) Rarely used
ANSWER: B

Q: What makes $topic important?
A) It's mandatory
B) It's foundational knowledge
C) It's optional
D) It's obsolete
ANSWER: B

Q: When should you use $topic?
A) Never
B) Always
C) When appropriate for the context
D) Only on weekends
ANSWER: C

Q: What is a key benefit of understanding $topic?
A) No benefits
B) Better problem-solving skills
C) More confusion
D) Less knowledge
ANSWER: B
""".trimIndent()
}

fun getMockQuestions(topic: String): List<QuizQuestion> {
    return generateBasicTopicQuestions(topic)
}

fun generateTopicBasedQuestions(topic: String): List<QuizQuestion> {
    return generateBasicTopicQuestions(topic)
}
