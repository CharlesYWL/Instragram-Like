package com.example.s4966.ecs165;

import android.util.Log;

import com.google.firebase.database.*;
import java.util.HashMap;
import java.util.Map;

public class User {
    public enum GENDER {MALE, FEMALE}

    private String uid;
    private String username;
    private String bio;
    private String email;
    private GENDER gender;

    public String getUsername(){
        return username;
    }

    public static void addFollow(DatabaseReference databaseFollowsNode, User follow, User followed){
        String TAG = "void addFollow(User follow, User followed)";
        if (!follow.hasUID() || !followed.hasUID()){
            Log.e(TAG, "follow or followed user has no uid,");
            System.exit(1);
        }
        Map<String, Object> result = new HashMap<>();
        // should be foreign key
        result.put("follow", follow.getUid());
        result.put("follow username", follow.getUsername());
        result.put("followed", followed.getUsername());
        databaseFollowsNode.push().setValue(result);
    }

    public static void deleteUser(DatabaseReference databaseUserNode, User user){
        databaseUserNode.child(user.getUid()).removeValue();
    }

    public static void addUser(DatabaseReference databaseUserNode, User user){
        if (!user.hasUID()) {
            String id = databaseUserNode.push().getKey();
            user.setUid(id);
        }
        user.toFireBase(databaseUserNode);
    }

    public User(String id, String usernameStr, String bioStr, String emailStr, GENDER gen){
        uid = id;
        username = usernameStr;
        bio = bioStr;
        email = emailStr;
        gender = gen;
    }

    public boolean hasUID(){
        return uid != "";
    }

    public void setUid(String id){
        uid = id;
    }

    public String getUid(){
        return uid;
    }

    public User(String usernameStr, String bioStr, String emailStr, GENDER gen){
        uid = "";
        username = usernameStr;
        bio = bioStr;
        email = emailStr;
        gender = gen;
    }

    public void toFireBase(DatabaseReference database){
        Map<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("bio", bio);
        result.put("email", email);
        result.put("gender", gender);
        database.child(uid).setValue(result);
    }



}
