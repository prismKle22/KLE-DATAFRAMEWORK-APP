package com.samsung.prism.android.app.aidatacapture.activities.mediaUploadActivites.captureAndUpload;


/*Agreement Upload Activity- Activity after user registration page. User Needs to capture and
upload agreement and check privacy policy then he can move to next activity */


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.samsung.prism.android.app.aidatacapture.R;
import com.samsung.prism.android.app.aidatacapture.activities.DownloadAgreementActivity;
import com.samsung.prism.android.app.aidatacapture.activities.KLEPrivacyPolicy;
import com.samsung.prism.android.app.aidatacapture.activities.PrivacyPolicyActivity;
import com.samsung.prism.android.app.aidatacapture.activities.UserFeedBackActivity;
import com.samsung.prism.android.app.aidatacapture.activities.UserUploadMenuActivity;
import com.samsung.prism.android.app.aidatacapture.activities.accountRelatedActivities.UserLoginActivity;
import com.samsung.prism.android.app.aidatacapture.constants.Constants;
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants;
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues;
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UploadAgreementActivity extends AppCompatActivity {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final String TAG = UserUploadMenuActivity.class.getSimpleName();
    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CODE = 1;
    public SharedPrefsManager sharedPrefsManager;
    public String categoryName;
    private boolean exit = false;
    private Uri fileUri; // file url to store image/video
    private TextView agreement;
    private Button btnRecordVideo, btnUploadImage, btnUploadVideo;
    private MaterialCardView btnCapturePicture, btnNext;
    private CheckBox agreementCheckbox, agreementCheckboxAnother;
    private Context context;
    private TextView agreementAnother;

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Constants.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Constants.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        if (ContextCompat.checkSelfPermission(UploadAgreementActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(UploadAgreementActivity.this, Manifest.permission_group.MICROPHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission_group.MICROPHONE}, 100);

        }
        agreement = findViewById(R.id.agreement);
        agreementAnother = findViewById(R.id.agreementAnother);
        customTextView(agreement);
        customTextViewAnother(agreementAnother);
        //((TextView) findViewById(R.id.infoTxtCredits)).setMovementMethod(LinkMovementMethod.getInstance());
        btnNext = findViewById(R.id.btnAgreementNext);
        agreementCheckbox = findViewById(R.id.agreementCheckBox);
        agreementCheckboxAnother = findViewById(R.id.agreementCheckBoxAnother);
        backButton();
        context = this;
        floatingActionButton();
        agreementCheckboxAnother.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (agreementCheckboxAnother.isChecked()) {
                    btnNext.setVisibility(View.VISIBLE);
                } else {
                    btnNext.setVisibility(View.GONE);
                }
            }
        });
        toolbarClick();
        agreementCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (agreementCheckbox.isChecked() && agreementCheckboxAnother.isChecked()) {
                    btnNext.setVisibility(View.VISIBLE);
                } else {
                    btnNext.setVisibility(View.GONE);
                }
            }
        });

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        // Changing action bar background color
        // These two lines are not needed
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new SharedPrefsManager(context).getBoolValue(SharedPrefsConstants.IS_ALL_AGREEMENT_UPLOADED, false)) {
                    if (sharedPrefsManager.getBoolValue(SharedPrefsConstants.IS_FROM_REGISTER, false)) {
                        startActivity(new Intent(UploadAgreementActivity.this, UserLoginActivity.class));
                        Toast.makeText(context, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
                        new SharedPrefsManager(context).saveBoolValue(SharedPrefsConstants.IS_ALL_AGREEMENT_UPLOADED, true);
                        finish();
                    } else {
                        startActivity(new Intent(UploadAgreementActivity.this, UserLoginActivity.class));
                        Toast.makeText(context, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
                        new SharedPrefsManager(context).saveBoolValue(SharedPrefsConstants.IS_ALL_AGREEMENT_UPLOADED, true);
                        finish();
                    }
                } else {
                    Toast.makeText(context, "Please upload agreement first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sharedPrefsManager = new SharedPrefsManager(this);
        categoryName = sharedPrefsManager.getStringValue(SharedPrefsConstants.DATA_MAJOR_CATEGORY, "Null");
        //getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(getResources().getString(R.color.action_bar))));
        btnCapturePicture = findViewById(R.id.btnCapturePicture);
        btnRecordVideo = findViewById(R.id.btnRecordVideo);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnUploadVideo = findViewById(R.id.btnUploadVideo);

        /**
         * Capture image button click event
         */


        btnCapturePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                captureImage();
            }
        });

        /**
         * Record video button click event
         */
        btnRecordVideo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // record video
                recordVideo();
            }
        });

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }

        initToolbar();
    }

    private void initToolbar() {
        TextView toolbarName;
        toolbarName = findViewById(R.id.toolbar_name);
        toolbarName.setText("Agreement");
    }

    /**
     * Checking device has camera hardware or not
     */

    private boolean isDeviceSupportCamera() {
        // this device has a camera
        // no camera on this device
        return getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    private void toolbarClick() {
        ImageView feedback, logout;
        feedback = findViewById(R.id.toolbar_feedback);
        logout = findViewById(R.id.toolbar_logout);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadAgreementActivity.this, UserFeedBackActivity.class));
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

    /**
     * Launching camera app to capture image
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public void requestRuntimePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    /**
     * Launching camera app to record video
     */
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
        // name

        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    private void customTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                "I agree to the ");
        spanTxt.append("Privacy Policy");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {

                Toast.makeText(getApplicationContext(), "Terms of services",
                        Toast.LENGTH_SHORT).show();
            }
        }, spanTxt.length() - "Term of services".length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        spanTxt.append("And");
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    private void customTextViewAnother(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                "I agree to the ");
        spanTxt.append("Samsung Privacy Policy");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(UploadAgreementActivity.this, PrivacyPolicyActivity.class));
                Toast.makeText(getApplicationContext(), "Terms of services Clicked",
                        Toast.LENGTH_SHORT).show();
            }
        }, spanTxt.length() - "Samsung Privacy Policy".length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        spanTxt.append(" and ");
        spanTxt.append("KLE Tech Privacy Policy.");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(UploadAgreementActivity.this, KLEPrivacyPolicy.class));
            }
        }, spanTxt.length() - "KLE Tech Privacy Policy.".length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                launchUploadActivity(true);


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // video successfully recorded
                // launching upload activity
                launchUploadActivity(false);

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void launchUploadActivity(boolean isImage) {
        Intent i = new Intent(UploadAgreementActivity.this, UploadAgreementHelper.class);
        i.putExtra("filePath", fileUri.getPath());
        i.putExtra("isImage", isImage);
        startActivity(i);
        finish();
    }

    /**
     * ------------ Helper Methods ----------------------
     * <p>
     * /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        requestRuntimePermission();
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private void backButton() {
        ImageView backButton = findViewById(R.id.toolbar_image);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exit) {
                    finish();
                    moveTaskToBack(true);
                } else {
                    Toast.makeText(context, getString(R.string.press_back_to_exit),
                            Toast.LENGTH_SHORT).show();
                    exit = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exit = false;
                        }
                    }, 3 * 1000);

                }
            }
        });


    }

    private void floatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.floating_action_button);
        SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPrefsManager.saveBoolValue(SharedPrefsConstants.IS_FROM_LOGIN, false);
                startActivity(new Intent(UploadAgreementActivity.this, DownloadAgreementActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
            moveTaskToBack(true);
        } else {
            Toast.makeText(this, getString(R.string.press_back_to_exit),
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }

}