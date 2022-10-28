package com.samsung.prism.android.app.aidatacapture.activities.adminRelatedActivities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.budiyev.android.imageloader.ImageLoader
import com.budiyev.android.imageloader.ImageUtils
import com.google.android.material.button.MaterialButton
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.Constants.BASE_URL
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.models.AdminResponseModel
import com.samsung.prism.android.app.aidatacapture.others.apiClients.AdminServiceAPIClient
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.URL

class ApproveUserProfile : AppCompatActivity() {
    private var userName: TextView? = null
    private var userEmail: TextView? = null
    private var userMobile: TextView? = null
    private var userNameString: String? = null
    private var userEmailString: String? = null
    private val userMobileString: String? = null
    private var agreementUrl: String? = null
    private var agreementImageView: AppCompatImageView? = null
    private var sharedPrefsManager: SharedPrefsManager? = null
    private var context: Context? = null
    private var approveUser: MaterialButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approve_user_profile)
        context = this
        init()
        toolbarClick()
        backButton()
        try {
            mainMethod()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun init() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        userName = findViewById(R.id.userName)
        userEmail = findViewById(R.id.userEmail)
        userMobile = findViewById(R.id.userMobile)
        agreementImageView = findViewById(R.id.agreementImage)
        approveUser = findViewById(R.id.approveUser)
        sharedPrefsManager =
            SharedPrefsManager(
                this
            )
        userEmailString = sharedPrefsManager!!.getStringValue(
            SharedPrefsConstants.APPROVE_USER_EMAIL,
            Constants.SHARED_PREF_ADMIN_EMAIL
        )
        agreementUrl = sharedPrefsManager!!.getStringValue(
            SharedPrefsConstants.AGREEMENT_URL,
            "https://learn.getgrav.org/user/pages/11.troubleshooting/01.page-not-found/error-404.png"
        )
        userNameString =
            sharedPrefsManager!!.getStringValue(SharedPrefsConstants.USER_FULL_NAME, "abc")
    }

    private fun approveUser() {
        val call: Call<AdminResponseModel> = AdminServiceAPIClient.instance!!.myApi.approveUser(
            sharedPrefsManager!!.getStringValue(SharedPrefsConstants.APPROVE_USER_EMAIL, Constants.SHARED_PREF_ADMIN_EMAIL)
        )
        call.enqueue(object : Callback<AdminResponseModel?> {
            override fun onResponse(
                call: Call<AdminResponseModel?>,
                response: Response<AdminResponseModel?>
            ) {
                Log.d(Constants.TAG, response.toString())
                if (response.isSuccessful()) {
                    Log.d(Constants.TAG, getString(R.string.response_successfull))
                    Toast.makeText(context, getString(R.string.user_approved), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Log.d(Constants.TAG, "Failed")
                }
            }

            override fun onFailure(call: Call<AdminResponseModel?>, t: Throwable) {}
        })
    }

    @Throws(IOException::class)
    private fun mainMethod() {
        agreementUrl = BASE_URL + agreementUrl
        Log.d(Constants.TAG, "mainMethod: $agreementUrl")
        val url = URL(agreementUrl)
        agreementImageView?.let {
            ImageLoader.with(this)
                .from(agreementUrl!!)
                .size(500, 500)
                .placeholder(ColorDrawable(Color.LTGRAY))
                .errorDrawable(R.drawable.not_found)
                .transform(ImageUtils.cropCenter())
                .transform(ImageUtils.convertToGrayScale())
                .load(it)
        }
        userEmail!!.text = userEmailString
        userName!!.text = userNameString
        approveUser!!.setOnClickListener {
            Toast.makeText(
                context,
                getString(R.string.user_approved_successfully),
                Toast.LENGTH_SHORT
            ).show()
            approveUser()
            startActivity(Intent(this@ApproveUserProfile, AdminApproveUsers::class.java))
            finish()
        }
    }

    private fun toolbarClick() {
        val feedback: ImageView = findViewById(R.id.toolbar_feedback)
        feedback.visibility = View.GONE
        val logout: ImageView = findViewById(R.id.toolbar_logout)
        logout.setOnClickListener {
            Dialogues().logoutDialogue(this)
        }
        feedback.setOnLongClickListener {
            Toast.makeText(context, getString(R.string.feedback_msg), Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener { onBackPressed() }
    }
}