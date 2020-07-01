package dev.beriashvili.exams.lovecraftlibrary.activities

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import dev.beriashvili.exams.lovecraftlibrary.R
import dev.beriashvili.exams.lovecraftlibrary.adapters.LibraryRecyclerViewAdapter
import dev.beriashvili.exams.lovecraftlibrary.models.Entry
import dev.beriashvili.exams.lovecraftlibrary.models.Url
import dev.beriashvili.exams.lovecraftlibrary.networking.HttpClient
import dev.beriashvili.exams.lovecraftlibrary.networking.RequestCallback
import dev.beriashvili.exams.lovecraftlibrary.utils.Constants
import kotlinx.android.synthetic.main.activity_library.*

class LibraryActivity : AppCompatActivity() {
    private var entries = listOf<Entry>()
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    private val sharedPreferences by lazy {
        getSharedPreferences("library", Context.MODE_PRIVATE)
    }

    private val editor by lazy {
        sharedPreferences.edit()
    }

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

        handleAppBarItem(item)

        return true
    }

    private fun init() {
        supportActionBar?.title = "Library"

        initializeDrawer()

        loadPreferences()

        swipeRefreshLayout.isRefreshing = true

        fetchEntries()

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true

            fetchEntries()
        }
    }

    private fun loadPreferences() {
        Url.category = sharedPreferences.getString("category", "All")!!
        Url.sort = sharedPreferences.getString("sort", "Ascending")!!
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
        HttpClient.get(Url.get(), object : RequestCallback {
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

    private fun handleAppBarItem(item: MenuItem) {
        when (item.itemId) {
            R.id.searchItem -> Toast.makeText(this, item.title, Toast.LENGTH_LONG).show()
            R.id.filterItem -> AlertDialog.Builder(this)
                .setTitle("Filter by Category")
                .setIcon(R.drawable.ic_baseline_filter_list_24)
                .setSingleChoiceItems(
                    Constants.CATEGORIES,
                    Constants.CATEGORIES.indexOf(Url.category)
                ) { _: DialogInterface?, index: Int ->
                    Url.category = Constants.CATEGORIES[index]
                }
                .setPositiveButton("Confirm") { _, _ ->
                    editor.apply {
                        putString("category", Url.category)

                        apply()
                    }

                    fetchEntries()
                }
                .setNegativeButton("Dismiss") { _, _ ->
                    Url.category = sharedPreferences.getString("category", "All")!!
                }
                .create()
                .show()
            R.id.sortItem -> AlertDialog.Builder(this)
                .setTitle("Sort")
                .setIcon(R.drawable.ic_baseline_sort_24)
                .setSingleChoiceItems(
                    Constants.SORT,
                    Constants.SORT.indexOf(Url.sort)
                ) { _: DialogInterface?, index: Int ->
                    Url.sort = Constants.SORT[index]
                }
                .setPositiveButton("Confirm") { _, _ ->
                    editor.apply {
                        putString("sort", Url.sort)

                        apply()
                    }

                    fetchEntries()
                }
                .setNegativeButton("Dismiss") { _, _ ->
                    Url.sort = sharedPreferences.getString("sort", "Ascending")!!
                }
                .create()
                .show()
        }
    }
}