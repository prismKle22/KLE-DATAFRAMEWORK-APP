package com.samsung.prism.android.app.aidatacapture.others.fileUploaders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.samsung.prism.android.app.aidatacapture.Retrofit.Interfaces.APIService;
import com.samsung.prism.android.app.aidatacapture.activities.metaData.MetaDataHumanCentricActivity;
import com.samsung.prism.android.app.aidatacapture.constants.Constants;
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants;
import com.samsung.prism.android.app.aidatacapture.others.PostData;
import com.samsung.prism.android.app.aidatacapture.others.apiClients.ApiClient;
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import static android.content.ContentValues.TAG;
import static com.samsung.prism.android.app.aidatacapture.constants.Constants.MIME_TYPE_TEXT;

public class MultipleAgreementUploader {

    public FileUploaderCallback fileUploaderCallback;
    private File[] files;
    public int uploadIndex = -1;
    private String uploadURL = "";
    private long totalFileLength = 0;
    private long totalFileUploaded = 0;
    private String filekey = "";
    private APIService apiService;
    private String auth_token = "";
    private Context context;
    private String[] responses;


    public interface FileUploaderCallback {
        void onError();

        void onFinish(String[] responses);

        void onProgressUpdate(int currentpercent, int totalpercent, int filenumber);
    }

    public class PRRequestBody extends RequestBody {
        private File mFile;

        private static final int DEFAULT_BUFFER_SIZE = 2048;

        public PRRequestBody(final File file) {
            mFile = file;

        }

        @Override
        public MediaType contentType() {
            // i want to upload only images
            return MediaType.parse("image/*");
        }

        @Override
        public long contentLength() throws IOException {
            return mFile.length();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            long fileLength = mFile.length();
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            FileInputStream in = new FileInputStream(mFile);
            long uploaded = 0;

            try {
                int read;
                Handler handler = new Handler(Looper.getMainLooper());
                while ((read = in.read(buffer)) != -1) {

                    // update progress on UI thread
                    handler.post(new ProgressUpdater(uploaded, fileLength));
                    uploaded += read;
                    sink.write(buffer, 0, read);
                }
            } finally {
                in.close();
            }
        }
    }

    public MultipleAgreementUploader() {
        apiService = ApiClient.getClient().create(APIService.class);
    }

    public void uploadFiles(String url, String filekey, File[] files, FileUploaderCallback fileUploaderCallback, Context context) {
        uploadFiles(url, filekey, files, fileUploaderCallback, "", context);
    }

    public void uploadFiles(String url, String filekey, File[] files, FileUploaderCallback fileUploaderCallback, String auth_token, Context context) {
        this.fileUploaderCallback = fileUploaderCallback;
        this.files = files;
        this.uploadIndex = -1;
        this.uploadURL = url;
        this.filekey = filekey;
        this.context = context;
        this.auth_token = auth_token;
        totalFileUploaded = 0;
        totalFileLength = 0;
        uploadIndex = -1;
        responses = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            totalFileLength = totalFileLength + files[i].length();
        }
        uploadNext();
    }

    private void uploadNext() {
        if (files.length > 0) {
            if (uploadIndex != -1)
                totalFileUploaded = totalFileUploaded + files[uploadIndex].length();
            uploadIndex++;
            if (uploadIndex < files.length) {
                uploadSingleFile(uploadIndex);
            } else {
                fileUploaderCallback.onFinish(responses);
                context.startActivity(new Intent(context, MetaDataHumanCentricActivity.class));
                ((Activity) context).finish();
            }
        } else {
            fileUploaderCallback.onFinish(responses);
            context.startActivity(new Intent(context, MetaDataHumanCentricActivity.class));
            ((Activity) context).finish();
        }
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MediaType.parse(MIME_TYPE_TEXT), descriptionString);
    }


    private void uploadSingleFile(final int index) {
        PRRequestBody fileBody = new PRRequestBody(files[index]);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(filekey, files[index].getName(), fileBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), files[index].getName());
        PostData data = new PostData();
        SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(context);
        String email = sharedPrefsManager.getStringValue(SharedPrefsConstants.USER_EMAIL, Constants.SHARED_PREF_ADMIN_EMAIL);
        RequestBody Category = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.CATEGORY_NAME, "Other"));
        RequestBody subCategory = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.SUBCATEGORY_NAME, "Other"));
        RequestBody username = createPartFromString(email);
        Call<JsonElement> call;
        call = apiService.uploadAgreement(Category, subCategory, username, filePart, filename);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    JsonElement jsonElement = response.body();
                    Log.d(TAG, "onResponse: " + response.body());
                } else {
                    responses[index] = "";
                    Log.d(TAG, "onResponse: " + response.body());
                }
                uploadNext();
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                fileUploaderCallback.onError();
            }
        });
    }

    private class ProgressUpdater implements Runnable {
        private final long mUploaded;
        private long mTotal;

        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            int current_percent = (int) (100 * mUploaded / mTotal);
            int total_percent = (int) (100 * (totalFileUploaded + mUploaded) / totalFileLength);
            fileUploaderCallback.onProgressUpdate(current_percent, total_percent, uploadIndex + 1);
        }
    }
}
