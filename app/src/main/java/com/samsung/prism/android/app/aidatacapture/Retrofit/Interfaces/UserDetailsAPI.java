package com.samsung.prism.android.app.aidatacapture.Retrofit.Interfaces;

import com.samsung.prism.android.app.aidatacapture.models.UserDetailsModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.POST;

public interface UserDetailsAPI {

    @POST("phps/admin-api/getUserDetails.php")
        // API's endpoints
    Call<List<UserDetailsModel>> getUsersList();
}