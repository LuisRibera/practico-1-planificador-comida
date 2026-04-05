package com.example.planificadordecomidas.ui.navegacion

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.planificadordecomidas.ui.pantallas.compras.PantallaCompras
import com.example.planificadordecomidas.ui.pantallas.crear.PantallaCrear
import com.example.planificadordecomidas.ui.pantallas.plansemanal.PantallaPlanSemanal
import com.example.planificadordecomidas.ui.pantallas.recetas.PantallaRecetas
import com.example.planificadordecomidas.viewmodel.PlanificadorViewModel

@Composable
fun AppNavHost(
	navController: NavHostController,
	paddingInterno: PaddingValues,
	viewModel: PlanificadorViewModel
) {
	NavHost(
		navController = navController,
		startDestination = DestinoNavegacion.Recetas.ruta,
		modifier = Modifier
			.fillMaxSize()
			.padding(paddingInterno)
	) {
		composable(DestinoNavegacion.Recetas.ruta) {
			PantallaRecetas(
				viewModel = viewModel,
				navController = navController
			)
		}
		composable(DestinoNavegacion.Crear.ruta) {
			PantallaCrear(
				viewModel = viewModel,
				navController = navController
			)
		}
		composable(DestinoNavegacion.PlanSemanal.ruta) {
			PantallaPlanSemanal(viewModel = viewModel)
		}
		composable(DestinoNavegacion.Compras.ruta) {
			PantallaCompras(viewModel = viewModel)
		}
	}
}

