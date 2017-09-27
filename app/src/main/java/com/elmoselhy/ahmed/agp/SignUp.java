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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    Button btnSignUp;
    Fonts fonts;
    private EditText inputEmail, inputPassword, inputconfPassword, inputName;
    private Spinner spinner;
    private DatabaseReference myUserDatabase;
    ProgressDialog progressDialogS;
    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        if (!internetConnection(SignUp.this))
            Toast.makeText(SignUp.this,"Check internet Connection Plz",Toast.LENGTH_SHORT).show();


        fonts = new Fonts(getApplicationContext());
        btnSignUp = (Button) findViewById(R.id.login_button);
        btnSignUp.setTypeface(fonts.robotoRegular);

        /*loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SignUp.this,PostsActivity.class));
            }
        });*/
        register();
    }

    private void register() {
        spinner = (Spinner) findViewById(R.id.spinner_categ);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputconfPassword = (EditText) findViewById(R.id.confPass);
        inputName = (EditText) findViewById(R.id.name);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                department = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(SignUp.this,"Choose Department Pleaze",Toast.LENGTH_SHORT).show();
            }


        });
        //Get Firebase auth instance
        myAuth = FirebaseAuth.getInstance();
        myUserDatabase = FirebaseDatabase.getInstance().getReference();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sinup();
            }
        });
    }
    String department = "";
    private void sinup() {


        final String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        final String firstName = inputName.getText().toString().trim();

        if (!validate()) {
            return;
        }
        progressDialogS = new ProgressDialog(SignUp.this);
        progressDialogS.setIndeterminate(true);
        progressDialogS.setMessage("Creating Account...");
        progressDialogS.show();
        //create user
        myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    AddUserData(firstName, email, "", department);
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor spe = sp.edit();
                    spe.putString(ConstantsAGP.USER_ID, myAuth.getCurrentUser().getUid()).apply();
                    spe.commit();

                } else {

                    progressDialogS.dismiss();

                    Toast.makeText(SignUp.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    Toast.makeText(SignUp.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        String firstName = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String repassword = inputconfPassword.getText().toString().trim();

        if (firstName.isEmpty() || firstName.length() < 3) {
            inputName.setError("at least 3 characters");
            Toast.makeText(SignUp.this, "Name is not right", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputName.setError(null);
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("enter a valid email address");
            Toast.makeText(SignUp.this, "Email is not right", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 7 || password.length() > 14) {
            inputPassword.setError("between 7 and 14 alphanumeric characters");
            Toast.makeText(SignUp.this, "Password is not right", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputPassword.setError(null);
        }
        if (!repassword.equals(password)) {
            inputPassword.setError("password not match!");
            Toast.makeText(SignUp.this, "password not match", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            inputPassword.setError(null);
        }
        return valid;
    }

    private void AddUserData(String firstName, String email, String image, String department) {
        User user = new User(firstName, email, "student", "", department, "");
        String user_id = myAuth.getCurrentUser().getUid();
        DatabaseReference user_database = myUserDatabase.child(ConstantsAGP.FIREBASE_LOCATION_USERS).child(user_id);
        user_database.child("name").setValue(firstName);
        user_database.child("email").setValue(email);
        user_database.child("image").setValue(image);
        user_database.child("type").setValue("student");
        user_database.child("subject").setValue("");
        user_database.child("department").setValue(department);
        Intent mainIntent = new Intent(SignUp.this, PostsActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                            finish();
//        user_database.setValue(user)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(SignUp.this, "Welcome", Toast.LENGTH_SHORT).show();
//                            progressDialogS.dismiss();
//                            Intent mainIntent = new Intent(SignUp.this, PostsActivity.class);
//                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(mainIntent);
//                            finish();
//                        } else {
//                            progressDialogS.dismiss();
//                            Toast.makeText(SignUp.this, "Error : not add ", Toast.LENGTH_SHORT).show();
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
