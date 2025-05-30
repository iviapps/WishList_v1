package com.iviapps.wishlistbyivi.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iviapps.wishlistbyivi.model.Product
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProductCard(
    product: Product,
    showLink: Boolean = false,
    linkColor: Color
) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    // Estado para expandir/contraer el nombre
    var expandedName by remember { mutableStateOf(false) }
    var isNameOverflowing by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 🔠 Nombre + estrella
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium.copy(color = colors.onSurface),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    maxLines = if (expandedName) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { layout: TextLayoutResult ->
                        if (!expandedName && layout.hasVisualOverflow) {
                            isNameOverflowing = true
                        }
                    }
                )
                if (product.favorite) {
                    Text(
                        text = "⭐",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.primary
                    )
                }
            }

            // “Leer más…” / “Leer menos”
            if (isNameOverflowing) {
                Text(
                    text = if (expandedName) "Leer menos" else "Leer más...",
                    style = MaterialTheme.typography.bodySmall.copy(color = colors.primary),
                    modifier = Modifier
                        .clickable { expandedName = !expandedName }
                        .padding(top = 2.dp, bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 📃 Descripción
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurface
            )

            // 🏷️ Estado del producto
            product.status?.let {
                val estadoTexto = when (it.name) {
                    "WANT_IT"     -> "❤️ Lo necesito, lo quiero."
                    "IN_PROGRESS" -> "💭 ¿Lo consideramos? A espera de tu opinión."
                    "HAVE_IT"     -> "➕ Otros, pero es importante."
                    else          -> it.name
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Estado: $estadoTexto",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onSurface
                )
            }

            // 🔗 Enlace resumido
            if (showLink && product.link.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "🔗 Enlace aquí",
                    style = MaterialTheme.typography.bodyLarge,
                    color = linkColor,
                    modifier = Modifier.clickable {
                        val url = if (product.link.startsWith("http")) product.link
                        else "https://${product.link}"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                )
            }

            // 📅 Fecha
            product.dateAchieved?.let { ts ->
                val date = ts.toDate()
                val formatted = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Añadido el: $formatted",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onSurface
                )
            }
        }
    }
}
