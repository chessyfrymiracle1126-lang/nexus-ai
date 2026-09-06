package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AiMode
import com.example.data.model.AiSettings
import com.example.data.model.GenerationUiState
import com.example.data.model.QuickPrompt
import com.example.data.preferences.SettingsManager
import com.example.data.service.AiService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NexusViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsManager = SettingsManager(application)
    private val aiService = AiService()

    private val _prompt = MutableStateFlow("")
    val prompt: StateFlow<String> = _prompt.asStateFlow()

    private val _selectedMode = MutableStateFlow(AiMode.CREATIVE)
    val selectedMode: StateFlow<AiMode> = _selectedMode.asStateFlow()

    private val _uiState = MutableStateFlow<GenerationUiState>(GenerationUiState.Idle)
    val uiState: StateFlow<GenerationUiState> = _uiState.asStateFlow()

    private val _settings = MutableStateFlow(settingsManager.getSettings())
    val settings: StateFlow<AiSettings> = _settings.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private var currentGenerationJob: Job? = null

    val quickPrompts = listOf(
        QuickPrompt(
            icon = "📝",
            title = "Summarize",
            prompt = "Summarize the core principles of quantum computing in 3 clear bullet points."
        ),
        QuickPrompt(
            icon = "💡",
            title = "Brainstorm Ideas",
            prompt = "Brainstorm 5 innovative startup ideas combining artificial intelligence and clean renewable energy."
        ),
        QuickPrompt(
            icon = "💻",
            title = "Write Code",
            prompt = "Write a clean, responsive CSS card component with glowing neon hover borders and glassmorphism."
        ),
        QuickPrompt(
            icon = "🔬",
            title = "Explain Simply",
            prompt = "Explain how neural network transformers work as if I were a 12-year-old curious gamer."
        ),
        QuickPrompt(
            icon = "✉️",
            title = "Draft Email",
            prompt = "Draft a polite and persuasive email requesting project feedback from a senior executive."
        ),
        QuickPrompt(
            icon = "🐛",
            title = "Debug Code",
            prompt = "Debug this JavaScript async/await issue where race condition causes state mismatch."
        )
    )

    fun onPromptChange(newText: String) {
        if (newText.length <= 2000) {
            _prompt.value = newText
        }
    }

    fun onClearPrompt() {
        _prompt.value = ""
    }

    fun onSelectMode(mode: AiMode) {
        _selectedMode.value = mode
    }

    fun onSelectQuickPrompt(item: QuickPrompt) {
        _prompt.value = item.prompt
        generate(item.prompt, _selectedMode.value)
    }

    fun onGenerate() {
        val current = _prompt.value.trim()
        if (current.isNotBlank()) {
            generate(current, _selectedMode.value)
        } else {
            showToast("Please enter a prompt or select a quick chip")
        }
    }

    fun onRegenerate() {
        val lastPrompt = when (val state = _uiState.value) {
            is GenerationUiState.Success -> state.prompt
            else -> _prompt.value.trim()
        }
        if (lastPrompt.isNotBlank()) {
            generate(lastPrompt, _selectedMode.value)
        }
    }

    private fun generate(promptText: String, mode: AiMode) {
        currentGenerationJob?.cancel()
        _uiState.value = GenerationUiState.Loading(promptText, mode)
        _isSpeaking.value = false

        currentGenerationJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            try {
                val response = aiService.generate(promptText, mode, _settings.value)
                val duration = System.currentTimeMillis() - startTime
                _uiState.value = GenerationUiState.Success(
                    prompt = promptText,
                    response = response,
                    durationMs = duration,
                    mode = mode
                )
            } catch (e: Exception) {
                _uiState.value = GenerationUiState.Error(
                    e.localizedMessage ?: "Generation failed"
                )
            }
        }
    }

    fun openSettings() {
        _isSettingsOpen.value = true
    }

    fun closeSettings() {
        _isSettingsOpen.value = false
    }

    fun saveSettings(newSettings: AiSettings) {
        settingsManager.saveSettings(newSettings)
        _settings.value = newSettings
        _isSettingsOpen.value = false
        showToast("Settings updated successfully")
    }

    fun resetSettings() {
        val defaultSettings = settingsManager.resetSettings()
        _settings.value = defaultSettings
        showToast("Settings reset to default")
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun setSpeaking(speaking: Boolean) {
        _isSpeaking.value = speaking
    }
}
