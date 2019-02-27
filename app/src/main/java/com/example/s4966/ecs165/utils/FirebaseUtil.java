package com.example.s4966.ecs165.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;


import com.example.s4966.ecs165.MainActivity;
import com.example.s4966.ecs165.models.Postmodel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FirebaseUtil {
    private static final String TAG = "FirebaseUtil";

    //firebase
    private Context context;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;
    private String userID;
    private double photoUploadProgress = 0;


    //vars
    //private Context mContext;
    //private double mPhotoUploadProgress = 0;

    public FirebaseUtil(Context context){
        this.context = context;
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();
        storageRef = FirebaseStorage.getInstance().getReference();

        if(auth.getCurrentUser() != null){
            userID = auth.getCurrentUser().getUid();
        }
    }

    public void uploadNewPost(final String text, Drawable pic){
        Log.d(TAG, "upload new post");

        StorageReference storageNode = this.storageRef.
                child(FirebasePaths.FIREBASE_POSTIMAGE_STORAGE_PATH + "/" + this.userID + "/" + getTimestamp());
        Bitmap bm = ((BitmapDrawable) pic).getBitmap();
        byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);
        UploadTask uploadTask = storageNode.putBytes(bytes);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri firebaseUrl = taskSnapshot.getUploadSessionUri();

                Toast.makeText(context, "post upload success", Toast.LENGTH_SHORT).show();

                //add the new photo to 'photos' node and 'user_photos' node
                uploadPostInfoToDatabase(text, firebaseUrl.toString());

                //navigate to the main feed so the user can see their photo
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "onFailure: Photo upload failed.");
                Toast.makeText(context, "Photo upload failed ", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                if(progress - 15 > photoUploadProgress){
                    Toast.makeText(context, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                    photoUploadProgress = progress;
                }

                Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
            }
        });

    }

    private void uploadPostInfoToDatabase(String text, String url){
        Log.d(TAG, "addPhotoToDatabase: adding photo to database.");

        //String tags = StringManipulation.getTags(caption);
        String newPostKey = databaseRef.child(FirebasePaths.FIREBASE_POST_DATABASE_PATH).push().getKey();
        Postmodel post = new Postmodel();
        post.setText(text);
        post.setDate_created(getTimestamp());
        post.setImage_path(url);
        //post.setTags(tags);
        post.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        post.setPost_id(newPostKey);

        //insert into database
        //databaseRef.child(mContext.getString(R.string.dbname_user_photos))
        //        .child(FirebaseAuth.getInstance().getCurrentUser()
        //                .getUid()).child(newPostKey).setValue(post);
        databaseRef.child(FirebasePaths.FIREBASE_POST_DATABASE_PATH).child(newPostKey).setValue(post);
    }

    // timestamp used as post image id
    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }



}

class ImageManager{
    /**
     * return byte array from a bitmap
     * quality is greater than 0 but less than 100
     * @param bm bitmap
     * @param quality quality ratio out of 100
     * @return
     */
    public static byte[] getBytesFromBitmap(Bitmap bm, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }
}
