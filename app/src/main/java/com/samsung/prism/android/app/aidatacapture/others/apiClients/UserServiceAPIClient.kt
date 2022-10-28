package com.samsung.prism.android.app.aidatacapture.others.apiClients

import com.google.gson.GsonBuilder
import com.samsung.prism.android.app.aidatacapture.Retrofit.Interfaces.UserService
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserServiceAPIClient private constructor() {
    private val retrofit: Retrofit
    val myApi: UserService
        get() = retrofit.create<UserService>(UserService::class.java)

    companion object {
        private var userServiceAPIClient: UserServiceAPIClient? = null

        @JvmStatic
        @get:Synchronized
        val instance: UserServiceAPIClient?
            get() {
                if (userServiceAPIClient == null) {
                    userServiceAPIClient = UserServiceAPIClient()
                }
                return userServiceAPIClient
            }
    }

    init {
        val gson = GsonBuilder().setLenient().create()
        retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
    }
}