package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.model.GenerationUiState
import com.example.ui.components.AmbientBackground
import com.example.ui.components.AppHeader
import com.example.ui.components.FloatingToast
import com.example.ui.components.InputSection
import com.example.ui.components.OutputSection
import com.example.ui.components.QuickPromptChips
import com.example.ui.components.SettingsDialog
import com.example.ui.viewmodel.NexusViewModel

@Composable
fun NexusMainScreen(
    viewModel: NexusViewModel,
    onSpeak: (String) -> Unit,
    onStopSpeak: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prompt by viewModel.prompt.collectAsState()
    val selectedMode by viewModel.selectedMode.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()

    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {
        // Atmospheric Ambient Background with Cyber Glows
        AmbientBackground()

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Header Section
                    AppHeader(
                        onSettingsClick = { viewModel.openSettings() }
                    )

                    // Quick-Prompt Suggestion Chips
                    QuickPromptChips(
                        prompts = viewModel.quickPrompts,
                        onPromptSelected = { promptItem ->
                            viewModel.onSelectQuickPrompt(promptItem)
                        }
                    )

                    // Main Input Section
                    InputSection(
                        prompt = prompt,
                        onPromptChange = { viewModel.onPromptChange(it) },
                        onClearPrompt = { viewModel.onClearPrompt() },
                        selectedMode = selectedMode,
                        onModeSelect = { viewModel.onSelectMode(it) },
                        onGenerate = { viewModel.onGenerate() },
                        isLoading = uiState is GenerationUiState.Loading
                    )

                    // Output Display Section
                    OutputSection(
                        uiState = uiState,
                        isSpeaking = isSpeaking,
                        onSpeakClick = { text -> onSpeak(text) },
                        onStopSpeak = { onStopSpeak() },
                        onRegenerate = { viewModel.onRegenerate() },
                        onCopyToast = { msg -> viewModel.showToast(msg) }
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Floating Feedback Toast
                FloatingToast(
                    message = toastMessage,
                    onDismiss = { viewModel.clearToast() },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }

        // Settings Dialog Modal
        if (isSettingsOpen) {
            SettingsDialog(
                currentSettings = settings,
                onDismiss = { viewModel.closeSettings() },
                onSave = { updatedSettings -> viewModel.saveSettings(updatedSettings) },
                onReset = { viewModel.resetSettings() }
            )
        }
    }
}
