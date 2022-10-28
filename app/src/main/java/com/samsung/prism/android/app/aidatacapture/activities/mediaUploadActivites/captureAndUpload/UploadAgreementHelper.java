package com.samsung.prism.android.app.aidatacapture.activities.mediaUploadActivites.captureAndUpload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.samsung.prism.android.app.aidatacapture.R;
import com.samsung.prism.android.app.aidatacapture.constants.Constants;
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants;
import com.samsung.prism.android.app.aidatacapture.models.ResponseModel;
import com.samsung.prism.android.app.aidatacapture.others.AndroidMultiPartEntity;
import com.samsung.prism.android.app.aidatacapture.others.apiClients.UserServiceAPIClient;
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager;
import com.samsung.prism.android.app.aidatacapture.utils.CheckInternet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadAgreementHelper extends AppCompatActivity {

    private static final String TAG = "Msg";
    private SharedPrefsManager sharedPrefsManager;
    private long totalSize = 0;
    private ProgressBar progressBar;
    private String filePath = null;
    private TextView txtPercentage;
    private ImageView imgPreview;
    private VideoView vidPreview;
    private MaterialCardView btnUpload;
    private CheckInternet checkInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_agreement);
        sharedPrefsManager = new SharedPrefsManager(this);
        initToolbar();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        backButton();
        checkInternet = new CheckInternet(this);
        txtPercentage = findViewById(R.id.txtPercentage);
        btnUpload = findViewById(R.id.btnUpload);
        progressBar = findViewById(R.id.progressBar);
        imgPreview = findViewById(R.id.imgPreview);
        vidPreview = findViewById(R.id.videoPreview);
        // Changing action bar background color
        //getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(getResources().getString(R.color.action_bar))));

        // Receiving the data from previous activity
        Intent i = getIntent();

        // image or video path that is captured in previous activity
        filePath = i.getStringExtra("filePath");

        // boolean flag to identify the media type, image or video
        boolean isImage = i.getBooleanExtra("isImage", true);

        if (filePath != null) {
            // Displaying the image or video on the screen
            previewMedia(isImage);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }

        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // uploading the file to server
                if (checkInternet.isInternetAvailable()) {
                    new UploadAgreementHelper.UploadFileToServer().execute();
                } else {
                    Toast.makeText(UploadAgreementHelper.this, "Internet not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initToolbar() {
        TextView toolbarName;
        toolbarName = findViewById(R.id.toolbar_name);
        toolbarName.setText("Agreement");
    }

    private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
            imgPreview.setVisibility(View.VISIBLE);
            vidPreview.setVisibility(View.GONE);
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            imgPreview.setImageBitmap(bitmap);
        } else {
            imgPreview.setVisibility(View.GONE);
            vidPreview.setVisibility(View.VISIBLE);
            vidPreview.setVideoPath(filePath);
            // start playing
            vidPreview.start();
        }
    }

    private void backButton() {
        ImageView backButton = findViewById(R.id.toolbar_image);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            Log.d(Constants.TAG, "Upload file to server called");
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress[0]);
            txtPercentage.setText(progress[0] + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {


            String email = sharedPrefsManager.getStringValue(SharedPrefsConstants.USER_EMAIL, Constants.SHARED_PREF_ADMIN_EMAIL);
            String Category = sharedPrefsManager.getStringValue(SharedPrefsConstants.CATEGORY_NAME, "Other");
            String subCategory = sharedPrefsManager.getStringValue(SharedPrefsConstants.SUBCATEGORY_NAME, "Other");

            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.AGREEMENT_UPLOAD_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filePath);
                entity.addPart("image", new FileBody(sourceFile));
                entity.addPart(SharedPrefsConstants.CATEGORY, new StringBody(Category));
                entity.addPart(Constants.SUB_CATEGORY, new StringBody(subCategory));
                entity.addPart(Constants.USER_NAME, new StringBody(email));
                entity.addPart(Constants.USER_NAME, new StringBody(email));
                entity.addPart(Constants.AGREEMENT, new StringBody("Agreements"));

                httpclient.getConnectionManager().getSchemeRegistry().register(
                        new Scheme("https", SSLSocketFactory.getSocketFactory(), 443)
                );
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            // showing the server response in an alert dialog
            //showAlert(result);
            Toast.makeText(UploadAgreementHelper.this, R.string.agreement_uploaded, Toast.LENGTH_SHORT).show();
            String email = sharedPrefsManager.getStringValue(SharedPrefsConstants.USER_EMAIL, Constants.SHARED_PREF_ADMIN_EMAIL);
            Log.d(TAG, "onPostExecute: " + email);
            sharedPrefsManager.saveBoolValue(SharedPrefsConstants.IS_ALL_AGREEMENT_UPLOADED, true);
            Call<ResponseModel> call = UserServiceAPIClient.getInstance().getMyApi().addAgreement(email, "true");
            call.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                    Log.d(Constants.TAG, response.toString());
                    if (response.isSuccessful()) {
                        Log.d(Constants.TAG, getString(R.string.response_successfull));
                        startActivity(new Intent(UploadAgreementHelper.this, UploadAgreementActivity.class));
                        finish();
                    } else {
                        Log.d(Constants.TAG, "Failed");
                    }
                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {

                }
            });
        }


    }


}