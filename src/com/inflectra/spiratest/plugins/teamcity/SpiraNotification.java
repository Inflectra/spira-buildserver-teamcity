package com.inflectra.spiratest.plugins.teamcity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.inflectra.spiratest.plugins.SpiraImportExport;

import jetbrains.buildServer.Build;
import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.buildLog.LogMessage;
import jetbrains.buildServer.serverSide.mute.MuteInfo;
import jetbrains.buildServer.serverSide.problems.BuildProblemInfo;
import jetbrains.buildServer.tests.TestName;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;
import jetbrains.buildServer.vcs.VcsRoot;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.vcs.SVcsModification;

@SuppressWarnings("deprecation")

/**
 * This defines the 'SpiraNotification' class, which contains methods that are
 * called for TeamCity when it wants to send a notification
 * 
 * @author		Bruno Gruber - Inflectra Corporation
 * @version		1.0.0 - June 2015
 *
 */

public class SpiraNotification implements Notificator {

private static final String TYPE = "spiraNotifier";
private static final String TYPE_NAME = "Spira Notifier for TeamCity";
private static final String SPIRA_URL = "SpriraURL";
private static final String SPIRA_USERNAME = "SpriraUser";
private static final String SPIRA_PASSWORD = "SpriraPassw";
private static Pattern incidentPattern = Pattern.compile( "\\[IN:(\\d+)\\]" );

static String Spira_dir = System.getenv("TEAMCITY_DATA_PATH"); 

private static final String GLOBAL_CONFIG_PATH = (Spira_dir + "\\config\\SpiraGlobalOutput.txt");
private static final String LOCAL_CONFIG_PATH =  (Spira_dir + "\\config\\SpiraOutput.txt");

String TcProjectID, spiraURL, spiraUsername, spiraPassword, spiraProjectVers;
Integer ProjectID, buildStatus;
int flag =0;

public SpiraNotification(NotificatorRegistry notificatorRegistry) {
	
	ArrayList<UserPropertyInfo> userProps = new ArrayList<UserPropertyInfo>();	
    notificatorRegistry.register(this, userProps);
}

//Takes a string from file and makes it a list 
public ArrayList<String> CreateProjectList(String path){
	
	Loggers.SERVER.info("Spira Plugin :: Entering CreateProjectList");

	  flag = 0; //watching for exceptions (usually file not found)
	  String line = null;
	  ArrayList<String> ProjectList = new ArrayList<String>();

    try {
	  FileReader reader = new FileReader(path); 
	  BufferedReader reading = new BufferedReader(reader);  
	  StringTokenizer st = null;
	  
	  while ((line = reading.readLine()) != null) {  
	        st = new StringTokenizer(line, "|*|");  
	        String data = null;  
	       while (st.hasMoreTokens()){  
	         data = st.nextToken();  
	         ProjectList.add(data);
	       }  
	 }  
	    reading.close();  
	    reader.close();
    }catch (IOException e) {
		  flag = 1;
		  ProjectList = null;
     }
    return ProjectList;
}

// Gets all the necessary info that will be send in a Notification Event
public void getInfo(SUser user) throws NumberFormatException, IOException {
	
	Loggers.SERVER.info("Spira Plugin :: Entering getInfo for user: '" + user.getUsername() + "'");
		
	//Read the Spira global configuration from the file
	String line = null; 
	try
	{
	  FileReader reader = new FileReader(GLOBAL_CONFIG_PATH); 
	  BufferedReader reading = new BufferedReader(reader);  
	  StringTokenizer st = null;
	  while ((line = reading.readLine()) != null)
	  {  
        st = new StringTokenizer(line, "|*|");  
        String data = null;  
        while (st.hasMoreTokens())
        {  
           data = st.nextToken();  
           spiraURL = data;
           data = st.nextToken();  
           spiraUsername = data;  
           data = st.nextToken();  
           spiraPassword = data; 
        }  
	  }  
	  reading.close();  
	  reader.close();
	}
	catch (IOException e)
	{
		//Log the error and exit
		Loggers.SERVER.error("Unable to access Spira global configuration at path '" + GLOBAL_CONFIG_PATH + "'");
		Loggers.SERVER.error(e);
		return;
	} 
		
	if (spiraURL != null && !spiraURL.isEmpty() && spiraUsername != null && !spiraUsername.isEmpty())
	{
		Loggers.SERVER.info("Spira Plugin :: got Spira URL = " + spiraURL + " configured for TeamCity user '" + user.getUsername() + "'");
		
		ArrayList<String> ProjectList = CreateProjectList(LOCAL_CONFIG_PATH);
	        
	    if (flag ==1){
		    Loggers.SERVER.warn(":: Spira Plugin :: Project information could not be loaded from file '" + LOCAL_CONFIG_PATH + "'. Action: Project fields set to 0");
		    ProjectID = 0;
		    spiraProjectVers = "0.0.0";	
		}
		else
		{
			for (int n=0; n<=ProjectList.size(); n=n+1) {
		 	     String piece = ProjectList.get(n);
		             if (piece.equals(TcProjectID)){
		            	 try {
		            		 ProjectID = Integer.parseInt(ProjectList.get(n+1)); 
		            	 }catch (NumberFormatException e){
		            			Loggers.SERVER.error(":: Spira Plugin :: Bad projectID format from file. Action: projectID changed to 0");
		            			ProjectID = 0;
		            			e.printStackTrace();	
		            	 }	
		            	 spiraProjectVers = ProjectList.get(n+2);
		            	 break; 	
		             }            
		             Loggers.SERVER.info(":: Spira Plugin :: ProjectID not found from file. Action: projectID and Release # considered 0");
		             ProjectID = 0;
		             spiraProjectVers = "0.0.0";
		    }
		}
	}
	Loggers.SERVER.info("Spira Plugin :: Exiting getInfo");
}

// Set connection with Spira Server and Send a notification
public void setSendInfo(SRunningBuild runningBuild){
	
	Loggers.SERVER.info("Spira Plugin :: Entering setSendInfo");

	Set<Integer> incidentIds = new HashSet<Integer>();
	List<String> revisions = new ArrayList<String>();
	SpiraImportExport spiraClient = new SpiraImportExport();
	
	spiraClient.setUrl(spiraURL);	
	spiraClient.setUserName(spiraUsername);
	spiraClient.setApiKey(spiraPassword);
	
	Loggers.SERVER.info("Spira Plugin :: Sending Notification to Spira at URL: " + spiraURL);
	
	Date date = runningBuild.getStartDate();
	String name = runningBuild.getFullName() + " #" + runningBuild.getBuildNumber() ;
	runningBuild.getProjectId();
	List<LogMessage> messages = runningBuild.getBuildLog().getMessages();	
	List<SVcsModification> modifications = runningBuild.getChanges(SelectPrevBuildPolicy.SINCE_LAST_BUILD , true);

	for (SVcsModification modification : modifications){
		String revision = modification.getVersion();
		revision = revision.split("_" )[0];
		Loggers.SERVER.info("Spira Plugin :: Adding revision " + revision + " from version " +
				modification.getVersion() );
		revisions.add(revision);
		Matcher m  = incidentPattern.matcher( modification.getDescription() );
		while( m.find() )
		{
			incidentIds.add( new Integer( m.group( 1 ) ) );
			Loggers.SERVER.info("Spira Plugin :: Adding incident " + m.group() );
		}
	}
	
	StringBuilder sb = new StringBuilder();
	
	for (LogMessage message : messages){
		sb.append(message.toString());
		sb.append(System.getProperty("line.separator"));
	}
	
	String description = sb.toString();
	
	try {
		spiraClient.testConnection();
	} catch (Exception e){
		Loggers.SERVER.error(e);
		e.printStackTrace();
	}
	
	spiraClient.setProjectId(ProjectID);
	
	try {
		spiraClient.verifyRelease (spiraProjectVers);
	} catch (Exception e) {
		
		Loggers.SERVER.error(e);
		e.printStackTrace();
	}
	
	try {
		spiraClient.recordBuild(spiraProjectVers, date, buildStatus, name, description, revisions,
				new ArrayList( incidentIds ) );
	} catch (Exception e) {
		Loggers.SERVER.error(e);
		e.printStackTrace();
	}
	Loggers.SERVER.info("Spira Plugin :: Exiting setSendInfo");
}

  
@Override
public String getDisplayName() {
    return TYPE_NAME;
}

@Override
public String getNotificatorType() {
    return TYPE;
}

@Override
// This method is called by TeamCity if a Build fails
public void notifyBuildFailed(SRunningBuild runningBuild, Set<SUser> userInfo) {
	
	Loggers.SERVER.info("Spira Plugin :: Build Failed Notification Requested by TeamCity for "+runningBuild.getProjectId());
	TcProjectID = runningBuild.getProjectId();
	buildStatus = 1; // Failed Status
	
	for (SUser user : userInfo) {
		try {
			getInfo(user);
		} catch (NumberFormatException e) {
			Loggers.SERVER.error(e);
			e.printStackTrace();
		} catch (IOException e) {
			Loggers.SERVER.error(e);
			e.printStackTrace();
		}
	}
	setSendInfo(runningBuild);
}

@Override
//This method is called by TeamCity if a Build couldn't start 
public void notifyBuildFailedToStart(SRunningBuild runningBuild, Set<SUser> userInfo) {
	Loggers.SERVER.info("Spira Plugin :: Build Failed to Start (Aborted) Notification Requested by TeamCity for "+runningBuild.getProjectId());
	buildStatus = 4; //Aborted
	
	for (SUser user : userInfo) {
		try {
			getInfo(user);
		} catch (NumberFormatException e) {
			Loggers.SERVER.error(e);
			e.printStackTrace();
		} catch (IOException e) {
			Loggers.SERVER.error(e);
			e.printStackTrace();
		}
	}
	setSendInfo(runningBuild);
}

@Override
//This method is called by TeamCity if a Build is unstable 
public void notifyBuildProbablyHanging(SRunningBuild runningBuild, Set<SUser> userInfo) {
	Loggers.SERVER.info("Spira Plugin :: Build Hanging (Unstable) Notification Requested by TeamCity for "+runningBuild.getProjectId());
	buildStatus = 3; // Unstable
	
	for (SUser user : userInfo) {
		try {
			getInfo(user);
		} catch (NumberFormatException e) {
			Loggers.SERVER.error(e);
			e.printStackTrace();
		} catch (IOException e) {
			Loggers.SERVER.error(e);
			e.printStackTrace();
		}
	}
	setSendInfo(runningBuild);
}

@Override
//This method is called by TeamCity if a Build was finished successfully 
public void notifyBuildSuccessful(SRunningBuild runningBuild, Set<SUser> userInfo) {
	
	Loggers.SERVER.info("Spira Plugin :: Build Successful Notification Requested by TeamCity for "+runningBuild.getProjectId());
	TcProjectID = runningBuild.getProjectId();
	buildStatus = 2; //Success
	
	for (SUser user : userInfo) {
		try {
			getInfo(user);
		} catch (NumberFormatException e) {
			Loggers.SERVER.error(e);
			e.printStackTrace();
		} catch (IOException e) {
			Loggers.SERVER.error(e);
			e.printStackTrace();
		}
	}
	setSendInfo(runningBuild);
}	
//Not implemented methods
@Override
public void notifyBuildFailing (SRunningBuild runningBuild, Set<SUser> userInfo) {
	// Not implemented
}

@Override
public void notifyBuildProblemResponsibleAssigned(
		Collection<BuildProblemInfo> arg0, ResponsibilityEntry arg1,
		SProject arg2, Set<SUser> arg3) {
	// Not implemented
}

@Override
public void notifyBuildProblemResponsibleChanged(
		Collection<BuildProblemInfo> arg0, ResponsibilityEntry arg1,
		SProject arg2, Set<SUser> arg3) {
	// Not implemented
}

@Override
public void notifyBuildProblemsMuted(Collection<BuildProblemInfo> arg0,
		MuteInfo arg1, Set<SUser> arg2) {
	// Not implemented
}

@Override
public void notifyBuildProblemsUnmuted(Collection<BuildProblemInfo> arg0,
		MuteInfo arg1, SUser arg2, Set<SUser> arg3) {
	// Not implemented
	
}

	@Override
	public void notifyQueuedBuildWaitingForApproval(SQueuedBuild sQueuedBuild, Set<SUser> set)
	{

	}

	@Override
public void notifyBuildStarted(SRunningBuild arg0, Set<SUser> arg1) {
}

@Override
public void notifyLabelingFailed(Build arg0, VcsRoot arg1, Throwable arg2,
		Set<SUser> arg3) {
	// TODO Auto-generated method stub
	
}

@Override
public void notifyResponsibleAssigned(SBuildType arg0, Set<SUser> arg1) {
	// TODO Auto-generated method stub
	
}

@Override
public void notifyResponsibleAssigned(TestNameResponsibilityEntry arg0,
		TestNameResponsibilityEntry arg1, SProject arg2, Set<SUser> arg3) {
	// TODO Auto-generated method stub
	
}

@Override
public void notifyResponsibleAssigned(Collection<TestName> arg0,
		ResponsibilityEntry arg1, SProject arg2, Set<SUser> arg3) {
	// TODO Auto-generated method stub
	
}

@Override
public void notifyResponsibleChanged(SBuildType arg0, Set<SUser> arg1) {
	// TODO Auto-generated method stub
	
}

@Override
public void notifyResponsibleChanged(TestNameResponsibilityEntry arg0,
		TestNameResponsibilityEntry arg1, SProject arg2, Set<SUser> arg3) {
	// TODO Auto-generated method stub
	
}

@Override
public void notifyResponsibleChanged(Collection<TestName> arg0,
		ResponsibilityEntry arg1, SProject arg2, Set<SUser> arg3) {
	// TODO Auto-generated method stub
	
}

@Override
public void notifyTestsMuted(Collection<STest> arg0, MuteInfo arg1,
		Set<SUser> arg2) {
	// TODO Auto-generated method stub
	
}

@Override
public void notifyTestsUnmuted(Collection<STest> arg0, MuteInfo arg1,
		SUser arg2, Set<SUser> arg3) {
	// TODO Auto-generated method stub
	
}

}
   