package com.samsung.prism.android.app.aidatacapture.dialogues

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.SignConsentActivity
import com.samsung.prism.android.app.aidatacapture.activities.UserCategoryActivity
import com.samsung.prism.android.app.aidatacapture.activities.UserFeedBackActivity
import com.samsung.prism.android.app.aidatacapture.activities.UserMenuActivity
import com.samsung.prism.android.app.aidatacapture.activities.accountRelatedActivities.UserLoginActivity
import com.samsung.prism.android.app.aidatacapture.activities.metaData.MetaDataHumanCentricActivity
import com.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.android.synthetic.main.activity_user_registration.*

class Dialogues {

    fun logoutDialogue(context: Context) {
        val activity = context as Activity
        val mDialog = MaterialDialog.Builder(activity)
            .setTitle(context.getString(R.string.logoutString))
            .setMessage(context.getString(R.string.do_you_want_to_logout))
            .setCancelable(false)
            .setPositiveButton(
                context.getString(R.string.yes), R.drawable.ic_baseline_check_24
            ) { dialogInterface, which ->
                Toast.makeText(context, "Logging out", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, UserLoginActivity::class.java))
                activity.finish()
            }
            .setNegativeButton(
                context.getString(R.string.no), R.drawable.ic_baseline_cancel_24
            ) { dialogInterface, which -> dialogInterface.dismiss() }
            .build()

        // Show Dialog
        mDialog.show()
    }

    fun showSuccessDialog(context: Context) {
        val activity = context as Activity
        val mDialog = MaterialDialog.Builder(activity)
            .setTitle(context.getString(R.string.upload_successful))
            .setMessage(context.getString(R.string.do_you_want_to_upload_more))
            .setCancelable(false)
            .setPositiveButton(
                context.getString(R.string.yes), R.drawable.ic_baseline_check_24
            ) { dialogInterface, which ->
                context.startActivity(Intent(context, UserMenuActivity::class.java))
                activity.finish()
            }
            .setNegativeButton(
                context.getString(R.string.no), R.drawable.ic_baseline_cancel_24
            ) { dialogInterface, which ->
                context.startActivity(Intent(context, UserFeedBackActivity::class.java))
                activity.finish()
            }
            .build()

        // Show Dialog
        mDialog.show()
    }


    fun showUploadSuccessDialog(context: Context) {
        val activity = context as Activity
        val mDialog = MaterialDialog.Builder(activity)
                .setTitle(context.getString(R.string.mail_has_been_sent))
                .setMessage(context.getString(R.string.do_you_want_to_upload_more_consent))
                .setCancelable(false)
                .setPositiveButton(
                        context.getString(R.string.yes), R.drawable.ic_baseline_check_24
                ) { dialogInterface, which ->
                    context.startActivity(Intent(context, SignConsentActivity::class.java))
                    activity.finish()
                }
                .setNegativeButton(
                        context.getString(R.string.no), R.drawable.ic_baseline_cancel_24
                ) { dialogInterface, which ->
                    context.startActivity(Intent(context, MetaDataHumanCentricActivity::class.java))
                    activity.finish()
                }
                .build()

        // Show Dialog
        mDialog.show()
    }

    fun showExitDialog(context: Context) {
        val activity = context as Activity
        val mDialog = MaterialDialog.Builder(activity)
            .setTitle(context.getString(R.string.exit))
            .setMessage(context.getString(R.string.do_you_want_to_exit))
            .setCancelable(false)
            .setPositiveButton(
                context.getString(R.string.yes), R.drawable.ic_baseline_check_24
            ) { dialogInterface, which ->
                Toast.makeText(context, "Thank you!", Toast.LENGTH_SHORT).show()
                activity.moveTaskToBack(true)
                activity.finish()
            }
            .setNegativeButton(
                context.getString(R.string.no), R.drawable.ic_baseline_cancel_24
            ) { dialogInterface, which ->
                context.startActivity(Intent(context, UserMenuActivity::class.java))
                activity.finish()
            }
            .build()

        // Show Dialog
        mDialog.show()
    }

}