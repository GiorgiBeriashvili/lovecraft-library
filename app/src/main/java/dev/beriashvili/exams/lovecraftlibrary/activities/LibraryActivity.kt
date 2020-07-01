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
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import com.google.gson.Gson
import dev.beriashvili.exams.lovecraftlibrary.R
import dev.beriashvili.exams.lovecraftlibrary.adapters.LibraryRecyclerViewAdapter
import dev.beriashvili.exams.lovecraftlibrary.models.Entry
import dev.beriashvili.exams.lovecraftlibrary.models.Url
import dev.beriashvili.exams.lovecraftlibrary.networking.HttpClient
import dev.beriashvili.exams.lovecraftlibrary.networking.RequestCallback
import dev.beriashvili.exams.lovecraftlibrary.ui.UI
import dev.beriashvili.exams.lovecraftlibrary.utils.Constants
import kotlinx.android.synthetic.main.activity_library.*

class LibraryActivity : AppCompatActivity() {
    private var entries = listOf<Entry>()
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var libraryRecyclerViewAdapter: LibraryRecyclerViewAdapter

    private val librarySharedPreferences by lazy {
        getSharedPreferences("library", Context.MODE_PRIVATE)
    }

    private val settingsSharedPreferences by lazy {
        getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    private val librarySharedPreferencesEditor by lazy {
        librarySharedPreferences.edit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.library_menu, menu)

        val searchItem = menu?.findItem(R.id.searchItem)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                libraryRecyclerViewAdapter.filter.filter(newText)

                return true
            }
        })

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

        loadPreferences()

        initializeDrawer()

        librarySwipeRefreshLayout.isRefreshing = true

        fetchEntries()

        librarySwipeRefreshLayout.setOnRefreshListener {
            librarySwipeRefreshLayout.isRefreshing = true

            fetchEntries()
        }
    }

    private fun loadPreferences() {
        when (settingsSharedPreferences.getString("theme_mode", "Day")) {
            "Day" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "Night" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        Url.apply {
            category = librarySharedPreferences.getString("category", "All")!!
            sort = librarySharedPreferences.getString("sort", "Ascending")!!
        }
    }

    private fun initializeDrawer() {
        actionBarDrawerToggle =
            ActionBarDrawerToggle(
                this,
                libraryDrawerLayout,
                R.string.open_drawer,
                R.string.close_drawer
            )

        libraryDrawerLayout.addDrawerListener(actionBarDrawerToggle)

        actionBarDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        libraryNavigationView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.libraryItem -> {
                    libraryDrawerLayout.closeDrawers()

                    librarySwipeRefreshLayout.isRefreshing = true

                    fetchEntries()
                }
                R.id.archiveItem -> Toast.makeText(this, item.title, Toast.LENGTH_LONG).show()
                R.id.aboutItem -> Toast.makeText(this, item.title, Toast.LENGTH_LONG).show()
                R.id.lovecraftItem -> Toast.makeText(this, item.title, Toast.LENGTH_LONG).show()
                R.id.settingsItem -> UI.switchThemeMode(this)
            }

            true
        }
    }

    private fun fetchEntries() {
        HttpClient.get(Url.get(), object : RequestCallback {
            override fun onError(throwable: Throwable) {
                Toast.makeText(this@LibraryActivity, throwable.message, Toast.LENGTH_SHORT)
                    .show()

                librarySwipeRefreshLayout.isRefreshing = false
            }

            override fun onSuccess(response: String) {
                entries =
                    Gson().fromJson(response, arrayOf<Entry>()::class.java).toList()

                libraryRecyclerViewAdapter =
                    LibraryRecyclerViewAdapter(entries, this@LibraryActivity)
                libraryRecyclerView.adapter = libraryRecyclerViewAdapter

                librarySwipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun handleAppBarItem(item: MenuItem) {
        when (item.itemId) {
            R.id.filterItem -> AlertDialog.Builder(this)
                .setTitle("Categorize")
                .setIcon(R.drawable.ic_baseline_filter_list_24)
                .setSingleChoiceItems(
                    Constants.CATEGORIES,
                    Constants.CATEGORIES.indexOf(Url.category)
                ) { _: DialogInterface?, index: Int ->
                    Url.category = Constants.CATEGORIES[index]
                }
                .setPositiveButton("Confirm") { _, _ ->
                    librarySharedPreferencesEditor.apply {
                        putString("category", Url.category)

                        apply()
                    }

                    fetchEntries()
                }
                .setNegativeButton("Dismiss") { _, _ ->
                    Url.category = librarySharedPreferences.getString("category", "All")!!
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
                    librarySharedPreferencesEditor.apply {
                        putString("sort", Url.sort)

                        apply()
                    }

                    fetchEntries()
                }
                .setNegativeButton("Dismiss") { _, _ ->
                    Url.sort = librarySharedPreferences.getString("sort", "Ascending")!!
                }
                .create()
                .show()
        }
    }
}