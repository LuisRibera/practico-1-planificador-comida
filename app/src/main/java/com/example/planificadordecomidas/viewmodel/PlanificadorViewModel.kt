package com.example.planificadordecomidas.viewmodel

import androidx.lifecycle.ViewModel
import com.example.planificadordecomidas.modelo.Ingrediente
import com.example.planificadordecomidas.modelo.ItemCompra
import com.example.planificadordecomidas.modelo.Receta
import com.example.planificadordecomidas.ui.utilidades.formatearCantidad
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

        _estado.value = _estado.value.copy(
            recetas = _estado.value.recetas + nuevaReceta
        )
    }

    fun eliminarReceta(idReceta: Int) {
        val estadoActual = _estado.value

        val recetasActualizadas = estadoActual.recetas.filterNot { it.id == idReceta }
        val planActualizado = estadoActual.planSemanal.map { recetaDelDia ->
            if (recetaDelDia?.id == idReceta) null else recetaDelDia
        }
        val comprasActualizadas = consolidarCompras(planActualizado)

        _estado.value = estadoActual.copy(
            recetas = recetasActualizadas,
            planSemanal = planActualizado,
            comprasConsolidadas = comprasActualizadas,
            itemsComprados = filtrarItemsCompradosVigentes(
                estadoActual.itemsComprados,
                comprasActualizadas
            )
        )
    }

    fun asignarRecetaADia(indiceDia: Int, idReceta: Int) {
        if (indiceDia !in 0..6) return

        val estadoActual = _estado.value
        val recetaSeleccionada = estadoActual.recetas.find { it.id == idReceta } ?: return

        val planActualizado = estadoActual.planSemanal.toMutableList()
        planActualizado[indiceDia] = recetaSeleccionada
        val comprasActualizadas = consolidarCompras(planActualizado)

        _estado.value = estadoActual.copy(
            planSemanal = planActualizado,
            comprasConsolidadas = comprasActualizadas,
            itemsComprados = filtrarItemsCompradosVigentes(
                estadoActual.itemsComprados,
                comprasActualizadas
            )
        )
    }

    fun limpiarDia(indiceDia: Int) {
        if (indiceDia !in 0..6) return

        val estadoActual = _estado.value
        val planActualizado = estadoActual.planSemanal.toMutableList()
        planActualizado[indiceDia] = null
        val comprasActualizadas = consolidarCompras(planActualizado)

        _estado.value = estadoActual.copy(
            planSemanal = planActualizado,
            comprasConsolidadas = comprasActualizadas,
            itemsComprados = filtrarItemsCompradosVigentes(
                estadoActual.itemsComprados,
                comprasActualizadas
            )
        )
    }

    fun actualizarEstadoComprado(nombreItem: String, estaComprado: Boolean) {
        val nombreNormalizado = normalizarNombreItemCompra(nombreItem)
        if (nombreNormalizado.isBlank()) return

        val estadoActual = _estado.value
        val existeEnCompras = estadoActual.comprasConsolidadas.any { it.nombre == nombreNormalizado }
        if (!existeEnCompras) return

        val itemsCompradosActualizados = estadoActual.itemsComprados.toMutableSet()
        if (estaComprado) {
            itemsCompradosActualizados.add(nombreNormalizado)
        } else {
            itemsCompradosActualizados.remove(nombreNormalizado)
        }

        _estado.value = estadoActual.copy(itemsComprados = itemsCompradosActualizados)
    }

    fun actualizarTextoBusqueda(texto: String) {
        _estado.value = _estado.value.copy(
            textoBusqueda = texto
        )
    }

    fun actualizarFiltroIngrediente(ingrediente: String) {
        _estado.value = _estado.value.copy(
            filtroIngrediente = ingrediente
        )
    }

    private fun consolidarCompras(planSemanal: List<Receta?>): List<ItemCompra> {
        val comprasAgrupadas = linkedMapOf<Pair<String, String>, Double>()

        for (receta in planSemanal) {
            if (receta == null) continue

            for (ingrediente in receta.ingredientes) {
                val nombreNormalizado = ingrediente.nombre.trim().lowercase()
                val unidadNormalizada = ingrediente.unidad.trim().lowercase()

                if (nombreNormalizado.isBlank()) continue

                val clave = Pair(nombreNormalizado, unidadNormalizada)
                val cantidadActual = comprasAgrupadas[clave] ?: 0.0
                comprasAgrupadas[clave] = cantidadActual + ingrediente.cantidad
            }
        }

        return comprasAgrupadas.map { (clave, cantidadTotal) ->
            val nombre = clave.first
            val unidad = clave.second

            ItemCompra(
                nombre = nombre,
                cantidadTotal = if (unidad.isNotBlank()) {
                    "${formatearCantidad(cantidadTotal)} $unidad"
                } else {
                    formatearCantidad(cantidadTotal)
                }
            )
        }.sortedBy { it.nombre }
    }

    private fun filtrarItemsCompradosVigentes(
        itemsComprados: Set<String>,
        comprasConsolidadas: List<ItemCompra>
    ): Set<String> {
        val nombresVigentes = comprasConsolidadas
            .map { normalizarNombreItemCompra(it.nombre) }
            .toSet()

        return itemsComprados.filter { it in nombresVigentes }.toSet()
    }

    private fun normalizarNombreItemCompra(nombre: String): String {
        return nombre.trim().lowercase()
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
                ),
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