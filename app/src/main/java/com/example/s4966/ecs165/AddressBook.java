package com.example.s4966.ecs165;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import static com.google.android.gms.common.internal.Objects.equal;


public class AddressBook extends Fragment {
    private static final String TAG = "AddressBook";
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private DatabaseReference userRef,followRef;
    private FirebaseUser currentUser;
    private StorageReference storageReference;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_addressbook, container, false);


        return view;
    }


    /**
     * It's the onCreate() for this Fragment
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initEverything();
        disPlayAllFollows();
    }

    public void disPlayAllFollows(){

        Query query = followRef.child(currentUser.getUid());
        FirebaseRecyclerOptions<String> options =
                new FirebaseRecyclerOptions.Builder<String>()
                        .setQuery(query,String.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<String,FollowViewHolder>(options){
            @Override
            public FollowViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_follow_user_display,parent,false);

                return new FollowViewHolder(view);
            }

            @Override
            protected  void onBindViewHolder(final FollowViewHolder holder, int position,final String model){
                Toast.makeText(getActivity(),"fetch "+model,Toast.LENGTH_SHORT).show();
                holder.setName(model);
                holder.setPhoto(model);
                //on Click listener

            }
        };

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }
    //init
    public void initEverything(){
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        userRef.keepSynced(true);
        followRef = FirebaseDatabase.getInstance().getReference().child("follows");
        userRef.keepSynced(true);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        recyclerView = getView().findViewById(R.id.addressRecycleView);
    }

    public class FollowViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView mTextView;
        ImageView mPhoto;
        String TAG = "FollowViewHolder";
        public FollowViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            mTextView = mView.findViewById(R.id.Name);
            mPhoto = mView.findViewById(R.id.Photo);
        }

        public void setPhoto(String uid){
            //we assume user has pictureid
            //if (photoID==null)
            //    return;

            final Semaphore semaphore = new Semaphore(1);
            final long TEN_MEGABYTE = 10 * 1024 * 1024;
            StorageReference storagePicNode = storageReference.child("pic");
            storagePicNode.child(uid).getBytes(TEN_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    mPhoto.setImageBitmap(bmp);
                }
            });
        }
        public void setName(String uid){
            userRef.child(uid).child("username").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue().toString();
                    mTextView.setText(name);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            userRef.child("test").setValue("123");
            userRef.child("test").setValue(null);
        }
    }
}



