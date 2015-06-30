package com.inflectra.spiratest.plugins;

import jetbrains.buildServer.controllers.admin.AdminPage;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.PositionConstraint;
import jetbrains.buildServer.web.openapi.WebControllerManager;

import javax.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * This defines the 'SpiraGlobalConfigPage' class, which contains methods that are
 * used for TeamCity to implement the Spira Global Configurations Page in Administration
 * 
 * @author		Bruno Gruber - Inflectra Corporation
 * @version		1.0.0 - June 2015
 *
 */

public class SpiraGlobalConfigPage extends AdminPage {
	static String Spira_dir = System.getenv("TEAMCITY_DATA_PATH"); 
	private static final String GLOBAL_CONFIG_PATH = (Spira_dir + "\\config\\SpiraGlobalOutput.txt");
	private static final String PLUGIN_NAME = "spiraGlobal";
	private static final String TAB_TITLE = "Spira Global Settings";
	private static final String AFTER_PAGE_ID = "serverConfigGeneral";
	private static final String BEFORE_PAGE_ID = "auth";
	private static final String PAGE = "spiraGlobal.jsp";
	
	String gotURL, gotUserName, gotPassword;
		
	public SpiraGlobalConfigPage(PagePlaces pagePlaces,
            					 PluginDescriptor pluginDescriptor,
            					 final WebControllerManager manager,
            					 final ProjectManager projectManager,
            					 final SBuildServer buildServer) {
		super(pagePlaces);
		
		setPluginName(PLUGIN_NAME);
		setIncludeUrl(pluginDescriptor.getPluginResourcesPath(PAGE));
		setTabTitle(TAB_TITLE);
		ArrayList<String> after = new ArrayList<String>();
		after.add(AFTER_PAGE_ID);
		ArrayList<String> before = new ArrayList<String>();
		before.add(BEFORE_PAGE_ID);
		setPosition(PositionConstraint.between(after, before));
		register();
		SpiraGlobalController controllerGlobal = new SpiraGlobalController(projectManager, buildServer);
		manager.registerController("/spira/GlobalSettings.html", controllerGlobal);
	}
	
	//Reads Global info from file 
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
			  gotURL = "http://";
			  gotUserName = "Username";
			  gotPassword = "Password";
			  e.printStackTrace();
			  return;
			} 
	  }
	
	 @Override
	// Fills the http page with the acquired data
	public void fillModel(Map<String, Object> map, HttpServletRequest request) {
		 
		super.fillModel(map, request);
		try {
			fromFile(GLOBAL_CONFIG_PATH);
		} catch (Exception e) {
			gotURL = "http://";
			gotUserName = "Username";
			gotPassword = "Password";
			e.printStackTrace();}
					
			String spiraUrl = gotURL;
		    String spiraUsername = gotUserName;
		    String spiraPassword = gotPassword;
		    map.put("spiraUrl", spiraUrl);
		    map.put("spiraUsername", spiraUsername);
		    map.put("spiraPassword", spiraPassword);
	 }
		@Override
		public String getGroup() {
			return SERVER_RELATED_GROUP;
		}
}
