package com.looksmaxxing.mellow.Activities.Classroom.Models;

import java.util.List;

public class Set {
    private String setId;
    private String setName;
    private List<Module> modules;

    public Set(String setId, String setName, List<Module> modules) {
        this.setId = setId;
        this.setName = setName;
        this.modules = modules;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }
}