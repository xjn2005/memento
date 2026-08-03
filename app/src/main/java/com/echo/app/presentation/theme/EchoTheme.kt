package com.echo.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightScheme = lightColorScheme(
    primary = LakeBlue,
    onPrimary = Color.White,
    secondaryContainer = LakeBlueSoft,
    onSecondaryContainer = LakeBlue,
    background = AppCanvas,
    onBackground = Ink,
    surface = CardSurface,
    onSurface = Ink,
    surfaceVariant = LakeBlueSoft,
    onSurfaceVariant = MutedInk,
    outlineVariant = Hairline,
)
private val DarkScheme = darkColorScheme(
    primary = IceBlue,
    onPrimary = DeepOcean,
    secondaryContainer = Color(0xFF123C55),
    onSecondaryContainer = IceBlue,
    background = DeepOcean,
    onBackground = IceInk,
    surface = DeepOceanSurface,
    onSurface = IceInk,
    surfaceVariant = Color(0xFF153245),
    onSurfaceVariant = IceMutedInk,
    outlineVariant = DeepHairline,
)

private val EchoTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp,
        lineHeight = 41.sp,
        letterSpacing = (-0.3).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.2).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 27.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 26.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 25.sp,
    ),
)

@Composable
fun EchoTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = EchoTypography,
        content = content,
    )
}
