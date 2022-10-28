package com.samsung.prism.android.app.aidatacapture.activities

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
import com.samsung.prism.android.app.aidatacapture.constants.IntentConstants

class PrivacyPolicyActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener,
    OnPageErrorListener {
    var SAMPLE_FILE: String? = "terms.pdf"
    private var pdfView: PDFView? = null
    private val uri: Uri? = null
    private var pageNumber = 0
    private var pdfFileName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        pdfView = findViewById(R.id.pdfView)
        SAMPLE_FILE = if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
                "terms.pdf"
            } else {
                extras.getString(IntentConstants.CONSENT_FILE_NAME)
            }
        } else {
            savedInstanceState.getSerializable(IntentConstants.CONSENT_FILE_NAME) as String?
        }
        displayFromAsset("terms.pdf")
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
        backButton.setOnClickListener { onBackPressed() }
    }

    private fun toolbarClick() {
        val feedback: ImageView = findViewById(R.id.toolbar_feedback)
        val logout: ImageView = findViewById(R.id.toolbar_logout)
        feedback.visibility = View.GONE
        logout.visibility = View.GONE
    }

    companion object {
        private val TAG = PrivacyPolicyActivity::class.java.simpleName
        private const val REQUEST_CODE = 42
        const val PERMISSION_CODE = 42042
        const val READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"
    }
}