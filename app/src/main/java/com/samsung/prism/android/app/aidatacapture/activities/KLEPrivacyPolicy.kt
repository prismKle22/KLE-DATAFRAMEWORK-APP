package com.samsung.prism.android.app.aidatacapture.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.KLEPrivacyPolicy
import com.samsung.prism.android.app.aidatacapture.activities.mediaUploadActivites.captureAndUpload.UploadAgreementActivity
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.Constants.SAMPLE_FILE

class KLEPrivacyPolicy : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener,
    OnPageErrorListener {
    private var pdfView: PDFView? = null
    private val uri: Uri? = null
    private var pageNumber = 0
    private var pdfFileName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        pdfView = findViewById(R.id.pdfView)
        displayFromAsset(Constants.KLETECH_PRIVACY_POLICY)
        backButton()
        toolbarClick()
    }

    private fun displayFromAsset(assetFileName: String) {
        pdfFileName = assetFileName
        pdfView!!.fromAsset(SAMPLE_FILE)
            .defaultPage(pageNumber)
            .onPageChange(this)
            .enableAnnotationRendering(true)
            .onLoad(this)
            .scrollHandle(DefaultScrollHandle(this))
            .spacing(10) // in dp
            .onPageError(this)
            .load()
    }

    override fun loadComplete(nbPages: Int) {
        val meta = pdfView!!.documentMeta
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        pageNumber = page
        title = String.format("%s %s / %s", pdfFileName, page + 1, pageCount)
    }

    override fun onPageError(page: Int, t: Throwable) {
        Log.e(TAG, "Cannot load page $page")
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(this@KLEPrivacyPolicy, UploadAgreementActivity::class.java))
            finish()
        }
    }

    private fun toolbarClick() {
        val feedback: ImageView = findViewById(R.id.toolbar_feedback)
        val logout: ImageView = findViewById(R.id.toolbar_logout)
        feedback.visibility = View.GONE
        logout.visibility = View.GONE
    }

    companion object {
        private val TAG = KLEPrivacyPolicy::class.java.simpleName
    }
}