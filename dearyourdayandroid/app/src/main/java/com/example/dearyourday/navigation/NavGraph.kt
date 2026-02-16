package com.example.dearyourday.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dearyourday.data.model.SignUpViewModel
import com.example.dearyourday.ui.screens.diary.DiarySummaryScreen
import com.example.dearyourday.ui.screens.diary.MainDiaryScreen
import com.example.dearyourday.ui.screens.diary.MonthlyDiariesScreen
import com.example.dearyourday.ui.screens.diary.WriteDiaryScreen
import com.example.dearyourday.ui.screens.login.LoginScreen
import com.example.dearyourday.ui.screens.signup.SignUpCompleteScreen
import com.example.dearyourday.ui.screens.signup.SignUpStep1Screen
import com.example.dearyourday.ui.screens.signup.SignUpStep2Screen
import com.example.dearyourday.ui.screens.signup.SignUpStep3Screen
import com.example.dearyourday.ui.screens.user.UserScreen
import java.time.LocalDate

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String // MainActivuty에서 전달받은 시작 화면
) {
    val today = LocalDate.now().toString()
    val signUpViewModel: SignUpViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // --- 로그인 흐름 ---
        composable("login") {
            LoginScreen(navController = navController)
        }

        // --- 회원가입 흐름 ---
        composable("signup_step1") {
            SignUpStep1Screen(navController = navController, viewModel = signUpViewModel)
        }
        composable("signup_step2") {
            SignUpStep2Screen(navController = navController, viewModel = signUpViewModel)
        }
        composable("signup_step3") {
            SignUpStep3Screen(navController = navController, viewModel = signUpViewModel)
        }
        composable("signup_complete") {
            SignUpCompleteScreen(navController = navController, viewModel = signUpViewModel)
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

        // --- 유저 화면 흐름 ---
        composable("user") {
            UserScreen(navController = navController)
        }
    }
}