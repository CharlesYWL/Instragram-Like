package com.example.s4966.ecs165.models;

public class PostTracer {
    private String pid;
    private String uid;

    public PostTracer(String pid, String uid) {
        this.pid = pid;
        this.uid = uid;
    }

    public PostTracer(){
        this.pid = null;
        this.uid = null;
    }

    public PostTracer(Postmodel post){
        this.pid = post.getPost_id();
        this.uid = post.getUser_id();
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
