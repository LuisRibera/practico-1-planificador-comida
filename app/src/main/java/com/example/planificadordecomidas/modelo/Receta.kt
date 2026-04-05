package com.example.planificadordecomidas.modelo

data class Receta(
	val id: Int,
	val nombre: String,
	val ingredientes: List<Ingrediente>
)