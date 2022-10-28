package com.samsung.prism.android.app.aidatacapture.activities.mySignConsent

import android.graphics.Bitmap

interface OnSignedCaptureListener {
    fun onSignatureCaptured(bitmap: Bitmap, fileUri: String)
}