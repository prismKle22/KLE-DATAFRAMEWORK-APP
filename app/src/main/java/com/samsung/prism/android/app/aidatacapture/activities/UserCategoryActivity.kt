package com.samsung.prism.android.app.aidatacapture.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.repos.RetrofitHelper
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import java.util.*

class UserCategoryActivity : AppCompatActivity() {
    private lateinit var addClass: Button
    private lateinit var categoryName: AutoCompleteTextView
    private lateinit var subcategoryName: AutoCompleteTextView
    private var categoryString: String? = null
    private var subcategoryString: String? = null
    private var context: Context? = null
    private var categoryModelList: List<String>? = null
    private var sharedPrefsManager: SharedPrefsManager? = null
    private var retrofitHelper: RetrofitHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_name)
        context = this
        categoryModelList = ArrayList()
        retrofitHelper = RetrofitHelper()
        if (ContextCompat.checkSelfPermission(
                this@UserCategoryActivity,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this@UserCategoryActivity,
                Manifest.permission_group.MICROPHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission_group.MICROPHONE),
                100
            )
        }
        toolbarClick()
        initToolbar()
        backButton()
        init()
    }

    override fun onStart() {
        super.onStart()
        // String[] subCatWithoutText = new String[]{"People Centric", Constants.NON_PEOPLE_CENTRIC};
        //String[] subCatTextCentric = new String[]{"Text Centric"};

        val catAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            retrofitHelper!!.getCategoryList(this).also {
                categoryModelList = it
            })
        val editTextFilledExposedDropdownCat =
            categoryName!!.findViewById<AutoCompleteTextView>(R.id.category)
        editTextFilledExposedDropdownCat.setAdapter(catAdapter)
        val subLocationAdapter: ArrayAdapter<*> = ArrayAdapter(
            context!!,
            R.layout.dropdown_menu_popup_item,
            retrofitHelper!!.getSubCategoryList(this)
        )
        val editTextFilledExposedDropdownSubLoc =
            subcategoryName!!.findViewById<AutoCompleteTextView>(R.id.subCategory)
        editTextFilledExposedDropdownSubLoc.setAdapter(subLocationAdapter)
    }

    private fun init() {
        addClass = findViewById(R.id.addClass)
        categoryName = findViewById(R.id.category)
        subcategoryName = findViewById(R.id.subCategory)
        sharedPrefsManager = SharedPrefsManager(this)
        val sharedPrefsManager = SharedPrefsManager(this)
        addClass.setOnClickListener(View.OnClickListener {
            categoryString = categoryName.text.toString()
            subcategoryString = subcategoryName.text.toString()

            if (subcategoryString!!.isEmpty()) {
                categoryName.setError(getString(R.string.invalid_input))
            } else if (subcategoryString!!.isEmpty()) {
                categoryName.setError(getString(R.string.invalid_input))
            } else {
                val intent = Intent(this@UserCategoryActivity, UserMenu::class.java)
                sharedPrefsManager.saveStringValue(
                    SharedPrefsConstants.CATEGORY_NAME, categoryString
                )
                sharedPrefsManager.saveStringValue(
                    SharedPrefsConstants.SUBCATEGORY_NAME,
                    subcategoryString
                )
                startActivity(intent)
                finish()
            }
        })
    }

    private fun initToolbar() {
        val toolbarName: TextView
        toolbarName = findViewById(R.id.toolbar_name)
        toolbarName.text = "Categories"
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(this@UserCategoryActivity, UserMenuActivity::class.java))
            finish()
        }
    }

    private fun toolbarClick() {
        val feedback: ImageView = findViewById(R.id.toolbar_feedback)
        val logout: ImageView = findViewById(R.id.toolbar_logout)
        feedback.visibility = View.VISIBLE
        feedback.setOnClickListener {
            startActivity(Intent(this@UserCategoryActivity, UserFeedBackActivity::class.java))
            finish()
        }
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

    override fun onBackPressed() {
        startActivity(Intent(this@UserCategoryActivity, UserMenuActivity::class.java))
        finish()
    }
}