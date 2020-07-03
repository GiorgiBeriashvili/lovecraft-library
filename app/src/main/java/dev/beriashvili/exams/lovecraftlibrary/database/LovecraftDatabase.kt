package dev.beriashvili.exams.lovecraftlibrary.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.beriashvili.exams.lovecraftlibrary.models.Entry
import dev.beriashvili.exams.lovecraftlibrary.models.Manuscript

@Database(entities = [Entry::class, Manuscript::class], version = 1, exportSchema = false)
abstract class LovecraftDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao
    abstract fun manuscriptDao(): ManuscriptDao
}