package com.samsung.prism.android.app.aidatacapture.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.adminRelatedActivities.AdminViewUsers
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.databinding.ActivityUserDashboardBinding
import com.samsung.prism.android.app.aidatacapture.databinding.StatItemLayoutBinding
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.models.AudioCountModel
import com.samsung.prism.android.app.aidatacapture.models.OverallCountModel
import com.samsung.prism.android.app.aidatacapture.models.UserCountDashboard
import com.samsung.prism.android.app.aidatacapture.others.apiClients.AdminServiceAPIClient
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import kotlinx.android.synthetic.main.stats_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class UserDashboard : AppCompatActivity() {
    var userName: TextView? = null
    var userEmail: TextView? = null
    var sharedPrefsManager: SharedPrefsManager? = null
    var goBack: MaterialButton? = null
    private var exit = false
    private lateinit var binding: ActivityUserDashboardBinding
    var context: Context? = null
    var viewType: Enum<ViewType> = ViewType.CHART
    val overallValuesData: MutableMap<String, String> = mutableMapOf()
    val audiooverallValuesData: MutableMap<String, String> = mutableMapOf()
    val imageoverallValuesData: MutableMap<String, String> = mutableMapOf()
    var overallContribViewType: Enum<OverAllStatus> = OverAllStatus.NOTCHECKED
    var audioContribContribViewType: Enum<AudioStatus> = AudioStatus.NOTCHECKED
    var imageContribViewType: Enum<ImageStatus> = ImageStatus.NOTCHECKED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this
        toolbarClick()
        setOnClickListeners()
        sharedPrefsManager =
            SharedPrefsManager(
                this
            )
        initScreen()
        setWelcomeMessage()
        initOverAllContributions()
        initAudioContributions()
        initImageContributions()
        initAltheViews()
    }

    private fun initScreen() {
        if(sharedPrefsManager?.getBoolValue(SharedPrefsConstants.IS_IT_USER_PROFILE,false) == true){
            binding.dashboardMenu.visibility=View.GONE
            binding.userProfile.visibility=View.VISIBLE
            initUserProfile()
            backButtonUserProfile()
        }else{
            binding.dashboardMenu.visibility=View.VISIBLE
            binding.userProfile.visibility=View.GONE
            backButton()
        }
    }

    private fun initUserProfile() {
        val name =   sharedPrefsManager?.getStringValue(SharedPrefsConstants.USER_FULL_NAME,"User Name")
        binding.userName.text = name
        binding.userEmail.text =
            sharedPrefsManager?.getStringValue(SharedPrefsConstants.USER_EMAIL,"username@gmail.com")
        binding.contributionText.setText("$name's Contribution")
        binding.callUser.setOnClickListener {
                val number = sharedPrefsManager?.getStringValue(SharedPrefsConstants.USER_MOBILE,"9999999999")
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                startActivity(intent)
        }
        binding.emailUser.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            val email = sharedPrefsManager?.getStringValue(SharedPrefsConstants.USER_EMAIL,"ganeshhulyal@gmail.com")
            intent.type = "text/plain"
            intent.data = Uri.parse("mailto:$email");
            //intent.putExtra(Intent.EXTRA_EMAIL, email)
            intent.putExtra(Intent.EXTRA_SUBJECT, "Admin - SEED Lab")
            intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.")

            startActivity(Intent.createChooser(intent, "Send Email"))
        }

    }

    private fun initAltheViews() {
        binding.overallContributionView.loadingAllStats.visibility = View.GONE
        binding.audioContributionView.loadingAllStats.visibility = View.GONE
        binding.audioContributionView.toogleAllStatus.visibility = View.GONE
        binding.overallContributionView.toogleAllStatus.visibility = View.GONE
        binding.imageContributionView.loadingAllStats.visibility = View.GONE
        binding.imageContributionView.toogleAllStatus.visibility = View.GONE
    }

    private fun setOverAllValues(overallValuesData: MutableMap<String, String>?) {
        viewType = ViewType.VALUES
        binding.overallContributionView.toogleAllStatus.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_baseline_pie_chart_24
            )
        )
        binding.overallContributionView.statusValuesContainer.visibility = View.VISIBLE
        overallValuesData!!.forEach {
            val valueCardBinding =
                StatItemLayoutBinding.inflate(
                    layoutInflater,
                    binding.overallContributionView.statusValuesContainer,
                    false
                )
            valueCardBinding.label.text = it.key
            valueCardBinding.value.text = it.value
            valueCardBinding.valueInPercentage.visibility = View.GONE
            binding.overallContributionView.statusValuesContainer.addView(valueCardBinding.root)
        }
        overallValuesData.clear()
    }

    private fun setAudioValues(overallValuesData: MutableMap<String, String>?) {
        viewType = ViewType.VALUES
        binding.audioContributionView.toogleAllStatus.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_baseline_pie_chart_24
            )
        )
        binding.audioContributionView.statusValuesContainer.visibility = View.VISIBLE
        overallValuesData!!.forEach {
            val valueCardBinding =
                StatItemLayoutBinding.inflate(
                    layoutInflater,
                    binding.audioContributionView.statusValuesContainer,
                    false
                )
            valueCardBinding.label.text = it.key
            valueCardBinding.value.text = it.value
            valueCardBinding.valueInPercentage.visibility = View.GONE
            binding.audioContributionView.statusValuesContainer.addView(valueCardBinding.root)
        }
        overallValuesData.clear()
    }

    private fun setImageValues(overallValuesData: MutableMap<String, String>?) {
        viewType = ViewType.VALUES
        binding.imageContributionView.toogleAllStatus.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_baseline_pie_chart_24
            )
        )
        binding.imageContributionView.statusValuesContainer.visibility = View.VISIBLE
        overallValuesData!!.forEach {
            val valueCardBinding =
                StatItemLayoutBinding.inflate(
                    layoutInflater,
                    binding.imageContributionView.statusValuesContainer,
                    false
                )
            valueCardBinding.label.text = it.key
            valueCardBinding.value.text = it.value
            valueCardBinding.valueInPercentage.visibility = View.GONE
            binding.imageContributionView.statusValuesContainer.addView(valueCardBinding.root)
        }
        overallValuesData.clear()
    }


    enum class ViewType {
        CHART,
        VALUES
    }

    private fun setWelcomeMessage() {

        val date = Calendar.getInstance()
        val timeOfDay = date.get(Calendar.HOUR_OF_DAY)
        var message = ""

        if (timeOfDay in 0..11) {
            message = getString(R.string.good_morning)
        } else if (timeOfDay in 12..15) {
            message = getString(R.string.good_afternoon)
        } else if (timeOfDay in 16..20) {
            message = getString(R.string.good_evening)
        } else if (timeOfDay in 21..23) {
            message = getString(R.string.good_night)
        }

        binding.welcomeMessage.text = getString(R.string.hi_template, message)

    }


    private fun setOnClickListeners() {
        binding.changeLanguage.setOnClickListener {
            startActivity(Intent(Activity@ this, SelectLanguageActivity::class.java))
            sharedPrefsManager!!.saveBoolValue(SharedPrefsConstants.IS_LANGUAGE_SET,false)
            sharedPrefsManager!!.saveBoolValue(SharedPrefsConstants.IS_FROM_DASHBOARD, true)
        }
        binding.goToMenu.setOnClickListener {
            startActivity(Intent(Activity@ this, UserMenuActivity::class.java))
            finish()
        }
        binding.giveFeedback.setOnClickListener {
            startActivity(Intent(Activity@ this, UserFeedBackActivity::class.java))
            finish()
        }
        binding.logout.setOnClickListener {
            Dialogues().logoutDialogue(this)
        }

        binding.toggleOverallContrib.setOnClickListener {

            if (overallContribViewType == OverAllStatus.NOTCHECKED) {
                overallContribViewType = OverAllStatus.CHECKED
                binding.overallContributionView.overallStatusChart.visibility = View.VISIBLE
                setOverAllValues(overallValuesData)
            } else {
                overallContribViewType = OverAllStatus.NOTCHECKED
                binding.overallContributionView.overallStatusChart.visibility = View.GONE
            }
        }
        binding.toggleAudioContrib.setOnClickListener {
            if (audioContribContribViewType == AudioStatus.NOTCHECKED) {
                audioContribContribViewType = AudioStatus.CHECKED
                binding.audioContributionView.overallStatusChart.visibility = View.VISIBLE
                setAudioValues(audiooverallValuesData)
            } else {
                audioContribContribViewType = AudioStatus.NOTCHECKED
                binding.audioContributionView.overallStatusChart.visibility = View.GONE
            }
        }
        binding.toggleImageContrib.setOnClickListener {
            if (imageContribViewType == ImageStatus.NOTCHECKED) {
                imageContribViewType = ImageStatus.CHECKED
                binding.imageContributionView.overallStatusChart.visibility = View.VISIBLE
                setImageValues(imageoverallValuesData)
            } else {
                imageContribViewType = ImageStatus.NOTCHECKED
                binding.imageContributionView.overallStatusChart.visibility = View.GONE
            }
        }

    }

    enum class OverAllStatus {
        CHECKED,
        NOTCHECKED
    }

    enum class AudioStatus {
        CHECKED,
        NOTCHECKED
    }

    enum class ImageStatus {
        CHECKED,
        NOTCHECKED
    }

    private fun initOverAllContributions() {
        val email: String =
            sharedPrefsManager!!.getStringValue(SharedPrefsConstants.USER_EMAIL, "abc@gmail.com")!!
        val call: Call<OverallCountModel> =
            AdminServiceAPIClient.instance!!.myApi.userDashboardCount(email)
        call.enqueue(object : Callback<OverallCountModel?> {
            override fun onResponse(
                call: Call<OverallCountModel?>,
                response: Response<OverallCountModel?>
            ) {

//                val responseBody = response.body()!!

                Log.d(Constants.TAG, response.toString())
//                Toast.makeText(this@UserDashboard,response.body()?.toString(),Toast.LENGTH_LONG).show()
                if (response.isSuccessful) {
                    overallValuesData[Constants.TOTAL_AUDIO] =
                            (Integer.parseInt(response.body()?.totalAudioNoise!!)+Integer.parseInt(response.body()?.totalAudioSpeech!!)+Integer.parseInt(response.body()?.totalAudioSpeaker!!)).toString()

                    overallValuesData[Constants.TOTAL_IMAGE_VIDEO] = response.body()?.totalImages!!
                    var totalContrib =
                        Integer.parseInt(
                            response.body()?.totalImages
                        ) +Integer.parseInt(response.body()?.totalAudioNoise!!)+Integer.parseInt(response.body()?.totalAudioSpeech!!)+Integer.parseInt(response.body()?.totalAudioSpeaker!!)

                    binding.overallContributionView.totalResponse.text = getString(
                        R.string.overall_contrib, totalContrib.toString()
                    )

                } else {
                    Log.d(Constants.TAG, "Failed")
                }
            }

            override fun onFailure(call: Call<OverallCountModel?>, t: Throwable) {}
        })
    }

    private fun initAudioContributions() {
        val email: String =
            sharedPrefsManager!!.getStringValue(SharedPrefsConstants.USER_EMAIL, "")!!
        val call: Call<OverallCountModel> =
            AdminServiceAPIClient.instance!!.myApi.userDashboardCount(email)
        call.enqueue(object : Callback<OverallCountModel?> {
            override fun onResponse(
                call: Call<OverallCountModel?>,
                response: Response<OverallCountModel?>
            ) {
                var resposeBody = response.body()
                Log.d(Constants.TAG, response.toString())
                if (response.isSuccessful) {
                    audiooverallValuesData[Constants.NOISE] =
                        response.body()?.totalAudioNoise!!
                    audiooverallValuesData[Constants.SPEAKER] =
                        response.body()?.totalAudioSpeaker!!
                    audiooverallValuesData[Constants.SPEECH] =
                        response.body()?.totalAudioSpeech!!
                    var totalAudio =
                        Integer.parseInt(resposeBody?.totalAudioSpeech) + Integer.parseInt(resposeBody?.totalAudioSpeaker) + Integer.parseInt(
                            resposeBody?.totalAudioNoise
                        )
                    binding.audioContributionView.totalResponse.text = getString(
                        R.string.total_audio,
                        totalAudio.toString()
                    )
                } else {
                    Log.d(Constants.TAG, "Failed")
                }
            }

            override fun onFailure(call: Call<OverallCountModel?>, t: Throwable) {}
        })
    }

    private fun initImageContributions() {
        val email: String =
            sharedPrefsManager!!.getStringValue(SharedPrefsConstants.USER_EMAIL, "abc@gmail.com")!!
        val call: Call<OverallCountModel> =
            AdminServiceAPIClient.instance!!.myApi.userDashboardCount(email)
        call.enqueue(object : Callback<OverallCountModel?> {
            override fun onResponse(
                call: Call<OverallCountModel?>,
                response: Response<OverallCountModel?>
            ) {
                Log.d(Constants.TAG, response.toString())
                if (response.isSuccessful) {
                    imageoverallValuesData[Constants.PEOPLE_CENTRIC] =
                        response.body()!!.totalHumanCentric!!
//                    imageoverallValuesData[Constants.TEXT_CENTRIC] =
//                        response.body()!!.totalTextCentric!!
                    imageoverallValuesData[Constants.NON_PEOPLE_CENTRIC] =
                        response.body()!!.totalNonHumanCentric!!
                    binding.imageContributionView.totalResponse.text = getString(
                        R.string.total_images,
                        response.body()!!.totalImages
                    )
                } else {
                    Log.d(Constants.TAG, "Failed")
                }
            }

            override fun onFailure(call: Call<OverallCountModel?>, t: Throwable) {}
        })
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            if (exit) {
                finish()
                moveTaskToBack(true)
            } else {
                Toast.makeText(
                    context, getString(R.string.press_back_to_exit),
                    Toast.LENGTH_SHORT
                ).show()
                exit = true
                Handler().postDelayed({ exit = false }, (3 * 1000).toLong())
            }
        }
    }

    private fun backButtonUserProfile() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(this@UserDashboard,AdminViewUsers::class.java))
            finish()
        }
    }


    private fun toolbarClick() {
        val feedback: ImageView = findViewById(R.id.toolbar_feedback)
        feedback.visibility = View.GONE
        feedback.visibility = View.GONE
        val logout: ImageView = findViewById(R.id.toolbar_logout)
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
        if(sharedPrefsManager?.getBoolValue(SharedPrefsConstants.IS_IT_USER_PROFILE,false) == true){
            startActivity(Intent(this@UserDashboard,AdminViewUsers::class.java))
            finish()
        }else{
            if (exit) {
                finish()
                moveTaskToBack(true)
            } else {
                Toast.makeText(
                    this, getString(R.string.press_back_to_exit),
                    Toast.LENGTH_SHORT
                ).show()
                exit = true
                Handler().postDelayed({ exit = false }, (3 * 1000).toLong())
            }
        }
    }
}