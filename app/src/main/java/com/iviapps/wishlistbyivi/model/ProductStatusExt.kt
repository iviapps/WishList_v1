package com.iviapps.wishlistbyivi.model

fun ProductStatus.displayName(): String = when (this) {
    ProductStatus.HAVE_IT -> "✅ Lo tengo"
    ProductStatus.IN_PROGRESS -> "⏳ En progreso"
    ProductStatus.WANT_IT -> "🛍️ Lo quiero"
}
