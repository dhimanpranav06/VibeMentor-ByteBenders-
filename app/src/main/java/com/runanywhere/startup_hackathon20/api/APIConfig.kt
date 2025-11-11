package com.runanywhere.startup_hackathon20.api

/**
 * API Configuration - Choose your AI provider
 *
 * Available providers:
 * - GEMINI: Google's FREE Gemini AI (Recommended!)
 * - OPENAI: ChatGPT API (Requires payment)
 * - WIKIPEDIA: Free Wikipedia API (No AI generation)
 */
object APIConfig {

    enum class AIProvider {
        GEMINI,    // Google Gemini - FREE!
        OPENAI,    // OpenAI ChatGPT - Paid
        WIKIPEDIA  // Wikipedia only - FREE!
    }

    // CHANGE THIS to switch providers
    // Recommended: GEMINI (it's free!)
    val CURRENT_PROVIDER = AIProvider.GEMINI

    /**
     * Get the appropriate content generator based on current provider
     */
    suspend fun getStudyContent(topic: String): String {
        return when (CURRENT_PROVIDER) {
            AIProvider.GEMINI -> {
                if (GeminiService.isConfigured()) {
                    GeminiService.generateStudyMaterial(topic)
                } else {
                    // Fall back to Wikipedia if Gemini not configured
                    val result = WikipediaService.getTopicSummary(topic)
                    if (result.success) result.summary else ""
                }
            }

            AIProvider.OPENAI -> {
                if (OpenAIService.isConfigured()) {
                    OpenAIService.generateStudyMaterial(topic)
                } else {
                    // Fall back to Wikipedia if OpenAI not configured
                    val result = WikipediaService.getTopicSummary(topic)
                    if (result.success) result.summary else ""
                }
            }

            AIProvider.WIKIPEDIA -> {
                val result = WikipediaService.getTopicSummary(topic)
                if (result.success) result.summary else ""
            }
        }
    }

    /**
     * Get quiz questions based on current provider
     */
    suspend fun getQuizQuestions(topic: String): String {
        return when (CURRENT_PROVIDER) {
            AIProvider.GEMINI -> {
                if (GeminiService.isConfigured()) {
                    GeminiService.generateQuizQuestions(topic)
                } else {
                    ""
                }
            }

            AIProvider.OPENAI -> {
                if (OpenAIService.isConfigured()) {
                    OpenAIService.generateQuizQuestions(topic)
                } else {
                    ""
                }
            }

            AIProvider.WIKIPEDIA -> {
                // Wikipedia doesn't generate quizzes, return empty
                ""
            }
        }
    }

    /**
     * Check if current provider is configured
     */
    fun isConfigured(): Boolean {
        return when (CURRENT_PROVIDER) {
            AIProvider.GEMINI -> GeminiService.isConfigured()
            AIProvider.OPENAI -> OpenAIService.isConfigured()
            AIProvider.WIKIPEDIA -> true // Wikipedia always works
        }
    }

    /**
     * Get the name of current provider
     */
    fun getProviderName(): String {
        return when (CURRENT_PROVIDER) {
            AIProvider.GEMINI -> "Google Gemini"
            AIProvider.OPENAI -> "OpenAI ChatGPT"
            AIProvider.WIKIPEDIA -> "Wikipedia"
        }
    }
}
