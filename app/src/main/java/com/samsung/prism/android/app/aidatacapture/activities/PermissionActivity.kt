package com.samsung.prism.android.app.aidatacapture.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.accountRelatedActivities.UserLoginActivity
import com.samsung.prism.android.app.aidatacapture.databinding.ActivityPermissionBinding

class PermissionActivity : AppCompatActivity() {

    private val REQUEST_DEFAULT_CALLER_ROLE: Int = 21
    private lateinit var permissionArray: MutableList<String>
    private val PERMISSION_REQUEST_CODE: Int = 25
    lateinit var binding: ActivityPermissionBinding
    var roleGranted = MutableLiveData<Boolean>()
    var allPermissionsGranted = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializePermissions()
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        if (isAllPermissionsGranted()) {
            startActivity(Intent(this, UserLoginActivity::class.java))
            finish()
        } else {
            binding = ActivityPermissionBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.allowPermission.setOnClickListener {
                requestPermissionRuntime()
            }

            allPermissionsGranted.observe(this, Observer {
                if (it == true) {
                    startMainActivity()
                }
            })

        }
    }

    private fun startMainActivity() {
        startActivity(
            Intent(
                this,
                UserLoginActivity::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()
    }


    private fun initializePermissions() {
        permissionArray = mutableListOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissionArray.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionArray.add(Manifest.permission.CAMERA)
        permissionArray.add(Manifest.permission.RECORD_AUDIO)
    }

    private fun isAllPermissionsGranted(): Boolean {
        var allPermsGranted = true
        for (i in permissionArray.indices) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permissionArray[i]
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                allPermsGranted = false
            }
        }
        return allPermsGranted
    }

    private fun requestPermissionRuntime() {
        requestPermissions(
            permissionArray.toTypedArray(),
            PERMISSION_REQUEST_CODE
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            var allPermissionGranted = true
            for (i in permissions.indices) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        permissions[i]
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d(
                        "PermissionActivity",
                        "onRequestPermissionsResult: Not Granted ${permissions[i]}"
                    )
                    allPermissionGranted = false
                }
            }
            if (allPermissionGranted) {
                this.allPermissionsGranted.value = true
                showRequestRole()
            } else {
                this.allPermissionsGranted.value = false
            }
            Log.d("PermissionActivity", "onRequestPermissionsResult: $allPermissionGranted")
        }
    }

    private fun showRequestRole() {
        binding.requestPermissionsLayout.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_DEFAULT_CALLER_ROLE) {
            if (resultCode == Activity.RESULT_OK) {
                roleGranted.value = true
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}