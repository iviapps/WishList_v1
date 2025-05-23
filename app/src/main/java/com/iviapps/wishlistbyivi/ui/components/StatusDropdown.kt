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
    selectedStatus: ProductStatus,
    onStatusSelected: (ProductStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(text = selectedStatus.displayName())
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ProductStatus.values().forEach { status ->
                DropdownMenuItem(
                    text = { Text(status.displayName()) },
                    onClick = {
                        onStatusSelected(status)
                        expanded = false
                    }
                )
            }
        }
    }
}
