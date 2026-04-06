package com.example.planificadordecomidas.ui.utilidades

fun formatearCantidad(cantidad: Double): String {
    return when (cantidad) {
        0.25 -> "1/4"
        0.5 -> "1/2"
        0.75 -> "3/4"
        else -> {
            if (cantidad % 1.0 == 0.0) {
                cantidad.toInt().toString()
            } else {
                cantidad.toString()
            }
        }
    }
}