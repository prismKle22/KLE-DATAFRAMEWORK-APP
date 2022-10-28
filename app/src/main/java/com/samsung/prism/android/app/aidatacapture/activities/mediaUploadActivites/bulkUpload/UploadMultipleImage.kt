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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.Retrofit.Interfaces.MyAPI
import com.samsung.prism.android.app.aidatacapture.activities.UserFeedBackActivity
import com.samsung.prism.android.app.aidatacapture.activities.UserUploadMenuActivity
import com.samsung.prism.android.app.aidatacapture.activities.mySignConsent.*
import com.samsung.prism.android.app.aidatacapture.adapters.MyAdapter
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.Constants.MIME_TYPE_IMAGE
import com.samsung.prism.android.app.aidatacapture.constants.Constants.MIME_TYPE_TEXT
import com.samsung.prism.android.app.aidatacapture.constants.Constants.REQUEST_CODE_PICK_IMAGE
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import com.samsung.prism.android.app.aidatacapture.utils.FileUtil.getFile
import kotlinx.android.synthetic.main.activity_multiple_image_upload.*
import kotlinx.android.synthetic.main.activity_upload_captured_image.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

class UploadMultipleImage : AppCompatActivity() {
    private val files = ArrayList<String>()
    private val REQUEST_CODE_PERMISSIONS = 1
    private val REQUEST_CODE_READ_STORAGE = 2
    private var listView: ListView? = null
    private lateinit var mProgressBar: ProgressBar
    private lateinit var btnChoose: MaterialButton
    private lateinit var btnUpload: MaterialButton
    private var arrayList: ArrayList<Uri?>? = null
    private var sharedPrefsManager: SharedPrefsManager? = null
    private var pDialog: ProgressDialog? = null
    private var context: Context? = null
    private var catName: String? = null
    private var subCatName: String? = null
    private var selectedImageUri: Uri? = null
    private var categoryString: String? = null
    private var locationString: String? = null
    private var subLocationString: String?= null
    private var timingString: String?= null
    private var lightingString: String?= null
    private var myDeviceModel: String?= null
    private var screenSize: String?= null
    private var selfieString: String?= null
    private var typeString: String?= null
    private var aboveEighteenString: String?= null
    private var propsString: String?= null
    private var consentString: String?= null
    private var humanPresent: String?=null
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
        listView = findViewById(R.id.listView)
//        mProgressBar = findViewById(R.id.progressBar)
        toolbarClick()
        btnChoose = findViewById(R.id.btnChoose)
        btnChoose.setOnClickListener(View.OnClickListener { v: View? -> showChooser() })
        btnUpload = findViewById<MaterialButton>(R.id.btnUpload)
        btnUpload.setOnClickListener{
            uploadFiles()
        }
        arrayList = ArrayList()
    }

    private fun toolbarClick() {
        val feedback: ImageView
        val logout: ImageView
        feedback = findViewById(R.id.toolbar_feedback)
        logout = findViewById(R.id.toolbar_logout)
        feedback.setOnClickListener {
            startActivity(Intent(this@UploadMultipleImage, UserFeedBackActivity::class.java))
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

    private fun showChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        btnUpload.visibility = View.VISIBLE
        if (resultCode == RESULT_OK) {
            if (resultData != null) {
                if (resultData.clipData != null) {
                    val count = resultData.clipData!!.itemCount
                    if (count >= 0) {
                        btnUpload.visibility = View.VISIBLE
                        btnChoose.text = "Add More"
                        //btnChoose.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(this, "Select 2 or more Images", Toast.LENGTH_SHORT)
                                .show()
                        btnUpload.visibility = View.VISIBLE
                        btnChoose.visibility = View.VISIBLE
                    }
                    var currentItem = 0
                    while (currentItem < count) {
                        val imageUri = resultData.clipData!!.getItemAt(currentItem).uri
                        currentItem += 1

                        getImageFilePath(imageUri)

                        Log.d("Uri Selected", imageUri.toString())
                        try {
                            arrayList?.add(imageUri)
                            val mAdapter = MyAdapter(this@UploadMultipleImage, arrayList)
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
                        val mAdapter = MyAdapter(this@UploadMultipleImage, arrayList)
                        listView!!.adapter = mAdapter
                    } catch (e: Exception) {
                        Log.e(TAG, "File select error", e)
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
        var count=0
//        Toast.makeText(context, arrayList!!.indices.toString(), Toast.LENGTH_SHORT).show()

        for (i in arrayList!!.indices) {
            selectedImageUri = arrayList!!.get(i)
//            Toast.makeText(context, selectedImageUri.toString(), Toast.LENGTH_SHORT).show()

//        showProgress(getString(R.string.uploading_media))
        val parcelFileDescriptor =
                contentResolver.openFileDescriptor(selectedImageUri!!, "r", null) ?: return

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFileName(selectedImageUri!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
            val body = UploadImageRequestBody(file, "image/*", this)

            catName = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.CATEGORY_NAME, "")
            subCatName = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.SUBCATEGORY_NAME, "")
            locationString = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.LOCATION, "")
            subLocationString = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.SUB_LOCATION, "")
            timingString = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.TIMING, "")
            lightingString = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.LIGHTING, "")
            myDeviceModel = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.MODEL, "")
            screenSize = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.SCREEN_SIZE, "")
            categoryString = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.CATEGORY, "")
            humanPresent = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.IS_HUMAN_PRESENT, "")
            selfieString = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.SELFIE, "")
            typeString = sharedPrefsManager!!.getStringValue(Constants.TYPE, "")
            aboveEighteenString = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.CHILDREN, "")
            propsString = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.PROPS, "")
            consentString = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.CONSENT, "")
            MyAPI().uploadImage(
                    MultipartBody.Part.createFormData(
                            "image",
                            file.name,
                            body
                    ),
                    RequestBody.create(MediaType.parse("multipart/form-data"), sharedPrefsManager!!.getStringValue(SharedPrefsConstants.USER_EMAIL, "")),
                    RequestBody.create(MediaType.parse("multipart/form-data"), catName),
                    RequestBody.create(MediaType.parse("multipart/form-data"), subCatName),
                    RequestBody.create(MediaType.parse("multipart/form-data"), locationString),
                    RequestBody.create(MediaType.parse("multipart/form-data"), subLocationString),
                    RequestBody.create(MediaType.parse("multipart/form-data"), timingString),
                    RequestBody.create(MediaType.parse("multipart/form-data"), lightingString),
                    RequestBody.create(MediaType.parse("multipart/form-data"), myDeviceModel),
                    RequestBody.create(MediaType.parse("multipart/form-data"), screenSize),
                    RequestBody.create(MediaType.parse("multipart/form-data"), categoryString),
                    RequestBody.create(MediaType.parse("multipart/form-data"), humanPresent),
                    RequestBody.create(MediaType.parse("multipart/form-data"), selfieString),
                    RequestBody.create(MediaType.parse("multipart/form-data"), typeString),
                    RequestBody.create(MediaType.parse("multipart/form-data"), aboveEighteenString),
                    RequestBody.create(MediaType.parse("multipart/form-data"), propsString),
                    RequestBody.create(MediaType.parse("multipart/form-data"), consentString)

            ).enqueue(object : Callback<UploadResponse> {
                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show()
                    count=count!!+1
                    if(t.toString()== Constants.ERROR){
                        Toast.makeText(context, "File was not uploaded", Toast.LENGTH_SHORT).show()
                    }
                    if(count==arrayList?.size){
                        val dialogues = Dialogues()
                        dialogues.showSuccessDialog(context!!)
                    }
                }

                override fun onResponse(
                        call: Call<UploadResponse>,
                        response: Response<UploadResponse>
                ) {
                    response.body()?.let {
                        val dialogues = Dialogues()
                        dialogues.showSuccessDialog(context!!)
                    }
                }
            })
        }
    }

    fun updateProgress(`val`: Int, title: String?, msg: String?) {
        pDialog?.setTitle(title)
        pDialog?.setMessage(msg)
        pDialog?.progress = `val`
    }

    fun showProgress(str: String?) {
        try {
            pDialog?.setCancelable(false)
            pDialog?.setTitle(getString(R.string.please_wait_text))
            pDialog?.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            pDialog?.max = 100 // Progress Dialog Max Value
            pDialog?.setMessage(str)
            if (pDialog!!.isShowing) pDialog!!.dismiss()
            pDialog?.show()
        } catch (e: Exception) {
        }
    }

    fun hideProgress() {
        try {
            if (pDialog!!.isShowing) pDialog!!.dismiss()
        } catch (e: Exception) {
        }
    }

    private fun createPartFromString(descriptionString: String): RequestBody {
        return RequestBody.create(MediaType.parse(MIME_TYPE_TEXT), descriptionString)
    }

    private fun prepareFilePart(partName: String, fileUri: Uri): MultipartBody.Part {
        val file = getFile(this, fileUri)
        val requestFile = RequestBody.create(MediaType.parse(MIME_TYPE_IMAGE), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    override fun onBackPressed() {
        startActivity(Intent(this@UploadMultipleImage, UserUploadMenuActivity::class.java))
        finish()
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(this@UploadMultipleImage, UserUploadMenuActivity::class.java))
            finish()
        }
    }

    companion object {
        private val TAG = UploadMultipleImage::class.java.simpleName
    }
}