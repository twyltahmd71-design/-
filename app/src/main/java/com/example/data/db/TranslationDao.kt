package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TranslationDao {
    @Query("SELECT * FROM translations ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<TranslationEntity>>

    @Query("SELECT * FROM translations WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<TranslationEntity>>

    @Query("SELECT * FROM translations WHERE sourceText LIKE '%' || :query || '%' OR translatedText LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchHistory(query: String): Flow<List<TranslationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(translation: TranslationEntity): Long

    @Update
    suspend fun update(translation: TranslationEntity)

    @Delete
    suspend fun delete(translation: TranslationEntity)

    @Query("DELETE FROM translations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE translations SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM translations")
    suspend fun clearAll()
}
