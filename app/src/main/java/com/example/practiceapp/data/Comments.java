package com.example.practiceapp.data;

public class Comments {

    public Comments(){

    }

    private String comment;

    private String userId;

   private String userPhoto;

   private String uniqueKey;

   private String uniquePostKey;

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getUniquePostKey() {
        return uniquePostKey;
    }

    public void setUniquePostKey(String uniquePostKey) {
        this.uniquePostKey = uniquePostKey;
    }

    public Comments(String comment, String userId, String userPhoto, String uniquePostKey) {
        this.comment = comment;
        this.userId = userId;
        this.userPhoto = userPhoto;
        this.uniquePostKey = uniquePostKey;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }
}
