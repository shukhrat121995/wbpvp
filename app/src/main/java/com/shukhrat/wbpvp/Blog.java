package com.shukhrat.wbpvp;

public class Blog {
    private String feedback_title, feedback_description, image;
    private Boolean anonymous;
    private String date;
    private String location;
    private Boolean status;

    private String admin;
    private String admin_reply;

    public Blog(){

    }

    public Blog(String feedback_title, String feedback_description, String image, Boolean anonymous, String date, String location, Boolean status, String admin, String admin_reply) {
        this.feedback_title = feedback_title;
        this.feedback_description = feedback_description;
        this.image = image;
        this.anonymous = anonymous;
        this.date = date;
        this.location = location;
        this.status = status;
        this.admin = admin;
        this.admin_reply = admin_reply;
    }

    public String getFeedback_title() {
        return feedback_title;
    }

    public void setFeedback_title(String feedback_title) {
        this.feedback_title = feedback_title;
    }

    public String getFeedback_description() {
        return feedback_description;
    }

    public void setFeedback_description(String feedback_description) {
        this.feedback_description = feedback_description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getAdmin_reply() {
        return admin_reply;
    }

    public void setAdmin_reply(String admin_reply) {
        this.admin_reply = admin_reply;
    }
}
