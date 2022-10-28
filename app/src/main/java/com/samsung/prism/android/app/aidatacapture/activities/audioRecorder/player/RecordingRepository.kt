package com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.player

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.repos.RetrofitHelper
import java.io.File

class RecordingRepository{
    companion object {
        @Volatile
        private var instance: RecordingRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: RecordingRepository().also { instance = it }
            }


        fun playRecording(context: Context, title: String){
            val filePath = Environment.getExternalStorageDirectory().absolutePath+Constants.COLLECT_SOUND_DIRECTORY+title
            val path = Uri.parse(filePath)
            val manager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if(manager.isMusicActive) {
                Toast.makeText(context, context.getString(R.string.another_audio_playing_toast_msg), Toast.LENGTH_SHORT).show()
            }else{
                val mediaPlayer: MediaPlayer? = MediaPlayer().apply {
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                    setDataSource(context, path)
                    prepare()
                    start()
                }
            }

        }
    }

    private val recorderDirectory = File(Environment.getExternalStorageDirectory().absolutePath+ Constants.COLLECT_SOUND_DIRECTORY)
    private var file : ArrayList<String>? = null

    init {
        file = ArrayList<String>()
        getRecording()
    }

    private fun getRecording(){
        val files: Array<out File>? = recorderDirectory.listFiles()
        for(i in files!!){
            println(i.name)
            file?.add(i.name)
        }
    }

    fun getRecordings() = file

}