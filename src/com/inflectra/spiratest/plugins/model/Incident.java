package com.inflectra.spiratest.plugins.model;

import java.util.Date;
import java.util.List;

/**
 * Represents an Incident in Spira
 */
public class Incident {
    public Integer IncidentId;
    public Integer PriorityId;
    public Integer SeverityId;
    public Integer IncidentStatusId;
    public Integer IncidentTypeId;
    public Integer OpenerId;
    public Integer OwnerId;
    public List<Integer> TestRunStepIds;
    public Integer DetectedReleaseId;
    public Integer ResolvedReleaseId;
    public Integer VerifiedReleaseId;
    public List<Integer> ComponentIds;
    public String Name;
    public String Description;
    public Date CreationDate;
    public Date StartDate;
    public Date EndDate;
    public Date ClosedDate;
    public int CompletionPercent;
    public Integer EstimatedEffort;
    public Integer ActualEffort;
    public Integer RemainingEffort;
    public Integer ProjectedEffort;
    public Date LastUpdateDate;
    public String PriorityName;
    public String SeverityName;
    public String IncidentStatusName;
    public String IncidentTypeName;
    public String OpenerName;
    public String OwnerName;
    public String ProjectName;
    public String DetectedReleaseVersionNumber;
    public String ResolvedReleaseVersionNumber;
    public String VerifiedReleaseVersionNumber;
    public boolean IncidentStatusOpenStatus;
    public Integer FixedBuildId;
    public String FixedBuildName;
    public Integer DetectedBuildId;
    public String DetectedBuildName;
    public int ProjectId;
    public int ArtifactTypeId;
    public Date ConcurrencyDate;
    public List<ArtifactCustomProperty> CustomProperties;
    public boolean IsAttachments;
    public String Tags;
}
