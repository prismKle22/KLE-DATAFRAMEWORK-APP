package com.samsung.prism.android.app.aidatacapture.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.metaData.CorpussmstextActivity

import com.samsung.prism.android.app.aidatacapture.activities.metaData.MetaDataTextActivity

import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants

import com.samsung.prism.android.app.aidatacapture.databinding.ActivityTextMenuBinding
import com.samsung.prism.android.app.aidatacapture.databinding.ActivityTypeTextBinding

import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager

class TextMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTextMenuBinding
    private lateinit var sharedPrefsManager: SharedPrefsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefsManager = SharedPrefsManager(this)
        backButton()
        toolbarClick()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.btnPDF.setOnClickListener {
            val ans=sharedPrefsManager?.getStringValue(SharedPrefsConstants.TEXT_META_DATA_TYPE, "")
            Toast.makeText(this,ans , Toast.LENGTH_SHORT).show()
            sharedPrefsManager.saveIntValue(SharedPrefsConstants.VIEW_TYPE, Constants.ViewTypeForText.PDF.ordinal)
            startActivity(Intent(this@TextMenuActivity, MetaDataTextActivity::class.java))
            finish()
        }
        binding.btnText.setOnClickListener {
            sharedPrefsManager.saveIntValue(SharedPrefsConstants.VIEW_TYPE, Constants.ViewTypeForText.TEXT.ordinal)
            startActivity(Intent(this@TextMenuActivity, TypeTextActivity::class.java))
            finish()
        }
        binding.btnDoc.setOnClickListener {
            sharedPrefsManager.saveIntValue(SharedPrefsConstants.VIEW_TYPE,Constants.ViewTypeForText.DOC.ordinal)
            startActivity(Intent(this@TextMenuActivity, MetaDataTextActivity::class.java))
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
        startActivity(Intent(Activity@ this, CorpussmstextActivity::class.java))
        finish()
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(Activity@ this, CorpussmstextActivity::class.java))
            finish()
        }
    }
}