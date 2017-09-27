package com.elmoselhy.ahmed.agp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ahmedelmoselhy on 9/4/2017.
 */

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.RecyclerViewHolder> {

    private static int NUM_OF_ITEMS = 0;
    private static Context c;
    List<Post> postList;


    public PostsAdapter(Context c, List<Post> postList) {
        this.c = c;
        this.postList = postList;
        NUM_OF_ITEMS = postList.size();

    }

    @Override
    public PostsAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.post_item, parent, false);
        RecyclerViewHolder holder = new RecyclerViewHolder(v);
        return holder;
    }

//    private final static int FADE_DURATION = 1000;

    @Override
    public void onBindViewHolder(PostsAdapter.RecyclerViewHolder holder, int position) {
//        Toast.makeText(c, "" + position, Toast.LENGTH_SHORT).show();
        holder.bindData(postList, position);
//        setFadeAnimation(holder.itemView);

    }

    // functions to Animate Recycler Items
//    private void setFadeAnimation(View view) {
//        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
//        anim.setDuration(FADE_DURATION);
//        view.startAnimation(anim);
//    }
//
//    private void setScaleAnimation(View view) {
//        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        anim.setDuration(FADE_DURATION);
//        view.startAnimation(anim);
//    }

    @Override
    public int getItemCount() {
        return NUM_OF_ITEMS;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {


        TextView doctorNameTextView, postTimeTextView, postDescriptionTextView, materialNameTextView;
        CircleImageView doctorImage;
        String doctorName;
        String postTime;
        String postDetail;
        String materialNam;
        String imagePath;
        Fonts fonts;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            fonts = new Fonts(c);
            doctorImage = (CircleImageView) itemView.findViewById(R.id.doctor_imageView);
            doctorNameTextView = (TextView) itemView.findViewById(R.id.doctor_name_text_view);
            doctorNameTextView.setTypeface(fonts.robotoRegular);
            postTimeTextView = (TextView) itemView.findViewById(R.id.post_time_text_view);
            postTimeTextView.setTypeface(fonts.robotoThin);
            postDescriptionTextView = (TextView) itemView.findViewById(R.id.post_description_text_view);
            postDescriptionTextView.setTypeface(fonts.arefRuqaaBold);
            materialNameTextView = (TextView) itemView.findViewById(R.id.material_name_text_view);
            materialNameTextView.setTypeface(fonts.robotoRegular);
        }

        public void bindData(List<Post> postList, int position) {

            doctorName = postList.get(position).getDoctorName();
            doctorNameTextView.setText(doctorName);
            postTime = postList.get(position).getTimestampCreatedLong();
            postTimeTextView.setText(postTime);
            postDetail = postList.get(position).getDetail();
            postDescriptionTextView.setText(postDetail);
            materialNam = postList.get(position).getSubject();
            materialNameTextView.setText(materialNam);
//Toast.makeText(c,postList.get(position).getImage(),Toast.LENGTH_LONG).show();


//            userIdToImage(postList.get(position).getImage());

            DatabaseReference myUserDatabase;
            Log.e("getImage",postList.get(position).getImage()+"");
            myUserDatabase = myApplication.getDatabaseReference().child(ConstantsAGP.FIREBASE_LOCATION_USERS).child(postList.get(position).getImage());
            myUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                    // TODO: handle the post
//                }
//                    User user = dataSnapshot.getValue(User.class);
                    String name,email,type,subject,image;
                    String department="";
                    name = dataSnapshot.child("name").getValue().toString();
                    type = dataSnapshot.child("type").getValue().toString();
                    image = dataSnapshot.child("image").getValue().toString();
                    email = dataSnapshot.child("email").getValue().toString();
                    subject = dataSnapshot.child("subject").getValue().toString();
//                    department = dataSnapshot.child("department").getValue().toString();

                    User user = new User(name,email,type,subject,department,image);
                    imagePath = user.getImage();

                    if (!TextUtils.isEmpty(imagePath)) {
//                        Toast.makeText(c, "1", Toast.LENGTH_LONG).show();
                        Picasso.with(c).load(imagePath).into(doctorImage);
                    } else {
                        Toast.makeText(c, "image path is null", Toast.LENGTH_LONG).show();
                    }
//                    Toast.makeText(c,imagePath,Toast.LENGTH_LONG).show();
//                    if (!image[0].equals(null))
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w("AG", "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            });

//  Toast.makeText(c,"1",Toast.LENGTH_LONG).show();
//            if (!TextUtils.isEmpty(imagePath)) {
//                Toast.makeText(c, "1", Toast.LENGTH_LONG).show();
//                Picasso.with(c).load(imagePath).into(doctorImage);
//            } else {
//                Toast.makeText(c, "image path is null", Toast.LENGTH_LONG).show();
//            }

        }

        public void userIdToImage(String userId) {

            final String[] image = {""};
            String userImage;
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            DatabaseReference myUserDatabase;
            myUserDatabase = FirebaseDatabase.getInstance().getReference().child(ConstantsAGP.FIREBASE_LOCATION_USERS).child(userId);
            myUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                    // TODO: handle the post
//                }
                    User user = dataSnapshot.getValue(User.class);
                    imagePath = user.getImage();
//                    Toast.makeText(c,imagePath,Toast.LENGTH_LONG).show();
//                    if (!image[0].equals(null))
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w("AG", "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            });

//                        Toast.makeText(c,image[0],Toast.LENGTH_LONG).show();
//            Toast.makeText(c,userImage,Toast.LENGTH_LONG).show();
        }


    }


}
