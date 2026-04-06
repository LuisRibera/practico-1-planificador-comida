package com.example.planificadordecomidas.ui.pantallas.compras

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.planificadordecomidas.viewmodel.PlanificadorViewModel

@Composable
fun PantallaCompras(
	viewModel: PlanificadorViewModel
) {
	val estado by viewModel.estado.collectAsState()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp)
	) {
		Text(
			text = "Lista de compras",
			style = MaterialTheme.typography.headlineSmall
		)

		if (estado.comprasConsolidadas.isEmpty()) {
			Text(
				text = "No hay ingredientes en la lista de compras",
				modifier = Modifier.padding(top = 16.dp)
			)
		} else {
			LazyColumn(
				modifier = Modifier
					.fillMaxSize()
					.padding(top = 12.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				items(estado.comprasConsolidadas, key = { it.clave }) { itemCompra ->
					ItemCompraFila(
						itemCompra = itemCompra,
						estaComprado = itemCompra.clave in estado.itemsComprados,
						alCambiarEstadoComprado = { estaComprado ->
							viewModel.actualizarEstadoComprado(
								claveItem = itemCompra.clave,
								estaComprado = estaComprado
							)
						}
					)
				}
			}
		}
	}
}

