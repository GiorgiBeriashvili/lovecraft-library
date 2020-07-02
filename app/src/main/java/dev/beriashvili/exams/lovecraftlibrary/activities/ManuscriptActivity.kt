package dev.beriashvili.exams.lovecraftlibrary.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import dev.beriashvili.exams.lovecraftlibrary.R
import dev.beriashvili.exams.lovecraftlibrary.models.Manuscript
import dev.beriashvili.exams.lovecraftlibrary.models.Url
import dev.beriashvili.exams.lovecraftlibrary.networking.HttpClient
import dev.beriashvili.exams.lovecraftlibrary.networking.RequestCallback
import dev.beriashvili.exams.lovecraftlibrary.ui.UI
import kotlinx.android.synthetic.main.activity_manuscript.*

class ManuscriptActivity : AppCompatActivity() {
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var manuscript: Manuscript
    private var textSize = 14f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manuscript)

        init()
    }

    private fun init() {
        supportActionBar?.title = "Manuscript"

        initializeDrawer()

        fetchManuscript()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.manuscript_menu, menu)

        return true
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
                R.id.archiveItem -> Toast.makeText(this, item.title, Toast.LENGTH_LONG).show()
                R.id.aboutItem -> UI.displayAboutInformation(this)
                R.id.lovecraftItem -> startActivity(Intent(this, LovecraftActivity::class.java))
                R.id.settingsItem -> UI.switchThemeMode(this)
            }

            true
        }
    }

    private fun fetchManuscript() {
        manuscriptSwipeRefreshLayout.isRefreshing = true

        val id = intent.getStringExtra("id")

        HttpClient.get("${Url.basePath}/$id", object : RequestCallback {
            override fun onError(throwable: Throwable) {
                Toast.makeText(this@ManuscriptActivity, throwable.message, Toast.LENGTH_SHORT)
                    .show()

                manuscriptSwipeRefreshLayout.isEnabled = false
            }

            override fun onSuccess(response: String) {
                manuscript = Gson().fromJson(response, Manuscript::class.java)

                manuscriptTitleTextView.text = manuscript.title
                manuscriptContentTextView.text = manuscript.content

                manuscriptSwipeRefreshLayout.isEnabled = false
            }
        })
    }

    private fun handleAppBarItem(item: MenuItem) {
        when (item.itemId) {
            R.id.zoomInItem -> if (textSize <= 20f) {
                textSize += 1f

                manuscriptContentTextView.textSize = textSize
            }
            R.id.zoomOutItem -> if (textSize >= 12f) {
                textSize -= 1f

                manuscriptContentTextView.textSize = textSize
            }
            R.id.saveItem -> Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
        }
    }
}