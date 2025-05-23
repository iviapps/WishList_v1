package com.iviapps.wishlistbyivi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iviapps.wishlistbyivi.model.Product
import com.iviapps.wishlistbyivi.model.ProductStatus
import com.iviapps.wishlistbyivi.model.displayName
import com.iviapps.wishlistbyivi.ui.components.ProductCard
import kotlinx.coroutines.launch

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
    var statusDropdownExpanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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

                    if (sorted.size > previousList.size && previousList.isNotEmpty()) {
                        val newProduct = sorted
                            .filterNot { old -> previousList.any { it.id == old.id } }
                            .maxByOrNull { it.dateAchieved ?: com.google.firebase.Timestamp(0, 0) }

                        newProduct?.let {
                            val message = "\uD83C\uDF89 Nuevo: ${it.name}\n\uD83D\uDD17 ${it.link}"
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    }

                    previousList = products
                    products = sorted
                }
            }
    }

    val filteredProducts = products.filter {
        it.name.contains(searchText, ignoreCase = true) &&
                (!onlyFavorites || it.favorite) &&
                (selectedStatus == null || it.status == selectedStatus)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Lista de deseos",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    TextButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                        onLogout()
                    }) {
                        Text("Cerrar sesión", color = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = {
                    Text("Buscar producto...", color = MaterialTheme.colorScheme.onSurface)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Switch(
                    checked = onlyFavorites,
                    onCheckedChange = { onlyFavorites = it },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text("Solo favoritos", modifier = Modifier.padding(start = 8.dp), color = MaterialTheme.colorScheme.onBackground)

                Spacer(modifier = Modifier.width(16.dp))

                Switch(
                    checked = sortByRecent,
                    onCheckedChange = { sortByRecent = it },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text("Más recientes", modifier = Modifier.padding(start = 8.dp), color = MaterialTheme.colorScheme.onBackground)
            }

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = statusDropdownExpanded,
                onExpandedChange = { statusDropdownExpanded = !statusDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedStatus?.displayName() ?: "Seleccionar estado",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filtrar por estado") },
                    trailingIcon = {
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown"
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = statusDropdownExpanded,
                    onDismissRequest = { statusDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todos") },
                        onClick = {
                            selectedStatus = null
                            statusDropdownExpanded = false
                        }
                    )
                    ProductStatus.values().forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.displayName()) },
                            onClick = {
                                selectedStatus = status
                                statusDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredProducts) { product ->
                    ProductCard(product = product, linkColor = MaterialTheme.colorScheme.onTertiaryContainer)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}