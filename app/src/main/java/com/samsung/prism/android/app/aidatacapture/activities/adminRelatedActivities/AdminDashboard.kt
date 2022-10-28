package  com.samsung.prism.android.app.aidatacapture.activities.adminRelatedActivities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.UserDashboard
import com.samsung.prism.android.app.aidatacapture.activities.UserMenuActivity
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.databinding.ActivityAdminDashboardBinding
import com.samsung.prism.android.app.aidatacapture.databinding.StatItemLayoutBinding
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.models.AdminResponseModel
import com.samsung.prism.android.app.aidatacapture.models.AudioCountModel
import com.samsung.prism.android.app.aidatacapture.models.OverallCountModel
import com.samsung.prism.android.app.aidatacapture.others.apiClients.AdminServiceAPIClient
import com.samsung.prism.android.app.aidatacapture.repos.DashboardStatsHelper
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class AdminDashboard : AppCompatActivity(), OnRefreshListener {
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var context: Context? = null
    var viewType: Enum<UserDashboard.ViewType> = UserDashboard.ViewType.CHART
    var viewTypeAudio : Enum<AdminDashboard.ViewTypeAudio> = AdminDashboard.ViewTypeAudio.CHART
    private lateinit var binding: ActivityAdminDashboardBinding
    private var exit = false
    private lateinit var dashboardStats: DashboardStatsHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        context = this
        dashboardStats = DashboardStatsHelper()
        setContentView(binding.root)
        SharedPrefsManager(this).saveStringValue(SharedPrefsConstants.USER_EMAIL,"admin@klesamsung")
        initAudioDashboard()
        initDashboard()
        toolbarClick()
        onClickListeners()
        setWelcomeMessage()
        backButton()
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

    private fun onClickListeners() {
        binding.approveUsers.setOnClickListener {
            startActivity(Intent(Activity@ this, AdminApproveUsers::class.java))
            finish()
        }
        binding.viewUsers.setOnClickListener {
            startActivity(Intent(Activity@ this, AdminViewUsers::class.java))
            finish()
        }
        binding.launchApp.setOnClickListener {
            SharedPrefsManager(this).saveStringValue(SharedPrefsConstants.USER_EMAIL,"admin@klesamsung")
            startActivity(Intent(Activity@ this, UserDashboard::class.java))
            finish()
        }
        binding.logout.setOnClickListener {
            Dialogues().logoutDialogue(this)
        }
        mSwipeRefreshLayout = findViewById<View>(R.id.swipeRefreshLayout) as SwipeRefreshLayout
        mSwipeRefreshLayout!!.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ mSwipeRefreshLayout!!.isRefreshing = false }, 2000)
    }

    enum class ViewType {
        CHART,
        VALUES
    }

    enum class ViewTypeAudio {
        CHART,
        VALUES
    }

    private fun setValuesAudio(valuesData: MutableMap<String, String>?) {
        viewTypeAudio = AdminDashboard.ViewTypeAudio.VALUES
        binding.audioToogleAllStatus.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_baseline_pie_chart_24
            )
        )
        binding.audioStatusPieChart.visibility = View.GONE
        binding.audioStatusValuesContainer.visibility = View.VISIBLE
        valuesData!!.forEach {
            val valueCardBinding =
                StatItemLayoutBinding.inflate(layoutInflater, binding.audioStatusValuesContainer, false)
            valueCardBinding.label.text = it.key
            valueCardBinding.value.text = it.value
            valueCardBinding.valueInPercentage.visibility = View.GONE
            binding.audioStatusValuesContainer.addView(valueCardBinding.root)
        }
        valuesData.clear()
    }

    private fun setValues(valuesData: MutableMap<String, String>?) {
        viewType = UserDashboard.ViewType.VALUES
        binding.toogleAllStatus.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_baseline_pie_chart_24
            )
        )
        binding.statusPieChart.visibility = View.GONE
        binding.statusValuesContainer.visibility = View.VISIBLE
        valuesData!!.forEach {
            val valueCardBinding =
                StatItemLayoutBinding.inflate(layoutInflater, binding.statusValuesContainer, false)
            valueCardBinding.label.text = it.key
            valueCardBinding.value.text = it.value
            valueCardBinding.valueInPercentage.visibility = View.GONE
            binding.statusValuesContainer.addView(valueCardBinding.root)
        }
        valuesData.clear()
    }

    private fun setChart(data: MutableList<DataEntry>) {
        binding.toogleAllStatus.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_baseline_view_list_24
            )
        )
        binding.statusValuesContainer.visibility = View.GONE
        binding.statusPieChart.visibility = View.VISIBLE
        viewType = UserDashboard.ViewType.CHART
        binding.statusPieChart.visibility = View.VISIBLE
        binding.loadingAllStats.visibility = View.GONE
        APIlib.getInstance().setActiveAnyChartView(binding.statusPieChart);
        val pie = AnyChart.pie()
        pie.data(data)
        binding.statusPieChart.setChart(pie)
    }

    private fun setChartAudio(data: MutableList<DataEntry>) {
        binding.audioToogleAllStatus.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_baseline_view_list_24
            )
        )
        binding.audioStatusValuesContainer.visibility = View.GONE
        binding.audioStatusPieChart.visibility = View.VISIBLE
        viewTypeAudio = ViewTypeAudio.CHART
        binding.audioStatusPieChart.visibility = View.VISIBLE
        binding.audioLoadingAllStats.visibility = View.GONE
        APIlib.getInstance().setActiveAnyChartView(binding.audioStatusPieChart);
        val pie = AnyChart.pie()
        pie.data(data)
        binding.audioStatusPieChart.setChart(pie)
    }

    private fun toggleChartAndValues(
        userData: MutableList<DataEntry>,
        valuesData: MutableMap<String, String>?
    ) {
        setChart(userData)
        binding.toogleAllStatus.setOnClickListener {
            if (viewType == UserDashboard.ViewType.CHART) {
                setValues(valuesData)
            } else {
                setChart(userData)
            }
        }

    }

    private fun toggleChartAndValuesAudio(
        userData: MutableList<DataEntry>,
        valuesData: MutableMap<String, String>?
    ) {
        setChartAudio(userData)
        binding.audioToogleAllStatus.setOnClickListener {
            if (viewTypeAudio == ViewTypeAudio.CHART) {
                setValuesAudio(valuesData)
            } else {
                setChartAudio(userData)
            }
        }

    }


    private fun initDashboard() {
        var valuesData: MutableMap<String, String> = mutableMapOf()
        val call: Call<AdminResponseModel> =
            AdminServiceAPIClient.instance!!.myApi.dashboardCount("text")
        call.enqueue(object : Callback<AdminResponseModel?> {
            override fun onResponse(
                call: Call<AdminResponseModel?>,
                response: Response<AdminResponseModel?>
            ) {
                Log.d(Constants.TAG, response.toString())
                if (response.isSuccessful) {
                    Log.d(Constants.TAG, "Response is Successful")
                    try {
                        dashboardStats.setStatsForAdminChartView(
                            Constants.TEXT_CENTRIC,
                            Integer.parseInt(response.body()!!.totalTextCentric)
                        )
                        dashboardStats.setDataForAdminValues(
                            Constants.TEXT_CENTRIC,
                            response.body()!!.totalHumanCentric!!
                        )
                    } catch (e: Exception) {
                        dashboardStats.setStatsForAdminChartView(Constants.TEXT_CENTRIC, 0)
                    }
                    try {
                        dashboardStats.setStatsForAdminChartView(
                            Constants.PEOPLE_CENTRIC,
                            Integer.parseInt(response.body()!!.totalHumanCentric)
                        )
                        dashboardStats.setDataForAdminValues(
                            Constants.PEOPLE_CENTRIC,
                            response.body()!!.totalTextCentric!!
                        )
                    } catch (e: Exception) {
                        dashboardStats.setStatsForAdminChartView(Constants.PEOPLE_CENTRIC, 0)
                    }
                    try {
                        dashboardStats.setStatsForAdminChartView(
                            Constants.NON_PEOPLE_CENTRIC,
                            Integer.parseInt(response.body()!!.totalNonHumanCentric)
                        )
                        dashboardStats.setDataForAdminValues(
                            Constants.NON_PEOPLE_CENTRIC,
                            response.body()!!.totalNonHumanCentric!!
                        )
                    } catch (e: Exception) {
                        dashboardStats.setStatsForAdminChartView(Constants.NON_PEOPLE_CENTRIC, 0)
                    }
                    binding.totalResponse.text =
                        getString(R.string.total_images, response.body()!!.totalImages.toString())
                    toggleChartAndValues(
                        dashboardStats.getStatsForAdminChartView(),
                        dashboardStats.getDataForAdminValues()
                    )
                } else {
                    Log.d(Constants.TAG, "Failed")
                }
            }

            override fun onFailure(call: Call<AdminResponseModel?>, t: Throwable) {}
        })

    }

    fun initAudioDashboard(){
        var valuesData: MutableMap<String, String> = mutableMapOf()
        val call: Call<OverallCountModel>? =
            AdminServiceAPIClient.instance!!.myApi.overAllCount("text")
        call?.enqueue(object : Callback<OverallCountModel?> {
            override fun onResponse(
                call: Call<OverallCountModel?>,
                response: Response<OverallCountModel?>
            ) {
                Log.d(Constants.TAG, response.toString())
                if (response.isSuccessful) {
                    Log.d(Constants.TAG, "Response is Successful")
                    try {
                        dashboardStats.setStatsForAdminAudioChartView(
                            Constants.NOISE,
                            Integer.parseInt(response.body()!!.totalAudioNoise)
                        )
                        dashboardStats.setDataForAdminAudioValues(
                            Constants.NOISE,
                            response.body()!!.totalAudioNoise!!
                        )
                    } catch (e: Exception) {
                        dashboardStats.setStatsForAdminAudioChartView(Constants.NOISE, 0)
                    }
                    try {
                        dashboardStats.setStatsForAdminAudioChartView(
                            Constants.SPEAKER,
                            Integer.parseInt(response.body()!!.totalAudioSpeaker)
                        )
                        dashboardStats.setDataForAdminAudioValues(
                            Constants.SPEAKER,
                            response.body()!!.totalAudioSpeaker!!
                        )
                    } catch (e: Exception) {
                        dashboardStats.setStatsForAdminAudioChartView(Constants.SPEAKER, 0)
                    }
                    try {
                        dashboardStats.setStatsForAdminAudioChartView(
                            Constants.SPEECH,
                            Integer.parseInt(response.body()!!.totalAudioSpeech)
                        )
                        dashboardStats.setDataForAdminAudioValues(
                            Constants.SPEECH,
                            response.body()!!.totalAudioSpeech!!
                        )
                    } catch (e: Exception) {
                        dashboardStats.setStatsForAdminAudioChartView(Constants.SPEECH, 0)
                    }
                    var totalAudio : Int = Integer.parseInt(response.body()!!.totalAudioNoise) + Integer.parseInt(response.body()!!.totalAudioSpeaker) + Integer.parseInt(response.body()!!.totalAudioSpeech);
                    Log.d("Msg", "onResponse: "+totalAudio)
                    binding.audioTotalResponse.text =
                        getString(R.string.total_audio, totalAudio.toString())
                    toggleChartAndValuesAudio(
                        dashboardStats.getStatsForAdminAudioChartView(),
                        dashboardStats.getDataForAdminAudioValues()
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

    private fun toolbarClick() {
        val feedback: ImageView
        val logout: ImageView
        feedback = findViewById(R.id.toolbar_feedback)
        feedback.visibility = View.GONE
        feedback.visibility = View.GONE
        logout = findViewById(R.id.toolbar_logout)
        logout.setOnClickListener {
            Dialogues().logoutDialogue(this)
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