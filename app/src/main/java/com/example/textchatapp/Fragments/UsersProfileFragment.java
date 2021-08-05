package com.example.textchatapp.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.textchatapp.Model.User;
import com.example.textchatapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;



public class UsersProfileFragment extends Fragment {


    CircleImageView profileImageView;
    TextView usernameText;

    FirebaseUser firebaseUser;
    DatabaseReference dbReference;

//    private static final int IMAGE_REQUEST = 1;

    private Uri imageUri;
    StorageReference storageReference;
    private StorageTask<UploadTask.TaskSnapshot> uploadImageTask;

    ActivityResultLauncher<Intent> resultLauncher;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users_profile, container,false);

        profileImageView = view.findViewById(R.id.profileImage_userProfile);
        usernameText = view.findViewById(R.id.userNameText_profile);

        storageReference = FirebaseStorage.getInstance().getReference("Uploaded_Images");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                assert user != null;
                usernameText.setText(user.getUsername());

                if (user.getImageURL().equals("default"))
                    profileImageView.setImageResource(R.drawable.default_profile_image);
                else
                    Glide.with(getContext()).load(user.getImageURL()).into(profileImageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        resultLauncher = registerForActivityResult (
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult (ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK)
                    {
                        Intent data = result.getData();

                        if (data != null && data.getData() != null) {
                            imageUri = data.getData();
                            if (uploadImageTask != null && uploadImageTask.isInProgress())
                                Toast.makeText(getContext(), "Uploading in Progress", Toast.LENGTH_LONG).show();
                            else
                                uploadImage();
                        }

                    }
                }
        });


        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        return view;
    }


    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        resultLauncher.launch(intent);
    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference fileReference = storageReference.child(
                    System.currentTimeMillis() + "." + getFileExtension(imageUri) );

            uploadImageTask = fileReference.putFile(imageUri);
            uploadImageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>> () {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//  unchecked or unsafe operation Error:
//      Caused by - throw task.getException();
                    if (!task.isSuccessful())
                        throw Objects.requireNonNull(task.getException());

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        dbReference.updateChildren(map);

                        progressDialog.dismiss();
                    }
                    else {
                        Toast.makeText(getContext(), "Upload Failed !", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        }
        else
            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
    }

}


/*      Deprecated Method :-
        #    startActivityForResult(intent, IMAGE_REQUEST);
            and
            @Override
        #    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
            {
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK &&
                        data != null && data.getData() != null)
                        {
                            imageUri = data.getData();
                            if (uploadImageTask != null && uploadImageTask.isInProgress())
                                Toast.makeText(getContext(), "Uploading in Progress", Toast.LENGTH_LONG).show();
                            else
                                uploadImage();
                        }
            }
 */
