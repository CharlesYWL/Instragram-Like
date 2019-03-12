package com.example.s4966.ecs165.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.s4966.ecs165.R;
import com.example.s4966.ecs165.models.CommentModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.Semaphore;

/*
* This is viewholder
*
*
* */
public class CommentViewHolder extends RecyclerView.ViewHolder{
    View mView;
    TextView commentview,nameview,timeview;
    ImageView mPhoto;
    String TAG = "CommentViewHolder";
    public CommentViewHolder(View itemView){
        super(itemView);
        mView = itemView;
        mPhoto = mView.findViewById(R.id.profilePhoto);
        commentview = mView.findViewById(R.id.comment);
        nameview = mView.findViewById(R.id.name);
        timeview = mView.findViewById(R.id.time);
    }
    public void setmPhoto(CommentModel model){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        final long TEN_MEGABYTE = 10 * 1024 * 1024;
        StorageReference storagePicNode = storageReference.child("pic");
        storagePicNode.child(model.getUser_id()).getBytes(TEN_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                mPhoto.setImageBitmap(bmp);
            }
        });
    }


    public void setString(CommentModel model){
        commentview.setText(model.getComment());
        timeview.setText(model.getDate_created());
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
        userRef.child(model.getUser_id()).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue().toString();
                nameview.setText(name);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"getString: addValueEventListener");
            }
        });
    }
}
