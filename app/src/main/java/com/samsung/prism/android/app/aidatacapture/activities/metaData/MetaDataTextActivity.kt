package com.samsung.prism.android.app.aidatacapture.activities.metaData

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.TextMenuActivity
import com.samsung.prism.android.app.aidatacapture.activities.textUploader.SubTextMenuActivity
import com.samsung.prism.android.app.aidatacapture.activities.textUploader.bulkTextUpload.UploadMultipleText
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.databinding.ActivityMetaDataTextBinding
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsHelper
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import kotlinx.android.synthetic.main.speaker_layout.*
import kotlinx.android.synthetic.main.text_layout.*

class MetaDataTextActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMetaDataTextBinding
    private lateinit var sharedPrefsManager: SharedPrefsManager
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMetaDataTextBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefsManager = SharedPrefsManager(this)
        sharedPrefsHelper = SharedPrefsHelper(this)
        getDeviceModel()
        initViews()
        backButton()
        toolbarClick()
        setOnClickListeners()
        val ans=sharedPrefsManager?.getStringValue(SharedPrefsConstants.TEXT_META_DATA_TYPE, "")
        Toast.makeText(this,ans , Toast.LENGTH_SHORT).show()
    }

    // TODO : Remove error after selecting correct option.

    private fun getDeviceModel(): String {
        return Build.MODEL
    }

    private fun setOnClickListeners() {
        binding.layoutText.addMetaData.setOnClickListener {
            if (inputlayoutTextValidation()) {
                saveData()

                startActivity(
                    Intent(
                        this@MetaDataTextActivity,
                        UploadMultipleText::class.java
                    )
                )
                finish()
            }
        }
    }

    private fun saveData() {
        val viewType = sharedPrefsManager.getIntValue(
            SharedPrefsConstants.VIEW_TYPE,
            Constants.ViewTypeForText.TEXT.ordinal
        )
        when (viewType) {
            0 -> {
                getDataFromTextTextView()
            }
        }
  }



    private fun getDataFromTextTextView() {
        val layout = binding.layoutText
          val filetype = layout.fileType.text.toString()
        sharedPrefsHelper.saveTextMetaData(
            filetype
        )
    }
    private fun inputlayoutTextValidation(): Boolean {
        return when {
            binding.layoutText.fileType.text.isEmpty() -> {
                binding.layoutText.fileType.error = getString(R.string.invalid_input)
                return false
            }
            else -> true
        }
    }


    private fun initViews() {
        val viewType = sharedPrefsManager.getIntValue(
            SharedPrefsConstants.VIEW_TYPE,
            Constants.ViewTypeForText.TEXT.ordinal
        )

        when (viewType) {
            0 -> {
                initTextTextView()
            }
        }
    }




    private fun initTextTextView() {
        binding.layoutPdf.root.visibility = View.GONE
        binding.layoutText.root.visibility = View.VISIBLE
        binding.layoutDoc.root.visibility = View.GONE
        initNoiseDropDownAdapter()
    }

    private fun initNoiseDropDownAdapter() {
        val filetype = arrayOf("pdf","csv","txt","doc","xlsx")
        val fileTypeAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            filetype
        )
        binding.layoutText.fileType.setAdapter(fileTypeAdapter)
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
            startActivity(Intent(Activity@ this, TextMenuActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(Activity@ this, TextMenuActivity::class.java))
        finish()
    }
}