package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @field:Json(name = "contents") val contents: List<GeminiContent>,
    @field:Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null,
    @field:Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @field:Json(name = "parts") val parts: List<GeminiPart>,
    @field:Json(name = "role") val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @field:Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    @field:Json(name = "temperature") val temperature: Float? = 0.3f,
    @field:Json(name = "topP") val topP: Float? = 0.95f,
    @field:Json(name = "topK") val topK: Int? = 40
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @field:Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @field:Json(name = "content") val content: GeminiContent? = null,
    @field:Json(name = "finishReason") val finishReason: String? = null
)

data class TranslationResult(
    val sourceText: String,
    val translatedText: String,
    val sourceLanguage: Language,
    val targetLanguage: Language,
    val pronunciation: String? = null,
    val dialectNote: String? = null,
    val alternatives: List<String> = emptyList(),
    val isOfflineFallback: Boolean = false
)
