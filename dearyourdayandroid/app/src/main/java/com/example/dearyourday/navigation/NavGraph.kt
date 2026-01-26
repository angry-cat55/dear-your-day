package com.example.dearyourday.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.dearyourday.ui.screens.diary.DiarySummaryScreen
import com.example.dearyourday.ui.screens.diary.MainDiaryScreen
import com.example.dearyourday.ui.screens.diary.MonthlyDiariesScreen
import com.example.dearyourday.ui.screens.diary.WriteDiaryScreen
import com.example.dearyourday.ui.screens.login.LoginScreen
import com.example.dearyourday.ui.screens.signup.SignUpCompleteScreen
import com.example.dearyourday.ui.screens.signup.SignUpStep1Screen
import com.example.dearyourday.ui.screens.signup.SignUpStep2Screen
import com.example.dearyourday.ui.screens.signup.SignUpStep3Screen
import java.time.LocalDate

@Composable
fun NavGraph(navController: NavHostController) {
    val today = LocalDate.now().toString()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }

        // --- 회원가입 흐름 ---
        composable("signup_step1") {
            SignUpStep1Screen(navController = navController)
        }
        composable("signup_step2") {
            SignUpStep2Screen(navController = navController)
        }
        composable("signup_step3") {
            SignUpStep3Screen(navController = navController)
        }
        composable("signup_complete") {
            SignUpCompleteScreen(navController = navController)
        }

        // --- 메인 흐름 ---
        composable(
            route = "main_diary/{targetDate}",
            arguments = listOf(
                navArgument("targetDate") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val targetDate = backStackEntry.arguments?.getString("targetDate") ?: today
            MainDiaryScreen(navController = navController, targetDate = targetDate)
        }
        composable(
            route = "write_diary/{targetDate}?mode={mode}",
            arguments = listOf(
                navArgument("targetDate") { type = NavType.StringType },
                navArgument("mode") {
                    type = NavType.StringType
                    defaultValue = "write"
                }
            )
        ) { backStackEntry ->
            val targetDate = backStackEntry.arguments?.getString("targetDate") ?: today
            val mode = backStackEntry.arguments?.getString("mode") ?: "write"
            WriteDiaryScreen(
                navController = navController,
                targetDate = targetDate,
                mode = mode
            )
        }
        composable("monthly_diaries") {
            MonthlyDiariesScreen(navController = navController)
        }
        composable("diary_summary") {
            DiarySummaryScreen(navController = navController)
        }
    }
}