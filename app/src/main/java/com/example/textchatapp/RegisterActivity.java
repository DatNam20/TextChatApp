package com.example.textchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "logTAG";


    MaterialEditText usernameText, emailText, passwordText;
    Button registerButton;
    Toolbar toolbar_layout;
    String userID;

    FirebaseAuth firebaseAuth;
    DatabaseReference dbReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        toolbar_layout = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar_layout);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        firebaseAuth = FirebaseAuth.getInstance();


        usernameText = findViewById(R.id.username_register);
        emailText = findViewById(R.id.email_register);
        passwordText = findViewById(R.id.password_register);
        registerButton = findViewById(R.id.registerButton);


        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String usernameValue = usernameText.getText().toString();
                String emailValue = emailText.getText().toString();
                String passwordValue = passwordText.getText().toString();


                if ( TextUtils.isEmpty(usernameValue) || TextUtils.isEmpty(emailValue) || TextUtils.isEmpty(passwordValue) )
                    Toast.makeText(RegisterActivity.this, "All Fields are Required", Toast.LENGTH_SHORT).show();

                else if (passwordValue.length() < 8)
                    Toast.makeText(RegisterActivity.this, "Password must have minimum 8 characters",
                            Toast.LENGTH_SHORT).show();

                else
                    registerAccount(usernameValue, emailValue, passwordValue);
            }
        });

    }


    private void registerAccount(String username, String email, String password)
    {
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful())
                        {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            assert firebaseUser != null;
                            userID = firebaseUser.getUid();

                            dbReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);


                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userID);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "default");


                            dbReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful())
                                    {
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }

                        else
                            Toast.makeText(RegisterActivity.this, "Invalid Email-id or Password",
                                    Toast.LENGTH_LONG).show();
                    }
                });
    }
}