package com.example.s4966.ecs165.models;

import android.support.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CommentModel {
    private String comment;
    private String user_id;
    private String date_created;
    private String poster_id;
    private String pid;//post id
    private String cid;//commant id

    public CommentModel(String comment, String user_id, String date_created, String poster_id, String pid, String cid) {
        this.comment = comment;
        this.user_id = user_id;
        this.date_created = date_created;
        this.poster_id = poster_id;
        this.pid = pid;
        this.cid = cid;
    }
    public CommentModel(){
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("comment", comment);
        result.put("user_id", user_id);
        result.put("date_created", date_created);
        result.put("poster_id",poster_id);
        result.put("pid",pid);
        result.put("cid",cid);
        return result;
    }

    public String getPoster_id() {
        return poster_id;
    }

    public void setPoster_id(String poster_id) {
        this.poster_id = poster_id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
