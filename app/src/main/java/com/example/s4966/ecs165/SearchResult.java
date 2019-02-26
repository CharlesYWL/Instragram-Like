package com.example.s4966.ecs165;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class SearchResult extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView userList;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private String target;
    private DatabaseReference userRef;
    private FirebaseRecyclerAdapter<User,SearchResult.UserViewHolder> mUserAdapter;
    //need to use for every class
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_nomenu,menu);
        return true;
    }

    @Override
    protected void onStart(){
        super.onStart();
        //start listener
        adapter.startListening();
    }
    @Override
    protected void onStop(){
        super.onStop();
        adapter.stopListening();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        //toolbar apply to all
        mToolbar=findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setBackWork(mToolbar);
        //get passedin data
        Intent intent = getIntent();
        target = intent.getStringExtra("targetName");

        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        userRef.keepSynced(true);
        //DatabaseReference userRef2 = FirebaseDatabase.getInstance().getReference().child("users");
        userList=findViewById(R.id.recyclerView);


        if(!target.contains("@"))
            DisplayAllUser(target,"username");
        else
            DisplayAllUser(target,"email");

    }

    private void DisplayAllUser(String target,String root){

        //main filter to select
        Query query = userRef.orderByChild(root).equalTo(target);
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query,User.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_search_user_display, parent, false);

                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final UserViewHolder holder, int position, final User model) {
                holder.setName(model.getUsername());
                holder.setPhoto(model.getPictureId());
                //TODO: both setPhoto and setButton
                holder.setButton_check(model.getUid());

                holder.imb.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        //TODO: follow action, add in follow Node.
                        //ToDO: need sync followed or not options
                        Toast.makeText(SearchResult.this,"Added",Toast.LENGTH_SHORT).show();
                        holder.setButton(true);

                        User.addFollow(userRef.getParent().child("follows"),model);
                        new Handler().postDelayed(new Runnable() {//delay function
                            @Override
                            public void run() {
                                startActivity(new Intent(getBaseContext(),SearchUser.class));
                            }
                        },1000);


                    }
                });
            }
        };

        linearLayoutManager = new LinearLayoutManager(this);
        userList.setLayoutManager(linearLayoutManager);
        userList.setHasFixedSize(true);
        userList.setAdapter(adapter);
    }

    public void setBackWork(Toolbar tb){
        getSupportActionBar().setTitle("Search Result");
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        View mView;
        String TAG = "UserViewHolder";
        public ImageButton imb;
        public UserViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            imb = mView.findViewById(R.id.imageButton);
        }
        public void setPhoto(String photoID){

            //Drawable photo = mView.findViewById(R.id.Photo);
        }
        public void setName(String name){
            TextView uname = mView.findViewById(R.id.Name);
            uname.setText(name);
        }
        public void setButton(boolean flag){
            Drawable grayD = getResources().getDrawable(R.drawable.gray_added);
            Drawable greenD = getResources().getDrawable(R.drawable.green_add);
            if (flag){
                imb.setImageDrawable(grayD);
                imb.setClickable(false);
            }else{
                imb.setImageDrawable(greenD);
                imb.setClickable(true);
            }
        }
        public void setButton_check(final String uid){
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("follows");
            DatabaseReference query = rootRef.child(currentUser.getUid());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){
                    boolean flag = false;
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        Toast.makeText(getApplicationContext(),"check "+ds.getValue(),Toast.LENGTH_SHORT).show();
                        if(ds.getValue()==uid)
                            flag = true;
                    }
                    if(flag) {
                        Toast.makeText(getApplicationContext(),"enter OndataChagne exists",Toast.LENGTH_SHORT).show();
                        imb.setImageDrawable(getResources().getDrawable(R.drawable.gray_added));
                        imb.setClickable(false);
                    }else {
                        Toast.makeText(getApplicationContext(),"enter OndataChagne noexists",Toast.LENGTH_SHORT).show();
                        imb.setImageDrawable(getResources().getDrawable(R.drawable.green_add));
                        imb.setClickable(true);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError){
                    Log.w(TAG,"Setbutton_check failed",databaseError.toException());
                }

            });
            query.child("test").setValue("123");
            query.child("test").setValue(null);
        }
    }
}

