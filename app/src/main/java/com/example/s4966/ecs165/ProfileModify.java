package com.example.s4966.ecs165;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.s4966.ecs165.User.GENDER.FEMALE;
import static com.example.s4966.ecs165.User.GENDER.MALE;
import com.example.s4966.ecs165.User;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ProfileModify extends AppCompatActivity {

    private TextView newName;
    private AppCompatButton updataB;
    private TextView bio;
    private RadioGroup radioGroup;
    private RadioButton radioButtonM;
    private RadioButton radioButtonF;
    private ImageView imageView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private StorageReference mStore;
    User user;


    private Toolbar mToolbar;
    //need to use for every class
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_nomenu,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_modify);

        //toolbar apply to all
        mToolbar=findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setBackWork(mToolbar);

        newName = findViewById(R.id.newName);
        updataB = findViewById(R.id.updata);
        radioGroup = findViewById(R.id.radioGroup);
        radioButtonM = findViewById(R.id.radioButtonM);
        radioButtonF = findViewById(R.id.radioButtonF);
        imageView = findViewById(R.id.pic_imageview);
        bio = findViewById(R.id.newBio);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStore = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        //it only opearte once per load
        mDatabase.child("users").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(ProfileModify.this,"Enter one time Listener",Toast.LENGTH_LONG).show();
                User us = dataSnapshot.getValue(User.class);
                us.setUid(mUser.getUid());
                updataUI(us);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        updataB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 User.GENDER gender;
                if (radioGroup.getCheckedRadioButtonId() == R.id.radioButtonM) gender = MALE;
                else gender = FEMALE;
                //didnt deal with picture
                 user = new User(mUser.getUid(), newName.getText().toString(), bio.getText().toString(), mUser.getEmail(), gender, imageView.getDrawable());
                User.updataUser(mDatabase.child("users"), mStore.child("pic"), user);
                Intent intent = new Intent();
                intent.setClass(ProfileModify.this,Profile.class);
                startActivity(intent);
            }
        });


    }


    //make back Navi on tool bar works and othersetting about tool bars
    public void setBackWork(Toolbar tb){
        getSupportActionBar().setTitle("Account setting");
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //when Click on Picture
    public void changePic(View v){
        Intent intent = new Intent();
        intent.setClass(ProfileModify.this,PictureChange.class);
        startActivity(intent);
    }

    //when click on ChangePassword
    public void changePassword(View v){
        Intent intent = new Intent();
        intent.setClass(ProfileModify.this,ChangePassword.class);
        startActivity(intent);
    }

    //updata info.
    public void updataUI(User user){
        newName.setText(user.getUsername());
        bio.setText(user.getBio());
        if (user.getGender()== MALE)    radioGroup.check(radioButtonM.getId());
        else    radioGroup.check(radioButtonF.getId());
//        Glide.with(this).load(storageReference).into(imageView);

    }
}
