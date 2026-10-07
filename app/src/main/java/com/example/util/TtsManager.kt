package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
        } else {
            Log.e("TtsManager", "TextToSpeech init failed with status: $status")
        }
    }

    fun speak(text: String, languageCode: String) {
        if (!isInitialized || tts == null) return
        try {
            val locale = when (languageCode) {
                "ar", "ar-IQ" -> Locale.forLanguageTag("ar-IQ")
                "en" -> Locale.ENGLISH
                "tr" -> Locale.forLanguageTag("tr")
                "fr" -> Locale.FRENCH
                "de" -> Locale.GERMAN
                "es" -> Locale.forLanguageTag("es")
                "ru" -> Locale.forLanguageTag("ru")
                "zh" -> Locale.CHINESE
                "ja" -> Locale.JAPANESE
                "it" -> Locale.ITALIAN
                "hi" -> Locale.forLanguageTag("hi")
                "ur" -> Locale.forLanguageTag("ur")
                else -> Locale.getDefault()
            }
            tts?.language = locale
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utterance_${System.currentTimeMillis()}")
            _isSpeaking.value = true
        } catch (e: Exception) {
            Log.e("TtsManager", "Error speaking text", e)
        }
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
        } catch (e: Exception) {
            // Ignore
        }
    }
}
