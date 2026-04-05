package com.example.planificadordecomidas.ui

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.planificadordecomidas.ui.componentes.BarraNavegacionInferior
import com.example.planificadordecomidas.ui.navegacion.AppNavHost

@Composable
fun PlanificadorApp() {
	val navController = rememberNavController()

	Scaffold(
		bottomBar = {
			BarraNavegacionInferior(navController = navController)
		}
	) { paddingInterno ->
		AppNavHost(
			navController = navController,
			paddingInterno = paddingInterno
		)
	}
}

