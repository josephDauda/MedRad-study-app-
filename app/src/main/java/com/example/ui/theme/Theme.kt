package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary = AcademicNavy,
  onPrimary = Color.White,
  primaryContainer = ProfessionalBlueLight,
  onPrimaryContainer = AcademicNavy,
  secondary = ProfessionalBlue,
  onSecondary = Color.White,
  secondaryContainer = ProfessionalBlueLight,
  onSecondaryContainer = AcademicNavyDark,
  tertiary = MedicalTeal,
  onTertiary = Color.White,
  tertiaryContainer = MedicalTealLight,
  onTertiaryContainer = AcademicNavyDark,
  background = BackgroundCoolLight,
  onBackground = TextPrimaryDarkSlate,
  surface = SurfaceWhite,
  onSurface = TextPrimaryDarkSlate,
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = TextSecondarySlate,
  outline = BorderSubtle,
  outlineVariant = Color(0xFFCBD5E1),
  error = ErrorRed,
  onError = Color.White,
  errorContainer = ErrorRedLight,
  onErrorContainer = Color(0xFF7F1D1D)
)

private val DarkColorScheme = darkColorScheme(
  primary = Color(0xFF90CAF9),
  onPrimary = AcademicNavyDark,
  primaryContainer = AcademicNavyLight,
  onPrimaryContainer = Color(0xFFE2E8F0),
  secondary = Color(0xFF64B5F6),
  onSecondary = AcademicNavyDark,
  secondaryContainer = Color(0xFF1E3A5F),
  onSecondaryContainer = Color(0xFFE0EBF7),
  tertiary = Color(0xFF2DD4BF),
  onTertiary = Color(0xFF042F2E),
  background = Color(0xFF0B1320),
  onBackground = Color(0xFFF1F5F9),
  surface = Color(0xFF131F33),
  onSurface = Color(0xFFF8FAFC),
  surfaceVariant = Color(0xFF1E2D44),
  onSurfaceVariant = Color(0xFF94A3B8),
  outline = Color(0xFF334155),
  outlineVariant = Color(0xFF1E293B),
  error = Color(0xFFF87171),
  onError = Color(0xFF450A0A)
)

@Composable
fun MedradTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
