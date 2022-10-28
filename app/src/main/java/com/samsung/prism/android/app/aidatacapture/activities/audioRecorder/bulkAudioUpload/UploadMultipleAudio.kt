package com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.bulkAudioUpload

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
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
import com.samsung.prism.android.app.aidatacapture.Retrofit.Interfaces.MyAPI
import com.samsung.prism.android.app.aidatacapture.activities.UserFeedBackActivity
import com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.SubAudioMenuActivity
import com.samsung.prism.android.app.aidatacapture.activities.mySignConsent.*
import com.samsung.prism.android.app.aidatacapture.adapters.MyAdapterAudio
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.others.fileUploaders.MultipleAudioUploader
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import kotlinx.android.synthetic.main.doc_layout.*
import kotlinx.android.synthetic.main.pdf_layout.*
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

class UploadMultipleAudio : AppCompatActivity() {
    private val files = ArrayList<String>()
    private val REQUEST_CODE_READ_STORAGE = 2
    private var listView: ListView? = null
    private var mProgressBar: ProgressBar? = null
    private lateinit var btnChoose: MaterialButton
    private lateinit var btnUpload: MaterialButton
    private var arrayList: ArrayList<Uri?>? = null
    private var sharedPrefsManager: SharedPrefsManager? = null
    private var pDialog: ProgressDialog? = null
    private var context: Context? = null
    private var audioType:String?=null
    private var selectedAudioUri: Uri? = null
    private var gender: String? = null
    private var ageGroup: String? = null
    private var noise: String? = null
    private var sourceDistance: String? = null
    private var languages: String? = null
    private var location: String?= null
    private var multipleSpeakers: String?= null
    private var multipleNoise: String?= null
    private var myDeviceModel: String?= null
    private var noiseObject:    String?=null
    private var source : String?=null
//    private var lightingString: String?= null
//    private var myDeviceModel: String?= null
//    private var screenSize: String?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.multiple_audio_upload)


        audioType=intent.getStringExtra("audioType").toString()


        sharedPrefsManager = SharedPrefsManager(this)
        context = this

        pDialog = ProgressDialog(this)
        backButton()
        if (Build.VERSION.SDK_INT > 9) {
            val policy =
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        listView = findViewById(R.id.listView)
        mProgressBar = findViewById(R.id.progressBar)
        toolbarClick()
        btnChoose = findViewById(R.id.btnChoose)
        btnChoose.setOnClickListener { showChooser() }
        btnUpload = findViewById(R.id.btnUpload)


        if(audioType=="speech")  {
            Toast.makeText(context, audioType, Toast.LENGTH_SHORT).show()
            btnUpload.setOnClickListener { uploadSpeechFiles() }
        }
        else if (audioType=="noise") {
            Toast.makeText(context, audioType, Toast.LENGTH_SHORT).show()
            btnUpload.setOnClickListener { uploadNoiseFiles() }
        }else{
            Toast.makeText(context, audioType, Toast.LENGTH_SHORT).show()
            btnUpload.setOnClickListener { uploadSpeakerFiles() }
        }
        arrayList = ArrayList()
    }



    private fun toolbarClick() {
        val feedback: ImageView = findViewById(R.id.toolbar_feedback)
        val logout: ImageView = findViewById(R.id.toolbar_logout)
        feedback.setOnClickListener {
            startActivity(Intent(this@UploadMultipleAudio, UserFeedBackActivity::class.java))
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
        intent.type = "audio/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, REQUEST_CODE_READ_STORAGE)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        resultData: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, resultData)
        btnUpload.visibility = View.VISIBLE
        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == REQUEST_CODE_READ_STORAGE) {


//                selectedAudioUri = resultData?.data


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
                            val audioUri =
                                resultData.clipData!!.getItemAt(currentItem).uri
                            currentItem += 1
                            getAudioFilePath(audioUri)
                            Log.d("Uri Selected", audioUri.toString())
                            try {
                                arrayList!!.add(audioUri)
                                val mAdapter =
                                    MyAdapterAudio(this@UploadMultipleAudio, arrayList)
                                listView!!.adapter = mAdapter
                            } catch (e: Exception) {
                                Log.e(
                                    TAG,
                                    "File select error",
                                    e
                                )
                            }
                        }
                    } else if (resultData.data != null) {
                        val uri = resultData.data
                        Log.i(
                            TAG,
                            "Uri = " + uri.toString()
                        )
                        try {
                            arrayList!!.add(uri)
                            val mAdapter = MyAdapterAudio(this@UploadMultipleAudio, arrayList)
                            listView!!.adapter = mAdapter
                        } catch (e: Exception) {
                            Log.e(
                                TAG,
                                "File select error",
                                e
                            )
                        }
                    }
                }
            }
//        }
    }



    fun uploadSpeechFiles() {
        var count=0

        for (i in arrayList!!.indices) {
            selectedAudioUri = arrayList!!.get(i)
//            Toast.makeText(context, selectedAudioUri.toString(), Toast.LENGTH_SHORT).show()
            val parcelFileDescriptor =
                    contentResolver.openFileDescriptor(selectedAudioUri!!, "r", null) ?: return

            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val file = File(cacheDir, contentResolver.getFileName(selectedAudioUri!!))
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
//        Toast.makeText(context, file, Toast.LENGTH_SHORT).show()

            val body = UploadSpeechRequestBody(file, "audio/*", this)

            gender = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.GENDER, "")
            ageGroup = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.AGE_GROUP, "")
            noise = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.NOISE, "")
            sourceDistance = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.SOURCE_DISTANCE, "")
            languages = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.LANGUAGE, "")
            location = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.LOCATION, "")
            multipleSpeakers = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.MULTIPLE_SPEAKERS, "")
            myDeviceModel = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.MODEL, "")


            Toast.makeText(context, sharedPrefsManager!!.getStringValue(SharedPrefsConstants.USER_EMAIL, ""), Toast.LENGTH_SHORT).show()
            MyAPI().uploadSpeech(
                    MultipartBody.Part.createFormData(
                            "audio",
                            file.name,
                            body
                    ),
                    RequestBody.create(MediaType.parse("multipart/form-data"), "vnhsdj"),
                    RequestBody.create(MediaType.parse("multipart/form-data"), gender),
                    RequestBody.create(MediaType.parse("multipart/form-data"), ageGroup),
                    RequestBody.create(MediaType.parse("multipart/form-data"), noise),
                    RequestBody.create(MediaType.parse("multipart/form-data"), sourceDistance),
                    RequestBody.create(MediaType.parse("multipart/form-data"), languages),
                    RequestBody.create(MediaType.parse("multipart/form-data"), location),
                    RequestBody.create(MediaType.parse("multipart/form-data"), multipleSpeakers),
                    RequestBody.create(MediaType.parse("multipart/form-data"), myDeviceModel),

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

   fun uploadNoiseFiles() {
       var count=0

       for (i in arrayList!!.indices) {
           selectedAudioUri = arrayList!!.get(i)
//           Toast.makeText(context, selectedAudioUri.toString(), Toast.LENGTH_SHORT).show()
           val parcelFileDescriptor =
                   contentResolver.openFileDescriptor(selectedAudioUri!!, "r", null) ?: return

           val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
           val file = File(cacheDir, contentResolver.getFileName(selectedAudioUri!!))
           val outputStream = FileOutputStream(file)
           inputStream.copyTo(outputStream)
           val body = UploadNoiseRequestBody(file, "audio/*", this)
           sourceDistance = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.SOURCE_DISTANCE, "")
           noiseObject = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.NOISE_OBJECT, "")
           location = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.LOCATION, "")
           multipleNoise = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.MULTIPLE_NOISE, "")

           myDeviceModel = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.MODEL, "")
           Toast.makeText(context, sharedPrefsManager!!.getStringValue(SharedPrefsConstants.USER_EMAIL, ""), Toast.LENGTH_SHORT).show()
           MyAPI().uploadNoise(
                   MultipartBody.Part.createFormData(
                           "audio",
                           file.name,
                           body
                   ),
                   RequestBody.create(MediaType.parse("multipart/form-data"), "vnhsdj"),
                   RequestBody.create(MediaType.parse("multipart/form-data"), sourceDistance),
                   RequestBody.create(MediaType.parse("multipart/form-data"), noiseObject),
                   RequestBody.create(MediaType.parse("multipart/form-data"), location),
                   RequestBody.create(MediaType.parse("multipart/form-data"), multipleNoise),
                   RequestBody.create(MediaType.parse("multipart/form-data"), myDeviceModel)

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

    fun uploadSpeakerFiles() {
        var count=0

        for (i in arrayList!!.indices) {
            selectedAudioUri = arrayList!!.get(i)
//            Toast.makeText(context, selectedAudioUri.toString(), Toast.LENGTH_SHORT).show()
            val parcelFileDescriptor =
                    contentResolver.openFileDescriptor(selectedAudioUri!!, "r", null) ?: return

            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val file = File(cacheDir, contentResolver.getFileName(selectedAudioUri!!))
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            val body = UploadSpeakerRequestBody(file, "audio/*", this)
            sourceDistance = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.SOURCE_DISTANCE, "")
            noise = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.NOISE, "")
            source = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.SOURCE, "")
            location = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.LOCATION, "")
            myDeviceModel = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.MODEL, "")

            Toast.makeText(context, sharedPrefsManager!!.getStringValue(SharedPrefsConstants.USER_EMAIL, ""), Toast.LENGTH_SHORT).show()
            MyAPI().uploadSpeaker(
                    MultipartBody.Part.createFormData(
                            "audio",
                            file.name,
                            body
                    ),
                    RequestBody.create(MediaType.parse("multipart/form-data"), "vnhsdj"),
                    RequestBody.create(MediaType.parse("multipart/form-data"), sourceDistance),
                    RequestBody.create(MediaType.parse("multipart/form-data"), noise),
                    RequestBody.create(MediaType.parse("multipart/form-data"), source),
                    RequestBody.create(MediaType.parse("multipart/form-data"), location),

                    RequestBody.create(MediaType.parse("multipart/form-data"), myDeviceModel),

//
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
//
//
//
  private fun getAudioFilePath(uri: Uri) {
        val file = File(uri.path)
        val filePath =
            file.path.split(":".toRegex()).toTypedArray()
        val image_id = filePath[filePath.size - 1]
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Audio.Media._ID + " = ? ",
            arrayOf(image_id),
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val imagePath =
                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
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
        val fileUploader = MultipleAudioUploader()

        fileUploader.uploadFiles(
            "/",
            "file",
            filesToUpload,
            object : MultipleAudioUploader.FileUploaderCallback {
                override fun onError() {
                    hideProgress()
                }

                override fun onFinish(responses: Array<String?>?) {
                    hideProgress()
                    val dialogues = Dialogues()
                    dialogues.showSuccessDialog(context!!)
                    for (i in responses?.indices!!) {
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
                        getString(R.string.uploading_status),
                        getString(R.string.total_uploaded_audio) + filenumber
                    )
                    Log.e(
                        "Progress Status",
                        "$currentpercent $totalpercent $filenumber"
                    )
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

    private fun showProgress(str: String?) {
        try {
            pDialog!!.setCancelable(false)
            pDialog!!.setTitle(getString(R.string.please_wait_text))
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


    override fun onBackPressed() {
        startActivity(Intent(this@UploadMultipleAudio, SubAudioMenuActivity::class.java))
        finish()
    }

    private fun backButton() {
        val backButton =
            findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(this@UploadMultipleAudio, SubAudioMenuActivity::class.java))
            finish()
        }
    }

    companion object {
        private val TAG = UploadMultipleAudio::class.java.simpleName
    }
}