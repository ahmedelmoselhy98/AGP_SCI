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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    TextView agpMainTextView, agpStandsForTextView;
    Button signInButton, signUpButton, forgetPasswordButton;
    private EditText inputEmail, inputPassword;
    Fonts fonts;
    ProgressDialog progressDialog;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    StorageReference mystorage;
    private FirebaseAuth myAuth;
    private DatabaseReference myUserDatabase;
    User doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fonts = new Fonts(getApplicationContext());
        agpMainTextView = (TextView) findViewById(R.id.agp_main_textView);
        agpMainTextView.setTypeface(fonts.lobsterRegular);
        signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setTypeface(fonts.robotoRegular);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setTypeface(fonts.robotoRegular);
        inputEmail = (EditText) findViewById(R.id.email_login);
        inputPassword = (EditText) findViewById(R.id.pswrd_login);

        myAuth = FirebaseAuth.getInstance();

        if (!internetConnection(MainActivity.this))
            Toast.makeText(MainActivity.this,"Check internet Connection Plz",Toast.LENGTH_SHORT).show();
        authenticatinChecker();


//        forgetPasswordButton = (Button) findViewById(R.id.forgetpassword);
//        forgetPasswordButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Check Your Email...", Toast.LENGTH_SHORT);
//                if (!TextUtils.isEmpty(inputEmail.getText().toString())) {
//
//                    myAuth.sendPasswordResetEmail(inputEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//
//                            if (task.isSuccessful()) {
//
//                                Toast.makeText(MainActivity.this, "Check Your Email...", Toast.LENGTH_SHORT);
//                            } else
//                                Toast.makeText(MainActivity.this, "retry...", Toast.LENGTH_SHORT);
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(MainActivity.this, "Sending Failed...", Toast.LENGTH_SHORT);
//                        }
//                    });
//
//                }else
//                    Toast.makeText(MainActivity.this, "Email is empty...", Toast.LENGTH_SHORT);
//
//            }
//        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogin();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
            }
        });

    }

    private void authenticatinChecker() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(MainActivity.this, PostsActivity.class));
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        myAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            myAuth.removeAuthStateListener(mAuthStateListener);
        }
    }


    private void mLogin() {

        String email = inputEmail .getText().toString().trim();
        final String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(MainActivity.this, "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(MainActivity.this, "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sign in Account...");
        progressDialog.show();

        //authenticate user
        myAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {

            @Override
            public void onSuccess(AuthResult authResult) {
                progressDialog.dismiss();
                startActivity(new Intent(MainActivity.this, PostsActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.e("Login Error",e.getMessage());
                progressDialog.dismiss();
            }
        });
//        myAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        // If sign in fails, display a message to the user. If sign in succeeds
//                        // the auth state listener will be notified and logic to handle the
//                        // signed in user can be handled in the listener.
//                        if (!task.isSuccessful()) {
//                            // there was an error
//
//                            progressDialog.dismiss();
//                            Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
//                        } else {
////                            // TODO hidden fab if user is not doctor
////                            String userID = myAuth.getCurrentUser().getUid();
////                            myUserDatabase = myApplication.getDatabaseReference().child(ConstantsAGP.FIREBASE_LOCATION_USERS).child(userID);
////                            myUserDatabase.addValueEventListener(new ValueEventListener() {
////                                @Override
////                                public void onDataChange(DataSnapshot dataSnapshot) {
////                                    doctor = dataSnapshot.getValue(User.class);
////                                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
////                                    SharedPreferences.Editor spe = sp.edit();
////                                    spe.putString(ConstantsAGP.USER_ID, myAuth.getCurrentUser().getUid()).apply();
////                                    spe.putString(ConstantsAGP.USER_TYPE, doctor.getType()).apply();
////                                    spe.commit();
////                                }
////
////                                @Override
////                                public void onCancelled(DatabaseError databaseError) {
////                                    // Getting Post failed, log a message
////                                    Log.w("AG", "loadPost:onCancelled", databaseError.toException());
////                                    // ...
////                                }
////                            });
//                            progressDialog.dismiss();
//                            startActivity(new Intent(MainActivity.this, PostsActivity.class));
//
//                        }
//                    }
//                });

    }
    public boolean internetConnection(Context c) {
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else
            return false;
    }



}
