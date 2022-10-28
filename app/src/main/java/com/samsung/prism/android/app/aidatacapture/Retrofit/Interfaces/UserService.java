package com.samsung.prism.android.app.aidatacapture.Retrofit.Interfaces;

import com.samsung.prism.android.app.aidatacapture.constants.Constants;
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants;
import com.samsung.prism.android.app.aidatacapture.models.ConsentResponseModel;
import com.samsung.prism.android.app.aidatacapture.models.DownloadUpdateModel;
import com.samsung.prism.android.app.aidatacapture.models.ResponseModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserService {

    @FormUrlEncoded
    @POST("phps/api/Users/addUser.php")
    Call<ResponseModel> insertUser(
            @Field("firstName") String firstName,
            @Field("lastName") String lastName,
            @Field("mobileNumber") String mobileNumber,
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("phps/api/Users/getUserDetails.php")
    Call<ResponseModel> userLogin(
            @Field("email") String email,
            @Field("password") String password

    );

    @Multipart
    @POST("data-framework/ConsentUploadAPI.php?apicall=uplaod")
    Call<ConsentResponseModel> userConsentUpload(
            @Part MultipartBody.Part part,
            @Part("usrname") RequestBody requestBody

    );

    @FormUrlEncoded
    @POST("phps/fileUpload.php")
    Call<ResponseBody> folders(
            @Field(Constants.SUB_CATEGORY) String SubCat,
            @Field(SharedPrefsConstants.CATEGORY) String Category,
            @Field(Constants.USER_NAME) String userName
    );

    @FormUrlEncoded
    @POST("phps/api/Users/emailValidation.php")
    Call<ResponseModel> checkEmail(
            @Field("email") String email
    );


    @FormUrlEncoded
    @POST("phps/api/Users/mobileNumberValidation.php")
    Call<ResponseModel> checkMobileNumber(
            @Field("mobileNumber") String mobileNumber
    );

    @FormUrlEncoded
    @POST("phps/storeFeedback.php")
    Call<ResponseModel> addFeedBack(
            @Field(Constants.USER_NAME) String userName,
            @Field("feedback") String feedback
    );

    @FormUrlEncoded
    @POST("phps/email-helper/send.php")
    Call<ResponseModel> resetPassword(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("phps/admin-api/resistrationAgreement.php")
    Call<ResponseModel> addAgreement(
            @Field("email") String email,
            @Field("isUploaded") String isUploaded
    );

    @FormUrlEncoded
    @POST("phps/update-helper-api/appUpdateHelperApi.php")
    Call<DownloadUpdateModel> getUpdate(
            @Field("email") String email
    );


}
