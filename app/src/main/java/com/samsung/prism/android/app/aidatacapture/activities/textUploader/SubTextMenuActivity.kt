package com.samsung.prism.android.app.aidatacapture.activities.textUploader

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.textUploader.bulkTextUpload.UploadMultipleText
import com.samsung.prism.android.app.aidatacapture.activities.metaData.MetaDataTextActivity
import com.samsung.prism.android.app.aidatacapture.databinding.ActivitySubTextMenuBinding
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager

class SubTextMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubTextMenuBinding
    private lateinit var sharedPrefsManager : SharedPrefsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySubTextMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefsManager = SharedPrefsManager(this)
        setOnClickListeners()
        toolbarClick()
        backButton()
    }

    private fun setOnClickListeners() {
        binding.btnUploadAudio.setOnClickListener {
            startActivity(Intent(this@SubTextMenuActivity, UploadMultipleText::class.java))
            finish()
        }

    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(Activity@ this, MetaDataTextActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(Activity@ this, MetaDataTextActivity::class.java))
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