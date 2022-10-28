package com.samsung.prism.android.app.aidatacapture.Retrofit.Interfaces;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.gson.JsonElement;
import com.samsung.prism.android.app.aidatacapture.constants.Constants;
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants;
import com.samsung.prism.android.app.aidatacapture.models.CategoryModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIService {

    @POST("getCategories.php")
        // API's endpoints
    Call<List<CategoryModel>> getCategoryList();

    @POST("getSubCategories.php")
        // API's endpoints
    Call<List<CategoryModel>> getSubCategoryList();

    @Multipart
    @POST("upload-api/uploadPdf.php")
    Call<ResponseBody> uploadPdfMeta(
            @Part(Constants.HUMAN_CENTRIC_AGREEMENT) RequestBody humanCentricAgreement,
            @Part(SharedPrefsConstants.IMAGE_TYPE) RequestBody isHumanCentric,
            @Part(Constants.DESCRIPTION) RequestBody description,
            @Part(Constants.SIZE) RequestBody size,
            @Part(SharedPrefsConstants.CATEGORY) RequestBody category,
            @Part(Constants.SUB_CATEGORY) RequestBody subCat,
            @Part(Constants.USER_NAME) RequestBody userName,
            @Part(Constants.DATA_TYPE) RequestBody dataType,
            @Part(Constants.DEVICE_USED) RequestBody deviceUsed,
            @Part(Constants.RESOLUTION) RequestBody resolution,
            @Part(SharedPrefsConstants.ORIENTATION) RequestBody orientation,
            @Part(Constants.DSLR_MOBILE) RequestBody dslrOrMobile,
            @Part(Constants.DATA_MAJOR_CATEGORY) RequestBody dataMajorCategory,
            @Part(SharedPrefsConstants.LOCATION) RequestBody location,
            @Part(SharedPrefsConstants.SUB_LOCATION) RequestBody subLocation,
            @Part(SharedPrefsConstants.TIMING) RequestBody timing,
            @Part(SharedPrefsConstants.LIGHTING) RequestBody lighting,
            @Part(Constants.HUMAN_PRESENT) RequestBody humanPresent,
            @Part(SharedPrefsConstants.SELFIE) RequestBody selfie,
            @Part("type") RequestBody type,
            @Part(SharedPrefsConstants.CHILDREN) RequestBody children,
            @Part(SharedPrefsConstants.PROPS) RequestBody props,
            @Part(Constants.CONSENT_OBTAINED) RequestBody consentObtained
    );

    @Multipart
    @POST("multipleAgreement.php")
    Call<ResponseBody> uploadMultipleAgreement(
            @Part(SharedPrefsConstants.CATEGORY) RequestBody category,
            @Part(Constants.SUB_CATEGORY) RequestBody subCat,
            @Part(Constants.USER_NAME) RequestBody userName,
            @Part(Constants.DESCRIPTION) RequestBody description,
            @Part(Constants.SIZE) RequestBody size,
            @Part List<MultipartBody.Part> files);

    @Multipart
    @POST("phps/multipleAudioUpload.php")
    Call<JsonElement> uploadNoiseAudio(
            @Part("AudioType") RequestBody audioType,
            @Part(SharedPrefsConstants.USER_EMAIL) RequestBody useremail,
            @Part(SharedPrefsConstants.SOURCE_DISTANCE) RequestBody sourceDistance,
            @Part(SharedPrefsConstants.MULTIPLE_NOISE) RequestBody multipleNoise,
            @Part(SharedPrefsConstants.NOISE_OBJECT) RequestBody noiseObject,
            @Part(SharedPrefsConstants.LOCATION) RequestBody location,
            @Part(SharedPrefsConstants.DEVICE) RequestBody device,
            @Part MultipartBody.Part file,
            @Part("file") RequestBody name
    );

    @Multipart
    @POST("phps/multipleAudioUpload.php")
    Call<JsonElement> uploadSpeechAudio(
            @Part("AudioType") RequestBody audioType,
            @Part(SharedPrefsConstants.USER_EMAIL) RequestBody useremail,
            @Part(SharedPrefsConstants.GENDER) RequestBody gender,
            @Part(SharedPrefsConstants.AGE_GROUP) RequestBody ageGroup,
            @Part(SharedPrefsConstants.NOISE) RequestBody noise,
            @Part(SharedPrefsConstants.SOURCE_DISTANCE) RequestBody sourceDistance,
            @Part(SharedPrefsConstants.DEVICE) RequestBody device,
            @Part(SharedPrefsConstants.LANGUAGE) RequestBody language,
            @Part(SharedPrefsConstants.LOCATION) RequestBody location,
            @Part(SharedPrefsConstants.MULTIPLE_SPEAKERS) RequestBody multipleSpeakers,
            @Part MultipartBody.Part file,
            @Part("file") RequestBody name
    );

    @Multipart
    @POST("phps/multipleAudioUpload.php")
    Call<JsonElement> uploadSpeakerAudio(
            @Part("AudioType") RequestBody audioType,
            @Part(SharedPrefsConstants.USER_EMAIL) RequestBody useremail,
            @Part(SharedPrefsConstants.SOURCE_MODEL) RequestBody sourceModel,
            @Part(SharedPrefsConstants.DEVICE) RequestBody device,
            @Part(SharedPrefsConstants.NOISE) RequestBody noise,
            @Part(SharedPrefsConstants.SOURCE_DISTANCE) RequestBody sourceDistance,
            @Part(SharedPrefsConstants.LOCATION) RequestBody location,
            @Part(SharedPrefsConstants.DURATION) RequestBody duration,
            @Part(SharedPrefsConstants.SOURCE) RequestBody source,
            @Part MultipartBody.Part file, @Part("file") RequestBody name);

    @Multipart
    @POST("upload-api/uploadHelperApi.php")
    Call<JsonElement> uploadAgreement(
            @Part(SharedPrefsConstants.CATEGORY) RequestBody category,
            @Part(Constants.SUB_CATEGORY) RequestBody subCat,
            @Part(Constants.USER_NAME) RequestBody userName,
            @Part MultipartBody.Part file, @Part("file") RequestBody name);

    @Multipart
    @POST("upload-api/multipleImageUpload.php")
    Call<JsonElement> uploadMultipleImage(
            @Part(Constants.HUMAN_CENTRIC_AGREEMENT) RequestBody humanCentricAgreement,
            @Part(SharedPrefsConstants.IMAGE_TYPE) RequestBody isHumanCentric,
            @Part(Constants.DESCRIPTION) RequestBody description,
            @Part(SharedPrefsConstants.CATEGORY) RequestBody category,
            @Part(Constants.SUB_CATEGORY) RequestBody subCat,
            @Part(Constants.USER_NAME) RequestBody userName,
            @Part(Constants.DATA_TYPE) RequestBody dataType,
            @Part(Constants.DEVICE_USED) RequestBody deviceUsed,
            @Part(Constants.RESOLUTION) RequestBody resolution,
            @Part(SharedPrefsConstants.ORIENTATION) RequestBody orientation,
            @Part(Constants.DSLR_MOBILE) RequestBody dslrOrMobile,
            @Part(Constants.DATA_MAJOR_CATEGORY) RequestBody dataMajorCategory,
            @Part(SharedPrefsConstants.LOCATION) RequestBody location,
            @Part(SharedPrefsConstants.SUB_LOCATION) RequestBody subLocation,
            @Part(SharedPrefsConstants.TIMING) RequestBody timing,
            @Part(SharedPrefsConstants.LIGHTING) RequestBody lighting,
            @Part(SharedPrefsConstants.IS_HUMAN_PRESENT) RequestBody humanPresent,
            @Part(SharedPrefsConstants.SELFIE) RequestBody selfie,
            @Part("type") RequestBody type,
            @Part(SharedPrefsConstants.CHILDREN) RequestBody children,
            @Part(SharedPrefsConstants.PROPS) RequestBody props,
            @Part("Constants.CONSENT_OBTAINED") RequestBody consentObtained,
            @Part MultipartBody.Part file,
            @Part("file") RequestBody name
    );
}