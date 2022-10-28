package com.samsung.prism.android.app.aidatacapture.others.apiClients

import com.samsung.prism.android.app.aidatacapture.Retrofit.Interfaces.APIService
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SubCategoryAPIClient {
    private var retrofit: Retrofit? = null
    @JvmStatic
    val client: APIService?
        get() {

            // change your base URL
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            //Creating object for our interface
            return retrofit?.create<APIService>(APIService::class.java)
        }
}