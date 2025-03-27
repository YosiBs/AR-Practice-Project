 package com.example.arlearner

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.arlearner.ui.navigation.ARScreen
import com.example.arlearner.ui.screens.ARScreenView
import com.example.arlearner.ui.navigation.AlphabetScreen
import com.example.arlearner.ui.navigation.HomeScreen
import com.example.arlearner.ui.navigation.QuizScreen
import com.example.arlearner.ui.screens.AlphabetScreen
import com.example.arlearner.ui.screens.HomeScreen
import com.example.arlearner.ui.theme.ARLearnerTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ARLearnerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = HomeScreen, modifier = Modifier.padding(innerPadding)) {
                        composable<HomeScreen> {
                            HomeScreen(navController)
                        }
                        composable<ARScreen> {
                            val alphabet = it.toRoute<ARScreen>().model
                            ARScreenView(navController, alphabet,this@MainActivity)
                        }
                        composable<AlphabetScreen> {
                            AlphabetScreen(navController)
                        }
                        composable<QuizScreen> {

                        }
                    }
                }
            }
        }
    }
}