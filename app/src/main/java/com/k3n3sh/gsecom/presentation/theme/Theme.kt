package com.k3n3sh.gsecom.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Brand
val Charcoal = Color(0xFF212529)
val Sand = Color(0xFFE9DED8)

val SandLight = Color(0xFFF7F2EF)
val SandDark = Color(0xFFDDD1CA)
val WarmGrey = Color(0xFF5C544F)
val CharcoalLight = Color(0xFF2B3035)
val CharcoalLighter = Color(0xFF3C4146)
val Rust = Color(0xFF9A3B26)
val RustLight = Color(0xFFF0A38A)

// Sand with charcoal
private val LightColors: ColorScheme = lightColorScheme(
    primary = Charcoal,
    onPrimary = Sand,
    secondaryContainer = SandDark,
    onSecondaryContainer = Charcoal,
    tertiary = Rust,
    onTertiary = Color.White,
    background = Sand,
    onBackground = Charcoal,
    surface = Sand,
    onSurface = Charcoal,
    surfaceVariant = SandDark,
    onSurfaceVariant = WarmGrey,
    surfaceContainerLow = SandLight,
    surfaceContainer = SandLight,
    surfaceContainerHighest = SandDark,
    outline = WarmGrey,
    outlineVariant = SandDark,
)

// Charcoal with sand
private val DarkColors: ColorScheme = darkColorScheme(
    primary = Sand,
    onPrimary = Charcoal,
    secondaryContainer = CharcoalLighter,
    onSecondaryContainer = Sand,
    tertiary = RustLight,
    onTertiary = Charcoal,
    background = Charcoal,
    onBackground = Sand,
    surface = Charcoal,
    onSurface = Sand,
    surfaceVariant = CharcoalLighter,
    onSurfaceVariant = SandDark,
    surfaceContainerLow = CharcoalLight,
    surfaceContainer = CharcoalLight,
    surfaceContainerHighest = CharcoalLighter,
    outline = SandDark,
    outlineVariant = CharcoalLighter,
)

// Brand colours only, no dynamic colour
@Composable
fun GsEcomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
