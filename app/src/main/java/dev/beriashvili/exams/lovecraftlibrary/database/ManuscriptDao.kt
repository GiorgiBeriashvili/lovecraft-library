package dev.beriashvili.exams.lovecraftlibrary.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import dev.beriashvili.exams.lovecraftlibrary.models.Manuscript

@Dao
interface ManuscriptDao {
    @Query("SELECT EXISTS(SELECT * FROM manuscript WHERE id = :id)")
    fun exists(id: Int): Boolean

    @Query("SELECT * FROM manuscript")
    suspend fun getAll(): MutableList<Manuscript>

    @Query("SELECT * FROM manuscript WHERE id = :id")
    suspend fun getById(id: Int): Manuscript

    @Query("SELECT * FROM manuscript ORDER BY id DESC LIMIT 1")
    suspend fun getLastById(): Manuscript

    @Insert
    suspend fun insert(manuscript: Manuscript)

    @Delete
    suspend fun delete(manuscript: Manuscript)

    @Query("UPDATE manuscript SET text_size = :textSize WHERE id = :id")
    suspend fun updateTextSize(id: Int, textSize: Float)
}