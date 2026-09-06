package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AiProvider
import com.example.data.model.AiSettings

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("nexus_ai_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PROVIDER = "provider_id"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_ENDPOINT = "endpoint_url"
    }

    fun getSettings(): AiSettings {
        val providerId = prefs.getString(KEY_PROVIDER, AiProvider.MOCK.id) ?: AiProvider.MOCK.id
        val apiKey = prefs.getString(KEY_API_KEY, "") ?: ""
        val endpoint = prefs.getString(
            KEY_ENDPOINT,
            "https://api.openai.com/v1/chat/completions"
        ) ?: "https://api.openai.com/v1/chat/completions"

        return AiSettings(
            provider = AiProvider.fromId(providerId),
            apiKey = apiKey,
            customEndpoint = endpoint
        )
    }

    fun saveSettings(settings: AiSettings) {
        prefs.edit()
            .putString(KEY_PROVIDER, settings.provider.id)
            .putString(KEY_API_KEY, settings.apiKey.trim())
            .putString(KEY_ENDPOINT, settings.customEndpoint.trim())
            .apply()
    }

    fun resetSettings(): AiSettings {
        prefs.edit().clear().apply()
        return AiSettings()
    }
}
