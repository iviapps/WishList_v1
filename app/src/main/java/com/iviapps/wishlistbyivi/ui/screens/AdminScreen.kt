package com.iviapps.wishlistbyivi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iviapps.wishlistbyivi.model.Product
import com.iviapps.wishlistbyivi.model.ProductStatus
import com.iviapps.wishlistbyivi.model.displayName
import com.iviapps.wishlistbyivi.services.EmailClient
import com.iviapps.wishlistbyivi.services.EmailData
import com.iviapps.wishlistbyivi.services.EmailUser
import com.iviapps.wishlistbyivi.ui.components.StatusDropdown
import kotlinx.coroutines.launch
import retrofit2.Call

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(ProductStatus.WANT_IT) }
    var isFavorite by remember { mutableStateOf(false) }
    var sortByRecent by remember { mutableStateOf(false) } // predeterminado: más antiguos
    var onlyFavorites by remember { mutableStateOf(false) }
    var filterStatus by remember { mutableStateOf<ProductStatus?>(null) }
    var isStatusDropdownExpanded by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var editingProductId by remember { mutableStateOf<String?>(null) }
    var products by remember { mutableStateOf(listOf<Product>()) }

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
                    products = sorted
                }
            }
    }

    val filteredProducts = products.filter {
        (!onlyFavorites || it.favorite) &&
                (filterStatus == null || it.status == filterStatus)
    }

    fun sendBrevoEmail(productName: String, productLink: String) {
        val email = EmailData(
            sender = EmailUser("iviapps.content@gmail.com", "WishList By Ivi"),
            to = listOf(
                EmailUser("Albertopintanel99@gmail.com", "Cliente"),
                EmailUser("iviapps.content@gmail.com", "Tester")
            ),
            subject = "Nuevo producto añadido",
            htmlContent = """
                <h2>🎉 ¡Nuevo producto añadido!</h2>
                <h3><strong>$productName</strong></h3>
                <p><a href=\"$productLink\">Ver producto</a></p>
            """.trimIndent()
        )
        val call = EmailClient.service.sendEmail(email)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                Toast.makeText(context, if (response.isSuccessful) "Correo enviado" else "Error ${response.code()}", Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    val clearForm = {
        name = ""
        description = ""
        link = ""
        isFavorite = false
        selectedStatus = ProductStatus.WANT_IT
        editingProductId = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Admin: Añadir producto", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = link,
                    onValueChange = { link = it },
                    label = { Text("Link") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (máx. 1000 caracteres)") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    maxLines = 6
                )
                if (description.length > 1000) {
                    Text("La descripción no puede superar los 1000 caracteres", color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Estado del nuevo producto")
                StatusDropdown(selectedStatus) { selectedStatus = it }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Filtros para la lista")

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = sortByRecent,
                        onCheckedChange = { sortByRecent = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary)
                    )
                    Text("Más recientes", modifier = Modifier.padding(start = 8.dp))

                    Spacer(modifier = Modifier.width(16.dp))

                    Switch(
                        checked = onlyFavorites,
                        onCheckedChange = { onlyFavorites = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary)
                    )
                    Text("Solo favoritos", modifier = Modifier.padding(start = 8.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = isStatusDropdownExpanded,
                    onExpandedChange = { isStatusDropdownExpanded = !isStatusDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = filterStatus?.displayName() ?: "Filtrar por estado",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filtrar por estado") },
                        trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isStatusDropdownExpanded,
                        onDismissRequest = { isStatusDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todos") },
                            onClick = {
                                filterStatus = null
                                isStatusDropdownExpanded = false
                            }
                        )
                        ProductStatus.values().forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.displayName()) },
                                onClick = {
                                    filterStatus = status
                                    isStatusDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank() && link.isNotBlank() && description.isNotBlank()) {
                            isLoading = true
                            val productData = hashMapOf(
                                "name" to name,
                                "link" to link,
                                "description" to description,
                                "status" to selectedStatus.name,
                                "favorite" to isFavorite,
                                "dateAchieved" to Timestamp.now()
                            )
                            val docRef = editingProductId?.let { db.collection("products").document(it) }
                                ?: db.collection("products").document()
                            productData["id"] = docRef.id

                            docRef.set(productData)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Producto guardado", Toast.LENGTH_SHORT).show()
                                    sendBrevoEmail(name, link)
                                    clearForm()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                                }
                                .addOnCompleteListener {
                                    isLoading = false
                                }
                        } else {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isLoading) "Guardando..." else "Guardar producto")
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            items(filteredProducts) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable {
                            name = item.name
                            link = item.link
                            description = item.description
                            selectedStatus = item.status
                            isFavorite = item.favorite
                            editingProductId = item.id
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.name, style = MaterialTheme.typography.titleMedium)
                            if (item.favorite) Text("⭐", color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(item.description, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Estado: ${item.status.displayName()}", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("🔗 ${item.link}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = {
                            db.collection("products").document(item.id).delete()
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show()
                                    if (editingProductId == item.id) clearForm()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error al eliminar: ${it.message}", Toast.LENGTH_LONG).show()
                                }
                        }) {
                            Text("🗑 Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}