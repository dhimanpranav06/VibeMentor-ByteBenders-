package com.runanywhere.startup_hackathon20

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {

        // 🏠 Home Screen (Dashboard)
        composable("home") { VibeMentorHomeScreen(navController) }

        // 💬 AI Chat Screen
        composable("chat") { ChatScreen() }

        // 📚 Study Mode Screen
        composable("study") { StudyModeScreen(navController) }

        // 💼 Career Mode Screen
        composable("career") { CareerModeScreen(navController) }

        // 🎨 Creative Muse Screen
        composable("creative") { CreativeMuseScreen() }

        composable("career_resume") { ResumeBuilderScreen() }
        composable("career_tracker") { SkillTrackerScreen() }
        composable("career_interview") { MockInterviewScreen(navController) }

    }
}
