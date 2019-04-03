<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<portlet:defineObjects />

<jsp:useBean id="cs" class="com.tjaglcs.plugins.ContentSelector" scope="session"/>

<!-- note: in current set up, volumeSet var has to be called first -->
<c:set var="groupId" scope="session" value="${cs.getGroupId(renderRequest)}" />
<c:set var="volumeSet" value="${cs.setVolumes(renderRequest) }" />

<c:choose>

	<c:when test="${!volumeSet }">
		<p>Welcome to the content selector portlet! Please add some web content articles to the portlet configuration.</p>
	</c:when>
	
	
	<c:otherwise>
		<c:set var="volumes" value="${cs.getVolumes() }" />
		<c:set var="currentVolume" value="${cs.fetchCurrentVolume(renderRequest)}" />
		<c:set var="archiveUrl" value="${cs.getArchiveUrl()}" />
		<c:set var="isMostRecent" scope="session" value="${cs.isMostRecent()}" />
	
		<div class="mlr-selector-container">

			<aui:form cssClass="content-selector-form">
			
				<c:if test="${isMostRecent}">
					<h3 class="most-recent-title">Most Recent Issue</h3>
				</c:if>
			
			    <aui:fieldset cssClass="selector-fieldset">
			        <aui:select label="" id="options" name="articleId" showEmptyOption="false" cssClass="dropdown" helpMessage="Select an issue.">
			
						<aui:option value="selectAnIssue">Select a volume</aui:option>
			
						<c:forEach items="${volumes}" var = "volume" varStatus="i"> 
					    	<aui:option value="${volume.getQueryString().getQueryString()}">Volume ${volume.getVolumeNumber()}</aui:option>
			 	        </c:forEach>
				        
				        <aui:option value="browseArchive">Browse archived issues</aui:option>
						
			        </aui:select>
			
			        <aui:button value=">" id="btnSubmit" cssClass="btn btn-primary"/>
			    </aui:fieldset>
			    
			</aui:form>
			
			
			<c:choose>
				<c:when test = "${archiveUrl==''}">
			       <p>Error: Please use the portlet configuration to add the PDF archive URL.</p>
			    </c:when>
	    			    
			    <c:otherwise>
			    	<c:forEach items="${currentVolume.getArticles()}" var = "article" varStatus="i"> 
			    	<div class="content-selector-toc">
			    		<liferay-ui:journal-article showTitle="false" articleId="${article.getArticleId() }" groupId="${groupId}" />
			    		<hr class="content-selector-separator"/>
			    	</div>
			    		
			    		
			 	    </c:forEach>
			
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
	</c:otherwise>

</c:choose>







