package  com.samsung.prism.android.app.aidatacapture.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.activities.metaData.MetaDataWordImageActivity
import com.samsung.prism.android.app.aidatacapture.databinding.ActivityUserMenu2Binding
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues

class UserMenuActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserMenu2Binding
    private lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserMenu2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        context = this
        toolbarClick()
        onClickListeners()
        backButton()
    }

    private fun onClickListeners() {
        binding.imageOrVideoUpload.setOnClickListener {
            startActivity(Intent(Activity@ this, UserCategoryActivity::class.java))
            finish()
        }
//        binding.imageLabeling.setOnClickListener {
//            startActivity(Intent(Activity@ this, MetaDataWordImageActivity::class.java))
//            finish()
//        }
        binding.audioUpload.setOnClickListener {
            startActivity(Intent(Activity@ this, AudioMenuActivity::class.java))
            finish()
        }
        binding.textUpload.setOnClickListener {
            startActivity(Intent(Activity@ this, SuperMetaDataText::class.java))
            finish()
        }
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(Activity@ this, UserDashboard::class.java))
            finish()
        }
    }

    private fun toolbarClick() {
        val feedback: ImageView = findViewById(R.id.toolbar_feedback)
        feedback.visibility = View.GONE
        feedback.visibility = View.GONE
        val logout: ImageView = findViewById(R.id.toolbar_logout)
        logout.setOnClickListener {
            val dialogue = Dialogues()
            dialogue.logoutDialogue(context)
        }
        feedback.setOnLongClickListener {
            Toast.makeText(context, getString(R.string.feedback_msg), Toast.LENGTH_SHORT).show()
            true
        }
        logout.setOnLongClickListener {
            Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show()
            false
        }
    }


    override fun onBackPressed() {
        startActivity(Intent(Activity@ this, UserDashboard::class.java))
        finish()
    }

}