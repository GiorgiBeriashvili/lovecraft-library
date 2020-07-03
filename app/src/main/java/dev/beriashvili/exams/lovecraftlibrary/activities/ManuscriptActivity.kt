package dev.beriashvili.exams.lovecraftlibrary.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.gson.Gson
import dev.beriashvili.exams.lovecraftlibrary.R
import dev.beriashvili.exams.lovecraftlibrary.database.LovecraftDatabase
import dev.beriashvili.exams.lovecraftlibrary.models.Entry
import dev.beriashvili.exams.lovecraftlibrary.models.Manuscript
import dev.beriashvili.exams.lovecraftlibrary.models.Query
import dev.beriashvili.exams.lovecraftlibrary.networking.HttpClient
import dev.beriashvili.exams.lovecraftlibrary.networking.RequestCallback
import dev.beriashvili.exams.lovecraftlibrary.ui.UI
import kotlinx.android.synthetic.main.activity_manuscript.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ManuscriptActivity : AppCompatActivity() {
    private val lovecraftDatabase: LovecraftDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            LovecraftDatabase::class.java, "lovecraft"
        ).build()
    }

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var entry: Entry
    private lateinit var manuscript: Manuscript
    private lateinit var optionsMenu: Menu
    private var canArchive = false
    private var canUnarchive = false
    private var isArchived = false
    private var textSize = 14f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manuscript)

        init()
    }

    private fun init() {
        supportActionBar?.title = "Manuscript"

        initializeDrawer()

        entry = intent.getParcelableExtra("entry") as Entry

        CoroutineScope(Dispatchers.IO).launch {
            isArchived = lovecraftDatabase.entryDao().exists(entry.id)

            delay(50)

            CoroutineScope(Dispatchers.Main).launch {
                if (isArchived) {
                    loadManuscript()
                } else {
                    fetchManuscript()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.manuscript_menu, menu)

        if (menu != null) {
            optionsMenu = menu
        }

        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (isArchived) {
            menu?.getItem(2)?.isVisible = false
        } else {
            menu?.getItem(3)?.isVisible = false
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        handleAppBarItem(item)

        return true
    }

    private fun initializeDrawer() {
        actionBarDrawerToggle =
            ActionBarDrawerToggle(
                this,
                manuscriptDrawerLayout,
                R.string.open_drawer,
                R.string.close_drawer
            )

        manuscriptDrawerLayout.addDrawerListener(actionBarDrawerToggle)

        actionBarDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        manuscriptNavigationView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.libraryItem -> {
                    val intent = Intent(this, LibraryActivity::class.java)

                    startActivity(intent)
                }
                R.id.archiveItem -> startActivity(Intent(this, ArchiveActivity::class.java))
                R.id.aboutItem -> UI.displayAboutInformation(this)
                R.id.lovecraftItem -> startActivity(Intent(this, LovecraftActivity::class.java))
                R.id.themeModeItem -> UI.switchThemeMode(this)
            }

            true
        }
    }

    private fun loadManuscript() {
        manuscriptSwipeRefreshLayout.isRefreshing = true

        CoroutineScope(Dispatchers.IO).launch {
            manuscript = lovecraftDatabase.manuscriptDao().getById(entry.id)

            delay(50)

            CoroutineScope(Dispatchers.Main).launch {
                manuscriptTitleTextView.text = manuscript.title
                manuscriptContentTextView.text = manuscript.content

                textSize = manuscript.textSize
                manuscriptContentTextView.textSize = textSize

                canUnarchive = true

                manuscriptSwipeRefreshLayout.isEnabled = false
            }
        }
    }

    private fun fetchManuscript() {
        manuscriptSwipeRefreshLayout.isRefreshing = true

        HttpClient.get("${Query.basePath}/${entry.id}", object : RequestCallback {
            override fun onError(throwable: Throwable) {
                Toast.makeText(this@ManuscriptActivity, throwable.message, Toast.LENGTH_SHORT)
                    .show()

                manuscriptSwipeRefreshLayout.isEnabled = false
            }

            override fun onSuccess(response: String) {
                manuscript = Gson().fromJson(response, Manuscript::class.java)

                manuscriptTitleTextView.text = manuscript.title
                manuscriptContentTextView.text = manuscript.content

                canArchive = true

                manuscriptSwipeRefreshLayout.isEnabled = false
            }
        })
    }

    private fun handleAppBarItem(item: MenuItem) {
        when (item.itemId) {
            R.id.zoomInItem -> if (this::manuscript.isInitialized && textSize <= 20f) {
                textSize += 1f

                manuscriptContentTextView.textSize = textSize

                CoroutineScope(Dispatchers.IO).launch {
                    if (lovecraftDatabase.manuscriptDao().exists(manuscript.id)) {
                        lovecraftDatabase.manuscriptDao().updateTextSize(manuscript.id, textSize)
                    }
                }
            }
            R.id.zoomOutItem -> if (this::manuscript.isInitialized && textSize >= 12f) {
                textSize -= 1f

                manuscriptContentTextView.textSize = textSize

                CoroutineScope(Dispatchers.IO).launch {
                    if (lovecraftDatabase.manuscriptDao().exists(manuscript.id)) {
                        lovecraftDatabase.manuscriptDao().updateTextSize(manuscript.id, textSize)
                    }
                }
            }
            R.id.archiveItem -> {
                if (this::manuscript.isInitialized && canArchive && !isArchived) {
                    CoroutineScope(Dispatchers.IO).launch {
                        CoroutineScope(Dispatchers.Main).launch {
                            item.isVisible = false

                            optionsMenu.getItem(3).isVisible = true

                            canUnarchive = true
                        }

                        lovecraftDatabase.entryDao().insert(entry)

                        manuscript.textSize = textSize
                        lovecraftDatabase.manuscriptDao().insert(manuscript)

                        isArchived = true

                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                this@ManuscriptActivity,
                                "${manuscript.title} has been archived successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            R.id.unarchiveItem -> {
                if (this::manuscript.isInitialized && canUnarchive && isArchived) {
                    CoroutineScope(Dispatchers.IO).launch {
                        CoroutineScope(Dispatchers.Main).launch {
                            item.isVisible = false

                            optionsMenu.getItem(2).isVisible = true

                            canArchive = true
                        }

                        lovecraftDatabase.entryDao().delete(entry)
                        lovecraftDatabase.manuscriptDao().delete(manuscript)

                        isArchived = false

                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                this@ManuscriptActivity,
                                "${manuscript.title} has been unarchived successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}