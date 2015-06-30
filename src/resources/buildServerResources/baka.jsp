<%@ include file="/include.jsp" %>
<jsp:useBean id="message" scope="request" type="java.lang.String" />

<h2><c:out value="${message}"/></h2>