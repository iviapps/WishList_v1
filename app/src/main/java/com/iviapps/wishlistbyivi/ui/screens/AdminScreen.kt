package com.iviapps.wishlistbyivi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.iviapps.wishlistbyivi.ui.components.ProductCard
import com.iviapps.wishlistbyivi.ui.components.StatusDropdown
import retrofit2.Call

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(ProductStatus.WANT_IT) }
    var isFavorite by remember { mutableStateOf(false) }
    var editingProductId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var showEditDialog by remember { mutableStateOf(false) }
    var editProduct by remember { mutableStateOf<Product?>(null) }
    var editName by remember { mutableStateOf("") }
    var editLink by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }
    var editStatus by remember { mutableStateOf(ProductStatus.WANT_IT) }
    var editFavorite by remember { mutableStateOf(false) }
    var isDialogLoading by remember { mutableStateOf(false) }

    var products by remember { mutableStateOf(listOf<Product>()) }
    var sortByRecent by remember { mutableStateOf(false) }
    var onlyFavorites by remember { mutableStateOf(false) }
    var filterStatus by remember { mutableStateOf<ProductStatus?>(null) }
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(sortByRecent) {
        db.collection("products").addSnapshotListener { snap, err ->
            if (err == null && snap != null) {
                val list = snap.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }
                products = if (sortByRecent)
                    list.sortedByDescending { it.dateAchieved?.seconds ?: 0L }
                else
                    list.sortedBy { it.dateAchieved?.seconds ?: 0L }
            }
        }
    }

    val filtered = products.filter { p ->
        val matchesText = p.name.contains(searchText, ignoreCase = true) ||
                p.description.contains(searchText, ignoreCase = true)
        val matchesFav = !onlyFavorites || p.favorite
        val matchesStat = filterStatus == null || p.status == filterStatus
        matchesText && matchesFav && matchesStat
    }

    fun clearForm() {
        name = ""; link = ""; description = ""
        selectedStatus = ProductStatus.WANT_IT
        isFavorite = false
        editingProductId = null
    }

    fun sendEmailNew() {
        val content = """
            <h2 style="color: #EB009A;">🎉 ¡Nuevo producto añadido!</h2>
            <h3 style="color: #412B39;"><strong>$name</strong></h3>
            <p style="color: #332B30;"><strong>Descripción:</strong> $description</p>
            <p style="color: #E6009E;"><strong>Estado:</strong> ${selectedStatus.displayName()}</p>
            <p style="color: #963273;"><strong>Favorito:</strong> ${if (isFavorite) "Sí" else "No"}</p>
            <p style="color: #99006B;"><a href=\"$link\">🔗 Ver producto</a></p>
        """.trimIndent()
        val email = EmailData(
            sender = EmailUser("tester@correo.com", "WishList By Ivi"),
            to = listOf(
                EmailUser("cliente@gmail.com", "Cliente"),
                EmailUser("tester@correo.com", "Admin")
            ),
            subject = "Nuevo producto añadido",
            htmlContent = content
        )
        EmailClient.service.sendEmail(email).enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                Toast.makeText(context, if (response.isSuccessful) "Correo enviado" else "Error ${response.code()}", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    if (showEditDialog && editProduct != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar producto") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it.take(200) },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editLink,
                        onValueChange = { editLink = it.take(1000) },
                        label = { Text("Link") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it.take(1000) },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                    if (editDescription.length >= 1000) {
                        Text("La descripción no puede superar los 1000 caracteres", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(Modifier.height(8.dp))
                    StatusDropdown(selectedStatus = editStatus, onStatusSelected = { editStatus = it!! })
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = editFavorite, onCheckedChange = { editFavorite = it })
                        Text("Favorito ⭐", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val prod = editProduct
                    if (prod == null || prod.id.isBlank()) {
                        Toast.makeText(context, "Producto inválido", Toast.LENGTH_SHORT).show()
                        showEditDialog = false
                        return@TextButton
                    }
                    val upd = mapOf<String, Any>(
                        "name" to editName,
                        "link" to editLink,
                        "description" to editDescription,
                        "status" to editStatus.name,
                        "favorite" to editFavorite
                    )
                    try {
                        isDialogLoading = true
                        db.collection("products")
                            .document(prod.id)
                            .update(upd)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Producto actualizado", Toast.LENGTH_SHORT).show()
                                showEditDialog = false
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Error actualizando: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                            .addOnCompleteListener { isDialogLoading = false }
                    } catch (e: Exception) {
                        isDialogLoading = false
                        Toast.makeText(context, "Error inesperado: ${e.message}", Toast.LENGTH_LONG).show()
                        showEditDialog = false
                    }
                }) {
                    Text(if (isDialogLoading) "Guardando..." else "Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin: Crear/Editar producto", color = MaterialTheme.colorScheme.onBackground) },
                actions = {
                    TextButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                        onLogout()
                    }) {
                        Text("Cerrar sesión")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = link,
                    onValueChange = { link = it },
                    label = { Text("Link") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (máx.1000 caracteres)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
                if (description.length > 1000) {
                    Text("La descripción no puede superar los 1000 caracteres", color = MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(8.dp))
                StatusDropdown(selectedStatus = selectedStatus, onStatusSelected = { selectedStatus = it!! })
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = isFavorite, onCheckedChange = { isFavorite = it })
                    Text("Crear favorito ⭐", modifier = Modifier.padding(start = 8.dp))
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { clearForm() }) {
                        Text("Limpiar formulario")
                    }
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (name.isBlank() || link.isBlank() || description.isBlank()) {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        val data = hashMapOf(
                            "name" to name,
                            "link" to link,
                            "description" to description,
                            "status" to selectedStatus.name,
                            "favorite" to isFavorite,
                            "dateAchieved" to Timestamp.now()
                        )
                        val docRef = editingProductId
                            ?.let { db.collection("products").document(it) }
                            ?: db.collection("products").document().also { data["id"] = it.id }

                        docRef.set(data)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Producto guardado", Toast.LENGTH_SHORT).show()
                                sendEmailNew()
                                clearForm()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                            }
                            .addOnCompleteListener { isLoading = false }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isLoading) "Guardando..." else "Guardar producto")
                }

                Spacer(Modifier.height(16.dp))
                Text("🔎 FILTRAR PRODUCTOS")
                StatusDropdown(selectedStatus = filterStatus, onStatusSelected = { filterStatus = it }, includeAllOption = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Buscar por nombre o descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = sortByRecent, onCheckedChange = { sortByRecent = it })
                    Text("Más recientes", modifier = Modifier.padding(start = 8.dp))
                    Spacer(Modifier.width(16.dp))
                    Switch(checked = onlyFavorites, onCheckedChange = { onlyFavorites = it })
                    Text("Solo favoritos", modifier = Modifier.padding(start = 8.dp))
                }
            }
            items(filtered) { prod ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        ProductCard(product = prod, showLink = true, linkColor = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = {
                                editProduct = prod
                                editName = prod.name
                                editLink = prod.link
                                editDescription = prod.description
                                editStatus = prod.status
                                editFavorite = prod.favorite
                                showEditDialog = true
                            }) {
                                Text("✏️ Editar")
                            }
                            TextButton(onClick = {
                                db.collection("products").document(prod.id).delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show()
                                        if (editingProductId == prod.id) clearForm()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
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
}
