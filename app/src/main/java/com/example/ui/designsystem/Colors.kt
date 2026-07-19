package com.example.ui.designsystem

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Cosmic Indigo Brand Palette
val PrimaryIndigo = Color(0xFF6366F1) // Modern Indigo Accent
val PrimaryLight = Color(0xFF4F46E5)
val SecondaryViolet = Color(0xFF8B5CF6) // Royal Violet Accent
val TertiaryTeal = Color(0xFF14B8A6) // Smart Neural Activation Teal

// Slate Neutral Palettes
val Slate50 = Color(0xFFF8FAFC)
val Slate100 = Color(0xFFF1F5F9)
val Slate200 = Color(0xFFE2E8F0)
val Slate300 = Color(0xFFCBD5E1)
val Slate700 = Color(0xFF334155)
val Slate800 = Color(0xFF1E293B)
val Slate900 = Color(0xFF0F172A)
val Slate950 = Color(0xFF0B0F19) // Ultra Deep Space

// Functional Status Colors
val LiveGreen = Color(0xFF10B981)
val AlertAmber = Color(0xFFF59E0B)
val DestructiveRed = Color(0xFFEF4444)

// Dark / Space Color Scheme
val DarkColorScheme = darkColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = SecondaryViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = Color(0xFFF5F3FF),
    tertiary = TertiaryTeal,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF115E59),
    onTertiaryContainer = Color(0xFFCCFBF1),
    background = Slate950,
    onBackground = Color(0xFFF1F5F9),
    surface = Slate900,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Slate800,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Slate700,
    outlineVariant = Slate800,
    error = DestructiveRed,
    onError = Color.White
)

// AMOLED True Black Color Scheme
val AmoledColorScheme = darkColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E1B4B),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = SecondaryViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2E1065),
    onSecondaryContainer = Color(0xFFF5F3FF),
    tertiary = TertiaryTeal,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF115E59),
    onTertiaryContainer = Color(0xFFCCFBF1),
    background = Color.Black,
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF111111),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E1E1E),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Slate800,
    outlineVariant = Color(0xFF2C2C2C),
    error = DestructiveRed,
    onError = Color.White
)

// Light / Alabaster Color Scheme
val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF312E81),
    secondary = SecondaryViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF5F3FF),
    onSecondaryContainer = Color(0xFF4C1D95),
    tertiary = TertiaryTeal,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFCCFBF1),
    onTertiaryContainer = Color(0xFF115E59),
    background = Slate50,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate700,
    outline = Slate300,
    outlineVariant = Slate200,
    error = DestructiveRed,
    onError = Color.White
)
