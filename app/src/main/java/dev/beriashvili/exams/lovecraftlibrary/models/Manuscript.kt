package dev.beriashvili.exams.lovecraftlibrary.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Manuscript(
    val id: String,
    val category: String,
    val title: String,
    val content: String
) : Parcelable