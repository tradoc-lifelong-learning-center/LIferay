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

boolean isMulti=false;
boolean isSingle=false;

if(numberOfIssues_cfg.contains("multi")){
	isMulti=true;
} else if(numberOfIssues_cfg.contains("single")){
	isSingle=true;
}

%>

<aui:form action="<%= configurationURL %>" method="post" name="fm" cssClass="config-form">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />

		<aui:fieldset label="Should the portlet display one issue at a time or an entire volume?">
			<aui:input name="preferences--numberOfIssues--" type="radio" value="single" label="Single Issue" checked="<%=isSingle %>"/>
	        <aui:input name="preferences--numberOfIssues--" type="radio" value="multi" label="Entire Volume" checked="<%=isMulti %>"/> 
		
		</aui:fieldset>

    <aui:button-row>
       <aui:button type="submit" />
    </aui:button-row>
</aui:form>
