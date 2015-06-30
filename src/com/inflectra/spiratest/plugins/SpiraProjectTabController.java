package com.inflectra.spiratest.plugins;

import java.io.IOException;

import jetbrains.buildServer.controllers.BaseFormXmlController;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildServer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This defines the 'SpiraProjectControllerTab' class, which contains methods that are
 * used to Import/Export data, as well as Post information
 * 
 * @author		Bruno Gruber - Inflectra Corporation
 * @version		1.0.0 - June 2015
 *
 */

public class SpiraProjectTabController extends BaseFormXmlController {
	
	static String Spira_dir = System.getenv("TEAMCITY_DATA_PATH"); 

	private static final String LOCAL_CONFIG_PATH =  (Spira_dir + "\\config\\SpiraOutput.txt");
	private static final String GLOBAL_CONFIG_PATH = (Spira_dir + "\\config\\SpiraGlobalOutput.txt");
	
	String gotURL, gotUserName, gotPassword;
	private ProjectManager projectManager;
	private SBuildServer server;
    
	public SpiraProjectTabController(ProjectManager projectManager, SBuildServer server){
        this.projectManager = projectManager;
        this.server = server;
    }
    
	@Override
    protected ModelAndView doGet(HttpServletRequest request, HttpServletResponse response) {
		return null;
    }
	
	//Saves the info referent to other projects from the output file 
	public ArrayList<String> CreateOthersProjectList(String path,String tcProjectName)
	{
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
		    
		    for (int n=0; n<ProjectList.size(); n=n+1) {
		 	     String piece = ProjectList.get(n);
		             if (piece.equals(tcProjectName)) {
		            	 //Removes old info of this project
		            	 ProjectList.remove(n); //ProjectID (TeamCity)
		            	 ProjectList.remove(n); //ProjectID (SpiraTeam)
		            	 ProjectList.remove(n); //Version#
		            	 break; 
		             }   
		         }
		    //removes the old file from system
		    File f = null;
		    f = new File(LOCAL_CONFIG_PATH);
		    f.delete();  
		  
	  }catch (IOException e) {
		  e.printStackTrace();
		  ProjectList.add("empty");
		}  
	      return ProjectList;
	}
	
	//Format a string with separators (|*|) from the List
	public String addAppend (ArrayList<String> List){
	
		String prev_out = "|*|";
		
		for (int n=0; n<List.size(); n++)
		{
	    prev_out = (prev_out + List.get(n) + "|*|");
		}
		return prev_out;
	}

	//Stores the info of the project in a file 
	protected void ToFile(String tcProjectName, String projectId, String releaseVers) throws IOException{
	    
		 ArrayList<String> OthersProjectList = CreateOthersProjectList(LOCAL_CONFIG_PATH,tcProjectName);
		 String add = addAppend(OthersProjectList);
		 Writer output;
		 output = new BufferedWriter(new FileWriter(LOCAL_CONFIG_PATH, true));
		 output.append(tcProjectName + "|*|" + projectId + "|*|" + releaseVers + "|*|" + add);
		 output.close();
	 }
	 //Reads Global Configuration from a file
	 protected void fromFile (String path) throws NumberFormatException, IOException {
		  String line = null; 
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
	  }

    @Override
    //Checks info entered by the user. If it is valid, saves it to a file.
    protected void doPost(HttpServletRequest request, HttpServletResponse response, Element responseElement) {
        String projectIdString = request.getParameter("txtProjectId");
        String releaseVersionNumber = request.getParameter("txtRelease");
        String projectTcID = request.getParameter ("txtTcProject");
        int projectID = 0;
        
        try {
        	projectID = Integer.parseInt(projectIdString);
    	} catch (NumberFormatException e) {
    		Loggers.SERVER.info("SpiraTeam Plugin :: Bad projectID format from user. Action: projectID changed to 0");
    		projectID =  0;
    		String returnUrl = request.getHeader("referer") + "&action=badformat";
    		try {
				response.sendRedirect(returnUrl);
				return;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
    		e.printStackTrace();}

        try {
        	SpiraImportExport spiraClient = new SpiraImportExport();
        	fromFile(GLOBAL_CONFIG_PATH); 
        	spiraClient.setUrl(gotURL);
        	spiraClient.setUserName(gotUserName);
        	spiraClient.setPassword(gotPassword);
        	spiraClient.setProjectId(projectID);
        	
        	//First, test url/login/password
        	boolean canConnect = spiraClient.testConnection();
        	if (!canConnect)
        	{
        		String returnUrl = request.getHeader("referer") + "&action=badconnection";
        		response.sendRedirect(returnUrl);
        		return;
        	}
        	
        	//Next test the project
        	canConnect = spiraClient.testProject();
        	if (!canConnect)
        	{
        		String returnUrl = request.getHeader("referer") + "&action=badproject";
        		response.sendRedirect(returnUrl);
        		return;
        	}
        	Integer verify = spiraClient.verifyRelease(releaseVersionNumber);
        	if (verify == null) { 
        		String returnUrl = request.getHeader("referer") + "&action=badrelease";
        		response.sendRedirect(returnUrl);
        	}
        	else {
        		ToFile (projectTcID,projectIdString,releaseVersionNumber);
        		String returnUrl = request.getHeader("referer") + "&action=success";
        		response.sendRedirect(returnUrl);
        	}
		} catch (IOException e) {
			String returnUrl = request.getHeader("referer") + "&action=badMisc";
			try {
				response.sendRedirect(returnUrl);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
 catch (Exception e) {
		String returnUrl = request.getHeader("referer") + "&action=badproject";
		try {
			response.sendRedirect(returnUrl);
    		return;
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
		}
    }	
    }
