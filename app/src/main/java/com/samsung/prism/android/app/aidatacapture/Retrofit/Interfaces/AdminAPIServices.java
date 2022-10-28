package com.samsung.prism.android.app.aidatacapture.Retrofit.Interfaces;

import com.samsung.prism.android.app.aidatacapture.models.AdminResponseModel;
import com.samsung.prism.android.app.aidatacapture.models.AudioCountModel;
import com.samsung.prism.android.app.aidatacapture.models.ModelApproveUser;
import com.samsung.prism.android.app.aidatacapture.models.OverallCountModel;
import com.samsung.prism.android.app.aidatacapture.models.UserCountDashboard;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AdminAPIServices {

    @FormUrlEncoded
    @POST("phps/admin-api/getCount.php")
    Call<AdminResponseModel> dashboardCount(
            @Field("text") String text
    );

    @FormUrlEncoded
    @POST("phps/admin-api/getEachUserCount.php")
    Call<OverallCountModel> userDashboardCount(
            @Field("email") String text
    );
    @FormUrlEncoded
    @POST("phps/admin-api/getAudioCount.php")
    Call<AudioCountModel> audioCount(
            @Field("email") String text
    );

    @FormUrlEncoded
    @POST("phps/admin-api/getAudioCountAdmin.php")
    Call<AudioCountModel> audioCountAdmin(
            @Field("email") String text
    );

    @FormUrlEncoded
    @POST("phps/admin-api/getCountOfAllData.php")
    Call<OverallCountModel> overAllCount(
            @Field("email") String text
    );

    @FormUrlEncoded
    @POST("phps/admin-api/approveUser.php")
    Call<AdminResponseModel> approveUser(
            @Field("email") String text
    );

    @POST("phps/admin-api/getApproveUsers.php")
        // API's endpoints
    Call<List<ModelApproveUser>> getUsersList();

}