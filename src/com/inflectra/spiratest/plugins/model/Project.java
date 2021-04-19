package com.inflectra.spiratest.plugins.model;

/**
 * Represents a Project in Spira
 */
public class Project {

    public Project()
    {
    }

    public Project(Integer ProjectId, String Name) {
        this.ProjectId = ProjectId;
        this.Name = Name;
    }

    public Integer ProjectId;
    public String Name;
}
