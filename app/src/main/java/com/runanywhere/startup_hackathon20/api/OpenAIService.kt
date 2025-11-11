package com.runanywhere.startup_hackathon20.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * OpenAI ChatGPT API Service
 * Get your API key from: https://platform.openai.com/api-keys
 * Note: Requires payment ($5 minimum)
 */
object OpenAIService {

    // ADD YOUR OPENAI API KEY HERE
    // Get it from: https://platform.openai.com/api-keys
    private const val API_KEY = "YOUR_OPENAI_API_KEY_HERE"

    private const val TAG = "OpenAIService"
    private const val API_BASE_URL = "https://api.openai.com/v1/chat/completions"
    private const val MODEL = "gpt-3.5-turbo"

    /**
     * Check if API key is configured
     */
    fun isConfigured(): Boolean {
        return API_KEY != "YOUR_OPENAI_API_KEY_HERE" && API_KEY.startsWith("sk-")
    }

    /**
     * Generate comprehensive study material using ChatGPT
     */
    suspend fun generateStudyMaterial(topic: String): String = withContext(Dispatchers.IO) {
        try {
            if (!isConfigured()) {
                return@withContext ""
            }

            Log.d(TAG, "Generating study material for: $topic")

            val prompt =
                """You are an expert educator. Provide COMPREHENSIVE, DETAILED study material about "$topic".

Write at least 8-10 detailed paragraphs covering:

📚 **Introduction & Overview**
Provide a thorough introduction explaining what "$topic" is, its historical context, why it's important, and where it's used. Write 3-4 detailed paragraphs.

🎯 **Core Concepts & Fundamentals**
Explain 6-8 key concepts in depth. For each concept:
- Define it clearly
- Provide concrete examples
- Use analogies to make it relatable
- Break down any technical terms

💡 **Real-World Applications**
Describe 5-6 specific practical applications:
- Industry use cases with detailed examples
- Current trends and innovations
- How professionals use this knowledge

🔬 **Technical Deep Dive**
Provide step-by-step explanations of how things work:
- Underlying mechanisms and principles
- Scientific or technical details
- Advanced concepts for deeper understanding

🔑 **Important Facts & Key Points**
List 7-8 critical facts including:
- Must-know information
- Common misconceptions (and corrections)
- Interesting facts or historical notes

📖 **Learning Path & Study Guide**
- What prerequisites are needed
- Recommended step-by-step learning approach
- Practice exercises or projects to try

🚀 **Significance & Future Impact**
- Why this matters in the real world
- Career opportunities in this field
- Future trends and developments

Make it extremely detailed, educational, and engaging. Use examples throughout."""

            val response = makeOpenAIRequest(prompt)

            if (response.startsWith("ERROR:") || response.isBlank()) {
                Log.e(TAG, "API Error: $response")
                return@withContext ""
            }

            Log.d(TAG, "Successfully generated content (${response.length} chars)")
            response

        } catch (e: Exception) {
            Log.e(TAG, "Exception in generateStudyMaterial", e)
            ""
        }
    }

    /**
     * Generate unique quiz questions using ChatGPT
     */
    suspend fun generateQuizQuestions(topic: String): String = withContext(Dispatchers.IO) {
        try {
            if (!isConfigured()) {
                return@withContext ""
            }

            Log.d(TAG, "Generating quiz questions for: $topic")

            val prompt =
                """Generate 5 UNIQUE, challenging multiple-choice quiz questions about "$topic".

IMPORTANT REQUIREMENTS:
- Generate DIFFERENT questions each time (vary the aspects covered)
- Make questions progressively challenging (easy → hard)
- Cover different facets of the topic
- Ensure options are meaningful (not obviously wrong)
- Have ONE clearly correct answer per question

FORMAT EXACTLY LIKE THIS:

Q1: [Specific question about fundamental concept of $topic]
A) [Plausible option]
B) [Plausible option]
C) [Correct answer]
D) [Plausible option]
ANSWER: C

Q2: [Question about application or use case of $topic]
A) [Option]
B) [Correct answer]
C) [Option]
D) [Option]
ANSWER: B

Q3: [Question about technical detail or how something works]
A) [Option]
B) [Option]
C) [Option]
D) [Correct answer]
ANSWER: D

Q4: [Advanced or conceptual question about $topic]
A) [Correct answer]
B) [Option]
C) [Option]
D) [Option]
ANSWER: A

Q5: [Application-based or real-world scenario question]
A) [Option]
B) [Option]
C) [Correct answer]
D) [Option]
ANSWER: C

Make sure:
- Each question tests different knowledge
- Options are educational even if wrong
- Questions require understanding, not just memorization
- Vary which option (A/B/C/D) is correct"""

            val response = makeOpenAIRequest(prompt)

            if (response.startsWith("ERROR:") || response.isBlank()) {
                Log.e(TAG, "Quiz generation error: $response")
                return@withContext ""
            }

            Log.d(TAG, "Successfully generated quiz (${response.length} chars)")
            response

        } catch (e: Exception) {
            Log.e(TAG, "Exception in generateQuizQuestions", e)
            ""
        }
    }

    /**
     * Make HTTP request to OpenAI API
     */
    private fun makeOpenAIRequest(prompt: String): String {
        return try {
            val url = URL(API_BASE_URL)
            val connection = url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Authorization", "Bearer $API_KEY")
                doOutput = true
                connectTimeout = 30000
                readTimeout = 30000
            }

            // Build JSON request body
            val requestBody = JSONObject().apply {
                put("model", MODEL)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
                put("temperature", 0.7)
                put("max_tokens", 4000)
            }

            // Send request
            connection.outputStream.use {
                it.write(requestBody.toString().toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode
            Log.d(TAG, "Response code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                Log.d(TAG, "Response received: ${responseText.take(200)}...")

                val jsonResponse = JSONObject(responseText)

                // Parse OpenAI response
                val choices = jsonResponse.optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    val firstChoice = choices.getJSONObject(0)
                    val message = firstChoice.optJSONObject("message")
                    val content = message?.optString("content", "")

                    if (!content.isNullOrBlank()) {
                        return content
                    }
                }

                // Check for error
                val error = jsonResponse.optJSONObject("error")
                if (error != null) {
                    val errorMessage = error.optString("message", "Unknown error")
                    Log.e(TAG, "API returned error: $errorMessage")
                    return "ERROR: $errorMessage"
                }

                Log.e(TAG, "No content in response")
                return "ERROR: Empty response from API"
            } else {
                val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() }
                Log.e(TAG, "HTTP Error $responseCode: $errorBody")

                return when (responseCode) {
                    401 -> "ERROR: Invalid API key"
                    429 -> "ERROR: Rate limit exceeded or insufficient credits"
                    500, 502, 503 -> "ERROR: OpenAI server error"
                    else -> "ERROR: API error (code $responseCode)"
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Request failed", e)
            "ERROR: ${e.message}"
        }
    }
}
