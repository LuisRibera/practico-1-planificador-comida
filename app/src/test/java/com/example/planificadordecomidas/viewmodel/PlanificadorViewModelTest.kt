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

        assertEquals(7, estado.recetas.size)
        assertEquals(7, estado.planSemanal.size)
        assertTrue(estado.planSemanal.all { it == null })
    }

    @Test
    fun asignar_y_limpiar_dia_funciona_correctamente() {
        val viewModel = PlanificadorViewModel()
        val receta = crearRecetaPrueba(id = 10, nombre = "Receta test")

        viewModel.agregarReceta(receta)
        viewModel.asignarRecetaADia(indiceDia = 0, idReceta = 10)
        assertEquals(10, viewModel.estado.value.planSemanal[0]?.id)

        viewModel.limpiarDia(0)
        assertNull(viewModel.estado.value.planSemanal[0])
    }

    @Test
    fun eliminar_receta_tambien_limpia_su_dia_asignado() {
        val viewModel = PlanificadorViewModel()
        val receta = crearRecetaPrueba(id = 10, nombre = "Receta test")

        viewModel.agregarReceta(receta)
        viewModel.asignarRecetaADia(indiceDia = 3, idReceta = 10)
        viewModel.eliminarReceta(10)

        assertTrue(viewModel.estado.value.recetas.none { it.id == 10 })
        assertNull(viewModel.estado.value.planSemanal[3])
    }

    @Test
    fun lista_de_compras_consolida_por_nombre_normalizado() {
        val viewModel = PlanificadorViewModel()

        val recetaA = Receta(
            id = 10,
            nombre = "Receta A",
            ingredientes = listOf(
                Ingrediente(nombre = " Pepino ", cantidad = 2.0, unidad = "unidades"),
                Ingrediente(nombre = "Arroz", cantidad = 1.0, unidad = "taza")
            )
        )
        val recetaB = Receta(
            id = 11,
            nombre = "Receta B",
            ingredientes = listOf(
                Ingrediente(nombre = "pepino", cantidad = 3.0, unidad = "unidades")
            )
        )

        viewModel.agregarReceta(recetaA)
        viewModel.agregarReceta(recetaB)
        viewModel.asignarRecetaADia(indiceDia = 1, idReceta = 10)
        viewModel.asignarRecetaADia(indiceDia = 2, idReceta = 11)

        val compras = viewModel.estado.value.comprasConsolidadas
        val itemPepino = compras.first { it.nombre == "pepino" }

        assertEquals(5.0, itemPepino.cantidad, 0.001)
        assertEquals("unidades", itemPepino.unidad)
    }

    @Test
    fun asignar_dia_con_indice_invalido_no_modifica_estado() {
        val viewModel = PlanificadorViewModel()
        val planAntes = viewModel.estado.value.planSemanal.toList()

        viewModel.asignarRecetaADia(indiceDia = 7, idReceta = 1)
        viewModel.asignarRecetaADia(indiceDia = -1, idReceta = 1)

        assertEquals(planAntes, viewModel.estado.value.planSemanal)
    }

    @Test
    fun actualizar_estado_comprado_marca_y_desmarca_correctamente() {
        val viewModel = PlanificadorViewModel()
        viewModel.asignarRecetaADia(indiceDia = 0, idReceta = 1)

        val claveIngrediente = "lechuga_planta"
        viewModel.actualizarEstadoComprado(claveIngrediente, true)
        assertTrue(viewModel.estado.value.itemsComprados.contains(claveIngrediente))

        viewModel.actualizarEstadoComprado(claveIngrediente, false)
        assertFalse(viewModel.estado.value.itemsComprados.contains(claveIngrediente))
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
        viewModel.asignarRecetaADia(indiceDia = 0, idReceta = 1)
        viewModel.actualizarEstadoComprado("lechuga_planta", true)

        viewModel.eliminarReceta(1)

        assertTrue(viewModel.estado.value.itemsComprados.isEmpty())
    }

    @Test
    fun filtrado_recetas_por_nombre_funciona_correctamente() {
        val viewModel = PlanificadorViewModel()
        viewModel.agregarReceta(crearRecetaPrueba(id = 10, nombre = "Cazuela unica"))

        viewModel.actualizarTextoBusqueda("cazuela unica")

        val filtradas = viewModel.estado.value.recetasFiltradas
        assertEquals(1, filtradas.size)
        assertEquals("Cazuela unica", filtradas[0].nombre)
    }

    @Test
    fun filtrado_recetas_por_ingrediente_funciona_correctamente() {
        val viewModel = PlanificadorViewModel()
        viewModel.agregarReceta(Receta(
            id = 10, nombre = "Receta especial",
            ingredientes = listOf(Ingrediente("Quinoa", 1.0, "taza"))
        ))

        viewModel.actualizarFiltroIngrediente("quinoa")

        val filtradas = viewModel.estado.value.recetasFiltradas
        assertEquals(1, filtradas.size)
        assertEquals("Receta especial", filtradas[0].nombre)
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
