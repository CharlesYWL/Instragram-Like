package com.example.s4966.ecs165;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.*;

import java.util.concurrent.Semaphore;


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
    private StorageReference storageReference;
    private TextView nameTextView;
    private TextView uidTextView;
    private TextView genderTextView;
    private TextView bioTextView;
    private ImageView imageView;

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
         storageReference = FirebaseStorage.getInstance().getReference();
         nameTextView = findViewById(R.id.name_textView);
         uidTextView = findViewById(R.id.uid_textView);
         genderTextView = findViewById(R.id.gender_textView);
         bioTextView= findViewById(R.id.bio_textView);
         imageView = findViewById(R.id.pic_imageview);

         if(user == null) { // for some rare case
             Intent intent = new Intent().setClass(Profile.this,LoginActivity.class);
             startActivity(intent);
         }
         //it only opearte once per load
         // test

        //[command start]
         mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = (String) dataSnapshot.child("username").getValue();
                String bio = (String) dataSnapshot.child("bio").getValue();
                String email = (String) dataSnapshot.child("email").getValue();
                User.GENDER gender = User.GENDER.UNKNOW;
                switch (dataSnapshot.child("gender").getValue().toString()){
                    case "MALE":
                        gender = User.GENDER.MALE;
                        break;
                    case "FEMALE":
                        gender = User.GENDER.FEMALE;
                        break;
                    case "UNKNOW":
                        gender = User.GENDER.UNKNOW;
                        break;
                }
                final User us = new User(user.getUid(),username, bio, email, gender, null);

                if (dataSnapshot.child("pictureId").exists()) {
                    final Semaphore semaphore = new Semaphore(1);
                    final String pictureId = (String) dataSnapshot.child("pictureId").getValue();
                    // TODO there is a hard image size limit, may fix it in future.
                    final long TEN_MEGABYTE = 10 * 1024 * 1024;
                    StorageReference storagePicNode = storageReference.child("pic");
                    storagePicNode.child(pictureId).getBytes(TEN_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageView.setImageBitmap(bmp);
                        }
                    });
                }
                us.setUid(user.getUid());
                updataUI(us);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //[command end]

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



    }


    //make back Navi on tool bar works and direction
    public void setBackWork(Toolbar tb){
        getSupportActionBar().setTitle("Profile");
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }

    public void logout(View v){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent();
        intent.setClass(Profile.this,MainActivity.class);
        startActivity(intent);
    }

    public void updataUI(User user){
        nameTextView.setText(user.getUsername());
        bioTextView.setText(user.getBio());
        uidTextView.setText(user.getUid());
        String g="\0";
        switch (user.getGender()){
            case MALE:
                g = "MALE";
                break;
            case FEMALE:
                g = "FEMALE";
                break;
            case UNKNOW:
                g = "UNKNOW";
                break;
        }
        genderTextView.setText(g);
        //TODO need dealwith photo
        imageView.setImageDrawable(user.getPicture());
//        GlideApp.with(this).load(storageReference.child("pic").getDownloadUrl()).into(imageView);

    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(Profile.this,MainActivity.class));
    }

}

