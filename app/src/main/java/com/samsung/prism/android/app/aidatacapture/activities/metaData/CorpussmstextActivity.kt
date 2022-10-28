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
import com.samsung.prism.android.app.aidatacapture.activities.SuperMetaDataText
import com.samsung.prism.android.app.aidatacapture.activities.TextMenuActivity
import com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.SubAudioMenuActivity
import com.samsung.prism.android.app.aidatacapture.activities.mySignConsent.EmailConsentActivity
import com.samsung.prism.android.app.aidatacapture.activities.textUploader.SubTextMenuActivity
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.databinding.ActivityMetaDataAudioBinding
import com.samsung.prism.android.app.aidatacapture.databinding.ContentCorpussmstextBinding
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsHelper
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import kotlinx.android.synthetic.main.speaker_layout.*

class CorpussmstextActivity : AppCompatActivity() {
    private lateinit var binding: ContentCorpussmstextBinding
    private lateinit var sharedPrefsManager: SharedPrefsManager
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentCorpussmstextBinding.inflate(layoutInflater)
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
        binding.layoutCorpus.addMetaData.setOnClickListener {
            if (inputLayoutSpeechValidation()) {
                saveData()
                sharedPrefsManager?.saveStringValue(SharedPrefsConstants.TEXT_META_DATA_TYPE, "corpus")
                val i:Intent?=Intent(this@CorpussmstextActivity, TextMenuActivity::class.java)
                i?.putExtra("corpusTextType","corpus")
                startActivity(i)
                finish()
            }
        }
        binding.layoutTextt.addMetaData.setOnClickListener {
            if (inputlayoutTexttValidation()) {
                saveData()
                sharedPrefsManager?.saveStringValue(SharedPrefsConstants.TEXT_META_DATA_TYPE, "text")
                val i:Intent?=Intent(this@CorpussmstextActivity, TextMenuActivity::class.java)
                i?.putExtra("corpusTextType","text")
                startActivity(i)
                finish()
            }
        }
        binding.layoutTranlation.addMetaData.setOnClickListener {
            if (inputlayoutTranlationValidation()) {
                saveData()
                sharedPrefsManager?.saveStringValue(SharedPrefsConstants.TEXT_META_DATA_TYPE, "translation")
                val i:Intent?=Intent(this@CorpussmstextActivity, TextMenuActivity::class.java)
                i?.putExtra("corpusTextType","translation")
                startActivity(i)
                finish()
            }
        }
    }

    private fun saveData() {
        val viewType = sharedPrefsManager.getIntValue(
                SharedPrefsConstants.VIEW_TYPE,
                Constants.ViewTypeForSuperMetaText.TEXT.ordinal
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
        val layout = binding.layoutTranlation
        val language = layout.language.text.toString()
        val standard = layout.standard.text.toString()
        val category = layout.category.text.toString()
        val mixedSource = layout.mixedSource.text.toString()
        val numbers = layout.numbers.text.toString()

        sharedPrefsHelper.saveTranslationMetaData(
                language,
                standard,
                category,
                mixedSource,
                numbers
        )
    }

    private fun getDataFromSpeechAudioView() {

        val layout = binding.layoutCorpus
        val type = layout.type.text.toString()
        val corpus_class =layout.corpusclass.text.toString()
        val personality =layout.personality.text.toString()
        val mood =layout.mood.text.toString()
        val age =layout.age.text.toString()
        val gender =layout.gender.text.toString()


        sharedPrefsHelper.saveCorpussmsMetaData(
                type,
                corpus_class,
                personality,
                mood,
                age,
                gender
        )
    }

    private fun getDataFromNoiseAudioView() {
        val layout = binding.layoutTextt
        val type = layout.type.text.toString()
        val texttclass = layout.texttclass.text.toString()
        val tag = layout.tag.text.toString()
        sharedPrefsHelper.saveTexttMetaData(
                type,
                texttclass,
                tag

        )
    }

    private fun inputlayoutTranlationValidation(): Boolean {
        return when {
            binding.layoutTranlation.language.text.isEmpty() -> {
                binding.layoutTranlation.language.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutTranlation.standard.text.isEmpty() -> {
                binding.layoutTranlation.standard.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutTranlation.category.text.isEmpty() -> {
                binding.layoutTranlation.category.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutTranlation.mixedSource.text.isEmpty() -> {
                binding.layoutTranlation.mixedSource.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutTranlation.numbers.text.isEmpty() -> {
                binding.layoutTranlation.numbers.error = getString(R.string.invalid_input)
                return false
            }
            else -> true
        }
    }

    private fun inputlayoutTexttValidation(): Boolean {
        return when {
            binding.layoutTextt.type.text.isEmpty() -> {
                binding.layoutTextt.type.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutTextt.texttclass.text!!.isEmpty() -> {
                binding.layoutTextt.texttclass.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutTextt.tag.text.isEmpty() -> {
                binding.layoutTextt.tag.error = getString(R.string.invalid_input)
                return false
            }

            else -> true
        }
    }

    private fun inputLayoutSpeechValidation(): Boolean {
        return when {

            binding.layoutCorpus.type.text.isEmpty() -> {
                binding.layoutCorpus.type.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutCorpus.corpusclass.text.isEmpty() -> {
                binding.layoutCorpus.corpusclass.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutCorpus.personality.text.isEmpty() -> {
                binding.layoutCorpus.personality.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutCorpus.mood.text.isEmpty() -> {
                binding.layoutCorpus.mood.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutCorpus.age.text.isEmpty() -> {
                binding.layoutCorpus.age.error = getString(R.string.invalid_input)
                return false
            }
            binding.layoutCorpus.gender.text.isEmpty() -> {
                binding.layoutCorpus.gender.error = getString(R.string.invalid_input)
                return false
            }

            else -> true
        }
    }

    private fun initViews() {
        val viewType = sharedPrefsManager.getIntValue(
                SharedPrefsConstants.VIEW_TYPE,
                Constants.ViewTypeForSuperMetaText.TEXT.ordinal
        )

        when (viewType) {
             1-> {
                initNoiseAudioView()
            }
            0 -> {
                initSpeechAudioView()
            }
            else -> {
                initSpeakerAudioView()
            }
        }
    }

    private fun initSpeakerAudioView() {
        binding.layoutTranlation.root.visibility = View.VISIBLE
        binding.layoutTextt.root.visibility = View.GONE
        binding.layoutCorpus.root.visibility = View.GONE
        initSpeakerDropDownAdapter()
    }

    private fun initSpeakerDropDownAdapter() {
        val language = arrayOf("English")
        val standard = arrayOf("Easy", "Medium","Hard")
        val category = arrayOf("News", "Fact", "Humour")
        val mixed_source = arrayOf("yes", "no")
        val numbers = arrayOf("yes","no")
        val languageAdapter: ArrayAdapter<*> = ArrayAdapter(
                this,
                R.layout.dropdown_menu_popup_item,
                language
        )
        val standardAdapter: ArrayAdapter<*> = ArrayAdapter(
                this,
                R.layout.dropdown_menu_popup_item,
                standard
        )
        val categoryAdapter: ArrayAdapter<*> = ArrayAdapter(
                this,
                R.layout.dropdown_menu_popup_item,
                category
        )
        val mixedSourceAdapter: ArrayAdapter<*> = ArrayAdapter(
                this,
                R.layout.dropdown_menu_popup_item,
                mixed_source
        )
        val numbersAdapter: ArrayAdapter<*> = ArrayAdapter(
                this,
                R.layout.dropdown_menu_popup_item,
                numbers
        )

        binding.layoutTranlation.language.setAdapter(languageAdapter)
        binding.layoutTranlation.standard.setAdapter(standardAdapter)
        binding.layoutTranlation.category.setAdapter(categoryAdapter)
        binding.layoutTranlation.mixedSource.setAdapter(mixedSourceAdapter)
        binding.layoutTranlation.numbers.setAdapter(numbersAdapter)

    }

    private fun initSpeechAudioView() {
        binding.layoutTranlation.root.visibility = View.GONE
        binding.layoutTextt.root.visibility = View.GONE
        binding.layoutCorpus.root.visibility = View.VISIBLE
        initSpeechDropDownAdapter()
    }

    private fun initSpeechDropDownAdapter() {
        val type = arrayOf("SMS", "IM message","Notification")
        val corpus_class = arrayOf("Personal", "Banking", "Transaction", "E-commerce", "Movie","Spam","Health","Travel","Others")
        val personality = arrayOf("Extroversion", "Neuroticism","Agreeableness","Conscientiousness","Openness")
        val moood = arrayOf("Cheerful/Happy", "Gloomy/Sad", "Humorous","Romantic","Calm","Hopeful","Angry","Fearful", "Tense", "Lonely", "Occupied")
//        val language = arrayOf(
//                "English",
//                "Hindi",
//                "Telugu",
//                "Tamil",
//                "Marathi",
//                "Punjabi",
//                "Kannada",
//                "Malayalam",
//                "Urdu",
//                "Bengali"
//        )
        val age = arrayOf("input", "box")
        val gender = arrayOf("Male", "Female","Other")

        val genderAdapter: ArrayAdapter<*> = ArrayAdapter(
                this,
                R.layout.dropdown_menu_popup_item,
                gender
        )
        val typeAdapter: ArrayAdapter<*> = ArrayAdapter(
                this,
                R.layout.dropdown_menu_popup_item,
                type
        )
        val corpusClassAdapter: ArrayAdapter<*> = ArrayAdapter(
                this,
                R.layout.dropdown_menu_popup_item,
                corpus_class
        )
        val personalityAdapter: ArrayAdapter<*> = ArrayAdapter(
                this,
                R.layout.dropdown_menu_popup_item,
                personality
        )
        val moodAdapter: ArrayAdapter<*> = ArrayAdapter(
                this,
                R.layout.dropdown_menu_popup_item,
                moood
        )
        val ageAdapter: ArrayAdapter<*> = ArrayAdapter(
                this,
                R.layout.dropdown_menu_popup_item,
                age
        )


        binding.layoutCorpus.type.setAdapter(typeAdapter)
        binding.layoutCorpus.corpusclass.setAdapter(corpusClassAdapter)
        binding.layoutCorpus.personality.setAdapter(personalityAdapter)
        binding.layoutCorpus.mood.setAdapter(moodAdapter)
        binding.layoutCorpus.age.setAdapter(ageAdapter)
        binding.layoutCorpus.gender.setAdapter(genderAdapter)
    }

    private fun initNoiseAudioView() {
        binding.layoutTranlation.root.visibility = View.GONE
        binding.layoutTextt.root.visibility = View.VISIBLE
        binding.layoutCorpus.root.visibility = View.GONE
        initNoiseDropDownAdapter()
    }

    private fun initNoiseDropDownAdapter() {
        val type = arrayOf("Notes","General Text")
        val text_class = arrayOf("Contact", "Location", "Calendar", "Reminder", "Notes", "User activity like jog/run/nap")
        val tag = arrayOf("input Text","Response Text")

        val typeAdapter: ArrayAdapter<*> = ArrayAdapter(
                this,
                R.layout.dropdown_menu_popup_item,
                type
        )
        val textClassAdapter: ArrayAdapter<*> = ArrayAdapter(
                this,
                R.layout.dropdown_menu_popup_item,
                text_class
        )
        val tagAdapter: ArrayAdapter<*> = ArrayAdapter(
                this,
                R.layout.dropdown_menu_popup_item,
                tag
        )
        binding.layoutTextt.type.setAdapter(typeAdapter)
        binding.layoutTextt.texttclass.setAdapter(textClassAdapter)
        binding.layoutTextt.tag.setAdapter(tagAdapter)
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
            startActivity(Intent(Activity@ this, SuperMetaDataText::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(Activity@ this, SuperMetaDataText::class.java))
        finish()
    }
}