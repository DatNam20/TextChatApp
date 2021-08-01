package com.example.textchatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.textchatapp.MessageActivity;
import com.example.textchatapp.Model.Chat;
import com.example.textchatapp.Model.User;
import com.example.textchatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private static final String TAG = "logTAG";


    private Context mContext;
    private List<User> mUsers;
    private boolean isChat;
    String lastMessageValue;

    FirebaseUser firebaseUser;
    DatabaseReference dbReference;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isChat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView username;
        private ImageView profile_image;
        private ImageView activityStatus;
        private TextView lastMessageText;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.usernameText_eachUser);
            profile_image = itemView.findViewById(R.id.profileImage_eachUser);
            activityStatus = itemView.findViewById(R.id.activityStatus_online);
            lastMessageText = itemView.findViewById(R.id.lastMessageText_eachUser);
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
        assert user != null;
        holder.username.setText(user.getUsername());

        if (user.getImageURL().equals("default"))
            holder.profile_image.setImageResource(R.drawable.default_profile_image);
        else
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);


        if (isChat == true && user.getActivityStatus() == "online") {
            holder.activityStatus.setVisibility(View.VISIBLE);
//            Log.i(TAG, "\t user "+user.getUsername()+" online ");
        }
        else {
            holder.activityStatus.setVisibility(View.GONE);
//            Log.i(TAG, "\t holder _ "+holder.username.getText()+" "+user.getActivityStatus());
        }


        if (isChat == true)
            lastMessage(user.getId(), holder.lastMessageText);
        else {
            holder.lastMessageText.setVisibility(View.GONE);
            holder.username.setGravity(Gravity.CENTER);
        }


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

    public void lastMessage(String userID, TextView lastMessageText) {

        lastMessageValue = "";

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference("Chats");

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiverID().equals(firebaseUser.getUid()) && chat.getSenderID().equals(userID) ||
                            chat.getReceiverID().equals(userID) && chat.getSenderID().equals(firebaseUser.getUid()) )
                        lastMessageValue = chat.getMessage();
                }

                if(lastMessageValue.equals(""))
                    lastMessageText.setVisibility(View.GONE);
                else
                    lastMessageText.setText(lastMessageValue);

                lastMessageValue = "";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
