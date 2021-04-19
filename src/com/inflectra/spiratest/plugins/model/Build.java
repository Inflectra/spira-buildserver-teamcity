package com.inflectra.spiratest.plugins.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Represents a Build in Spira
 */
public class Build {
    public Integer BuildId;
    public int BuildStatusId;
    public int ProjectId;
    public int ReleaseId;
    public String Name;
    public String Description;
    public Date LastUpdateDate;
    public Date CreationDate;
    public String BuildStatusName;
    public ArrayList<BuildSourceCode> Revisions;

    public Integer getBuildId() {
        return BuildId;
    }
    public void setBuildId(Integer buildId) {
        BuildId = buildId;
    }

    public int getBuildStatusId() {
        return BuildStatusId;
    }

    public void setBuildStatusId(int buildStatusId) {
        BuildStatusId = buildStatusId;
    }

    public int getProjectId() {
        return ProjectId;
    }

    public void setProjectId(int projectId) {
        ProjectId = projectId;
    }

    public int getReleaseId() {
        return ReleaseId;
    }

    public void setReleaseId(int releaseId) {
        ReleaseId = releaseId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Date getLastUpdateDate() {
        return LastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        LastUpdateDate = lastUpdateDate;
    }

    public Date getCreationDate() {
        return CreationDate;
    }

    public void setCreationDate(Date creationDate) {
        CreationDate = creationDate;
    }

    public String getBuildStatusName() {
        return BuildStatusName;
    }

    public void setBuildStatusName(String buildStatusName) {
        BuildStatusName = buildStatusName;
    }

    public ArrayList<BuildSourceCode> getRevisions() {
        return Revisions;
    }

    public void setRevisions(ArrayList<BuildSourceCode> revisions) {
        Revisions = revisions;
    }
}
