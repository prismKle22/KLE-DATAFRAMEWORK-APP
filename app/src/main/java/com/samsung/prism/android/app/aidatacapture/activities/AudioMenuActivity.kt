package com.samsung.prism.android.app.aidatacapture.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.metaData.MetaDataAudioActivity
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.databinding.ActivityAudioBinding
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager

class AudioMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioBinding
    private lateinit var sharedPrefsManager: SharedPrefsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefsManager = SharedPrefsManager(this)
        backButton()
        toolbarClick()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.btnRealSpeech.setOnClickListener {
            sharedPrefsManager.saveIntValue(SharedPrefsConstants.VIEW_TYPE, Constants.ViewTypeForAudio.SPEECH.ordinal)
            startActivity(Intent(this@AudioMenuActivity, MetaDataAudioActivity::class.java))
            finish()
        }
        binding.btnNoice.setOnClickListener {
            sharedPrefsManager.saveIntValue(SharedPrefsConstants.VIEW_TYPE, Constants.ViewTypeForAudio.NOISE.ordinal)
            startActivity(Intent(this@AudioMenuActivity, MetaDataAudioActivity::class.java))
            finish()
        }
        binding.btnSpeaker.setOnClickListener {
            sharedPrefsManager.saveIntValue(SharedPrefsConstants.VIEW_TYPE,Constants.ViewTypeForAudio.SPEAKER.ordinal)
            startActivity(Intent(this@AudioMenuActivity, MetaDataAudioActivity::class.java))
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