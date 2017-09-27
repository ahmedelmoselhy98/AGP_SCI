package com.elmoselhy.ahmed.agp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {

    ImageView backImageView, submitPost;
    private FirebaseAuth myAuth;
    ProgressDialog progressDialog;
    User doctor;
    EditText doctorNameEdit, doctorSubjectEdit;

    private DatabaseReference myUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        if (!internetConnection(AddPostActivity.this)) {
            Toast.makeText(AddPostActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT);
            return;
        }

        doctorNameEdit = (EditText) findViewById(R.id.addpostdocname);
        doctorSubjectEdit = (EditText) findViewById(R.id.addpostsubject);
        myAuth = FirebaseAuth.getInstance();

        String userID = myAuth.getCurrentUser().getUid();
        myUserDatabase = myApplication.getDatabaseReference().child(ConstantsAGP.FIREBASE_LOCATION_USERS).child(userID);
        myUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                    // TODO: handle the post
//                }
//                doctor = dataSnapshot.getValue(User.class);
                String name,email,type,subject,image;
                String department="";
                name = dataSnapshot.child("name").getValue().toString();
                type = dataSnapshot.child("type").getValue().toString();
                image = dataSnapshot.child("image").getValue().toString();
                email = dataSnapshot.child("email").getValue().toString();
                subject = dataSnapshot.child("subject").getValue().toString();
                department = dataSnapshot.child("department").getValue().toString();

                doctor = new User(name,email,type,subject,department,image);
                Toast.makeText(getApplicationContext(), doctor.getName() + "  " + doctor.subject, Toast.LENGTH_SHORT).show();
                doctorNameEdit.setText(doctor.getName());
                doctorSubjectEdit.setText(doctor.getSubject());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("AG", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

        final EditText detailTxt = (EditText) findViewById(R.id.detail);
        submitPost = (ImageView) findViewById(R.id.submit_post_done);
        submitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(detailTxt.getText().toString()) && !TextUtils.isEmpty(doctor.getSubject()) && !TextUtils.isEmpty(doctor.getName())) {
                    AddPost(detailTxt.getText().toString(), doctor.getSubject(), doctor.getName());
                } else {
                    Toast.makeText(getApplicationContext(), "Empty Fields", Toast.LENGTH_SHORT).show();
                }

            }
        });

        backImageView = (ImageView) findViewById(R.id.back_to_posts);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AddPostActivity.this, PostsActivity.class));
            }
        });

    }

    private void AddPost(String Details, String subject, String doctName) {

        progressDialog = new ProgressDialog(AddPostActivity.this);
        progressDialog.setTitle("Add Client ...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        HashMap<String, Object> timestampJoined = new HashMap<>();
        timestampJoined.put("timestamp", ServerValue.TIMESTAMP);


        // TODO edite image
        String user_id = myAuth.getCurrentUser().getUid();
        Post post = new Post(user_id,Details, doctName, subject, timestampJoined);
        DatabaseReference myPostsDataBase = FirebaseDatabase.getInstance().getReference().child(ConstantsAGP.FIREBASE_LOCATION_POSTS).push();
        myPostsDataBase.setValue(post)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddPostActivity.this, "Done", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Intent mainIntent = new Intent(AddPostActivity.this, PostsActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(AddPostActivity.this, "Error : not add ", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


    //TODO Check Internet Connection
    public boolean internetConnection(Context c) {
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else
            return false;
    }


}