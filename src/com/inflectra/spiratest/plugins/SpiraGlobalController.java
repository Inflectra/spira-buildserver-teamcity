package com.inflectra.spiratest.plugins;

import jetbrains.buildServer.controllers.BaseFormXmlController;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildServer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;
import org.springframework.web.servlet.ModelAndView;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This defines the 'SpiraGlobalController' class, which contains methods that are
 * used for verify global settings and export it to a file (works on Spira Global page)
 * 
 * @author		Bruno Gruber - Inflectra Corporation
 * @version		1.0.0 - June 2015
 *
 */

public class SpiraGlobalController extends BaseFormXmlController {
	static String Spira_dir = System.getenv("TEAMCITY_DATA_PATH"); 
	private static final String GLOBAL_CONFIG_PATH = (Spira_dir + "\\config\\SpiraGlobalOutput.txt");
	Throwable t = null; // watching for IO errors
	private ProjectManager projectManager;
    private SBuildServer server;

	public SpiraGlobalController (ProjectManager projectManager, SBuildServer server){
        this.projectManager = projectManager;
        this.server = server;
	}

	//Exports Global info to a file
	protected void ToFile(String url, String userName, String password ) throws IOException{
			
		    try {
		    FileWriter file = new FileWriter(GLOBAL_CONFIG_PATH);
	        PrintWriter record = new PrintWriter(file);
	        record.printf("%s|*|%s|*|%s",url,userName,password);
	        file.close();
	         }	        
	      catch (IOException e) {
	    	    t = e;
				e.printStackTrace();
			}
	       catch (Exception e) {
		 		t = e;
				e.printStackTrace();}
	 }
	 
	 
	@Override
	// Verify the info from user. If it is valid, export it to a file
	protected void doPost(HttpServletRequest request, HttpServletResponse response, Element responseElement)
	{
		String SpiraUrlString = request.getParameter("txtSpiraUrl");
        String SpiraUserString = request.getParameter("txtUsername");
        String SpiraPasswordString = request.getParameter("txtPassword");
        
        SpiraImportExport spiraClient = new SpiraImportExport();
        
        spiraClient.setUrl(SpiraUrlString);
    	spiraClient.setUserName(SpiraUserString);
    	spiraClient.setPassword(SpiraPasswordString);

        try {       	
        	if (t!= null){
        		String returnUrl = request.getHeader("referer") + "&action=badIO";
        		response.sendRedirect(returnUrl);
        		return;
        	}
        	
        	//Test url/login/password(connection)
        	boolean canConnect = spiraClient.testConnection();
        	if (!canConnect)
        	{
        		String returnUrl = request.getHeader("referer") + "&action=badconnection";
        		response.sendRedirect(returnUrl);
        		return;
        	}
        	       
        	else {
        		String returnUrl = request.getHeader("referer") + "&action=success";
        		ToFile (SpiraUrlString,SpiraUserString,SpiraPasswordString);
        		response.sendRedirect(returnUrl);
        		return;
        	}
    	
		} catch (IOException e) {
			String returnUrl = request.getHeader("referer") + "&action=badconnection";
			try {
				response.sendRedirect(returnUrl);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
        
          catch (Exception e) {
        	  String returnUrl = request.getHeader("referer") + "&action=badconnection";
        	  try {
				response.sendRedirect(returnUrl);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected ModelAndView doGet(HttpServletRequest arg0,
			HttpServletResponse arg1) {
		return null;
	}

}
