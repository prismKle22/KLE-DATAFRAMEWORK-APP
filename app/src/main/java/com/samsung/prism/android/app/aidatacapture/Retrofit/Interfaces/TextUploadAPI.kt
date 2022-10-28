package com.samsung.prism.android.app.aidatacapture.activities.textUploader.uploader

import com.samsung.prism.android.app.aidatacapture.constants.Constants.BASE_URL
import com.samsung.prism.android.app.aidatacapture.models.TextFileResponseModel
import com.samsung.prism.android.app.aidatacapture.models.TypedTextResponseModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface TextUploadAPI {

    @Multipart
    @POST("/data-framework/uploadImage.php?apicall=uploadfile")
    fun uploadImage(
            @Part file: MultipartBody.Part,
            @Part("usrname") usrname: RequestBody,
            create: RequestBody,
            create1: RequestBody,
            create2: RequestBody,
            create3: RequestBody,
            create4: RequestBody,
            create5: RequestBody
    ): Call<TextFileResponseModel>


    @Multipart
    @POST("TypeText.php?apicall=uploadTypedText")
    fun addData(
            @Part("desc") desc: RequestBody,
            @Part("usrname") usrname: RequestBody
    ): Call<TypedTextResponseModel>

    companion object {
        operator fun invoke(): TextUploadAPI {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TextUploadAPI::class.java)
        }
    }
}
