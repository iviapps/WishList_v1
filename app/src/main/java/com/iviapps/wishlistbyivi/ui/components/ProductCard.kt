package com.iviapps.wishlistbyivi.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iviapps.wishlistbyivi.model.Product
import com.iviapps.wishlistbyivi.model.displayName

@Composable
fun ProductCard(product: Product, linkColor: Color) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 🔠 Nombre + estrella
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (product.favorite) {
                    Text("⭐", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 📃 Descripción
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 🔗 Link clickeable
            if (product.link.isNotEmpty()) {
                ClickableText(
                    text = AnnotatedString(product.link),
                    style = MaterialTheme.typography.bodySmall,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.link))
                        context.startActivity(intent)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🏷️ Estado del producto
            val estadoTexto = product.status.displayName()
            Text(text = estadoTexto, style = MaterialTheme.typography.bodySmall)
        }
    }
}