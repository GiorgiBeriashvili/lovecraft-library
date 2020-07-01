package dev.beriashvili.exams.lovecraftlibrary.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.beriashvili.exams.lovecraftlibrary.R
import dev.beriashvili.exams.lovecraftlibrary.models.Manuscript
import kotlinx.android.synthetic.main.activity_manuscript.*

class ManuscriptActivity : AppCompatActivity() {
    private lateinit var manuscript: Manuscript

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manuscript)

        init()
    }

    private fun init() {
        manuscript = intent.getParcelableExtra("manuscript") as Manuscript

        intent.removeExtra("manuscript")

        manuscriptTitleTextView.text = manuscript.title
        manuscriptContentTextView.text = manuscript.content

        Toast.makeText(this, manuscript.title, Toast.LENGTH_SHORT).show()
    }
}