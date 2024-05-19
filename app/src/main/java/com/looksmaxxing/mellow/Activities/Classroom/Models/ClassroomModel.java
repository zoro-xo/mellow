package com.looksmaxxing.mellow.Activities.Classroom.Models;

public class ClassroomModel {
    private String title;
    private String description;
    private String ImageUrl;
    private String courseId;
    private String selectedAccess;
    private String sets;

    


    private int progress;

    public ClassroomModel(String title, String description, String imageUrl, String courseId, String selectedAccess, String sets, int progress, boolean isPublished) {
        this.title = title;
        this.description = description;
        ImageUrl = imageUrl;
        this.courseId = courseId;
        this.selectedAccess = selectedAccess;
        this.sets = sets;
        this.progress = progress;
        this.isPublished = isPublished;
    }

    public String getSets() {
        return sets;
    }

    public void setSets(String sets) {
        this.sets = sets;
    }

    public ClassroomModel(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getSelectedAccess() {
        return selectedAccess;
    }

    public void setSelectedAccess(String selectedAccess) {
        this.selectedAccess = selectedAccess;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    private boolean isPublished;




    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
