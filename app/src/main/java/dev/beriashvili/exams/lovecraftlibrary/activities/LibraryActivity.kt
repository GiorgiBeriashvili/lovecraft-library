package dev.beriashvili.exams.lovecraftlibrary.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
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
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        when (item.itemId) {
            R.id.searchItem -> Toast.makeText(this, item.title, Toast.LENGTH_LONG).show()
            R.id.filterItem -> Toast.makeText(this, item.title, Toast.LENGTH_LONG).show()
        }

        return true
    }

    private fun init() {
        initializeDrawer()

        swipeRefreshLayout.isRefreshing = true

        fetchEntries()

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true

            fetchEntries()
        }
    }

    private fun initializeDrawer() {
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        actionBarDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.aboutItem -> Toast.makeText(this, item.title, Toast.LENGTH_LONG).show()
                R.id.lovecraftItem -> Toast.makeText(this, item.title, Toast.LENGTH_LONG).show()
                R.id.settingsItem -> Toast.makeText(this, item.title, Toast.LENGTH_LONG).show()
            }

            true
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