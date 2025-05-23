package com.iviapps.wishlistbyivi.model

import com.google.firebase.Timestamp

enum class ProductStatus {
    HAVE_IT,
    IN_PROGRESS,
    WANT_IT
}

data class Product(
    val id: String = "",
    val name: String = "",
    val link: String = "",
    val description: String = "",
    val status: ProductStatus = ProductStatus.WANT_IT,
    val favorite: Boolean = false,
    val dateAchieved: Timestamp? = null
)
