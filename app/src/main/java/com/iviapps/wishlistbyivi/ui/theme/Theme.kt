package com.iviapps.wishlistbyivi.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

val LightPrimary = Color(0xFFFF00A1)
val LightSecondary = Color(0xFFFFBFE8)
val LightTertiary = Color(0xFF7D5260)

val DarkPrimary = Color(0xFFFF45BA)
val DarkSecondary = Color(0xFFFF479C)
val DarkTertiary = Color(0xFF393131)

val LightError = Color(0xFF78005A)
val DarkError = Color(0xFFB34A9E)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF00A1),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFE3F5),
    onPrimaryContainer = Color.White,

    secondary = Color(0xFF707070),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF996184),
    onSecondaryContainer = Color(0xFFEFD3D3),

    tertiary = Color(0xFFDEB9C3),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF3C2B2D),
    onTertiaryContainer = Color.White,

    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF7B5080), // rgba(100, 60, 96, 0.52)
    onSurface = Color(0xFFFCDDEF),

    error = Color(0xFFFF8FE9),
    onError = Color(0xFF000000),
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFF007B),
    onPrimaryContainer = Color.White,

    secondary = LightSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFAD6995),
    onSecondaryContainer = Color.White,

    tertiary = LightTertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFF729F),
    onTertiaryContainer = Color(0xFFFF7C99),

    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFBAF7),
    onSurface = Color(0xFF000000),

    error = LightError,
    onError = Color.White,
)

@Composable
fun WishListByIviTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
