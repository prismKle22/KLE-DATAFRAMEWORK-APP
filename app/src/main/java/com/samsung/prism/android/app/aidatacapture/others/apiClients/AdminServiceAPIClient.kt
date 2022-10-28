package com.samsung.prism.android.app.aidatacapture.others.apiClients

import com.google.gson.GsonBuilder
import com.samsung.prism.android.app.aidatacapture.Retrofit.Interfaces.AdminAPIServices
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AdminServiceAPIClient private constructor() {
    private val retrofit: Retrofit
    val myApi: AdminAPIServices
        get() = retrofit.create<AdminAPIServices>(AdminAPIServices::class.java)

    companion object {
        private var myClient: AdminServiceAPIClient? = null

        @JvmStatic
        @get:Synchronized
        val instance: AdminServiceAPIClient?
            get() {
                if (myClient == null) {
                    myClient = AdminServiceAPIClient()
                }
                return myClient
            }
    }

    init {
        val gson = GsonBuilder().setLenient().create()
        retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
    }
}