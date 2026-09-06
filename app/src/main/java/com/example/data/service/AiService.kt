package com.example.data.service

import com.example.BuildConfig
import com.example.data.model.AiMode
import com.example.data.model.AiProvider
import com.example.data.model.AiSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generate(
        prompt: String,
        mode: AiMode,
        settings: AiSettings
    ): String = withContext(Dispatchers.IO) {
        when (settings.provider) {
            AiProvider.MOCK -> generateMockResponse(prompt, mode)
            AiProvider.GEMINI -> generateGeminiResponse(prompt, mode, settings)
            AiProvider.CUSTOM -> generateCustomEndpointResponse(prompt, mode, settings)
        }
    }

    private suspend fun generateMockResponse(prompt: String, mode: AiMode): String {
        // Realistic neural synthesis delay between 700ms and 1100ms
        delay(850)

        val trimmed = prompt.trim().lowercase()

        return when {
            trimmed.contains("quantum computing") || (trimmed.contains("summarize") && trimmed.contains("quantum")) -> {
                """
                ### Core Principles of Quantum Computing

                1. **Superposition**: Unlike classical bits that are restricted to binary states of 0 or 1, quantum bits (**qubits**) exist in a linear combination of both states simultaneously. This allows a quantum processor to evaluate billions of states in parallel.
                
                2. **Entanglement**: Qubits can become fundamentally linked such that the state of one instantly dictates the state of another, regardless of spatial distance. This allows exponential computational density and unified state processing.
                
                3. **Quantum Interference**: Specialized algorithms leverage constructive interference to amplify the probability of correct solutions while destructive interference cancels out erroneous outcomes, solving combinatorial hurdles in seconds.
                """.trimIndent()
            }

            trimmed.contains("renewable energy") || trimmed.contains("clean energy") || (trimmed.contains("brainstorm") && trimmed.contains("startup")) -> {
                """
                ### 5 High-Impact AI & Clean Energy Startups

                1. **AeroGrid AI**: Machine learning microgrid load balancing using localized atmospheric Doppler radar and micro-climate wind vectors to preemptively schedule battery discharging.
                
                2. **SolarSense Photometrics**: Autonomous drone swarms performing computer vision electroluminescence scanning across gigawatt solar installations to eliminate micro-crack cell losses.
                
                3. **TwinStorage OS**: Digital twin simulation predicting lithium dendrite accumulation in utility-scale battery energy storage systems (BESS), extending pack lifecycle by 34%.
                
                4. **GeoFlow AI**: Deep geothermal trajectory prediction system reducing exploration seismic drilling hazards through subterranean acoustic neural modeling.
                
                5. **VoltPeer Smart V2G**: Decentralized bidirectional vehicle-to-grid marketplace enabling autonomous EV fleet arbitrage during peak transmission strain.
                """.trimIndent()
            }

            trimmed.contains("css") || trimmed.contains("card component") || (trimmed.contains("code") && trimmed.contains("neon")) -> {
                """
                ### Cyberpunk Glassmorphic Card Component

                Here is a clean, modern CSS component with dynamic neon hover borders and frosted glass backdrop:

                ```css
                .nexus-card {
                  position: relative;
                  background: rgba(14, 23, 47, 0.65);
                  backdrop-filter: blur(16px);
                  -webkit-backdrop-filter: blur(16px);
                  border: 1px solid rgba(0, 242, 254, 0.25);
                  border-radius: 16px;
                  padding: 24px;
                  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.4);
                  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                  overflow: hidden;
                }

                .nexus-card:hover {
                  border-color: #00f2fe;
                  box-shadow: 0 0 24px rgba(0, 242, 254, 0.45),
                              0 0 48px rgba(168, 85, 247, 0.25);
                  transform: translateY(-4px);
                }
                ```

                **Design Features**:
                - Frosted glass depth with high contrast obsidian surface
                - Dual-hue neon accent bloom (Cyan `#00f2fe` + Purple `#a855f7`)
                - Sub-pixel smooth elevation translation on hover
                """.trimIndent()
            }

            trimmed.contains("transformer") || (trimmed.contains("explain") && trimmed.contains("12-year-old")) -> {
                """
                ### How Neural Network Transformers Work (Gamer Edition) 🎮

                Imagine you are playing an open-world battle arena game:

                - **Old AI (RNNs)**: Like reading a chat log one word at a time from left to right. By the time it reads word 50, it forgot what word 1 said!
                - **The Transformer (Self-Attention)**: Like an eagle-eye minimap! It looks at every word in the entire sentence at the exact same millisecond.
                - **Attention Heads**: Like having 12 pro-scouts tracking different action items—one scout tracks who used an ability, another tracks which target took damage, and another tracks the ultimate cooldown.
                - **Parallel Processing**: Because all tokens are analyzed concurrently across GPU clusters, the AI learns patterns and writes back with near-instantaneous speed!
                """.trimIndent()
            }

            trimmed.contains("email") || (trimmed.contains("draft") && trimmed.contains("feedback")) -> {
                """
                **Subject**: Executive Briefing & Strategic Feedback Request: [Project Name]

                Dear [Executive Name],

                I hope you are having an exceptional week.

                Our team has reached a pivotal milestone with the rollout of **[Project Name]**. In our early telemetry, the initiative has achieved [mention specific win, e.g., 28% reduction in latency / positive initial cohort adoption].

                As we finalize our next milestone roadmap, your strategic perspective on [specific topic or architectural trade-off] would be invaluable in ensuring our execution continues to align seamlessly with broader organizational priorities.

                Would you have 10 minutes next Tuesday or Wednesday for a quick sync? I've prepared an executive one-pager outlining the core decision forks for your review.

                Thank you for your time and continued guidance.

                Warm regards,

                **[Your Name]**  
                [Your Title] • [Your Team]
                """.trimIndent()
            }

            trimmed.contains("debug") || (trimmed.contains("race condition") && trimmed.contains("async")) -> {
                """
                ### Diagnosing JavaScript Async Race Conditions

                **The Root Cause**: Fast successive network calls resolve out of order, causing stale promises to overwrite newer application state.

                ```javascript
                // Solution: Request Sequence Token or AbortController
                let currentRequestId = 0;

                async function fetchAndApplyData(query) {
                  const requestId = ++currentRequestId;
                  const response = await api.search(query);
                  
                  // Ignore if a newer request has already been dispatched
                  if (requestId === currentRequestId) {
                    updateUiState(response);
                  }
                }
                ```

                **Recommended Best Practices**:
                1. Pass `AbortSignal` with `AbortController` to immediately terminate orphaned network requests.
                2. Implement a 300ms debounce buffer on user typing to avoid rapid fire invocation.
                """.trimIndent()
            }

            else -> {
                when (mode) {
                    AiMode.CREATIVE -> {
                        """
                        ### Nexus Creative Intelligence Synthesis

                        Synthesized perspective on: **"${prompt.trim()}"**

                        - **Visionary Trajectory**: Looking at this from a high-dimensional angle, the core vectors intersect emergent automation, fluid user dynamics, and decentralized scalability.
                        - **Innovative Angles**:
                          1. *Synergistic Co-design*: Blending proactive neural heuristics with ambient contextual awareness.
                          2. *Dynamic Adaptability*: Building feedback loops that re-calibrate based on real-time sensory inputs.
                        - **Strategic Horizon**: By leaning into asymmetric iteration, rapid prototyping surfaces novel insights that conventional linear pipelines overlook.
                        """.trimIndent()
                    }

                    AiMode.BALANCED -> {
                        """
                        ### Nexus Analysis: Balanced Architecture

                        Addressing prompt: **"${prompt.trim()}"**

                        - **Key Insight**: Success relies on balancing functional resilience, speed to deployment, and maintainable simplicity.
                        - **Core Pillars**:
                          1. **Core Clarity**: Clearly separate business logic, state flows, and interface presentation.
                          2. **Predictable Performance**: Optimize resource overhead and handle edge cases proactively.
                          3. **User Centricity**: Deliver immediate tactile feedback with crisp visual feedback loops.
                        
                        *Summary*: Proceeding with a modular, testable implementation delivers both short-term velocity and sustained long-term durability.
                        """.trimIndent()
                    }

                    AiMode.PRECISE -> {
                        """
                        ### Nexus Precise Evaluation

                        Query: **"${prompt.trim()}"**

                        **Formal Specifications**:
                        - **Verification**: Input validated against deterministic logical rules.
                        - **Direct Action Items**:
                          1. Validate input parameters and sanitize boundary conditions.
                          2. Execute linear dependency checks and guarantee state immutability.
                          3. Return verified deterministic outputs with zero hallucinated drift.
                        - **Metrics**: 99.9% consistency rating with minimal computational complexity.
                        """.trimIndent()
                    }
                }
            }
        }
    }

    private suspend fun generateGeminiResponse(
        prompt: String,
        mode: AiMode,
        settings: AiSettings
    ): String {
        val apiKey = settings.apiKey.ifBlank { BuildConfig.GEMINI_API_KEY }.trim()
        if (apiKey.isBlank()) {
            return "⚠️ **Gemini API Key Required**\n\nPlease open the Settings menu (gear icon in the top right) and enter your Google Gemini API key, or configure it via the Secrets panel in AI Studio."
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

        val jsonBody = JSONObject().apply {
            val contentsArray = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    }
                    put("parts", partsArray)
                }
                put(contentObj)
            }
            put("contents", contentsArray)

            val generationConfig = JSONObject().apply {
                put("temperature", mode.temperature.toDouble())
                put("topP", 0.95)
                put("maxOutputTokens", 2048)
            }
            put("generationConfig", generationConfig)

            val systemInstruction = JSONObject().apply {
                val parts = JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", "You are NexusAI, an advanced futuristic AI assistant. Provide direct, beautifully structured, insightful answers using markdown formatting (headers, bullet points, bold highlights, clean code blocks where relevant). Never add disclaimers unless strictly asked.")
                    })
                }
                put("parts", parts)
            }
            put("systemInstruction", systemInstruction)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonBody.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = try {
                    val errJson = JSONObject(responseBody)
                    errJson.optJSONObject("error")?.optString("message") ?: "HTTP ${response.code}"
                } catch (e: Exception) {
                    "HTTP ${response.code}: $responseBody"
                }
                return "❌ **API Error (${response.code})**: $errorMsg\n\nPlease verify your API key in Settings."
            }

            val json = JSONObject(responseBody)
            val candidates = json.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            text?.takeIf { it.isNotBlank() }
                ?: "NexusAI received an empty response from the neural engine."
        } catch (e: Exception) {
            "⚠️ **Connection Failure**: ${e.localizedMessage ?: e.message}\n\nPlease check your network connection and API configuration."
        }
    }

    private suspend fun generateCustomEndpointResponse(
        prompt: String,
        mode: AiMode,
        settings: AiSettings
    ): String {
        val endpoint = settings.customEndpoint.ifBlank { "https://api.openai.com/v1/chat/completions" }.trim()
        val apiKey = settings.apiKey.trim()

        val jsonBody = JSONObject().apply {
            put("model", "gpt-4o-mini")
            val messages = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "You are NexusAI, an advanced futuristic AI assistant. Format your response cleanly with markdown.")
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            }
            put("messages", messages)
            put("temperature", mode.temperature.toDouble())
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonBody.toString().toRequestBody(mediaType)
        val requestBuilder = Request.Builder()
            .url(endpoint)
            .post(requestBody)

        if (apiKey.isNotBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $apiKey")
        }

        return try {
            val response = client.newCall(requestBuilder.build()).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return "❌ **Endpoint Error (${response.code})**: $responseBody"
            }

            val json = JSONObject(responseBody)
            val choices = json.optJSONArray("choices")
            val message = choices?.optJSONObject(0)?.optJSONObject("message")
            val content = message?.optString("content")

            content?.takeIf { it.isNotBlank() }
                ?: "NexusAI received an empty response from the custom endpoint."
        } catch (e: Exception) {
            "⚠️ **Endpoint Connection Error**: ${e.localizedMessage ?: e.message}"
        }
    }
}
