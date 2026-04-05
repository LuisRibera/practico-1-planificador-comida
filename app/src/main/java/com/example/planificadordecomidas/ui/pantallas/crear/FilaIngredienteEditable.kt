package com.example.planificadordecomidas.ui.pantallas.crear

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilaIngredienteEditable(
    ingrediente: IngredienteEditable,
    alCambiar: (IngredienteEditable) -> Unit,
    alEliminar: (() -> Unit)? = null,
    puedeEliminar: Boolean = true,
    modificador: Modifier = Modifier
) {
    Column(
        modifier = modificador
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        OutlinedTextField(
            value = ingrediente.nombre,
            onValueChange = { nuevoValor ->
                alCambiar(ingrediente.copy(nombre = nuevoValor))
            },
            label = { Text("Nombre del ingrediente") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = ingrediente.cantidad,
            onValueChange = { nuevoValor ->
                alCambiar(ingrediente.copy(cantidad = nuevoValor))
            },
            label = { Text("Cantidad") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = ingrediente.unidad,
            onValueChange = { nuevoValor ->
                alCambiar(ingrediente.copy(unidad = nuevoValor))
            },
            label = { Text("Unidad") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            singleLine = true
        )

        if (puedeEliminar && alEliminar != null) {
            Button(
                onClick = alEliminar,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Eliminar ingrediente")
            }
        }
    }
}


