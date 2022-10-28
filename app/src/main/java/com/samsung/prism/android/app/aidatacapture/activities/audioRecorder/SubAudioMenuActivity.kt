package com.samsung.prism.android.app.aidatacapture.activities.audioRecorder

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.bulkAudioUpload.UploadMultipleAudio
import com.samsung.prism.android.app.aidatacapture.activities.metaData.MetaDataAudioActivity
import com.samsung.prism.android.app.aidatacapture.databinding.ActivitySubAudioMenuBinding
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager

class SubAudioMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubAudioMenuBinding
    private lateinit var sharedPrefsManager : SharedPrefsManager
    private var audioType:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySubAudioMenuBinding.inflate(layoutInflater)
        audioType=intent.getStringExtra("audioType").toString()

        setContentView(binding.root)
        sharedPrefsManager = SharedPrefsManager(this)
        setOnClickListeners()
        toolbarClick()
        backButton()
    }

    private fun setOnClickListeners() {
        binding.btnUploadAudio.setOnClickListener {

//            startActivity(Intent(this@SubAudioMenuActivity, UploadMultipleAudio::class.java))
//            finish()


            val i:Intent?=Intent(this@SubAudioMenuActivity, UploadMultipleAudio::class.java)
            i?.putExtra("audioType",audioType)

            startActivity(i)
            finish()
        }
        binding.btnRecordAudio.setOnClickListener {
            startActivity(Intent(this@SubAudioMenuActivity,AudioRecorderActivity::class.java))
            finish()
        }
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(Activity@ this, MetaDataAudioActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(Activity@ this, MetaDataAudioActivity::class.java))
        finish()
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