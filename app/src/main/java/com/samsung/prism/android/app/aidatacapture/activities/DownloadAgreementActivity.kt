package com.samsung.prism.android.app.aidatacapture.activities

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.accountRelatedActivities.UserRegistration
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.Constants.BASE_URL
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager

class DownloadAgreementActivity : AppCompatActivity() {
    private lateinit var downloadAgreement: MaterialCardView
    private lateinit var downloadConsent: MaterialCardView
    private lateinit var btnNext: Button
    private var sharedPrefsManager: SharedPrefsManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_agreement)
        isDownloadManagerAvailable(this)
        sharedPrefsManager = SharedPrefsManager(this)
        init()
        backButton()
        toolbarClick()
    }

    private fun init() {
        downloadAgreement = findViewById(R.id.btnDownloadAgreement)
        downloadConsent = findViewById(R.id.btnDownloadConsent)
        btnNext = findViewById(R.id.btnNext)
        btnNext.setOnClickListener(View.OnClickListener {
            if (sharedPrefsManager!!.getBoolValue(SharedPrefsConstants.IS_FROM_LOGIN, false)) {
                startActivity(Intent(this@DownloadAgreementActivity, UserRegistration::class.java))
                finish()
            } else {
                onBackPressed()
            }
        })
        downloadAgreement.setOnClickListener(View.OnClickListener {
            val url: String = BASE_URL + Constants.REGISTER_PDF_NAME
            val request = DownloadManager.Request(Uri.parse(url))
            request.setDescription("Some description")
            request.setTitle(getString(R.string.samsung_agreement))
            // in order for this if to run, you must use the android 3.2 to compile your app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner()
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            }
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "Samsung_Agreement.pdf"
            )
            Toast.makeText(
                this@DownloadAgreementActivity,
                R.string.file_downloaded_at,
                Toast.LENGTH_SHORT
            ).show()

            // get download service and enqueue file
            val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        })
        downloadConsent.setOnClickListener(View.OnClickListener {
            val url: String = BASE_URL + Constants.CONSENT_PDF_NAME
            val request = DownloadManager.Request(Uri.parse(url))
            request.setDescription("Some descrition")
            request.setTitle(getString(R.string.samsung_consent))
            // in order for this if to run, you must use the android 3.2 to compile your app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner()
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            }
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "Human_Centric_Consent.pdf"
            )
            Toast.makeText(
                this@DownloadAgreementActivity,
                "File Downloaded at Downloads",
                Toast.LENGTH_SHORT
            ).show()

            // get download service and enqueue file
            val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        })
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener { onBackPressed() }
    }

    private fun toolbarClick() {
        val feedback: ImageView = findViewById(R.id.toolbar_feedback)
        val logout: ImageView = findViewById(R.id.toolbar_logout)
        feedback.visibility = View.GONE
        logout.visibility = View.GONE
    }

    companion object {
        fun isDownloadManagerAvailable(context: Context?): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
        }
    }
}