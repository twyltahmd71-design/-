package com.example.data.repository

import com.example.BuildConfig
import com.example.data.api.RetrofitClient
import com.example.data.db.TranslationDao
import com.example.data.db.TranslationEntity
import com.example.data.model.GeminiContent
import com.example.data.model.GeminiGenerationConfig
import com.example.data.model.GeminiPart
import com.example.data.model.GeminiRequest
import com.example.data.model.Language
import com.example.data.model.TranslationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TranslationRepository(
    private val translationDao: TranslationDao
) {

    val allHistory: Flow<List<TranslationEntity>> = translationDao.getAllHistory()
    val favoriteHistory: Flow<List<TranslationEntity>> = translationDao.getFavorites()

    fun searchHistory(query: String): Flow<List<TranslationEntity>> =
        translationDao.searchHistory(query)

    suspend fun translate(
        text: String,
        sourceLanguage: Language,
        targetLanguage: Language,
        isIraqiDialectMode: Boolean
    ): Result<TranslationResult> = withContext(Dispatchers.IO) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("النص فارغ"))
        }

        val apiKey = BuildConfig.GEMINI_API_KEY

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val systemPrompt = buildSystemPrompt(sourceLanguage, targetLanguage, isIraqiDialectMode)
                val userPrompt = """
                    Translate the following text from ${sourceLanguage.nameAr} (${sourceLanguage.nameEn}) to ${targetLanguage.nameAr} (${targetLanguage.nameEn}).
                    ${if (isIraqiDialectMode || targetLanguage.code == "ar-IQ") "SPECIAL INSTRUCTION: Deliver the translation in natural, authentic Iraqi Arabic (اللهجة العراقية) using beloved everyday Iraqi vocabulary." else ""}
                    
                    Format your response strictly as:
                    TRANSLATION: [The translated text directly]
                    PRONUNCIATION: [Phonetic transcription or pronunciation guide if useful, otherwise NONE]
                    NOTE: [Cultural or Iraqi dialect note if applicable, otherwise NONE]
                    ALTERNATIVES: [Comma-separated alternative translations if any, otherwise NONE]
                    
                    TEXT TO TRANSLATE:
                    $trimmed
                """.trimIndent()

                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = userPrompt)),
                            role = "user"
                        )
                    ),
                    systemInstruction = GeminiContent(
                        parts = listOf(GeminiPart(text = systemPrompt))
                    ),
                    generationConfig = GeminiGenerationConfig(
                        temperature = 0.25f,
                        topP = 0.95f,
                        topK = 40
                    )
                )

                val response = RetrofitClient.geminiService.generateContent(apiKey, request)
                val candidateText = response.candidates?.firstOrNull()
                    ?.content?.parts?.firstOrNull()?.text

                if (!candidateText.isNullOrBlank()) {
                    val parsed = parseGeminiOutput(
                        raw = candidateText,
                        sourceText = trimmed,
                        sourceLang = sourceLanguage,
                        targetLang = targetLanguage
                    )

                    // Save to Room DB
                    val entity = TranslationEntity(
                        sourceText = trimmed,
                        translatedText = parsed.translatedText,
                        sourceLanguageCode = sourceLanguage.code,
                        sourceLanguageName = sourceLanguage.nameAr,
                        targetLanguageCode = targetLanguage.code,
                        targetLanguageName = targetLanguage.nameAr,
                        timestamp = System.currentTimeMillis(),
                        pronunciation = parsed.pronunciation,
                        dialectNote = parsed.dialectNote
                    )
                    translationDao.insert(entity)

                    return@withContext Result.success(parsed)
                }
            } catch (e: Exception) {
                // If API failed (e.g. network timeout or quota), fallback to smart offline dictionary
                val fallback = resolveOfflineTranslation(trimmed, sourceLanguage, targetLanguage, isIraqiDialectMode)
                if (fallback != null) {
                    saveFallbackEntity(fallback, sourceLanguage, targetLanguage)
                    return@withContext Result.success(fallback)
                }
                return@withContext Result.failure(Exception("تعذر الاتصال بخدمة الذكاء الاصطناعي: ${e.localizedMessage ?: "خطأ في الشبكة"}"))
            }
        }

        // Offline / built-in translator fallback
        val offline = resolveOfflineTranslation(trimmed, sourceLanguage, targetLanguage, isIraqiDialectMode)
        if (offline != null) {
            saveFallbackEntity(offline, sourceLanguage, targetLanguage)
            return@withContext Result.success(offline)
        }

        Result.failure(Exception("يرجى التأكد من الاتصال بالإنترنت أو إعداد مفتاح API في لوحة الأسرار."))
    }

    private suspend fun saveFallbackEntity(
        res: TranslationResult,
        sourceLang: Language,
        targetLang: Language
    ) {
        val entity = TranslationEntity(
            sourceText = res.sourceText,
            translatedText = res.translatedText,
            sourceLanguageCode = sourceLang.code,
            sourceLanguageName = sourceLang.nameAr,
            targetLanguageCode = targetLang.code,
            targetLanguageName = targetLang.nameAr,
            timestamp = System.currentTimeMillis(),
            pronunciation = res.pronunciation,
            dialectNote = res.dialectNote
        )
        translationDao.insert(entity)
    }

    private fun buildSystemPrompt(
        sourceLanguage: Language,
        targetLanguage: Language,
        isIraqiDialectMode: Boolean
    ): String {
        return """
            You are 'المترجم العراقي' (The Iraqi AI Translator), an advanced linguistic AI engine superior to Google Translate.
            You excel at nuanced translation across all world languages with profound mastery of Iraqi Arabic (اللهجة العراقية: البغدادية والجنوبية والمصلاوية).
            You preserve tone, humor, context, and idioms.
            When translating into Iraqi Arabic, speak authentically like a native Iraqi (using words like: شكو ماكو، فدوة، تدلل، لعد، خوش، هوايه، حيل، هسه، دير بالك، عاشت ايدك).
            Never add markdown fences or conversational preambles.
        """.trimIndent()
    }

    private fun parseGeminiOutput(
        raw: String,
        sourceText: String,
        sourceLang: Language,
        targetLang: Language
    ): TranslationResult {
        var translation = ""
        var pronunciation: String? = null
        var note: String? = null
        val alternatives = mutableListOf<String>()

        val lines = raw.lines()
        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("TRANSLATION:", ignoreCase = true) -> {
                    translation = trimmed.substringAfter("TRANSLATION:").trim()
                }
                trimmed.startsWith("PRONUNCIATION:", ignoreCase = true) -> {
                    val p = trimmed.substringAfter("PRONUNCIATION:").trim()
                    if (p.isNotBlank() && !p.equals("NONE", ignoreCase = true)) {
                        pronunciation = p
                    }
                }
                trimmed.startsWith("NOTE:", ignoreCase = true) -> {
                    val n = trimmed.substringAfter("NOTE:").trim()
                    if (n.isNotBlank() && !n.equals("NONE", ignoreCase = true)) {
                        note = n
                    }
                }
                trimmed.startsWith("ALTERNATIVES:", ignoreCase = true) -> {
                    val alts = trimmed.substringAfter("ALTERNATIVES:").trim()
                    if (alts.isNotBlank() && !alts.equals("NONE", ignoreCase = true)) {
                        alternatives.addAll(alts.split(",").map { it.trim() }.filter { it.isNotEmpty() })
                    }
                }
            }
        }

        if (translation.isBlank()) {
            // Fallback: take raw text if format wasn't strictly followed
            translation = raw.trim()
                .replace("TRANSLATION:", "")
                .trim()
        }

        return TranslationResult(
            sourceText = sourceText,
            translatedText = translation,
            sourceLanguage = sourceLang,
            targetLanguage = targetLang,
            pronunciation = pronunciation,
            dialectNote = note,
            alternatives = alternatives,
            isOfflineFallback = false
        )
    }

    suspend fun toggleFavorite(id: Long, currentFavorite: Boolean) = withContext(Dispatchers.IO) {
        translationDao.updateFavorite(id, !currentFavorite)
    }

    suspend fun deleteHistoryItem(id: Long) = withContext(Dispatchers.IO) {
        translationDao.deleteById(id)
    }

    suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
        translationDao.clearAll()
    }

    private fun resolveOfflineTranslation(
        input: String,
        source: Language,
        target: Language,
        iraqiMode: Boolean
    ): TranslationResult? {
        val clean = input.trim().lowercase()

        // Check dictionary matches
        for (entry in OFFLINE_DICTIONARY) {
            if (entry.matches(clean)) {
                val translation = entry.getTranslation(target.code, iraqiMode)
                if (translation != null) {
                    return TranslationResult(
                        sourceText = input,
                        translatedText = translation,
                        sourceLanguage = source,
                        targetLanguage = target,
                        pronunciation = entry.pronunciation,
                        dialectNote = entry.note ?: "ترجمة فورية من القاموس العراقي الذكي",
                        alternatives = entry.alternatives,
                        isOfflineFallback = true
                    )
                }
            }
        }

        // Heuristic Iraqi-to-English / Iraqi-to-Fusha / English-to-Iraqi simple rule mapper
        if (target.code == "ar-IQ" || (target.code == "ar" && iraqiMode)) {
            val converted = convertToIraqiDialect(input)
            return TranslationResult(
                sourceText = input,
                translatedText = converted,
                sourceLanguage = source,
                targetLanguage = target,
                pronunciation = null,
                dialectNote = "تمت الصياغة باللهجة العراقية الأصيلة",
                alternatives = listOf(),
                isOfflineFallback = true
            )
        }

        return null
    }

    private fun convertToIraqiDialect(text: String): String {
        var res = text
        val mappings = mapOf(
            "ماذا" to "شـ / شكو",
            "كيف حالك" to "شلونك / شخبارك",
            "جداً" to "حيل / كلش",
            "كثيراً" to "هوايه",
            "الآن" to "هسه",
            "لماذا" to "ليش",
            "حسناً" to "خوش / تمام",
            "أريد" to "أريد / رايد",
            "أذهب" to "أروح",
            "جميل" to "صاك / يخبل",
            "شكراً" to "عاشت إيدك / ممنون",
            "أهلاً وسهلاً" to "هلا بيك وتدلل",
            "لا بأس" to "ماكو مشكلة / فدوة",
            "أنظر" to "باوع",
            "تفضل" to "تدلل / استريح",
            "هنا" to "هنايه",
            "هناك" to "هناكيه",
            "أنا أعمل" to "دا أشتغل",
            "إلى أين" to "لوين"
        )
        for ((fusha, iraqi) in mappings) {
            res = res.replace(fusha, iraqi)
        }
        return res
    }

    data class DictEntry(
        val keys: List<String>,
        val en: String,
        val ar: String,
        val iraqi: String,
        val tr: String? = null,
        val ku: String? = null,
        val fr: String? = null,
        val pronunciation: String? = null,
        val note: String? = null,
        val alternatives: List<String> = emptyList()
    ) {
        fun matches(query: String): Boolean {
            val q = query.trim().lowercase()
            return keys.any { it.lowercase() == q } ||
                    en.lowercase() == q ||
                    ar == query ||
                    iraqi == query
        }

        fun getTranslation(targetCode: String, iraqiMode: Boolean): String? {
            return when (targetCode) {
                "ar-IQ" -> iraqi
                "ar" -> if (iraqiMode) iraqi else ar
                "en" -> en
                "tr" -> tr ?: en
                "ku" -> ku ?: iraqi
                "fr" -> fr ?: en
                else -> en
            }
        }
    }

    companion object {
        val OFFLINE_DICTIONARY = listOf(
            DictEntry(
                keys = listOf("شكو ماكو", "شكو ماكو؟", "shako mako", "what's up", "whats up"),
                en = "What's up? / What's going on?",
                ar = "ما الأخبار؟ ماذا يجري؟",
                iraqi = "شكو ماكو؟ شأخبارك؟",
                tr = "Ne haber? Ne var ne yok?",
                ku = "چ باسە؟ چ هەواڵە؟",
                fr = "Quoi de neuf ? Qu'est-ce qui se passe ?",
                pronunciation = "Shako Mako?",
                note = "العبارة البغدادية والعراقية الأشهر للتحية والسؤال عن الأحوال.",
                alternatives = listOf("شأخبارك؟", "شلونك؟")
            ),
            DictEntry(
                keys = listOf("شلونك", "شلونك؟", "how are you", "how are you?"),
                en = "How are you doing?",
                ar = "كيف حالك؟",
                iraqi = "شلونك؟ عساك بخير",
                tr = "Nasılsın?",
                ku = "چۆنی؟ باشی؟",
                fr = "Comment vas-tu ?",
                pronunciation = "Shlonak?",
                note = "تحية عراقية أصيلة، تقال للمذكر (شلونك) وللمؤنث (شلونچ).",
                alternatives = listOf("شخبارك؟", "شلون الصحة؟")
            ),
            DictEntry(
                keys = listOf("فدوة", "فدوه", "fedwa", "my pleasure"),
                en = "May I sacrifice for you! / You're so precious!",
                ar = "فداك روحي / أنت عزيز جداً",
                iraqi = "فدوة لعينك / فدوة لقلبك",
                tr = "Canım sana feda!",
                ku = "قوربانت بم",
                fr = "Que je me sacrifie pour toi / Tu es précieux",
                pronunciation = "Fadwa",
                note = "تعبير عراقي دافئ جداً يدل على المحبة والتضحية والمودة.",
                alternatives = listOf("بعد روحي", "عيوني")
            ),
            DictEntry(
                keys = listOf("تدلل", "تتدلل", "you are welcome", "at your service"),
                en = "At your service / You got it!",
                ar = "على الرحب والسعة / طلبك مجاب",
                iraqi = "تدلل من عيوني الثنتين",
                tr = "Başım üstüne / Rica ederim",
                ku = "سەر چاو",
                fr = "À ton service / Avec grand plaisir",
                pronunciation = "Tidallal",
                note = "تقال بكرم عراقي أصيل عندما يطلب شخص منك شيئاً.",
                alternatives = listOf("من عيوني", "تأمر أمر")
            ),
            DictEntry(
                keys = listOf("لعد", "la'ad", "so then"),
                en = "So then / In that case",
                ar = "إذاً / في هذه الحالة",
                iraqi = "لعد شنسوي؟",
                tr = "Öyleyse / O zaman",
                ku = "ئەی كەواتە",
                fr = "Alors / Dans ce cas",
                pronunciation = "La'ad",
                note = "أداة ربط عراقية مميزة جداً تعني 'إذاً'."
            ),
            DictEntry(
                keys = listOf("خوش", "khosh", "good", "nice"),
                en = "Good / Nice / Fine",
                ar = "جيد / طيب / ممتاز",
                iraqi = "خوش ولد / خوش فكرة",
                tr = "Hoş / Güzel / İyi",
                ku = "خۆش / باش",
                fr = "Bien / Bon",
                pronunciation = "Khosh",
                note = "كلمة سومرية وبابلية متجذرة في اللهجة العراقية تعني الشيء الطيب والجيد."
            ),
            DictEntry(
                keys = listOf("حيل", "كلش", "very", "so much"),
                en = "Very / Extremely / So much",
                ar = "جداً / للغاية",
                iraqi = "حيل هوايه / كلش حلو",
                tr = "Çok fazla",
                ku = "زۆر",
                fr = "Très / Tellement",
                pronunciation = "Heil / Killish",
                note = "تفيد المبالغة والتأكيد في اللهجة العراقية."
            ),
            DictEntry(
                keys = listOf("عاشت ايدك", "عاشت إيدك", "bless your hands", "thank you"),
                en = "Bless your hands! / Well done!",
                ar = "سلمت يداك / بارك الله فيك",
                iraqi = "عاشت إيدك يا غالي",
                tr = "Eline sağlık!",
                ku = "دەستت خۆش بێت",
                fr = "Bravo et que tes mains soient bénies !",
                pronunciation = "Asht Eedak",
                note = "أرقى عبارة شكر وامتنان عراقية تقال لمن أنجز عملاً أو أعد طعاماً."
            ),
            DictEntry(
                keys = listOf("هسه", "هسة", "now", "right now"),
                en = "Now / Right now",
                ar = "الآن / في هذا الوقت",
                iraqi = "هسه أجي / هسه نروح",
                tr = "Şimdi",
                ku = "ئێستا",
                fr = "Maintenant",
                pronunciation = "Hassa",
                note = "تعني 'الآن' في اللهجة العراقية، من أصل آرامي وبابلي."
            ),
            DictEntry(
                keys = listOf("صباح الخير", "good morning"),
                en = "Good morning",
                ar = "صباح الخير",
                iraqi = "صباح الخير والياسمين / صباح الورد",
                tr = "Günaydın",
                ku = "بەیانیت باش",
                fr = "Bonjour",
                pronunciation = "Sabah Al-Khair"
            ),
            DictEntry(
                keys = listOf("مساء الخير", "good evening"),
                en = "Good evening",
                ar = "مساء الخير",
                iraqi = "مساء الخير والعافية",
                tr = "İyi akşamlar",
                ku = "ئێوارەت باش",
                fr = "Bonsoir",
                pronunciation = "Masa'a Al-Khair"
            ),
            DictEntry(
                keys = listOf("شكرا", "شكراً", "thank you", "thanks"),
                en = "Thank you very much",
                ar = "شكراً جزيلاً",
                iraqi = "ممنون منك وعاشت إيدك",
                tr = "Çok teşekkür ederim",
                ku = "سوپاس بۆ تۆ",
                fr = "Merci beaucoup",
                pronunciation = "Mamnoon minnak"
            )
        )
    }
}
