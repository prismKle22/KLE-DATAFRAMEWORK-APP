package com.samsung.prism.android.app.aidatacapture.utils

import android.content.Context
import android.net.ConnectivityManager
import com.samsung.prism.android.app.aidatacapture.R
import java.net.InetAddress
import java.net.UnknownHostException

class CheckInternet(var context: Context) {
    val isNetworkAvailable: Boolean
        get() {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
                .isConnected
        }

    // Log error
    val isInternetAvailable: Boolean
        get() {
            try {
                val address = InetAddress.getByName(context.getString(R.string.google_url))
                return !address.equals("")
            } catch (e: UnknownHostException) {
                // Log error
            }
            return false
        }
}