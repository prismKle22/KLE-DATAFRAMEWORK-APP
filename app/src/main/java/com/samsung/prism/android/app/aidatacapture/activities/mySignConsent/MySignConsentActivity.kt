package com.samsung.prism.android.app.aidatacapture.activities.mySignConsent

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.Retrofit.Interfaces.MyAPI
import com.samsung.prism.android.app.aidatacapture.activities.DownloadAgreementActivity
import com.samsung.prism.android.app.aidatacapture.activities.SignConsentActivity
import com.samsung.prism.android.app.aidatacapture.activities.accountRelatedActivities.UserLoginActivity
import com.samsung.prism.android.app.aidatacapture.activities.metaData.MetaDataHumanCentricActivity
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.models.ResponseModel
import com.samsung.prism.android.app.aidatacapture.others.apiClients.UserServiceAPIClient
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import kotlinx.android.synthetic.main.activity_my_sign_consent.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class MySignConsentActivity : AppCompatActivity() {
    private var uri: Uri? = null
    private var toolbarName: TextView? = null

    private var filename:String?=null
    private var consentemail: String? = null
    private var consentname: String? = null
    private var consentplace: String? = null
    private var sharedPrefsManager: SharedPrefsManager? = null
    private var SharedPref: SharedPrefsManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_sign_consent)
        backButton()

        SharedPref =
                SharedPrefsManager(
                        this
                )
        sharedPrefsManager =
                SharedPrefsManager(
                        this
                )
        buttonShowDialog.setOnClickListener { showDialog() }
    }

    private fun showDialog() {
        val dialogFragment = SignatureDialogFragment(this)
        dialogFragment.show(supportFragmentManager, "signature")
    }
    fun onSignatureCaptured(bitmap: Bitmap, fileUri: String) {
        if(bitmap!=null){
            // Save the bitmap to a file and display it into image view

            uri = bitmapToFile(bitmap)
//            imageView.setImageURI(uri)
//            text_view.text = uri.toString()
//            Log.d("kavya",uri.toString())
            val bundle:Bundle?=intent.extras
            bundle?.putString("name",filename)
            bundle?.putString("email",consentemail)
            val i:Intent?=Intent(this@MySignConsentActivity, EmailConsentActivity::class.java)
            i?.putExtra("name",filename)
            i?.putExtra("email",consentemail)
            startActivity(i)
            finish()

        }else{
            toast("bitmap not found.")
        }

    }

    // Extension function to show toast message
    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Method to save an bitmap to a file
    private fun bitmapToFile(bitmap: Bitmap): Uri {
        // Get the context wrapper
        val wrapper = ContextWrapper(applicationContext)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")

        try{
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        val body = UploadRequestBody(file, "image/jpg", this)
        consentemail = sharedPrefsManager?.getStringValue(SharedPrefsConstants.CONSENT_EMAIL, "")
        consentname = sharedPrefsManager?.getStringValue(SharedPrefsConstants.CONSENT_NAME, "")
        consentplace = sharedPrefsManager?.getStringValue(SharedPrefsConstants.CONSENT_PLACE, "")

//        sharedPrefsManager.saveStringValue(SharedPrefsConstants.CONSENT_NAME,text.toString())
//        consentemail="01fe19bcs145@kletech.ac.in";
//        Toast.makeText(this@MySignConsentActivity,consentemail,Toast.LENGTH_LONG).show()
        val currentTimestamp = System.currentTimeMillis()
        filename=consentemail+currentTimestamp.toString()
        sharedPrefsManager?.saveStringValue(SharedPrefsConstants.CONSENT_NAME,filename)


        MyAPI().uploadConsent(
                MultipartBody.Part.createFormData(
                        "image",
                        file.name,
                        body
                ),
                RequestBody.create(MediaType.parse("multipart/form-data"), consentemail),
                RequestBody.create(MediaType.parse("multipart/form-data"), consentname),
                RequestBody.create(MediaType.parse("multipart/form-data"), consentplace),
                RequestBody.create(MediaType.parse("multipart/form-data"),filename)
        ).enqueue(object : Callback<UploadResponse> {
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
//                Toast.makeText(this@MySignConsentActivity,t.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                    call: Call<UploadResponse>,
                    response: Response<UploadResponse>
            ) {
                response.body()?.let {
//                    Toast.makeText(this@MySignConsentActivity,response.body()?.toString(),Toast.LENGTH_LONG).show()
                }
            }
        })



        // Return the saved bitmap uri
        return Uri.parse(file.absolutePath)

    }
    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(this, SignConsentActivity::class.java))
            finish()
        }
    }
    override fun onBackPressed() {
        startActivity(Intent(this, SignConsentActivity::class.java))
        finish()
    }

    private fun initToolbar() {
        toolbarName!!.text = "User Registration"
    }
    fun onProgressUpdate(percentage: Int) {
//        val progress_bar=findViewById<ProgressBar>(R.id.progress_bar)
//        progress_bar.progress = percentage
    }
}