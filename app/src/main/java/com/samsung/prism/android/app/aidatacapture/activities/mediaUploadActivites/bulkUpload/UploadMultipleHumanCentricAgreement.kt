package com.samsung.prism.android.app.aidatacapture.activities.mediaUploadActivites.bulkUpload

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.UserFeedBackActivity
import com.samsung.prism.android.app.aidatacapture.activities.mediaUploadActivites.bulkUpload.UploadMultipleHumanCentricAgreement
import com.samsung.prism.android.app.aidatacapture.activities.mediaUploadActivites.captureAndUpload.UploadHumanCentricAgreementActivity
import com.samsung.prism.android.app.aidatacapture.adapters.MyAdapter
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.others.fileUploaders.MultipleAgreementUploader
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import java.io.File
import java.util.*

class UploadMultipleHumanCentricAgreement : AppCompatActivity() {
    private val files = ArrayList<String>()
    private val exit = false
    private var listView: ListView? = null
    private var mProgressBar: ProgressBar? = null
    private lateinit var btnChoose: MaterialButton
    private lateinit var btnUpload: MaterialButton
    private var arrayList: ArrayList<Uri?>? = null
    private var sharedPrefsManager: SharedPrefsManager? = null
    private var pDialog: ProgressDialog? = null
    private var context: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_image_upload)
        sharedPrefsManager = SharedPrefsManager(this)
        context = this
        pDialog = ProgressDialog(this)
        backButton()
        if (Build.VERSION.SDK_INT > 9) {
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        toolbarClick()
        listView = findViewById(R.id.listView)
        mProgressBar = findViewById(R.id.progressBar)
        btnChoose = findViewById(R.id.btnChoose)
        btnChoose.setOnClickListener { showChooser() }
        btnUpload = findViewById(R.id.btnUpload)
        btnUpload.setOnClickListener {
            if (files.size > 0) {
                uploadFiles()
            }
        }
        arrayList = ArrayList()
    }

    private fun showChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, Constants.REQUEST_CODE_READ_STORAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        btnUpload!!.visibility = View.VISIBLE
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE_READ_STORAGE) {
                if (resultData != null) {
                    if (resultData.clipData != null) {
                        val count = resultData.clipData!!.itemCount
                        val flag: Boolean
                        flag = count > 1
                        if (flag) {
                            btnUpload!!.visibility = View.VISIBLE
                            btnChoose!!.text = "Add More"
                            //btnChoose.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(
                                this,
                                R.string.select_two_or_more_images,
                                Toast.LENGTH_SHORT
                            ).show()
                            btnUpload!!.visibility = View.VISIBLE
                            btnChoose!!.visibility = View.VISIBLE
                        }
                        var currentItem = 0
                        while (currentItem < count) {
                            val imageUri = resultData.clipData!!.getItemAt(currentItem).uri
                            currentItem = currentItem + 1
                            getImageFilePath(imageUri)
                            Log.d("Uri Selected", imageUri.toString())
                            try {
                                arrayList!!.add(imageUri)
                                val mAdapter =
                                    MyAdapter(this@UploadMultipleHumanCentricAgreement, arrayList)
                                listView!!.adapter = mAdapter
                            } catch (e: Exception) {
                                Log.e(TAG, "File select error", e)
                            }
                        }
                    } else if (resultData.data != null) {
                        val uri = resultData.data
                        Log.i(TAG, "Uri = " + uri.toString())
                        try {
                            arrayList!!.add(uri)
                            val mAdapter =
                                MyAdapter(this@UploadMultipleHumanCentricAgreement, arrayList)
                            listView!!.adapter = mAdapter
                        } catch (e: Exception) {
                            Log.e(TAG, "File select error", e)
                        }
                    }
                }
            }
        }
    }

    fun getImageFilePath(uri: Uri) {
        val file = File(uri.path)
        val filePath = file.path.split(":").toTypedArray()
        val image_id = filePath[filePath.size - 1]
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Images.Media._ID + " = ? ",
            arrayOf(image_id),
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            files.add(imagePath)
            cursor.close()
        }
    }

    fun uploadFiles() {
        val filesToUpload = arrayOfNulls<File>(files.size)
        for (i in files.indices) {
            filesToUpload[i] = File(files[i])
        }
        showProgress(getString(R.string.uploading_media))
        val fileUploader = MultipleAgreementUploader()
        fileUploader.uploadFiles(
            "/",
            "file",
            filesToUpload,
            object : MultipleAgreementUploader.FileUploaderCallback {
                override fun onError() {
                    hideProgress()
                }

                override fun onFinish(responses: Array<String>) {
                    hideProgress()
                    for (i in responses.indices) {
                        val str = responses[i]
                        //   Log.e("RESPONSE "+i, responses[i]);
                    }
                }

                override fun onProgressUpdate(
                    currentpercent: Int,
                    totalpercent: Int,
                    filenumber: Int
                ) {
                    updateProgress(
                        totalpercent,
                        getString(R.string.total_images_uploaded),
                        "Total Uploaded Consents :$filenumber"
                    )
                    Log.e("Progress Status", "$currentpercent $totalpercent $filenumber")
                }
            },
            this
        )
    }

    fun updateProgress(`val`: Int, title: String?, msg: String?) {
        pDialog!!.setTitle(title)
        pDialog!!.setMessage(msg)
        pDialog!!.progress = `val`
    }

    fun showProgress(str: String?) {
        try {
            pDialog!!.setCancelable(false)
            pDialog!!.setTitle("Please wait")
            pDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            pDialog!!.max = 100 // Progress Dialog Max Value
            pDialog!!.setMessage(str)
            if (pDialog!!.isShowing) pDialog!!.dismiss()
            pDialog!!.show()
        } catch (e: Exception) {
        }
    }

    fun hideProgress() {
        try {
            if (pDialog!!.isShowing) pDialog!!.dismiss()
        } catch (e: Exception) {
        }
    }

    private fun toolbarClick() {
        val feedback: ImageView
        val logout: ImageView
        feedback = findViewById(R.id.toolbar_feedback)
        logout = findViewById(R.id.toolbar_logout)
        feedback.setOnClickListener {
            startActivity(
                Intent(
                    this@UploadMultipleHumanCentricAgreement,
                    UserFeedBackActivity::class.java
                )
            )
            finish()
        }
        logout.setOnClickListener {
            val dialogue = Dialogues()
            dialogue.logoutDialogue(context!!)
        }
        feedback.setOnLongClickListener {
            Toast.makeText(context, getString(R.string.feedback_msg), Toast.LENGTH_SHORT).show()
            true
        }
        logout.setOnLongClickListener {
            Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show()
            false
        }
    }

    override fun onBackPressed() {
        startActivity(
            Intent(
                this@UploadMultipleHumanCentricAgreement,
                UploadHumanCentricAgreementActivity::class.java
            )
        )
        finish()
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(
                Intent(
                    this@UploadMultipleHumanCentricAgreement,
                    UploadHumanCentricAgreementActivity::class.java
                )
            )
            finish()
        }
    }

    companion object {
        private val TAG = UploadMultipleHumanCentricAgreement::class.java.simpleName
    }
}