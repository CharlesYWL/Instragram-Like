package com.example.s4966.ecs165;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.*;

public class Profile extends AppCompatActivity {

    private Toolbar mToolbar;
    //need to use for every class
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //toolbar apply to all
        mToolbar=findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setBackWork(mToolbar);

        allListen();

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

    public void allListen(){
        //init all button
        ImageButton PicChange = findViewById(R.id.pic_button);
        ImageButton NameChange = findViewById(R.id.name_button);
        ImageButton IdChange = findViewById(R.id.uid_button);
        ImageButton GenderChange = findViewById(R.id.gender_button);
        ImageButton BioChange = findViewById(R.id.bio_button);

        PicChange.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent1 = new Intent();
                intent1.setClass(Profile.this,PictureChange.class);
                startActivity(intent1);
            }
        });
        NameChange.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent2 = new Intent();
                intent2.setClass(Profile.this,NameChange.class);
                startActivity(intent2);
            }
        });
        IdChange.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent3 = new Intent();
                intent3.setClass(Profile.this,IdChange.class);
                startActivity(intent3);
            }
        });
        GenderChange.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent4 = new Intent();
                intent4.setClass(Profile.this,GenderChange.class);
                startActivity(intent4);
            }
        });
        BioChange.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent5 = new Intent();
                intent5.setClass(Profile.this,BioChange.class);
                startActivity(intent5);
            }
        });

    }
}

