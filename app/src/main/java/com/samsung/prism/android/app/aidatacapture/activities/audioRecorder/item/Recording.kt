package com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.item

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.UserMenuActivity
import com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.AudioRecorderActivity
import com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.player.RecordingRepository
import com.samsung.prism.android.app.aidatacapture.activities.audioRecorder.player.RecordingsActivity
import com.samsung.prism.android.app.aidatacapture.activities.callbacks.FileDeleteCallback
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.repos.RetrofitHelper
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.recording_layout.view.*
import java.io.File

@Suppress("DEPRECATION")
class Recording(val title: String, val context: Context) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.recording_title_textview.text = title
        viewHolder.itemView.recording_image.setOnClickListener {
            RecordingRepository.playRecording(context, title)
        }
        viewHolder.itemView.upload.setOnClickListener {
            val filePath =
                Environment.getExternalStorageDirectory().absolutePath + Constants.COLLECT_SOUND_DIRECTORY + title
            RetrofitHelper().uploadAudioFileToServer(context, filePath)
        }
        viewHolder.itemView.delete.setOnClickListener {
            showDeleteConfirmationDialog(context)
        }
    }

    override fun getLayout(): Int {
        return R.layout.recording_layout
    }

    fun showDeleteConfirmationDialog(context: Context) {
        val activity = context as Activity
        val mDialog = MaterialDialog.Builder(activity)
            .setTitle("Delete Audio")
            .setMessage("Do you really want to delete?")
            .setCancelable(true)
            .setPositiveButton(
                context.getString(R.string.yes), R.drawable.ic_baseline_check_24
            ) { dialogInterface, which ->
                val filePath =
                    Environment.getExternalStorageDirectory().absolutePath + Constants.COLLECT_SOUND_DIRECTORY + title
                RetrofitHelper().delteFile(File(filePath), object : FileDeleteCallback {
                    override fun onDeleteSuccess(msg: String) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.file_deleted_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                        activity.startActivity(Intent(context,AudioRecorderActivity::class.java))
                        activity.finish()
                    }

                    override fun onDeleteFail(msg: String) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.file_delete_failed),
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                })
            }
            .setNegativeButton(
                context.getString(R.string.no), R.drawable.ic_baseline_cancel_24
            ) { dialogInterface, which ->
                dialogInterface.dismiss()
            }
            .build()

        // Show Dialog
        mDialog.show()
    }

}