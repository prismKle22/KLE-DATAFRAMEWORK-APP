package com.samsung.prism.android.app.aidatacapture.others.apiClients

import com.samsung.prism.android.app.aidatacapture.Retrofit.Interfaces.UserDetailsAPI
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserDetailsApiClient {
    private var retrofit: Retrofit? = null
    @JvmStatic
    val client: UserDetailsAPI?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit?.create<UserDetailsAPI>(
                UserDetailsAPI::class.java
            )
        }
}