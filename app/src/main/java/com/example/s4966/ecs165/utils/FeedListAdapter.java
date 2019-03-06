package com.example.s4966.ecs165.utils;

import com.bumptech.glide.Glide;
import com.example.s4966.ecs165.AddressBook;
import com.example.s4966.ecs165.MainActivity;
import com.example.s4966.ecs165.R;
import com.example.s4966.ecs165.SquareImageView;
import com.example.s4966.ecs165.models.Postmodel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;
import java.util.concurrent.Semaphore;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedListAdapter extends ArrayAdapter<Postmodel> {
    private static final String TAG = "FeedListAdapter";

    private static int temp = 0;

    private int layoutResourcesNum;
    private Context myContext;
    private DatabaseReference firebaseRef;
    private StorageReference storageRef;
    private LayoutInflater myInflater;
    private String curretUserName = "";

    public FeedListAdapter(@NotNull Context context, int resource, @NotNull List<Postmodel> postmodels){
        super(context, resource, postmodels);
        myInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResourcesNum = resource;
        myContext = context;
        firebaseRef = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    static class PostViewCollection{
        CircleImageView profileImageView;
        TextView usernameTextView;
        TextView postTextView;
        SquareImageView postImageView;
        RelativeLayout likeImageContainer;
        ImageView likeImageView;
        ImageView likeImageViewLiked;
        Postmodel postmodel;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final PostViewCollection viewCollection;

        if(convertView == null){
            convertView = myInflater.inflate(layoutResourcesNum, parent, false);
            viewCollection = new PostViewCollection();

            viewCollection.usernameTextView = convertView.findViewById(R.id.post_username);
            viewCollection.postImageView = convertView.findViewById(R.id.post_image);
            viewCollection.profileImageView = convertView.findViewById(R.id.post_profile_photo);
            viewCollection.postTextView = convertView.findViewById(R.id.post_text);
            viewCollection.likeImageContainer = convertView.findViewById(R.id.like_bottom_container);
            viewCollection.likeImageView =convertView.findViewById(R.id.like_bottom_image);
            viewCollection.likeImageViewLiked = convertView.findViewById(R.id.like_bottom_image_likedStatus);

            convertView.setTag(viewCollection);
        }else{
            viewCollection = (PostViewCollection) convertView.getTag();
        }

        viewCollection.postmodel = getItem(position);
        updatePost(viewCollection);
        viewCollection.likeImageViewLiked.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                updatePostLike(viewCollection);
            }
        });
        viewCollection.likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updatePostLike(viewCollection);
                    }
                });
                /*
                if(temp == 0) {
                    viewCollection.likeImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_heart_outline_red));
                }else if(temp == 1){
                    viewCollection.likeImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_heart_outline));
                }
                temp = (temp + 1)%2;
                */
            }
        });

        return convertView;
    }

    private void updatePostLike(PostViewCollection viewCollection){


        Toast.makeText(getContext(), "enter onClick Like button", Toast.LENGTH_SHORT).show();

        // TODO testONly
        if(viewCollection.likeImageView.getVisibility() == View.VISIBLE){
            Toast.makeText(getContext(), "is visible", Toast.LENGTH_SHORT).show();
            viewCollection.likeImageViewLiked.setVisibility(View.VISIBLE);
            viewCollection.likeImageView.setVisibility(View.INVISIBLE);
        }else{
            Toast.makeText(getContext(), "not visible", Toast.LENGTH_SHORT).show();
            viewCollection.likeImageViewLiked.setVisibility(View.INVISIBLE);
            viewCollection.likeImageView.setVisibility(View.VISIBLE);
        }
    }

    public void updatePost(final PostViewCollection viewCollection){

        viewCollection.postTextView.setText(viewCollection.postmodel.getText());
        //read for post owner stuff
        firebaseRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(ds.child("uid").getValue().toString().equals(viewCollection.postmodel.getUser_id())){
                        //here we found the right users
                        Toast.makeText(getContext(), "post Updated", Toast.LENGTH_SHORT).show();
                        final String text = ds.child("username").getValue().toString();
                        ((Activity)getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewCollection.usernameTextView.setText(text);
                            }
                        });

                        //profile setting
                        if (ds.child("pictureId").exists()) {
                            final String pictureId = (String) ds.child("pictureId").getValue();
                            // TODO there is a hard image size limit, may fix it in future.
                            final long TEN_MEGABYTE = 10 * 1024 * 1024;
                            StorageReference storagePicNode = storageRef.child("pic");
                            storagePicNode.child(pictureId).getBytes(TEN_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    final Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    ((Activity)getContext()).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            viewCollection.profileImageView.setImageBitmap(bmp);
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //read for post itself
        firebaseRef.child("posts");
        firebaseRef.child(viewCollection.postmodel.getUser_id());
        firebaseRef.child(viewCollection.postmodel.getPost_id());
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // TODO there is a hard image size limit, may fix it in future.
                final long TEN_MEGABYTE = 10 * 1024 * 1024;
                StorageReference storagePicNode = storageRef.child("post_pic").child("users");
                storagePicNode.child(viewCollection.postmodel.getUser_id())
                        .child(viewCollection.postmodel.getDate_created()).getBytes(TEN_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        final Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ((Activity)getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewCollection.postImageView.setImageBitmap(bmp);
                            }
                        });
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        checkLikeButton();
    }

    /*
    * It will check if user like the post
    * TODO:do it after we figure out how to add like into post
    * */
    public void checkLikeButton(){

    }
}
