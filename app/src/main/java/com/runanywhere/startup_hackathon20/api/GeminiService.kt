//package com.runanywhere.startup_hackathon20.api
//
//import android.util.Log
//import com.runanywhere.startup_hackathon20.BuildConfig
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import org.json.JSONArray
//import org.json.JSONObject
//import java.net.HttpURLConnection
//import java.net.URL
//
///**
// * Google Gemini API Service - FREE AI for comprehensive content generation
// * Get your FREE API key from: https://makersuite.google.com/app/apikey
// */
//object GeminiService {
//
//    // Securely load API key from BuildConfig (injected via local.properties)
//    private const val TAG = "GeminiService"
//    private const val API_BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
//    private const val MODEL = "gemini-pro"
//
//    // Access the key from Gradle BuildConfig
//    private val API_KEY: String = BuildConfig.GEMINI_API_KEY
//
//    /**
//     * Check if API key is configured
//     */
//    fun isConfigured(): Boolean {
//        return API_KEY.isNotBlank() && API_KEY.length > 20
//    }
//
//    /**
//     * Generate comprehensive study material using Gemini AI
//     */
//    suspend fun generateStudyMaterial(topic: String): String = withContext(Dispatchers.IO) {
//        try {
//            if (!isConfigured()) {
//                Log.e(TAG, "Gemini API Key not configured!")
//                return@withContext ""
//            }
//
//            Log.d(TAG, "Generating study material for: $topic")
//
//            val prompt =
//                """You are an expert educator. Provide COMPREHENSIVE, DETAILED study material about "$topic".
//
//Include:
//📚 **Overview** - What is $topic and why is it important?
//🎯 **Key Concepts** - Main ideas and principles
//💡 **Applications** - Real-world uses and examples
//🔑 **Important Facts** - Key points to remember
//
//Write at least 3-4 detailed paragraphs. Be specific and educational!"""
//
//            val response = makeGeminiRequest(prompt)
//            if (response.startsWith("ERROR:") || response.isBlank()) {
//                Log.e(TAG, "API Error: $response")
//                return@withContext ""
//            }
//
//            Log.d(TAG, "Successfully generated content (${response.length} chars)")
//            response
//        } catch (e: Exception) {
//            Log.e(TAG, "Exception in generateStudyMaterial", e)
//            ""
//        }
//    }
//
//    /**
//     * Get topic summary from Gemini
//     */
//    suspend fun getTopicSummary(topic: String): TopicResult = withContext(Dispatchers.IO) {
//        try {
//            if (!isConfigured()) {
//                Log.e(TAG, "Gemini API Key not configured!")
//                return@withContext TopicResult(false, "", "API key not configured")
//            }
//
//            Log.d(TAG, "Getting topic summary for: $topic")
//
//            val prompt = """Provide a comprehensive, detailed explanation about "$topic".
//
//Include:
//📚 **Overview**: What is $topic? Provide a clear, detailed introduction.
//🎯 **Key Concepts**: Explain the main ideas, principles, and components.
//💡 **Applications**: Describe real-world uses, examples, and practical applications.
//🔑 **Important Facts**: List key points, facts, and information to remember.
//📈 **Significance**: Why is this topic important? What is its relevance?
//
//Write at least 4-5 detailed paragraphs with specific information. Be thorough and educational!"""
//
//            val response = makeGeminiRequest(prompt)
//            if (response.startsWith("ERROR:") || response.isBlank()) {
//                Log.e(TAG, "API Error: $response")
//                return@withContext TopicResult(false, "", response)
//            }
//
//            Log.d(TAG, "Successfully got topic summary (${response.length} chars)")
//            TopicResult(true, response, "")
//        } catch (e: Exception) {
//            Log.e(TAG, "Exception in getTopicSummary", e)
//            TopicResult(false, "", e.message ?: "Unknown error")
//        }
//    }
//
//    /**
//     * Get related topics from Gemini
//     */
//    suspend fun getRelatedTopics(topic: String): List<String> = withContext(Dispatchers.IO) {
//        try {
//            if (!isConfigured()) {
//                return@withContext emptyList()
//            }
//
//            Log.d(TAG, "Getting related topics for: $topic")
//
//            val prompt =
//                """List 5 related topics to "$topic" that someone studying this subject should also learn about.
//
//Return ONLY the topic names, one per line, without numbers or bullet points.
//Example format:
//Related Topic 1
//Related Topic 2
//Related Topic 3"""
//
//            val response = makeGeminiRequest(prompt)
//            if (response.startsWith("ERROR:") || response.isBlank()) {
//                return@withContext emptyList()
//            }
//
//            val topics = response.lines()
//                .filter { it.isNotBlank() }
//                .map { it.trim() }
//                .filter { it.isNotEmpty() }
//                .take(5)
//
//            Log.d(TAG, "Found ${topics.size} related topics")
//            topics
//        } catch (e: Exception) {
//            Log.e(TAG, "Exception in getRelatedTopics", e)
//            emptyList()
//        }
//    }
//
//    /**
//     * Generate quiz questions using Gemini AI
//     */
//    suspend fun generateQuizQuestions(topic: String): String = withContext(Dispatchers.IO) {
//        try {
//            if (!isConfigured()) {
//                Log.e(TAG, "Gemini API Key not configured!")
//                return@withContext ""
//            }
//
//            Log.d(TAG, "Generating quiz questions for: $topic")
//
//            val prompt = """Create 5 multiple-choice quiz questions about "$topic".
//
//Format each question EXACTLY like this:
//
//Q1: [Question text]
//A) [Option A]
//B) [Option B]
//C) [Option C]
//D) [Option D]
//Correct: [A/B/C/D]
//
//Q2: [Question text]
//A) [Option A]
//B) [Option B]
//C) [Option C]
//D) [Option D]
//Correct: [A/B/C/D]
//
//Continue for all 5 questions. Make questions educational and test understanding of key concepts."""
//
//            val response = makeGeminiRequest(prompt)
//            if (response.startsWith("ERROR:") || response.isBlank()) {
//                Log.e(TAG, "API Error: $response")
//                return@withContext ""
//            }
//
//            Log.d(TAG, "Successfully generated quiz (${response.length} chars)")
//            response
//        } catch (e: Exception) {
//            Log.e(TAG, "Exception in generateQuizQuestions", e)
//            ""
//        }
//    }
//
//    private fun makeGeminiRequest(prompt: String): String {
//        return try {
//            val endpoint = "$API_BASE_URL/models/$MODEL:generateContent?key=$API_KEY"
//            val url = URL(endpoint)
//            val connection = url.openConnection() as HttpURLConnection
//
//            connection.apply {
//                requestMethod = "POST"
//                setRequestProperty("Content-Type", "application/json; charset=utf-8")
//                doOutput = true
//                connectTimeout = 30000
//                readTimeout = 30000
//            }
//
//            val requestBody = JSONObject().apply {
//                put("contents", JSONArray().apply {
//                    put(JSONObject().apply {
//                        put("parts", JSONArray().apply {
//                            put(JSONObject().apply {
//                                put("text", prompt)
//                            })
//                        })
//                    })
//                })
//            }
//
//            connection.outputStream.use {
//                it.write(requestBody.toString().toByteArray(Charsets.UTF_8))
//            }
//
//            val responseCode = connection.responseCode
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
//                val jsonResponse = JSONObject(responseText)
//
//                val candidates = jsonResponse.optJSONArray("candidates")
//                if (candidates != null && candidates.length() > 0) {
//                    val firstCandidate = candidates.getJSONObject(0)
//                    val content = firstCandidate.optJSONObject("content")
//                    val parts = content?.optJSONArray("parts")
//
//                    if (parts != null && parts.length() > 0) {
//                        return parts.getJSONObject(0).optString("text", "")
//                    }
//                }
//                "ERROR: Empty response from API"
//            } else {
//                val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() }
//                Log.e(TAG, "HTTP Error $responseCode: $errorBody")
//                "ERROR: API error (code $responseCode)"
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "Request failed", e)
//            "ERROR: ${e.message}"
//        }
//    }
//
//    // Data class for topic results
//    data class TopicResult(val success: Boolean, val summary: String, val error: String)
//}