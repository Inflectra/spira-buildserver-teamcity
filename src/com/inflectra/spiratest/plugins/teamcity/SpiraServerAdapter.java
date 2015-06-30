/*package com.inflectra.spiratest.plugins.teamcity;

import jetbrains.buildServer.issueTracker.Issue;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserSet;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;
import jetbrains.buildServer.vcs.VcsRoot;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.inflectra.spiratest.plugins.SpiraImportExport;

/**
* Created by 
*/

/*public class SpiraServerAdapter extends BuildServerAdapter {
	
	private final SBuildServer buildServer;
	private final ProjectSettingsManager projectSettingsManager;
	private final ProjectManager projectManager ;

	public SpiraServerAdapter(SBuildServer sBuildServer, ProjectManager projectManager, ProjectSettingsManager projectSettingsManager) {
		this.projectManager = projectManager ;
		this.projectSettingsManager = projectSettingsManager ;
		this.buildServer = sBuildServer ;
	}
	
	public void init()
		{
		buildServer.addListener(this);
		}
	
@Override
public void buildStarted(SRunningBuild build) {
super.buildStarted(build);
Loggers.SERVER.info(" @@@ build started1.");
if( !build.isPersonal() )
{
   Loggers.SERVER.info(" @@@ build started2.");
}
}

@Override
public void buildFinished(SRunningBuild build) {
super.buildFinished(build);
if( !build.isPersonal() && build.getBuildStatus().isSuccessful() )
{
	Loggers.SERVER.info(" @@@ build sucessfull.");
	//Spira code
	Date date = new Date();
	List<Integer> incidentIds = new ArrayList<Integer>();
	List<String> revisions = new ArrayList<String>();
	revisions.add("rev001");
	SpiraImportExport spiraClient = new SpiraImportExport();
	spiraClient.setUrl("http://doctor/spirateam");
	spiraClient.setUserName("fredbloggs");
	spiraClient.setPassword("PleaseChange");
	try {
		spiraClient.testConnection();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	spiraClient.setProjectId(1);
	try {
		spiraClient.verifyRelease ("1.0.0.0");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	/*spiraClient.recordTestRun(1,103,1,1,date,date,1,"Testing", "Testing Test Name",1, 
			                  "Testing Acess from SOAP", "Test Stack Trace field");*/
	
	/*try {
		spiraClient.recordBuild("1.0.0.0", date, 2, "Sucessfull_TestBuild", "Sucessfull_Just testing", revisions, incidentIds);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
else if ( !build.isPersonal() && build.getBuildStatus().isFailed() )
{
	Loggers.SERVER.info(" @@@ build failed.");
}
}
}*/

