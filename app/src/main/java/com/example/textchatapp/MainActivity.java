package com.example.textchatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.textchatapp.Fragments.ChatsFragment;
import com.example.textchatapp.Fragments.UsersFragment;
import com.example.textchatapp.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "logTAG";


    CircleImageView profileImageView;
    TextView usernameText;
    Toolbar toolbar_main;

    FirebaseUser firebaseUser;
    DatabaseReference dbReference;

    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        profileImageView = findViewById(R.id.profileImage_main);
        usernameText = findViewById(R.id.usernameText_main);

//      DOes not Work --> usernameText.setTypeface(null, Typeface.ITALIC);


        toolbar_main = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_main.inflateMenu(R.menu.menu);

        /*      deepali_StringError: Empty String Error in
                        toolbar_main.setTitle(" ");
                correction: getSupportActionBar().setDisplayShowTitleEnabled(false);
        */


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        dbReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

/*    Log.i(TAG, "user_id  -->  "+user.getId());      */

                assert user != null;
                usernameText.setText(user.getUsername());

                if (user.getImageURL().equals("default"))
                    profileImageView.setImageResource(R.drawable.default_profile_image);
                else
                    Glide.with(MainActivity.this).load(user.getImageURL()).into(profileImageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        TabLayout tabLayout = findViewById(R.id.tabLayout_main);
        ViewPager viewPager_main = findViewById(R.id.viewPager_main);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addToList(new ChatsFragment(), "CHATS");
        viewPagerAdapter.addToList(new UsersFragment(), "USERS");

        viewPager_main.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager_main);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.logout_item_menu:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, StartActivity.class));
                finish();
                return true;
        }
        return false;
    }



        /*    deepali_deprecatedClass:
                    ViewPager and FragmentPagerAdapter are deprecated
                    ViewPager2 and FragmentStateAdapter are respective alternative
        */


    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragmentsList;
        private ArrayList<String> fragmentsTitleList;


        ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            this.fragmentsList = new ArrayList<>();
            this.fragmentsTitleList = new ArrayList<>();
        }


        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentsList.get(position);
        }


        @Override
        public int getCount() {
            return fragmentsList.size();
        }


        public void addToList(Fragment eachFragment, String eachFragmentTitle)
        {
            fragmentsList.add(eachFragment);
            fragmentsTitleList.add(eachFragmentTitle);
        }


        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitleList.get(position);
        }

    }


}