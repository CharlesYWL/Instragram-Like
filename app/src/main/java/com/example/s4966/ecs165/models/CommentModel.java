package com.example.s4966.ecs165.models;

import java.util.List;

public class CommentModel {
    private String comment;
    private String user_id;
    private List<LikeModel> likes;
    private String date_created;

    public CommentModel(String comment, String user_id, List<LikeModel> likes, String date_created) {
        this.comment = comment;
        this.user_id = user_id;
        this.likes = likes;
        this.date_created = date_created;
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

    public List<LikeModel> getLikes() {
        return likes;
    }

    public void setLikes(List<LikeModel> likes) {
        this.likes = likes;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }
}
