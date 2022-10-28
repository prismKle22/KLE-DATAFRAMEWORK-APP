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
import com.samsung.prism.android.app.aidatacapture.adapters.UserDetailsAdapter
import com.samsung.prism.android.app.aidatacapture.dialogues.Dialogues
import com.samsung.prism.android.app.aidatacapture.models.UserDetailsModel
import com.samsung.prism.android.app.aidatacapture.others.apiClients.UserDetailsApiClient.client
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AdminViewUsers : AppCompatActivity(), OnRefreshListener {
    var recyclerView: RecyclerView? = null
    var userListResponseData: List<UserDetailsModel>? = null
    lateinit var searcBarLayout: TextInputLayout
    lateinit var searchInputEditText: TextInputEditText
    var usersAdapter: UserDetailsAdapter? = null
    var context: Context? = null
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_view_users)
        context = this
        toolbarClick()
        backButton()
        recyclerView = findViewById(R.id.recyclerView)
        userListData // call a method in which we have implement our GET type web API
        searcBarLayout = findViewById(R.id.search_bar_layout)
        searchInputEditText = findViewById(R.id.search_bar_input)
        searchInputEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    filter(s.toString())
                }catch (e:Exception){
                    Log.d("Exception", "onTextChanged: "+e.localizedMessage)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {

                // filter your list from your input
                try {
                    filter(s.toString())
                }catch (e:Exception){
                    Log.d("Exception", "onTextChanged: "+e.localizedMessage)
                }
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        })
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        mSwipeRefreshLayout.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ mSwipeRefreshLayout!!.isRefreshing = false }, 2000)
    }// if error occurs in network transaction then we can get the error in this method.

    //dismiss progress dialog
//dismiss progress dialog
    // display a progress dialog
    private val userListData: Unit // set cancelable to false // set message
        // show progress dialog
        private get() {
            // display a progress dialog
            val progressDialog = ProgressDialog(this@AdminViewUsers)
            progressDialog.setCancelable(false) // set cancelable to false
            progressDialog.setMessage(getString(R.string.please_wait_text)) // set message
            progressDialog.show() // show progress dialog
            client!!.usersList.enqueue(object : Callback<List<UserDetailsModel>> {
                override fun onResponse(
                    call: Call<List<UserDetailsModel>>,
                    response: Response<List<UserDetailsModel>>
                ) {
                    Log.d("responseGET", response.body()!![0].userFullName!!)
                    progressDialog.dismiss() //dismiss progress dialog
                    userListResponseData = response.body()
                    setDataInRecyclerView()
                }

                override fun onFailure(call: Call<List<UserDetailsModel>>, t: Throwable) {
                    // if error occurs in network transaction then we can get the error in this method.
                    Toast.makeText(this@AdminViewUsers, t.toString(), Toast.LENGTH_LONG).show()
                    progressDialog.dismiss() //dismiss progress dialog
                }
            })
        }

    private fun setDataInRecyclerView() {
        // set a LinearLayoutManager with default vertical orientation
        val linearLayoutManager = LinearLayoutManager(this@AdminViewUsers)
        recyclerView!!.layoutManager = linearLayoutManager
        // call the constructor of UsersAdapter to send the reference and data to Adapter
        usersAdapter = UserDetailsAdapter(this@AdminViewUsers, userListResponseData)
        recyclerView!!.adapter = usersAdapter // set the Adapter to RecyclerView
    }

    fun filter(text: String) {
        val temp: MutableList<UserDetailsModel?> = ArrayList<UserDetailsModel?>()
        for (d in userListResponseData!!) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.userFullName!!.toLowerCase().contains(text.toLowerCase())) {
                temp.add(d)
            }
        }
        //update recyclerview
        usersAdapter!!.updateList(temp)
    }

    private fun toolbarClick() {
        val feedback: ImageView
        val logout: ImageView
        feedback = findViewById(R.id.toolbar_feedback)
        feedback.visibility = View.GONE
        feedback.visibility = View.GONE
        logout = findViewById(R.id.toolbar_logout)
        logout.setOnClickListener {
            val dialogue = Dialogues()
            dialogue.logoutDialogue(context!!)
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
        startActivity(Intent(this@AdminViewUsers, AdminDashboard::class.java))
        finish()
    }

    private fun backButton() {
        val backButton = findViewById<ImageView>(R.id.toolbar_image)
        backButton.setOnClickListener {
            startActivity(Intent(this@AdminViewUsers, AdminDashboard::class.java))
            finish()
        }
    }
}