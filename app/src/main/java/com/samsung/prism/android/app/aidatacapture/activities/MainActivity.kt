package com.samsung.prism.android.app.aidatacapture.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.samsung.prism.android.app.aidatacapture.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}