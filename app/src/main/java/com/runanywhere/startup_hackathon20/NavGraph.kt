package com.runanywhere.startup_hackathon20

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {

        // 🏠 Home Screen (Dashboard)
        composable("home") { VibeMentorHomeScreen(navController) }

        // 💬 AI Chat Screen
        composable("chat") { ChatScreen(navController) }

        // 📚 Study Mode Screen
        composable("study") { StudyModeScreen(navController) }

        // 📝 Quiz Screen with topic parameter
        composable(
            route = "quiz/{topic}",
            arguments = listOf(navArgument("topic") { type = NavType.StringType })
        ) { backStackEntry ->
            val topic = backStackEntry.arguments?.getString("topic") ?: "General"
            QuizScreen(navController, topic)
        }

        // 💼 Career Mode Screen
        composable("career") { CareerModeScreen(navController) }

        // 🎨 Creative Muse Screen
        composable("creative") { CreativeMuseScreen() }

        composable("career_resume") { ResumeBuilderScreen(navController) }
        composable("career_tracker") { SkillTrackerScreen() }
        composable("career_interview") { MockInterviewScreen(navController) }

    }
}
