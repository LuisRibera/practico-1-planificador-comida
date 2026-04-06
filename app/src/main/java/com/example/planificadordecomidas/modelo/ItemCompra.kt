package com.example.planificadordecomidas.modelo

data class ItemCompra(
    val nombre: String,
    val cantidad: Double,
    val unidad: String
) {
    val clave: String get() = "${nombre}_${unidad}"
}
