<%@ page import="java.io.*" %>
<%@ page import="javax.servlet.*" %>
<%@ page import="jetbrains.buildServer.log.Loggers" %>
<%@ include file="/include.jsp" %>
<%@ page trimDirectiveWhitespaces="true" %>

<c:url var="controllerUrl" value="/spira/ProjectSettings.html"/>
<jsp:useBean id="projectId" scope="request" type="java.lang.Integer" />
<jsp:useBean id="releaseVersionNumber" scope="request" type="java.lang.String" />
<jsp:useBean id="spiraUrl" scope="request" type="java.lang.String" />
<jsp:useBean id="tcProject" scope="request" type="java.lang.String" />

<h2>Spira Configuration</h2>
<form action="${controllerUrl}" id="spiraProjectForm" method="post" onsubmit="return verifySpira()">
<div id="spiraSuccess" style="color: green; font-weight: bold; display: none">
	Your changes have been successfully saved!
</div>
<div id="spiraBadConnection" style="color: red; font-weight: bold; display: none">
	Unable to connect to the Spira server, please verify the Global Configuration settings!
</div>
<div id="spiraBadProject" style="color: red; font-weight: bold; display: none">
	The project Id you specified either does not exist, or your user does not have access to it!
</div>
<div id="spiraBadRelease" style="color: red; font-weight: bold; display: none">
	The release version number you specified does not exist in the current project!
</div>
<div id="spiraBadFormat" style="color: orange; font-weight: bold; display: none">
	Error. Please input a valid numeric value for ProjectID. 
</div>
<div id="spiraOtherError" style="color: red; font-weight: bold; display: none">
	Error. Please check the provided information and Administration > Spira Global Settings.
</div>
<table>       
	<tr>
		<td>Spira URL:
		</td>
		<td>
			<c:out value="${spiraUrl}"/>
		</td>
	</tr>
	<tr>
		<td>TeamCity Project ID:
		</td>
		<td>
			<c:out value="${tcProject}"/>
			<input type="hidden" name="txtTcProject" value="${tcProject}" />
		</td>
	</tr>
	<tr>
		<td><label for="txtProjectId">Project ID:</label>
		</td>
		<td>
			PR<input type="text" name="txtProjectId" placeholder="ID" maxlength="10"  value="${projectId}" />
		</td>
	</tr>
	<tr>
		<td><label for="txtRelease">Release Version #:</label>
		</td>
		<td>
			<input type="text" name="txtRelease" placeholder="x.x.x.x" maxlength="50" value="${releaseVersionNumber}" />
		</td>
	</tr>
	<tr>
		<td>
		</td>
		<td>
			<input type="submit" name="btnVerify" value="Save" />
		</td>
	</tr>
</table>
	<input type="hidden" name="action" value="" />
</form>
<script type="text/javascript">
function verifySpira()
{
	return true;
}
$j(document).ready(function(){
	var vars = [], hash;
    var q = document.URL.split('?')[1];
    if(q != undefined){
        q = q.split('&');
        for(var i = 0; i < q.length; i++){
            hash = q[i].split('=');
            vars.push(hash[1]);
            vars[hash[0]] = hash[1];
        }
    }
  	if(vars['action'] == 'success')
 	{
  		$j('#spiraSuccess').css('display', 'block');
 	}
  	if(vars['action'] == 'badproject')
 	{
  		$j('#spiraBadProject').css('display', 'block');
 	}
  	if(vars['action'] == 'badrelease')
 	{
  		$j('#spiraBadRelease').css('display', 'block');
 	}
  	if(vars['action'] == 'badconnection')
 	{
  		$j('#spiraBadConnection').css('display', 'block');
 	}
	if(vars['action'] == 'badformat')
 	{
  		$j('#spiraBadFormat').css('display', 'block');
 	}	
	if(vars['action'] == 'badMisc')
 	{
  		$j('#spiraOtherError').css('display', 'block');
 	}	
});
</script>

