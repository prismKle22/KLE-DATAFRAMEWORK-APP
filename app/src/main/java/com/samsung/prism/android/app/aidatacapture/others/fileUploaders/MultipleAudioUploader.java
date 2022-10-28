package com.samsung.prism.android.app.aidatacapture.others.fileUploaders;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.samsung.prism.android.app.aidatacapture.Retrofit.Interfaces.APIService;
import com.samsung.prism.android.app.aidatacapture.constants.Constants;
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants;
import com.samsung.prism.android.app.aidatacapture.others.apiClients.ApiClient;
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.ContentValues.TAG;
import static com.samsung.prism.android.app.aidatacapture.constants.Constants.MIME_TYPE_TEXT;

public class MultipleAudioUploader {

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
            return MediaType.parse("audio/*");
        }

        @Override
        public long contentLength() {
            return mFile.length();
        }

        @Override
        public void writeTo(@NotNull BufferedSink sink) throws IOException {
            long fileLength = mFile.length();
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

            try (FileInputStream in = new FileInputStream(mFile)) {
                long uploaded = 0;
                int read;
                Handler handler = new Handler(Looper.getMainLooper());
                while ((read = in.read(buffer)) != -1) {

                    // update progress on UI thread
                    handler.post(new ProgressUpdater(uploaded, fileLength));
                    uploaded += read;
                    sink.write(buffer, 0, read);
                }
            }
        }
    }

    public MultipleAudioUploader() {
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
        for (File file : files) {
            totalFileLength = totalFileLength + file.length();
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
            }
        } else {
            fileUploaderCallback.onFinish(responses);
        }
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MediaType.parse(MIME_TYPE_TEXT), descriptionString);
    }

    private void uploadSingleFile(final int index) {
        Log.d(TAG, "uploadSingleFile: " + files[index].getName());
        Call<JsonElement> call;
        call = getCall(index);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                if (response.isSuccessful()) {
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

    private Call<JsonElement> getCall(int index) {
        SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(context);
        PRRequestBody fileBody = new PRRequestBody(files[index]);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(filekey, files[index].getName(), fileBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), files[index].getName());
        int viewType = sharedPrefsManager.getIntValue(
                SharedPrefsConstants.VIEW_TYPE,
                Constants.ViewTypeForAudio.NOISE.ordinal()
        );

        if (viewType == 0) {
            RequestBody audioType = createPartFromString("Noise");
            RequestBody userEmail = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.USER_EMAIL,""));
            RequestBody sourceDistance = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.SOURCE_DISTANCE, ""));
            RequestBody multipleNoise = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.MULTIPLE_NOISE, ""));
            RequestBody noiseObject = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.NOISE_OBJECT, ""));
            RequestBody location = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.LOCATION, " "));
            RequestBody device = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.DEVICE, " "));
            return apiService.uploadNoiseAudio(audioType,userEmail,sourceDistance, multipleNoise, noiseObject, location, device, filePart, filename);
        } else if (viewType == 1) {
            RequestBody audioType = createPartFromString("Speech");
            RequestBody userEmail = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.USER_EMAIL,""));
            RequestBody gender = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.GENDER, ""));
            RequestBody ageGroup = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.AGE_GROUP, " "));
            RequestBody noise = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.NOISE, " "));
            RequestBody sourceDistance = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.SOURCE_DISTANCE, ""));
            RequestBody device = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.DEVICE, " "));
            RequestBody language = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.LANGUAGE, " "));
            RequestBody location = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.LOCATION, " "));
            RequestBody multipleSpeakers = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.MULTIPLE_SPEAKERS, " "));
            return apiService.uploadSpeechAudio(audioType,userEmail,gender, ageGroup, noise, sourceDistance, device, language, location, multipleSpeakers, filePart, filename);
        } else {
            RequestBody audioType = createPartFromString("Speaker");
            RequestBody userEmail = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.USER_EMAIL,""));
            RequestBody sourceModel = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.SOURCE_MODEL, " "));
            RequestBody device = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.DEVICE, " "));
            RequestBody noise = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.NOISE, " "));
            RequestBody sourceDistance = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.SOURCE_DISTANCE, " "));
            RequestBody location = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.LOCATION, " "));
            RequestBody duration = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.DURATION, " "));
            RequestBody source = createPartFromString(sharedPrefsManager.getStringValue(SharedPrefsConstants.SOURCE, ""));
            return apiService.uploadSpeakerAudio(audioType,userEmail,sourceModel, device, noise, sourceDistance, location, duration, source, filePart, filename);
        }
    }

    private class ProgressUpdater implements Runnable {
        private final long mUploaded;
        private final long mTotal;

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
