# Wikipedia API Integration - Study Mode & Quiz

## Overview

VibeMentor now uses Wikipedia API as the primary source for all educational content in Study Mode
and Quiz generation. This ensures students receive accurate, verified, and comprehensive information
from a trusted source.

## Features Implemented

### 1. Study Mode - Wikipedia-Powered Explanations

**File:** `app/src/main/java/com/runanywhere/startup_hackathon20/StudyModeScreen.kt`

#### Key Changes:

- ✅ **Primary Source:** Wikipedia API (`WikipediaService`) fetches comprehensive articles
- ✅ **Full Article Content:** Uses `getFullArticleExtract()` to retrieve detailed, multi-paragraph
  content
- ✅ **AI Enhancement:** Optional AI layer adds study tips and key takeaways based on Wikipedia
  content
- ✅ **Related Topics:** Automatically suggests related Wikipedia articles for further learning
- ✅ **Fallback System:** If Wikipedia is unavailable, falls back to local AI generation

#### How It Works:

1. User enters a topic (e.g., "Quantum Physics")
2. System searches Wikipedia for the best matching article
3. Fetches full article extract with multiple sections and paragraphs
4. Formats content with proper structure (headers, paragraphs, source links)
5. Optionally enhances with AI-generated study guides
6. Displays related topics from Wikipedia for exploration

### 2. Quiz Mode - Wikipedia-Based Questions

**File:** `app/src/main/java/com/runanywhere/startup_hackathon20/QuizScreen.kt`

#### Key Changes:

- ✅ **Wikipedia Content First:** Fetches topic content from Wikipedia before generating questions
- ✅ **Factual Questions:** Quiz questions are based on actual Wikipedia article content
- ✅ **Three-Tier Generation:**
    1. **AI-Generated:** AI creates questions based on Wikipedia content (most comprehensive)
    2. **Direct Extraction:** Questions generated directly from Wikipedia sentences (fallback)
    3. **Basic Questions:** General learning questions about the topic (ultimate fallback)
- ✅ **Content-Based:** All questions reference actual facts from Wikipedia articles
- ✅ **Source Attribution:** Quiz clearly indicates questions are based on Wikipedia

#### Quiz Generation Methods:

##### Method 1: AI-Based Generation (Primary)

```kotlin
suspend fun generateQuiz(topic: String) {
    // 1. Fetch Wikipedia content
    val wikiResult = WikipediaService.getTopicSummary(topic)
    
    // 2. Pass Wikipedia content to AI
    // AI generates questions based ONLY on Wikipedia facts
    val prompt = """Generate questions based on this Wikipedia article:
                    ${wikipediaContent}"""
    
    // 3. Parse and validate questions
}
```

##### Method 2: Direct Wikipedia Extraction (Fallback)

```kotlin
fun generateQuestionsFromWikipedia(topic: String, content: String) {
    // Extracts key sentences from Wikipedia
    // Creates fill-in-the-blank questions
    // Generates plausible wrong answers
    // Returns 5 fact-based questions
}
```

##### Method 3: Basic Topic Questions (Ultimate Fallback)

```kotlin
fun generateBasicTopicQuestions(topic: String) {
    // Returns general educational questions
    // Tests fundamental understanding
    // Used only if Wikipedia is unavailable
}
```

## API Service

**File:** `app/src/main/java/com/runanywhere/startup_hackathon20/api/WikipediaService.kt`

### Key Functions:

- `getTopicSummary(topic)` - Fetches comprehensive Wikipedia content
- `getFullArticleExtract(title)` - Gets full article with multiple paragraphs
- `searchWikipedia(query)` - Finds best matching article
- `getRelatedTopics(topic)` - Suggests related articles

## User Experience Improvements

### Study Mode UI Updates:

- 📚 Header emphasizes "Wikipedia + AI enhancements"
- 🔍 Loading message shows "Searching on Wikipedia..."
- ✨ Clear attribution to Wikipedia as source
- 🔗 Direct links to Wikipedia articles
- 📖 Related topics for continued learning

### Quiz Mode UI Updates:

- 📚 Badge shows "Questions based on Wikipedia"
- 🎓 Results screen references Wikipedia content
- 💡 Feedback encourages reviewing Wikipedia articles
- 🔄 Clear indication of content source

## Benefits

1. **Accuracy:** Wikipedia provides verified, fact-checked information
2. **Comprehensiveness:** Full articles with multiple sections and details
3. **Credibility:** Students learn from a trusted, widely-recognized source
4. **Up-to-date:** Wikipedia content is regularly maintained and updated
5. **Free:** No API costs or rate limits for Wikipedia access
6. **Educational:** Encourages students to explore authoritative sources

## Testing Recommendations

Test with various topics:

- **Science:** "Quantum Mechanics", "Photosynthesis", "Theory of Relativity"
- **History:** "World War II", "Ancient Egypt", "Renaissance"
- **Technology:** "Machine Learning", "Blockchain", "Cloud Computing"
- **Arts:** "Impressionism", "Jazz Music", "Shakespeare"
- **Mathematics:** "Calculus", "Linear Algebra", "Number Theory"

## Error Handling

The system includes robust fallbacks:

1. Wikipedia API fails → Try AI generation with topic
2. AI generation fails → Use basic structured content
3. Both fail → Display informative error message

## Future Enhancements

Potential improvements:

- [ ] Cache Wikipedia content for offline access
- [ ] Add images from Wikipedia articles
- [ ] Include article sections as separate study cards
- [ ] Extract key facts as flashcards
- [ ] Add citation links to specific article sections
- [ ] Support multiple languages from Wikipedia
- [ ] Show article quality ratings

## Code Quality

- ✅ Proper error handling with try-catch blocks
- ✅ Coroutine-based async operations
- ✅ Clear function documentation
- ✅ Fallback mechanisms at every level
- ✅ User-friendly loading states and messages
- ✅ Responsive UI with proper feedback

---

**Implementation Date:** 2024
**Status:** ✅ Complete and Functional
**API Used:** Wikipedia REST API (free, no authentication required)
