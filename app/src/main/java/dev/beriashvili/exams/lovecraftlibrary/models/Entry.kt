package dev.beriashvili.exams.lovecraftlibrary.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Entry(
    @PrimaryKey(autoGenerate = false) val id: Int,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String
) : Parcelable