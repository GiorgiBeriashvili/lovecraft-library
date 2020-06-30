package dev.beriashvili.exams.lovecraftlibrary.networking

interface RequestCallback {
    fun onError(throwable: Throwable)
    fun onSuccess(response: String)
}