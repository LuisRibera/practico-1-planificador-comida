package com.example.planificadordecomidas.viewmodel

import com.example.planificadordecomidas.modelo.ItemCompra
import com.example.planificadordecomidas.modelo.Receta

data class EstadoPlanificador(
	val recetas: List<Receta> = emptyList(),
	val planSemanal: List<Receta?> = List(7) { null },
	val comprasConsolidadas: List<ItemCompra> = emptyList(),
	val itemsComprados: Set<String> = emptySet(),
	val textoBusqueda: String = "",
	val filtroIngrediente: String = ""
) {
	val recetasFiltradas: List<Receta>
		get() {
			val busqueda = textoBusqueda.trim().lowercase()
			val filtro = filtroIngrediente.trim().lowercase()
			return recetas.filter { receta ->
				val coincideNombre = busqueda.isBlank() ||
						receta.nombre.lowercase().contains(busqueda)
				val coincideIngrediente = filtro.isBlank() ||
						receta.ingredientes.any { it.nombre.lowercase().contains(filtro) }
				coincideNombre && coincideIngrediente
			}
		}
}