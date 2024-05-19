package com.looksmaxxing.mellow.Activities.Classroom.Models;

public class Module {
    private String moduleId;
    private String moduleName;
    private String moduleContent;
    private boolean isLocked;
    private boolean isPublished;
    private String videoUrl;

    public Module(String moduleId, String moduleName, String moduleContent, boolean isLocked, boolean isPublished, String videoUrl) {
        this.moduleId = moduleId;
        this.moduleName = moduleName;
        this.moduleContent = moduleContent;
        this.isLocked = isLocked;
        this.isPublished = isPublished;
        this.videoUrl = videoUrl;
    }

    public Module(String moduleId, String moduleName, boolean isLocked) {
        this.moduleId = moduleId;
        this.moduleName = moduleName;
        this.isLocked = isLocked;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleContent() {
        return moduleContent;
    }

    public void setModuleContent(String moduleContent) {
        this.moduleContent = moduleContent;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
