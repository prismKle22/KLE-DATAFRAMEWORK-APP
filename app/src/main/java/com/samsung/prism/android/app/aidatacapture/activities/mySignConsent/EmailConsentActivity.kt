package com.samsung.prism.android.app.aidatacapture.activities.mySignConsent

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.metaData.MetaDataHumanCentricActivity
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues

class EmailConsentActivity : AppCompatActivity() {
    private var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_consent)

        val fn=intent.getStringExtra("name").toString()
        val em = intent.getStringExtra("email").toString()
//        Toast.makeText(this,("https://sendpdfmail.herokuapp.com/"+fn+"/"+em),Toast.LENGTH_LONG).show()
//        Toast.makeText(this,"Consent sent to your mail", Toast.LENGTH_LONG).show()
        val webv=findViewById<WebView>(R.id.wb)
        webv.loadUrl("https://sendpdfmail.herokuapp.com/"+fn+"/"+em)
        context = this
        val dialogues = Dialogues()
        dialogues.showUploadSuccessDialog(context!!)
    }
}