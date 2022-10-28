package com.samsung.prism.android.app.aidatacapture.others

import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.samsung.prism.android.app.aidatacapture.constants.Constants.BASE_URL
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import java.io.IOException
import java.util.*

class PostData {
    @Throws(IOException::class)
    fun SendtoPHP(categoryName: String, userId: String) {
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val httpclient: HttpClient = DefaultHttpClient()
        val httppost = HttpPost(BASE_URL + "fileUpload.php")
        try {
            val nameValuePairs: MutableList<NameValuePair> = ArrayList(2)
            nameValuePairs.add(BasicNameValuePair("responseFromAppCat", "" + categoryName))
            nameValuePairs.add(BasicNameValuePair("responseFromAppUser", "" + userId))
            httppost.entity = UrlEncodedFormEntity(nameValuePairs)
            val response = httpclient.execute(httppost)
        } catch (e: ClientProtocolException) {
            // TODO Auto-generated catch block
        } catch (e: IOException) {
            // TODO Auto-generated catch block
        }
    }
}