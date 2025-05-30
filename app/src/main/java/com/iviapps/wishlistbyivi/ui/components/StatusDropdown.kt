package com.iviapps.wishlistbyivi.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.iviapps.wishlistbyivi.model.ProductStatus
import com.iviapps.wishlistbyivi.model.displayName

@Composable
fun StatusDropdown(
    selectedStatus: ProductStatus?,
    onStatusSelected: (ProductStatus?) -> Unit,
    includeAllOption: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    val statusOptions = buildList {
        if (includeAllOption) add(null)
        addAll(ProductStatus.values())
    }

    val selectedLabel = when (selectedStatus) {
        null -> if (includeAllOption) "🔄 Todos" else "Elegir estado"
        ProductStatus.WANT_IT -> "❤️ Lo necesito, lo quiero."
        ProductStatus.IN_PROGRESS -> "💭 ¿Lo consideramos? A espera de tu opinión."
        ProductStatus.HAVE_IT -> "➕ Otros, pero es importante."
    }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = selectedLabel)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            statusOptions.forEach { status ->
                val label = when (status) {
                    null -> "🔄 Todos"
                    ProductStatus.WANT_IT -> "❤️ Lo necesito, lo quiero."
                    ProductStatus.IN_PROGRESS -> "💭 ¿Lo consideramos? A espera de tu opinión."
                    ProductStatus.HAVE_IT -> "➕ Otros, pero es importante."
                }

                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onStatusSelected(status)
                        expanded = false
                    }
                )
            }
        }
    }
}
