package dev.beriashvili.exams.lovecraftlibrary.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Manuscript(
    @PrimaryKey(autoGenerate = false) val id: Int,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "text_size") var textSize: Float
) : Parcelable