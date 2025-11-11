package com.runanywhere.startup_hackathon20

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// import com.runanywhere.startup_hackathon20.api.GroqService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Skill(
    val name: String,
    val progress: Float, // 0.0 to 1.0
    val color: Color
)

class SkillTrackerViewModel : ViewModel() {

    private val _skills = MutableStateFlow<List<Skill>>(emptyList())
    val skills: StateFlow<List<Skill>> = _skills

    private val _suggestedSkills = MutableStateFlow<List<String>>(emptyList())
    val suggestedSkills: StateFlow<List<String>> = _suggestedSkills

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _learningPathAnalysis = MutableStateFlow<String?>(null)
    val learningPathAnalysis: StateFlow<String?> = _learningPathAnalysis

    private val _skillTips = MutableStateFlow<String?>(null)
    val skillTips: StateFlow<String?> = _skillTips

    init {
        // Load initial skills
        _skills.value = listOf(
            Skill("Python", 0.85f, Color(0xFF00C6FF)),
            Skill("Data Science", 0.7f, Color(0xFFFFA726)),
            Skill("UI/UX Design", 0.6f, Color(0xFFAB47BC)),
            Skill("Machine Learning", 0.5f, Color(0xFF66BB6A)),
            Skill("Communication", 0.9f, Color(0xFFF06292))
        )
    }

    fun addSkill(skillName: String, color: Color = Color(0xFF64B5F6)) {
        val newSkill = Skill(skillName, 0.1f, color)
        _skills.value = _skills.value + newSkill
    }

    fun updateSkillProgress(skillName: String, newProgress: Float) {
        _skills.value = _skills.value.map {
            if (it.name == skillName) {
                it.copy(progress = newProgress.coerceIn(0f, 1f))
            } else {
                it
            }
        }
    }

    /**
     * Get AI-powered skill suggestions based on user interests
     */
    fun getSkillSuggestions(interests: String) {
        if (interests.isBlank()) {
            _error.value = "Please enter your interests first"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _suggestedSkills.value = emptyList()

            _error.value = "AI service not configured"
            _isLoading.value = false
            /*
            if (!GroqService.isConfigured()) {
                _error.value = "Groq API key not configured.\n\n" +
                        "Get a FREE API key in 30 seconds:\n" +
                        "1. Visit https://console.groq.com/keys\n" +
                        "2. Sign up with Google/GitHub\n" +
                        "3. Create an API key\n" +
                        "4. Add it to GroqService.kt"
                _isLoading.value = false
                return@launch
            }

            try {
                val suggestions = GroqService.generateSkillSuggestions(interests)

                if (suggestions.isNotEmpty()) {
                    _suggestedSkills.value = suggestions
                } else {
                    _error.value = "Failed to generate skill suggestions. Please try again."
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
            */
        }
    }

    /**
     * Get personalized learning path analysis
     */
    fun getLearningPathAnalysis(interests: String) {
        if (interests.isBlank()) {
            _error.value = "Please enter your interests first"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _learningPathAnalysis.value = null

            _error.value = "AI service not configured"
            _isLoading.value = false
            /*
            if (!GroqService.isConfigured()) {
                _error.value = "Groq API key not configured. Please add it to GroqService.kt"
                _isLoading.value = false
                return@launch
            }

            try {
                val currentSkillNames = _skills.value.map { it.name }
                val analysis = GroqService.analyzeLearningPath(currentSkillNames, interests)

                if (analysis.isNotEmpty()) {
                    _learningPathAnalysis.value = analysis
                } else {
                    _error.value = "Failed to generate learning path analysis."
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
            */
        }
    }

    /**
     * Get development tips for a specific skill
     */
    fun getSkillDevelopmentTips(skillName: String, currentLevel: Float) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _skillTips.value = null

            _error.value = "AI service not configured"
            _isLoading.value = false
            /*
            if (!GroqService.isConfigured()) {
                _error.value = "Groq API key not configured. Please add it to GroqService.kt"
                _isLoading.value = false
                return@launch
            }

            try {
                val tips = GroqService.getSkillDevelopmentTips(skillName, currentLevel)

                if (tips.isNotEmpty()) {
                    _skillTips.value = tips
                } else {
                    _error.value = "Failed to generate skill tips."
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
            */
        }
    }

    /**
     * Get career recommendations based on current skills
     */
    fun getCareerRecommendations() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            _error.value = "AI service not configured"
            _isLoading.value = false
            /*
            if (!GroqService.isConfigured()) {
                _error.value = "Groq API key not configured. Please add it to GroqService.kt"
                _isLoading.value = false
                return@launch
            }

            try {
                val currentSkillNames = _skills.value.map { it.name }
                val recommendations = GroqService.getCareerRecommendations(currentSkillNames)

                if (recommendations.isNotEmpty()) {
                    _learningPathAnalysis.value = recommendations
                } else {
                    _error.value = "Failed to generate career recommendations."
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
            */
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuggestions() {
        _suggestedSkills.value = emptyList()
    }

    fun clearLearningPathAnalysis() {
        _learningPathAnalysis.value = null
    }

    fun clearSkillTips() {
        _skillTips.value = null
    }
}