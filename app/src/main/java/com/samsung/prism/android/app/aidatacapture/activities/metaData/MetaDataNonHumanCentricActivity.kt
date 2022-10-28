package com.samsung.prism.android.app.aidatacapture.activities.metaData

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.DownloadAgreementActivity
import com.samsung.prism.android.app.aidatacapture.activities.UserCategoryActivity
import com.samsung.prism.android.app.aidatacapture.activities.UserFeedBackActivity
import com.samsung.prism.android.app.aidatacapture.activities.UserUploadMenuActivity
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsHelper
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager

class MetaDataNonHumanCentricActivity : AppCompatActivity() {
    private var dataMajorCategory: AutoCompleteTextView? = null
    private var timingView: AutoCompleteTextView? = null
    private var subLocationView: AutoCompleteTextView? = null
    private var locationView: AutoCompleteTextView? = null
    private var lightingView: AutoCompleteTextView? = null
    private var addMetaData: Button? = null
    private var categoryString: String? = null
    private var timingString: String? = null
    private var subLocationString: String? = null
    private var locationString: String? = null
    private var lightingString: String? = null
    private var sharedPrefsManager: SharedPrefsManager? = null
    private var subLocationOther: TextInputEditText? = null
    private var lightingOther: TextInputEditText? = null
    private var subLocationOtherLayout: TextInputLayout? = null
    private var lightingOtherLayout: TextInputLayout? = null
    private var context: Context? = null
    private lateinit var catView: TextView
    private lateinit var subCatView: TextView
    private var catName: String? = null
    private var subCatName: String? = null
    private lateinit var chipCategory: Chip
    private lateinit var chipSubcategory: Chip
    private var sharedPrefsHelper: SharedPrefsHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meta_data_non_human_centric)
        context = this
        backButton()
        initToolbar()
        toolbarClick()
        init()
    }

    private fun initToolbar() {
        val toolbarName: TextView
        toolbarName = findViewById(R.id.toolbar_name)
        toolbarName.text = "Non Human Centric"
    }

    // TODO : Remove error after selecting correct option.

    val screenSize: String
        get() {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels
            return width.toString() + "x" + height
        }

    private fun floatingActionButton() {
        val fab = findViewById<FloatingActionButton>(R.id.floating_action_button)
        val sharedPrefsManager = SharedPrefsManager(this)
        fab.setOnClickListener {
            sharedPrefsManager.saveBoolValue(SharedPrefsConstants.IS_FROM_LOGIN, false)
            startActivity(
                Intent(
                    this@MetaDataNonHumanCentricActivity,
                    DownloadAgreementActivity::class.java
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        val dataCat = arrayOf("Text", "Natural")
        val locationType = arrayOf("Indoor", "Outdoor")
        val subLocationIndoor = arrayOf("Home", "Office", "Mall", "School", "Theater", "Other")
        val subLocationOutdoor = arrayOf(
            "Road",
            "Beach",
            "In water",
            "Mountain",
            "Desert",
            "Natural Landscape",
            "Forest",
            "Other"
        )
        val timing =
            arrayOf("Sunrise", "Morning", "Forenoon", "Noon", "Afternoon", "Evening", "Night")
        val lighting = arrayOf("Incandescent", "Florescent", "Diffused", "Natural Light", "Other")
        val catAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            dataCat
        )
        val editTextFilledExposedDropdownCat =
            dataMajorCategory!!.findViewById<AutoCompleteTextView>(R.id.data_major_category)
        editTextFilledExposedDropdownCat.setAdapter(catAdapter)
        val locationAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            locationType
        )
        val editTextFilledExposedDropdownLocation =
            locationView!!.findViewById<AutoCompleteTextView>(R.id.location_type)
        editTextFilledExposedDropdownLocation.setAdapter(locationAdapter)
        locationView!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val location = locationView!!.text.toString()
            if (location == "Indoor") {
                val subLocationAdapter: ArrayAdapter<*> =
                    ArrayAdapter(context!!, R.layout.dropdown_menu_popup_item, subLocationIndoor)
                val editTextFilledExposedDropdownSubLoc =
                    subLocationView!!.findViewById<AutoCompleteTextView>(R.id.sub_location)
                editTextFilledExposedDropdownSubLoc.setAdapter(subLocationAdapter)
                subLocationView!!.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        val subLocation = subLocationView!!.text.toString()
                        if (subLocation == "Other") {
                            subLocationOtherLayout!!.visibility = View.VISIBLE
                            subLocationString = subLocationOther!!.text.toString()
                        } else {
                            subLocationOtherLayout!!.visibility = View.GONE
                        }
                    }
            } else if (location == "Outdoor") {
                val subLocationAdapter: ArrayAdapter<*> =
                    ArrayAdapter(context!!, R.layout.dropdown_menu_popup_item, subLocationOutdoor)
                val editTextFilledExposedDropdownSubLoc =
                    subLocationView!!.findViewById<AutoCompleteTextView>(R.id.sub_location)
                editTextFilledExposedDropdownSubLoc.setAdapter(subLocationAdapter)
                subLocationView!!.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        val subLocation = subLocationView!!.text.toString()
                        if (subLocation == "Other") {
                            subLocationOtherLayout!!.visibility = View.VISIBLE
                        } else {
                            subLocationOtherLayout!!.visibility = View.GONE
                        }
                    }
            }
        }
        val lightingAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            lighting
        )
        val editTextFilledExposedDropdownLighting =
            lightingView!!.findViewById<AutoCompleteTextView>(R.id.lighting)
        editTextFilledExposedDropdownLighting.setAdapter(lightingAdapter)
        lightingView!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val lighting = lightingView!!.text.toString()
            if (lighting == "Other") {
                lightingOtherLayout!!.visibility = View.VISIBLE
                lightingString = lightingOther!!.text.toString()
            } else {
                lightingOtherLayout!!.visibility = View.GONE
            }
        }
        val timingAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            timing
        )
        val editTextFilledExposedDropdownTiming =
            timingView!!.findViewById<AutoCompleteTextView>(R.id.timing)
        editTextFilledExposedDropdownTiming.setAdapter(timingAdapter)
        addMetaData!!.setOnClickListener {
            if (inputValidation()) {
                categoryString = dataMajorCategory!!.text.toString()
                timingString = timingView!!.text.toString()
                subLocationString = subLocationView!!.text.toString()
                lightingString = lightingView!!.text.toString()
                locationString = locationView!!.text.toString()
                val myDeviceModel = Build.MODEL
                val screenSize = screenSize
                sharedPrefsHelper!!.saveMetadataForHumanCentric(
                    categoryString,
                    locationString,
                    subLocationString,
                    timingString,
                    lightingString,
                    myDeviceModel,
                    screenSize,
                    "Null",
                    "Null",
                    "Null",
                    "Null",
                    "Null"
                )
                startActivity(
                    Intent(
                        this@MetaDataNonHumanCentricActivity,
                        UserUploadMenuActivity::class.java
                    )
                )
                finish()
            }
        }
    }

    private fun toolbarClick() {
        val feedback: ImageView
        val logout: ImageView
        feedback = findViewById(R.id.toolbar_feedback)
        logout = findViewById(R.id.toolbar_logout)
        feedback.setOnClickListener {
            startActivity(
                Intent(
                    this@MetaDataNonHumanCentricActivity,
                    UserFeedBackActivity::class.java
                )
            )
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

    fun inputValidation(): Boolean {
        categoryString = dataMajorCategory!!.text.toString()
        timingString = timingView!!.text.toString()
        subLocationString = subLocationView!!.text.toString()
        lightingString = lightingView!!.text.toString()
        locationString = locationView!!.text.toString()
        if (categoryString!!.isEmpty()) {
            dataMajorCategory!!.error = getString(R.string.invalid_input)
            return false
        } else {
            dataMajorCategory!!.error = null
        }
        if (locationString!!.isEmpty()) {
            locationView!!.error = getString(R.string.invalid_input)
            return false
        } else {
            locationView!!.error = null
        }
        if (subLocationString!!.isEmpty()) {
            subLocationView!!.error = getString(R.string.invalid_input)
            return false
        } else {
            subLocationView!!.error = null
        }
        if (timingString!!.isEmpty()) {
            timingView!!.error = getString(R.string.invalid_input)
            return false
        } else {
            timingView!!.error = null
        }
        if (lightingString!!.isEmpty()) {
            lightingView!!.error = getString(R.string.invalid_input)
            return false
        } else {
            lightingView!!.error = null
        }
        return !categoryString!!.isEmpty() && !subLocationString!!.isEmpty() && !locationString!!.isEmpty() && !timingString!!.isEmpty() && !lightingString!!.isEmpty()
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(
                Intent(
                    this@MetaDataNonHumanCentricActivity,
                    UserCategoryActivity::class.java
                )
            )
            finish()
        }
    }

    private fun init() {
        sharedPrefsHelper = SharedPrefsHelper(this)
        chipCategory = findViewById(R.id.chip_1)
        chipSubcategory = findViewById(R.id.chip_2)
        dataMajorCategory = findViewById(R.id.data_major_category)
        timingView = findViewById(R.id.timing)
        subLocationView = findViewById(R.id.sub_location)
        locationView = findViewById(R.id.location_type)
        lightingView = findViewById(R.id.lighting)
        addMetaData = findViewById(R.id.addMetaData)
        subLocationOther = findViewById(R.id.sub_location_other)
        lightingOther = findViewById(R.id.lighting_other)
        subLocationOtherLayout = findViewById(R.id.sub_location_other_layout)
        lightingOtherLayout = findViewById(R.id.lighting_other_layout)
        sharedPrefsManager = SharedPrefsManager(this)
        catView = findViewById(R.id.cat)
        subCatView = findViewById(R.id.subCat)
        catName = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.CATEGORY_NAME, "")
        subCatName = sharedPrefsManager!!.getStringValue(SharedPrefsConstants.SUBCATEGORY_NAME, "")
        chipCategory.setText("Category: $catName")
        chipSubcategory.setText("Subcategory: $subCatName")
        catView.setText(catName)
        subCatView.setText(subCatName)
    }

    override fun onBackPressed() {
        startActivity(
            Intent(
                this@MetaDataNonHumanCentricActivity,
                UserCategoryActivity::class.java
            )
        )
        finish()
    }
}