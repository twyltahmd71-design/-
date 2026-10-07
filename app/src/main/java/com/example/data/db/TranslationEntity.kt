package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translations")
data class TranslationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sourceText: String,
    val translatedText: String,
    val sourceLanguageCode: String,
    val sourceLanguageName: String,
    val targetLanguageCode: String,
    val targetLanguageName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val pronunciation: String? = null,
    val dialectNote: String? = null
)
