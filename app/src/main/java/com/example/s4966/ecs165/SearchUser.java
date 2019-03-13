package com.example.s4966.ecs165;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SearchUser extends AppCompatActivity {

    public String TAG = "SearchUser";
    private ImageButton searchB;
    private Toolbar mToolbar;
    private EditText target;
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_nomenu,menu);
        return true;
    }
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth.AuthStateListener mAuthListener;

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

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users");


        //onclicklinsenter Start
        searchB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if(target.getText().toString().contains("#")){
                    Intent intent = new Intent(getApplicationContext(),ShowPosts.class);
                    //test only
                    FirebaseUser cu = FirebaseAuth.getInstance().getCurrentUser();
                    intent.putExtra("uid", (String)null);
                    intent.putExtra("hashtag", target.getText().toString());
                    Log.d(TAG, "hashtag is: " + target.getText().toString());
                    startActivity(intent);
                    return;
                }

                //search start
                //TODO: now we dont judge it, just enter result page with every one display
                if(isUserExist(target.getText().toString())){
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
                //search end
            }
        });
        //onclicklinsenter End

    }

    public boolean isUserExist(final String name){

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        String nodes;
        if(!name.contains("@"))
            nodes ="username";
        else
            nodes = "email";

            Query query = mDatabaseRef.child("users").orderByChild(nodes).equalTo(name);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(getBaseContext(),"user "+ name +" doesnt exists",Toast.LENGTH_LONG).show();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,databaseError.getMessage());
            }
        };

        query.addListenerForSingleValueEvent(eventListener);
        return false;
    }




    public void setBackWork(Toolbar tb){
        getSupportActionBar().setTitle("Search");
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }

    //change back behavior
    @Override
    public void onBackPressed(){
        startActivity(new Intent(getBaseContext(),MainActivity.class));
    }
}
