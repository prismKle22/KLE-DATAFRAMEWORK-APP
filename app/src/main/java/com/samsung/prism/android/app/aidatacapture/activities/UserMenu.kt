package com.samsung.prism.android.app.aidatacapture.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.metaData.MetaDataNonHumanCentricActivity
import com.samsung.prism.android.app.aidatacapture.activities.mySignConsent.MySignConsentActivity
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager

class UserMenu : AppCompatActivity() {
    private lateinit var nonHumanCentric: MaterialCardView
    private lateinit var humanCentric: MaterialCardView
    private var sharedPrefsManager: SharedPrefsManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_menu)
        sharedPrefsManager = SharedPrefsManager(this)
        if (sharedPrefsManager!!.getStringValue(
                SharedPrefsConstants.SUBCATEGORY_NAME,
                null
            ) == Constants.PEOPLE_CENTRIC
        ) {
//            SharedPrefsConstants.IS_FROM_METADATA
            sharedPrefsManager!!.saveStringValue(SharedPrefsConstants.SUBCATEGORY_NAME, "People Centric")
            sharedPrefsManager!!.saveStringValue(SharedPrefsConstants.IMAGE_TYPE, "Human Centric")
            startActivity(Intent(this@UserMenu, SignConsentActivity::class.java))
            finish()
        }
        else{
            sharedPrefsManager!!.saveStringValue(SharedPrefsConstants.SUBCATEGORY_NAME, "Non People Centric")
            sharedPrefsManager!!.saveStringValue(SharedPrefsConstants.IMAGE_TYPE, "Non Human Centric")
            startActivity(Intent(this@UserMenu, MetaDataNonHumanCentricActivity::class.java))
            finish()
        }

//        } else {
//            startActivity(Intent(this@UserMenu, MetaDataNonHumanCentricActivity::class.java))
//            sharedPrefsManager!!.saveStringValue(
//                SharedPrefsConstants.IMAGE_TYPE,
//                "Non Human Centric"
//            )
//            sharedPrefsManager!!.saveStringValue(SharedPrefsConstants.HUMAN_AGREEMENT_NAME, "Null")
//            finish()
//        }
    }

    private fun init() {
        nonHumanCentric = findViewById(R.id.non_human_centric)
        nonHumanCentric.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@UserMenu, MetaDataNonHumanCentricActivity::class.java))
            finish()
            sharedPrefsManager!!.saveStringValue(
                SharedPrefsConstants.IMAGE_TYPE,
                "Non Human Centric"
            )
        })

//        humanCentric.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(UserMenu.this, UploadHumanCentricAgreementActivity.class));
//                sharedPrefsManager.saveStringValue(SharedPrefsConstants.IMAGE_TYPE, "Human Centric");
//                finish();
//            }
//        });
    }

    private fun initToolbar() {
        val toolbarName: TextView
        toolbarName = findViewById(R.id.toolbar_name)
        toolbarName.text = "Select type"
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(this@UserMenu, UserCategoryActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}