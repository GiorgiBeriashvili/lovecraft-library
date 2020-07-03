package dev.beriashvili.exams.lovecraftlibrary.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import dev.beriashvili.exams.lovecraftlibrary.R
import dev.beriashvili.exams.lovecraftlibrary.adapters.ArchiveRecyclerViewAdapter
import dev.beriashvili.exams.lovecraftlibrary.database.LovecraftDatabase
import dev.beriashvili.exams.lovecraftlibrary.models.Entry
import dev.beriashvili.exams.lovecraftlibrary.models.Query
import dev.beriashvili.exams.lovecraftlibrary.ui.UI
import dev.beriashvili.exams.lovecraftlibrary.utils.Constants
import kotlinx.android.synthetic.main.activity_archive.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ArchiveActivity : AppCompatActivity() {
    private val lovecraftDatabase: LovecraftDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            LovecraftDatabase::class.java, "lovecraft"
        ).build()
    }

    private val archiveSharedPreferences by lazy {
        getSharedPreferences("archive", Context.MODE_PRIVATE)
    }

    private val archiveSharedPreferencesEditor by lazy {
        archiveSharedPreferences.edit()
    }

    private val entries = mutableListOf<Entry>()
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var archiveRecyclerViewAdapter: ArchiveRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)

        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.archive_menu, menu)

        val searchItem = menu?.findItem(R.id.archiveSearchItem)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                archiveRecyclerViewAdapter.filter.filter(newText)

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
        supportActionBar?.title = "Archive"

        loadPreferences()

        initializeDrawer()

        archiveSwipeRefreshLayout.isRefreshing = true

        loadEntries()

        archiveSwipeRefreshLayout.setOnRefreshListener {
            archiveSwipeRefreshLayout.isRefreshing = true

            loadEntries()
        }
    }

    private fun loadPreferences() {
        Query.apply {
            category = archiveSharedPreferences.getString("category", "All")!!
            sort = archiveSharedPreferences.getString("sort", "Ascending")!!
        }
    }

    private fun initializeDrawer() {
        actionBarDrawerToggle =
            ActionBarDrawerToggle(
                this,
                archiveDrawerLayout,
                R.string.open_drawer,
                R.string.close_drawer
            )

        archiveDrawerLayout.addDrawerListener(actionBarDrawerToggle)

        actionBarDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        archiveNavigationView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.libraryItem -> startActivity(Intent(this, LibraryActivity::class.java))
                R.id.archiveItem -> {
                    archiveDrawerLayout.closeDrawers()

                    archiveSwipeRefreshLayout.isRefreshing = true

                    loadEntries()
                }
                R.id.aboutItem -> UI.displayAboutInformation(this)
                R.id.lovecraftItem -> startActivity(Intent(this, LovecraftActivity::class.java))
                R.id.themeModeItem -> UI.switchThemeMode(this)
            }

            true
        }
    }

    private fun loadEntries() {
        entries.clear()

        CoroutineScope(Dispatchers.IO).launch {
            when (Query.category) {
                "All" -> entries.addAll(lovecraftDatabase.entryDao().getAll())
                else -> entries.addAll(lovecraftDatabase.entryDao().getByCategory(Query.category))
            }

            when (Query.sort) {
                "Ascending" -> entries.sortBy { entry -> entry.id }
                "Descending" -> entries.sortByDescending { entry -> entry.id }
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            delay(100)

            archiveRecyclerView.layoutManager = LinearLayoutManager(this@ArchiveActivity)
            archiveRecyclerViewAdapter = ArchiveRecyclerViewAdapter(entries, this@ArchiveActivity)
            archiveRecyclerView.adapter = archiveRecyclerViewAdapter

            archiveSwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun handleAppBarItem(item: MenuItem) {
        when (item.itemId) {
            R.id.archiveFilterItem -> AlertDialog.Builder(this, R.style.AlertDialog)
                .setTitle("Categorize")
                .setIcon(R.drawable.ic_baseline_filter_list_24)
                .setSingleChoiceItems(
                    Constants.CATEGORIES,
                    Constants.CATEGORIES.indexOf(Query.category)
                ) { _: DialogInterface?, index: Int ->
                    Query.category = Constants.CATEGORIES[index]
                }
                .setPositiveButton("Confirm") { _, _ ->
                    archiveSharedPreferencesEditor.apply {
                        putString("category", Query.category)

                        apply()
                    }

                    loadEntries()
                }
                .setNegativeButton("Dismiss") { _, _ ->
                    Query.category = archiveSharedPreferences.getString("category", "All")!!
                }
                .create()
                .show()
            R.id.archiveSortItem -> AlertDialog.Builder(this, R.style.AlertDialog)
                .setTitle("Sort")
                .setIcon(R.drawable.ic_baseline_sort_24)
                .setSingleChoiceItems(
                    Constants.SORT,
                    Constants.SORT.indexOf(Query.sort)
                ) { _: DialogInterface?, index: Int ->
                    Query.sort = Constants.SORT[index]
                }
                .setPositiveButton("Confirm") { _, _ ->
                    archiveSharedPreferencesEditor.apply {
                        putString("sort", Query.sort)

                        apply()
                    }

                    loadEntries()
                }
                .setNegativeButton("Dismiss") { _, _ ->
                    Query.sort = archiveSharedPreferences.getString("sort", "Ascending")!!
                }
                .create()
                .show()
        }
    }
}