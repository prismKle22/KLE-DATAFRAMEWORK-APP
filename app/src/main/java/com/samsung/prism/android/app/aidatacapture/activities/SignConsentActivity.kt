package com.samsung.prism.android.app.aidatacapture.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.mySignConsent.MySignConsentActivity
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.IntentConstants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.databinding.ActivitySignConsentBinding
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignConsentActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignConsentBinding
    private lateinit var context: Context
    private lateinit var sharedPrefsManager : SharedPrefsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignConsentBinding.inflate(layoutInflater)
        context = this;
        setContentView(binding.root)
        sharedPrefsManager = SharedPrefsManager(context)
        setOnClickListeners()
        backButton()
        customTextView()
        toolbarClick()
    }

    private fun customInputDialogueName() {
        MaterialDialog(this).show {
            title(R.string.input_dialogue_title_name)
            input(
                hint = getString(R.string.name_consent),
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            ) { _, text ->
                sharedPrefsManager.saveStringValue(SharedPrefsConstants.CONSENT_NAME,text.toString())
                customInputDialoguePlace()
            }
            positiveButton(R.string.agree)
            negativeButton(R.string.disagree)
        }
    }

    private fun customInputDialoguePlace() {
        MaterialDialog(this).show {
            title(R.string.input_dialogue_title_place)
            input(
                    hint = getString(R.string.place_consent),
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            ) { _, text ->
                sharedPrefsManager.saveStringValue(SharedPrefsConstants.CONSENT_PLACE,text.toString())
                customInputDialogueEmail()
            }
            positiveButton(R.string.agree)
            negativeButton(R.string.disagree)
        }
    }

    fun isEmailValid(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }
    private fun emailValidator(email: String?): Boolean {
        val pattern: Pattern = Pattern.compile(Constants.EMAIL_PATTERN)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun customInputDialogueEmail() {
        MaterialDialog(this).show {
            title(R.string.input_dialogue_title_email)
            input(
            ) { _, text ->
                sharedPrefsManager.saveStringValue(SharedPrefsConstants.CONSENT_EMAIL,text.toString())
                if(!emailValidator(sharedPrefsManager.getStringValue(SharedPrefsConstants.CONSENT_EMAIL,""))) {
                    Toast.makeText(context, "Invalid Email, Please Re-enter email", Toast.LENGTH_SHORT).show()
                    customInputDialogueEmail()
                }
                else
                {
                    startActivity(Intent(this@SignConsentActivity, MySignConsentActivity::class.java))
                    finish()
                }

            }
            positiveButton(R.string.agree)
            negativeButton(R.string.disagree)
        }
    }


    private fun setOnClickListeners() {
        binding.btnSignConsent.setOnClickListener {
            customInputDialogueName()
        }
    }




    fun customTextView() {
        val spanTxt = SpannableStringBuilder(
            getString(R.string.I_give_consent_text)
        )
        spanTxt.append("agreement       ")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(
                    Intent(
                        this@SignConsentActivity,
                        PrivacyPolicyActivity::class.java
                    ).putExtra(IntentConstants.CONSENT_FILE_NAME, "consent.pdf")
                )
            }
        }, spanTxt.length - "Term of services".length, spanTxt.length - 6, 0)
        binding.uploadingTextCapture.movementMethod = LinkMovementMethod.getInstance()
        binding.uploadingTextCapture.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(this@SignConsentActivity, UserCategoryActivity::class.java))
            finish()
        }
    }

    private fun toolbarClick() {
        val feedback: ImageView = findViewById(R.id.toolbar_feedback)
        val logout: ImageView = findViewById(R.id.toolbar_logout)
        feedback.setOnClickListener {
            startActivity(Intent(this@SignConsentActivity, UserFeedBackActivity::class.java))
            finish()
        }
        logout.setOnClickListener {
            val dialogue = Dialogues()
            dialogue.logoutDialogue(context)
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

    override fun onBackPressed() {
        startActivity(Intent(this@SignConsentActivity, UserCategoryActivity::class.java))
        finish()
    }


}