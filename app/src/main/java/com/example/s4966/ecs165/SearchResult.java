package com.example.s4966.ecs165;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s4966.ecs165.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.Semaphore;

import static com.google.android.gms.common.internal.Objects.equal;


public class SearchResult extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView userList;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private String target;
    private DatabaseReference userRef;
    private StorageReference storageReference;

    //need to use for every class
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_nomenu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //start listener
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        //toolbar apply to all
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setBackWork(mToolbar);
        //get passedin data
        Intent intent = getIntent();
        target = intent.getStringExtra("targetName");

        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        userRef.keepSynced(true);
        storageReference = FirebaseStorage.getInstance().getReference();
        //DatabaseReference userRef2 = FirebaseDatabase.getInstance().getReference().child("users");
        userList = findViewById(R.id.recyclerView);


        if (!target.contains("@"))
            DisplayAllUser(target, "username");
        else
            DisplayAllUser(target, "email");

    }

    private void DisplayAllUser(String target, String root) {

        //main filter to select
        Query query = userRef.orderByChild(root).equalTo(target);
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout_search_user_display for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_search_user_display, parent, false);

                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final UserViewHolder holder, int position, final User model) {
                holder.setName(model.getUsername());
                holder.setPhoto(model.getPictureId());
                holder.setButton_check(model.getUid());

                holder.imb.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if(equal(currentUser.getUid(),model.getUid())){
                            Toast.makeText(getApplicationContext(),"You cannot add yourself",Toast.LENGTH_SHORT).show();
                            return;
                        }
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
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(),ShowPosts.class);
                        intent.putExtra("uid",model.getUid());
                        startActivity(intent);
                    }
                });


            }
        };

        linearLayoutManager = new LinearLayoutManager(this);
        userList.setLayoutManager(linearLayoutManager);
        userList.setHasFixedSize(true);
        userList.setAdapter(adapter);
    }

    public void setBackWork(Toolbar tb) {
        getSupportActionBar().setTitle("Search Result");
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),SearchUser.class));
            }
        });
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;
        String TAG = "UserViewHolder";
        public ImageView imageView;
        public ImageButton imb;

        public UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            imb = mView.findViewById(R.id.imageButton);
            imageView = mView.findViewById(R.id.Photo);
        }

        public void setPhoto(String photoID) {
            final ImageView photo = mView.findViewById(R.id.Photo);
            if (photoID == null)
                return;

            final Semaphore semaphore = new Semaphore(1);
            // TODO there is a hard image size limit, may fix it in future.
            final long TEN_MEGABYTE = 10 * 1024 * 1024;
            StorageReference storagePicNode = storageReference.child("pic");
            storagePicNode.child(photoID).getBytes(TEN_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    photo.setImageBitmap(bmp);
                }
            });
        }


        public void setName(String name) {
            TextView uname = mView.findViewById(R.id.Name);
            uname.setText(name);
        }


        public void setButton(boolean flag) {
            Drawable grayD = getResources().getDrawable(R.drawable.gray_added);
            Drawable greenD = getResources().getDrawable(R.drawable.green_add);
            if (flag) {
                imb.setImageDrawable(grayD);
                imb.setClickable(false);
            } else {
                imb.setImageDrawable(greenD);
                imb.setClickable(true);
            }
        }

        public void setButton_check(final String uid) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("follows");
            DatabaseReference query = rootRef.child(currentUser.getUid());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean flag = false;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (equal(ds.getValue().toString(), uid))
                            flag = true;
                    }
                    if (flag) {
                        imb.setImageDrawable(getResources().getDrawable(R.drawable.gray_added));
                        imb.setClickable(false);
                    } else {
                        imb.setImageDrawable(getResources().getDrawable(R.drawable.green_add));
                        imb.setClickable(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "Setbutton_check failed", databaseError.toException());
                }

            });
            //not sure if it helps on onDataChange()
            query.child("test").setValue("123");
            query.child("test").setValue(null);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
    }
    @Override
    protected void onResume(){
        super.onResume();
    }

    //change back behavior
    @Override
    public void onBackPressed(){
        startActivity(new Intent(getBaseContext(),SearchUser.class));
    }
}

