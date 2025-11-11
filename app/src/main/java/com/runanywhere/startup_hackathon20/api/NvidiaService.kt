package com.runanywhere.startup_hackathon20.api

import android.util.Log
import com.runanywhere.startup_hackathon20.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * NVIDIA NIM API Service - AI content generation using NVIDIA's API
 * API Key is loaded from local.properties
 */
object NvidiaService {

    private const val TAG = "NvidiaService"
    private const val API_BASE_URL = "https://integrate.api.nvidia.com/v1/chat/completions"
    private const val MODEL = "meta/llama-3.1-405b-instruct" // NVIDIA's powerful model

    // API key loaded from BuildConfig (which gets it from local.properties)
    private val API_KEY: String
        get() = BuildConfig.NVIDIA_API_KEY

    /**
     * Check if NVIDIA API is configured
     */
    fun isConfigured(): Boolean {
        val configured = API_KEY.isNotEmpty() && API_KEY.startsWith("nvapi-")
        Log.d(TAG, "NVIDIA API configured: $configured")
        return configured
    }

    /**
     * Generate chat response (for Chat Screen)
     */
    suspend fun generateChatResponse(
        userMessage: String,
        chatHistory: List<Any> = emptyList()
    ): String {
        return withContext(Dispatchers.IO) {
            try {
                if (!isConfigured()) {
                    return@withContext "❌ NVIDIA API key not configured. Please add it to local.properties"
                }

                // Build conversation context
                val messages = JSONArray()

                // System message
                messages.put(JSONObject().apply {
                    put("role", "system")
                    put(
                        "content",
                        "You are a friendly, helpful AI learning assistant. Provide clear, concise, and educational responses. Use emojis occasionally to make it engaging."
                    )
                })

                // Add chat history (last 5 messages for context)
                chatHistory.takeLast(5).forEach { msg ->
                    if (msg is com.runanywhere.startup_hackathon20.ChatMessage) {
                        messages.put(JSONObject().apply {
                            put("role", if (msg.isUser) "user" else "assistant")
                            put("content", msg.text)
                        })
                    }
                }

                // Add current message
                messages.put(JSONObject().apply {
                    put("role", "user")
                    put("content", userMessage)
                })

                val response = makeNvidiaRequest(messages)
                Log.d(TAG, "Chat response generated successfully")
                response

            } catch (e: Exception) {
                Log.e(TAG, "Error generating chat response", e)
                "Error: ${e.message}"
            }
        }
    }

    /**
     * Generate interview questions (for Mock Interview)
     */
    suspend fun generateInterviewQuestions(role: String, experience: String): String {
        return withContext(Dispatchers.IO) {
            try {
                if (!isConfigured()) {
                    return@withContext "NVIDIA API not configured"
                }

                val prompt =
                    """You are an expert technical interviewer. Generate 5 interview questions for:
Role: $role
Experience Level: $experience

Generate progressively challenging questions covering:
1. Basic role understanding and introduction
2. Technical/functional knowledge
3. Problem-solving scenario
4. Behavioral/situational question
5. Advanced concept or leadership

Format each question clearly numbered:
1. [Question text]
2. [Question text]
3. [Question text]
4. [Question text]
5. [Question text]

Make questions realistic, role-specific, and appropriate for the experience level."""

                val messages = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put(
                            "content",
                            "You are an expert interview coach who creates realistic interview questions."
                        )
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                }

                val response = makeNvidiaRequest(messages)
                Log.d(TAG, "Interview questions generated successfully")
                response

            } catch (e: Exception) {
                Log.e(TAG, "Error generating interview questions", e)
                "Error: ${e.message}"
            }
        }
    }

    /**
     * Evaluate interview answer (for Mock Interview)
     */
    suspend fun evaluateInterviewAnswer(question: String, answer: String): Pair<Int, String> {
        return withContext(Dispatchers.IO) {
            try {
                if (!isConfigured()) {
                    return@withContext Pair(6, "NVIDIA API not configured")
                }

                val prompt = """You are an interview evaluator. Rate this interview answer:

Question: $question
Candidate's Answer: $answer

Provide:
1. Score (1-10) - Be fair and constructive
2. Brief feedback (2-3 sentences) on strengths and areas for improvement

Format:
SCORE: [number]
FEEDBACK: [Your detailed feedback]"""

                val messages = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put(
                            "content",
                            "You are an experienced interview evaluator who provides constructive feedback."
                        )
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                }

                val response = makeNvidiaRequest(messages)

                // Parse score and feedback
                val scoreMatch = Regex("SCORE:\\s*(\\d+)").find(response)
                val feedbackMatch =
                    Regex("FEEDBACK:\\s*(.+)", RegexOption.DOT_MATCHES_ALL).find(response)

                val score = scoreMatch?.groupValues?.get(1)?.toIntOrNull() ?: 6
                val feedback = feedbackMatch?.groupValues?.get(1)?.trim()
                    ?: response.takeIf { it.length > 20 }
                    ?: "Good effort! Consider providing more specific examples and structuring your answer better."

                Log.d(TAG, "Interview answer evaluated: Score $score")
                Pair(score, feedback)

            } catch (e: Exception) {
                Log.e(TAG, "Error evaluating answer", e)
                Pair(6, "Good response! Focus on being more specific with examples.")
            }
        }
    }

    /**
     * Generate quiz questions (for Study Mode - Quiz)
     */
    suspend fun generateQuizQuestions(topic: String): String {
        return withContext(Dispatchers.IO) {
            try {
                if (!isConfigured()) {
                    return@withContext "NVIDIA API not configured"
                }

                val prompt = """Create 5 multiple-choice quiz questions about: $topic

For each question, provide:
- The question
- 4 answer options (A, B, C, D)
- Mark the correct answer

Format:
Q1: [Question text]
A) [Option A]
B) [Option B]
C) [Option C]
D) [Option D]
Correct: [A/B/C/D]

Make questions educational and progressively challenging."""

                val messages = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put(
                            "content",
                            "You are an educational content creator who makes engaging quiz questions."
                        )
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                }

                val response = makeNvidiaRequest(messages)
                Log.d(TAG, "Quiz questions generated successfully")
                response

            } catch (e: Exception) {
                Log.e(TAG, "Error generating quiz", e)
                "Error: ${e.message}"
            }
        }
    }

    /**
     * Generate career recommendations (for Skill Tracker)
     */
    suspend fun generateCareerRecommendations(skills: List<String>): String {
        return withContext(Dispatchers.IO) {
            try {
                if (!isConfigured()) {
                    return@withContext "NVIDIA API not configured"
                }

                val prompt = """Based on these skills: ${skills.joinToString(", ")}

Provide 3 career recommendations:

For each career:
1. Job title
2. Why it's a good fit
3. Average salary range
4. Key skills to develop further

Be specific and practical."""

                val messages = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put(
                            "content",
                            "You are a career counselor who provides practical job recommendations."
                        )
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                }

                val response = makeNvidiaRequest(messages)
                Log.d(TAG, "Career recommendations generated")
                response

            } catch (e: Exception) {
                Log.e(TAG, "Error generating career recommendations", e)
                "Error: ${e.message}"
            }
        }
    }

    /**
     * Generate skill suggestions (for Skill Tracker)
     */
    suspend fun generateSkillSuggestions(interests: String): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                if (!isConfigured()) {
                    return@withContext emptyList()
                }

                val prompt = """Based on the interest in: $interests

Suggest 5 relevant skills to learn. 
Just list the skill names, one per line, without numbers or explanations."""

                val messages = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", "You are a career advisor who suggests relevant skills.")
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                }

                val response = makeNvidiaRequest(messages)

                // Parse response into list of skills
                val skills = response.lines()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() && !it.startsWith("#") }
                    .map { it.replaceFirst(Regex("^\\d+[.)\\s]+"), "") }
                    .filter { it.length in 3..50 }
                    .take(5)

                Log.d(TAG, "Skill suggestions generated: ${skills.size}")
                skills

            } catch (e: Exception) {
                Log.e(TAG, "Error generating skill suggestions", e)
                emptyList()
            }
        }
    }

    /**
     * Get skill development tips (for Skill Tracker)
     */
    suspend fun getSkillDevelopmentTips(skillName: String, currentLevel: Float): String {
        return withContext(Dispatchers.IO) {
            try {
                if (!isConfigured()) {
                    return@withContext "NVIDIA API not configured"
                }

                val levelDescription = when {
                    currentLevel < 0.3f -> "beginner"
                    currentLevel < 0.7f -> "intermediate"
                    else -> "advanced"
                }

                val prompt =
                    """Provide 3 development tips for improving "$skillName" skill at $levelDescription level:

1. A practical exercise or project
2. A learning resource recommendation
3. A real-world application tip

Keep each tip concise (1-2 sentences)."""

                val messages = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put(
                            "content",
                            "You are a skill development coach who provides actionable tips."
                        )
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                }

                val response = makeNvidiaRequest(messages)
                Log.d(TAG, "Skill tips generated")
                response

            } catch (e: Exception) {
                Log.e(TAG, "Error generating skill tips", e)
                "Error: ${e.message}"
            }
        }
    }

    /**
     * Analyze learning path (for Skill Tracker)
     */
    suspend fun analyzeLearningPath(currentSkills: List<String>, interests: String): String {
        return withContext(Dispatchers.IO) {
            try {
                if (!isConfigured()) {
                    return@withContext "NVIDIA API not configured"
                }

                val prompt = """Current skills: ${currentSkills.joinToString(", ")}
Career interests: $interests

Provide a learning path analysis:
1. Assessment of current skill profile (2 sentences)
2. Three specific next steps to take
3. Two priority skills to focus on

Be specific and actionable."""

                val messages = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put(
                            "content",
                            "You are a learning path advisor who creates personalized development plans."
                        )
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                }

                val response = makeNvidiaRequest(messages)
                Log.d(TAG, "Learning path analyzed")
                response

            } catch (e: Exception) {
                Log.e(TAG, "Error analyzing learning path", e)
                "Error: ${e.message}"
            }
        }
    }

    /**
     * Make HTTP request to NVIDIA API
     */
    private suspend fun makeNvidiaRequest(messages: JSONArray): String {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(API_BASE_URL)
                val connection = url.openConnection() as HttpURLConnection

                connection.apply {
                    requestMethod = "POST"
                    setRequestProperty("Authorization", "Bearer $API_KEY")
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Accept", "application/json")
                    doOutput = true
                    connectTimeout = 30000
                    readTimeout = 30000
                }

                // Build request body
                val requestBody = JSONObject().apply {
                    put("model", MODEL)
                    put("messages", messages)
                    put("temperature", 0.7)
                    put("max_tokens", 1024)
                    put("top_p", 1.0)
                    put("stream", false)
                }

                Log.d(TAG, "Sending request to NVIDIA API...")

                // Send request
                connection.outputStream.use { os ->
                    os.write(requestBody.toString().toByteArray())
                }

                // Read response
                val responseCode = connection.responseCode
                Log.d(TAG, "Response code: $responseCode")

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)

                    val content = jsonResponse
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")

                    Log.d(TAG, "Response received successfully")
                    content.trim()

                } else {
                    val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() }
                    Log.e(TAG, "API Error $responseCode: $errorBody")
                    "ERROR: API returned status $responseCode"
                }

            } catch (e: Exception) {
                Log.e(TAG, "Request failed", e)
                "ERROR: ${e.message}"
            }
        }
    }
}
