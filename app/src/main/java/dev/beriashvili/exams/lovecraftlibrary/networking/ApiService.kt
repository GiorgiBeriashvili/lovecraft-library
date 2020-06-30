package dev.beriashvili.exams.lovecraftlibrary.networking

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface ApiService {
    @GET
    fun get(@Url url: String): Call<String>

    @GET
    fun get(@Url url: String, @QueryMap parameters: Map<String, String>): Call<String>
}