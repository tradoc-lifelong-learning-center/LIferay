<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<portlet:defineObjects />

<jsp:useBean id="cd" class="com.tjaglcs.plugins.ContentDashboard"/>

<c:set var="stuff" value="${cd.doStuff(renderRequest) }" />
<c:set var="sitemap" value="${cd.fetchSiteMap(renderRequest) }" />

This is the <b>Content Dashboard</b> portlet in View mode.

<%-- 

<c:forEach items="${stuff }" var = "item" varStatus="i">

	<p><c:out value="${item.getFriendlyURL() }"/></p>
</c:forEach>
 --%>


<div>

<c:out value="${sitemap }"/>
</div>
