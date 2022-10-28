package com.samsung.prism.android.app.aidatacapture.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.samsung.prism.android.app.aidatacapture.R;
import com.samsung.prism.android.app.aidatacapture.activities.UserDashboard;
import com.samsung.prism.android.app.aidatacapture.activities.UserDetails;
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants;
import com.samsung.prism.android.app.aidatacapture.models.UserDetailsModel;
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager;

import java.util.List;


public class UserDetailsAdapter extends RecyclerView.Adapter<UserDetailsAdapter.UsersViewHolder> {

    Context context;
    List<UserDetailsModel> userListResponseData;
    private SharedPrefsManager sharedPrefsManager;

    public UserDetailsAdapter(Context context, List<UserDetailsModel> userListResponseData) {
        this.userListResponseData = userListResponseData;
        this.context = context;
        sharedPrefsManager = new SharedPrefsManager(context);
    }

    @Override
    public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_list_items, null);
        UsersViewHolder usersViewHolder = new UsersViewHolder(view);
        return usersViewHolder;
    }

    @Override
    public void onBindViewHolder(UsersViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        // set the data
        holder.name.setText(userListResponseData.get(position).getUserFullName());
        holder.email.setText(userListResponseData.get(position).getUserEmail());
        // implement setONCLickListtener on itemView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // display a toast with user name
                sharedPrefsManager.saveBoolValue(SharedPrefsConstants.IS_IT_USER_PROFILE,true);
                sharedPrefsManager.saveStringValue(SharedPrefsConstants.USER_EMAIL, userListResponseData.get(position).getUserEmail());
                sharedPrefsManager.saveStringValue(SharedPrefsConstants.USER_FULL_NAME, userListResponseData.get(position).getUserFullName());
                sharedPrefsManager.saveStringValue(SharedPrefsConstants.USER_MOBILE,userListResponseData.get(position).getUserMobileNumber());
                context.startActivity(new Intent(context, UserDashboard.class));
            }
        });
    }

    public void updateList(List<UserDetailsModel> list) {
        userListResponseData = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userListResponseData.size(); // size of the list items
    }

    class UsersViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView name, email;

        public UsersViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.name);
            email = (TextView) itemView.findViewById(R.id.email);
        }
    }
}