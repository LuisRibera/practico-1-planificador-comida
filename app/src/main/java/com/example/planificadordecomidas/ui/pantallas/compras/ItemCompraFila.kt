package com.example.planificadordecomidas.ui.pantallas.compras

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.planificadordecomidas.modelo.ItemCompra

@Composable
fun ItemCompraFila(
    itemCompra: ItemCompra,
    estaComprado: Boolean,
    alCambiarEstadoComprado: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Checkbox(
                checked = estaComprado,
                onCheckedChange = alCambiarEstadoComprado
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = itemCompra.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (estaComprado) TextDecoration.LineThrough else TextDecoration.None
                )
                Text(
                    text = itemCompra.cantidadTotal,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

