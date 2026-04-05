package com.example.planificadordecomidas.viewmodel

import androidx.lifecycle.ViewModel
import com.example.planificadordecomidas.modelo.Ingrediente
import com.example.planificadordecomidas.modelo.ItemCompra
import com.example.planificadordecomidas.modelo.Receta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlanificadorViewModel : ViewModel() {

    private var siguienteIdReceta = 2

    private val recetaInicial = crearRecetaPrecargada()

    private val _estado = MutableStateFlow(
        EstadoPlanificador(
            recetas = listOf(recetaInicial)
        )
    )
    val estado: StateFlow<EstadoPlanificador> = _estado.asStateFlow()

    fun agregarReceta(receta: Receta) {
        val estadoActual = _estado.value
        val yaExiste = estadoActual.recetas.any { it.id == receta.id }

        if (yaExiste) return

        _estado.value = estadoActual.copy(
            recetas = estadoActual.recetas + receta
        )
    }

    fun crearYAgregarReceta(
        nombre: String,
        ingredientes: List<Ingrediente>
    ) {
        val nuevaReceta = Receta(
            id = siguienteIdReceta++,
            nombre = nombre,
            ingredientes = ingredientes
        )

        agregarReceta(nuevaReceta)
    }

    fun eliminarReceta(idReceta: Int) {
        val estadoActual = _estado.value

        val recetasActualizadas = estadoActual.recetas.filterNot { it.id == idReceta }
        val planActualizado = estadoActual.planSemanal.map { recetaDelDia ->
            if (recetaDelDia?.id == idReceta) null else recetaDelDia
        }

        _estado.value = estadoActual.copy(
            recetas = recetasActualizadas,
            planSemanal = planActualizado,
            comprasConsolidadas = consolidarCompras(planActualizado)
        )
    }

    fun asignarRecetaADia(indiceDia: Int, idReceta: Int) {
        if (indiceDia !in 0..6) return

        val estadoActual = _estado.value
        val recetaSeleccionada = estadoActual.recetas.find { it.id == idReceta } ?: return

        val planActualizado = estadoActual.planSemanal.toMutableList()
        planActualizado[indiceDia] = recetaSeleccionada

        _estado.value = estadoActual.copy(
            planSemanal = planActualizado,
            comprasConsolidadas = consolidarCompras(planActualizado)
        )
    }

    fun limpiarDia(indiceDia: Int) {
        if (indiceDia !in 0..6) return

        val estadoActual = _estado.value
        val planActualizado = estadoActual.planSemanal.toMutableList()
        planActualizado[indiceDia] = null

        _estado.value = estadoActual.copy(
            planSemanal = planActualizado,
            comprasConsolidadas = consolidarCompras(planActualizado)
        )
    }

    private fun consolidarCompras(planSemanal: List<Receta?>): List<ItemCompra> {
        val compras = mutableListOf<ItemCompra>()

        for (receta in planSemanal) {
            if (receta == null) continue

            for (ingrediente in receta.ingredientes) {
                val nombreNormalizado = ingrediente.nombre.trim().lowercase()
                if (nombreNormalizado.isEmpty()) continue

                val cantidad = ingrediente.cantidad
                val unidad = ingrediente.unidad.trim().lowercase()
                val descripcionCantidad = "$cantidad $unidad"
                val indiceExistente = compras.indexOfFirst { it.nombre == nombreNormalizado }

                if (indiceExistente == -1) {
                    compras.add(
                        ItemCompra(
                            nombre = nombreNormalizado,
                            cantidadTotal = descripcionCantidad
                        )
                    )
                } else {
                    val itemActual = compras[indiceExistente]
                    val cantidadCombinada = combinarCantidades(
                        itemActual.cantidadTotal,
                        descripcionCantidad
                    )

                    compras[indiceExistente] = itemActual.copy(
                        cantidadTotal = cantidadCombinada
                    )
                }
            }
        }

        return compras.sortedBy { it.nombre }
    }

    private fun combinarCantidades(cantidadActual: String, nuevaCantidad: String): String {
        val actual = cantidadActual.trim()
        val nueva = nuevaCantidad.trim()

        if (actual.isEmpty()) return nueva
        if (nueva.isEmpty()) return actual

        return "$actual + $nueva"
    }

    private fun crearRecetaPrecargada(): Receta {
        return Receta(
            id = 1,
            nombre = "Ensalada simple",
            ingredientes = listOf(
                Ingrediente(
                    nombre = "Lechuga",
                    cantidad = 1.0,
                    unidad = "hoja"
                )
                ,
                Ingrediente(
                    nombre = "Tomate",
                    cantidad = 2.0,
                    unidad = "unidades"
                ),
                Ingrediente(
                    nombre = "Aceite de oliva",
                    cantidad = 2.0,
                    unidad = "cucharadas"
                )
            )
        )
    }
}