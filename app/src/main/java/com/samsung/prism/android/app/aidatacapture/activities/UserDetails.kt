package com.samsung.prism.android.app.aidatacapture.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.adminRelatedActivities.AdminDashboard
import com.samsung.prism.android.app.aidatacapture.activities.adminRelatedActivities.AdminViewUsers
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.models.OverallCountModel
import com.samsung.prism.android.app.aidatacapture.models.UserCountDashboard
import com.samsung.prism.android.app.aidatacapture.others.apiClients.AdminServiceAPIClient.Companion.instance
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDetails : AppCompatActivity() {
    lateinit var userName: TextView
    lateinit var userEmail: TextView
    lateinit var totalImages: TextView
    lateinit var textCentric: TextView
    lateinit var humanCentric: TextView
    lateinit var nonHumanCentric: TextView
    var sharedPrefsManager: SharedPrefsManager? = null
    lateinit var goBack: MaterialButton
    var context: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)
        context = this
        toolbarClick()
        sharedPrefsManager = SharedPrefsManager(this)
        init()
        backButton()
    }

    private fun init() {
        userName = findViewById(R.id.userName)
        userEmail = findViewById(R.id.userEmail)
        totalImages = findViewById(R.id.totalUploads)
        textCentric = findViewById(R.id.noOfTextCentric)
        humanCentric = findViewById(R.id.noOfHumanCentric)
        nonHumanCentric = findViewById(R.id.noOfNonHumanCentric)
        goBack = findViewById(R.id.goBack)
        goBack.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@UserDetails,
                    AdminViewUsers::class.java
                )
            )
        })
        userName.setText(sharedPrefsManager!!.getStringValue("userNameFromAdapter", "Ganesh H"))
        userEmail.setText(
            sharedPrefsManager!!.getStringValue(
                "userEmailFromAdapter",
                "abc@gmail.com"
            )
        )
        val email = sharedPrefsManager!!.getStringValue("userEmailFromAdapter", "abc@gmail.com")
        Toast.makeText(this, email, Toast.LENGTH_SHORT).show()
        val call = instance!!.myApi.userDashboardCount(email)
        call.enqueue(object : Callback<OverallCountModel> {
            override fun onResponse(
                call: Call<OverallCountModel>,
                response: Response<OverallCountModel>
            ) {
                Log.d(Constants.TAG, response.toString())
                if (response.isSuccessful) {
                    Log.d(Constants.TAG, getString(R.string.response_successfull))
                    totalImages.setText(response.body()!!.totalImages)
                    humanCentric.setText(response.body()!!.totalHumanCentric)
//                    textCentric.setText(response.body()!!.totalTextCentric)
                    nonHumanCentric.setText(response.body()!!.totalNonHumanCentric)
                } else {
                    Log.d(Constants.TAG, "Failed")
                }
            }

            override fun onFailure(call: Call<OverallCountModel>, t: Throwable) {}
        })
    }

    private fun toolbarClick() {
        val feedback: ImageView
        val logout: ImageView
        feedback = findViewById(R.id.toolbar_feedback)
        feedback.visibility = View.GONE
        feedback.visibility = View.GONE
        logout = findViewById(R.id.toolbar_logout)
        logout.setOnClickListener {
            val dialogue = Dialogues()
            dialogue.logoutDialogue(context!!)
        }
        feedback.setOnLongClickListener {
            Toast.makeText(context, getString(R.string.feedback_msg), Toast.LENGTH_SHORT).show()
            true
        }
        logout.setOnLongClickListener {
            Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(this@UserDetails, AdminDashboard::class.java))
            finish()
        }
    }
}