package com.example.planificadordecomidas.ui.navegacion

sealed class DestinoNavegacion(
    val ruta: String,
    val titulo: String
) {
    data object Recetas : DestinoNavegacion(
        ruta = "recetas",
        titulo = "Recetas"
    )

    data object Crear : DestinoNavegacion(
        ruta = "crear",
        titulo = "Crear"
    )

    data object PlanSemanal : DestinoNavegacion(
        ruta = "plan_semanal",
        titulo = "Plan Semanal"
    )

    data object Compras : DestinoNavegacion(
        ruta = "compras",
        titulo = "Compras"
    )

    companion object {
        val itemsBarraInferior = listOf(
            Recetas,
            Crear,
            PlanSemanal,
            Compras
        )
    }
}

