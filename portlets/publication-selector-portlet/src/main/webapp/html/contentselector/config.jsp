<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>

<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.GetterUtil" %>
<%@ page import="com.liferay.portal.kernel.util.StringPool" %>

<portlet:defineObjects />

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<%  
String includeArticle_cfg = GetterUtil.getString(portletPreferences.getValue("contentSelectorIncludeArticles", ""));
String articleNotFound_cfg = GetterUtil.getString(portletPreferences.getValue("contentSelectorArticleNotFound", ""));
String archiveUrl_cfg = GetterUtil.getString(portletPreferences.getValue("contentSelectorArchiveUrl", ""));
%>

<aui:form action="<%= configurationURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />

    <aui:input name="preferences--contentSelectorIncludeArticles--" type="textarea" value="<%= includeArticle_cfg %>" />
    
    <aui:input name="preferences--contentSelectorArticleNotFound--" type="text" value="<%= articleNotFound_cfg %>" />
    
    <aui:input name="preferences--contentSelectorArchiveUrl--" type="text" value="<%= archiveUrl_cfg %>" />

    <aui:button-row>
       <aui:button type="submit" />
    </aui:button-row>
</aui:form>