package com.samsung.prism.android.app.aidatacapture.dialogues

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.databinding.LoadingDailougeBinding

class ProgressDialog(context: Context,val message:String) : Dialog(context, R.style.FullScreenTransparentDialog) {

    lateinit var binding  : LoadingDailougeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoadingDailougeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.message.setText(message)
        setCancelable(false)
    }
}