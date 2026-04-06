package com.example.planificadordecomidas.ui.pantallas.recetas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.planificadordecomidas.ui.componentes.BarraBusqueda
import com.example.planificadordecomidas.ui.componentes.TarjetaReceta
import com.example.planificadordecomidas.ui.navegacion.DestinoNavegacion
import com.example.planificadordecomidas.viewmodel.PlanificadorViewModel

@Composable
fun PantallaRecetas(
    viewModel: PlanificadorViewModel,
    navController: NavHostController
) {
    val estado by viewModel.estado.collectAsState()
    val recetasFiltradas = estado.recetasFiltradas

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(DestinoNavegacion.Crear.ruta)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear nueva receta"
                )
            }
        }
    ) { paddingScaffold ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingScaffold)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            BarraBusqueda(
                valor = estado.textoBusqueda,
                alCambiar = { nuevoTexto ->
                    viewModel.actualizarTextoBusqueda(nuevoTexto)
                },
                etiqueta = "Buscar por nombre..."
            )

            Spacer(modifier = Modifier.height(8.dp))

            BarraBusqueda(
                valor = estado.filtroIngrediente,
                alCambiar = { nuevoFiltro ->
                    viewModel.actualizarFiltroIngrediente(nuevoFiltro)
                },
                etiqueta = "Filtrar por ingrediente..."
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (recetasFiltradas.isEmpty()) {
                Text(text = "No hay recetas que coincidan con tu búsqueda")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(recetasFiltradas) { receta ->
                        TarjetaReceta(
                            receta = receta,
                            alEliminar = {
                                viewModel.eliminarReceta(receta.id)
                            }
                        )
                    }
                }
            }
        }
    }
}