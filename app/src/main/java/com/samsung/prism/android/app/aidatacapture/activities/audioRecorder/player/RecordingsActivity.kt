package com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.player

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.UserMenuActivity
import com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.AudioRecorderActivity
import com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.item.Recording
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.utils.InjectorUtils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_recordings.*
import java.io.File

class RecordingsActivity : AppCompatActivity() {

    private lateinit var recorderDirectory: File
    private var viewModel: RecordingViewModel? = null
    private lateinit var groupAdapter: GroupAdapter<ViewHolder>
    private var data: ArrayList<String>? = ArrayList<String>()
    private var file : ArrayList<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recordings)
        groupAdapter = GroupAdapter<ViewHolder>()
        groupAdapter.notifyDataSetChanged()
        toolbarClick()
        backButton()
        initUI()
    }

    private fun getRecording(){
        val files: Array<out File>? = recorderDirectory.listFiles()
        for(i in files!!){
            println(i.name)
            file?.add(i.name)
        }
    }

    override fun onStart() {
        super.onStart()
        recordings_recyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }

    private fun initUI() {
        //Get the viewmodel factory
        recorderDirectory = File(Environment.getExternalStorageDirectory().absolutePath+ Constants.COLLECT_SOUND_DIRECTORY)
        file = ArrayList<String>()
        val factory = InjectorUtils.provideRecordingViewModelFactory()

        //Getting the viewmodel
        viewModel = ViewModelProviders.of(this, factory).get(RecordingViewModel::class.java)
        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        updateAdapter()
    }

    private fun updateAdapter() {
        getRecording()
        data = file
        println("Updating Adapter")
        groupAdapter.clear()

        if(data != null) {
            data!!.forEach {
                println("Data: $it")
                groupAdapter.add(Recording(it,this))
            }
        }
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
            startActivity(
                Intent(
                    activity@this,
                    AudioRecorderActivity::class.java
                )
            )
            finish()
        }
    }
}
