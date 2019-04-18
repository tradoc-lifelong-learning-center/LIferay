<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.GetterUtil" %>
<%@ page import="com.liferay.portal.kernel.util.StringPool" %>

<portlet:defineObjects />

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<%  
String numberOfIssues_cfg = GetterUtil.getString(portletPreferences.getValue("numberOfIssues", ""));
%>

<aui:form action="<%= configurationURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
    
	
	
		<aui:select label="Single or multi issue" id="numberOfIssues" name="preferences--numberOfIssues--" showEmptyOption="true" cssClass="dropdown" helpMessage="Portlet can display either a single issue or all issues in a volume.">
			
			<aui:option id="numberOfIssues--single" value="single">Single</aui:option>
			<aui:option id="numberOfIssues--multi" value="multi">Multi</aui:option>

        </aui:select>
		<p>Current selection: <%= numberOfIssues_cfg %></p>
	
    <aui:button-row>
       <aui:button type="submit" />
    </aui:button-row>
</aui:form>
