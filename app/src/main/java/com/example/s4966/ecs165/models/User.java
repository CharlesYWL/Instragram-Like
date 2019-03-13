package com.example.s4966.ecs165.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.s4966.ecs165.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.*;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import static android.provider.Settings.System.getString;

public class User {
    public enum GENDER {MALE, FEMALE,UNKNOWN}

    private String uid ;
    private String username;
    private String bio;
    private String email;
    private GENDER gender;
    private Drawable picture;
    private String TAG = "User class";
    //pictureId should always be the same with uid, if exists a picture;
    private String pictureId;
    private String fcmtoken;

    public String getFcmtoken() {
        return fcmtoken;
    }

    public void setFcmtoken(String fcmtoken) {
        this.fcmtoken = fcmtoken;
    }
    /**
     * Constructor for the User class.
     * @param usernameStr username string
     * @param bioStr bio info string
     * @param emailStr email string
     * @param gen gender
     * @param pic: If there is no picture for the user, please pass null
     */

    /**
     *
     * @param id user id, get from user.getUid()
     * @param usernameStr username string
     * @param bioStr bio info string
     * @param emailStr email string
     * @param gen gender
     * @param pic picture drawable class, if not exist, please specify null.
     */
    public User(String id, String usernameStr, String bioStr, String emailStr, GENDER gen, Drawable pic){
        uid = id;
        username = usernameStr;
        bio = bioStr;
        email = emailStr;
        gender = gen;
        picture = pic;
        pictureId = null;
    }
    public User(){}

    public void setUid(String id){
        uid = id;
    }

    public void setUsername(String name){username = name;}

    public void setBio(String tbio){bio = tbio;}
    public void setEmail(String mail){email = mail;}
    public void setGender(User.GENDER g){gender = g;}
    public void setPicture(Drawable pic){ picture=pic; }
    public void setPictureId (String pic){pictureId=pic;}

    public String getUsername(){
        return username;
    }


    public boolean hasUID(){
        return uid != "";
    }

    public Drawable getPicture(){
        return picture;
    }

    public String getUid(){
        return uid;
    }

    public String getBio(){
        return bio;
    }

    public String getEmail(){return email;}

    public GENDER getGender(){return gender;}

    public String getPictureId(){return pictureId;}


    public static void addFollow(DatabaseReference databaseFollowsNode, User follow_target){
        String TAG = "void addFollow(User follow, User followed)";
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if ( !follow_target.hasUID()){
            Log.e(TAG, "follow or followed user has no uid,");
            System.exit(1);
        }

        //Map<String, Object> result = new HashMap<>();
        // should be foreign key
        //result.put("follower", currentUser.getUid());
        //result.put("follow_target", follow_target.getUid());
        databaseFollowsNode.child(currentUser.getUid()).push().setValue(follow_target.getUid());
    }

    // TODO: later we need to delete related picture in database as well
    public static void deleteUser(DatabaseReference databaseUserNode, User user){
        databaseUserNode.child(user.getUid()).removeValue();
    }

    public static void addUser(DatabaseReference databaseUserNode, StorageReference storagePicNode, User user){
        if (!user.hasUID()) {
            String id = databaseUserNode.push().getKey();
            user.setUid(id);
        }
        user.toFireBase(databaseUserNode, storagePicNode);
    }

    public static void updataUser(DatabaseReference databaseUserNode, StorageReference storagePicNode, User user){
        if(!user.hasUID()){
            String id = databaseUserNode.push().getKey();
            user.setUid(id);
        }
        user.updataFireBase(databaseUserNode,storagePicNode);
    }


    //cannot run for some reasons
    //TODO fix it
    public static void getUserFromFireBase(String Uid,final User target){
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        DatabaseReference databaseUserNode = FirebaseDatabase.getInstance().getReference();
        databaseUserNode.child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User us = dataSnapshot.getValue(User.class);
                target.setUsername(us.getUsername());
                target.setBio(us.getBio());
                target.setEmail(us.getEmail());
                target.setGender(us.getGender());
                target.setPictureId(us.getPictureId());
                target.setUid(us.getUid());

                if (dataSnapshot.child("pictureId").exists()) {
                    final Semaphore semaphore = new Semaphore(1);
                    final String pictureId = (String) dataSnapshot.child("pictureId").getValue();
                    // TODO there is a hard image size limit, may fix it in future.
                    final long TEN_MEGABYTE = 10 * 1024 * 1024;
                    StorageReference storagePicNode = storageReference.child("pic");
                    storagePicNode.child(pictureId).getBytes(TEN_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            target.setPicture(new BitmapDrawable(bmp));
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {//donothing
            }
        });

    }


    private void toFireBase(DatabaseReference databaseUserNode, StorageReference storageImageNode){
        if(!hasUID()){
            Log.e(TAG+=" toFireBase", "try to add to firebase when no uid is assigned.");
            System.exit(1);
        }

        if(picture != null) {
            pictureId = uid;
            Bitmap bitmap = ((BitmapDrawable)picture).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = storageImageNode.child(pictureId).putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG += " toFireBase function", "upload picture bytes failure.");
                    System.exit(1);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    return;
                }
            });
        }
        Map<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("bio", bio);
        result.put("email", email);
        result.put("gender", gender);
        if(pictureId != null){
            result.put("pictureId", pictureId);
        }
        databaseUserNode.child(uid).setValue(result);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(Uri.parse("gs://pic/"+pictureId))
                .build();
        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Log.d(TAG, "User profile update.");
            }
        });
    }

    private void updataFireBase(DatabaseReference databaseUserNode, StorageReference storageImageNode){
        if(!hasUID()){
            Log.e(TAG+=" toFireBase", "try to add to firebase when no uid is assigned.");
            System.exit(1);
        }

        if(picture != null) {
            pictureId = uid;
            Bitmap bitmap = ((BitmapDrawable)picture).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = storageImageNode.child(pictureId).putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG += " toFireBase function", "upload picture bytes failure.");
                    System.exit(1);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    return;
                }
            });
        }

        Map<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("bio", bio);
        result.put("email", email);
        result.put("gender", gender);
        if(pictureId != null){
            result.put("pictureId", pictureId);
        }
        databaseUserNode.child(uid).updateChildren(result);
        //test for updataProfile
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(Uri.parse("gs://pic/"+pictureId))
                .build();
        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Log.d(TAG, "User profile update.");
            }
        });

    }

    //used for init uid for profile
    public static void updataUid(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(currentUser.getUid()).child("uid").setValue(currentUser.getUid());
    }
    public static void updateFMCToken(){
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        // [START retrieve_current_token]
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("updateFMCToken", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                         mDatabase.child("users").child(currentUser.getUid()).child("fcmtoken").setValue(token);
                }
            });
    }
}

