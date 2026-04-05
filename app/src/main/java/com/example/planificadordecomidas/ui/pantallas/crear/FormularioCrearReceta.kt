package com.example.planificadordecomidas.ui.pantallas.crear

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FormularioCrearReceta(
    nombreReceta: String,
    alCambiarNombreReceta: (String) -> Unit,
    ingredientes: List<IngredienteEditable>,
    alCambiarIngrediente: (indice: Int, ingrediente: IngredienteEditable) -> Unit,
    alAgregarIngrediente: () -> Unit,
    alEliminarIngrediente: (indice: Int) -> Unit,
    nombreError: String?,
    ingredientesError: String?,
    puedeGuardar: Boolean,
    alGuardar: () -> Unit,
    alCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = nombreReceta,
            onValueChange = alCambiarNombreReceta,
            label = { Text("Nombre de la receta") },
            modifier = Modifier.fillMaxWidth(),
            isError = nombreError != null,
            singleLine = true
        )

        if (nombreError != null) {
            Text(
                text = nombreError,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        Text(text = "Ingredientes")

        if (ingredientesError != null) {
            Text(
                text = ingredientesError,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        ingredientes.forEachIndexed { indice, ingrediente ->
            FilaIngredienteEditable(
                ingrediente = ingrediente,
                alCambiar = { ingredienteActualizado ->
                    alCambiarIngrediente(indice, ingredienteActualizado)
                },
                alEliminar = if (ingredientes.size > 1) {
                    { alEliminarIngrediente(indice) }
                } else {
                    null
                },
                puedeEliminar = ingredientes.size > 1
            )
        }

        TextButton(
            onClick = alAgregarIngrediente,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Agregar ingrediente")
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = alCancelar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = alGuardar,
                enabled = puedeGuardar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}




