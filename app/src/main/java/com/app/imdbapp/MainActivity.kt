package com.app.imdbapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.app.imdbapp.navigation.NavGraph
import com.app.imdbapp.ui.theme.IMDBAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IMDBAppTheme {
                NavGraph()
            }
        }
    }
}