package dev.beriashvili.exams.lovecraftlibrary.activities

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import dev.beriashvili.exams.lovecraftlibrary.R
import dev.beriashvili.exams.lovecraftlibrary.utils.Constants
import kotlinx.android.synthetic.main.activity_lovecraft.*

class LovecraftActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lovecraft)

        init()
    }

    private fun init() {
        supportActionBar?.title = "H. P. Lovecraft"

        lovecraftWebView.webViewClient = WebViewClient()

        lovecraftWebView.loadUrl(Constants.LOVECRAFT_WIKIPEDIA)
    }

    override fun onBackPressed() {
        if (lovecraftWebView.canGoBack()) {
            lovecraftWebView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}