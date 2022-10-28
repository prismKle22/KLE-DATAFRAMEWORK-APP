package com.samsung.prism.android.app.aidatacapture.activities.textUploader.bulkTextUpload

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.Retrofit.Interfaces.MyAPI
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.activities.TextMenuActivity
import com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.bulkAudioUpload.UploadMultipleAudio
import com.samsung.prism.android.app.aidatacapture.activities.mediaUploadActivites.bulkUpload.UploadMultipleImage
import com.samsung.prism.android.app.aidatacapture.activities.metaData.MetaDataTextActivity
import com.samsung.prism.android.app.aidatacapture.activities.mySignConsent.*
import com.samsung.prism.android.app.aidatacapture.activities.mySignConsent.getFileName
import com.samsung.prism.android.app.aidatacapture.activities.textUploader.uploader.*
import com.samsung.prism.android.app.aidatacapture.activities.textUploader.uploader.snackbar
import com.samsung.prism.android.app.aidatacapture.adapters.MyAdapter
import com.samsung.prism.android.app.aidatacapture.adapters.MyAdapterAudio
import com.samsung.prism.android.app.aidatacapture.adapters.MyAdapterText
import com.samsung.prism.android.app.aidatacapture.constants.Constants.BASE_URL
import com.samsung.prism.android.app.aidatacapture.constants.Constants.ERROR
import com.samsung.prism.android.app.aidatacapture.constants.Constants.USER_NAME
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants.FILE_TYPE
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants.USER_EMAIL
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.models.TextFileResponseModel
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsHelper
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import kotlinx.android.synthetic.main.activity_upload_multiple_text.*
import kotlinx.android.synthetic.main.text_layout.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Part
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.ArrayList

class UploadMultipleText : AppCompatActivity(), UploadTextFileRequestBody.UploadCallback {
    private var selectedFileUri: Uri?  =null
    private val files = ArrayList<String>()
    private var listView: ListView? = null
    private lateinit var sharedPrefsManager: SharedPrefsManager
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    private var context: Context? = null
    private var xxxxx: String? = null
    private var yyyyy: String? = null
    private var languages: String? = null
    private var location: String?= null
    private var myDeviceModel: String?= null
    private var xyxy:    String?=null
    private var arrayList: ArrayList<Uri?>? = null
    var ftype=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_multiple_text)
        backButton()
        toolbarClick()
        context = this
        listView = findViewById(R.id.listView)
        sharedPrefsManager = SharedPrefsManager(this)
        sharedPrefsHelper = SharedPrefsHelper(this)
        val btnChoose=findViewById<MaterialButton>(R.id.btnChoose)

        btnChoose.setOnClickListener {
            openImageChooser()
        }
        val texttype=sharedPrefsManager?.getStringValue(SharedPrefsConstants.TEXT_META_DATA_TYPE, "")
        Toast.makeText(this,texttype , Toast.LENGTH_SHORT).show()
        val btnUpload=findViewById<MaterialButton>(R.id.btnUpload)
//        btnUpload.setOnClickListener {
//            uploadFile()
//        }
        if(texttype=="corpus")  {
            Toast.makeText(context, texttype, Toast.LENGTH_SHORT).show()
            btnUpload.setOnClickListener { uploadCorpusFiles() }
        }
        else if (texttype=="text") {
            Toast.makeText(context, texttype, Toast.LENGTH_SHORT).show()
            btnUpload.setOnClickListener { uploadTextFiles() }
        }else{
            Toast.makeText(context, texttype, Toast.LENGTH_SHORT).show()
            btnUpload.setOnClickListener { uploadTranslationFiles() }
        }
        arrayList = ArrayList()

    }

    private fun openImageChooser() {
        val ans=sharedPrefsManager.getStringValue(FILE_TYPE,"")
//        Toast.makeText(this,ans,Toast.LENGTH_LONG).show()
        when (ans) {
            "pdf" -> ftype= "application/pdf"
            "csv" -> ftype= "text/csv"
            "doc" -> ftype="application/msword"
            "txt" -> ftype="text/plain"
            "xlsx" -> ftype="application/vnd.ms-excel"
        }
        Intent(Intent.ACTION_GET_CONTENT).also {
            it.type = ftype
            it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            it.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(it, REQUEST_CODE_PICK_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        btnUpload.visibility = View.VISIBLE
        if (resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                if (resultData.clipData != null) {
                    val count = resultData.clipData!!.itemCount
                    if (count >= 0) {
                        btnUpload.visibility = View.VISIBLE
                        btnChoose.text = getString(R.string.add_more)
                        //btnChoose.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(this, getString(R.string.select_two_or_more_audio_files), Toast.LENGTH_SHORT)
                                .show()
                        btnUpload.visibility = View.VISIBLE
                        btnChoose.visibility = View.VISIBLE
                    }
                    var currentItem = 0
                    while (currentItem < count) {
                        val textUri =
                                resultData.clipData!!.getItemAt(currentItem).uri
                        currentItem += 1
//                        getFilePath(textUri)
//                        Log.d("Uri Selected", textUri.toString())
                        try {
                            arrayList!!.add(textUri)
                            val mAdapter =
                                    MyAdapterText(this, arrayList)
                            listView!!.adapter = mAdapter
                        } catch (e: Exception) {
//                            Log.e(
//                                    UploadMultipleAudio.TAG,
//                                    "File select error",
//                                    e
//                            )
                        }
                    }
                } else if (resultData.data != null) {
                    val uri = resultData.data
//                    Log.i(
//                            UploadMultipleAudio.TAG,
//                            "Uri = " + uri.toString()
//                    )
                    try {
                        arrayList!!.add(uri)
                        val mAdapter = MyAdapterText(this, arrayList)
                        listView!!.adapter = mAdapter
                    } catch (e: Exception) {
//                        Log.e(
//                                UploadMultipleAudio.TAG,
//                                "File select error",
//                                e
//                        )
                    }
                }
            }

        }
    }

    fun getFilePath(uri: Uri) {
        val file = File(uri.path)
        val filePath = file.path.split(":").toTypedArray()
        val file_id = filePath[filePath.size - 1]
        val cursor = contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                null,
                MediaStore.Files.FileColumns._ID + " = ? ",
                arrayOf(file_id),
                null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
            files.add(filePath)
            cursor.close()
        }
    }

    fun uploadCorpusFiles() {
        var count=0
        for (i in arrayList!!.indices) {
            selectedFileUri = arrayList!!.get(i)
//            Toast.makeText(context, selectedFileUri.toString(), Toast.LENGTH_SHORT).show()

            val parcelFileDescriptor =
                    contentResolver.openFileDescriptor(selectedFileUri!!, "r", null) ?: return
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val file = File(cacheDir, contentResolver.getFileName(selectedFileUri!!))
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            progressBar.progress = 0
            val body = UploadCorpusSMSRequestBody(file, ftype, this)
            val gender = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.GENDER, "")
            val age = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.AGE, "")
            val mood = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.MOOD, "")
            val personality = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.PERSONALITY, "")
            val corpus_class = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.CLASS, "")
            val type = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.TYPE, "")
            MyAPI().uploadCorpusSMS(
                    MultipartBody.Part.createFormData(
                            "textfile",
                            file.name,
                            body
                    ),
                    RequestBody.create(MediaType.parse("multipart/form-data"), "jhvcs"),
                    RequestBody.create(MediaType.parse("multipart/form-data"), gender),
                    RequestBody.create(MediaType.parse("multipart/form-data"), age),
                    RequestBody.create(MediaType.parse("multipart/form-data"), mood),
                    RequestBody.create(MediaType.parse("multipart/form-data"), personality),
                    RequestBody.create(MediaType.parse("multipart/form-data"), corpus_class),
                    RequestBody.create(MediaType.parse("multipart/form-data"), type),

                    ).enqueue(object : Callback<UploadResponse> {
                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show()
                    count=count!!+1
                    if(t.toString()==ERROR){
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
//                    layout_root.snackbar("Uploaded Successfully")
                        val dialogues = Dialogues()
                        dialogues.showSuccessDialog(context!!)
                    }
                }
            })
        }
    }

    fun uploadTextFiles() {
        var count=0
        for (i in arrayList!!.indices) {
            selectedFileUri = arrayList!!.get(i)
//            Toast.makeText(context, selectedFileUri.toString(), Toast.LENGTH_SHORT).show()

            val parcelFileDescriptor =
                    contentResolver.openFileDescriptor(selectedFileUri!!, "r", null) ?: return

            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val file = File(cacheDir, contentResolver.getFileName(selectedFileUri!!))
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            progressBar.progress = 0
            val body = UploadTextRequestBody(file, ftype, this)

            val text_class = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.CLASS, "")
            val type = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.TYPE, "")
            val tag = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.TAG, "")
            MyAPI().uploadText(
                    MultipartBody.Part.createFormData(
                            "textfile",
                            file.name,
                            body
                    ),
                    RequestBody.create(MediaType.parse("multipart/form-data"), "jcvnsk"),
                    RequestBody.create(MediaType.parse("multipart/form-data"), text_class),
                    RequestBody.create(MediaType.parse("multipart/form-data"), type),
                    RequestBody.create(MediaType.parse("multipart/form-data"), tag)

            ).enqueue(object : Callback<UploadResponse> {
                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show()
                    count=count!!+1
                    if(t.toString()==ERROR){
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



    fun uploadTranslationFiles() {
        var count=0
        for (i in arrayList!!.indices) {
            selectedFileUri = arrayList!!.get(i)
//            Toast.makeText(context, selectedFileUri.toString(), Toast.LENGTH_SHORT).show()

            val parcelFileDescriptor =
                    contentResolver.openFileDescriptor(selectedFileUri!!, "r", null) ?: return

            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val file = File(cacheDir, contentResolver.getFileName(selectedFileUri!!))
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            progressBar.progress = 0
            val body = UploadTranslationRequestBody(file, ftype, this)

            val numbersPresent = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.NUMBERS, "")
            val mixedSource = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.MIXED_SOURCE, "")
            val category = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.CATEGORY, "")
            val standard = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.STANDARD, "")
            val language = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.LANGUAGE, "")

            MyAPI().uploadTranslation(
                    MultipartBody.Part.createFormData(
                            "textfile",
                            file.name,
                            body
                    ),
                    RequestBody.create(MediaType.parse("multipart/form-data"), "jvhcnus"),
                    RequestBody.create(MediaType.parse("multipart/form-data"), numbersPresent),
                    RequestBody.create(MediaType.parse("multipart/form-data"), mixedSource),
                    RequestBody.create(MediaType.parse("multipart/form-data"), category),
                    RequestBody.create(MediaType.parse("multipart/form-data"), standard),
                    RequestBody.create(MediaType.parse("multipart/form-data"), language),

                    ).enqueue(object : Callback<UploadResponse> {
                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show()
                    count=count!!+1
                    if(t.toString()==ERROR){
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
//                    layout_root.snackbar("Uploaded Successfully")
                        val dialogues = Dialogues()
                        dialogues.showSuccessDialog(context!!)
                    }
                }
            })
        }
    }


    override fun onProgressUpdate(percentage: Int) {
        progressBar.progress = percentage
    }

    companion object {
        const val REQUEST_CODE_PICK_IMAGE = 101
    }
    private fun toolbarClick() {
        val feedback: ImageView = findViewById(R.id.toolbar_feedback)
        feedback.visibility = View.GONE
        feedback.visibility = View.GONE
        val logout: ImageView = findViewById(R.id.toolbar_logout)
        logout.setOnClickListener {
            val dialogue = Dialogues()
            dialogue.logoutDialogue(this)
        }
        feedback.setOnLongClickListener {
            Toast.makeText(this, getString(R.string.feedback_msg), Toast.LENGTH_SHORT).show()
            true
        }
        logout.setOnLongClickListener {
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(Activity@ this, MetaDataTextActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(Activity@ this, MetaDataTextActivity::class.java))
        finish()
    }
}