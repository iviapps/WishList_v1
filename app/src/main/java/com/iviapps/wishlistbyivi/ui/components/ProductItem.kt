package com.iviapps.wishlistbyivi.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.iviapps.wishlistbyivi.model.Product
import com.iviapps.wishlistbyivi.model.ProductStatus
import com.iviapps.wishlistbyivi.model.displayName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItem(product: Product) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onSurface
                )
                if (product.favorite) {
                    Text(
                        text = "⭐",
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (product.link.isNotEmpty()) {
                ClickableText(
                    text = AnnotatedString(product.link),
                    style = MaterialTheme.typography.bodySmall.copy(color = colors.primary),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.link))
                        context.startActivity(intent)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Estado: ${product.status.displayName()}",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            product.dateAchieved?.let { timestamp ->
                val date = timestamp.toDate()
                val formattedDate = remember(date) {
                    android.text.format.DateFormat.format("dd/MM/yyyy HH:mm", date).toString()
                }
                Text(
                    text = "Conseguido: $formattedDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurface
                )
            }
        }
    }
}
