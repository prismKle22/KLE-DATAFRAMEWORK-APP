package com.samsung.prism.android.app.aidatacapture.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.textUploader.uploader.TextUploadAPI
import com.samsung.prism.android.app.aidatacapture.activities.textUploader.uploader.snackbar
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants

import com.samsung.prism.android.app.aidatacapture.databinding.ActivityTypeText2Binding

import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.models.TypedTextResponseModel
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import kotlinx.android.synthetic.main.activity_type_text2.*
import kotlinx.android.synthetic.main.activity_upload_multiple_text.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TypeTextActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTypeText2Binding
    private lateinit var sharedPrefsManager: SharedPrefsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTypeText2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        showInput.setOnClickListener{
        sharedPrefsManager = SharedPrefsManager(this)
            val editText = findViewById<EditText>(R.id.editText)
            // Getting the user input
            val text = editText.text
            TextUploadAPI().addData(
                    RequestBody.create(MediaType.parse("multipart/form-data"), text.toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), sharedPrefsManager.getStringValue(
                            SharedPrefsConstants.USER_EMAIL,""))
            ).enqueue(object : Callback<TypedTextResponseModel> {
                override fun onFailure(call: Call<TypedTextResponseModel>, t: Throwable) {
                    layout_root.snackbar("Uploaded Successfully")

                }

                override fun onResponse(
                        call: Call<TypedTextResponseModel>,
                        response: Response<TypedTextResponseModel>
                ) {
                    response.body()?.let {
//                    layout_root.snackbar(it.message)
                        layout_root.snackbar("Uploaded Successfully")
                    }
                }
            })

        }
        backButton()
        toolbarClick()
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
        startActivity(Intent(Activity@ this, TextMenuActivity::class.java))
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