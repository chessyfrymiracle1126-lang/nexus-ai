package com.example.data.model

enum class AiMode(
    val label: String,
    val emoji: String,
    val badgeLabel: String,
    val description: String,
    val temperature: Float
) {
    CREATIVE("Creative", "✨", "Creative Mode", "Imaginative synthesis with expanded neural temperature", 0.9f),
    BALANCED("Balanced", "⚡", "Balanced Mode", "Optimal equilibrium of precision and creativity", 0.7f),
    PRECISE("Precise", "🎯", "Precise Mode", "Strict deterministic logic and factual accuracy", 0.2f)
}

enum class AiProvider(
    val id: String,
    val displayName: String,
    val symbol: String
) {
    MOCK("mock", "Built-in Mock Neural Core (No API Key Required)", "⚡"),
    GEMINI("gemini", "Google Gemini API (gemini-2.5-flash)", "✨"),
    CUSTOM("custom", "Custom OpenAI-compatible Endpoint", "🌐");

    companion object {
        fun fromId(id: String): AiProvider = entries.find { it.id == id } ?: MOCK
    }
}

data class QuickPrompt(
    val icon: String,
    val title: String,
    val prompt: String
)

data class AiSettings(
    val provider: AiProvider = AiProvider.MOCK,
    val apiKey: String = "",
    val customEndpoint: String = "https://api.openai.com/v1/chat/completions"
)

sealed interface GenerationUiState {
    data object Idle : GenerationUiState
    data class Loading(val prompt: String, val mode: AiMode) : GenerationUiState
    data class Success(
        val prompt: String,
        val response: String,
        val durationMs: Long,
        val mode: AiMode
    ) : GenerationUiState
    data class Error(val message: String) : GenerationUiState
}
