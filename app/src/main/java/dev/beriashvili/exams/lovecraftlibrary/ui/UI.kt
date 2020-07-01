package dev.beriashvili.exams.lovecraftlibrary.ui

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import dev.beriashvili.exams.lovecraftlibrary.R
import dev.beriashvili.exams.lovecraftlibrary.utils.Constants

object UI {
    fun switchThemeMode(context: Context) {
        val settingsSharedPreferences by lazy {
            context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        }

        val settingsSharedPreferencesEditor by lazy {
            settingsSharedPreferences.edit()
        }

        var themeMode = settingsSharedPreferences.getString("theme_mode", "Day")

        AlertDialog.Builder(context)
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
                    "Day" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                        settingsSharedPreferencesEditor.apply {
                            putString("theme_mode", "Day")

                            apply()
                        }
                    }
                    "Night" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                        settingsSharedPreferencesEditor.apply {
                            putString("theme_mode", "Night")

                            apply()
                        }
                    }
                }
            }
            .setNegativeButton("Dismiss") { _, _ ->
                themeMode = settingsSharedPreferences.getString("theme_mode", "Day")
            }
            .create()
            .show()
    }
}