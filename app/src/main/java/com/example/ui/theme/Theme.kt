package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class BoostSemanticColors(
    val mpesaGreen: Color,
    val mpesaContainer: Color,
    val onMpesaContainer: Color,
    val waitContainer: Color,
    val onWaitContainer: Color
)

val LocalBoostColors = staticCompositionLocalOf {
    BoostSemanticColors(
        mpesaGreen = MpesaEmerald,
        mpesaContainer = MpesaContainerLight,
        onMpesaContainer = MpesaOnContainerLight,
        waitContainer = WaitAmberBgLight,
        onWaitContainer = WaitAmberTextLight
    )
}

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = IndigoContainerLight,
    onPrimaryContainer = IndigoPrimary,
    secondary = MpesaEmerald,
    onSecondary = Color.White,
    secondaryContainer = MpesaContainerLight,
    onSecondaryContainer = MpesaOnContainerLight,
    background = SlateBgLight,
    onBackground = SlateTextLight,
    surface = SlateSurfaceLight,
    onSurface = SlateTextLight,
    surfaceVariant = SlateSurfaceVariantLight,
    onSurfaceVariant = SlateMutedLight,
    outline = SlateOutlineLight,
    error = ErrorRedLight,
    onError = Color.White,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight
)

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimaryDark,
    onPrimary = SlateBgDark,
    primaryContainer = IndigoContainerDark,
    onPrimaryContainer = IndigoContainerLight,
    secondary = MpesaEmeraldBright,
    onSecondary = SlateBgDark,
    secondaryContainer = MpesaContainerDark,
    onSecondaryContainer = MpesaOnContainerDark,
    background = SlateBgDark,
    onBackground = SlateTextDark,
    surface = SlateSurfaceDark,
    onSurface = SlateTextDark,
    surfaceVariant = SlateSurfaceVariantDark,
    onSurfaceVariant = SlateMutedDark,
    outline = SlateOutlineDark,
    error = ErrorRedDark,
    onError = SlateBgDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark
)

val BoostShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(26.dp)
)

@Composable
fun BoostKETheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val semanticColors = if (darkTheme) {
        BoostSemanticColors(
            mpesaGreen = MpesaEmeraldBright,
            mpesaContainer = MpesaContainerDark,
            onMpesaContainer = MpesaOnContainerDark,
            waitContainer = WaitAmberBgDark,
            onWaitContainer = WaitAmberTextDark
        )
    } else {
        BoostSemanticColors(
            mpesaGreen = MpesaEmerald,
            mpesaContainer = MpesaContainerLight,
            onMpesaContainer = MpesaOnContainerLight,
            waitContainer = WaitAmberBgLight,
            onWaitContainer = WaitAmberTextLight
        )
    }

    CompositionLocalProvider(LocalBoostColors provides semanticColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = BoostTypography,
            shapes = BoostShapes,
            content = content
        )
    }
}
