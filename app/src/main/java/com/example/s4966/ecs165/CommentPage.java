package com.example.s4966.ecs165;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s4966.ecs165.models.CommentModel;
import com.example.s4966.ecs165.utils.CommentListAdapter;
import com.example.s4966.ecs165.utils.FirebaseUtil;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CommentPage extends AppCompatActivity{

    private Button SendButton;
    private Toolbar mToolbar;
    private FirebaseUtil uti;
    private TextView comment;
    private String pid,uid;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    //need to use for every class
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_nomenu,menu);
        return true;
    }

    @Override
    public void onStart(){
        super.onStart();
        //start listener
        adapter.startListening();
    }
    @Override
    public void onStop(){
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_page);
        init();
        SendButtonListener();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your recyclerview reload logic function will be here!!!
               adapter.onDataChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        displayCommentList();

    }

    public void displayCommentList(){
        Query query = uti.getDatabaseRef().child("posts").child(uid).child(pid).child("comments");
        FirebaseRecyclerOptions<CommentModel> options =
                new FirebaseRecyclerOptions.Builder<CommentModel>()
                        .setQuery(query,CommentModel.class)
                        .build();
        adapter = new CommentListAdapter(options){};
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


    }
    //when click the button
    public void SendButtonListener() {
        SendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(comment.getText().length()==0){
                    Toast.makeText(CommentPage.this, "No Comment!", Toast.LENGTH_SHORT).show();
                }else{
                    String time = getTimestamp();
                    final CommentModel commentModel = new CommentModel(comment.getText().toString(),uti.getUserID().toString(),time,uid,pid,null);
                    uti.addCommandToPost(pid,uid,commentModel);
                    comment.setText("");
                }
            }
        });
    }


    //make back Navi on tool bar works
    public void setBackWork(Toolbar tb){
        getSupportActionBar().setTitle("Comments");
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    public void init(){
        //init ToolBar
        mToolbar=findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setBackWork(mToolbar);
        //get Pid
        Intent intent=getIntent();
        pid = intent.getStringExtra("pid");//for searching stuff
        uid = intent.getStringExtra("uid");
        
        //init View
        SendButton = findViewById(R.id.send_comment_button);
        comment = findViewById(R.id.comment_view_holder);
        uti = new FirebaseUtil(this);
        recyclerView = findViewById(R.id.commentlist);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }
}
