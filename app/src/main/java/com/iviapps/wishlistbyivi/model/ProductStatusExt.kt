package com.iviapps.wishlistbyivi.model

fun ProductStatus.displayName(): String = when (this) {
    ProductStatus.HAVE_IT -> "➕ Otros, pero es importante."
    ProductStatus.IN_PROGRESS -> "💭¿Lo consideramos? A espera de tu opinión."
    ProductStatus.WANT_IT -> "❤️ Lo necesito, lo quiero."
}
