package com.example.planificadordecomidas.ui.pantallas.plansemanal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import com.example.planificadordecomidas.R
import com.example.planificadordecomidas.modelo.Receta
import com.example.planificadordecomidas.viewmodel.PlanificadorViewModel

@Composable
fun PantallaPlanSemanal(viewModel: PlanificadorViewModel) {
    val estado by viewModel.estado.collectAsState()
    val nombresDias = stringArrayResource(R.array.nombres_dias)
    var indiceDiaSeleccionado by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Plan semanal", style = MaterialTheme.typography.headlineSmall)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(estado.planSemanal) { indiceDia, recetaDelDia ->
                FilaDiaPlanSemanal(
                    nombreDia = nombresDias[indiceDia],
                    recetaAsignada = recetaDelDia,
                    alSeleccionarReceta = { indiceDiaSeleccionado = indiceDia },
                    alQuitarReceta = { viewModel.limpiarDia(indiceDia) }
                )
            }
        }
    }

    indiceDiaSeleccionado?.let { indice ->
        SelectorRecetaDialog(
            recetas = estado.recetas,
            alSeleccionar = { receta ->
                viewModel.asignarRecetaADia(indiceDia = indice, idReceta = receta.id)
                indiceDiaSeleccionado = null
            },
            alCerrar = { indiceDiaSeleccionado = null }
        )
    }
}

@Composable
private fun FilaDiaPlanSemanal(
    nombreDia: String,
    recetaAsignada: Receta?,
    alSeleccionarReceta: () -> Unit,
    alQuitarReceta: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = nombreDia, style = MaterialTheme.typography.titleMedium)
            Text(
                text = recetaAsignada?.nombre ?: "Sin receta asignada",
                modifier = Modifier.padding(top = 4.dp)
            )
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = alSeleccionarReceta) { Text("Seleccionar receta") }
                TextButton(onClick = alQuitarReceta, enabled = recetaAsignada != null) {
                    Text("Quitar")
                }
            }
        }
    }
}

@Composable
private fun SelectorRecetaDialog(
    recetas: List<Receta>,
    alSeleccionar: (Receta) -> Unit,
    alCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = alCerrar,
        title = { Text("Seleccionar receta") },
        text = {
            if (recetas.isEmpty()) {
                Text("No hay recetas disponibles")
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(recetas) { receta ->
                        TextButton(
                            onClick = { alSeleccionar(receta) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(receta.nombre)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = alCerrar) { Text("Cerrar") }
        }
    )
}
