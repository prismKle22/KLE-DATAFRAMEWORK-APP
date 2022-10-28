package com.samsung.prism.android.app.aidatacapture.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.google.android.material.button.MaterialButton
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.metaData.MetaDataHumanCentricActivity
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.IntentConstants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.dialogues.ProgressDialog
import com.samsung.prism.android.app.aidatacapture.others.AndroidMultiPartEntity
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import com.samsung.prism.android.app.aidatacapture.utils.FileOperations
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.williamww.silkysignature.views.SignaturePad
import com.williamww.silkysignature.views.SignaturePad.OnSignedListener
import kotlinx.android.synthetic.main.activity_signing_canvas.*
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.ssl.SSLSocketFactory
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.entity.mime.content.StringBody
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import java.io.File
import java.io.IOException


class SigningCanvasActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener,
    OnPageErrorListener{
    private lateinit var mSilkySignaturePad: SignaturePad
    private lateinit var mClearButton: MaterialButton
    private lateinit var mSaveButton: MaterialButton
    private var image: ImageView? = null
    private var progressDialog: ProgressDialog? = null
    private var context: Context? = null
    private var sharedPrefsManager: SharedPrefsManager? = null
    private var customText: TextView? = null
    private var totalSize: Long = 0
    var SAMPLE_FILE: String? = "terms.pdf"
    private var pdfView: PDFView? = null
    private val uri: Uri? = null
    private var pageNumber = 0
    private var pdfFileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signing_canvas)
        sharedPrefsManager =
            SharedPrefsManager(
                this
            )
        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        context = this
        mSilkySignaturePad = findViewById(R.id.signature_pad)
        customText = findViewById(R.id.signature_pad_description)
        mSilkySignaturePad.setOnSignedListener(object : OnSignedListener {
            override fun onStartSigning() {
                sign_here_text.text = "Sign here"
                sign_here_text.setTextColor(resources.getColor(R.color.holo_red))
            }

            override fun onSigned() {
                mSaveButton.isEnabled = true
                mClearButton.isEnabled = true
            }

            override fun onClear() {
                mSaveButton.isEnabled = false
                mClearButton.isEnabled = false
            }
        })
        mClearButton = findViewById(R.id.clear_button)
        mSaveButton = findViewById(R.id.save_button)
        image = findViewById(R.id.imageView)
        mClearButton!!.setOnClickListener(View.OnClickListener { mSilkySignaturePad.clear() })
        mSaveButton.setOnClickListener {
            showDialogue(this, getString(R.string.please_wait_text))
            val signatureBitmap = mSilkySignaturePad.signatureBitmap
            Thread {
                sendSignatureToServer(signatureBitmap);
            }.start()
        }

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
        Log.e(SigningCanvasActivity.TAG, "Cannot load page $page")
    }

    companion object {
        private val TAG = PrivacyPolicyActivity::class.java.simpleName
    }


   fun sendSignatureToServer(signatureBitmap: Bitmap) {
        val fop = FileOperations()
        context?.let { fop.write("sample", signatureBitmap, it) }
        if (context?.let { fop.write("sample", signatureBitmap, it) } == true) {
            if (sendFileToServer() == "true") {
                object : Thread() {
                    override fun run() {
                        this@SigningCanvasActivity.runOnUiThread(Runnable {
                            dialogue()
                        })
                    }
                }.start()
                progressDialog!!.dismiss()
            } else {
                object : Thread() {
                    override fun run() {
                        this@SigningCanvasActivity.runOnUiThread(Runnable {
                            Toast.makeText(
                                context,
                                getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                    }
                }.start()
                progressDialog!!.dismiss()
            }
        } else {
            Toast.makeText(
                applicationContext, "I/O error",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showDialogue(context: Context?, message: String) {
        progressDialog = ProgressDialog((context)!!, message)
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }


    fun dialogue() {
        val mDialog = MaterialDialog.Builder(this)
            .setTitle(getString(R.string.upload_suceess_dialogue_message))
            .setMessage(getString(R.string.upload_more_messgae))
            .setCancelable(false)
            .setPositiveButton("Yes?", R.drawable.ic_baseline_check_24) { dialogInterface, which ->
                startActivity(Intent(this@SigningCanvasActivity, SignConsentActivity::class.java))
                finish()
            }
            .setNegativeButton("No", R.drawable.ic_baseline_cancel_24) { dialogInterface, which ->
                startActivity(
                    Intent(
                        this@SigningCanvasActivity,
                        MetaDataHumanCentricActivity::class.java
                    )
                )
                finish()
            }
            .build()
        mDialog.show()
    }

    private fun sendFileToServer(): String {
        val file = File(Constants.SDCARD_PATH + "/sample.pdf")
        var responseString: String? = null
        val email: String = sharedPrefsManager!!.getStringValue(
            SharedPrefsConstants.USER_EMAIL,
            Constants.SHARED_PREF_ADMIN_EMAIL
        )!!
        val Category: String =
            sharedPrefsManager!!.getStringValue(SharedPrefsConstants.CATEGORY_NAME, "Other")!!
        val subCategory: String =
            sharedPrefsManager!!.getStringValue(SharedPrefsConstants.SUBCATEGORY_NAME, "Other")!!
        val consentEmail: String =
            sharedPrefsManager!!.getStringValue(
                SharedPrefsConstants.CONSENT_EMAIL,
                ""
            )!!;
        val consetUserName: String =
            sharedPrefsManager?.getStringValue(SharedPrefsConstants.CONSENT_NAME, "User Name")!!
        val httpclient: HttpClient = DefaultHttpClient()
        val httppost = HttpPost(Constants.HUMAN_CENTRIC_AGREEMENT_UPLOAD_URL)
        try {
            val entity = AndroidMultiPartEntity { }
            entity.addPart("image", FileBody(file))
            entity.addPart(SharedPrefsConstants.CATEGORY, StringBody(Category))
            entity.addPart(Constants.SUB_CATEGORY, StringBody(subCategory))
            entity.addPart(Constants.USER_NAME, StringBody(email))
            entity.addPart(SharedPrefsConstants.CONSENT_EMAIL, StringBody(consentEmail))
            entity.addPart(SharedPrefsConstants.CONSENT_NAME, StringBody(consetUserName))
            httpclient.connectionManager.schemeRegistry.register(
                Scheme("https", SSLSocketFactory.getSocketFactory(), 443)
            )
            totalSize = entity.contentLength
            httppost.entity = entity
            val response = httpclient.execute(httppost)
            val r_entity = response.entity
            val statusCode = response.statusLine.statusCode
            responseString = if (statusCode == 200) {
                // Server response
                EntityUtils.toString(r_entity)
            } else {
                ("Error occurred! Http Status Code: "
                        + statusCode)
            }
        } catch (e: ClientProtocolException) {
            responseString = e.toString()
        } catch (e: IOException) {
            responseString = e.toString()
        }
        return responseString!!.trim { it <= ' ' }
    }


    override fun onBackPressed() {
        startActivity(Intent(this@SigningCanvasActivity, SignConsentActivity::class.java))
        finish()
    }

}