package com.inflectra.spiratest.plugins;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;

import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;
import java.util.StringTokenizer;  

/**
 * This defines the 'SpiraProjectTab' class, which contains methods that are
 * used for implement the Spira Settings in each project
 * 
 * @author		Bruno Gruber - Inflectra Corporation
 * @version		1.0.0 - June 2015
 *
 */

public class SpiraProjectTab extends jetbrains.buildServer.web.openapi.project.ProjectTab {
	
	static String Spira_dir = System.getenv("TEAMCITY_DATA_PATH"); 
	private static final String LOCAL_CONFIG_PATH =  (Spira_dir + "\\config\\SpiraOutput.txt");
	private static final String GLOBAL_CONFIG_PATH = (Spira_dir + "\\config\\SpiraGlobalOutput.txt");
	
	private static final String TAB_TITLE = "Spira Project Configuration";
	
	int gotProjectID;
	int flag = 0;
	String gotRelease, gotURL, gotUserName, gotPassword;
	
  protected SpiraProjectTab(final PluginDescriptor pluginDescriptor,
                         final WebControllerManager manager,
                         final ProjectManager projectManager,
                         final SBuildServer buildServer) {
	  super("spiraProjectTab",
			TAB_TITLE,
    		manager,
    		projectManager,
    		pluginDescriptor.getPluginResourcesPath("spira.jsp"));
	   
	  SpiraProjectTabController controller = new SpiraProjectTabController(projectManager, buildServer);
	  manager.registerController("/spira/ProjectSettings.html", controller);
  }

//Takes a string from file and makes it a list 
  public ArrayList<String> CreateProjectList(String path){
	  
	  flag = 0; //watching for exceptions (usually file not found)
	  String line = null;
	  ArrayList<String> ProjectList = new ArrayList<String>();
	  
      try{
	  FileReader reader = new FileReader(path); 
	  BufferedReader reading = new BufferedReader(reader);  
	  StringTokenizer st = null;
	  while ((line = reading.readLine()) != null) {  
	        st = new StringTokenizer(line, "|*|");  
	        String data = null;  
	       while (st.hasMoreTokens()) {  
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
  //Read file for Project-Based configurations (aka local configurations)
  protected void getFile (String path, String TcProjectID) throws IOException{
	    ArrayList<String> ProjectList = CreateProjectList(path);
	    if (flag ==1){
	    Loggers.SERVER.info(":: SpiraTeam Plugin :: Project information could not be loaded from file. Action: Project fields set to 0");
	    gotProjectID = 0;
	    gotRelease = "0.0.0";	
	    }
	    else{
	    for (int n=0; n<=ProjectList.size(); n=n+1) {
	 	     String piece = ProjectList.get(n);
	             if (piece.equals(TcProjectID)) {
	            	 try {
	            		 gotProjectID = Integer.parseInt(ProjectList.get(n+1));
	            		} catch (NumberFormatException e) {
	            			Loggers.SERVER.info(":: SpiraTeam Plugin :: Bad projectID format from file. Action: projectID changed to 0");
	            			gotProjectID = 0;
	            			e.printStackTrace();	
	            		}	
	            	 gotRelease = ProjectList.get(n+2);
	            	 break; 	
	             }            
	             Loggers.SERVER.info(":: SpiraTeam Plugin :: ProjectID not found from file. Action: projectID and Release # considered 0");
	             gotProjectID = 0;
	             gotRelease = "0.0.0";
	         }
	    }
	    }   
  
  //Read file for Global configurations 
  protected void fromFile (String path) throws NumberFormatException {
	  String line = null; 
	  try{
		  FileReader reader = new FileReader(path);
		  BufferedReader reading = new BufferedReader(reader);  
	      StringTokenizer st = null;
	      while ((line = reading.readLine()) != null) {  
	          st = new StringTokenizer(line, "|*|");  
	          String data = null;  
	          while (st.hasMoreTokens()) {  
	        	 data = st.nextToken();  
	             gotURL = data;
	             data = st.nextToken();  
	             gotUserName = data;  
	             data = st.nextToken();  
	             gotPassword = data; 
	          }  
	       }  
	       reading.close();  
	       reader.close();
	  }catch (IOException e) {
		  gotURL = "URL not found. Please inform URL going to Administration > Spira Global Settings.";
		  gotUserName = "user";
		  gotPassword = "passw";
		  e.printStackTrace();
		  return;
		} 
  }
//Fills the boxes in the html page with the loaded information
  @Override
  protected void fillModel(final Map<String, Object> map,
                           final HttpServletRequest httpServletRequest,
                           final SProject sProject,
                           final SUser sUser) {
	   
	try {
		fromFile (GLOBAL_CONFIG_PATH);
	} catch (Exception e1) {
		gotURL = "http://";
		gotUserName = "Username";
		gotPassword = "Password";
		gotProjectID = 0;
		gotRelease = "0.0.0";
		e1.printStackTrace();
	}
	
	try {
		getFile (LOCAL_CONFIG_PATH,sProject.getProjectId());
	} catch (Exception e) {
		Loggers.SERVER.info("SpiraTeam Plugin :: Bad projectID format from user. Action: projectID changed to 0");
		gotURL = "http://";
		gotUserName = "Username";
		gotPassword = "Password";
		gotProjectID = 0;
		gotRelease = "0.0.0";
		e.printStackTrace();
	} 
	String spiraUrl = gotURL;
	Integer projectId = gotProjectID;
	String releaseVersionNumber = gotRelease;
    	 
	map.put("spiraUrl", spiraUrl);
    map.put("projectId", projectId);
    map.put("releaseVersionNumber", releaseVersionNumber);
    map.put("tcProject",sProject.getProjectId());  
  }
 
}
