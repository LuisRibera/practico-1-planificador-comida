package com.example.planificadordecomidas.viewmodel

import com.example.planificadordecomidas.modelo.ItemCompra
import com.example.planificadordecomidas.modelo.Receta

data class EstadoPlanificador(
	val recetas: List<Receta> = emptyList(),
	val planSemanal: List<Receta?> = List(7) { null },
	val comprasConsolidadas: List<ItemCompra> = emptyList(),
	val textoBusqueda: String = "",
	val filtroIngrediente: String = ""
)