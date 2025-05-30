package com.iviapps.wishlistbyivi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment                // <— aquí
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iviapps.wishlistbyivi.model.Product
import com.iviapps.wishlistbyivi.model.ProductStatus
import com.iviapps.wishlistbyivi.ui.components.ProductCard
import com.iviapps.wishlistbyivi.ui.components.StatusDropdown
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onLogout: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var products by remember { mutableStateOf(listOf<Product>()) }
    var previousList by remember { mutableStateOf(listOf<Product>()) }

    var searchText by remember { mutableStateOf("") }
    var onlyFavorites by remember { mutableStateOf(false) }
    var sortByRecent by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf<ProductStatus?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Escucha y ordena
    LaunchedEffect(sortByRecent) {
        db.collection("products")
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject(Product::class.java) }
                    val sorted = if (sortByRecent) {
                        list.sortedByDescending { it.dateAchieved?.seconds ?: 0L }
                    } else {
                        list.sortedBy { it.dateAchieved?.seconds ?: 0L }
                    }

                    // Notificar nuevos
                    if (previousList.isNotEmpty() && sorted.size > previousList.size) {
                        val newProduct = sorted
                            .filterNot { old -> previousList.any { it.id == old.id } }
                            .maxByOrNull { it.dateAchieved ?: com.google.firebase.Timestamp(0,0) }
                        newProduct?.let {
                            val msg = "🎉 Nuevo: ${it.name}\n🔗 ${it.link}"
                            scope.launch {
                                snackbarHostState.showSnackbar(msg)
                            }
                        }
                    }
                    previousList = products
                    products = sorted
                }
            }
    }

    // Filtrado
    val filteredProducts = products.filter { p ->
        val term = searchText.lowercase()
        val matchesText = p.name.lowercase().contains(term) ||
                p.description.lowercase().contains(term)
        val matchesFav = !onlyFavorites || p.favorite
        val matchesStatus = selectedStatus == null || p.status == selectedStatus
        matchesText && matchesFav && matchesStatus
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de deseos", color = MaterialTheme.colorScheme.onBackground) },
                actions = {
                    TextButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                        onLogout()
                    }) {
                        Text("Cerrar sesión", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = {
                    Text("Buscar producto...", color = MaterialTheme.colorScheme.onSurface)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    cursorColor          = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(Modifier.height(8.dp))

            StatusDropdown(
                selectedStatus = selectedStatus,
                onStatusSelected = { selectedStatus = it },
                includeAllOption = true
            )

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = onlyFavorites,
                    onCheckedChange = { onlyFavorites = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary)
                )
                Text("Solo favoritos", modifier = Modifier.padding(start = 8.dp))

                Spacer(Modifier.width(16.dp))

                Switch(
                    checked = sortByRecent,
                    onCheckedChange = { sortByRecent = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary)
                )
                Text("Más recientes", modifier = Modifier.padding(start = 8.dp))
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredProducts) { item ->
                    ProductCard(
                        product  = item,
                        showLink = true,
                        linkColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
