package com.samsung.prism.android.app.aidatacapture.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.accountRelatedActivities.UserLoginActivity
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import com.samsung.prism.android.app.aidatacapture.utils.LanguageSettings

class SplashScreenActivity : AppCompatActivity() {
    private val SPLASH_DISPLAY_LENGTH = 4000
    private lateinit var sharedPrefsManager: SharedPrefsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        init()
        initSplashScreen()
    }

    private fun initSplashScreen() {
        Handler().postDelayed({
            val mainIntent = Intent(this@SplashScreenActivity, PermissionActivity::class.java)
            this@SplashScreenActivity.startActivity(mainIntent)
            sharedPrefsManager.saveBoolValue(SharedPrefsConstants.IS_FROM_DASHBOARD, false)
            LanguageSettings().setLocale(this, "en")
            finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }


    private fun init() {
        sharedPrefsManager =
            SharedPrefsManager(
                this
            )
    }
}