package com.samsung.prism.android.app.aidatacapture.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.models.ResponseModel
import com.samsung.prism.android.app.aidatacapture.others.apiClients.UserServiceAPIClient.Companion.instance
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserFeedBackActivity : AppCompatActivity() {
    private var UserFeedBackActivityButton: Button? = null
    private var btnExit: Button? = null
    private var emailLayout: TextInputLayout? = null
    private var emailField: TextInputEditText? = null
    private var sharedPrefsManager: SharedPrefsManager? = null
    private var userEmail: String? = null
    private var feedback: String? = null
    private var context: Context? = null
    private var exit = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_back)
        context = this
        init()
        emailField!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                emailField!!.setHintTextColor(Color.RED)
            }
        }
        btnExit!!.setOnClickListener {
            val dialogues = Dialogues()
            dialogues.showExitDialog(context as UserFeedBackActivity)
        }
        UserFeedBackActivityButton!!.setOnClickListener {
            feedback = emailField!!.text.toString()
            if (feedback!!.isEmpty()) {
                emailField!!.error = "Please Enter Feedback"
            } else {
                storeFeedback()
            }
        }
        toolbarClick()
        backButton()
    }

    private fun backButton() {
        val backButton =
            findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(this@UserFeedBackActivity, UserDashboard::class.java))
            //onBackPressed()
            finish()
        }
    }

    private fun toolbarClick() {
        val feedback: ImageView = findViewById(R.id.toolbar_feedback)
        val logout: ImageView = findViewById(R.id.toolbar_logout)
        feedback.visibility = View.GONE
        logout.visibility = View.GONE
    }

    private fun init() {
        sharedPrefsManager = SharedPrefsManager(this)
        userEmail =
            sharedPrefsManager!!.getStringValue(SharedPrefsConstants.USER_EMAIL, "abc@gmail.com")
        UserFeedBackActivityButton = findViewById(R.id.feedback_button)
        emailLayout = findViewById(R.id.feedback_layout)
        emailField = findViewById(R.id.feedback_field)
        btnExit = findViewById(R.id.exit_button)
    }

    private fun storeFeedback() {
        val call = instance!!.myApi.addFeedBack(userEmail, feedback)
        call.enqueue(object : Callback<ResponseModel?> {
            override fun onResponse(
                call: Call<ResponseModel?>,
                response: Response<ResponseModel?>
            ) {
                Log.d(Constants.TAG, response.toString())
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@UserFeedBackActivity,
                        "Thanks for your feedback!",
                        Toast.LENGTH_SHORT
                    ).show()
//                    moveTaskToBack(true)
                    startActivity(Intent(this@UserFeedBackActivity, UserDashboard::class.java))
                    finish()
                } else {
                    Log.d(Constants.TAG, "Failed")
                    Toast.makeText(
                        this@UserFeedBackActivity,
                        getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseModel?>, t: Throwable) {
                Toast.makeText(
                    this@UserFeedBackActivity,
                    getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onBackPressed() {
        if (exit) {
            val dialogues = Dialogues()
            dialogues.showExitDialog(context!!)
        } else {
            Toast.makeText(
                this, getString(R.string.press_back_to_exit),
                Toast.LENGTH_SHORT
            ).show()
            exit = true
            Handler().postDelayed({ exit = false }, (3 * 1000).toLong())
        }
    }
}