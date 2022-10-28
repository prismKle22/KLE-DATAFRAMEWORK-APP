package com.samsung.prism.android.app.aidatacapture.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.mediaUploadActivites.bulkUpload.UploadMultipleImage
import com.samsung.prism.android.app.aidatacapture.activities.mediaUploadActivites.bulkUpload.UploadMultipleVideo
import com.samsung.prism.android.app.aidatacapture.activities.mediaUploadActivites.captureAndUpload.UploadCapturedImageActivity
import com.samsung.prism.android.app.aidatacapture.activities.metaData.MetaDataHumanCentricActivity
import com.samsung.prism.android.app.aidatacapture.activities.metaData.MetaDataNonHumanCentricActivity
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/* User has all the upload options in this activity- This activity comes after MetaData Screen */   class UserUploadMenuActivity :
    AppCompatActivity() {
    var sharedPrefsManager: SharedPrefsManager? = null
    var categoryName: String? = null
    private var context: Context? = null
    private var fileUri // file url to store image/video
            : Uri? = null
    private lateinit var btnCapturePicture: MaterialCardView
    private lateinit var btnRecordVideo: MaterialCardView
    private lateinit var btnUploadImage: MaterialCardView
    private lateinit var btnUploadVideo: MaterialCardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_upload)
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        context = this
        backButton()
        sharedPrefsManager = SharedPrefsManager(this)
        categoryName =
            sharedPrefsManager!!.getStringValue(SharedPrefsConstants.DATA_MAJOR_CATEGORY, "Null")
        //getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(getResources().getString(R.color.action_bar))));
        btnCapturePicture = findViewById(R.id.btnCapturePicture)
        btnRecordVideo = findViewById(R.id.btnRecordVideo)
        btnUploadImage = findViewById(R.id.btnUploadImage)
        btnUploadVideo = findViewById(R.id.btnUploadVideo)
        toolbarClick()
        //floatingActionButton();
        /**
         * Capture image button click event
         */

        //Upload Button click event
        btnUploadImage.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@UserUploadMenuActivity, UploadMultipleImage::class.java))
            finish()
        })

        //capture button click event
        btnCapturePicture.setOnClickListener(View.OnClickListener { // capture picture
//            captureImage()
            startActivity(Intent(this@UserUploadMenuActivity, UploadCapturedImageActivity::class.java))
        })

        //upload button click event
        btnUploadVideo.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@UserUploadMenuActivity, UploadMultipleVideo::class.java))
            finish()
        })
        /**
         * Record video button click event
         */

        //upload record button click event
        btnRecordVideo.setOnClickListener(View.OnClickListener { // record video
            recordVideo()
        })

        // Checking camera availability
//        if (!isDeviceSupportCamera()) {
//            Toast.makeText(getApplicationContext(),
//                    "Sorry! Your device doesn't support camera",
//                    Toast.LENGTH_LONG).show();
//            // will close the app if the device does't have camera
//            finish();
//        }
        initToolbar()
    }

    private fun initToolbar() {
        val toolbarName: TextView
        toolbarName = findViewById(R.id.toolbar_name)
        toolbarName.text = "Upload"
    }

    /**
     * Checking device has camera hardware or not
     */
    //toolbar click even
    private fun toolbarClick() {
        val feedback: ImageView
        val logout: ImageView
        feedback = findViewById(R.id.toolbar_feedback)
        logout = findViewById(R.id.toolbar_logout)
        feedback.setOnClickListener {
            startActivity(Intent(this@UserUploadMenuActivity, UserFeedBackActivity::class.java))
            finish()
        }

        //logout click event
        logout.setOnClickListener {
            val dialogue = Dialogues()
            dialogue.logoutDialogue(context!!)
        }

        //toolbar feedback button
        feedback.setOnLongClickListener {
            Toast.makeText(context, getString(R.string.feedback_msg), Toast.LENGTH_SHORT).show()
            true
        }


        //toolbar logout button
        logout.setOnLongClickListener {
            Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show()
            false
        }
    }


    /**
     * Launching camera app to record video
     */
    //floating action button for download activity
    private fun floatingActionButton() {
        val fab = findViewById<FloatingActionButton>(R.id.floating_action_button)
        val sharedPrefsManager = SharedPrefsManager(this)
        fab.setOnClickListener {
            sharedPrefsManager.saveBoolValue(SharedPrefsConstants.IS_FROM_LOGIN, false)
            startActivity(
                Intent(
                    this@UserUploadMenuActivity,
                    DownloadAgreementActivity::class.java
                )
            )
        }
    }

    private fun recordVideo() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO)

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri) // set the image file
        // name

        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE)
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri")
    }



    /**
     * ------------ Helper Methods ----------------------
     */
    /**
     * Creating file uri to store image/video
     */
    fun getOutputMediaFileUri(type: Int): Uri {
        return Uri.fromFile(getOutputMediaFile(type))
    }

    //toolbar back button
    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener { //Checking whether the Activity is from HumanMetaData Activity or Not
            if (sharedPrefsManager!!.getBoolValue(SharedPrefsConstants.IS_FROM_METADATA, false)) {
                startActivity(
                    Intent(
                        this@UserUploadMenuActivity,
                        MetaDataHumanCentricActivity::class.java
                    )
                )
                finish()
            } else {
                startActivity(
                    Intent(
                        this@UserUploadMenuActivity,
                        MetaDataNonHumanCentricActivity::class.java
                    )
                )
                finish()
            }
        }
    }

    //on BackPressed event
    override fun onBackPressed() {

        if (sharedPrefsManager!!.getBoolValue(SharedPrefsConstants.IS_FROM_METADATA, false)) {
            startActivity(
                Intent(
                    this@UserUploadMenuActivity,
                    MetaDataHumanCentricActivity::class.java
                )
            )
            finish()
        } else {
            startActivity(
                Intent(
                    this@UserUploadMenuActivity,
                    MetaDataNonHumanCentricActivity::class.java
                )
            )
            finish()
        }
    }

    companion object {
        // LogCat tag
        private val TAG = UserUploadMenuActivity::class.java.simpleName

        // Camera activity request codes
        private const val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100
        private const val CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200
        const val MEDIA_TYPE_IMAGE = 1
        const val MEDIA_TYPE_VIDEO = 2

        /**
         * returning image / video
         */
        private fun getOutputMediaFile(type: Int): File? {

            // External sdcard location
            val mediaStorageDir = File(
                Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Constants.IMAGE_DIRECTORY_NAME
            )

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(
                        TAG, "Oops! Failed create "
                                + Constants.IMAGE_DIRECTORY_NAME + " directory"
                    )
                    return null
                }
            }
            // Create a media file name
            val timeStamp = SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Date())
            val mediaFile: File
            mediaFile = if (type == MEDIA_TYPE_IMAGE) {
                File(
                    mediaStorageDir.path + File.separator
                            + "IMG_" + timeStamp + ".jpg"
                )
            } else if (type == MEDIA_TYPE_VIDEO) {
                File(
                    mediaStorageDir.path + File.separator
                            + "VID_" + timeStamp + ".mp4"
                )
            } else {
                return null
            }
            return mediaFile
        }
    }
}