package com.samsung.prism.android.app.aidatacapture.others.apiClients

import com.samsung.prism.android.app.aidatacapture.Retrofit.Interfaces.ApproveUserAPI
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApproveUsersAPIClient {
    private var retrofit: Retrofit? = null
    @JvmStatic
    val client: ApproveUserAPI?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit?.create<ApproveUserAPI>(
                ApproveUserAPI::class.java
            ) // return the APIInterface object
        }
}