package com.elmoselhy.ahmed.agp;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by ahmedelmoselhy on 9/22/2017.
 */

public class Table extends AppCompatActivity {
    ImageView drawerMenuBtn;
    FloatingActionButton actionButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_posts);
        drawerMenuBtn = (ImageView) findViewById(R.id.cv_mnu);
        drawerMenuBtn.setVisibility(View.GONE);
        actionButton = (FloatingActionButton) findViewById(R.id.fab_out_free_code);
        actionButton.setVisibility(View.GONE);

    }
}
