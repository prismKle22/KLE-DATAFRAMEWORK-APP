package com.samsung.prism.android.app.aidatacapture.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.samsung.prism.android.app.aidatacapture.R;
import com.samsung.prism.android.app.aidatacapture.activities.adminRelatedActivities.ApproveUserProfile;
import com.samsung.prism.android.app.aidatacapture.constants.SharedPrefsConstants;
import com.samsung.prism.android.app.aidatacapture.models.ModelApproveUser;
import com.samsung.prism.android.app.aidatacapture.repos.SharedPrefsManager;

import java.util.List;


public class ApproveUserAdapter extends RecyclerView.Adapter<ApproveUserAdapter.UsersViewHolder> {

    private Context context;
    private List<ModelApproveUser> userListResponseData;
    private boolean agreementUploadStatus;
    private SharedPrefsManager sharedPrefsManager;

    public ApproveUserAdapter(Context context, List<ModelApproveUser> userListResponseData) {
        this.userListResponseData = userListResponseData;
        this.context = context;
        sharedPrefsManager = new SharedPrefsManager(context);
    }

    @Override
    public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_list_items_approve, null);
        UsersViewHolder usersViewHolder = new UsersViewHolder(view);
        return usersViewHolder;
    }

    @Override
    public void onBindViewHolder(UsersViewHolder holder, final int position) {
        // set the data
        holder.name.setText(userListResponseData.get(position).getFullName());
        holder.email.setText(userListResponseData.get(position).getEmail());
        if (userListResponseData.get(position).getAgreementStatus().equals("true")) {
            holder.approve.setText("Agreement Upload Status: Completed");
            agreementUploadStatus = true;
        } else {
            holder.approve.setText("Agreement Upload Status: Incomplete");
            agreementUploadStatus = false;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = userListResponseData.get(position).getEmail();
                sharedPrefsManager.saveStringValue(SharedPrefsConstants.APPROVE_USER_EMAIL, userEmail);
                sharedPrefsManager.saveStringValue(SharedPrefsConstants.USER_FULL_NAME, userListResponseData.get(position).getFullName());
                sharedPrefsManager.saveStringValue(SharedPrefsConstants.AGREEMENT_URL, userListResponseData.get(position).getAgreementUrl());
                context.startActivity(new Intent(context, ApproveUserProfile.class));
            }
        });
    }

    public void updateList(List<ModelApproveUser> list) {
        userListResponseData = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userListResponseData.size(); // size of the list items
    }

    class UsersViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView name, email, approve;
        ImageView approved;

        public UsersViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.name);
            email = (TextView) itemView.findViewById(R.id.email);
            approve = itemView.findViewById(R.id.approve);
            approved = itemView.findViewById(R.id.approved);

        }
    }
}