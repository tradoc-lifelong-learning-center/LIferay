<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%@ page import="javax.portlet.RenderRequest" %>
<%@ page import="javax.portlet.PortletRequest" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>

<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow" %>
<%@ page import="com.liferay.portal.kernel.util.WebKeys" %>
<%@ page import="com.liferay.portal.util.PortletKeys" %>
<%@ page import="com.liferay.portlet.PortletURLFactoryUtil" %>
<%@ page import="com.liferay.portal.service.LayoutServiceUtil" %>
<%@ page import="com.liferay.portal.model.Layout" %>
<%@ page import="com.liferay.portal.theme.ThemeDisplay" %>

<%@ page import="com.liferay.portal.model.LayoutConstants" %>

<%@ page import="com.liferay.portal.kernel.search.BooleanQuery" %>
<%@ page import="com.liferay.portal.kernel.search.BooleanQueryFactoryUtil" %>
<%@ page import="com.liferay.portal.kernel.search.Document" %>
<%@ page import="com.liferay.portal.kernel.search.Field" %>
<%@ page import="com.liferay.portal.kernel.search.Hits" %>
<%@ page import="com.liferay.portal.kernel.search.SearchContext" %>
<%@ page import="com.liferay.portal.kernel.search.SearchContextFactory" %>
<%@ page import="com.liferay.portal.kernel.search.SearchEngineUtil" %>

<%@ page import="com.liferay.portal.kernel.search.Document" %><%@
page import="com.liferay.portal.kernel.search.FacetedSearcher" %><%@
page import="com.liferay.portal.kernel.search.FolderSearcher" %><%@
page import="com.liferay.portal.kernel.search.HitsOpenSearchImpl" %><%@
page import="com.liferay.portal.kernel.search.KeywordsSuggestionHolder" %><%@
page import="com.liferay.portal.kernel.search.OpenSearch" %><%@
page import="com.liferay.portal.kernel.search.OpenSearchUtil" %><%@
page import="com.liferay.portal.kernel.search.facet.AssetEntriesFacet" %><%@
page import="com.liferay.portal.kernel.search.facet.Facet" %><%@
page import="com.liferay.portal.kernel.search.facet.ScopeFacet" %><%@
page import="com.liferay.portal.kernel.search.facet.collector.FacetCollector" %><%@
page import="com.liferay.portal.kernel.search.facet.collector.TermCollector" %><%@
page import="com.liferay.portal.kernel.search.facet.config.FacetConfiguration" %><%@
page import="com.liferay.portal.kernel.search.facet.config.FacetConfigurationUtil" %><%@
page import="com.liferay.portal.kernel.search.facet.util.FacetFactoryUtil" %><%@
page import="com.liferay.portal.kernel.search.facet.util.RangeParserUtil" %><%@
page import="com.liferay.portal.kernel.util.DateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.xml.Element" %><%@
page import="com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil" %><%@
page import="com.liferay.portal.security.permission.comparator.ModelResourceComparator" %><%@
page import="com.liferay.portal.service.PortletLocalServiceUtil" %><%@
page import="com.liferay.portlet.asset.NoSuchCategoryException" %><%@
page import="com.liferay.taglib.aui.ScriptTag" %><%@
page import="com.liferay.util.PropertyComparator" %>
<%@ page import="com.liferay.portal.kernel.search.SearchContextFactory" %>

<%@ page import="com.liferay.portal.kernel.search.SearchContext" %>
<%@ page import="com.liferay.portal.kernel.search.BooleanClause" %>
<%@ page import="com.liferay.portal.kernel.search.BooleanClauseFactoryUtil" %>
<%@ page import="com.liferay.portal.kernel.search.BooleanClauseOccur" %>
<%@ page import="com.liferay.portal.kernel.search.StringQueryFactoryUtil" %>
<%@ page import="com.liferay.portal.kernel.search.Query" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>

<%@ page import="com.liferay.portal.kernel.search.IndexerRegistryUtil" %>
<%@ page import="com.liferay.portal.kernel.search.Indexer" %>
<%@ page import="com.liferay.portal.kernel.search.TermQuery" %>
<%@ page import="com.liferay.portal.kernel.search.TermQueryFactory" %>



<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer" %>
<%@ page import="com.liferay.portal.kernel.exception.PortalException" %>
<%@ page import="com.liferay.portal.kernel.exception.SystemException" %>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ page import="com.liferay.portal.kernel.log.Log" %>
<%@ page import="com.liferay.portal.kernel.log.LogFactoryUtil" %>
<%@ page import="com.liferay.portal.kernel.search.Indexer" %>
<%@ page import="com.liferay.portal.kernel.search.IndexerRegistryUtil" %>
<%@ page import="com.liferay.portal.kernel.search.SearchContext" %>
<%@ page import="com.liferay.portal.kernel.search.SearchContextFactory" %>
<%@ page import="com.liferay.portal.kernel.search.Hits" %>
<%@ page import="com.liferay.portal.kernel.search.Document" %>
<%@ page import="com.liferay.portal.kernel.search.Field" %>
<%@ page import="com.liferay.portal.kernel.util.StringPool" %>
<%@ page import="com.liferay.portal.kernel.util.GetterUtil" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>
<%@ page import="com.liferay.portal.util.PortalUtil" %>

<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>

<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>

<%@ page import="javax.portlet.PortletURL" %>

<%@ page import="com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.journal.model.JournalArticle" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<jsp:useBean id="ps" class="com.tjaglcs.pubsearch.PublicationSearch"/>



<liferay-portlet:renderURL varImpl="searchURL">
        <portlet:param name="mvcPath" value="/html/publication-search/view_search.jsp" />
</liferay-portlet:renderURL>

<liferay-portlet:renderURL varImpl="viewURL">
        <portlet:param name="mvcPath" value="/html/publication-search/view.jsp" />
</liferay-portlet:renderURL>


<c:set var="setRequest" value="${ps.setRequest(renderRequest) }" />
<c:set var="setURL" value="${ps.setPortletUrl(searchURL) }" />
<c:set var="setFields" value="${ps.setFields() }" />
<c:set var="keywords" value="${ps.getKeywords() }" />
<c:set var="results" value="${ps.getResults() }" />
<c:set var="searchContainer" value="${ps.getSearchContainer() }" />





<aui:form action="${searchURL }" method="get" name="fm">
    <liferay-portlet:renderURLParams varImpl="searchURL" />

    <liferay-ui:header
        backURL="${viewURL.toString() }"
        title="search"
    />

    <div class="search-form">
        <span class="aui-search-bar">
            <aui:input inlineField="${true }" label="" name="keywords" size="30" title="search-entries" type="text" />

            <aui:button type="submit" value="search" />
        </span>
    </div>
</aui:form>

<p>keywords: <c:out value="${keywords }"/></p>
<p>results: <c:out value="${results.size() }"/></p>



<%-- Now display results! --%>
<c:forEach items="${results }" var = "result" varStatus="i">
	<p><b><c:out value="${result.getField('title').getValue() }"/></b></p>
	<%-- <p><c:out value="${result }"/></p> --%>
</c:forEach>



<%--  
<%

ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

System.out.println("WebKeys: " + WebKeys.SEARCH_CONTAINER_RESULT_ROW);
System.out.println("request: " + request);
System.out.println("resultRow: " + row);

Document document = (Document)row.getObject();

PortletURL viewFullContentURL = null;

viewFullContentURL = _getViewFullContentURL(request, themeDisplay, PortletKeys.ASSET_PUBLISHER, document);

viewFullContentURL.setParameter("struts_action", "/asset_publisher/view_content");

%>

<%!




private PortletURL _getViewFullContentURL(HttpServletRequest request, ThemeDisplay themeDisplay, String portletId, Document document) throws Exception {
	
	System.out.println("request: " + request);
	System.out.println("themeDisplay: " + themeDisplay);
	System.out.println("portletId: " + portletId);
	System.out.println("document: " + document);
	
	long groupId = GetterUtil.getLong(document.get(Field.GROUP_ID));

	if (groupId == 0) {
		Layout layout = themeDisplay.getLayout();

		groupId = layout.getGroupId();
	}

	long scopeGroupId = GetterUtil.getLong(document.get(Field.SCOPE_GROUP_ID));

	if (scopeGroupId == 0) {
		scopeGroupId = themeDisplay.getScopeGroupId();
	}

	long plid = LayoutConstants.DEFAULT_PLID;

	Layout layout = (Layout)request.getAttribute(WebKeys.LAYOUT);

	if (layout != null) {
		plid = layout.getPlid();
	}

	if (plid == 0) {
		plid = LayoutServiceUtil.getDefaultPlid(groupId, scopeGroupId, portletId);
	}

	PortletURL portletURL = PortletURLFactoryUtil.create(request, portletId, plid, PortletRequest.RENDER_PHASE);

	//portletURL.setPortletMode("PortletMode.VIEW");
	//portletURL.setWindowState("WindowState.MAXIMIZED");

	return portletURL;
}
%>

--%>




<%-- 
<liferay-ui:search-container
			searchContainer="${searchContainer }"
			total="5">
			
			<liferay-ui:search-container-results
				results="${results }"
			/>
			
			<liferay-ui:search-container-row
				className="com.liferay.portal.kernel.search.Document"
				escapedModel="<%= false %>"
				keyProperty="UID"
				modelVar="resultDoc"
				stringKey="<%= true %>"
			>
			
			<p><a href="${viewFullContentURL }">${resultDoc.getField("title").getValue() }</a></p>
			<p>${themeDisplay }</p>
			
			</liferay-ui:search-container-row>
			
</liferay-ui:search-container>
 --%>





