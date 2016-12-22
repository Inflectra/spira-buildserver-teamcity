<%@ page import="java.io.*" %>
<%@ page import="javax.servlet.*" %>
<%@ page import="jetbrains.buildServer.log.Loggers" %>
<%@ include file="/include.jsp" %>
<%@ page trimDirectiveWhitespaces="true" %>

<c:url var="controllerUrl" value="/spira/GlobalSettings.html"/>
<jsp:useBean id="spiraUrl" scope="request" type="java.lang.String" />
<jsp:useBean id="spiraUsername" scope="request" type="java.lang.String" />
<jsp:useBean id="spiraPassword" scope="request" type="java.lang.String" />

<h2>Spira Configuration</h2>
<form action="${controllerUrl}" id="spiraGlobalForm" method="post" onsubmit="return verifySpira()">
<div id="spiraSuccess" style="color: green; font-weight: bold; display: none">
	Your changes have been successfully saved!
</div>
<div id="spiraBadConnection" style="color: orange; font-weight: bold; display: none">
	Unable to connect to the Spira server, please verify the provided data and try again. 
</div>
<div id="spiraBadIO" style="color: red; font-weight: bold; display: none">
	Error importing/exporting data, please try again.
</div>
<table>       
	<tr>
		<td><label for="txtSpiraUrl">SpiraTeam URL:</label>
		</td>
		<td>
			<input type="text" name="txtSpiraUrl" placeholder="E.g.: http://doc/spirateam" maxlength="255"  value="${spiraUrl}" />
		</td>
	</tr>
	<tr>
		<td><label for="txtUsername">User Name:</label>
		</td>
		<td>
			<input type="text" name="txtUsername" placeholder="SpiraTest User Name" maxlength="50" value="${spiraUsername}" />
		</td>
	</tr>
	<tr>
		<td><label for="txtPassword">Password:</label>
		</td>
		<td>
			<input type="password" name="txtPassword" placeholder="SpiraTest Password" maxlength="128"  value="${spiraPassword}" />
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
  	if(vars['action'] == 'badconnection')
 	{
  		$j('#spiraBadConnection').css('display', 'block');
 	}
  	if(vars['action'] == 'badIO')
 	{
  		$j('#spiraBadIO').css('display', 'block');
 	}
});
</script>

