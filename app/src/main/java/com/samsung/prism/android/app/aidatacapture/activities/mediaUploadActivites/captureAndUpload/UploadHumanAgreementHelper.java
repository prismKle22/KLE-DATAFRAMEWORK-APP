package com.samsung.prism.android.app.aidatacapture.activities.mediaUploadActivites.captureAndUpload;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.samsung.prism.android.app.aidatacapture.activities.UserFeedBackActivity;
import com.samsung.prism.android.app.aidatacapture.activities.UserUploadMenuActivity;
import com.samsung.prism.android.app.aidatacapture.activities.metaData.MetaDataHumanCentricActivity;
import com.samsung.prism.android.app.aidatacapture.constants.Constants;
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants;
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues;
import com.samsung.prism.android.app.aidatacapture.others.AndroidMultiPartEntity;
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager;
import com.samsung.prism.android.app.aidatacapture.utils.CheckInternet;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

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

public class UploadHumanAgreementHelper extends AppCompatActivity {

    private static final String TAG = UserUploadMenuActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private String filePath = null;
    private TextView txtPercentage;
    private ImageView imgPreview;
    private VideoView vidPreview;
    private MaterialCardView btnUpload;
    private SharedPrefsManager sharedPrefsManager;
    private long totalSize = 0;
    private CheckInternet checkInternet;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_human_agreement);
        sharedPrefsManager = new SharedPrefsManager(this);
        initToolbar();
        backButton();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        context = this;
        checkInternet = new CheckInternet(this);
        txtPercentage = findViewById(R.id.txtPercentage);
        btnUpload = findViewById(R.id.btnUpload);
        progressBar = findViewById(R.id.progressBar);
        imgPreview = findViewById(R.id.imgPreview);
        vidPreview = findViewById(R.id.videoPreview);
        toolbarClick();
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
                    new UploadFileToServer().execute();
                } else {
                    Toast.makeText(UploadHumanAgreementHelper.this, "Internet not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void initToolbar() {
        TextView toolbarName;
        toolbarName = findViewById(R.id.toolbar_name);
        toolbarName.setText("Human Centric");
    }

    private void toolbarClick() {
        ImageView feedback, logout;
        feedback = findViewById(R.id.toolbar_feedback);
        logout = findViewById(R.id.toolbar_logout);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadHumanAgreementHelper.this, UserFeedBackActivity.class));
                finish();

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialogues dialogue = new Dialogues();
                dialogue.logoutDialogue(context);

            }
        });

        feedback.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, getString(R.string.feedback_msg), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        logout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }


    public void dialogue() {

        MaterialDialog mDialog = new MaterialDialog.Builder(this)
                .setTitle(getString(R.string.upload_suceess_dialogue_message))
                .setMessage(getString(R.string.upload_more_messgae))
                .setCancelable(false)
                .setPositiveButton("Yes?", R.drawable.ic_baseline_check_24, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        startActivity(new Intent(UploadHumanAgreementHelper.this, UploadHumanCentricAgreementActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("No", R.drawable.ic_baseline_cancel_24, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        startActivity(new Intent(UploadHumanAgreementHelper.this, MetaDataHumanCentricActivity.class));
                        finish();
                    }
                })
                .build();

        // Show Dialog
        mDialog.show();
    }

    /**
     * Displaying captured image/video on the screen
     */
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

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void backButton() {
        ImageView backButton = findViewById(R.id.toolbar_image);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadHumanAgreementHelper.this, UploadHumanCentricAgreementActivity.class));
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(UploadHumanAgreementHelper.this, UploadHumanCentricAgreementActivity.class));
        finish();
    }

    /**
     * Uploading the file to server
     */
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
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
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
            HttpPost httppost = new HttpPost(Constants.HUMAN_CENTRIC_AGREEMENT_UPLOAD_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filePath);

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart(SharedPrefsConstants.CATEGORY, new StringBody(Category));
                entity.addPart(Constants.SUB_CATEGORY,
                        new StringBody(subCategory));
                entity.addPart(Constants.USER_NAME, new StringBody(email));
                entity.addPart(Constants.AGREEMENT, new StringBody("Agreements"));

                httpclient.getConnectionManager().getSchemeRegistry().register(
                        new Scheme("https", SSLSocketFactory.getSocketFactory(), 443)
                );
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    sharedPrefsManager.saveStringValue(SharedPrefsConstants.HUMAN_AGREEMENT_NAME, EntityUtils.toString(r_entity));
                    // Server response
                    responseString = EntityUtils.toString(r_entity, "UTF-8");

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
            Toast.makeText(UploadHumanAgreementHelper.this, "Agreement Uploaded", Toast.LENGTH_SHORT).show();
            dialogue();
            super.onPostExecute(result);
        }

    }


}