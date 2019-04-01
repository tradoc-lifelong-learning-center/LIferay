<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<portlet:defineObjects />

<jsp:useBean id="cs" class="com.tjaglcs.plugins.ContentSelector" scope="session"/>

<!-- 
In class, need to be able to return the list of articles for the dropdown
	-If nothing in config, do something to handle error
Also needs to return the most recent articleId which will pop
 -->

<!-- note: in current set up, articleObjs var has to be called first -->
<c:set var="articleObjs" scope="session" value="${cs.getArticleObjs(renderRequest) }" />
<c:set var="articleId" scope="session" value="${cs.fetchCurrentArticleId(renderRequest)}" />
<c:set var="groupId" scope="session" value="${cs.getGroupId(renderRequest)}" />
<c:set var="isMostRecent" scope="session" value="${cs.isMostRecent()}" />
<c:set var="archiveUrl" scope="session" value="${cs.getArchiveUrl()}" />



<div class="mlr-selector-container">

<c:if test="${isMostRecent}">
	<h3 class="most-recent-title">Most Recent Issue</h3>
</c:if>


<aui:form cssClass="content-selector-form">
	<!--  <p class="mlr-selector-title">Select an issue or <a>view the archive</a></p> -->
    <aui:fieldset cssClass="selector-fieldset">
        <aui:select label="" id="options" name="articleId" showEmptyOption="false" cssClass="dropdown" helpMessage="Select an issue.">

			<aui:option value="selectAnIssue">Select a volume</aui:option>

		    <c:forEach items="${articleObjs}" var = "articleObj" varStatus="i">
		    	<aui:option value="${articleObj.getQueryString()}">Volume ${articleObj.getVolume()}</aui:option>
	        </c:forEach>
	        
	        <aui:option value="browseArchive">Browse archived issues</aui:option>
			
        </aui:select>

        <aui:button value=">" id="btnSubmit" cssClass="btn btn-primary"/>
    </aui:fieldset>
    
</aui:form>

<c:choose>
	<c:when test = "${articleId==0 || archiveUrl==''}">
       <p>Welcome to the content selector portlet. Please use the portlet configuration to add a list of articles for the selector, a fallback article, and a URL link to the PDF archive.</p>
    </c:when>
    
    
    <c:otherwise>
       <liferay-ui:journal-article showTitle="false" articleId="${articleId}" groupId="${groupId}" />
    </c:otherwise>
</c:choose>



</div>
 



<aui:script use="event, node">
    var btn = A.one('#btnSubmit');       
    var option = A.one('#<portlet:namespace/>options');

    btn.on('click', function(event){
    	if(option.val()=="browseArchive"){
        	var url = "${archiveUrl }";
        } else if(option.val()=="selectAnIssue") {
        	return false;
        } else {
        	var url = window.location.href.split('?')[0] + option.val();
        }

        window.location.href = url;
    });

</aui:script>


