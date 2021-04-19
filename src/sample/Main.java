package sample;
 //standard IO libs
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

// XML libs
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.users.SUser;

// REST server libs
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.inflectra.spiratest.plugins.*;

/**
 * @author Bruno Gruber
 * 
 * This program contains just testing code of communication with
 * SpiraTeam (main2 function) and TeamCity rest server (main)
 */
 
public class Main {
	private static final SRunningBuild SRunningBuild = null;
	private static final int SUser = 0;

	public static void main2(String[] args) throws Exception {
	//Spira code
		Date date = new Date();
		List<Integer> incidentIds = new ArrayList<Integer>();
		List<String> revisions = new ArrayList<String>();
		revisions.add("rev001");
		SpiraImportExport spiraClient = new SpiraImportExport();
		spiraClient.setUrl("http://doctor/spirateam");
		spiraClient.setUserName("fredbloggs");
		spiraClient.setApiKey("PleaseChange");
		spiraClient.testConnection();
		spiraClient.setProjectId(1);
		spiraClient.verifyRelease ("1.0.0.0");
		/*spiraClient.recordTestRun(1,103,1,1,date,date,1,"Testing", "Testing Test Name",1, 
				                  "Testing Access from SOAP", "Test Stack Trace field");*/
		
		spiraClient.recordBuild("1.0.0.0", date, 2, "TestBuild", "Just testing", revisions, incidentIds);
	}
	
	public static void main(String[] args) {
		//TeamCity code
				DefaultHttpClient dhttpclient = new DefaultHttpClient();
				
				try {
								
					//HTTP Rest Server Authentication Parameters
			        String username = "brgruber";
			        String password = "teamtest";
			        String host = "localhost";
			        String uri = "http://localhost:88/httpAuth/app/rest/projects/";
			        
			        dhttpclient.getCredentialsProvider().setCredentials(new AuthScope(host, 88), 
			    		   											    new UsernamePasswordCredentials
			    		   											              (username, password));
			        HttpGet dhttpget = new HttpGet(uri);
			        System.out.println("executing request " + dhttpget.getRequestLine());
			        HttpResponse dresponse = dhttpclient.execute(dhttpget);
			        System.out.println(dresponse.getStatusLine());
			       
			        //Check if HTTP response code is 200 (success)
					if (dresponse.getStatusLine().getStatusCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : " 
					                                      + dresponse.getStatusLine().getStatusCode());
					}
				 
					// Get-Capture Complete application/xml body response
					BufferedReader br = new BufferedReader(new InputStreamReader((dresponse.getEntity().getContent())));
					String output;
					System.out.println("============Output:============");
					// Simply iterate through XML response and show on console.
					StringBuilder sb = new StringBuilder();
					while ((output = br.readLine()) != null) {
							
						System.out.println(output);
						sb.append(output);
					}	
					String completeOutput = sb.toString();
					
					try {
						Document doc = loadXMLFromString(completeOutput);
						Node projects = doc.getFirstChild();	//<projects>
						NodeList projectList = projects.getChildNodes(); //<project> (all)
						for (int i = 0; i < projectList.getLength(); i++)
						{
							Node project = projectList.item(i);
							Node printTest = project.getAttributes().getNamedItem("description");
							System.out.println("=========Bruno Test=========");
							System.out.println(printTest);
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			        } 
				catch (ClientProtocolException e) {
					e.printStackTrace();
			    } 
				catch (IOException e) {
					e.printStackTrace();
			    }
				
			    finally{
			    	dhttpclient.getConnectionManager().shutdown();
		        }
	}
	
	public static Document loadXMLFromString(String xml) throws Exception
	{
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(xml));
	    return builder.parse(is);
	    
	}
}