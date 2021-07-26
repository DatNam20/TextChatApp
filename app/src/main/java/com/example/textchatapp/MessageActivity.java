package com.example.textchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.textchatapp.Adapters.MessageAdapter;
import com.example.textchatapp.Model.Chat;
import com.example.textchatapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {


    CircleImageView profileImageView;
    ImageButton sendMessageButton;
    TextView usernameText;
    EditText inputText;
    Intent intent;
    String userID;
    Toolbar toolbar_message;

    MessageAdapter messageAdapter;
    List<Chat> mChats;
    RecyclerView recyclerView;

    FirebaseUser firebaseUser;
    DatabaseReference dbReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        profileImageView = findViewById(R.id.profileImage_message);
        sendMessageButton = findViewById(R.id.sendMessage_imageButton);
        usernameText = findViewById(R.id.usernameText_message);
        inputText = findViewById(R.id.inputText_message);
        intent = getIntent();
        userID = intent.getStringExtra("userID");


        recyclerView = findViewById(R.id.recyclerView_message);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        toolbar_message = findViewById(R.id.toolbar_message);
        setSupportActionBar(toolbar_message);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar_message.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String inputMessage = inputText.getText().toString();

                if ("".equals(inputMessage))
                    Toast.makeText(MessageActivity.this, "Empty Message", Toast.LENGTH_SHORT).show();
                else
                    sendMessage(firebaseUser.getUid(), userID, inputMessage);

                inputText.setText("");
            }
        });


        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                assert user != null;
                usernameText.setText(user.getUsername());

                if (user.getImageURL().equals("default"))
                    profileImageView.setImageResource(R.drawable.default_profile_image);
                else
                    Glide.with(MessageActivity.this).load(user.getImageURL()).into(profileImageView);

                readMessage(firebaseUser.getUid(), userID, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendMessage(String senderID, String receiverID, String message) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("senderID", senderID);
        hashMap.put("receiverID", receiverID);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);
    }

    private void readMessage(String myUserID, String otherUserID, String imageURL){

        mChats = new ArrayList<>();

        dbReference = FirebaseDatabase.getInstance().getReference().child("Chats");
        dbReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mChats.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                        Chat chat = snapshot.getValue(Chat.class);

                        if ( (chat.getReceiverID().equals(myUserID) && chat.getSenderID().equals(otherUserID))
                                || (chat.getSenderID().equals(myUserID) && chat.getReceiverID().equals(otherUserID)) )
                            mChats.add(chat);

                    messageAdapter = new MessageAdapter(MessageActivity.this, mChats, imageURL);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}