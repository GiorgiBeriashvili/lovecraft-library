package dev.beriashvili.exams.lovecraftlibrary.models

object Url {
    val basePath = "texts"
    var category = "All"
    var sort = "Ascending"

    fun get(): String {
        return "$basePath?category=$category&sort=$sort"
    }
}