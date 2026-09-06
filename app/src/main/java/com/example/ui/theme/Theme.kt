package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CyberColorScheme = darkColorScheme(
  primary = NeonCyan,
  onPrimary = CyberBackground,
  primaryContainer = CyberCardElevated,
  onPrimaryContainer = NeonCyan,
  secondary = NeonPurple,
  onSecondary = CyberBackground,
  secondaryContainer = CyberCardElevated,
  onSecondaryContainer = NeonPurple,
  tertiary = NeonBlue,
  onTertiary = CyberBackground,
  background = CyberBackground,
  onBackground = TextPrimary,
  surface = CyberSurface,
  onSurface = TextPrimary,
  surfaceVariant = CyberCard,
  onSurfaceVariant = TextSecondary,
  outline = CyberBorder,
  outlineVariant = CyberBorderLight
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = CyberColorScheme,
    typography = Typography,
    content = content
  )
}

