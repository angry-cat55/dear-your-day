package com.example.dearyourday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.dearyourday.navigation.NavGraph
import com.example.dearyourday.ui.theme.DearYourDayTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DearYourDayTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}