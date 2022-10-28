package com.samsung.prism.android.app.aidatacapture.activities.metaData

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.AudioMenuActivity
import com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.SubAudioMenuActivity
import com.samsung.prism.android.app.aidatacapture.activities.mySignConsent.EmailConsentActivity
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.databinding.ActivityMetaDataAudioBinding
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsHelper
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import kotlinx.android.synthetic.main.speaker_layout.*

class MetaDataAudioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMetaDataAudioBinding
    private lateinit var sharedPrefsManager: SharedPrefsManager
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMetaDataAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefsManager = SharedPrefsManager(this)
        sharedPrefsHelper = SharedPrefsHelper(this)
        getDeviceModel()
        initViews()
        backButton()
        toolbarClick()
        setOnClickListeners()
    }

    // TODO : Remove error after selecting correct option.

    private fun getDeviceModel(): String {
        return Build.MODEL
    }

    private fun setOnClickListeners() {
        binding.layoutSpeech.addMetaData.setOnClickListener {
            if (inputLayoutSpeechValidation()) {
                saveData()

                val i:Intent?=Intent(this@MetaDataAudioActivity, SubAudioMenuActivity::class.java)
                i?.putExtra("audioType","speech")
                startActivity(i)
                finish()
            }
        }
        binding.layoutNoise.addMetaData.setOnClickListener {
            if (inputLayoutNoiseValidation()) {
                saveData()


                val i:Intent?=Intent(this@MetaDataAudioActivity, SubAudioMenuActivity::class.java)
                i?.putExtra("audioType","noise")
                startActivity(i)
                finish()
            }
        }
        binding.layoutSpeaker.addMetaData.setOnClickListener {
            if (inputLayoutSpeakerValidation()) {
                saveData()
                val i:Intent?=Intent(this@MetaDataAudioActivity, SubAudioMenuActivity::class.java)
                i?.putExtra("audioType","speaker")
                startActivity(i)
                finish()
            }
        }
    }

    private fun saveData() {
        val viewType = sharedPrefsManager.getIntValue(
            SharedPrefsConstants.VIEW_TYPE,
            Constants.ViewTypeForAudio.NOISE.ordinal
        )

        when (viewType) {
            0 -> {
                getDataFromNoiseAudioView()
            }
            1 -> {
                getDataFromSpeechAudioView()
            }
            else -> {
                getDataFromSpeakerAudioView()
            }
        }
    }

    private fun getDataFromSpeakerAudioView() {
        val layout = binding.layoutSpeaker
        val sourceDistance = layout.sourceDistance.text.toString()
        val model = getDeviceModel()
        val noise = layout.noise.text.toString()
        val source = layout.source.text.toString()
        val location = layout.location.text.toString()
        sharedPrefsHelper.saveSpeakerMetaData(
           model,
            model,
            noise,
            sourceDistance,
            location,
            "Duration",
            source
        )
    }

    private fun getDataFromSpeechAudioView() {
        val layout = binding.layoutSpeech
        val gender = layout.gender.text.toString()
        val ageGroup = layout.ageGroup.text.toString()
        val noise = layout.noise.text.toString()
        val sourceDistance = layout.sourceDistance.text.toString()
        val device = getDeviceModel()
        val language = layout.languages.text.toString()
        val location = layout.location.text.toString()
        val multipleSpeakers = layout.multipleSpeakers.text.toString()
        sharedPrefsHelper.saveSpeechMetaData(
            gender,
            ageGroup,
            noise,
            sourceDistance,
            device,
            language,
            location,
            multipleSpeakers
        )
    }

    private fun getDataFromNoiseAudioView() {
        val layout = binding.layoutNoise
        val sourceDistance = layout.sourceDistance.text.toString()
        val device = getDeviceModel()
        val noiseObject = layout.noiseObject.text.toString()
        val location = layout.location.text.toString()
        val multipleNoise = layout.multipleNoise.text.toString()
        sharedPrefsHelper.saveNoiseMetaData(
            sourceDistance,
            multipleNoise,
            noiseObject,
            location,
            device
        )
    }

    private fun inputLayoutSpeakerValidation(): Boolean {
        return when {
            binding.layoutSpeaker.sourceDistance.text.isEmpty() -> {
                binding.layoutSpeaker.sourceDistance.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutSpeaker.noise.text.isEmpty() -> {
                binding.layoutSpeaker.noise.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutSpeaker.source.text.isEmpty() -> {
                binding.layoutSpeaker.source.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutSpeaker.location.text.isEmpty() -> {
                binding.layoutSpeaker.location.error = getString(R.string.invalid_input)
                return false
            }
            else -> true
        }
    }

    private fun inputLayoutNoiseValidation(): Boolean {
        return when {
            binding.layoutNoise.sourceDistance.text.isEmpty() -> {
                binding.layoutNoise.sourceDistance.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutNoise.noiseObject.text!!.isEmpty() -> {
                binding.layoutNoise.noiseObjectLayout.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutNoise.location.text.isEmpty() -> {
                binding.layoutNoise.location.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutNoise.multipleNoise.text.isEmpty() -> {
                binding.layoutNoise.multipleNoise.error = getString(R.string.invalid_input)
                return false
            }
            else -> true
        }
    }

    private fun inputLayoutSpeechValidation(): Boolean {
        return when {
            binding.layoutSpeech.gender.text.isEmpty() -> {
                binding.layoutSpeech.gender.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutSpeech.ageGroup.text.isEmpty() -> {
                binding.layoutSpeech.ageGroup.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutSpeech.noise.text.isEmpty() -> {
                binding.layoutSpeech.noise.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutSpeech.sourceDistance.text.isEmpty() -> {
                binding.layoutSpeech.sourceDistance.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutSpeech.languages.text.isEmpty() -> {
                binding.layoutSpeech.languages.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutSpeech.location.text.isEmpty() -> {
                binding.layoutSpeech.location.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutSpeech.multipleSpeakers.text.isEmpty() -> {
                binding.layoutSpeech.multipleSpeakers.error = getString(R.string.invalid_input)
                return false
            }
            else -> true
        }
    }

    private fun initViews() {
        val viewType = sharedPrefsManager.getIntValue(
            SharedPrefsConstants.VIEW_TYPE,
            Constants.ViewTypeForAudio.NOISE.ordinal
        )

        when (viewType) {
            0 -> {
                initNoiseAudioView()
            }
            1 -> {
                initSpeechAudioView()
            }
            else -> {
                initSpeakerAudioView()
            }
        }
    }

    private fun initSpeakerAudioView() {
        binding.layoutSpeaker.root.visibility = View.VISIBLE
        binding.layoutNoise.root.visibility = View.GONE
        binding.layoutSpeech.root.visibility = View.GONE
        initSpeakerDropDownAdapter()
    }

    private fun initSpeakerDropDownAdapter() {
        val sourceDistance = arrayOf("Near", "Mid", "Far")
        val noise = arrayOf("Yes", "No")
        val source = arrayOf("Laptop", "Speaker", "Mobile")
        val location = arrayOf("Indoor", "Outdoor")

        val locationAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            location
        )
        val sourceAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            source
        )
        val noiseAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            noise
        )
        val sourceDistanceAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            sourceDistance
        )

        binding.layoutSpeaker.location.setAdapter(locationAdapter)
        binding.layoutSpeaker.noise.setAdapter(noiseAdapter)
        binding.layoutSpeaker.source.setAdapter(sourceAdapter)
        binding.layoutSpeaker.sourceDistance.setAdapter(sourceDistanceAdapter)
    }

    private fun initSpeechAudioView() {
        binding.layoutSpeaker.root.visibility = View.GONE
        binding.layoutNoise.root.visibility = View.GONE
        binding.layoutSpeech.root.visibility = View.VISIBLE
        initSpeechDropDownAdapter()
    }

    private fun initSpeechDropDownAdapter() {
        val gender = arrayOf("Male", "Female")
        val ageGroup = arrayOf("0-5", "5-15", "15-25", "25-50", "50+")
        val noise = arrayOf("Yes", "No")
        val sourceDistance = arrayOf("Near", "Mid", "Far")
        val language = arrayOf(
            "English",
            "Hindi",
            "Telugu",
            "Tamil",
            "Marathi",
            "Punjabi",
            "Kannada",
            "Malayalam",
            "Urdu",
            "Bengali"
        )
        val location = arrayOf("Indoor", "Outdoor")
        val multipleSpeakers = arrayOf("Yes", "No")

        val genderAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            gender
        )
        val ageGroupAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            ageGroup
        )
        val noiseAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            noise
        )
        val sourceDistanceAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            sourceDistance
        )
        val languageAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            language
        )
        val locationAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            location
        )
        val multipleSpeakerAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            multipleSpeakers
        )

        binding.layoutSpeech.noise.setAdapter(noiseAdapter)
        binding.layoutSpeech.ageGroup.setAdapter(ageGroupAdapter)
        binding.layoutSpeech.languages.setAdapter(languageAdapter)
        binding.layoutSpeech.location.setAdapter(locationAdapter)
        binding.layoutSpeech.multipleSpeakers.setAdapter(multipleSpeakerAdapter)
        binding.layoutSpeech.sourceDistance.setAdapter(sourceDistanceAdapter)
        binding.layoutSpeech.gender.setAdapter(genderAdapter)
    }

    private fun initNoiseAudioView() {
        binding.layoutSpeaker.root.visibility = View.GONE
        binding.layoutNoise.root.visibility = View.VISIBLE
        binding.layoutSpeech.root.visibility = View.GONE
        initNoiseDropDownAdapter()
    }

    private fun initNoiseDropDownAdapter() {
        val sourceDistance = arrayOf("Near", "Mid", "Far")
        val location = arrayOf("Indoor", "Outdoor")
        val multipleNoise = arrayOf("Yes", "No")

        val sourceDistanceAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            sourceDistance
        )
        val locationAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            location
        )
        val multipleNoiseAdapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            multipleNoise
        )
        binding.layoutNoise.sourceDistance.setAdapter(sourceDistanceAdapter)
        binding.layoutNoise.location.setAdapter(locationAdapter)
        binding.layoutNoise.multipleNoise.setAdapter(multipleNoiseAdapter)
    }

    private fun toolbarClick() {
        val feedback: ImageView = findViewById(R.id.toolbar_feedback)
        feedback.visibility = View.GONE
        feedback.visibility = View.GONE
        val logout: ImageView = findViewById(R.id.toolbar_logout)
        logout.setOnClickListener {
            val dialogue = Dialogues()
            dialogue.logoutDialogue(this)
        }
        feedback.setOnLongClickListener {
            Toast.makeText(this, getString(R.string.feedback_msg), Toast.LENGTH_SHORT).show()
            true
        }
        logout.setOnLongClickListener {
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(Activity@ this, AudioMenuActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(Activity@ this, AudioMenuActivity::class.java))
        finish()
    }
}