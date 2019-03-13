package com.example.s4966.ecs165;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.eschao.android.widget.elasticlistview.ElasticListView;
import com.eschao.android.widget.elasticlistview.LoadFooter;
import com.eschao.android.widget.elasticlistview.OnLoadListener;
import com.eschao.android.widget.elasticlistview.OnUpdateListener;
import com.example.s4966.ecs165.models.LikeModel;
import com.example.s4966.ecs165.models.PostTracer;
import com.example.s4966.ecs165.models.Postmodel;
import com.example.s4966.ecs165.utils.FeedListAdapter;
import com.example.s4966.ecs165.utils.FirebasePaths;
import com.example.s4966.ecs165.utils.FirebaseUtil;
import com.example.s4966.ecs165.utils.ProfilePostsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ShowPosts  extends AppCompatActivity implements OnUpdateListener, OnLoadListener {
    private Toolbar mToolbar;
    private static String TAG="ShowPosts Class";
    private ElasticListView listView;
    private FeedListAdapter adapter;
    private ArrayList<Postmodel> posts;
    private String uid;
    private static final int NUM_GRID_COLUMNS = 3;
    private ArrayList<String> followingUsers; // String is their user_id
    private String hashtag;

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent();
        this.uid=myIntent.getStringExtra("uid");
        if(this.uid == null){
            String tagTemp = myIntent.getStringExtra("hashtag");
            Log.d(TAG, "Tag passed in is: " + tagTemp);
            String tag = tagTemp.split("#")[1];
            this.hashtag = tag.split(" ")[0].toLowerCase();
            Log.d(TAG, "the tag we get is: "+ this.hashtag);
        }else{
            this.hashtag = null;
        }
        Log.d(TAG, "uid is " + uid);
        setContentView(R.layout.activity_showposts);
        posts = new ArrayList<>();

        mToolbar=findViewById(R.id.showposts_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setBackWork(mToolbar);
        followingUsers = new ArrayList<>();

        listView = findViewById(R.id.personal_feed_listView);
        listViewInit();
        displayPosts();


    }

    private  void displayPosts(){

        clearData();
        if(this.uid != null) {
            getUserPosts();
        }else {
            getSearchPosts();
        }
    }


    private void clearData(){
        posts.clear();
        followingUsers.clear();
    }

    private void getUserPosts(){
        Log.d(TAG, "get following users");

        followingUsers.add(this.uid);

        getPosts();

    }

    private void getSearchPosts(){
        Log.d(TAG, "get into search posts based on tag, listen for " + FirebasePaths.FIREBASE_HASHTAG_PATH + "/" + this.hashtag);
        FirebaseUtil firebaseUtil = new FirebaseUtil(ShowPosts.this);
        Query query = firebaseUtil.getDatabaseRef()
                .child(FirebasePaths.FIREBASE_HASHTAG_PATH)
                .child(this.hashtag);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<PostTracer> postTracers = new ArrayList<>();
                for(DataSnapshot singleNode : dataSnapshot.getChildren()){
                    PostTracer postTracer = new PostTracer();
                    postTracer.setPid((String)singleNode.child("pid").getValue());
                    postTracer.setUid((String)singleNode.child("uid").getValue());
                    postTracers.add(postTracer);
                    Log.d(TAG, "\tadd one tracer");
                }
                Log.d(TAG, "get tracers length " + Integer.toString(postTracers.size()));
                getPostsViaTracers(postTracers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "add post tracers failed");
            }
        });
    }

    private void getPostsViaTracers(final ArrayList<PostTracer> tracers){
        FirebaseUtil firebaseUtil = new FirebaseUtil(ShowPosts.this);
        if(tracers.size() == 0){
            presentPostList();
            return;
        }
        Query query = firebaseUtil.getDatabaseRef()
                .child(FirebasePaths.FIREBASE_POST_DATABASE_PATH)
                .child(tracers.get(0).getUid())
                .child(tracers.get(0).getPid());
        tracers.remove(0);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Postmodel newPost = new Postmodel();
                Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();

                newPost.setText(objectMap.get(getString(R.string.field_text)).toString());
                //newPost.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                newPost.setPost_id(objectMap.get(getString(R.string.field_pid)).toString());
                newPost.setUser_id(objectMap.get(getString(R.string.field_uid)).toString());
                newPost.setDate_created(objectMap.get(getString(R.string.field_date)).toString());
                newPost.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                Log.d(TAG, "getPhotos: photo: " + newPost.getPost_id());
                        /*
                        List<CommentModel> commentsList = new ArrayList<CommentModel>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()){
                            Map<String, Object> object_map = (HashMap<String, Object>) dSnapshot.getValue();
                            Comment comment = new Comment();
                            comment.setUser_id(object_map.get(getString(R.string.field_user_id)).toString());
                            comment.setComment(object_map.get(getString(R.string.field_comment)).toString());
                            comment.setDate_created(object_map.get(getString(R.string.field_date_created)).toString());
                            commentsList.add(comment);

                        }

                        newPost.setComments(commentsList);
                        */
                posts.add(newPost);
                getPostsViaTracers(tracers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getPosts(){
        Log.d(TAG, "getting following users' posts");
        FirebaseUtil firebaseUtil = new FirebaseUtil(ShowPosts.this);
        for(int i = 0; i < followingUsers.size(); i++){
            Query query = firebaseUtil.getDatabaseRef()
                    .child(FirebasePaths.FIREBASE_POST_DATABASE_PATH)
                    .child(followingUsers.get(i))
                    //.orderByChild(getString())
                    //.equalTo(followingUsers.get(i))
                    ;

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                        Postmodel newPost = new Postmodel();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        newPost.setText(objectMap.get(getString(R.string.field_text)).toString());
                        //newPost.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        newPost.setPost_id(objectMap.get(getString(R.string.field_pid)).toString());
                        newPost.setUser_id(objectMap.get(getString(R.string.field_uid)).toString());
                        newPost.setDate_created(objectMap.get(getString(R.string.field_date)).toString());
                        newPost.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        Log.d(TAG, "getPhotos: photo: " + newPost.getPost_id());
                        /*
                        List<CommentModel> commentsList = new ArrayList<CommentModel>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()){
                            Map<String, Object> object_map = (HashMap<String, Object>) dSnapshot.getValue();
                            Comment comment = new Comment();
                            comment.setUser_id(object_map.get(getString(R.string.field_user_id)).toString());
                            comment.setComment(object_map.get(getString(R.string.field_comment)).toString());
                            comment.setDate_created(object_map.get(getString(R.string.field_date_created)).toString());
                            commentsList.add(comment);

                        }

                        newPost.setComments(commentsList);
                        */
                        posts.add(newPost);
                    }
                    /*
                    if(count >= mFollowing.size() - 1){
                        //display the photos
                        displayPhotos();
                    }*/
                    presentPostList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void presentPostList(){
        if(posts != null){

            try{

                //sort for newest to oldest
                Collections.sort(posts, new Comparator<Postmodel>() {
                    public int compare(Postmodel o1, Postmodel o2) {
                        return o2.getDate_created().compareTo(o1.getDate_created());
                    }
                });

                //we want to load 10 at a time. So if there is more than 10, just load 10 to start
                /*
                int iterations = mPhotos.size();
                if(iterations > 10){
                    iterations = 10;
                }

//
                //resultsCount = 0;
                for(int i = 0; i < iterations; i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                    resultsCount++;
                    Log.d(TAG, "displayPhotos: adding a photo to paginated list: " + mPhotos.get(i).getPhoto_id());
                }
                */

                adapter = new FeedListAdapter(ShowPosts.this, R.layout.layout_post_view, posts);
                listView.setAdapter(adapter);
                //adapter = new MainFeedListAdapter(getActivity(), R.layout.layout_mainfeed_listitem, mPaginatedPhotos);
                //mListView.setAdapter(adapter);

                // Notify update is done
                listView.notifyUpdated();

            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage() );
            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage() );
            }
        }
        ////////////////

    }

    private void listViewInit(){
        listView.setHorizontalFadingEdgeEnabled(true);
        listView.setAdapter(adapter);
        listView.enableLoadFooter(true).getLoadFooter().setLoadAction(LoadFooter.LoadAction.RELEASE_TO_LOAD);
        //listView.setOnUpdateListener(this).setOnLoadListener(this);
    }

    /*
    private void setupPostGrid(){
        Log.d("profile activity: setup post grid", "Setting up post grid.");

        // first get all posts
        posts = new ArrayList<>();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference
                .child(FirebasePaths.FIREBASE_POST_DATABASE_PATH)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleNode : dataSnapshot.getChildren()){
                    Postmodel post = new Postmodel();
                    try{
                        post.setText((String)singleNode.child(getString(R.string.field_text)).getValue());
                        post.setDate_created((String) singleNode.child(getString(R.string.field_date)).getValue());
                        post.setImage_path((String) singleNode.child(getString(R.string.field_image_path)).getValue());
                        post.setPost_id((String) singleNode.child(getString(R.string.field_pid)).getValue());
                        post.setUser_id((String) singleNode.child(getString(R.string.field_uid)).getValue());
                        ArrayList<LikeModel> likes = new ArrayList<>();
                        for(DataSnapshot likeNode : singleNode.child(getString(R.string.field_like_node)).getChildren()){
                            LikeModel likeModel = new LikeModel((String)likeNode.getValue());
                            likes.add(likeModel);
                        }
                        post.setLikes(likes);
                        Log.d("Profile set up grid", "post image_path is: " + post.getImage_path());


                        posts.add(post);
                        setupPostGrid2(posts);

                    }catch(NullPointerException e){
                        Log.e("Profile set up grid", e.getMessage());
                        Toast.makeText(ShowPosts.this, "setup grid post failure", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Profile set up grid", "retrieving post info failed");
                Toast.makeText(ShowPosts.this, "retrieving posts failed", Toast.LENGTH_LONG);
            }
        });

    }

    private void setupPostGrid2(ArrayList<Postmodel> posts){
        // then get storageImagePath
        final ArrayList<String> imageStoragePaths = new ArrayList<>();
        for(Postmodel post : posts){
            imageStoragePaths.add(post.getStorageImagePath());
        }
        Log.d("Profile Activity", "number of posts gathered: " + Integer.toString(imageStoragePaths.size()));

        // finally put image in grid in adapter TODO
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        ProfilePostsAdapter adapter = new ProfilePostsAdapter(ShowPosts.this, R.layout.layout_grid_view, imageStoragePaths);
        gridView.setAdapter(adapter);

    }*/


    public void setBackWork(Toolbar tb){
        getSupportActionBar().setTitle("Personal Posts");
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onUpdate() {
        Log.d(TAG, "ElasticListView: updating list view...");

        getUserPosts();
    }

    @Override
    public void onLoad() {
        Log.d(TAG, "ElasticListView: loading...");

        // Notify load is done
        listView.notifyLoaded();
    }

}
