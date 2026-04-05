package com.example.planificadordecomidas.modelo

data class Receta(
	val id: String,
	val nombre: String,
	val ingredientes: List<Ingrediente>
)

