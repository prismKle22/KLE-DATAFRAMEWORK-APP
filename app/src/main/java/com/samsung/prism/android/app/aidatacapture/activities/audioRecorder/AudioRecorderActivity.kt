package com.samsung.prism.android.app.aidatacapture.activities.audioRecorder

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gabriel.soundrecorder.util.RecorderState
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.item.Recording
import com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.recorder.RecorderViewModel
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.utils.InjectorUtils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_audio_recorder.*
import kotlinx.android.synthetic.main.activity_audio_recorder.recordings_recyclerview
import kotlinx.android.synthetic.main.activity_recordings.*
import java.io.File

class AudioRecorderActivity : AppCompatActivity() {

    private var viewModel: RecorderViewModel? = null
    private lateinit var recorderDirectory: File
    private lateinit var groupAdapter: GroupAdapter<ViewHolder>
    private var data: ArrayList<String>? = ArrayList<String>()
    private var file : ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_recorder)
        initUI()
        fab_start_recording.setOnClickListener {
            startRecording()
        }

        fab_stop_recording.setOnClickListener {
            stopRecording()
            finish();
            startActivity(getIntent());
        }

        fab_pause_recording.setOnClickListener {
            pauseRecording()
            //Ably the span count and the adapter to the recyclerview
        }

        fab_resume_recording.setOnClickListener {
            resumeRecording()
        }

        fab_recordings.setOnClickListener {
            finish();
            startActivity(getIntent());
        }

        if (viewModel?.recorderState == RecorderState.Stopped) {
            fab_stop_recording.isEnabled = false
        }
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
        val factory = InjectorUtils.provideRecorderViewModelFactory()
        viewModel = ViewModelProviders.of(this, factory).get(RecorderViewModel::class.java)
        groupAdapter = GroupAdapter<ViewHolder>()
        groupAdapter.notifyDataSetChanged()
        recorderDirectory = File(Environment.getExternalStorageDirectory().absolutePath+ Constants.COLLECT_SOUND_DIRECTORY)
        file = ArrayList<String>()
        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        addObserver()
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

    private fun addObserver() {
        viewModel?.getRecordingTime()?.observe(this, Observer {
            textview_recording_time.text = it
        })
    }


    @SuppressLint("RestrictedApi")
    private fun startRecording() {
        viewModel?.startRecording()

        fab_stop_recording.isEnabled = true
        fab_start_recording.visibility = View.INVISIBLE
        fab_pause_recording.visibility = View.VISIBLE
        fab_resume_recording.visibility = View.INVISIBLE
    }

    @SuppressLint("RestrictedApi")
    private fun stopRecording() {
        viewModel?.stopRecording()

        fab_stop_recording.isEnabled = false
        fab_start_recording.visibility = View.VISIBLE
        fab_pause_recording.visibility = View.INVISIBLE
        fab_resume_recording.visibility = View.INVISIBLE
    }

    @TargetApi(Build.VERSION_CODES.N)
    @SuppressLint("RestrictedApi")
    private fun pauseRecording() {
        viewModel?.pauseRecording()

        fab_stop_recording.isEnabled = true
        fab_start_recording.visibility = View.INVISIBLE
        fab_pause_recording.visibility = View.INVISIBLE
        fab_resume_recording.visibility = View.VISIBLE
    }

    @TargetApi(Build.VERSION_CODES.N)
    @SuppressLint("RestrictedApi")
    private fun resumeRecording() {
        viewModel?.resumeRecording()
        fab_stop_recording.isEnabled = true
        fab_start_recording.visibility = View.INVISIBLE
        fab_pause_recording.visibility = View.VISIBLE
        fab_resume_recording.visibility = View.INVISIBLE
    }

    override fun onBackPressed() {
        startActivity(Intent(this@AudioRecorderActivity, SubAudioMenuActivity::class.java))
        finish()
    }


}
