package com.example.planificadordecomidas.viewmodel

import com.example.planificadordecomidas.modelo.Ingrediente
import com.example.planificadordecomidas.modelo.Receta
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlanificadorViewModelTest {

    @Test
    fun inicia_con_receta_precargada_y_plan_vacio() {
        val viewModel = PlanificadorViewModel()
        val estado = viewModel.estado.value

        assertEquals(1, estado.recetas.size)
        assertEquals(7, estado.planSemanal.size)
        assertTrue(estado.planSemanal.all { it == null })
    }

    @Test
    fun asignar_y_limpiar_dia_funciona_correctamente() {
        val viewModel = PlanificadorViewModel()
        val receta = crearRecetaPrueba(id = 2, nombre = "Pasta")

        viewModel.agregarReceta(receta)
        viewModel.asignarRecetaADia(indiceDia = 0, idReceta = 2)
        assertEquals(2, viewModel.estado.value.planSemanal[0]?.id)

        viewModel.limpiarDia(0)
        assertNull(viewModel.estado.value.planSemanal[0])
    }

    @Test
    fun eliminar_receta_tambien_limpia_su_dia_asignado() {
        val viewModel = PlanificadorViewModel()
        val receta = crearRecetaPrueba(id = 3, nombre = "Tarta")

        viewModel.agregarReceta(receta)
        viewModel.asignarRecetaADia(indiceDia = 3, idReceta = 3)
        viewModel.eliminarReceta(3)

        assertTrue(viewModel.estado.value.recetas.none { it.id == 3 })
        assertNull(viewModel.estado.value.planSemanal[3])
    }

    @Test
    fun lista_de_compras_consolida_por_nombre_normalizado() {
        val viewModel = PlanificadorViewModel()

        val recetaA = Receta(
            id = 4,
            nombre = "Receta A",
            ingredientes = listOf(
                Ingrediente(nombre = " Tomate ", cantidad = 2.0, unidad = "unidades"),
                Ingrediente(nombre = "Arroz", cantidad = 1.0, unidad = "taza")
            )
        )
        val recetaB = Receta(
            id = 5,
            nombre = "Receta B",
            ingredientes = listOf(
                Ingrediente(nombre = "tomate", cantidad = 3.0, unidad = "unidades")
            )
        )

        viewModel.agregarReceta(recetaA)
        viewModel.agregarReceta(recetaB)
        viewModel.asignarRecetaADia(indiceDia = 1, idReceta = 4)
        viewModel.asignarRecetaADia(indiceDia = 2, idReceta = 5)

        val compras = viewModel.estado.value.comprasConsolidadas
        val itemTomate = compras.first { it.nombre == "tomate" }

        assertEquals(5.0, itemTomate.cantidad, 0.001)
        assertEquals("unidades", itemTomate.unidad)
    }

    @Test
    fun asignar_dia_con_indice_invalido_no_modifica_estado() {
        val viewModel = PlanificadorViewModel()
        val receta = crearRecetaPrueba(id = 2, nombre = "Sopa")
        viewModel.agregarReceta(receta)

        val planAntes = viewModel.estado.value.planSemanal.toList()
        viewModel.asignarRecetaADia(indiceDia = 7, idReceta = 2)
        viewModel.asignarRecetaADia(indiceDia = -1, idReceta = 2)

        assertEquals(planAntes, viewModel.estado.value.planSemanal)
    }

    @Test
    fun actualizar_estado_comprado_marca_y_desmarca_correctamente() {
        val viewModel = PlanificadorViewModel()
        val receta = crearRecetaPrueba(id = 2, nombre = "Pasta")
        viewModel.agregarReceta(receta)
        viewModel.asignarRecetaADia(indiceDia = 0, idReceta = 2)

        val nombreIngrediente = "ingrediente"
        viewModel.actualizarEstadoComprado(nombreIngrediente, true)
        assertTrue(viewModel.estado.value.itemsComprados.contains(nombreIngrediente))

        viewModel.actualizarEstadoComprado(nombreIngrediente, false)
        assertFalse(viewModel.estado.value.itemsComprados.contains(nombreIngrediente))
    }

    @Test
    fun actualizar_estado_comprado_ignora_item_inexistente() {
        val viewModel = PlanificadorViewModel()

        viewModel.actualizarEstadoComprado("ingrediente_fantasma", true)

        assertTrue(viewModel.estado.value.itemsComprados.isEmpty())
    }

    @Test
    fun eliminar_receta_limpia_items_comprados_relacionados() {
        val viewModel = PlanificadorViewModel()
        val receta = crearRecetaPrueba(id = 2, nombre = "Pasta")
        viewModel.agregarReceta(receta)
        viewModel.asignarRecetaADia(indiceDia = 0, idReceta = 2)
        viewModel.actualizarEstadoComprado("ingrediente", true)

        viewModel.eliminarReceta(2)

        assertTrue(viewModel.estado.value.itemsComprados.isEmpty())
    }

    @Test
    fun filtrado_recetas_por_nombre_funciona_correctamente() {
        val viewModel = PlanificadorViewModel()
        viewModel.agregarReceta(crearRecetaPrueba(id = 2, nombre = "Sopa de Pollo"))
        viewModel.agregarReceta(crearRecetaPrueba(id = 3, nombre = "Pasta Carbonara"))

        viewModel.actualizarTextoBusqueda("sopa")

        val filtradas = viewModel.estado.value.recetasFiltradas
        assertEquals(1, filtradas.size)
        assertEquals("Sopa de Pollo", filtradas[0].nombre)
    }

    @Test
    fun filtrado_recetas_por_ingrediente_funciona_correctamente() {
        val viewModel = PlanificadorViewModel()
        viewModel.agregarReceta(Receta(
            id = 2, nombre = "Sopa",
            ingredientes = listOf(Ingrediente("Zanahoria", 1.0, "unidad"))
        ))
        viewModel.agregarReceta(Receta(
            id = 3, nombre = "Pasta",
            ingredientes = listOf(Ingrediente("Harina", 200.0, "gramos"))
        ))

        viewModel.actualizarFiltroIngrediente("zanahoria")

        val filtradas = viewModel.estado.value.recetasFiltradas
        assertEquals(1, filtradas.size)
        assertEquals("Sopa", filtradas[0].nombre)
    }

    private fun crearRecetaPrueba(id: Int, nombre: String): Receta {
        return Receta(
            id = id,
            nombre = nombre,
            ingredientes = listOf(
                Ingrediente(nombre = "Ingrediente", cantidad = 1.0, unidad = "unidad")
            )
        )
    }
}
