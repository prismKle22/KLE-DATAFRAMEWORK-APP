package com.samsung.prism.android.app.aidatacapture.activities.accountRelatedActivities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.balsikandar.crashreporter.CrashReporter
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.*
import com.samsung.prism.android.app.aidatacapture.activities.adminRelatedActivities.AdminDashboard
import com.samsung.prism.android.app.aidatacapture.activities.mediaUploadActivites.bulkUpload.UploadMultipleImage
import com.samsung.prism.android.app.aidatacapture.activities.mediaUploadActivites.captureAndUpload.UploadAgreementActivity
import com.samsung.prism.android.app.aidatacapture.activities.metaData.CorpussmstextActivity
import com.samsung.prism.android.app.aidatacapture.activities.metaData.MetaDataTextActivity
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.databinding.ActivityLoginBinding
import com.samsung.prism.android.app.aidatacapture.dialogues.ProgressDialog
import com.samsung.prism.android.app.aidatacapture.models.DownloadUpdateModel
import com.samsung.prism.android.app.aidatacapture.models.ResponseModel
import com.samsung.prism.android.app.aidatacapture.others.apiClients.UserServiceAPIClient
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsHelper
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserLoginActivity() : AppCompatActivity() {
    private var userEmailEdit: TextInputEditText? = null
    private var userPasswordEdit: TextInputEditText? = null
    private var resetPassword: TextView? = null
    private var userEmailLayout: TextInputLayout? = null
    private var userPasswordLayout: TextInputLayout? = null
    private var Email: String? = null
    private var Password: String? = null
    private var loginButton: Button? = null
    private var backToRegister: Button? = null
    private val email: String? = null
    private var sharedPrefsManager: SharedPrefsManager? = null
    private var SharedPref: SharedPrefsManager? = null
    private var mAuth: FirebaseAuth? = null
    private var exit = false
    private var context: Context? = null
    private var binding: ActivityLoginBinding? = null
    private var progressDialog: ProgressDialog? = null
    private var sharedPrefsHelper: SharedPrefsHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        sharedPrefsHelper = SharedPrefsHelper(this)
        context = this
        updateFunction()
        init()
        main()
        resetPassword?.setOnClickListener {
            startActivity(
                Intent(
                    this@UserLoginActivity,
                    UpdatePassword::class.java
                )
            )
        }
    }

    private fun updateFunction() {
        val call: Call<DownloadUpdateModel> =
            UserServiceAPIClient.instance!!.myApi.getUpdate(Constants.SHARED_PREF_ADMIN_EMAIL)
        call.enqueue(object : Callback<DownloadUpdateModel> {
            override fun onResponse(
                call: Call<DownloadUpdateModel>,
                response: Response<DownloadUpdateModel>
            ) {
                Log.d(Constants.TAG, response.body().toString())
                if (response.isSuccessful) {
                    Log.d(Constants.TAG, getString(R.string.response_successfull))
                    if (response.body()!!.version!!.toInt() > Constants.APK_VERSION_NO) {
                        binding!!.updateLayout.visibility = View.VISIBLE
                        binding!!.loginButton.isEnabled = false
                        binding!!.backToRegister.isEnabled = false
                        onClickListenerForDownload(response.body()?.downloadURL!!)
                    } else if (response.body()!!.subVersion
                        !!.toInt() > Constants.APK_SUB_VERSION_NO
                    ) {
                        binding!!.updateLayout.visibility = View.VISIBLE
                        onClickListenerForDownload(response.body()!!.downloadURL!!)
                    } else {
                        binding!!.updateLayout.visibility = View.GONE
                    }
                } else {
                    Log.d(Constants.TAG, "Failed")
                }
            }

            override fun onFailure(call: Call<DownloadUpdateModel>, t: Throwable) {
                Log.d("FailureMsg", "onFailure: " + t.message)
            }
        })
    }

    private fun onClickListenerForDownload(url: String) {
        binding!!.downloadUpdateButton.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
                )
            )
        }
    }

    private fun showDialogue(context: Context?, message: String) {
        progressDialog = ProgressDialog((context)!!, message)
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun validateInputs(): Boolean {
        Email = userEmailEdit!!.text.toString().trim { it <= ' ' }
        Password = userPasswordEdit!!.text.toString().trim { it <= ' ' }
        if (Email!!.isEmpty() || Email!!.length < 5) {
            userEmailLayout!!.error = getString(R.string.inavlid_email)
            return false
        } else if (Password!!.isEmpty()) {
            userEmailLayout!!.error = null
            userPasswordLayout!!.error = getString(R.string.inavlid_password)
            return false
        } else {
            userPasswordLayout!!.error = null
            return true
        }
    }

    private fun main() {
        loginButton!!.setOnClickListener {
            if (validateInputs()) {
                userPasswordLayout!!.error = null
                val call: Call<ResponseModel> =
                    UserServiceAPIClient.instance!!.myApi.userLogin(Email, Password)
                call.enqueue(object : Callback<ResponseModel> {
                    override fun onResponse(
                        call: Call<ResponseModel>,
                        response: Response<ResponseModel>
                    ) {
//                        Toast.makeText(
//                                this@UserLoginActivity,
//                                response.body()?.email.toString(),
//                                Toast.LENGTH_SHORT
//                        ).show()

                        Log.d(Constants.TAG, response.toString())
                        if(response.isSuccessful){
                            sharedPrefsManager?.saveStringValue(SharedPrefsConstants.USER_EMAIL, response.body()?.email.toString())
                            if ((Email == Constants.SHARED_PREF_ADMIN_EMAIL)) {
                                        moveToAdminView()
                            }
                            else{
                                moveToUserDashboard(response)
                            }
                        }
//                        if (response.isSuccessful) {
//                            if (response.body()!!.isAuthenticated) {
//                                if (response.body()!!.isVerified) {
//
//                                    if ((Email == Constants.SHARED_PREF_ADMIN_EMAIL)) {
//                                        moveToAdminView()
//                                    } else {
//                                        if (response.body()!!.isAgreementUploaded) {
//                                            moveToUserDashboard(response)
//                                        } else {
//                                            moveToUploadAgreement(response)
//                                        }
//                                    }
//                                } else {
//                                    if (!response.body()!!.isAgreementUploaded) {
//                                        moveToUserDashboard(response)
//                                    } else {
//                                        moveToWaitForApproval()
//                                    }
//                                }
//                                finish()
//                            } else {
//                                Toast.makeText(
//                                    this@UserLoginActivity,
//                                    "Failed!",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        } else {
//                            Toast.makeText(
//                                this@UserLoginActivity,
//                                getString(R.string.something_went_wrong),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
                    }

                    override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                        Toast.makeText(
                            this@UserLoginActivity,
                            "Invalid Credentials",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }

    private fun moveToWaitForApproval() {
        showDialogue(
            context,
            getString(R.string.getting_started_text)
        )
        startActivity(
            Intent(
                this@UserLoginActivity,
                WaitForApprovalActivity::class.java
            )
        )
    }

    private fun moveToUploadAgreement(response: Response<ResponseModel>) {

        sharedPrefsHelper!!.saveInitialSharedPrefs(response)
        showDialogue(
            context,
            getString(R.string.getting_started_text)
        )
        startActivity(
            Intent(
                this@UserLoginActivity,
                UploadAgreementActivity::class.java
            )
        )

    }

    private fun moveToUserDashboard(response: Response<ResponseModel>) {
        Toast.makeText(
            this@UserLoginActivity,
            "Welcome!",
            Toast.LENGTH_SHORT
        ).show()
        showDialogue(
            context,
            getString(R.string.please_wait_text)
        )
        sharedPrefsManager!!.saveBoolValue(SharedPrefsConstants.IS_IT_USER_PROFILE,false)
        sharedPrefsHelper!!.saveInitialSharedPrefs(response)
        startActivity(
            Intent(
                this@UserLoginActivity,
                UserDashboard::class.java
            )
        )
    }

    private fun moveToAdminView() {
        showDialogue(
            context,
            getString(R.string.please_wait_text)
        )
        startActivity(
            Intent(
                this@UserLoginActivity,
                AdminDashboard::class.java
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog!!.dismiss()
    }

    private fun init() {
        try {

        } catch (e: Exception) {
            CrashReporter.logException(e)
        }

        userPasswordLayout = findViewById(R.id.userPasswordLayout)
        userEmailLayout = findViewById(R.id.userUSNLayout)
        userEmailEdit = findViewById(R.id.userEmail)
        resetPassword = findViewById(R.id.resendPassword)
        userPasswordEdit = findViewById(R.id.userPassword)
        mAuth = FirebaseAuth.getInstance()
        loginButton = findViewById(R.id.loginButton)
        mAuth = FirebaseAuth.getInstance()
        backToRegister = findViewById(R.id.backToRegister)
        resetPassword = findViewById(R.id.resendPassword)
        SharedPref =
            SharedPrefsManager(
                this
            )
        sharedPrefsManager =
            SharedPrefsManager(
                this
            )
        backToRegister!!.setOnClickListener {
            sharedPrefsManager!!.saveBoolValue(SharedPrefsConstants.IS_FROM_LOGIN, true)
            startActivity(Intent(this@UserLoginActivity, DownloadAgreementActivity::class.java))
        }
    }


    override fun onBackPressed() {
        if (exit) moveTaskToBack(true) else {
            Toast.makeText(
                this, getString(R.string.press_back_to_exit),
                Toast.LENGTH_SHORT
            ).show()
            exit = true
            Handler().postDelayed({ exit = false }, (3 * 1000).toLong())
        }
    }
}