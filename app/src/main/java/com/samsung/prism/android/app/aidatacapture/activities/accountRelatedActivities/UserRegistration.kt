package  com.samsung.prism.android.app.aidatacapture.activities.accountRelatedActivities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.DownloadAgreementActivity
import com.samsung.prism.android.app.aidatacapture.activities.WaitForApprovalActivity
import com.samsung.prism.android.app.aidatacapture.activities.mediaUploadActivites.captureAndUpload.UploadAgreementActivity
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.databinding.ActivityUserRegistrationBinding
import com.samsung.prism.android.app.aidatacapture.dialogues.ProgressDialog
import com.samsung.prism.android.app.aidatacapture.models.ResponseModel
import com.samsung.prism.android.app.aidatacapture.others.apiClients.UserServiceAPIClient
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Field
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

class UserRegistration : AppCompatActivity() {
    private var userName: TextInputEditText? = null
    private var userEmail: TextInputEditText? = null
    private var userUSN: TextInputEditText? = null
    private var userPassword: TextInputEditText? = null
    private var userConfirmPassword: TextInputEditText? = null
    private var userMobileNumber: TextInputEditText? = null
    private var userOTP: TextInputEditText? = null
    private var toolbarName: TextView? = null
    private var userNameLayout: TextInputLayout? = null
    private var userEmailLayout: TextInputLayout? = null
    private var userUSNLayout: TextInputLayout? = null
    private var userPasswordLayout: TextInputLayout? = null
    private var userConfirmationLayout: TextInputLayout? = null
    private var userMobileNumberLauout: TextInputLayout? = null
    private var sendOTPLayout: TextInputLayout? = null
    private var userOTPLayout: TextInputLayout? = null
    private var firstName: String? = null
    private var email: String? = null
    private var userConfirmPasswordString: String? = null
    private var lastName: String? = null
    private var password: String? = null
    private var mobileNumber: String? = null
    private var registerButton: Button? = null
    private var sendOTP: Button? = null
    private var mDatabase: DatabaseReference? = null
    private var verificationCodeBySystem: String? = null
    protected var SharedPrefs: SharedPrefsManager? = null
    private var sharedPrefsManager: SharedPrefsManager? = null
    private var mAuth: FirebaseAuth? = null
    private var progressBar: LottieAnimationView? = null
    private var number: String? = null
    private var isEmailExist = false
    private var isMobileNumberExist = false
    private var context: Context? = null
    private lateinit var binding:ActivityUserRegistrationBinding
    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUserRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefsManager =
            SharedPrefsManager(
                this
            )
        context = this
        init()
        initToolbar()
        backButton()
        sendOTP()
        verifyOTP()
        toolbarClick()
    }
    private fun toolbarClick() {
        val feedback: ImageView = findViewById(R.id.toolbar_feedback)
        val logout: ImageView = findViewById(R.id.toolbar_logout)
        feedback.visibility = View.GONE
        logout.visibility = View.GONE
    }

    private fun verifyOTP() {
        registerButton?.setOnClickListener(View.OnClickListener {
            val otp = userOTP?.text.toString()
            if (otp.isEmpty() || otp.length < 6) {
                userOTPLayout?.error = "Wrong OTP..."
                userOTPLayout?.requestFocus()
                return@OnClickListener
            } else {
                verifyCode(otp)
            }
        })
    }

    private fun sendOTP() {
        try {
            sendOTP!!.setOnClickListener {
                try {
                    number = userMobileNumber!!.text.toString()
                } catch (e: Exception) {
                    Toast.makeText(
                        this@UserRegistration,
                        getString(R.string.enter_mobile_number_first),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (number!!.length < 10 || number!!.length > 10) {
                    Toast.makeText(
                        this@UserRegistration,
                        getString(R.string.invalid_number),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    userMobileNumberLauout!!.error = getString(R.string.invalid_number)
                } else if (!inputValidation()) {
                } else {
                    binding.registrationLayout.visibility = View.GONE
                    binding.enterDetailsText.text = getString(R.string.enter_otp)
                    binding.otpLayout.visibility = View.VISIBLE
                    progressBar?.visibility = View.VISIBLE
                    phoneAuth(number)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.otp_already_sent), Toast.LENGTH_SHORT).show()
        }
    }

    private fun phoneAuth(number: String?) {
        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber("+91$number") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(mCallbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)



//        val phoneNum = "+16505554567"
        val testVerificationCode = "123456"

// Whenever verification is triggered with the whitelisted number,
// provided it is not set for auto-retrieval, onCodeSent will be triggered.
//        val options = PhoneAuthOptions.newBuilder(mAuth!!)
//                .setPhoneNumber("+91$number")
//                .setTimeout(30L, TimeUnit.SECONDS)
//                .setActivity(this)
//                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//                    override fun onCodeSent(
//                            verificationId: String,
//                            forceResendingToken: PhoneAuthProvider.ForceResendingToken
//                    ) {
//                        // Save the verification id somewhere
//                        // ...
//
//                        // The corresponding whitelisted code above should be used to complete sign-in.
////                        this@UserRegistration.enableUserManuallyInputCode()
//                    }
//
//                    override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
//                        // Sign in with the credential
//                        // ...
//                        Log.d(TAG, "onVerificationCompleted:$phoneAuthCredential")
//                        val code = phoneAuthCredential.smsCode
//                        println(code)
//                        if (code != null) {
//                            //progressBarOTP.setVisibility(View.VISIBLE);
//                            verifyCode(code)
//                        } else {
//                            print("No OTP")
//                        }
//                    }
//
//                    override fun onVerificationFailed(e: FirebaseException) {
//                        // ...
//                        Toast.makeText(this@UserRegistration, e.message, Toast.LENGTH_SHORT).show()
//
//                    }
//                })
//                .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val mCallbacks: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                verificationCodeBySystem = s
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$phoneAuthCredential")
                val code = phoneAuthCredential.smsCode
                println(code)
                if (code != null) {
                    //progressBarOTP.setVisibility(View.VISIBLE);
                    verifyCode(code)
                } else {
                    print("No OTP")
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@UserRegistration, e.message, Toast.LENGTH_SHORT).show()
            }
        }

    private fun verifyCode(codeByUser: String) {
        Toast.makeText(this,codeByUser,Toast.LENGTH_LONG).show()
        try {
            val credential = PhoneAuthProvider.getCredential(verificationCodeBySystem!!, (codeByUser))
            signInTheUserByCredentials(credential)
        } catch (e: Exception) {
            userOTPLayout!!.error = "Invalid OTP!"
        }
    }

    private fun signInTheUserByCredentials(credential: PhoneAuthCredential) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this@UserRegistration) { task ->
                if (task.isSuccessful) {
                    sharedPrefsManager?.saveStringValue(SharedPrefsConstants.MOBILE_NUMBER, number)
                    Log.d(Constants.TAG, "User Added")
                    sharedPrefsManager?.saveBoolValue(SharedPrefsConstants.IS_FROM_REGISTER, true)
                    Toast.makeText(
                        this@UserRegistration,
                        getString(R.string.otp_verfied_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                    userOTP?.setText(getString(R.string.otp_verfied_successfully))
                    userOTP?.setTextColor(resources.getColor(R.color.green))
                    userOTPLayout!!.setEndIconDrawable(R.drawable.ic_baseline_check_circle_24)
                    mainMethod()
//                    startActivity(
//                            Intent(
//                                    this@UserRegistration,
//                                    UploadAgreementActivity::class.java
//                            )
//                    )
                    //Toast.makeText(UserRegistration.this, "Your account has been created successfully!", Toast.LENGTH_SHORT).show();

                    //Perform Your required action here to either let the user sign In or do something required
                } else {
                    Toast.makeText(
                        this@UserRegistration,
                        task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    userOTPLayout!!.error = task.exception!!.message
                    sharedPrefsManager!!.saveBoolValue(SharedPrefsConstants.USER, false)
                }
            }
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(this@UserRegistration, DownloadAgreementActivity::class.java))
            finish()
        }
    }

    private fun mainMethod() {
        if (inputValidation()) {
            progressDialog = ProgressDialog(context!!, getString(R.string.getting_started_text))
            progressDialog!!.setCancelable(false)
            progressDialog!!.show()
            Log.d(Constants.TAG, "Main Method Called")
            Toast.makeText(
                                this@UserRegistration,
                                "main method called",
                                Toast.LENGTH_SHORT
                            ).show()
            val call: Call<ResponseModel> = UserServiceAPIClient.instance!!.myApi.insertUser(
                firstName,
                lastName,
                mobileNumber,
                email,
                password
            )
            call.enqueue(object : Callback<ResponseModel> {
                override fun onResponse(
                    call: Call<ResponseModel>,
                    response: Response<ResponseModel>
                ) {
                    Log.d(Constants.TAG, response.toString())
                    if (response.isSuccessful()) {
                        Log.d(Constants.TAG, getString(R.string.response_successfull))
                        Toast.makeText(
                                this@UserRegistration,
                                response.body().toString(),
                                Toast.LENGTH_LONG
                        ).show()
//                        if (response.body()!!.isAuthenticated) {
//                            Log.d(Constants.TAG, getString(R.string.user_already_exist))
//                            Toast.makeText(
//                                this@UserRegistration,
//                                getString(R.string.user_already_exist),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        } else {
//                            SharedPrefs?.saveStringValue(
//                                SharedPrefsConstants.USER_EMAIL,
//                                email
//                            )
//                            SharedPrefsManager(
//                                context!!
//                            ).saveBoolValue(
//                                SharedPrefsConstants.IS_ALL_AGREEMENT_UPLOADED,
//                                false
//                            )
//                            val r = Runnable {
                                startActivity(
                                    Intent(
                                        this@UserRegistration,
                                        UploadAgreementActivity::class.java
                                    )
                                )
                                finish()
//                            }
//                            val h = Handler()
//                            h.postDelayed(r, 2000)
//                        }
                    } else {
                        Log.d(Constants.TAG, "Failed")
                        Toast.makeText(this@UserRegistration, "Failed!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    Toast.makeText(
                        this@UserRegistration,
                        getString(R.string.user_creation_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            startActivity(
                    Intent(
                            this@UserRegistration,
                            UploadAgreementActivity::class.java
                    )
            )
            finish()
        }
    }

    private fun emailValidator(email: String?): Boolean {
        val pattern: Pattern = Pattern.compile(Constants.EMAIL_PATTERN)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun inputValidation(): Boolean {
        firstName = userName?.text.toString()
        email = userEmail?.text.toString().trim { it <= ' ' }
        userConfirmPasswordString = userConfirmPassword!!.text.toString()
        lastName = userUSN?.text.toString()
        password = userPassword?.text.toString()
        mobileNumber = userMobileNumber?.text.toString()



        if (firstName!!.length < 3 || firstName!!.isEmpty()) {
            userNameLayout!!.error = getString(R.string.invalid_input)
            return false
        } else {
            userNameLayout!!.error = null
        }
        if (isEmailExist || !emailValidator(email)) {
            userEmailLayout!!.error = getString(R.string.inavlid_email)
            return false
        } else {
            userEmailLayout!!.error = null
        }
        if (mobileNumber!!.length < 10 || mobileNumber!!.isEmpty() || isMobileNumberExist) {
            userMobileNumberLauout!!.error = getString(R.string.invalid_number)
            return false
        } else {
            userMobileNumberLauout!!.error = null
        }
        if (password != userConfirmPasswordString || password!!.isEmpty()) {
            userPasswordLayout!!.error = getString(R.string.password_not_matched)
            return false
        } else {
            userPasswordLayout!!.error = null
        }
        return if (password!!.length < 3 || password!!.isEmpty()) {
            userPasswordLayout!!.error = getString(R.string.password_validation_text)
            false
        } else {
            true
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@UserRegistration, UserLoginActivity::class.java))
        finish()
    }

    private fun initToolbar() {
        toolbarName!!.text = "User Registration"
    }

    private fun init() {
        val view = findViewById<ScrollView>(R.id.scrollView)
        view.descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.setOnTouchListener { v, _ ->
            v.requestFocusFromTouch()
            false
        }
        toolbarName = findViewById(R.id.toolbar_name)
        userName = findViewById(R.id.fullName)
        userEmail = findViewById(R.id.userEmail)
        userUSN = findViewById(R.id.userUSN)
        userOTP = findViewById(R.id.userOTP)
        userOTPLayout = findViewById(R.id.userOtpLayout)
        userPassword = findViewById(R.id.userPassword)
        userConfirmPassword = findViewById(R.id.userConfirmPassword)
        userNameLayout = findViewById(R.id.fullNameLayout)
        userEmailLayout = findViewById(R.id.userEmailLayout)
        userMobileNumberLauout = findViewById(R.id.userMobileNumberLayout)
        userUSNLayout = findViewById(R.id.userUSNLayout)
        userPasswordLayout = findViewById(R.id.userPasswordLayout)
        userConfirmationLayout = findViewById(R.id.userConfirmPasswordLayout)
        registerButton = findViewById(R.id.registerButton)
        sendOTP = findViewById(R.id.requestOTP)
        sendOTPLayout = findViewById(R.id.userOtpLayout)
        userMobileNumber = findViewById(R.id.userMobileNumber)
        progressBar = findViewById(R.id.progress_bar)
        mDatabase = FirebaseDatabase.getInstance().reference
        SharedPrefs =
            SharedPrefsManager(
                this
            )
        mAuth = FirebaseAuth.getInstance()
        userEmail!!.addTextChangedListener(
            object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                private var timer = Timer()
                private val DELAY: Long = 1000 // milliseconds
                override fun afterTextChanged(s: Editable) {
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                email = userEmail!!.text.toString().trim { it <= ' ' }
                                val call: Call<ResponseModel> =
                                    UserServiceAPIClient.instance!!.myApi.checkEmail(email)
                                call.enqueue(object : Callback<ResponseModel> {
                                    override fun onResponse(
                                        call: Call<ResponseModel>,
                                        response: Response<ResponseModel>
                                    ) {
                                        Log.d(Constants.TAG, response.toString())
                                        if (response.isSuccessful) {
                                            Log.d(
                                                Constants.TAG,
                                                getString(R.string.response_successfull)
                                            )
//                                            isEmailExist = if (response.body()!!.isEmailExist) {
//                                                Log.d(Constants.TAG, "Already Exist")
//                                                userEmailLayout!!.error =
//                                                    getString(R.string.email_exist)
//                                                userMobileNumber!!.isEnabled = false
//                                                userMobileNumber!!.isFocusable = false
//                                                userPassword!!.setEnabled(false)
//                                                userPassword!!.setFocusable(false)
//                                                userConfirmPassword!!.setEnabled(false)
//                                                userConfirmPassword!!.setFocusable(false)
//                                                userOTP!!.setEnabled(false)
//                                                userOTP!!.setFocusable(false)
//                                                sendOTP!!.setEnabled(false)
//                                                registerButton!!.setEnabled(false)
//                                                true
//                                            } else {
//                                                userEmailLayout!!.error = null
//                                                userMobileNumber!!.setEnabled(true)
//                                                userMobileNumber!!.setFocusableInTouchMode(true)
//                                                userPassword!!.setEnabled(true)
//                                                userPassword!!.setFocusableInTouchMode(true)
//                                                userConfirmPassword!!.setEnabled(true)
//                                                userConfirmPassword!!.setFocusableInTouchMode(true)
//                                                userOTP!!.setEnabled(true)
//                                                userOTP!!.setFocusableInTouchMode(true)
//                                                sendOTP!!.setEnabled(true)
//                                                registerButton!!.setEnabled(true)
//                                                false
//                                            }
                                        } else {
                                            Log.d(Constants.TAG, "Failed")
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<ResponseModel>,
                                        t: Throwable
                                    ) {
                                    }
                                })
                            }
                        },
                        DELAY
                    )
                }
            }
        )
        userMobileNumber!!.addTextChangedListener(
            object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                private var timer = Timer()
                private val DELAY: Long = 1000 // milliseconds
                override fun afterTextChanged(s: Editable) {
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                mobileNumber = userMobileNumber!!.getText().toString()
                                val call: Call<ResponseModel> =
                                    UserServiceAPIClient.instance!!.myApi.checkMobileNumber(
                                        mobileNumber
                                    )
                                call.enqueue(object : Callback<ResponseModel> {
                                    override fun onResponse(
                                        call: Call<ResponseModel>,
                                        response: Response<ResponseModel>
                                    ) {
                                        Log.d(Constants.TAG, response.toString())
                                        if (response.isSuccessful) {
                                            Log.d(
                                                Constants.TAG,
                                                getString(R.string.response_successfull)
                                            )
//                                            isMobileNumberExist =
//                                                if (response.body()!!.isMobileExist) {
//                                                    Log.d(Constants.TAG, "Already Exist")
//                                                    userMobileNumberLauout!!.error =
//                                                        getString(R.string.mobile_number_exist)
//                                                    userPassword!!.isEnabled = false
//                                                    userPassword!!.isFocusable = false
//                                                    userConfirmPassword!!.isEnabled = false
//                                                    userConfirmPassword!!.isFocusable = false
//                                                    userOTP!!.isEnabled = false
//                                                    userOTP!!.isFocusable = false
//                                                    sendOTP!!.isEnabled = false
//                                                    registerButton!!.isEnabled = false
//                                                    true
//                                                } else {
//                                                    userMobileNumberLauout!!.error = null
//                                                    userPassword!!.isEnabled = true
//                                                    userPassword!!.isFocusableInTouchMode = true
//                                                    userConfirmPassword!!.isEnabled = true
//                                                    userConfirmPassword!!.isFocusableInTouchMode =
//                                                        true
//                                                    userOTP!!.isEnabled = true
//                                                    userOTP!!.isFocusableInTouchMode = true
//                                                    sendOTP!!.isEnabled = true
//                                                    registerButton!!.isEnabled = true
//                                                    false
//                                                }
                                        } else {
                                            Log.d(Constants.TAG, "Failed")
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<ResponseModel>,
                                        t: Throwable
                                    ) {
                                    }
                                })
                            }
                        },
                        DELAY
                    )
                }
            }
        )
        userPassword!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus -> }
    }

    companion object {
        private const val TAG = "MSG"
    }
}