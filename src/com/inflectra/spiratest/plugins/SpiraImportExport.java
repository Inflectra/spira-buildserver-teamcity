package com.inflectra.spiratest.plugins;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.inflectra.spiratest.plugins.model.*;
import org.apache.http.auth.InvalidCredentialsException;


/**
 * This defines the 'SpiraImportExport' class that provides the Java facade
 * for calling the REST web service exposed by SpiraTest
 * 
 * @author		Inflectra Corporation
 * @version		6.0.0
 *
 */
public class SpiraImportExport
{
	private static final String REST_SERVICE_SUFFIX = "/Services/v6_0/RestService.svc";

	private static final String SPIRA_PLUG_IN_NAME = "TeamCity Plugin";

	//The JSON date format used by the Spira 6.0 API
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	
	private String url;
	private String userName;
	private String apiKey;
	private int projectId;
	
	public SpiraImportExport()
	{
	}

	public SpiraImportExport(String url, String userName, String apiKey, int projectId)
	{
		this.url = url;
		this.userName = userName;
		this.apiKey = apiKey;
		this.projectId = projectId;
	}
	
	/**
	 * Tests the SpiraTeam connection by getting a list of projects the user has access to
	 * @return
	 */
	public boolean testConnection() throws Exception
	{
		boolean success = false;

		//Instantiate the rest proxy
		try
		{
			//Trust all SSL certificates
			SSLUtilities.trustAllHttpsCertificates();
			
			//Break up the URL into server name and the service path
			String url = this.url + REST_SERVICE_SUFFIX + "/projects";

			//Try getting the list of projects
			String json = httpGet(url, this.userName, this.apiKey);

			//Parse the returned data to make sure it is a valid list of projects
			Gson gson = new Gson();
			ArrayList<Project> projects;
			Type listOfProjects = new TypeToken<ArrayList<Project>>(){}.getType();
			projects = gson.fromJson(json, listOfProjects);
			if (projects.size() > 0)
			{
				success = true;
			}
			else
			{
				throw new Exception ("You need to be a member of at least one project in SpiraTeam");
			}

			return success;
		}
		catch (InvalidCredentialsException exception)
		{
			return false;
		}
		catch (MalformedURLException exception)
		{
			throw new Exception ("Error creating URL for connecting to SpiraTest server (" + exception.getMessage() + ")\n\n");
		}
		catch (Exception exception)
		{
			//Display the error
			throw new Exception ("Error sending results to SpiraTest server (" + exception.getMessage() + ")\n\n");
		}
	}

	/**
	 * Performs an HTTP POST request ot the specified URL
	 *
	 * @param input The URL to perform the query on
	 * @param body  The request body to be sent
	 * @param apiKey The Spira API Key to use
	 * @param login The Spira login to use
	 * @return An InputStream containing the JSON returned from the POST request
	 * @throws IOException
	 * @throws InvalidCredentialsException
	 */
	public static String httpPost(String input, String login, String apiKey, String body) throws IOException,  InvalidCredentialsException {
		URL url = new URL(input);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		//allow sending a request body
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		//have the connection send and retrieve JSON
		connection.setRequestProperty("accept", "application/json; charset=utf-8");
		connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		//Set the authentication headers
		connection.setRequestProperty("username", login);
		connection.setRequestProperty("api-key", apiKey);
		connection.connect();

		//used to send data in the REST request
		DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
		//write the body to the stream
		outputStream.writeBytes(body);
		//send the OutputStream to the server
		outputStream.flush();
		outputStream.close();

		int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// return result
			return response.toString();
		} else {
			if (responseCode == 403)
			{
				throw new InvalidCredentialsException("Invalid Spira login and API Key were provided!");
			}
			if (responseCode == 400)
			{
				throw new InvalidCredentialsException("Invalid Spira login and API Key were provided!");
			}
			throw new IOException("POST request not worked: " + responseCode);
		}
	}

	/**
	 * Performs an HTTP PUT request ot the specified URL
	 *
	 * @param input The URL to perform the query on
	 * @param body  The request body to be sent
	 * @param apiKey The Spira API Key to use
	 * @param login The Spira login to use
	 * @return An InputStream containing the JSON returned from the POST request
	 * @throws IOException
	 * @throws InvalidCredentialsException
	 */
	public static String httpPut(String input, String login, String apiKey, String body) throws IOException,  InvalidCredentialsException {
		URL url = new URL(input);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		//allow sending a request body
		connection.setDoOutput(true);
		connection.setRequestMethod("PUT");
		//have the connection send and retrieve JSON
		connection.setRequestProperty("accept", "application/json; charset=utf-8");
		connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		//Set the authentication headers
		connection.setRequestProperty("username", login);
		connection.setRequestProperty("api-key", apiKey);
		connection.connect();

		//used to send data in the REST request
		DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
		//write the body to the stream
		outputStream.writeBytes(body);
		//send the OutputStream to the server
		outputStream.flush();
		outputStream.close();

		int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// return result
			return response.toString();
		} else {
			if (responseCode == 403)
			{
				throw new InvalidCredentialsException("Invalid Spira login and API Key were provided!");
			}
			if (responseCode == 400)
			{
				throw new InvalidCredentialsException("Invalid Spira login and API Key were provided!");
			}
			throw new IOException("PUT request not worked: " + responseCode);
		}
	}

	/**
	 * Performs an HTTP GET request ot the specified URL
	 *
	 * @param input The URL to perform the query on
	 * @param apiKey The Spira API Key to use
	 * @param login The Spira login to use
	 * @return An InputStream containing the JSON returned from the POST request
	 * @throws IOException
	 * @throws InvalidCredentialsException
	 */
	public static String httpGet(String input, String login, String apiKey) throws IOException, InvalidCredentialsException {
		URL url = new URL(input);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		//allow sending a request body
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");
		//have the connection send and retrieve JSON
		connection.setRequestProperty("accept", "application/json; charset=utf-8");
		connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		//Set the authentication headers
		connection.setRequestProperty("username", login);
		connection.setRequestProperty("api-key", apiKey);
		connection.connect();
		int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// return result
			return response.toString();
		} else {
			if (responseCode == 403)
			{
				throw new InvalidCredentialsException("Invalid Spira login and API Key were provided!");
			}
			if (responseCode == 400)
			{
				throw new InvalidCredentialsException("Invalid Spira login and API Key were provided!");
			}
			throw new IOException("GET request not worked: " + responseCode);
		}
	}

	/**
	 * Tests that the project can be connected to
	 * @return
	 * @throws Exception
	 */
	public boolean testProject() throws Exception
	{
		boolean success = false;
		try
		{
			//Trust all SSL certificates
			SSLUtilities.trustAllHttpsCertificates();

			//Break up the URL into server name and the service path
			String url = this.url + REST_SERVICE_SUFFIX + "/projects/" + this.projectId;

			//Try getting the specified project
			String json = httpGet(url, this.userName, this.apiKey);

			//Parse the returned data to make sure it is a valid project object
			Gson gson = new Gson();
			Project project = gson.fromJson(json, Project.class);

			//Make sure the project id is returned and not null
			if (project != null && project.ProjectId != null && project.ProjectId > 0)
			{
				success = true;
			}

			return success;
		}
		catch (InvalidCredentialsException exception)
		{
			throw new Exception ("Error authenticating with SpiraTest server (" + exception.getMessage() + ")\n\n");
		}
		catch (MalformedURLException exception)
		{
			throw new Exception ("Error creating URL for connecting to SpiraTest server (" + exception.getMessage() + ")\n\n");
		}
		catch (Exception exception)
		{
			//Display the error
			throw new Exception ("Error sending results to SpiraTest server (" + exception.getMessage() + ")\n\n");
		}
	}
	
	/**
	 * Verifies that the release exists in the project
	 * @param releaseVersionNumber
	 * @return The id of the release or null
	 */
	public Integer verifyRelease(String releaseVersionNumber) throws Exception
	{
		boolean success = false;
		try
		{
			//Trust all SSL certificates
			SSLUtilities.trustAllHttpsCertificates();

			//Break up the URL into server name and the service path
			String url = this.url + REST_SERVICE_SUFFIX + "/projects/" + this.projectId + "/releases";

			//Verify the release exists

			//Get all the active releases
			String json = httpGet(url, this.userName, this.apiKey);

			//Parse the returned data to make sure it is a valid list of projects
			Gson gson = new Gson();
			ArrayList<Release> releases;
			Type listOfReleases = new TypeToken<ArrayList<Release>>(){}.getType();
			releases = gson.fromJson(json, listOfReleases);

			if (releases == null || releases.isEmpty())
			{
				return null;
			}
			//Need to make sure we have an exact match since a filter will do a LIKE comparison
			Integer releaseId = null;
			for (Release release : releases)
			{
				if (release.VersionNumber.equals(releaseVersionNumber))
				{
					releaseId = release.ReleaseId;
					break;
				}
			}
			return releaseId;
		}
		catch (InvalidCredentialsException exception)
		{
			throw new Exception ("Error authenticating with SpiraTest server (" + exception.getMessage() + ")\n\n");
		}
		catch (MalformedURLException exception)
		{
			throw new Exception ("Error creating URL for connecting to SpiraTest server (" + exception.getMessage() + ")\n\n");
		}
		catch (Exception exception)
		{
			//Display the error
			throw new Exception ("Error sending results to SpiraTest server (" + exception.getMessage() + ")\n\n");
		}
	}

	/**
	 * Creates a new build entry in SpiraTest
	 * @param releaseVersionNumber			The current release
	 * @param creationDate 		The creation date
	 * @param buildStatusId 	The status of the build (1 = Fail, 2 = Succeed)
	 * @param name 				The name of the build
	 * @param description 		The full build description
	 * @param revisions			The list of revisions associated with the build
	 * @param incidents			The list of incidents fixed in the build
	 * @return
	 * @throws Exception 
	 */
	public int recordBuild(String releaseVersionNumber, Date creationDate, int buildStatusId, String name,
						   String description, List<String> revisions, List<Integer> incidents) throws Exception
	{
		try
		{
			//Trust all SSL certificates
			SSLUtilities.trustAllHttpsCertificates();

			//Now get the release id for the specific version number
			//Get all the active releases
			String url = this.url + REST_SERVICE_SUFFIX + "/projects/" + this.projectId + "/releases";
			String json = httpGet(url, this.userName, this.apiKey);

			//Parse the returned data to make sure it is a valid list of projects
			//Gson gson = new GsonBuilder().setDateFormat(DATE_TIME_FORMAT).create();
			Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonUTCDateAdapter()).create();
			ArrayList<Release> releases;
			Type listOfReleases = new TypeToken<ArrayList<Release>>(){}.getType();
			releases = gson.fromJson(json, listOfReleases);
			Integer releaseId = null;
			if (releases != null && !releases.isEmpty()) {
				for (Release release : releases) {
					if (release.VersionNumber.equals(releaseVersionNumber)) {
						releaseId = release.ReleaseId;
						break;
					}
				}
			}
			if (releaseId == null)
			{
				throw new Exception("Unable to locate a release with version number '" + releaseVersionNumber + "' in project PR" + this.projectId);
			}

			//Now create the REST request to post a new build under this release
			url = this.url + REST_SERVICE_SUFFIX + "/projects/" + this.projectId + "/releases/" + releaseId + "/builds";

			//Now create the new build object
			Build remoteBuild = new Build();
			remoteBuild.setProjectId(this.projectId);
			remoteBuild.setReleaseId(releaseId);
			remoteBuild.setBuildStatusId(buildStatusId);	//Succeeded
			remoteBuild.setName(cleanText(name));
			remoteBuild.setDescription(cleanText(description));
			remoteBuild.setCreationDate(creationDate);

			//Add the source code revisions
			if (revisions != null && !revisions.isEmpty()) {
				ArrayList<BuildSourceCode> buildSourceCodes = new ArrayList<BuildSourceCode>();
				for (String revision : revisions) {
					BuildSourceCode buildSourceCode = new BuildSourceCode();
					buildSourceCode.RevisionKey = revision;
					buildSourceCodes.add(buildSourceCode);
				}
				remoteBuild.setRevisions(buildSourceCodes);
			}

			//POST the object and get the new ID
			String buildJson = gson.toJson(remoteBuild);
			buildJson = httpPost(url, this.userName, this.apiKey, buildJson);
			remoteBuild = gson.fromJson(buildJson, Build.class);
			int buildId = remoteBuild.getBuildId();
			
			//Now we need to set the 'FixedBuildId' for any incidents listed in the commit messages
			if (incidents != null && !incidents.isEmpty())
			{
				for (Integer incidentId : incidents)
				{
					//Try and retrieve/update the incident
					updateIncidentFixedBuild(incidentId, buildId);
				}
			}
			
			return buildId;
		}
		catch (InvalidCredentialsException exception)
		{
			throw new Exception ("Error authenticating with SpiraTest server (" + exception.getMessage() + ")");
		}
		catch (MalformedURLException exception)
		{
			throw new Exception("Error creating URL for connecting to SpiraTest server (" + exception.getMessage() + ")");
		}
		catch (Exception exception)
		{
			//Display the error
			throw new Exception ("Error sending results to SpiraTest server (" + exception.getMessage() + ")");
		}
	}

	/**
	 * Updates the fixed build of a specific incident
	 * @param incidentId The id of the incident
	 * @param buildId The id of the fixed build
	 * @throws Exception
	 */
	private void updateIncidentFixedBuild(int incidentId, int buildId) throws Exception
	{
		try {
			//First get the incident by its ID
			String url = this.url + REST_SERVICE_SUFFIX + "/projects/" + this.projectId + "/incidents/" + incidentId;
			String incidentJson = httpGet(url, this.userName, this.apiKey);
			if (incidentJson != null) {
				//Gson gson = new GsonBuilder().setDateFormat(DATE_TIME_FORMAT).create();
				Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonUTCDateAdapter()).create();
				Incident incident = gson.fromJson(incidentJson, Incident.class);

				//Update the build id
				incident.FixedBuildId = buildId;

				//Update the incident with the new build id
				incidentJson = gson.toJson(incident);
				httpPut(url, this.userName, this.apiKey, incidentJson);
			}
		}
		catch (InvalidCredentialsException exception)
		{
			throw new Exception ("Error authenticating with SpiraTest server (" + exception.getMessage() + ")");
		}
		catch (MalformedURLException exception)
		{
			throw new Exception("Error creating URL for connecting to SpiraTest server (" + exception.getMessage() + ")");
		}
		catch (Exception exception)
		{
			//Display the error
			throw new Exception ("Error sending results to SpiraTest server (" + exception.getMessage() + ")");
		}
	}
	
	/**
	 * Removes any invalid XML contract characters from a string before being used in a SOAP call
	 * @param text
	 * @return
	 */
	public String cleanText(String text)
	{
		if (text == null)
		{
			return null;
		}
		return text.replaceAll("\\p{Cntrl}", "");
	}
	
	public String extractStackTrace(StackTraceElement[] elements)
	{
		String output = "";
		if (elements != null)
		{
			for (int i = 0; i < elements.length; i++)
			{
				output += elements[i].toString();
			}
		}
		
		return output;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
}
