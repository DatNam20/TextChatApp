package com.example.textchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.textchatapp.Adapters.MessageAdapter;
import com.example.textchatapp.Fragments.APIService;
import com.example.textchatapp.Model.Chat;
import com.example.textchatapp.Model.User;
import com.example.textchatapp.Notifications.Client;
import com.example.textchatapp.Notifications.Data;
import com.example.textchatapp.Notifications.MessagingService;
import com.example.textchatapp.Notifications.RetrofitResponse;
import com.example.textchatapp.Notifications.Sender;
import com.example.textchatapp.Notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MessageActivity extends AppCompatActivity {

    private static final String TAG = "logTAG";


    CircleImageView profileImageView;
    ImageButton sendMessageButton;
    TextView usernameText;
    EditText inputText;
    Intent intent;
    Toolbar toolbar_message;
    String userID;

    MessageAdapter messageAdapter;
    List<Chat> mChats;
    RecyclerView recyclerView;

    FirebaseUser firebaseUser;
    DatabaseReference dbReference;

    ValueEventListener messageSeenListener;

    APIService apiService;
    boolean toNotify = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

//        startService(new Intent(this, MessagingService.class));

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
                startActivity(new Intent(MessageActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        apiService = Client.getClient().create(APIService.class);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String inputMessage = inputText.getText().toString();

                if ("".equals(inputMessage))
                    Toast.makeText(MessageActivity.this, "Empty Message", Toast.LENGTH_SHORT).show();
                else {
                    toNotify = true;
                    sendMessage(firebaseUser.getUid(), userID, inputMessage);
                }

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
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profileImageView);

                readMessage(firebaseUser.getUid(), userID, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        checkMessageSeen(userID);
    }


    private void checkMessageSeen(String userID) {
        dbReference = FirebaseDatabase.getInstance().getReference("Chats");

        messageSeenListener = dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if (chat.getReceiverID().equals(firebaseUser.getUid()) && chat.getSenderID().equals(userID))
                    {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }

                }
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
        hashMap.put("isseen", false);

        reference.child("Chats").push().setValue(hashMap);


        final String notificationMessage = message;

        DatabaseReference idReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        idReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                User user = dataSnapshot.getValue(User.class);
                if (toNotify) {
                    Log.i(TAG, "    MessageActivity  -->  sendMessage \n receiver id : " + receiverID
                                        + "\n user name : "+user.getUsername() + "\n message : " + notificationMessage + "\n  ");
                    sendNotification(receiverID, user.getUsername(), notificationMessage);
                    toNotify = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void sendNotification(String receiverID, String username, String notificationMessage) {

        DatabaseReference tokenReference = FirebaseDatabase.getInstance().getReference("Token");

        Query query = tokenReference.orderByKey().equalTo(receiverID);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Token token = snapshot.getValue(Token.class);

                    Data data = new Data ( userID, username+": "+notificationMessage,
                                "TextChatApp New Message", firebaseUser.getUid(), R.mipmap.ic_launcher );

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                        .enqueue(new Callback<RetrofitResponse>()
                        {
                            @Override
                            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> retrofitResponse) {
                                if (retrofitResponse.code() == 200){
                                    if (retrofitResponse.body().success != 1){
                                        Toast.makeText(MessageActivity.this, "MessageActivity_onResponse_Failed!", Toast.LENGTH_LONG).show();
                                    }
                                }
                                Log.i(TAG, "    MessageActivity  -->  (sendNotification) " +
                                                    "\n response.code _ "+retrofitResponse.code()
                                                    + " \n response.success _ "+retrofitResponse.body().success);
                            }

                            @Override
                            public void onFailure(Call<RetrofitResponse> call, Throwable t) {

                            }

                        });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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


    public void switchActivityStatus (String activity) {

        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> map = new HashMap<>();
        map.put("activity", activity);

        dbReference.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        switchActivityStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        dbReference.removeEventListener(messageSeenListener);
        switchActivityStatus("offline");
    }


}