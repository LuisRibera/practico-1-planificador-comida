package com.example.planificadordecomidas.ui.componentes

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.planificadordecomidas.ui.navegacion.DestinoNavegacion

@Composable
fun BarraNavegacionInferior(
	navController: NavHostController
) {
	val entradaActual = navController.currentBackStackEntryAsState().value
	val destinoActual = entradaActual?.destination

	NavigationBar {
		DestinoNavegacion.itemsBarraInferior.forEach { destino ->
			val estaSeleccionado = destinoActual
				?.hierarchy
				?.any { it.route == destino.ruta } == true

			NavigationBarItem(
				selected = estaSeleccionado,
				onClick = {
					navController.navigate(destino.ruta) {
						popUpTo(navController.graph.startDestinationId) {
							saveState = true
						}
						launchSingleTop = true
						restoreState = true
					}
				},
				icon = { },
				label = { Text(text = destino.titulo) }
			)
		}
	}
}

