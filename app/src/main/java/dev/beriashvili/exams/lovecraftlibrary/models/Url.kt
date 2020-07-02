package dev.beriashvili.exams.lovecraftlibrary.models

object Url {
    const val basePath = "manuscripts"
    var category = "All"
    var sort = "Ascending"

    fun get(): String {
        return "$basePath?category=$category&sort=$sort"
    }
}