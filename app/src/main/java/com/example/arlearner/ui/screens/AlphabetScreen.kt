package com.example.arlearner.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.arlearner.ui.navigation.ARScreen
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlphabetScreen(navController: NavController) {
    val context = LocalContext.current

    // ✅ Get the save success flag from `savedStateHandle` directly
    val saveSuccess = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<Boolean>("saveSuccess")

    // ✅ Show a toast message if objects were saved
    if (saveSuccess == true) {
        Toast.makeText(context, "Objects saved successfully!", Toast.LENGTH_SHORT).show()
        navController.currentBackStackEntry?.savedStateHandle?.set("saveSuccess", false) // Reset flag
    }


    val listOfAlphabets = listOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    )

    Column{
        Box(modifier = Modifier.height(60.dp)){
            Text(text = "Alphabets", fontSize = 24.sp, modifier = Modifier.align(Alignment.Center))

        }
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.Center
        ){
            listOfAlphabets.forEach { alphabet ->
                AlphabetItem(alphabet = alphabet){
                    navController.navigate(ARScreen(alphabet))
                }
            }
        }
    }


}

@Composable
fun AlphabetItem(alphabet: String,onClick: () -> Unit){
    val color = remember(alphabet)  {
        generateRandomLightColor()
    }
    Box(modifier = Modifier
        .padding(16.dp)
        .size(60.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(color)
        .clickable { onClick() }){

        Text(text = alphabet, fontSize = 24.sp, modifier = Modifier.align(Alignment.Center))
    }
}

fun generateRandomLightColor(): Color{
    val random = Random( System.currentTimeMillis())
    val red = random.nextInt(150,256)
    val green = random.nextInt(200,256)
    val blue = random.nextInt(200,256)
    val color = Color(red, green, blue)

    return color
}

@Preview
@Composable
fun AlphabetScreenPreview(){
    val navController = NavController(LocalContext.current)
    AlphabetScreen(navController)
}