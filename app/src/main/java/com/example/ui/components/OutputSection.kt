package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GenerationUiState
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCard
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OutputSection(
    uiState: GenerationUiState,
    isSpeaking: Boolean,
    onSpeakClick: (String) -> Unit,
    onStopSpeak: () -> Unit,
    onRegenerate: () -> Unit,
    onCopyToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Output Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "OUTPUT RESPONSE",
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            if (uiState is GenerationUiState.Success) {
                val sec = String.format("%.1f", uiState.durationMs / 1000.0)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF14203D))
                        .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Generated in ${sec}s",
                        color = NeonCyan,
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Main Output Card Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(CyberCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(18.dp))
                .padding(16.dp)
                .testTag("output_card")
        ) {
            when (uiState) {
                is GenerationUiState.Idle -> {
                    EmptyStateView()
                }

                is GenerationUiState.Loading -> {
                    GeneratingStateView()
                }

                is GenerationUiState.Error -> {
                    ErrorStateView(
                        message = uiState.message,
                        onRetry = onRegenerate
                    )
                }

                is GenerationUiState.Success -> {
                    FilledResponseView(
                        state = uiState,
                        isSpeaking = isSpeaking,
                        isCopied = isCopied,
                        onSpeakClick = {
                            if (isSpeaking) {
                                onStopSpeak()
                            } else {
                                onSpeakClick(uiState.response)
                            }
                        },
                        onCopyClick = {
                            clipboardManager.setText(AnnotatedString(uiState.response))
                            isCopied = true
                            onCopyToast("Response copied to clipboard")
                            scope.launch {
                                delay(2000)
                                isCopied = false
                            }
                        },
                        onRegenerate = onRegenerate,
                        onCopyToast = onCopyToast
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateView() {
    val infiniteTransition = rememberInfiniteTransition(label = "emptyOrb")
    val orbPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbPulse"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Glowing Orb Icon
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(68.dp)
                .scale(orbPulse)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            NeonCyan.copy(alpha = 0.25f),
                            NeonPurple.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
                .border(1.5.dp, NeonCyan.copy(alpha = 0.4f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Outlined.SmartToy,
                contentDescription = "Neural Engine",
                tint = NeonCyan,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Neural Engine Idle",
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Select a quick prompt chip above or type your prompt to generate intelligent responses with real-time streaming.",
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun GeneratingStateView() {
    val infiniteTransition = rememberInfiniteTransition(label = "generating")
    val avatarScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatarScale"
    )

    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // AI Avatar Row with pulsating status
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .scale(avatarScale)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.25f))
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F1B3B))
                        .border(1.dp, NeonCyan, CircleShape)
                ) {
                    Text(
                        text = "AI",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "Synthesizing intelligence...",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Compiling neural pathways & context tensors",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Shimmer Skeleton Lines
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(NeonCyan.copy(alpha = shimmerAlpha * 0.25f))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(NeonPurple.copy(alpha = shimmerAlpha * 0.25f))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(NeonCyan.copy(alpha = shimmerAlpha * 0.20f))
            )
        }
    }
}

@Composable
private fun FilledResponseView(
    state: GenerationUiState.Success,
    isSpeaking: Boolean,
    isCopied: Boolean,
    onSpeakClick: () -> Unit,
    onCopyClick: () -> Unit,
    onRegenerate: () -> Unit,
    onCopyToast: (String) -> Unit
) {
    val wordCount = remember(state.response) {
        state.response.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
    }
    val charCount = state.response.length

    Column(modifier = Modifier.fillMaxWidth()) {
        // Response Header: AI Identity + Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Identity
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F1B3B))
                        .border(1.dp, NeonCyan, CircleShape)
                ) {
                    Text(
                        text = "AI",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "Nexus Intelligence",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = state.mode.badgeLabel,
                        color = when (state.mode) {
                            com.example.data.model.AiMode.CREATIVE -> NeonPurple
                            com.example.data.model.AiMode.BALANCED -> NeonCyan
                            com.example.data.model.AiMode.PRECISE -> NeonGreen
                        },
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Action Buttons: Speak & Copy
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speak Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSpeaking) NeonCyan.copy(alpha = 0.2f) else Color(0xFF14203D))
                        .border(
                            1.dp,
                            if (isSpeaking) NeonCyan else Color(0xFF1E2D54),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable(onClick = onSpeakClick)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("speak_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = if (isSpeaking) "Stop Speech" else "Read Aloud",
                            tint = if (isSpeaking) NeonCyan else TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSpeaking) "Stop" else "Speak",
                            color = if (isSpeaking) NeonCyan else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Copy Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isCopied) NeonGreen.copy(alpha = 0.2f) else Color(0xFF14203D))
                        .border(
                            1.dp,
                            if (isCopied) NeonGreen else Color(0xFF1E2D54),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable(onClick = onCopyClick)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("copy_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "Copy response",
                            tint = if (isCopied) NeonGreen else TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isCopied) "Copied" else "Copy",
                            color = if (isCopied) NeonGreen else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Response Body (Markdown formatted)
        FormattedResponseView(
            content = state.response,
            onCopyCode = onCopyToast
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Response Footer: Word stats and Regenerate button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$wordCount words • $charCount chars",
                color = TextMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF14203D))
                    .border(1.dp, Color(0xFF1E2D54), RoundedCornerShape(8.dp))
                    .clickable(onClick = onRegenerate)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("regenerate_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Regenerate",
                        tint = NeonCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Regenerate",
                        color = NeonCyan,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorStateView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⚠️ Generation Alert",
            color = NeonAmber,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF22162B))
                .border(1.dp, NeonPurple, RoundedCornerShape(8.dp))
                .clickable(onClick = onRetry)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Retry",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
