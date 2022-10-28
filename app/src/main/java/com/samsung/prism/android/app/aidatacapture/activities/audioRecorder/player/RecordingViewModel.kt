package com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.player

import androidx.lifecycle.ViewModel

class RecordingViewModel(val recordingRepository: RecordingRepository): ViewModel(){

    fun getRecordings() = recordingRepository.getRecordings()
}