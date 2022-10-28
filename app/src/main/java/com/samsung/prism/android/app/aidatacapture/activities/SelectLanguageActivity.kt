package com.samsung.prism.android.app.aidatacapture.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.databinding.ActivitySelectLanguageBinding
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import com.samsung.prism.android.app.aidatacapture.utils.LanguageSettings


class SelectLanguageActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectLanguageBinding
    private lateinit var sharedPrefsManager: SharedPrefsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        backButton()
        toolbarClick()
    }

    private fun init() {
        sharedPrefsManager =
            SharedPrefsManager(
                this
            )
        if (sharedPrefsManager.getBoolValue(SharedPrefsConstants.IS_LANGUAGE_SET, false)) {
            LanguageSettings().setLocale(
                this,
                sharedPrefsManager.getStringValue(SharedPrefsConstants.LANGUAGE_SELECTED, "en")
            )
            if (sharedPrefsManager.getBoolValue(SharedPrefsConstants.IS_FROM_DASHBOARD, false)) {
                startActivity(Intent(Activity@ this, UserDashboard::class.java))
            } else {
                startActivity(Intent(this@SelectLanguageActivity, PermissionActivity::class.java))
            }
        } else {
            onClickListeners()
        }
    }

    private fun onClickListeners() {
        binding.kannadaLanguage.setOnClickListener {
            LanguageSettings().setLocale(this, "kn")
            sharedPrefsManager.saveStringValue(SharedPrefsConstants.LANGUAGE_SELECTED, "kn")
            sharedPrefsManager.saveBoolValue(SharedPrefsConstants.IS_LANGUAGE_SET, true)
            if (sharedPrefsManager.getBoolValue(SharedPrefsConstants.IS_FROM_DASHBOARD, false)) {
                startActivity(Intent(Activity@ this, UserDashboard::class.java))
            } else {
                startActivity(Intent(this@SelectLanguageActivity, PermissionActivity::class.java))
            }
        }
        binding.hindiLanguage.setOnClickListener {
            LanguageSettings().setLocale(this, "hi")
            sharedPrefsManager.saveStringValue(SharedPrefsConstants.LANGUAGE_SELECTED, "hi")
            sharedPrefsManager.saveBoolValue(SharedPrefsConstants.IS_LANGUAGE_SET, true)
            if (sharedPrefsManager.getBoolValue(SharedPrefsConstants.IS_FROM_DASHBOARD, false)) {
                startActivity(Intent(Activity@ this, UserDashboard::class.java))
            } else {
                startActivity(Intent(this@SelectLanguageActivity, PermissionActivity::class.java))
            }
        }
        binding.englishLanguage.setOnClickListener {
            sharedPrefsManager.saveStringValue(SharedPrefsConstants.LANGUAGE_SELECTED, "en")
            LanguageSettings().setLocale(this, "en")
            if (sharedPrefsManager.getBoolValue(SharedPrefsConstants.IS_FROM_DASHBOARD, false)) {
                startActivity(Intent(Activity@ this, UserDashboard::class.java))
            } else {
                startActivity(Intent(this@SelectLanguageActivity, PermissionActivity::class.java))
            }
        }

    }

    private fun backButton() {
        val backButton =
            findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(this@SelectLanguageActivity, UserDashboard::class.java))
            finish()
        }
    }

    private fun toolbarClick() {
        val feedback: ImageView = findViewById(R.id.toolbar_feedback)
        val logout: ImageView = findViewById(R.id.toolbar_logout)
        feedback.visibility = View.GONE
        logout.visibility = View.GONE
    }

}
