package com.samsung.prism.android.app.aidatacapture.activities.mediaUploadActivites.captureAndUpload

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.Retrofit.Interfaces.MyAPI
import com.samsung.prism.android.app.aidatacapture.activities.mySignConsent.*
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import kotlinx.android.synthetic.main.activity_upload_captured_image.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.util.*

class UploadCapturedImageActivity : AppCompatActivity() {
    val REQUEST_CODE = 200
    var bitmap: Bitmap? = null
    private lateinit var file: File

    private var filename:String?=null
    private var consentemail: String? = null

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
        setContentView(R.layout.activity_upload_captured_image)
        sharedPrefsManager = SharedPrefsManager(this)
        context = this
        val buttonCapture=findViewById<Button>(R.id.buttonCapture)
        buttonCapture.setOnClickListener {
            capturePhoto()
        }
        val buttonUpload=findViewById<Button>(R.id.buttonUpload)
        buttonUpload.setOnClickListener { uploadImage() }

    }
    fun capturePhoto() {

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null){

            val wrapper = ContextWrapper(applicationContext)

            // Initialize a new file instance to save bitmap object
            file = wrapper.getDir("Images", Context.MODE_PRIVATE)
            file = File(file,"${UUID.randomUUID()}.jpg")
            bitmap=data.extras?.get("data") as Bitmap
            try{
                // Compress the bitmap and save in jpg format
                val stream: OutputStream = FileOutputStream(file)
                bitmap?.compress(Bitmap.CompressFormat.JPEG,100,stream)
                stream.flush()
                stream.close()
            }catch (e: IOException){
                e.printStackTrace()
            }
            buttonCapture.text="Recapture"
            buttonUpload.visibility = View.VISIBLE
            capturedUploadImageView.setImageURI(Uri.parse(file.absolutePath))
        }
    }


    fun uploadImage(){

        val body = UploadCapturedImageRequestBody(file, "image/*", this)
        catName = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.CATEGORY_NAME, "")
        subCatName = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.SUBCATEGORY_NAME, "")
        locationString=sharedPrefsManager!!.getStringValue(SharedPrefsConstants.LOCATION, "")
        subLocationString=sharedPrefsManager!!.getStringValue(SharedPrefsConstants.SUB_LOCATION, "")
        timingString=sharedPrefsManager!!.getStringValue(SharedPrefsConstants.TIMING, "")
        lightingString=sharedPrefsManager!!.getStringValue(SharedPrefsConstants.LIGHTING, "")
        myDeviceModel=sharedPrefsManager!!.getStringValue(SharedPrefsConstants.MODEL, "")
        screenSize=sharedPrefsManager!!.getStringValue(SharedPrefsConstants.SCREEN_SIZE, "")
        categoryString=sharedPrefsManager!!.getStringValue(SharedPrefsConstants.CATEGORY, "")
        humanPresent=sharedPrefsManager!!.getStringValue(SharedPrefsConstants.IS_HUMAN_PRESENT, "")
        selfieString=sharedPrefsManager!!.getStringValue(SharedPrefsConstants.SELFIE, "")
        typeString=sharedPrefsManager!!.getStringValue(Constants.TYPE, "")
        aboveEighteenString=sharedPrefsManager!!.getStringValue(SharedPrefsConstants.CHILDREN, "")
        propsString=sharedPrefsManager!!.getStringValue(SharedPrefsConstants.PROPS, "")
        consentString=sharedPrefsManager!!.getStringValue(SharedPrefsConstants.CONSENT, "")
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
                val dialogues = Dialogues()
                dialogues.showSuccessDialog(context!!)
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