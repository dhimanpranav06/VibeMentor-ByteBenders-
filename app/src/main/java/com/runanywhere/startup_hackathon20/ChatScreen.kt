package com.runanywhere.startup_hackathon20

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.startup_hackathon20.api.APIConfig
import com.runanywhere.startup_hackathon20.api.GeminiService
import com.runanywhere.startup_hackathon20.api.WikipediaService
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.random.Random

// Using ChatMessage from ChatViewModel.kt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController) {
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Welcome message
    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages = listOf(
                ChatMessage(
                    text = "👋 Hello! I'm your learning assistant powered by Gemini.\n\n" +
                            "I can help you learn about:\n" +
                            "• 📚 Scientific concepts\n" +
                            "• 💡 Historical events\n" +
                            "• 🎯 General knowledge topics\n" +
                            "• 🔬 Technology and science\n" +
                            "• 📝 And much more!\n\n" +
                            "Ask me about any topic and I'll do my best to help you!",
                    isUser = false
                )
            )
        }
    }

    // Send message function
    suspend fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        // Add user message
        messages = messages + ChatMessage(
            text = userMessage,
            isUser = true
        )
        inputText = ""
        isLoading = true

        // Scroll to bottom
        coroutineScope.launch {
            listState.animateScrollToItem(messages.size)
        }

        try {
            // Use Gemini API
            val aiResponse = getGeminiChatResponse(userMessage, messages.takeLast(10))

            // Add AI response
            messages = messages + ChatMessage(
                text = aiResponse,
                isUser = false
            )

            // Scroll to bottom
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size)
            }

        } catch (e: Exception) {
            Log.e("ChatScreen", "Error getting response", e)
            messages = messages + ChatMessage(
                text = "❌ Sorry, I encountered an error: ${e.message}",
                isUser = false
            )
        } finally {
            isLoading = false
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        // Animated background
        ChatBackgroundAnimation()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            Surface(
                color = Color(0xFF1A1A2E).copy(alpha = 0.9f),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "🤖 Gemini Chat Assistant",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Powered by Google Gemini",
                            color = Color(0xFF00D9FF),
                            fontSize = 12.sp
                        )
                    }

                    // Status indicator
                    Surface(
                        color = if (GeminiService.isConfigured()) Color(0xFF00FF88) else Color.Red,
                        shape = CircleShape,
                        modifier = Modifier.size(12.dp)
                    ) {}
                }
            }

            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    ChatMessageBubble(message)
                }

                // Loading indicator
                if (isLoading) {
                    item {
                        TypingIndicator()
                    }
                }
            }

            // Input Area
            Surface(
                color = Color(0xFF1A1A2E).copy(alpha = 0.95f),
                shadowElevation = 16.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                "Ask me anything...",
                                color = Color.White.copy(0.5f)
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(0.1f),
                            unfocusedContainerColor = Color.White.copy(0.1f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF00D9FF)
                        ),
                        modifier = Modifier
                            .weight(1f),
                        shape = RoundedCornerShape(25.dp),
                        enabled = !isLoading,
                        maxLines = 3
                    )

                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                sendMessage(inputText)
                            }
                        },
                        enabled = inputText.isNotBlank() && !isLoading,
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (inputText.isNotBlank() && !isLoading)
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF00D9FF), Color(0xFF7F00FF))
                                    )
                                else
                                    Brush.linearGradient(
                                        colors = listOf(Color.Gray, Color.Gray)
                                    ),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Quick Prompts
            if (messages.size <= 1 && !isLoading) {
                QuickPrompts { prompt ->
                    coroutineScope.launch {
                        sendMessage(prompt)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (message.isUser)
                Color(0xFF7F00FF).copy(alpha = 0.9f)
            else
                Color.White.copy(alpha = 0.15f),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (message.isUser) 20.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 20.dp
            ),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = message.text,
                    color = Color.White,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            color = Color.White.copy(alpha = 0.15f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val infiniteTransition = rememberInfiniteTransition(label = "")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = index * 200),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = ""
                    )

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .alpha(alpha)
                            .background(Color(0xFF00D9FF), CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickPrompts(onPromptClick: (String) -> Unit) {
    val prompts = listOf(
        "📚 Tell me about quantum physics",
        "💡 What is artificial intelligence?",
        "✍️ Explain photosynthesis",
        "🧪 What is DNA?",
        "🌍 Tell me about climate change"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            Text(
                text = "💡 Quick Prompts:",
                color = Color.White.copy(0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(prompts) { prompt ->
            Surface(
                onClick = { onPromptClick(prompt) },
                color = Color.White.copy(0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = prompt,
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
fun ChatBackgroundAnimation() {
    val particles = remember {
        List(20) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 3 + 1,
                speedY = Random.nextFloat() * 0.002f + 0.001f,
                alpha = Random.nextFloat() * 0.4f + 0.1f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
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
                color = Color(0xFF00D9FF).copy(alpha = particle.alpha),
                radius = particle.radius,
                center = Offset(particle.x * canvasWidth, newY * canvasHeight)
            )
        }
    }
}

/**
 * Get chat response using Wikipedia API
 * This works without any API key!
 */
suspend fun getRunAnywhereChatResponse(
    userMessage: String,
    chatHistory: List<ChatMessage>
): String {
    return try {
        Log.d("ChatScreen", "Using Wikipedia API for: $userMessage")

        // Use WikipediaService to search and summarize
        val summary = WikipediaService.getSummaryForQuery(userMessage)

        if (summary.isNotBlank()) {
            summary
        } else {
            // Fallback response
            """I'm here to help you learn! 

You asked: "$userMessage"

Unfortunately, I wasn't able to find a Wikipedia article matching your question.

Try asking about a topic, like:
"What is photosynthesis?"
"Tell me about algebra"
"Explain climate change"

I'm ready when you are! 📚"""
        }

    } catch (e: Exception) {
        Log.e("ChatScreen", "Error in getRunAnywhereChatResponse", e)
        """I encountered an error: ${e.message}

But I'm still here to help! Try asking me about a specific topic, like:
"What is photosynthesis?"
"How do I solve quadratic equations?"
Or use Study Mode for detailed reading! 💡"""
    }
}

/**
 * Get chat response from Gemini API (keeping for reference)
 */
suspend fun getGeminiChatResponse(userMessage: String, chatHistory: List<ChatMessage>): String {
    return try {
        // First, try Gemini API
        if (GeminiService.isConfigured()) {
            // Build conversation context
            val context = if (chatHistory.size > 1) {
                buildString {
                    appendLine("Previous conversation:")
                    chatHistory.takeLast(5).forEach { msg ->
                        if (msg.isUser) {
                            appendLine("User: ${msg.text}")
                        } else {
                            appendLine("Assistant: ${msg.text}")
                        }
                    }
                    appendLine("\nCurrent question:")
                }
            } else {
                ""
            }

            val prompt =
                """You are a friendly, helpful AI learning assistant. Your goal is to help students learn and understand topics clearly.

$context
User: $userMessage

Provide a clear, concise, and helpful response. Include:
- A direct answer to their question
- Simple explanations with examples
- Encouragement and support
- Follow-up suggestions if relevant

Keep it conversational and friendly. Use emojis occasionally to make it engaging."""

            val response = GeminiService.generateStudyMaterial(prompt)

            if (response.isNotEmpty() && !response.startsWith("ERROR")) {
                return response
            } else {
                Log.e("ChatScreen", "Gemini API failed: $response")
                // Fall through to RunAnywhere fallback
            }
        }

        // Fallback: Use RunAnywhere AI (local, offline)
        Log.d("ChatScreen", "Using RunAnywhere AI as fallback")
        var aiResponse = ""
        
        try {
            val fallbackPrompt = """You are a helpful learning assistant. Answer this question clearly and concisely:

Question: $userMessage

Provide a helpful, educational response."""

            RunAnywhere.generateStream(fallbackPrompt)
                .catch { e ->
                    Log.e("ChatScreen", "RunAnywhere failed", e)
                }
                .collect { token ->
                    aiResponse += token
                }

            if (aiResponse.length > 50) {
                return aiResponse
            }
        } catch (e: Exception) {
            Log.e("ChatScreen", "RunAnywhere exception", e)
        }

        // Last resort: Helpful error message
        """I'm having trouble connecting to my AI services right now. 

**What to check:**
1. Is your Gemini API key valid? (Check GeminiService.kt line 19)
2. Do you have internet connection?
3. Try again in a moment

**Current status:**
- Gemini API: ${if (GeminiService.isConfigured()) "Configured" else "Not configured"}

**Get a FREE Gemini API key:**
https://makersuite.google.com/app/apikey

Meanwhile, you can use Study Mode which works with Wikipedia! 📚"""

    } catch (e: Exception) {
        Log.e("ChatScreen", "Error in getGeminiChatResponse", e)
        "Oops! Something went wrong: ${e.message}\n\nPlease check your internet connection and Gemini API key."
    }
}
