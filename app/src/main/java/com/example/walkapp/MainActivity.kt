package com.example.walkapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.walkapp.navigation.RootNavGraph
import com.example.walkapp.ui.theme.AppTheme
import org.koin.compose.KoinContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                KoinContext{
                    val navController = rememberNavController()
                    RootNavGraph(navController = navController)
                }
            }
        }
    }
}