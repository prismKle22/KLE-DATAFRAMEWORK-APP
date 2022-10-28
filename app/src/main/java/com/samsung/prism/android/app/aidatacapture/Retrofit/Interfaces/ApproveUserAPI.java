package com.samsung.prism.android.app.aidatacapture.Retrofit.Interfaces;

import com.samsung.prism.android.app.aidatacapture.models.ModelApproveUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.POST;

public interface ApproveUserAPI {
    @POST("/admin-api/getApproveUsers.php")
        // API's endpoints
    Call<List<ModelApproveUser>> getUsersList();
}
