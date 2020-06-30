package dev.beriashvili.exams.lovecraftlibrary.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import dev.beriashvili.exams.lovecraftlibrary.R
import dev.beriashvili.exams.lovecraftlibrary.adapters.LibraryRecyclerViewAdapter
import dev.beriashvili.exams.lovecraftlibrary.models.Entry
import dev.beriashvili.exams.lovecraftlibrary.networking.HttpClient
import dev.beriashvili.exams.lovecraftlibrary.networking.RequestCallback
import kotlinx.android.synthetic.main.activity_library.*

class LibraryActivity : AppCompatActivity() {
    private var entries = listOf<Entry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        init()
    }

    private fun init() {
        swipeRefreshLayout.isRefreshing = true

        fetchEntries()

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true

            fetchEntries()
        }
    }

    private fun fetchEntries() {
        HttpClient.get("texts", object : RequestCallback {
            override fun onError(throwable: Throwable) {
                Toast.makeText(this@LibraryActivity, throwable.message, Toast.LENGTH_SHORT)
                    .show()

                swipeRefreshLayout.isRefreshing = false
            }

            override fun onSuccess(response: String) {
                entries =
                    Gson().fromJson(response, arrayOf<Entry>()::class.java).toList()

                apartmentRecyclerView.adapter =
                    LibraryRecyclerViewAdapter(entries, this@LibraryActivity)

                swipeRefreshLayout.isRefreshing = false
            }
        })
    }
}