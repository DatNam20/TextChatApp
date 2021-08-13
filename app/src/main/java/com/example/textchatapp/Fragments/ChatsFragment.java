package com.example.textchatapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.textchatapp.Adapters.UserAdapter;
import com.example.textchatapp.Model.Chat;
import com.example.textchatapp.Model.User;
import com.example.textchatapp.Notifications.MessagingService;
import com.example.textchatapp.Notifications.Token;
import com.example.textchatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class ChatsFragment extends Fragment {

    private static final String TAG = "logTAG";


    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    private List<String> usersList;
// using HashSet to prevent Duplicate Data
    private HashSet<User> mUsersSet;
    private HashSet<String> usersSet;
/*
        // to sort arraylist alphabetically while ignoring the 'case'

        Collections.sort(myList, new Comparator<String>() {
		    @Override
		    public int compare(String s1, String s2) {
		        return s1.compareToIgnoreCase(s2);
		    }
		});
 */

    FirebaseUser firebaseUser;
    DatabaseReference dbReference;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        recyclerView = view.findViewById(R.id.recyclerView_chatsFragment);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        usersList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference("Chats");

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usersList.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    Chat chat = snapshot.getValue(Chat.class);

                    assert chat != null;
                    if ( chat.getSenderID().equals(firebaseUser.getUid()) )
                        usersList.add(chat.getReceiverID());
                    if ( chat.getReceiverID().equals(firebaseUser.getUid()) )
                        usersList.add(chat.getSenderID());
                }
// using HashSet to prevent Duplicate Data
                usersSet = new HashSet<>(usersList);
                usersList.removeAll(usersList);
                usersList.addAll(usersSet);
                readChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        updateToken(FirebaseMessaging.getInstance().getToken().toString());

        return view;
    }


    private void updateToken(String newToken){
        dbReference = FirebaseDatabase.getInstance().getReference("Token");

        Token updatedToken = new Token(newToken);

        Log.i(TAG, "    chatsFragment  -->  TOKEN : " + updatedToken.toString());

        dbReference.child(firebaseUser.getUid()).setValue(updatedToken);
    }


    private void readChat() {

        mUsers = new ArrayList<>();

//        dbReference = FirebaseDatabase.getInstance().getReference().child("Users");

        dbReference = FirebaseDatabase.getInstance().getReference("Users");
        dbReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUsers.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    User user = snapshot.getValue(User.class);

                    for(String id: usersList)
                    {
                        if (user.getId().equals(id))
                        {
/*      deepali_ConcurrentModificationException:
                mUsers was getting iterated and modified concurrently

            solution -  separated the arraylist into one being iterated(temp)
                        and the other being modified (mUsers)
*/
                            ArrayList<User> temp = new ArrayList<>(mUsers);
                            if (temp.size() != 0) {
                                for(User user_m: temp)
                                {
                                    if (!user.getId().equals(user_m.getId()))
                                        mUsers.add(user);
                                }
                            }
                            else
                                mUsers.add(user);
                        }
                    }
                }
// using HashSet to prevent Duplicate Data
                mUsersSet = new HashSet<>(mUsers);
                mUsers.removeAll(mUsers);
                mUsers.addAll(mUsersSet);

                userAdapter = new UserAdapter(getContext(), mUsers, true);
                recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}