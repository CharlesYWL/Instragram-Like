package com.example.s4966.ecs165;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    private Toolbar mToolbar;
    //need to use for every class
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }
    private ImageButton SignoutButton;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private TextView nameTextView;
    private TextView uidTextView;
    private TextView genderTextView;
    private TextView bioTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //toolbar apply to all
        mToolbar=findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setBackWork(mToolbar);

        //init all thing
         mDatabase = FirebaseDatabase.getInstance().getReference();
         user = FirebaseAuth.getInstance().getCurrentUser();
         nameTextView = findViewById(R.id.name_textView);
         uidTextView = findViewById(R.id.uid_textView);
         genderTextView = findViewById(R.id.gender_textView);
         bioTextView= findViewById(R.id.bio_textView);



        //this one listen to toolbar click
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.Setting:
                        //open modify activity
                        Intent intent = new Intent();
                        intent.setClass(Profile.this,ProfileModify.class);
                        startActivity(intent);
                        //test only
                        Toast.makeText(Profile.this,"Enter setting",Toast.LENGTH_LONG).show();
                        return true;
                }
                return false;
            }
        });
        //make sure it has user

 /*       mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(Profile.this,dataSnapshot.getValue().toString(),Toast.LENGTH_LONG).show();

                String name = dataSnapshot.child("username").getValue().toString();
                String gender = dataSnapshot.child("gender").getValue().toString();
                String bio = dataSnapshot.child("bio").getValue().toString();
                nameTextView.setText(name);
                uidTextView.setText(user.getUid());
                genderTextView.setText(gender);
                bioTextView.setText(bio);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //do nothing
            }
        });
*/



    }


    //make back Navi on tool bar works
    public void setBackWork(Toolbar tb){
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void logout(View v){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent();
        intent.setClass(Profile.this,MainActivity.class);
        startActivity(intent);
    }

    public void UpdataProfile(User user,FirebaseDatabase mFirebase){

    }


}

