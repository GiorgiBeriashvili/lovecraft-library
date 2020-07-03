package dev.beriashvili.exams.lovecraftlibrary.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import dev.beriashvili.exams.lovecraftlibrary.models.Entry

@Dao
interface EntryDao {
    @Query("SELECT EXISTS(SELECT * FROM entry WHERE id = :id)")
    fun exists(id: Int): Boolean

    @Query("SELECT * FROM entry")
    suspend fun getAll(): MutableList<Entry>

    @Query("SELECT * FROM entry WHERE id = :id")
    suspend fun getById(id: Int): Entry

    @Query("SELECT * FROM entry WHERE category = :category")
    suspend fun getByCategory(category: String): MutableList<Entry>

    @Query("SELECT * FROM entry ORDER BY id DESC LIMIT 1")
    suspend fun getLastById(): Entry

    @Insert
    suspend fun insert(entry: Entry)

    @Delete
    suspend fun delete(entry: Entry)
}