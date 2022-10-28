package com.samsung.prism.android.app.aidatacapture.repos

import android.content.Context
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.models.ResponseModel
import retrofit2.Response
import java.util.concurrent.TimeUnit

class SharedPrefsHelper(activityContext: Context?) {

    var context: Context = activityContext!!
    private var sharedPrefsManager = SharedPrefsManager(context);

    fun saveInitialSharedPrefs(response: Response<ResponseModel>) {
        sharedPrefsManager.saveStringValue(
            SharedPrefsConstants.USER_EMAIL,
            response.body()?.email
        )
        sharedPrefsManager.saveBoolValue(SharedPrefsConstants.IS_LOGGED_IN, true)
        sharedPrefsManager.saveBoolValue(SharedPrefsConstants.IS_FROM_REGISTER, false)
        sharedPrefsManager.saveLongValue(
            "ExpiredDate",
            System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1)
        )
//        sharedPrefsManager.saveStringValue(
//            SharedPrefsConstants.USER_FULL_NAME,
//            response.body()?.fullName
//        )
    }

    fun saveMetadataForHumanCentric(
        categoryString: String?,
        locationString: String?,
        subLocationString: String?,
        timingString: String?,
        lightingString: String?,
        myDeviceModel: String?,
        screenSize: String?,
        selfieString: String?,
        typeString: String?,
        aboveEighteenString: String?,
        propsString: String?,
        consentString: String?
    ): Boolean {
        sharedPrefsManager!!.saveStringValue(
            SharedPrefsConstants.DATA_MAJOR_CATEGORY,
            categoryString
        )
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.LOCATION, locationString)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.SUB_LOCATION, subLocationString)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.TIMING, timingString)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.LIGHTING, lightingString)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.MODEL, myDeviceModel)
        sharedPrefsManager.saveStringValue(
            SharedPrefsConstants.ORIENTATION,
            context.getString(R.string.potrait_text)
        )
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.SCREEN_SIZE, screenSize)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.IS_IT_DSLR, "No")
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.CATEGORY, categoryString)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.IS_HUMAN_PRESENT, "Yes")
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.SELFIE, selfieString)
        sharedPrefsManager.saveStringValue(Constants.TYPE, typeString)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.CHILDREN, aboveEighteenString)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.PROPS, propsString)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.CONSENT, consentString)
        sharedPrefsManager.saveBoolValue(SharedPrefsConstants.IS_FROM_METADATA, true)
        return true
    }

    fun saveWordImageMetaData(
        language: String,
        content: String,
        readability: String,
        clarity: String
    ): Boolean {
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.LANGUAGE, language)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.CONTENT, content)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.READABILITY, readability)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.CLARITY, clarity)
        return true
    }

    fun saveSpeakerMetaData(
        sourceModel: String?,
        device: String?,
        noise: String?,
        sourceDistance: String?,
        location: String?,
        duration: String?,
        source: String?
    ): Boolean {
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.SOURCE_DISTANCE, sourceDistance)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.DEVICE, device)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.NOISE, noise)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.SOURCE,source)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.SOURCE_MODEL, sourceModel)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.LOCATION, location)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.DURATION, duration)
        return true
    }

    fun saveSpeechMetaData(
        gender: String?,
        ageGroup: String?,
        noise: String?,
        sourceDistace: String?,
        device: String?,
        languages: String?,
        location: String?,
        multipleSpeakers: String?
    ):Boolean{
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.GENDER,gender)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.AGE_GROUP,ageGroup)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.NOISE,noise)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.SOURCE_DISTANCE,sourceDistace)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.DEVICE,device)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.LANGUAGE,languages)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.LOCATION,location)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.MULTIPLE_SPEAKERS,multipleSpeakers)
        return true
    }
    fun saveNoiseMetaData(
        sourceDistance: String?,
        multipleNoise: String?,
        noiseObject: String?,
        location: String?,
        device: String?
    ):Boolean{
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.SOURCE_DISTANCE,sourceDistance)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.DEVICE,device)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.NOISE_OBJECT,noiseObject)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.LOCATION,location)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.MULTIPLE_NOISE,multipleNoise)
        return true
    }
    fun saveCorpussmsMetaData(

            type: String?,
            corpus_class: String?,
            personality: String?,
            mood: String?,
            age: String?,
            gender: String?
    ):Boolean{
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.TYPE,type)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.CLASS,corpus_class)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.PERSONALITY,personality)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.MOOD,mood)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.AGE,age)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.GENDER,gender)
        return true
    }
    fun saveTexttMetaData(
            type : String?,
            texttclass : String?,
            tag : String?
    ):Boolean{
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.TYPE,type)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.CLASS,texttclass)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.TAG,tag)
         return true
    }
    fun saveTranslationMetaData(

            language : String?,
            standard : String?,
            category : String?,
            mixedSource : String?,
            numbers : String?
    ): Boolean {
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.LANGUAGE, language)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.STANDARD, standard)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.CATEGORY, category)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.MIXED_SOURCE,mixedSource)
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.NUMBERS, numbers)

        return true
    }

    fun saveTextMetaData(filetype: String) :Boolean{
        sharedPrefsManager.saveStringValue(SharedPrefsConstants.FILE_TYPE,filetype)
        return true}
}