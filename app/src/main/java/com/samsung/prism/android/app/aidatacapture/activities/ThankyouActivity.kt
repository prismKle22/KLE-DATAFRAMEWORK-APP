package com.samsung.prism.android.app.aidatacapture

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.samsung.prism.android.app.aidatacapture.activities.SignConsentActivity
import com.samsung.prism.android.app.aidatacapture.activities.mySignConsent.MySignConsentActivity

class ThankyouActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thankyou)
        val back = findViewById<ImageView>(R.id.toolbar_image)
        back.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@ThankyouActivity,SignConsentActivity::class.java))
        })
    }
}