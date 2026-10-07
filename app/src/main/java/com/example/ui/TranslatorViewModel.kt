package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.TranslationEntity
import com.example.data.model.Language
import com.example.data.model.TranslationResult
import com.example.data.repository.TranslationRepository
import com.example.util.TtsManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TranslatorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = TranslationRepository(db.translationDao())
    val ttsManager = TtsManager(application)

    // Current Navigation Tab (0: Translator, 1: History, 2: About Developer)
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // Translator Input / Output State
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _sourceLanguage = MutableStateFlow(Language.AUTO)
    val sourceLanguage: StateFlow<Language> = _sourceLanguage.asStateFlow()

    private val _targetLanguage = MutableStateFlow(Language.IRAQI)
    val targetLanguage: StateFlow<Language> = _targetLanguage.asStateFlow()

    private val _isIraqiDialectMode = MutableStateFlow(true)
    val isIraqiDialectMode: StateFlow<Boolean> = _isIraqiDialectMode.asStateFlow()

    private val _isTranslating = MutableStateFlow(false)
    val isTranslating: StateFlow<Boolean> = _isTranslating.asStateFlow()

    private val _translationResult = MutableStateFlow<TranslationResult?>(null)
    val translationResult: StateFlow<TranslationResult?> = _translationResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // History Search & Filter State
    private val _historySearchQuery = MutableStateFlow("")
    val historySearchQuery: StateFlow<String> = _historySearchQuery.asStateFlow()

    private val _historyFilterFavoritesOnly = MutableStateFlow(false)
    val historyFilterFavoritesOnly: StateFlow<Boolean> = _historyFilterFavoritesOnly.asStateFlow()

    val historyItems: StateFlow<List<TranslationEntity>> = combine(
        _historySearchQuery,
        _historyFilterFavoritesOnly
    ) { query, favsOnly ->
        Pair(query, favsOnly)
    }.flatMapLatest { (query, favsOnly) ->
        if (query.isNotBlank()) {
            repository.searchHistory(query)
        } else if (favsOnly) {
            repository.favoriteHistory
        } else {
            repository.allHistory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(index: Int) {
        _currentTab.value = index
    }

    fun setInputText(text: String) {
        _inputText.value = text
        if (text.isBlank()) {
            _translationResult.value = null
            _errorMessage.value = null
        }
    }

    fun clearInput() {
        _inputText.value = ""
        _translationResult.value = null
        _errorMessage.value = null
    }

    fun setSourceLanguage(lang: Language) {
        _sourceLanguage.value = lang
    }

    fun setTargetLanguage(lang: Language) {
        _targetLanguage.value = lang
    }

    fun swapLanguages() {
        val currSource = _sourceLanguage.value
        val currTarget = _targetLanguage.value

        if (currSource == Language.AUTO) {
            // Cannot swap with auto, make target source and default target to Arabic/Iraqi
            _sourceLanguage.value = currTarget
            _targetLanguage.value = Language.IRAQI
        } else {
            _sourceLanguage.value = currTarget
            _targetLanguage.value = currSource
        }

        // Swap texts if we already have a translation
        val currentRes = _translationResult.value
        if (currentRes != null && currentRes.translatedText.isNotBlank()) {
            _inputText.value = currentRes.translatedText
            _translationResult.value = null
        }
    }

    fun toggleIraqiMode() {
        _isIraqiDialectMode.value = !_isIraqiDialectMode.value
    }

    fun translate() {
        val text = _inputText.value.trim()
        if (text.isEmpty()) {
            _errorMessage.value = "يرجى كتابة نص للترجمة"
            return
        }

        viewModelScope.launch {
            _isTranslating.value = true
            _errorMessage.value = null
            val result = repository.translate(
                text = text,
                sourceLanguage = _sourceLanguage.value,
                targetLanguage = _targetLanguage.value,
                isIraqiDialectMode = _isIraqiDialectMode.value
            )

            result.onSuccess {
                _translationResult.value = it
            }.onFailure {
                _errorMessage.value = it.localizedMessage ?: "حدث خطأ أثناء الترجمة"
            }
            _isTranslating.value = false
        }
    }

    fun setHistorySearchQuery(query: String) {
        _historySearchQuery.value = query
    }

    fun setHistoryFilterFavoritesOnly(favoritesOnly: Boolean) {
        _historyFilterFavoritesOnly.value = favoritesOnly
    }

    fun toggleFavorite(id: Long, current: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, current)
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryItem(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }

    fun loadHistoryItem(item: TranslationEntity) {
        _inputText.value = item.sourceText
        // Find matching source and target
        _sourceLanguage.value = Language.allSourceLanguages.firstOrNull { it.code == item.sourceLanguageCode }
            ?: Language.AUTO
        _targetLanguage.value = Language.allTargetLanguages.firstOrNull { it.code == item.targetLanguageCode }
            ?: Language.IRAQI

        _translationResult.value = TranslationResult(
            sourceText = item.sourceText,
            translatedText = item.translatedText,
            sourceLanguage = _sourceLanguage.value,
            targetLanguage = _targetLanguage.value,
            pronunciation = item.pronunciation,
            dialectNote = item.dialectNote
        )
        _currentTab.value = 0 // Navigate back to translator tab
    }

    fun speak(text: String, languageCode: String) {
        ttsManager.speak(text, languageCode)
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
