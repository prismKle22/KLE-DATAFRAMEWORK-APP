package com.samsung.prism.android.app.aidatacapture.activities.callbacks;

public interface FileUploaderCallback {
    void onError();

    void onFinish(String[] responses);

    void onProgressUpdate(int currentpercent, int totalpercent, int filenumber);
}
