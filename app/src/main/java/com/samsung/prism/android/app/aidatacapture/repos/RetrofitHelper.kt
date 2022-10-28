package com.samsung.prism.android.app.aidatacapture.repos

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.callbacks.FileDeleteCallback
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.Constants.TAG
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.dialogues.ProgressDialog
import com.samsung.prism.android.app.aidatacapture.models.CategoryModel
import com.samsung.prism.android.app.aidatacapture.others.AndroidMultiPartEntity
import com.samsung.prism.android.app.aidatacapture.others.apiClients.CategoryAPIClient
import com.samsung.prism.android.app.aidatacapture.others.apiClients.SubCategoryAPIClient
import org.apache.http.HttpEntity
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.ssl.SSLSocketFactory
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.entity.mime.content.StringBody
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*


@Suppress("DEPRECATION")
class RetrofitHelper {
    var dialogues:Dialogues = Dialogues()
    var categoryListData: MutableList<String> = ArrayList()
    var subCategoryListData: MutableList<String> = ArrayList()
    private var totalSize: Long = 0
    fun getCategoryList(context: Context?): List<String> {
//        val progressDialog = ProgressDialog(
//            context!!, context.getString(R.string.getting_categories_msg)
//        )
//        progressDialog.setCancelable(false)
//        progressDialog.show()
        CategoryAPIClient.client!!.categoryList.enqueue(object :
            Callback<List<CategoryModel>> {
            override fun onResponse(
                call: Call<List<CategoryModel>>,
                response: Response<List<CategoryModel>>
            ) {
                val values = listOf("Office", "Malls", "Roads", "Scenaries", "Flora", "Other water bodies","Shops","AdvertisementBus Board","Bus Board","Traffic Signs","Name Boards","Sign Boards","From Books", "Animals","Vegetables and Fruits","School","Vehicles","Furnitures","Others")
                for (i in values) {
                    categoryListData.add(i)
//                    Log.d(TAG, "onResponse: $i")
//                    Log.d(TAG, "onResponse: " + response.body()!![i].categoryName)
//                    progressDialog.dismiss()
                }
//                categoryListData.add("bye")
            }

            override fun onFailure(call: Call<List<CategoryModel>>, t: Throwable) {
                // if error occurs in network transaction then we can get the error in this method.
                Log.d(TAG, "onFailure: $t")
                Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show()
//                progressDialog.dismiss()
            }
        })
        return categoryListData
    }

    fun getSubCategoryList(context: Context?): List<String> {
        SubCategoryAPIClient.client!!.subCategoryList
            .enqueue(object : Callback<List<CategoryModel>> {
                override fun onResponse(
                    call: Call<List<CategoryModel>>,
                    response: Response<List<CategoryModel>>
                ) {
                    val values = listOf("People Centric","Non People Centric")
                    for (i in values) {
                        subCategoryListData.add(i)
//                        Log.d(TAG, "onResponse: $i")
//                        Log.d(TAG, "onResponse: " + response.body()!![i].subCategory)
                    }
                }

                override fun onFailure(call: Call<List<CategoryModel>>, t: Throwable) {
                    // if error occurs in network transaction then we can get the error in this method.
                    Log.d(TAG, "onFailure: $t")
                    Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT)
                        .show()
                }
            })
        return subCategoryListData
    }

    fun uploadAudioFileToServer(context: Context, filePath: String) {
        val sharedPrefsManager = SharedPrefsManager(context)
        val viewType = sharedPrefsManager.getIntValue(
            SharedPrefsConstants.VIEW_TYPE,
            Constants.ViewTypeForAudio.NOISE.ordinal
        )
        Toast.makeText(context, "Uploading...", Toast.LENGTH_SHORT).show()
        val file = File(filePath)
        val httpclient: HttpClient = DefaultHttpClient()
        val httppost = HttpPost(Constants.AUDIO_UPLOAD_URL)
        when (viewType) {
            0 -> {
                pushNoiseSharedPrefsToServer(
                    httpclient,
                    file,
                    httppost,
                    context,
                    sharedPrefsManager
                )
            }
            1 -> {
                pushSpeechSharedPrefsToServer(
                    httpclient,
                    file,
                    httppost,
                    context,
                    sharedPrefsManager
                )
            }
            else -> {
                pushSpeakerSharedPrefsToServer(
                    httpclient,
                    file,
                    httppost,
                    context,
                    sharedPrefsManager
                )
            }
        }
    }

    private fun pushNoiseSharedPrefsToServer(
        httpclient: HttpClient,
        file: File,
        httppost: HttpPost,
        context: Context,
        sharedPrefsManager: SharedPrefsManager
    ): String {
        var responseString: String?
        try {
            val entity = sendDataNoise(sharedPrefsManager, file)
            httpclient.connectionManager.schemeRegistry.register(
                Scheme("https", SSLSocketFactory.getSocketFactory(), 443)
            )
            totalSize = entity.contentLength
            httppost.entity = entity
            val response = httpclient.execute(httppost)
            val r_entity = response.entity
            val statusCode = response.statusLine.statusCode
            responseString = if (statusCode == 200) {
                delteFile(file, object : FileDeleteCallback {
                    override fun onDeleteSuccess(msg: String) {
                        dialogues.showSuccessDialog(context)
                        //Toast.makeText(context, "Successfully uploaded!", Toast.LENGTH_SHORT).show()
                    }

                    override fun onDeleteFail(msg: String) {
                        Toast.makeText(context, "Delete Failed!", Toast.LENGTH_SHORT).show()
                    }
                })
                EntityUtils.toString(r_entity)
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.uploading_failed),
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(TAG, "uploadAudioFileToServer: " + EntityUtils.toString(r_entity))
                ("Error occurred! Http Status Code: "
                        + statusCode)
            }
        } catch (e: ClientProtocolException) {
            responseString = e.toString()
        } catch (e: IOException) {
            responseString = e.toString()
        }
        return responseString!!.trim { it <= ' ' }
    }

    private fun sendDataNoise(sharedPrefsManager: SharedPrefsManager, file: File): HttpEntity {
        val entity = AndroidMultiPartEntity { }
        val sourceDistance =
            sharedPrefsManager.getStringValue(SharedPrefsConstants.SOURCE_DISTANCE, "Null")
        val multipleNoise =
            sharedPrefsManager.getStringValue(SharedPrefsConstants.MULTIPLE_NOISE, "Null")
        val noiseObject =
            sharedPrefsManager.getStringValue(SharedPrefsConstants.NOISE_OBJECT, "Null")
        val location = sharedPrefsManager.getStringValue(SharedPrefsConstants.LOCATION, "Null")
        val device = sharedPrefsManager.getStringValue(SharedPrefsConstants.DEVICE, "Null")
        val userEmail =
            sharedPrefsManager.getStringValue(SharedPrefsConstants.USER_EMAIL, "Null")
        entity.addPart(SharedPrefsConstants.USER_EMAIL, StringBody(userEmail))
        entity.addPart("image", FileBody(file))
        entity.addPart("AudioType", StringBody("Noise"))
        entity.addPart(SharedPrefsConstants.SOURCE_DISTANCE, StringBody(sourceDistance))
        entity.addPart(SharedPrefsConstants.MULTIPLE_NOISE, StringBody(multipleNoise))
        entity.addPart(SharedPrefsConstants.NOISE_OBJECT, StringBody(noiseObject))
        entity.addPart(SharedPrefsConstants.LOCATION, StringBody(location))
        entity.addPart(SharedPrefsConstants.DEVICE, StringBody(device))
        return entity
    }

    private fun pushSpeechSharedPrefsToServer(
        httpclient: HttpClient,
        file: File,
        httppost: HttpPost,
        context: Context,
        sharedPrefsManager: SharedPrefsManager
    ): String {
        var responseString: String
        try {
            val entity = sendDataSpeech(sharedPrefsManager, file)
            httpclient.connectionManager.schemeRegistry.register(
                Scheme("https", SSLSocketFactory.getSocketFactory(), 443)
            )
            totalSize = entity.contentLength
            httppost.entity = entity
            val response = httpclient.execute(httppost)
            val r_entity = response.entity
            val statusCode = response.statusLine.statusCode
            responseString = if (statusCode == 200) {
                delteFile(file, object : FileDeleteCallback {
                    override fun onDeleteSuccess(msg: String) {
                        dialogues.showSuccessDialog(context)
                        //Toast.makeText(context, "Successfully uploaded!", Toast.LENGTH_SHORT).show()
                    }

                    override fun onDeleteFail(msg: String) {
                        Toast.makeText(context, "Delete Failed!", Toast.LENGTH_SHORT).show()
                    }
                })
                Log.d(TAG, "uploadAudioFileToServer: " + EntityUtils.toString(r_entity))
                EntityUtils.toString(r_entity)
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.uploading_failed),
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(TAG, "@drawable/ic_pause: " + EntityUtils.toString(r_entity))
                ("Error occurred! Http Status Code: "
                        + statusCode)
            }
        } catch (e: ClientProtocolException) {
            responseString = e.toString()
        } catch (e: IOException) {
            responseString = e.toString()
        }
        return responseString.trim { it <= ' ' }
    }

    private fun sendDataSpeech(sharedPrefsManager: SharedPrefsManager, file: File): HttpEntity {
        val entity = AndroidMultiPartEntity { }
        val gender = sharedPrefsManager.getStringValue(SharedPrefsConstants.GENDER, "Null")
        val ageGroup = sharedPrefsManager.getStringValue(SharedPrefsConstants.AGE_GROUP, "Null")
        val languages = sharedPrefsManager.getStringValue(SharedPrefsConstants.LANGUAGE, "Null")
        val multipleSpeaker =
            sharedPrefsManager.getStringValue(SharedPrefsConstants.MULTIPLE_SPEAKERS, "Null")
        val sourceDistance =
            sharedPrefsManager.getStringValue(SharedPrefsConstants.SOURCE_DISTANCE, "Null")
        val device = sharedPrefsManager.getStringValue(SharedPrefsConstants.DEVICE, "Null")
        val noise = sharedPrefsManager.getStringValue(SharedPrefsConstants.NOISE, "Null")
        val source = sharedPrefsManager.getStringValue(SharedPrefsConstants.SOURCE, "Null")
        val sourceModel =
            sharedPrefsManager.getStringValue(SharedPrefsConstants.SOURCE_MODEL, "Null")
        val location = sharedPrefsManager.getStringValue(SharedPrefsConstants.LOCATION, "Null")
        val duration = sharedPrefsManager.getStringValue(SharedPrefsConstants.DURATION, "Null")
        val userEmail =
            sharedPrefsManager.getStringValue(SharedPrefsConstants.USER_EMAIL, "Null")
        entity.addPart(SharedPrefsConstants.LANGUAGE, StringBody(languages))
        entity.addPart(SharedPrefsConstants.MULTIPLE_SPEAKERS, StringBody(multipleSpeaker))
        entity.addPart(SharedPrefsConstants.GENDER, StringBody(gender))
        entity.addPart(SharedPrefsConstants.AGE_GROUP, StringBody(ageGroup))
        entity.addPart(SharedPrefsConstants.USER_EMAIL, StringBody(userEmail))
        entity.addPart("AudioType", StringBody("Speech"))
        entity.addPart(SharedPrefsConstants.SOURCE_DISTANCE, StringBody(sourceDistance))
        entity.addPart(SharedPrefsConstants.DEVICE, StringBody(device))
        entity.addPart(SharedPrefsConstants.NOISE, StringBody(noise))
        entity.addPart(SharedPrefsConstants.SOURCE, StringBody(source))
        entity.addPart(SharedPrefsConstants.SOURCE_MODEL, StringBody(sourceModel))
        entity.addPart(SharedPrefsConstants.LOCATION, StringBody(location))
        entity.addPart(SharedPrefsConstants.DURATION, StringBody(duration))
        entity.addPart("image", FileBody(file))
        return entity

    }

    private fun pushSpeakerSharedPrefsToServer(
        httpclient: HttpClient,
        file: File,
        httppost: HttpPost,
        context: Context,
        sharedPrefsManager: SharedPrefsManager
    ): String {
        var responseString: String?
        try {
            val entity = sendDataSpeaker(sharedPrefsManager, file)
            httpclient.connectionManager.schemeRegistry.register(
                Scheme("https", SSLSocketFactory.getSocketFactory(), 443)
            )
            totalSize = entity.contentLength
            httppost.entity = entity
            val response = httpclient.execute(httppost)
            val r_entity = response.entity
            val statusCode = response.statusLine.statusCode
            responseString = if (statusCode == 200) {
                delteFile(file, object : FileDeleteCallback {
                    override fun onDeleteSuccess(msg: String) {
                        dialogues.showSuccessDialog(context)
                        //Toast.makeText(context, "Successfully uploaded!", Toast.LENGTH_SHORT).show()
                    }

                    override fun onDeleteFail(msg: String) {
                        Toast.makeText(context, "Delete Failed!", Toast.LENGTH_SHORT).show()
                    }
                })
                Log.d(TAG, "@drawable/ic_pause: " + EntityUtils.toString(r_entity))
                EntityUtils.toString(r_entity)
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.uploading_failed),
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(TAG, "@drawable/ic_pause: " + EntityUtils.toString(r_entity))
                ("Error occurred! Http Status Code: "
                        + statusCode)
            }
        } catch (e: ClientProtocolException) {
            responseString = e.toString()
        } catch (e: IOException) {
            responseString = e.toString()
        }
        return responseString!!.trim { it <= ' ' }
    }


    private fun sendDataSpeaker(sharedPrefsManager: SharedPrefsManager, file: File): HttpEntity {
        val entity = AndroidMultiPartEntity { }
        val sourceModel =
            sharedPrefsManager.getStringValue(SharedPrefsConstants.SOURCE_MODEL, "Null")
        val device = sharedPrefsManager.getStringValue(SharedPrefsConstants.DEVICE, "Null")
        val noise = sharedPrefsManager.getStringValue(SharedPrefsConstants.NOISE, "Null")
        val sourceDistance =
            sharedPrefsManager.getStringValue(SharedPrefsConstants.SOURCE_DISTANCE, "Null")
        val location = sharedPrefsManager.getStringValue(SharedPrefsConstants.LOCATION, "Null")
        val duration = sharedPrefsManager.getStringValue(SharedPrefsConstants.DURATION, "Null")
        val source = sharedPrefsManager.getStringValue(SharedPrefsConstants.SOURCE, "Null")
        val userEmail =
            sharedPrefsManager.getStringValue(SharedPrefsConstants.USER_EMAIL, "Null")
        entity.addPart(SharedPrefsConstants.USER_EMAIL, StringBody(userEmail))
        entity.addPart("AudioType", StringBody("Speaker"))
        entity.addPart("image", FileBody(file))
        entity.addPart(SharedPrefsConstants.SOURCE_MODEL, StringBody(sourceModel))
        entity.addPart(SharedPrefsConstants.DEVICE, StringBody(device))
        entity.addPart(SharedPrefsConstants.NOISE, StringBody(noise))
        entity.addPart(SharedPrefsConstants.SOURCE_DISTANCE, StringBody(sourceDistance))
        entity.addPart(SharedPrefsConstants.LOCATION, StringBody(location))
        entity.addPart(SharedPrefsConstants.DURATION, StringBody(duration))
        entity.addPart(SharedPrefsConstants.SOURCE, StringBody(source))
        return entity

    }

    fun delteFile(file: File, callback: FileDeleteCallback) {
        if (file.exists()) {
            if (file.delete()) {
                callback.onDeleteSuccess("File Deleted Successfully!")
            } else {
                callback.onDeleteFail("File Delete Failed")
            }
        }

    }


}