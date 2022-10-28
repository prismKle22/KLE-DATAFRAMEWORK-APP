package com.samsung.prism.android.app.aidatacapture.activities.callbacks

import android.content.Context

interface FileDeleteCallback {
    fun onDeleteSuccess(msg: String)
    fun onDeleteFail(msg: String)
}