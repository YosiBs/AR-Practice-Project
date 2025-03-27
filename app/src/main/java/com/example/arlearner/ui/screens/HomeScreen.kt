package com.example.arlearner.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.arlearner.ui.navigation.AlphabetScreen
import com.example.arlearner.ui.navigation.HomeScreen
import com.example.arlearner.ui.navigation.QuizScreen


@Composable
fun HomeScreen(navController: NavController){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center ,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally){

        Button(onClick = { navController.navigate(AlphabetScreen) }){
            Text(text = "Alphabets")
        }
        Button(onClick = { navController.navigate(QuizScreen)}){
            Text(text = "Quiz")
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview(){
    val navController = rememberNavController()
    HomeScreen(navController)
}