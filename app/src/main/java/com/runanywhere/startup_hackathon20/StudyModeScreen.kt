package com.runanywhere.startup_hackathon20

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
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
import com.runanywhere.sdk.public.RunAnywhere
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.runanywhere.startup_hackathon20.api.WikipediaService
import com.runanywhere.startup_hackathon20.api.GeminiService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyModeScreen(navController: NavController) {
    var topic by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var shouldGenerate by remember { mutableStateOf(false) }
    var relatedTopics by remember { mutableStateOf<List<String>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // AI fallback - generates content when Wikipedia is unavailable
    suspend fun generateAIContent(topic: String): String {
        Log.d("StudyMode", "Generating AI content for: $topic")
        var result = ""

        try {
            val prompt =
                """You are an expert educator. Provide comprehensive study material about "$topic".

Include:
📚 **Overview** - What is $topic and why is it important?
🎯 **Key Concepts** - Main ideas and principles
💡 **Applications** - Real-world uses and examples
🔑 **Important Facts** - Key points to remember

Write at least 3-4 detailed paragraphs. Be specific and educational!"""

            RunAnywhere.generateStream(prompt)
                .catch { e ->
                    Log.e("StudyMode", "AI generation failed: ${e.message}")
                    result = generateFallbackSummary(topic)
                }
                .collect { token -> result += token }

            return if (result.length > 100) result else generateFallbackSummary(topic)
        } catch (e: Exception) {
            Log.e("StudyMode", "AI content exception: ${e.message}")
            return generateFallbackSummary(topic)
        }
    }

    // SIMPLIFIED: Generate study material using Gemini API FIRST
    suspend fun generateComprehensiveStudyMaterial(topic: String): String {
        Log.d("StudyMode", "=== Starting content generation for: $topic ===")

        try {
            // STEP 1: Always try Gemini API FIRST
            Log.d("StudyMode", "Fetching from Gemini API...")
            val geminiResult = GeminiService.getTopicSummary(topic)

            Log.d(
                "StudyMode", "Gemini result - Success: ${geminiResult.success}, " +
                        "Summary length: ${geminiResult.summary.length}, " +
                        "Error: ${geminiResult.error}"
            )

            if (geminiResult.success && geminiResult.summary.isNotEmpty()) {
                Log.d("StudyMode", "✅ Got Gemini content! Length: ${geminiResult.summary.length}")

                // Get related topics
                relatedTopics = GeminiService.getRelatedTopics(topic)
                Log.d("StudyMode", "Found ${relatedTopics.size} related topics")

                // Try to enhance with AI (optional)
                var aiEnhancement = ""
                try {
                    val aiPrompt =
                        """Based on this Gemini article about "$topic", create a brief study guide with:
                    
🎯 **Key Takeaways** (3-4 bullet points)
💡 **Study Tips** (2-3 tips for learning this topic)

Keep it concise! Gemini content:
${geminiResult.summary.take(1500)}"""

                    RunAnywhere.generateStream(aiPrompt)
                        .catch { Log.d("StudyMode", "AI enhancement failed") }
                        .collect { aiEnhancement += it }
                } catch (e: Exception) {
                    Log.d("StudyMode", "AI enhancement exception: ${e.message}")
                }

                // Return Gemini content with optional AI enhancement
                return if (aiEnhancement.length > 50) {
                    """${geminiResult.summary}

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

✨ **Study Guide**

$aiEnhancement

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"""
                } else {
                    geminiResult.summary
                }
            } else {
                // Gemini failed - log why
                Log.e("StudyMode", "❌ Gemini failed: ${geminiResult.error}")

                // Try Wikipedia as fallback
                Log.d("StudyMode", "Trying Wikipedia fallback...")
                try {
                    val wikiResult = WikipediaService.getTopicSummary(topic)

                    Log.d(
                        "StudyMode", "Wikipedia result - Success: ${wikiResult.success}, " +
                                "Summary length: ${wikiResult.summary.length}, " +
                                "Error: ${wikiResult.error}"
                    )

                    if (wikiResult.success && wikiResult.summary.isNotEmpty()) {
                        Log.d(
                            "StudyMode",
                            "✅ Got Wikipedia content! Length: ${wikiResult.summary.length}"
                        )

                        // Get related topics
                        relatedTopics = WikipediaService.getRelatedTopics(topic)
                        Log.d("StudyMode", "Found ${relatedTopics.size} related topics")

                        // Try to enhance with AI (optional)
                        var aiEnhancement = ""
                        try {
                            val aiPrompt =
                                """Based on this Wikipedia article about "$topic", create a brief study guide with:
                            
🎯 **Key Takeaways** (3-4 bullet points)
💡 **Study Tips** (2-3 tips for learning this topic)

Keep it concise! Wikipedia content:
${wikiResult.summary.take(1500)}"""

                            RunAnywhere.generateStream(aiPrompt)
                                .catch { Log.d("StudyMode", "AI enhancement failed") }
                                .collect { aiEnhancement += it }
                        } catch (e: Exception) {
                            Log.d("StudyMode", "AI enhancement exception: ${e.message}")
                        }

                        // Return Wikipedia content with optional AI enhancement
                        return if (aiEnhancement.length > 50) {
                            """${wikiResult.summary}

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

✨ **Study Guide**

$aiEnhancement

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"""
                        } else {
                            wikiResult.summary
                        }
                    } else {
                        // Wikipedia failed - log why
                        Log.e("StudyMode", "❌ Wikipedia failed: ${wikiResult.error}")

                        // Try AI as complete fallback
                        Log.d("StudyMode", "Trying AI fallback...")
                        return generateAIContent(topic)
                    }
                } catch (e: Exception) {
                    Log.e("StudyMode", "Exception in Wikipedia fallback: ${e.message}")
                    return generateAIContent(topic)
                }
            }

        } catch (e: Exception) {
            Log.e("StudyMode", "Exception in content generation: ${e.message}", e)
            return generateAIContent(topic)
        }
    }

    // Handle summary generation
    LaunchedEffect(shouldGenerate) {
        if (shouldGenerate && topic.isNotBlank()) {
            isGenerating = true
            summary = """🔍 Searching "$topic" on Gemini...

⏳ Please wait:
   • Fetching from Gemini
   • Getting comprehensive article content
   • Loading related topics
   
This may take a few seconds!"""

            // Small delay for better UX
            delay(500)

            val generatedSummary = generateComprehensiveStudyMaterial(topic)
            summary = generatedSummary

            Log.d("StudyMode", "Final summary length: ${summary.length}")

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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "📘 Study Mode",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "📚 Get comprehensive information from Gemini + AI enhancements!",
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            // Topic Input
            TextField(
                value = topic,
                onValueChange = { topic = it },
                placeholder = {
                    Text(
                        "Search any topic (e.g., Quantum Physics, Python, History)...",
                        color = Color.White.copy(0.6f)
                    )
                },
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
                        if (topic.isNotBlank() && !isGenerating) {
                            shouldGenerate = true
                        }
                    }
                ),
                enabled = !isGenerating
            )

            // Generate Button
            Button(
                onClick = {
                    if (topic.isNotBlank() && !isGenerating) {
                        shouldGenerate = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .height(55.dp)
                    .width(240.dp)
                    .shadow(8.dp, RoundedCornerShape(30.dp)),
                enabled = !isGenerating && topic.isNotBlank()
            ) {
                if (isGenerating) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Fetching...",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = "🔍 Search & Learn",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            // Summary Section - Comprehensive content from Gemini
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
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = text,
                                color = Color.White,
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            )

                            // Related Topics Section
                            if (relatedTopics.isNotEmpty() && !isGenerating) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Divider(color = Color.White.copy(0.3f))
                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "🔗 Related Topics:",
                                    color = Color(0xFF00E0FF),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                relatedTopics.take(3).forEach { relatedTopic ->
                                    TextButton(
                                        onClick = {
                                            topic = relatedTopic
                                            shouldGenerate = true
                                        },
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Text("→ $relatedTopic", fontSize = 14.sp)
                                    }
                                }
                            }

                            // Quiz Button
                            if (!isGenerating && topic.isNotBlank() && !text.contains("🔍 Searching")) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        navController.navigate("quiz/$topic")
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00E0FF)
                                    ),
                                    shape = RoundedCornerShape(25.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                ) {
                                    Text(
                                        text = "📝 Take Quiz on This Topic",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
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
                // Back Button
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
                    "Real knowledge from Gemini 📚",
                    "Learn with verified information ✨",
                    "Your focus is your superpower ⚡",
                    "Every search expands your knowledge 🌐",
                    "Gemini + AI = Better Learning 🎓"
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

// Enhanced fallback summary generator - provides structured, detailed content
fun generateFallbackSummary(topic: String): String {
    return """📚 **Understanding $topic**

**Overview:**
$topic is an important concept that plays a significant role in its field. Understanding this topic helps build a strong foundation for further learning and practical applications.

🎯 **Key Concepts:**

• **Definition**: $topic encompasses fundamental principles and methodologies that are essential for mastery
• **Core Components**: The main elements include theoretical understanding, practical application, and real-world problem-solving
• **Learning Approach**: Best learned through a combination of study, practice, and hands-on experience

💡 **Real-World Applications:**

• Used extensively in professional settings and academic research
• Applies to solving everyday challenges and industry-specific problems
• Foundation for advanced topics and specialized areas of study

🔑 **Important Points:**

• Start with fundamentals before moving to advanced concepts
• Practice regularly to reinforce understanding
• Connect theory with practical examples
• Ask questions and explore different perspectives

📈 **Why It Matters:**

Understanding $topic is crucial for:
- Building expertise in related fields
- Solving complex problems effectively
- Staying relevant in modern contexts
- Advancing your knowledge and career

💪 **Next Steps:**
Continue exploring related topics, practice with examples, and test your knowledge with the quiz below!

---
✨ Generated by VibeMentor AI Study Assistant"""
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