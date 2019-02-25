package com.example.s4966.ecs165;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SearchUser extends AppCompatActivity {

    private ImageButton searchB;
    private Toolbar mToolbar;
    private EditText target;
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_nomenu,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        target = findViewById(R.id.target);

        mToolbar=findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setBackWork(mToolbar);

        searchB = findViewById(R.id.searchButton);
        searchB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //search
                //TODO: now we dont judge it, just enter result page with every one display
                if(false){
                    Toast.makeText(SearchUser.this,"No Such Users",Toast.LENGTH_LONG).show();
                    target.setText("");
                }else{
                    //jump to SearchResult activity
                    Intent intent = new Intent();
                    intent.setClass(SearchUser.this,SearchResult.class);
                    String targetName=target.getText().toString();
                    intent.putExtra("targetName",targetName);
                    startActivity(intent);
                }
            }
        });
    }


    public void setBackWork(Toolbar tb){
        getSupportActionBar().setTitle("Search");
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
