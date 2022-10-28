package com.samsung.prism.android.app.aidatacapture.activities.adminRelatedActivities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.samsung.prism.android.app.aidatacapture.R
import com.samsung.prism.android.app.aidatacapture.adapters.ApproveUserAdapter
import com.samsung.prism.android.app.aidatacapture.constants.Constants
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.models.ModelApproveUser
import com.samsung.prism.android.app.aidatacapture.others.apiClients.ApproveUsersAPIClient.client
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AdminApproveUsers : AppCompatActivity(), OnRefreshListener {
    var recyclerView: RecyclerView? = null
    var userListResponseData: List<ModelApproveUser>? = null
    lateinit var searcBarLayout: TextInputLayout
    lateinit var searchInputEditText: TextInputEditText
    var usersAdapter: ApproveUserAdapter? = null
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private var context: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_view_users)
        context = this
        toolbarClick()
        backButton()
        recyclerView = findViewById(R.id.recyclerView)
        try {
            userListData
            // call a method in which we have implement our GET type web API
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.all_users_verfied), Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@AdminApproveUsers, AdminDashboard::class.java))
            finish()
        }
        searcBarLayout = findViewById(R.id.search_bar_layout)
        searchInputEditText = findViewById(R.id.search_bar_input)
        searchInputEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
            }
        })
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        mSwipeRefreshLayout.setOnRefreshListener(this)
    }

    override fun onResume() {
        super.onResume()
        val call = client!!.usersList
        call.enqueue(object : Callback<List<ModelApproveUser?>?> {
            override fun onResponse(
                call: Call<List<ModelApproveUser?>?>,
                response: Response<List<ModelApproveUser?>?>
            ) {
                Log.d(Constants.TAG, response.toString())
                if (response.isSuccessful) {
                    Log.d(Constants.TAG, getString(R.string.response_successfull))
                } else {
                    Log.d(Constants.TAG, "Failed")
                }
            }

            override fun onFailure(call: Call<List<ModelApproveUser?>?>, t: Throwable) {}
        })
    }

    override fun onRefresh() {
        Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show()
        client!!.usersList.enqueue(object : Callback<List<ModelApproveUser>> {
            override fun onResponse(
                call: Call<List<ModelApproveUser>>,
                response: Response<List<ModelApproveUser>>
            ) {
                try {
                    Log.d("responseGET", response.body()!![0].fullName!!)
                    userListResponseData = response.body()
                    setDataInRecyclerView()
                } catch (e: Exception) {
                    Toast.makeText(
                        this@AdminApproveUsers,
                        getString(R.string.no_users_found),
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@AdminApproveUsers, AdminDashboard::class.java))
                    finish()
                }
            }

            override fun onFailure(call: Call<List<ModelApproveUser>>, t: Throwable) {
                // if error occurs in network transaction then we can get the error in this method.
                Toast.makeText(this@AdminApproveUsers, t.toString(), Toast.LENGTH_LONG).show()
            }
        })
        Handler().postDelayed({ mSwipeRefreshLayout!!.isRefreshing = false }, 2000)
    }//dismiss progress dialog//dismiss progress dialog

    // display a progress dialog
    private val userListData: Unit // set cancelable to false // set message
        // show progress dialog
        private get() {
            // display a progress dialog
            val progressDialog = ProgressDialog(this@AdminApproveUsers)
            progressDialog.setCancelable(false) // set cancelable to false
            progressDialog.setMessage(getString(R.string.please_wait_text)) // set message
            progressDialog.show() // show progress dialog
            client!!.usersList.enqueue(object : Callback<List<ModelApproveUser>> {
                override fun onResponse(
                    call: Call<List<ModelApproveUser>>,
                    response: Response<List<ModelApproveUser>>
                ) {
                    try {
                        Log.d("responseGET", response.body()!![0].fullName!!)
                        progressDialog.dismiss() //dismiss progress dialog
                        userListResponseData = response.body()
                        setDataInRecyclerView()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@AdminApproveUsers,
                            getString(R.string.no_users_found),
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@AdminApproveUsers, AdminDashboard::class.java))
                        finish()
                    }
                }

                override fun onFailure(call: Call<List<ModelApproveUser>>, t: Throwable) {
                    Toast.makeText(this@AdminApproveUsers, t.toString(), Toast.LENGTH_LONG).show()
                    progressDialog.dismiss() //dismiss progress dialog
                }
            })
        }

    private fun setDataInRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this@AdminApproveUsers)
        recyclerView!!.layoutManager = linearLayoutManager
        usersAdapter = ApproveUserAdapter(this@AdminApproveUsers, userListResponseData)
        recyclerView!!.adapter = usersAdapter
    }

    fun filter(text: String) {
        val temp: MutableList<ModelApproveUser?> = ArrayList<ModelApproveUser?>()
        for (d in userListResponseData!!) {
            if (d.fullName!!.toLowerCase().contains(text.toLowerCase())) {
                temp.add(d)
            }
        }
        usersAdapter!!.updateList(temp)
    }

    private fun toolbarClick() {
        val feedback: ImageView
        val logout: ImageView
        feedback = findViewById(R.id.toolbar_feedback)
        feedback.visibility = View.GONE
        logout = findViewById(R.id.toolbar_logout)
        logout.setOnClickListener {
            val dialogue = Dialogues()
            dialogue.logoutDialogue(context!!)
        }
        feedback.setOnLongClickListener {
            Toast.makeText(context, R.string.feedback, Toast.LENGTH_SHORT).show()
            true
        }
        logout.setOnLongClickListener {
            Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show()
            false
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@AdminApproveUsers, AdminDashboard::class.java))
        finish()
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(this@AdminApproveUsers, AdminDashboard::class.java))
            finish()
        }
    }
}