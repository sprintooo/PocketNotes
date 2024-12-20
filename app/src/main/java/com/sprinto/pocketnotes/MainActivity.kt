package com.sprinto.pocketnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sprinto.pocketnotes.core.navigation.SetupNavGraph
import com.sprinto.pocketnotes.ui.theme.PocketNotesTheme

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PocketNotesTheme {
                navController = rememberNavController()
                SetupNavGraph(navController = navController)
            }
        }
    }
}