package com.example.textchatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.textchatapp.MessageActivity;
import com.example.textchatapp.Model.User;
import com.example.textchatapp.R;

import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private static final String TAG = "logTAG";


    private Context mContext;
    private List<User> mUsers;
    private boolean isChat;


    public UserAdapter(Context mContext, List<User> mUsers, boolean isChat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView username;
        private ImageView profile_image;
        private ImageView activityStatus;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.usernameText_eachUser);
            profile_image = itemView.findViewById(R.id.profileImage_eachUser);
            activityStatus = itemView.findViewById(R.id.activityStatus_online);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_each_user, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user = mUsers.get(position);
        holder.username.setText(user.getUsername());

        if (user.getImageURL().equals("default"))
            holder.profile_image.setImageResource(R.drawable.default_profile_image);
        else
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);


        if (isChat == true && user.getActivityStatus() == "online")
            holder.activityStatus.setVisibility(View.VISIBLE);
        else
            holder.activityStatus.setVisibility(View.GONE);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent( mContext, MessageActivity.class );
                intent.putExtra("userID", user.getId());
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


}
