package com.example.s4966.ecs165;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.google.firebase.auth.*;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.*;

public class Profile extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init myAuth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser= mAuth.getCurrentUser();
                if(currentUser == null) {
                    Snackbar.make(view, "Please Log In", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //然后进入log in界面
                    Intent intent = new Intent();
                    intent.setClass(Profile.this,LoginActivity.class);
                    startActivity(intent);
                } else {
                    String name = currentUser.getDisplayName();
                    Snackbar.make(view, "Hello"+ name, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //然后进入setting界面, setting页面未设置
                    Intent intent = new Intent();
                    intent.setClass(Profile.this,LoginActivity.class);
                    startActivity(intent);

                }
            }
        });
    }
    public void updateUI(FirebaseUser user){

    }


}
