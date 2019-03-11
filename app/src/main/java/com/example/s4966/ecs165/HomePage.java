package com.example.s4966.ecs165;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eschao.android.widget.elasticlistview.ElasticListView;
import com.eschao.android.widget.elasticlistview.OnLoadListener;
import com.eschao.android.widget.elasticlistview.OnUpdateListener;
import com.eschao.android.widget.elasticlistview.LoadFooter;
import com.example.s4966.ecs165.models.Postmodel;
import com.example.s4966.ecs165.utils.FeedListAdapter;
import com.example.s4966.ecs165.utils.FirebasePaths;
import com.example.s4966.ecs165.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class HomePage extends Fragment implements OnUpdateListener, OnLoadListener {

    private String TAG = "HomePage";
    private ElasticListView mainFeedListView;
    private FeedListAdapter adapter;
    private FirebaseUtil firebaseUtil;

    // data section
    private ArrayList<Postmodel> postmodels;
    private ArrayList<String> followingUsers; // String is their user_id
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_homepage, container, false);
        followingUsers = new ArrayList<>();
        firebaseUtil = new FirebaseUtil(getActivity());
        mainFeedListView = (ElasticListView) view.findViewById(R.id.main_feed_listView);


        postmodels = new ArrayList<>();
        postmodels.add(new Postmodel());
        postmodels.add(new Postmodel());
        postmodels.add(new Postmodel());
        postmodels.add(new Postmodel());
        mainFeedlistInit();
        displayPosts();

        return view;
    }

    private void mainFeedlistInit(){
        mainFeedListView.setHorizontalFadingEdgeEnabled(true);
        mainFeedListView.setAdapter(adapter);
        mainFeedListView.enableLoadFooter(true).getLoadFooter().setLoadAction(LoadFooter.LoadAction.RELEASE_TO_LOAD);
        mainFeedListView.setOnUpdateListener(this).setOnLoadListener(this);
    }

    private void displayPosts(){
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser==null){
            startActivity(new Intent(getContext(),LoginActivity.class));
        }
        getFollowingUsersPosts();

    }

    private void getFollowingUsersPosts(){
        Log.d(TAG, "get following users");

        clearData();

        followingUsers.add(firebaseUtil.getUserID());
        Query query = firebaseUtil.getDatabaseRef()
                .child(FirebasePaths.FIREBASE_FOLLOWING_PATH)
                .child(firebaseUtil.getUserID());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "getFollowing: found user: " + singleSnapshot
                            .getValue());

                    followingUsers.add(singleSnapshot
                            .getValue().toString());
                }

                getPosts();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "get user followers fail", Toast.LENGTH_LONG);
            }

        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private void getPosts(){
        Log.d(TAG, "getting following users' posts");
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
                        postmodels.add(newPost);
                    }
                    presentPostList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void presentPostList(){
        if(postmodels != null){
            try{
                //sort for newest to oldest
                Collections.sort(postmodels, new Comparator<Postmodel>() {
                    public int compare(Postmodel o1, Postmodel o2) {
                        return o2.getDate_created().compareTo(o1.getDate_created());
                    }
                });


                adapter = new FeedListAdapter(getActivity(), R.layout.layout_post_view, postmodels);
                mainFeedListView.setAdapter(adapter);
                //adapter = new MainFeedListAdapter(getActivity(), R.layout.layout_mainfeed_listitem, mPaginatedPhotos);
                //mListView.setAdapter(adapter);

                // Notify update is done
                mainFeedListView.notifyUpdated();

            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage() );
            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage() );
            }
        }
        ////////////////

    }

    private void clearData(){
        if(! postmodels.isEmpty()){
            postmodels.clear();
        }

        if(! followingUsers.isEmpty()){
            followingUsers.clear();
        }
    }

    @Override
    public void onUpdate() {
        Log.d(TAG, "ElasticListView: updating list view...");

        getFollowingUsersPosts();
    }

    @Override
    public void onLoad() {
        Log.d(TAG, "ElasticListView: loading...");

        // Notify load is done
        mainFeedListView.notifyLoaded();
    }

}
