package com.samsung.prism.android.app.aidatacapture.activities

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View.OnFocusChangeListener
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.models.ResponseModel
import com.samsung.prism.android.app.aidatacapture.others.apiClients.UserServiceAPIClient.Companion.instance
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdatePassword : AppCompatActivity() {
    private var submitFeedbackBtn: Button? = null
    private var btnExit: Button? = null
    private lateinit var feedbackLayout: TextInputLayout
    private lateinit var feedbackField: TextInputEditText
    private var sharedPrefsManager: SharedPrefsManager? = null
    private var userEmail: String? = null
    private var feedback: String? = null
    private lateinit var message: TextView
    private var context: Context? = null
    private val exit = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_password)
        context = this
        init()
        feedbackField!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
//                    GradientDrawable drawable = (GradientDrawable)feedbackField.getBackground();
//                    drawable.setStroke(2, Color.RED);
                feedbackField!!.setHintTextColor(Color.RED)
            }
        }
        submitFeedbackBtn!!.setOnClickListener {
            feedback = feedbackField!!.text.toString()
            message!!.text = "Password reset link sent to $feedback"
            if (feedback!!.isEmpty()) {
                feedbackField!!.error = "Please Enter Feedback"
            } else {
                storeFeedback()
            }
        }
    }

    private fun init() {
        sharedPrefsManager = SharedPrefsManager(this)
        userEmail =
            sharedPrefsManager!!.getStringValue(SharedPrefsConstants.USER_EMAIL, "abc@gmail.com")
        submitFeedbackBtn = findViewById(R.id.feedback_button)
        feedbackLayout = findViewById(R.id.feedback_layout)
        feedbackField = findViewById(R.id.feedback_field)
        btnExit = findViewById(R.id.exit_button)
        message = findViewById(R.id.message)
    }

    private fun storeFeedback() {
        val call = instance!!.myApi.resetPassword(feedback)
        call.enqueue(object : Callback<ResponseModel?> {
            override fun onResponse(
                call: Call<ResponseModel?>,
                response: Response<ResponseModel?>
            ) {
                Log.d(Constants.TAG, response.toString())
                if (response.isSuccessful) {
                } else {
                    Log.d(Constants.TAG, "Failed")
                    //Toast.makeText(UpdatePassword.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            override fun onFailure(call: Call<ResponseModel?>, t: Throwable) {
                // Toast.makeText(UpdatePassword.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        })
    }
}