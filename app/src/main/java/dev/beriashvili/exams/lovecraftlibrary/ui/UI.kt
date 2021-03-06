package dev.beriashvili.exams.lovecraftlibrary.ui

import android.content.Context
import android.content.DialogInterface
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import dev.beriashvili.exams.lovecraftlibrary.R
import dev.beriashvili.exams.lovecraftlibrary.utils.Constants

object UI {
    fun displayAboutInformation(context: Context) {
        val aboutMessageTextView = TextView(context)

        aboutMessageTextView.text = SpannableString(context.getText(R.string.about_message))
        aboutMessageTextView.movementMethod = LinkMovementMethod.getInstance()
        aboutMessageTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER

        AlertDialog.Builder(context, R.style.AlertDialog)
            .setTitle("About the Application")
            .setIcon(R.drawable.ic_baseline_info_24)
            .setMessage("Visit the following link to learn more about the application:\n\n")
            .setView(aboutMessageTextView)
            .setPositiveButton("Ok") { _, _ -> }
            .create()
            .show()
    }

    fun switchThemeMode(context: Context) {
        val settingsSharedPreferences by lazy {
            context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        }

        val settingsSharedPreferencesEditor by lazy {
            settingsSharedPreferences.edit()
        }

        var themeMode = settingsSharedPreferences.getString("theme_mode", "Light")

        AlertDialog.Builder(context, R.style.AlertDialog)
            .setTitle("Theme Mode")
            .setIcon(R.drawable.ic_baseline_settings_24)
            .setSingleChoiceItems(
                Constants.THEME_MODES,
                Constants.THEME_MODES.indexOf(themeMode)
            ) { _: DialogInterface?, index: Int ->
                themeMode = Constants.THEME_MODES[index]
            }
            .setPositiveButton("Confirm") { _, _ ->
                when (themeMode) {
                    "Light" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                        settingsSharedPreferencesEditor.apply {
                            putString("theme_mode", "Light")

                            apply()
                        }
                    }
                    "Dark" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                        settingsSharedPreferencesEditor.apply {
                            putString("theme_mode", "Dark")

                            apply()
                        }
                    }
                    "Battery Saver" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)

                        settingsSharedPreferencesEditor.apply {
                            putString("theme_mode", "Battery Saver")

                            apply()
                        }
                    }
                    "System Default" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

                        settingsSharedPreferencesEditor.apply {
                            putString("theme_mode", "System Default")

                            apply()
                        }
                    }
                }
            }
            .setNegativeButton("Dismiss") { _, _ ->
                themeMode = settingsSharedPreferences.getString("theme_mode", "Light")
            }
            .create()
            .show()
    }
}