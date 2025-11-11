package com.runanywhere.startup_hackathon20.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Enhanced Wikipedia API Service
 * Fetches COMPREHENSIVE, DETAILED information from Wikipedia
 */
object WikipediaService {

    private const val TAG = "WikipediaService"
    private const val WIKIPEDIA_API_BASE = "https://en.wikipedia.org/api/rest_v1/page/summary/"
    private const val WIKIPEDIA_SEARCH_API = "https://en.wikipedia.org/w/api.php"
    private const val WIKIPEDIA_EXTRACT_API =
        "https://en.wikipedia.org/w/api.php" // For full content

    /**
     * Get COMPREHENSIVE, DETAILED content from Wikipedia
     * This fetches the full article with multiple sections
     */
    suspend fun getTopicSummary(topic: String): WikipediaResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching Wikipedia content for: $topic")

            // First, search for the best matching article
            val searchResult = searchWikipedia(topic)
            Log.d(TAG, "Search result: $searchResult")

            if (searchResult.isNotEmpty()) {
                // Get FULL DETAILED extract (not just summary)
                val fullContent = getFullArticleExtract(searchResult)
                Log.d(TAG, "Full content length: ${fullContent.length}")

                if (fullContent.isNotEmpty() && fullContent.length > 200) {
                    // We got good detailed content!
                    val formattedContent = formatDetailedWikipediaContent(searchResult, fullContent)

                    WikipediaResult(
                        success = true,
                        title = searchResult,
                        summary = formattedContent,
                        description = ""
                    )
                } else {
                    // Try the summary endpoint as fallback
                    Log.d(TAG, "Full extract too short, trying basic summary")
                    getBasicSummary(searchResult)
                }
            } else {
                Log.e(TAG, "No search results found for: $topic")
                WikipediaResult(success = false, error = "Topic not found on Wikipedia")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching Wikipedia content: ${e.message}", e)
            WikipediaResult(success = false, error = e.message ?: "Failed to fetch data")
        }
    }

    /**
     * Get FULL article extract with comprehensive content
     * This gets multiple paragraphs, not just a summary
     */
    private fun getFullArticleExtract(title: String): String {
        return try {
            val encodedTitle = URLEncoder.encode(title, "UTF-8")
            // Use 'extract' API to get full content - exintro=0 means get FULL article, not just intro
            val extractUrl =
                "$WIKIPEDIA_EXTRACT_API?action=query&format=json&prop=extracts&exintro=0&explaintext=1&titles=$encodedTitle&exsectionformat=plain"

            Log.d(TAG, "Fetching full article from: $extractUrl")

            val url = URL(extractUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "VibeMentor/1.0")
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            val responseCode = connection.responseCode
            Log.d(TAG, "Response code: $responseCode")

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(response)

            val query = json.optJSONObject("query")
            val pages = query?.optJSONObject("pages")

            if (pages != null) {
                val pageKeys = pages.keys()
                if (pageKeys.hasNext()) {
                    val pageKey = pageKeys.next()

                    // Check if page exists (pageKey != "-1")
                    if (pageKey == "-1") {
                        Log.e(TAG, "Page not found")
                        return ""
                    }

                    val page = pages.getJSONObject(pageKey)
                    val extract = page.optString("extract", "")

                    Log.d(TAG, "Extract length: ${extract.length}")
                    // Return the full extract (this can be multiple paragraphs)
                    return extract
                }
            }

            ""
        } catch (e: Exception) {
            Log.e(TAG, "Error getting full article extract: ${e.message}", e)
            ""
        }
    }

    /**
     * Fallback: Get basic summary if full extract fails
     */
    private fun getBasicSummary(title: String): WikipediaResult {
        return try {
            Log.d(TAG, "Fetching basic summary for: $title")
            val encodedTitle = URLEncoder.encode(title, "UTF-8")
            val url = URL("$WIKIPEDIA_API_BASE$encodedTitle")
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "VibeMentor/1.0")
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseCode = connection.responseCode
            Log.d(TAG, "Basic summary response code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)

                val pageTitle = json.optString("title", title)
                val extract = json.optString("extract", "")
                val description = json.optString("description", "")

                Log.d(TAG, "Basic summary extract length: ${extract.length}")

                val content = if (extract.isNotEmpty()) {
                    formatDetailedWikipediaContent(pageTitle, extract)
                } else {
                    ""
                }

                WikipediaResult(
                    success = content.isNotEmpty(),
                    title = pageTitle,
                    summary = content,
                    description = description
                )
            } else {
                Log.e(TAG, "Failed to fetch basic summary, code: $responseCode")
                WikipediaResult(success = false, error = "No information found")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting basic summary: ${e.message}", e)
            WikipediaResult(success = false, error = e.message ?: "Failed to fetch")
        }
    }

    /**
     * Search Wikipedia for the best matching article title
     */
    private fun searchWikipedia(query: String): String {
        return try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val searchUrl =
                "$WIKIPEDIA_SEARCH_API?action=query&list=search&srsearch=$encodedQuery&format=json&utf8=1"

            Log.d(TAG, "Searching Wikipedia: $searchUrl")

            val url = URL(searchUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "VibeMentor/1.0")
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(response)

            val queryObj = json.optJSONObject("query")
            val search = queryObj?.optJSONArray("search")

            if (search != null && search.length() > 0) {
                val firstResult = search.getJSONObject(0)
                val resultTitle = firstResult.optString("title", "")
                Log.d(TAG, "Found Wikipedia article: $resultTitle")
                return resultTitle
            } else {
                Log.e(TAG, "No search results found")
                return ""
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error searching Wikipedia: ${e.message}", e)
            ""
        }
    }

    /**
     * Format comprehensive Wikipedia content into well-structured study material
     */
    private fun formatDetailedWikipediaContent(title: String, extract: String): String {
        return buildString {
            appendLine("📚 **$title**")
            appendLine()
            appendLine("*From Wikipedia - The Free Encyclopedia*")
            appendLine()
            appendLine("─".repeat(50))
            appendLine()

            // Split into paragraphs for better readability
            val paragraphs = extract.split("\n\n").filter { it.trim().isNotEmpty() }

            if (paragraphs.isNotEmpty()) {
                // Add all paragraphs with proper formatting
                paragraphs.forEach { paragraph ->
                    val trimmed = paragraph.trim()
                    if (trimmed.isNotEmpty()) {
                        // Check if it's a section header (usually shorter and followed by content)
                        if (trimmed.length < 100 && !trimmed.contains(".")) {
                            appendLine("### $trimmed")
                            appendLine()
                        } else {
                            appendLine(trimmed)
                            appendLine()
                        }
                    }
                }
            } else {
                // Fallback: just add the extract as is
                appendLine(extract)
            }

            appendLine()
            appendLine("─".repeat(50))
            appendLine()
            appendLine("📖 **Source:** Wikipedia")
            appendLine("🔗 **Learn more:** https://en.wikipedia.org/wiki/${title.replace(" ", "_")}")
        }
    }

    /**
     * Get multiple related topics
     */
    suspend fun getRelatedTopics(topic: String): List<String> = withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(topic, "UTF-8")
            val searchUrl =
                "$WIKIPEDIA_SEARCH_API?action=query&list=search&srsearch=$encodedQuery&format=json&utf8=1&srlimit=6"

            val url = URL(searchUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "VibeMentor/1.0")
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(response)

            val query = json.optJSONObject("query")
            val search = query?.optJSONArray("search")

            val topics = mutableListOf<String>()
            if (search != null) {
                // Skip first result (it's the main topic) and get the next 5
                for (i in 1 until minOf(search.length(), 6)) {
                    val result = search.getJSONObject(i)
                    topics.add(result.optString("title", ""))
                }
            }
            Log.d(TAG, "Found ${topics.size} related topics")
            topics
        } catch (e: Exception) {
            Log.e(TAG, "Error getting related topics: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Get a conversational summary for chat purposes
     * Searches Wikipedia and returns a friendly response
     */
    suspend fun getSummaryForQuery(query: String): String = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting summary for chat query: $query")

            // Search for the topic
            val searchResult = searchWikipedia(query)

            if (searchResult.isEmpty()) {
                return@withContext ""
            }

            // Get the basic summary (not full article for chat)
            val encodedTitle = URLEncoder.encode(searchResult, "UTF-8")
            val url = URL("$WIKIPEDIA_API_BASE$encodedTitle")
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "VibeMentor/1.0")
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)

                val pageTitle = json.optString("title", searchResult)
                val extract = json.optString("extract", "")

                if (extract.isNotEmpty()) {
                    // Format as a friendly chat response
                    return@withContext buildString {
                        appendLine("📚 **$pageTitle**")
                        appendLine()
                        appendLine(extract)
                        appendLine()
                        appendLine(
                            "🔗 Learn more: https://en.wikipedia.org/wiki/${
                                pageTitle.replace(
                                    " ",
                                    "_"
                                )
                            }"
                        )
                    }
                }
            }

            ""
        } catch (e: Exception) {
            Log.e(TAG, "Error in getSummaryForQuery: ${e.message}", e)
            ""
        }
    }
}

/**
 * Result data class for Wikipedia API responses
 */
data class WikipediaResult(
    val success: Boolean,
    val title: String = "",
    val summary: String = "",
    val description: String = "",
    val error: String = ""
)
