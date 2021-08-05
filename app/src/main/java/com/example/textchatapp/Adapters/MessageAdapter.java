package com.example.textchatapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.textchatapp.Model.Chat;
import com.example.textchatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    private static final int MESSAGE_SENT = 0;
    private static final int MESSAGE_RECEIVED = 1;

    private Context mContext;
    private List<Chat> mChats;
    private String imageURL;

    FirebaseUser firebaseUser;


    public MessageAdapter(Context mContext, List<Chat> mChats, String imageURL) {
        this.mContext = mContext;
        this.mChats = mChats;
        this.imageURL = imageURL;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public ImageView profile_image;
        public TextView seenIndicatorText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.message_text);
            profile_image = itemView.findViewById(R.id.profile_image);
            seenIndicatorText = itemView.findViewById(R.id.delivered_seen_text);
        }
    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MESSAGE_SENT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_message_sent, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_message_received, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChats.get(position);
        holder.messageText.setText(chat.getMessage());

        if(imageURL.equals("default"))
            holder.profile_image.setImageResource(R.drawable.default_profile_image);
        else
            Glide.with(mContext).load(imageURL).into(holder.profile_image);

        if(position == (mChats.size()-1) )
        {
            if(chat.isIsseen())
                holder.seenIndicatorText.setText("Seen");
            else
                holder.seenIndicatorText.setText("Delivered");
        }
        else
            holder.seenIndicatorText.setVisibility(View.GONE);
        
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    @Override
    public int getItemViewType(int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if( mChats.get(position).getSenderID().equals(firebaseUser.getUid()) )
            return MESSAGE_SENT;
        else
            return MESSAGE_RECEIVED;

    }



}
