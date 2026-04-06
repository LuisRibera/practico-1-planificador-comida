package com.example.planificadordecomidas.viewmodel

import androidx.lifecycle.ViewModel
import com.example.planificadordecomidas.modelo.Ingrediente
import com.example.planificadordecomidas.modelo.ItemCompra
import com.example.planificadordecomidas.modelo.Receta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlanificadorViewModel : ViewModel() {

    private val _estado = MutableStateFlow(
        EstadoPlanificador(recetas = listOf(crearRecetaPrecargada()))
    )
    val estado: StateFlow<EstadoPlanificador> = _estado.asStateFlow()

    // ID derivado del máximo existente para evitar duplicados
    private var siguienteIdReceta =
        (_estado.value.recetas.maxOfOrNull { it.id } ?: 0) + 1

    fun agregarReceta(receta: Receta) {
        val estadoActual = _estado.value
        if (estadoActual.recetas.any { it.id == receta.id }) return
        _estado.value = estadoActual.copy(recetas = estadoActual.recetas + receta)
    }

    fun crearYAgregarReceta(nombre: String, ingredientes: List<Ingrediente>) {
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
        val nombreNormalizado = normalizarNombre(nombreItem)
        if (nombreNormalizado.isBlank()) return
        val estadoActual = _estado.value
        if (estadoActual.comprasConsolidadas.none { it.nombre == nombreNormalizado }) return
        val itemsActualizados = estadoActual.itemsComprados.toMutableSet()
        if (estaComprado) itemsActualizados.add(nombreNormalizado)
        else itemsActualizados.remove(nombreNormalizado)
        _estado.value = estadoActual.copy(itemsComprados = itemsActualizados)
    }

    fun actualizarTextoBusqueda(texto: String) {
        _estado.value = _estado.value.copy(textoBusqueda = texto)
    }

    fun actualizarFiltroIngrediente(ingrediente: String) {
        _estado.value = _estado.value.copy(filtroIngrediente = ingrediente)
    }

    fun obtenerListaComprasConsolidada(): List<ItemCompra> =
        _estado.value.comprasConsolidadas

    private fun consolidarCompras(planSemanal: List<Receta?>): List<ItemCompra> {
        val agrupado = linkedMapOf<Pair<String, String>, Double>()
        for (receta in planSemanal) {
            if (receta == null) continue
            for (ingrediente in receta.ingredientes) {
                val nombre = normalizarNombre(ingrediente.nombre)
                val unidad = ingrediente.unidad.trim().lowercase()
                if (nombre.isBlank()) continue
                val clave = Pair(nombre, unidad)
                agrupado[clave] = (agrupado[clave] ?: 0.0) + ingrediente.cantidad
            }
        }
        return agrupado.map { (clave, total) ->
            ItemCompra(nombre = clave.first, cantidad = total, unidad = clave.second)
        }.sortedBy { it.nombre }
    }

    private fun filtrarItemsCompradosVigentes(
        itemsComprados: Set<String>,
        comprasConsolidadas: List<ItemCompra>
    ): Set<String> {
        val vigentes = comprasConsolidadas.map { it.nombre }.toSet()
        return itemsComprados.filter { it in vigentes }.toSet()
    }

    private fun normalizarNombre(nombre: String): String = nombre.trim().lowercase()

    private fun crearRecetaPrecargada(): Receta {
        return Receta(
            id = 1,
            nombre = "Ensalada simple",
            ingredientes = listOf(
                Ingrediente(nombre = "Lechuga", cantidad = 1.0, unidad = "hoja"),
                Ingrediente(nombre = "Tomate", cantidad = 2.0, unidad = "unidades"),
                Ingrediente(nombre = "Aceite de oliva", cantidad = 2.0, unidad = "cucharadas")
            )
        )
    }
}
