package com.example.s4966.ecs165;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;


public class ProfileModify extends AppCompatActivity {


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
