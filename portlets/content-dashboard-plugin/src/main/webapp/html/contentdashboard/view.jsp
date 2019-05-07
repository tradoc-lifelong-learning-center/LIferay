<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<portlet:defineObjects />

<%
String portletResource = ParamUtil.getString(request, "portletResource");
%>

<jsp:useBean id="cd" class="com.tjaglcs.plugins.ContentDashboard"/>

<portlet:resourceURL  var="exportCSVURL">
<portlet:param name="<%= Constants.CMD %>" value="exportCSV"/>
</portlet:resourceURL>
<portlet:resourceURL  var="exportHTMLCSVURL">
<portlet:param name="<%= Constants.CMD %>" value="exportHTMLCSV"/>
</portlet:resourceURL>

<c:set var="stuff" value="${cd.doStuff(renderRequest) }" />
<c:set var="sitemap" value="${cd.fetchSiteMap(renderRequest) }" />


This is the <b>Content Dashboard</b> portlet in View mode.



<p><a href="<%=exportCSVURL%>">Export Data as CSV</a></p>

<c:forEach items="${stuff }" var = "item" varStatus="i">

	<p><c:out value="${item.getFriendlyURL() }"/></p>
</c:forEach>
 


<div>


</div>
