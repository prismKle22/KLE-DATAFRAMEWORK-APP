package com.samsung.prism.android.app.aidatacapture.activities.metaData

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.webkit.WebView
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.R.layout
import com.samsung.prism.android.app.aidatacapture.activities.DownloadAgreementActivity
import com.samsung.prism.android.app.aidatacapture.activities.SignConsentActivity
import com.samsung.prism.android.app.aidatacapture.activities.UserFeedBackActivity
import com.samsung.prism.android.app.aidatacapture.activities.UserUploadMenuActivity
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsHelper
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager

class MetaDataHumanCentricActivity : AppCompatActivity() {
    private var dataMajorCategory: AutoCompleteTextView? = null
    private var humanPresentView: AutoCompleteTextView? = null
    private var selfieView: AutoCompleteTextView? = null
    private var typeView: AutoCompleteTextView? = null
    private var aboveEighteenView: AutoCompleteTextView? = null
    private var props: AutoCompleteTextView? = null
    private var consentView: AutoCompleteTextView? = null
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
    private var humanPresentString: String? = null
    private var selfieString: String? = null
    private var typeString: String? = null
    private var aboveEighteenString: String? = null
    private var consentString: String? = null
    private var propsString: String? = null
    private var sharedPrefsManager: SharedPrefsManager? = null
    private var subLocationOther: TextInputEditText? = null
    private var lightingOther: TextInputEditText? = null
    private var subLocationOtherLayout: TextInputLayout? = null
    private var lightingOtherLayout: TextInputLayout? = null
    private var context: Context? = null
    private var catName: String? = null
    private var subCatName: String? = null
    private lateinit var catView: TextView
    private lateinit var subCatView: TextView
    private lateinit var chipCategory: Chip
    private lateinit var chipSubcategory: Chip
    private var sharedPrefsHelper: SharedPrefsHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_meta_data_human_centric)
        context = this
        backButton()
        initToolbar()
        toolbarClick()
        init()
    }

    // TODO : Remove error after selecting correct option.

    private fun initToolbar() {
        val toolbarName: TextView = findViewById(R.id.toolbar_name)
        toolbarName.text = "Human Centric"
    }

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
                    this@MetaDataHumanCentricActivity,
                    DownloadAgreementActivity::class.java
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        val dataCat = arrayOf("People", "Text", "Natural")
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
        val humanPresent = arrayOf("Yes")
        val selfie = arrayOf("Yes", "No")
        val type = arrayOf("Single", "Group", "Crowd", "Image of Human")
        val aboveEighteen = arrayOf("Yes", "No")
        val Consent = arrayOf("Yes")
        val Props = arrayOf("Pets", "None")
        val catAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            layout.dropdown_menu_popup_item,
            dataCat
        )
        val editTextFilledExposedDropdownCat =
            dataMajorCategory!!.findViewById<AutoCompleteTextView>(R.id.data_major_category)
        editTextFilledExposedDropdownCat.setAdapter(catAdapter)
        val locationAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            layout.dropdown_menu_popup_item,
            locationType
        )
        val editTextFilledExposedDropdownLocation =
            locationView!!.findViewById<AutoCompleteTextView>(R.id.location_type)
        editTextFilledExposedDropdownLocation.setAdapter(locationAdapter)
        locationView!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val location = locationView!!.text.toString()
            if (location == "Indoor") {
                val subLocationAdapter: ArrayAdapter<*> =
                    ArrayAdapter(context!!, layout.dropdown_menu_popup_item, subLocationIndoor)
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
                    ArrayAdapter(context!!, layout.dropdown_menu_popup_item, subLocationOutdoor)
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
            }
        }
        val lightingAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            layout.dropdown_menu_popup_item,
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
        val humanPresentAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            layout.dropdown_menu_popup_item,
            humanPresent
        )
        val editTextFilledExposedDropdownHumanPresent =
            humanPresentView!!.findViewById<AutoCompleteTextView>(R.id.humanPresent)
        editTextFilledExposedDropdownHumanPresent.setAdapter(humanPresentAdapter)
        val typeAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            layout.dropdown_menu_popup_item,
            type
        )
        val editTextFilledExposedDropdownType =
            typeView!!.findViewById<AutoCompleteTextView>(R.id.type)
        editTextFilledExposedDropdownType.setAdapter(typeAdapter)
        val propsAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            layout.dropdown_menu_popup_item,
            Props
        )
        val editTextFilledExposedDropdownProps =
            props!!.findViewById<AutoCompleteTextView>(R.id.props)
        editTextFilledExposedDropdownProps.setAdapter(propsAdapter)
        val selfieAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            layout.dropdown_menu_popup_item,
            selfie
        )
        val editTextFilledExposedDropdownSelfie =
            selfieView!!.findViewById<AutoCompleteTextView>(R.id.selfie)
        editTextFilledExposedDropdownSelfie.setAdapter(selfieAdapter)
        val aboveEighteenAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            layout.dropdown_menu_popup_item,
            aboveEighteen
        )
        val editTextFilledExposedDropdownAboveEighteen =
            aboveEighteenView!!.findViewById<AutoCompleteTextView>(R.id.above_eighteen)
        editTextFilledExposedDropdownAboveEighteen.setAdapter(aboveEighteenAdapter)
        val consentAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            layout.dropdown_menu_popup_item,
            Consent
        )
        val editTextFilledExposedDropdownConsent =
            consentView!!.findViewById<AutoCompleteTextView>(R.id.consent)
        editTextFilledExposedDropdownConsent.setAdapter(consentAdapter)
        val timingAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            layout.dropdown_menu_popup_item,
            timing
        )
        consentView!!.dismissDropDown()
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
                humanPresentString = humanPresentView!!.text.toString()
                typeString = typeView!!.text.toString()
                aboveEighteenString = aboveEighteenView!!.text.toString()
                propsString = props!!.text.toString()
                selfieString = selfieView!!.text.toString()
                consentString = "Yes"
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
                    selfieString,
                    typeString,
                    aboveEighteenString,
                    propsString,
                    consentString
                )
                startActivity(
                    Intent(
                        this@MetaDataHumanCentricActivity,
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
                    this@MetaDataHumanCentricActivity,
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
        humanPresentString = humanPresentView!!.text.toString()
        typeString = typeView!!.text.toString()
        aboveEighteenString = aboveEighteenView!!.text.toString()
        propsString = props!!.text.toString()
        selfieString = selfieView!!.text.toString()
        consentString = consentView!!.text.toString()
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
        if (humanPresentString!!.isEmpty()) {
            humanPresentView!!.error = getString(R.string.invalid_input)
            return false
        } else {
            humanPresentView!!.error = null
        }
        if (selfieString!!.isEmpty()) {
            selfieView!!.error = getString(R.string.invalid_input)
            return false
        } else {
            selfieView!!.error = null
        }
        if (typeString!!.isEmpty()) {
            typeView!!.error = getString(R.string.invalid_input)
            return false
        } else {
            typeView!!.error = null
        }
        if (aboveEighteenString!!.isEmpty()) {
            aboveEighteenView!!.error = getString(R.string.invalid_input)
            return false
        } else {
            aboveEighteenView!!.error = null
        }
        if (propsString!!.isEmpty()) {
            props!!.error = getString(R.string.invalid_input)
            return false
        } else {
            props!!.error = null
        }
        if (consentString!!.isEmpty()) {
            consentView!!.error = getString(R.string.invalid_input)
            return false
        } else {
            consentView!!.error = null
        }
        return !categoryString!!.isEmpty() && !subLocationString!!.isEmpty() && !locationString!!.isEmpty() && !timingString!!.isEmpty() && !lightingString!!.isEmpty() && !humanPresentString!!.isEmpty() && !selfieString!!.isEmpty() && !aboveEighteenString!!.isEmpty() && !consentString!!.isEmpty()
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(
                Intent(
                    this@MetaDataHumanCentricActivity,
                    SignConsentActivity::class.java
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
        humanPresentView = findViewById(R.id.humanPresent)
        selfieView = findViewById(R.id.selfie)
        typeView = findViewById(R.id.type)
        lightingOther = findViewById(R.id.lighting_other)
        subLocationOther = findViewById(R.id.sub_location_other)
        aboveEighteenView = findViewById(R.id.above_eighteen)
        consentView = findViewById(R.id.consent)
        props = findViewById(R.id.props)
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
        startActivity(Intent(this@MetaDataHumanCentricActivity, SignConsentActivity::class.java))
        finish()
    }
}