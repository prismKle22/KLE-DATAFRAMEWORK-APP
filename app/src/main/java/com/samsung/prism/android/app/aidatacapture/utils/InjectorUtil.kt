package com.samsung.prism.android.app.aidatacapture.utils

import com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.player.RecordingRepository
import com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.player.RecordingViewModelProvider
import com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.recorder.RecorderRepository
import com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.recorder.RecorderViewModelProvider

object InjectorUtils {
    fun provideRecorderViewModelFactory(): RecorderViewModelProvider {
        val recorderRepository = RecorderRepository.getInstance()
        return RecorderViewModelProvider(recorderRepository)
    }

    fun provideRecordingViewModelFactory(): RecordingViewModelProvider {
        val recordingRepository = RecordingRepository.getInstance()
        return RecordingViewModelProvider(recordingRepository)
    }
}