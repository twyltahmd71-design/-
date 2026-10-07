package com.example.data.model

data class Language(
    val code: String,
    val nameAr: String,
    val nameEn: String,
    val flag: String,
    val ttsLocale: String? = null
) {
    companion object {
        val AUTO = Language(
            code = "auto",
            nameAr = "اكتشاف تلقائي",
            nameEn = "Auto Detect",
            flag = "🌐",
            ttsLocale = null
        )

        val IRAQI = Language(
            code = "ar-IQ",
            nameAr = "اللهجة العراقية",
            nameEn = "Iraqi Arabic",
            flag = "🇮🇶",
            ttsLocale = "ar"
        )

        val ARABIC = Language(
            code = "ar",
            nameAr = "العربية الفصحى",
            nameEn = "Standard Arabic",
            flag = "🇸🇦",
            ttsLocale = "ar"
        )

        val ENGLISH = Language(
            code = "en",
            nameAr = "الإنجليزية",
            nameEn = "English",
            flag = "🇬🇧",
            ttsLocale = "en"
        )

        val KURDISH = Language(
            code = "ku",
            nameAr = "الكردية",
            nameEn = "Kurdish",
            flag = "☀️",
            ttsLocale = null
        )

        val TURKISH = Language(
            code = "tr",
            nameAr = "التركية",
            nameEn = "Turkish",
            flag = "🇹🇷",
            ttsLocale = "tr"
        )

        val PERSIAN = Language(
            code = "fa",
            nameAr = "الفارسية",
            nameEn = "Persian",
            flag = "🇮🇷",
            ttsLocale = "fa"
        )

        val FRENCH = Language(
            code = "fr",
            nameAr = "الفرنسية",
            nameEn = "French",
            flag = "🇫🇷",
            ttsLocale = "fr"
        )

        val GERMAN = Language(
            code = "de",
            nameAr = "الألمانية",
            nameEn = "German",
            flag = "🇩🇪",
            ttsLocale = "de"
        )

        val SPANISH = Language(
            code = "es",
            nameAr = "الإسبانية",
            nameEn = "Spanish",
            flag = "🇪🇸",
            ttsLocale = "es"
        )

        val RUSSIAN = Language(
            code = "ru",
            nameAr = "الروسية",
            nameEn = "Russian",
            flag = "🇷🇺",
            ttsLocale = "ru"
        )

        val CHINESE = Language(
            code = "zh",
            nameAr = "الصينية",
            nameEn = "Chinese",
            flag = "🇨🇳",
            ttsLocale = "zh"
        )

        val JAPANESE = Language(
            code = "ja",
            nameAr = "اليابانية",
            nameEn = "Japanese",
            flag = "🇯🇵",
            ttsLocale = "ja"
        )

        val ITALIAN = Language(
            code = "it",
            nameAr = "الإيطالية",
            nameEn = "Italian",
            flag = "🇮🇹",
            ttsLocale = "it"
        )

        val SWEDISH = Language(
            code = "sv",
            nameAr = "السويدية",
            nameEn = "Swedish",
            flag = "🇸🇪",
            ttsLocale = "sv"
        )

        val HINDI = Language(
            code = "hi",
            nameAr = "الهندية",
            nameEn = "Hindi",
            flag = "🇮🇳",
            ttsLocale = "hi"
        )

        val URDU = Language(
            code = "ur",
            nameAr = "الأردية",
            nameEn = "Urdu",
            flag = "🇵🇰",
            ttsLocale = "ur"
        )

        val allSourceLanguages = listOf(
            AUTO,
            IRAQI,
            ARABIC,
            ENGLISH,
            KURDISH,
            TURKISH,
            PERSIAN,
            FRENCH,
            GERMAN,
            SPANISH,
            RUSSIAN,
            CHINESE,
            JAPANESE,
            ITALIAN,
            SWEDISH,
            HINDI,
            URDU
        )

        val allTargetLanguages = listOf(
            IRAQI,
            ARABIC,
            ENGLISH,
            KURDISH,
            TURKISH,
            PERSIAN,
            FRENCH,
            GERMAN,
            SPANISH,
            RUSSIAN,
            CHINESE,
            JAPANESE,
            ITALIAN,
            SWEDISH,
            HINDI,
            URDU
        )
    }
}
