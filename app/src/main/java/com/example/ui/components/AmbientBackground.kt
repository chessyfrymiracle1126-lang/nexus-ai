package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple

@Composable
fun AmbientBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Base obsidian background
        drawRect(color = CyberBackground)

        // Top-left cyan ambient glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    NeonCyan.copy(alpha = 0.18f),
                    NeonCyan.copy(alpha = 0.06f),
                    Color.Transparent
                ),
                center = Offset(x = width * 0.15f, y = height * 0.10f),
                radius = width * 0.75f
            )
        )

        // Top-right purple ambient glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    NeonPurple.copy(alpha = 0.16f),
                    NeonPurple.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                center = Offset(x = width * 0.85f, y = height * 0.22f),
                radius = width * 0.80f
            )
        )

        // Subtle cybernetic grid lines
        val gridSpacing = 48f
        val gridColor = Color(0xFF162344).copy(alpha = 0.35f)
        val maxGridY = height * 0.65f

        var x = 0f
        while (x < width) {
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, maxGridY),
                strokeWidth = 1f
            )
            x += gridSpacing
        }

        var y = 0f
        while (y < maxGridY) {
            val alphaFade = (1f - (y / maxGridY)).coerceIn(0f, 1f)
            drawLine(
                color = gridColor.copy(alpha = 0.35f * alphaFade),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
            y += gridSpacing
        }
    }
}
