package com.samsung.prism.android.app.aidatacapture.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.metaData.CorpussmstextActivity
import com.samsung.prism.android.app.aidatacapture.activities.metaData.MetaDataAudioActivity
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.databinding.ActivityAudioBinding
import com.samsung.prism.android.app.aidatacapture.databinding.ActivitySuperMetaDataTextBinding
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager

class SuperMetaDataText : AppCompatActivity() {
    private lateinit var binding: ActivitySuperMetaDataTextBinding
    private lateinit var sharedPrefsManager: SharedPrefsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuperMetaDataTextBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefsManager = SharedPrefsManager(this)
        backButton()
        toolbarClick()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.btnCorpusSms.setOnClickListener {
            sharedPrefsManager.saveIntValue(SharedPrefsConstants.VIEW_TYPE, Constants.ViewTypeForSuperMetaText.CORPUSSMS.ordinal)
            startActivity(Intent(this@SuperMetaDataText, CorpussmstextActivity::class.java))
            finish()
        }
        binding.btnText.setOnClickListener {
            sharedPrefsManager.saveIntValue(SharedPrefsConstants.VIEW_TYPE, Constants.ViewTypeForSuperMetaText.TEXT.ordinal)
            startActivity(Intent(this@SuperMetaDataText, CorpussmstextActivity::class.java))
            finish()
        }
        binding.btnTranslation.setOnClickListener {
            sharedPrefsManager.saveIntValue(SharedPrefsConstants.VIEW_TYPE,Constants.ViewTypeForSuperMetaText.TRANSLATION.ordinal)
            startActivity(Intent(this@SuperMetaDataText, CorpussmstextActivity::class.java))
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

    override fun onBackPressed() {
        startActivity(Intent(Activity@ this, UserMenuActivity::class.java))
        finish()
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(Activity@ this, UserMenuActivity::class.java))
            finish()
        }
    }
}