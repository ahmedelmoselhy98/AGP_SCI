package com.elmoselhy.ahmed.agp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static FlowingDrawer mDrawer;
    TextView agpPostsTextView, doctorNameTextView, postTimeTextView, postDescriptionTextView, materialNameTextView, logOut;
    TextView profileName, profileDepartment, profileEmail, table;
    FlowingDrawer flowingDrawer;
    ImageView drawerMenuBtn;
    CircleImageView profileImage;
    FloatingActionButton actionButton;
    RecyclerView postsRecyclerView;
    PostsAdapter postsAdapter;
    Fonts fonts;
    DrawerLayout drawer;
    SearchView searchView;
    static final int GALLARY_REQUEST = 1;
    Uri imageUri;
    private FirebaseAuth myAuth;
    private DatabaseReference myPostsDatabase, myUserDatabase;
    private StorageReference mstorage;
    User user;
    String userType;
    private ProgressDialog mProgressDialog;
    List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_posts);
        if (!internetConnection(PostsActivity.this)) {
            Toast.makeText(PostsActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT);
        }

        profileName = (TextView) findViewById(R.id.profile_username);
        profileEmail = (TextView) findViewById(R.id.profile_email);
        profileDepartment = (TextView) findViewById(R.id.profile_department);
        profileImage = (CircleImageView) findViewById(R.id.profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentImageSelect = new Intent(Intent.ACTION_GET_CONTENT);
                intentImageSelect.setType("image/*");
                startActivityForResult(intentImageSelect, GALLARY_REQUEST);
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");

        table = (TextView) findViewById(R.id.table_text_view);
        table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(PostsActivity.this, Table.class));

            }
        });

        myAuth = FirebaseAuth.getInstance();
        mstorage = FirebaseStorage.getInstance().getReference();
        final String userID = myAuth.getCurrentUser().getUid();
        myUserDatabase = myApplication.getDatabaseReference().child(ConstantsAGP.FIREBASE_LOCATION_USERS).child(userID);
        if (!internetConnection(PostsActivity.this)) {
            Toast.makeText(PostsActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT);
        } else {
            myUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                    // TODO: handle the post
//                }
                    Toast.makeText(getApplicationContext(), userID, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), dataSnapshot.child("name").getValue().toString(), Toast.LENGTH_SHORT).show();
                    String name, email, type, subject, image;
                    String department = "";
                    name = dataSnapshot.child("name").getValue().toString();
                    type = dataSnapshot.child("type").getValue().toString();
                    image = dataSnapshot.child("image").getValue().toString();
                    email = dataSnapshot.child("email").getValue().toString();
                    subject = dataSnapshot.child("subject").getValue().toString();
                    department = dataSnapshot.child("department").getValue().toString();
                    String hour = dataSnapshot.child("hour").getValue().toString();
                    String mint = dataSnapshot.child("minute").getValue().toString();
                    String day = dataSnapshot.child("day").getValue().toString();

                    user = new User(name, email, type, subject, department, image);
//                user = dataSnapshot.getValue(User.class);

                    profileName.setText(user.getName());
                    profileDepartment.setText(user.getDepartment());
                    profileEmail.setText(user.getEmail());
                    userType = user.getType();
                    if (!TextUtils.isEmpty(user.getImage()) || !TextUtils.isEmpty("")) {
                        Picasso.with(PostsActivity.this).load(user.getImage()).into(profileImage);
                    } else
                        Toast.makeText(PostsActivity.this, "Image Path Empty", Toast.LENGTH_SHORT).show();
                    // TODO Send Notification
                    if (userType.equals("doctor")) {
                        Calendar calendar = Calendar.getInstance();
                        int dayC = calendar.get(Calendar.DAY_OF_WEEK);
                        int hourC = calendar.get(Calendar.HOUR_OF_DAY);
                        int mintC = calendar.get(Calendar.MINUTE);
                        if (day.equals(dayC + "")) {
                            if (hourC <= Integer.parseInt(hour)) {
                                if (mintC <= Integer.parseInt(mint)) {
                                    user.setHour(hour);
                                    user.setMinute(mint);
                                    sendNotification();
                                }
                            }
                        }
                    }

                    // TODO User Type
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(PostsActivity.this);
//        String userType = sp.getString(ConstantsAGP.USER_TYPE, "student");
                    if (userType.equals("student")) {
                        FloatingActionButton mFab = (FloatingActionButton) findViewById(R.id.fab_out_free_code);
                        mFab.setVisibility(View.INVISIBLE);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w("AG", "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            });
        }
        logOut = (TextView) findViewById(R.id.log_out);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(PostsActivity.this, MainActivity.class));
            }
        });

        searchView = (SearchView) findViewById(R.id.search_text);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });


        fonts = new Fonts(getApplicationContext());
        agpPostsTextView = (TextView) findViewById(R.id.agp_Posts_textView);
        agpPostsTextView.setTypeface(fonts.lobsterRegular);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        drawerMenuBtn = (ImageView) findViewById(R.id.cv_mnu);
//        flowingDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        drawerMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                flowingDrawer.setVisibility(View.VISIBLE);
                mDrawer.toggleMenu();

            }
        });


//        logOut = findViewById()
        postsRecyclerView = (RecyclerView) findViewById(R.id.posts_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        postsRecyclerView.setLayoutManager(layoutManager);
        postsRecyclerView.setHasFixedSize(true);

        myPostsDatabase = myApplication.getDatabaseReference().child(ConstantsAGP.FIREBASE_LOCATION_POSTS);
        myPostsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    post.setDoctorimage(postSnapshot.child("doctorimage").getValue().toString());
                    postList.add(post);
                }
                postsAdapter = new PostsAdapter(PostsActivity.this, postList);
                postsRecyclerView.setAdapter(postsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("AG", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });


        actionButton = (FloatingActionButton) findViewById(R.id.fab_out_free_code);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(PostsActivity.this, AddPostActivity.class));
            }
        });


//        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDrawer.toggleMenu();
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLARY_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
            StorageReference putFile = mstorage.child("Blog_Images").child(imageUri.getLastPathSegment());

            putFile.putFile(imageUri).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Uploading Faild", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
//                    Toast.makeText(PostsActivity.this, downloadUri.toString(), Toast.LENGTH_SHORT).show();
                    myUserDatabase.child("image").setValue(downloadUri.toString());
                    Toast.makeText(PostsActivity.this, "Uploading Success", Toast.LENGTH_SHORT).show();
                }

            });
        } else {
            Toast.makeText(getApplicationContext(), "There Is Empty Fields", Toast.LENGTH_SHORT).show();
        }

    }


    private void search(final String newText) {
        myPostsDatabase = myApplication.getDatabaseReference().child(ConstantsAGP.FIREBASE_LOCATION_POSTS);
        myPostsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList = new ArrayList<>();
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    Post post = postSnapshot.getValue(Post.class);
//                    if (post.getDoctorName().startsWith(newText) || post.getSubject().startsWith(newText)) {
//                        postList.add(post);
//                    }
//                }

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    post.setDoctorimage(postSnapshot.child("doctorimage").getValue().toString());
                    if (post.getSubject().contains(newText)) {
                        postList.add(post);
                    }

                }

                postsAdapter = new PostsAdapter(PostsActivity.this, postList);
                postsRecyclerView.setAdapter(postsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("AG", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        drawer = (DrawerLayout) findViewById(R.id.menulayout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    protected void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.toggleMenu();
            }
        });
    }


    /*@Override
    public void onBackPressed() {
        if (mDrawer.isMenuVisible()) {
            mDrawer.closeMenu();
        } else {
            super.onBackPressed();
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.menulayout);
        if (id == R.id.cv_mnu) {
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END);
            } else {
                drawer.openDrawer(GravityCompat.END);
            }

        }
        return super.onOptionsItemSelected(item);
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


    public void sendNotification() {
        try {
            String date = new SimpleDateFormat("d-M-yyyy").format(new Date());
            String toParse = date + " " + user.getHour() + ":" + user.getMinute(); // Results in "2-5-2012 20:43"
            SimpleDateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm"); // I assume d-M, you may refer to M-d for month-day instead.
            Date dateF = null; // You will need try/catch around this
            dateF = formatter.parse(toParse);

            long millis = dateF.getTime();


            Intent alertIntent = new Intent(getApplicationContext(), AlertReceiver.class);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            alarmManager.set(AlarmManager.RTC_WAKEUP, millis, PendingIntent.getBroadcast(getApplicationContext(), 0, alertIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT));


        } catch (ParseException e) {
            e.printStackTrace();
        }
        /*Calendar calendar = Calendar.getInstance();
        long hourOfDay = Long.parseLong(user.getHour());
        int minute = Integer.parseInt(user.getMinute());
        ;
        String day = user.getDay();*/

//        calendar.set(Calendar.DAY_OF_WEEK, hourOfDay);
//        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
//        calendar.set(Calendar.MINUTE, minute);
//        calendar.set(Calendar.SECOND, 0);
    }

}
