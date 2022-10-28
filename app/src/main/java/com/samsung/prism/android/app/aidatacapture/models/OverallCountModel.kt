package com.samsung.prism.android.app.aidatacapture.models

data class OverallCountModel(
    var totalUsers: String? = null,
    var totalImages: String? = null,
    var totalHumanCentric: String? = null,
    var totalNonHumanCentric: String? = null,
    var totalAudioNoise: String? = null,
    var totalAudioSpeech: String?=null,
    var totalAudioSpeaker: String? = null,
)