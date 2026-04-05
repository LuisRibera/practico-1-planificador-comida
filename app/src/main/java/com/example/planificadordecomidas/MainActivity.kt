package com.example.planificadordecomidas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.planificadordecomidas.ui.PlanificadorApp
import com.example.planificadordecomidas.ui.theme.PlanificadorDeComidasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlanificadorDeComidasTheme {
                PlanificadorApp()
            }
        }
    }
}
