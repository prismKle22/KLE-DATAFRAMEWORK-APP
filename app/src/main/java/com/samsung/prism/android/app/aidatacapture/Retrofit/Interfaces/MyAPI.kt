package com.samsung.prism.android.app.aidatacapture.Retrofit.Interfaces

import com.samsung.prism.android.app.aidatacapture.activities.mySignConsent.UploadResponse
import com.samsung.prism.android.app.aidatacapture.constants.Constants.BASE_URL
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MyAPI {



    //used to upload consent( i.e., only the signature in image/* form)
    //check MySignConsentActivity for more info
    @Multipart
    @POST("/data-framework/ConsentUploadAPI.php?apicall=upload")
    fun uploadConsent(
        @Part image: MultipartBody.Part,
        @Part("usrname") usrname: RequestBody,
        @Part("name") name: RequestBody,
        @Part("place") place: RequestBody,
        @Part("filename") filename: RequestBody
    ): Call<UploadResponse>

    //this is used to upload single image to the server
    //Used in UploadMultipleImage
    @Multipart
    @POST("/data-framework/uploadImage.php?apicall=uploadimage")
    fun uploadImage(
            @Part image: MultipartBody.Part,
            @Part("usrname") usrname: RequestBody,
            @Part("category") category:RequestBody,
            @Part("subCategory") subCategory:RequestBody,
            @Part("locationString") locationString: RequestBody,
            @Part("subLocationString") subLocationString:RequestBody,
            @Part("timingString") timingString:RequestBody,
            @Part("lightingString") lightingString: RequestBody,
            @Part("myDeviceModel") myDeviceModel:RequestBody,
            @Part("screenSize") screenSize:RequestBody,
            @Part("categoryString") categoryString: RequestBody,
            @Part("humanPresent") humanPresent:RequestBody,
            @Part("selfieString") selfieString:RequestBody,
            @Part("typeString") typeString: RequestBody,
            @Part("aboveEighteenString") aboveEighteenString:RequestBody,
            @Part("propsString") propsString:RequestBody,
            @Part("consentString") consentString:RequestBody,



    ): Call<UploadResponse>

    //this is used to upload single video to the server
    //Used in UploadMultipleVideo
    @Multipart
    @POST("/data-framework/uploadImage.php?apicall=uploadvideo")
    fun uploadVideo(
            @Part video: MultipartBody.Part,
            @Part("usrname") usrname: RequestBody,
            @Part("category") category:RequestBody,
            @Part("subCategory") subCategory:RequestBody,
            @Part("locationString") locationString: RequestBody,
            @Part("subLocationString") subLocationString:RequestBody,
            @Part("timingString") timingString:RequestBody,
            @Part("lightingString") lightingString: RequestBody,
            @Part("myDeviceModel") myDeviceModel:RequestBody,
            @Part("screenSize") screenSize:RequestBody,
            @Part("categoryString") categoryString: RequestBody,
            @Part("humanPresent") humanPresent:RequestBody,
            @Part("selfieString") selfieString:RequestBody,
            @Part("typeString") typeString: RequestBody,
            @Part("aboveEighteenString") aboveEighteenString:RequestBody,
            @Part("propsString") propsString:RequestBody,
            @Part("consentString") consentString:RequestBody,
    ): Call<UploadResponse>
//
//    //function used to call getUserDetails.php with request body consisting of email and unhashed password
//    //Check UserLoginActivity.kt for usage
//    @Multipart
//    @POST("phps/api/Users/getUserDetails.php")
//    fun login(
//            @Part("email") email: RequestBody
//            @Part("password") password: RequestBody
//    ): Call<UploadResponse>

    @Multipart
    @POST("/data-framework/uploadAudio.php?apicall=uploadSpeech")
    fun uploadSpeech(
            @Part audio: MultipartBody.Part,
            @Part("usrname") usrname: RequestBody,
            @Part("gender") gender:RequestBody,
            @Part("ageGroup") ageGroup:RequestBody,
            @Part("noise") noise: RequestBody,
            @Part("sourceDistance") sourceDistance:RequestBody,
            @Part("languages") languages:RequestBody,
            @Part("location") location: RequestBody,
            @Part("multipleSpeaker") multipleSpeaker:RequestBody,
            @Part("myDeviceModel") myDeviceModel:RequestBody,
            
            ): Call<UploadResponse>

    @Multipart
    @POST("/data-framework/uploadAudio.php?apicall=uploadNoise")
    fun uploadNoise(
            @Part audio: MultipartBody.Part,
            @Part("usrname") usrname: RequestBody,
            @Part("sourceDistance") sourceDistance: RequestBody,
            @Part("noiseObject") noiseObject:RequestBody,
            @Part("source") source:RequestBody,
            @Part("location") location: RequestBody,
            @Part("myDeviceModel") myDeviceModel:RequestBody,

            ): Call<UploadResponse>

    @Multipart
    @POST("/data-framework/uploadAudio.php?apicall=uploadSpeaker")
    fun uploadSpeaker(
            @Part audio: MultipartBody.Part,
            @Part("usrname") usrname: RequestBody,
            @Part("sourceDistance") sourceDistance: RequestBody,
            @Part("noise") noise:RequestBody,
            @Part("source") source:RequestBody,
            @Part("location") location: RequestBody,
            @Part("myDeviceModel") myDeviceModel:RequestBody,

            ): Call<UploadResponse>

    @Multipart
    @POST("/data-framework/uploadTextFiles.php?apicall=uploadCorpusSms")
    fun uploadCorpusSMS(
            @Part textfile: MultipartBody.Part,
            @Part("usrname") usrname: RequestBody,
            @Part("gender") gender: RequestBody,
            @Part("age") age:RequestBody,
            @Part("mood") mood:RequestBody,
            @Part("personality") personality: RequestBody,
            @Part("corpus_class") corpus_class:RequestBody,
            @Part("type") type:RequestBody,

            ): Call<UploadResponse>


    @Multipart
    @POST("/data-framework/uploadTextFiles.php?apicall=uploadText")
    fun uploadText(
            @Part textfile: MultipartBody.Part,
            @Part("usrname") usrname: RequestBody,
            @Part("text_class") text_class: RequestBody,
            @Part("type") type:RequestBody,
            @Part("tag") tag:RequestBody

            ): Call<UploadResponse>


    @Multipart
    @POST("/data-framework/uploadTextFiles.php?apicall=uploadTranslation")
    fun uploadTranslation(
            @Part textfile: MultipartBody.Part,
            @Part("usrname") usrname: RequestBody,
            @Part("numbersPresent") numbersPresent: RequestBody,
            @Part("category") category: RequestBody,
            @Part("mixedSource") mixedSource:RequestBody,
            @Part("standard") standard:RequestBody,
            @Part("language") language: RequestBody

            ): Call<UploadResponse>


    companion object {
        operator fun invoke(): MyAPI {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyAPI::class.java)
        }
    }
}