package com.samsung.prism.android.app.aidatacapture.activities.metaData

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.UserMenuActivity
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.databinding.ActivityMetaDataWordImageBinding
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MetaDataWordImageActivity : AppCompatActivity() {
    private lateinit var readability: String
    private lateinit var clarity: String
    private lateinit var content: String
    private lateinit var language: String
    private lateinit var binding: ActivityMetaDataWordImageBinding
    private var fileUri: Uri? = null
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMetaDataWordImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefsHelper = SharedPrefsHelper(this)
        backButton()
        toolbarClick()
        setOnClickListeners()
        initializeDropdownAdapters()
    }

    private fun initializeDropdownAdapters() {
        val language = arrayOf(
            "English",
            "Hindi",
            "Telugu",
            "Tamil",
            "Marathi",
            "Punjabi",
            "Kannada",
            "Malayalam",
            "Urdu",
            "Bengali"
        )
        val readability = arrayOf("Easy", "Medium", "Hard")
        val content = arrayOf("Shop", "Banner", "Book", "Others")
        val clarity = arrayOf("Letters blurred", "Overlapping")

        val languageAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            language
        )

        val readabilityAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            readability
        )

        val contentAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            content
        )

        val clarityAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            clarity
        )

        binding.language.setAdapter(languageAdapter)
        binding.clarity.setAdapter(clarityAdapter)
        binding.readability.setAdapter(readabilityAdapter)
        binding.content.setAdapter(contentAdapter)

    }

    private fun setOnClickListeners() {
        binding.addMetaData.setOnClickListener {
            getFromEditText()
        }
    }

    private fun getFromEditText() {
        language = binding.language.text.toString()
        content = binding.content.text.toString()
        clarity = binding.clarity.text.toString()
        readability = binding.readability.text.toString()
        Log.d("Msg", "getFromEditText: " + binding.language.text.toString())
    }

    private fun inputValidation(): Boolean {

        return when {
            binding.language.text.isEmpty() -> {
                binding.language.error = getString(R.string.invalid_input)
                return false
            }
            binding.content.text.isEmpty() -> {
                binding.content.error = getString(R.string.invalid_input)
                return false
            }
            binding.readability.text.isEmpty() -> {
                binding.readability.error = getString(R.string.invalid_input)
                return false
            }
            binding.clarity.text.isEmpty() -> {
                binding.clarity.error = getString(R.string.invalid_input)
                return false
            }

            else -> true
        }

    }


    private fun getOutputMediaFileUri(mediaTypeImage: Int): Uri? {
        return Uri.fromFile(getOutputMediaFile(mediaTypeImage))

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("file_uri", fileUri)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        fileUri = savedInstanceState.getParcelable("file_uri")
    }




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
                    Constants.TAG, "Oops! Failed create "
                            + Constants.IMAGE_DIRECTORY_NAME + " directory"
                )
                return null
            }
        }

        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        return if (type == Constants.MEDIA_TYPE_IMAGE) {
            File(
                mediaStorageDir.path + File.separator
                        + "IMG_" + timeStamp + ".jpg"
            )
        } else {
            return null
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@MetaDataWordImageActivity, UserMenuActivity::class.java))
        finish()
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(
                Intent(
                    this@MetaDataWordImageActivity,
                    UserMenuActivity::class.java
                )
            )
            finish()
        }
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
}