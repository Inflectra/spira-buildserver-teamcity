/*package com.inflectra.spiratest.plugins.teamcity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.lang.*;

import jetbrains.buildServer.log.Loggers;/*

/**
 * This defines the 'SpiraInitialization' class, which creates the files that
 * will be used by the Plug-In. This class is not necessary anymore and was kept
 * for testing reasons.
 * 
 * @author		Bruno Gruber - Inflectra Corporation
 * @version		1.0.0 - June 2015
 *
 */
/*public class Initialization {
	
	static String Spira_dir = System.getenv("TEAMCITY_DATA_PATH"); 

	private static final String LOCAL_CONFIG_PATH =  (Spira_dir + "\\config\\SpiraOutput.txt");
	private static final String GLOBAL_CONFIG_PATH = (Spira_dir + "\\config\\SpiraGlobalOutput.txt");
		
	public Initialization() throws IOException {
		 File local,global = null;
		 local = new File(LOCAL_CONFIG_PATH);
		 local.delete();  
		 global = new File(GLOBAL_CONFIG_PATH);
		 global.delete();  
		 initializeLocal();
		 initializeGlobal();
		}
	
	public void initializeLocal () throws IOException{
	    Writer output;
	    output = new BufferedWriter(new FileWriter(LOCAL_CONFIG_PATH, true));
		ArrayList<String> InitializationList = new ArrayList<String>();
		//supporting initially 50 projects
		for (int n=0; n<5; n++) {
			InitializationList.add("project" + (n+1) + "|*|9|*|9.9.9|*|");
			output.append(InitializationList.get(n));
					}
		output.close();
		Loggers.SERVER.info("Spira_dir " + Spira_dir );
		Loggers.SERVER.info("LOCAL_CONFIG_PATH " + LOCAL_CONFIG_PATH);
	}
public void initializeGlobal () throws IOException{
		
		FileWriter file = new FileWriter(GLOBAL_CONFIG_PATH);
		PrintWriter record = new PrintWriter(file);
		//record.printf("http://example;SpiraUser;Password");
		record.printf("http://doctor/SpiraTeam;fredbloggs;PleaseChange");
		file.close();
		Loggers.SERVER.info("@@@ Global initialization sucessfull!");
	}
	}*/
