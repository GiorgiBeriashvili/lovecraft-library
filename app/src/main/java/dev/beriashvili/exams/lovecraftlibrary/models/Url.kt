package dev.beriashvili.exams.lovecraftlibrary.models

object Url {
    var category = "All"
    var sort = "Ascending"

    fun get(): String {
        return "texts?category=$category&sort=$sort"
    }
}