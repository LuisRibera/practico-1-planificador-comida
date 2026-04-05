package com.example.planificadordecomidas.ui.pantallas.crear

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.planificadordecomidas.modelo.Ingrediente
import com.example.planificadordecomidas.viewmodel.PlanificadorViewModel

@Composable
fun PantallaCrear(
	viewModel: PlanificadorViewModel,
	navController: NavHostController
) {
	var nombreReceta by remember { mutableStateOf("") }
	val ingredientesEditables = remember { mutableStateListOf(IngredienteEditable()) }
	var mostrarErrores by remember { mutableStateOf(false) }

	val nombreError = if (mostrarErrores && nombreReceta.trim().isEmpty()) {
		"El nombre de la receta es obligatorio"
	} else {
		null
	}

	val ingredientesValidos = ingredientesEditables.filter { ingrediente ->
		esIngredienteValido(ingrediente)
	}

	val ingredientesError = if (mostrarErrores && ingredientesValidos.isEmpty()) {
		"Debes agregar al menos un ingrediente válido"
	} else {
		null
	}

	Scaffold { paddingInterno ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingInterno)
		) {
			Text(
				text = "Crear receta",
				style = MaterialTheme.typography.headlineSmall,
				modifier = Modifier.padding(16.dp)
			)

			FormularioCrearReceta(
				nombreReceta = nombreReceta,
				alCambiarNombreReceta = { nuevoNombre ->
					nombreReceta = nuevoNombre
				},
				ingredientes = ingredientesEditables,
				alCambiarIngrediente = { indice, ingredienteActualizado ->
					ingredientesEditables[indice] = ingredienteActualizado
				},
				alAgregarIngrediente = {
					ingredientesEditables.add(IngredienteEditable())
				},
				alEliminarIngrediente = { indice ->
					if (ingredientesEditables.size > 1) {
						ingredientesEditables.removeAt(indice)
					}
				},
				nombreError = nombreError,
				ingredientesError = ingredientesError,
				puedeGuardar = nombreReceta.trim().isNotEmpty() && ingredientesValidos.isNotEmpty(),
				alGuardar = {
					mostrarErrores = true

					val nombreValido = nombreReceta.trim().isNotEmpty()
					val ingredientesSonValidos = ingredientesValidos.isNotEmpty()

					if (nombreValido && ingredientesSonValidos) {
						val ingredientes = ingredientesValidos.map { ingrediente ->
							Ingrediente(
								nombre = ingrediente.nombre.trim(),
								cantidad = ingrediente.cantidad.toDoubleOrNull() ?: 0.0,
								unidad = ingrediente.unidad.trim()
							)
						}

						viewModel.crearYAgregarReceta(
							nombre = nombreReceta.trim(),
							ingredientes = ingredientes
						)

						navController.popBackStack()
					}
				},
				alCancelar = {
					navController.popBackStack()
				}
			)

			Spacer(modifier = Modifier.height(16.dp))
		}
	}
}

private fun esIngredienteValido(ingrediente: IngredienteEditable): Boolean {
	val cantidadNumerica = ingrediente.cantidad.toDoubleOrNull() ?: return false
	return ingrediente.nombre.trim().isNotEmpty() &&
		ingrediente.unidad.trim().isNotEmpty() &&
		cantidadNumerica > 0
}

