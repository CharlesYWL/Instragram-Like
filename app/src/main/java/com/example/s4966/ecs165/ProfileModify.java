package com.example.s4966.ecs165;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.s4966.ecs165.User.GENDER.FEMALE;
import static com.example.s4966.ecs165.User.GENDER.MALE;
import com.example.s4966.ecs165.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ProfileModify extends AppCompatActivity {

    private TextView newName;
    private AppCompatButton updataB;
    private TextView bio;
    private RadioGroup radioGroup;
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
        bio = findViewById(R.id.newBio);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStore = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        updataB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 User.GENDER gender;
                if (radioGroup.getCheckedRadioButtonId() == R.id.radioButtonM) gender = MALE;
                else gender = FEMALE;
                //didnt deal with picture
                 user = new User(mUser.getUid(), newName.getText().toString(), bio.getText().toString(), mUser.getEmail(), gender, null);
                User.updataUser(mDatabase.child("users"), mStore.child("pic"), user);
                Intent intent = new Intent();
                intent.setClass(ProfileModify.this,Profile.class);
                startActivity(intent);
            }
        });


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


}
