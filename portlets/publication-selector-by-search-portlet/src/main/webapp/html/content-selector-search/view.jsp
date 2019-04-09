<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<portlet:defineObjects />

<jsp:useBean id="st" class="com.tjaglcs.plugins.ContentSelector"/>

<script type="text/javascript">
<%@ include file="/js/main.js" %>
</script>

This is the <b>TestPortlet</b> portlet in View mode.

<c:set var="pubData" value="${st.fetchPublication(renderRequest) }" />

<c:set var="volumeArray" value="${pubData.getVolumes() }" />


<c:forEach items="${volumeArray}" var = "volumeObj" varStatus="i">

	<p>volume object: ${volumeObj }</p>
</c:forEach>

<aui:form cssClass="content-selector-form">
    <aui:fieldset cssClass="selector-fieldset">
    
    
    	<aui:input name="year-input" type="range" min="1950" max="2019"></aui:input>
    	
    	<input type="range" multiple min="1950" max="2019"/> 
    	
    	<input type="number" multiple min="1950" max="2019"/>
    
    
        <aui:select label="" id="volumeOptions" name="volume" showEmptyOption="false" cssClass="dropdown" helpMessage="Select a volume." onChange="getIssues()">

			<aui:option value="selectAnIssue">Select a volume</aui:option>

		    <c:forEach items="${volumeArray}" var = "volumeObj" varStatus="i">
		    	<aui:option value="${volumeObj.getNumber() }">${volumeObj.getNumber() }</aui:option>
		    	
			</c:forEach>
			
        </aui:select>

		<aui:select label="" id="issueOptions" name="issue" showEmptyOption="false" cssClass="dropdown" helpMessage="Select an issue." disabled="true">

			<aui:option value="selectAnIssue">Select an issue</aui:option>

		    
			
        </aui:select>

        <aui:button value=">" id="btnSubmit" cssClass="btn btn-primary"/>
    </aui:fieldset>
    
</aui:form>



<aui:script use="aui-base, event, node">
    var btn = A.one('#btnSubmit');
    
    
    console.log(Liferay);
    
    buildJSON();
    
    function buildJSON(){
    	//console.log("volume array: ")
    	//console.log(${volumeArray.size()});
    	
    	var jsonData = ${pubData.getJson() };
    	console.log(jsonData);
    	
    	//console.log(jsonData.publication.name);
    }

    Liferay.contentselectorsearchportlet.init(
        {
            namespace: '<portlet:namespace/>'
        }
    );

    getIssues = function(){
    	var jsonData = ${pubData.getJson() };
    	var volumeOptions = A.one('#<portlet:namespace/>volumeOptions');
    	//var issueOptions = A.one('#<portlet:namespace/>issueOptions');
    	
    	//var issues = jsonData.publication.
    	
        //var fs = Liferay.contentselectorsearchportlet.getIssues(volNo);
        //console.log(fs);
        
        //console.log(volumeOptions.val());
        //var value = volumeOptions.val();
        
        //don't think I can mix JS/JSTL like this. 
        //Might have to be a two step thing - select volume, refresh page with vol query string, then populate issue and allow selection
        //unless I can load hash map into JS/JSON?
        //console.log(${volumeArray} + value.get(0).getNumber() });
        
        //var url = window.location.href.split('?')[0] + "?vol=" + volumeOptions.val();
        //window.location.href = url;
    }
    
    ///console.log("Group id: ");
    //console.log(${volumeArray.get(0).getNumber() });
</aui:script>






<h3>Found volumes:</h3>
<c:forEach items="${pubData.getVolumes() }" var = "volume" varStatus="i">
	<p>${volume.getNumber() }, year: ${volume.getYear() }</p>
</c:forEach>

<h3>Found issues:</h3>
<c:forEach items="${pubData.getIssues() }" var = "issue" varStatus="i">
	<p>${issue.getNumber() }</p>
</c:forEach>

<h3>Found articles:</h3>
<c:forEach items="${pubData.getArticles() }" var = "article" varStatus="i">
	<p>${article.getTitle() }, volume ${article.getVolume() }</p>
</c:forEach>

<%--  

<c:set var="volumeArray" value="${st.fetchVolumes(renderRequest) }" />

<p>pubData volume length: 
<c:out value="${pubData.getVolumes().size() }"/>
</p>


<c:forEach items="${volumeArray}" var = "volumeObj" varStatus="i">

	<p>volume object: ${volumeObj.getIssueList() }</p>
</c:forEach>

<aui:form cssClass="content-selector-form">
    <aui:fieldset cssClass="selector-fieldset">
        <aui:select label="" id="volumeOptions" name="volume" showEmptyOption="false" cssClass="dropdown" helpMessage="Select a volume." onChange="getIssues()">

			<aui:option value="selectAnIssue">Select a volume</aui:option>

		    <c:forEach items="${volumeArray}" var = "volumeObj" varStatus="i">
		    	<aui:option value="${volumeObj.getNumber() }">${volumeObj.getNumber() }</aui:option>
		    	
			</c:forEach>
			
        </aui:select>

		<aui:select label="" id="issueOptions" name="issue" showEmptyOption="false" cssClass="dropdown" helpMessage="Select an issue." disabled="true">

			<aui:option value="selectAnIssue">Select an issue</aui:option>

		    
			
        </aui:select>

        <aui:button value=">" id="btnSubmit" cssClass="btn btn-primary"/>
    </aui:fieldset>
    
</aui:form>



<aui:script use="aui-base, event, node">
    var btn = A.one('#btnSubmit');
    
    
    console.log(Liferay);
    
    buildJSON();
    
    function buildJSON(){
    	//console.log("volume array: ")
    	//console.log(${volumeArray.size()});
    	
    	var jsonData = ${pubData.getJson() };
    	console.log(jsonData);
    	
    	//console.log(jsonData.publication.name);
    }

    Liferay.contentselectorsearchportlet.init(
        {
            namespace: '<portlet:namespace/>'
        }
    );

    getIssues = function(){
    	var jsonData = ${pubData.getJson() };
    	var volumeOptions = A.one('#<portlet:namespace/>volumeOptions');
    	//var issueOptions = A.one('#<portlet:namespace/>issueOptions');
    	
    	//var issues = jsonData.publication.
    	
        //var fs = Liferay.contentselectorsearchportlet.getIssues(volNo);
        //console.log(fs);
        
        //console.log(volumeOptions.val());
        //var value = volumeOptions.val();
        
        //don't think I can mix JS/JSTL like this. 
        //Might have to be a two step thing - select volume, refresh page with vol query string, then populate issue and allow selection
        //unless I can load hash map into JS/JSON?
        //console.log(${volumeArray} + value.get(0).getNumber() });
        
        //var url = window.location.href.split('?')[0] + "?vol=" + volumeOptions.val();
        //window.location.href = url;
    }
    
    ///console.log("Group id: ");
    //console.log(${volumeArray.get(0).getNumber() });
</aui:script>
--%>



<%--
<c:set var="volumeArray" value="${st.fetchVolumes(renderRequest) }" />


<c:set var="articlesArray" scope="session" value="${st.fetchArticlesArray(renderRequest) }" />

<c:forEach items="${volumeArray}" var = "volumeObj" varStatus="i">
	<p>Volume: ${volumeObj.getNumber() }, Issue: ${volumeObj.getIssues() }</p>
</c:forEach>

<p>-------</p>

<c:forEach items="${articlesArray}" var = "articleObj" varStatus="i">
	<p>Title: ${articleObj.getTitle()}, Volume: ${articleObj.getVolume()}, Issue: ${articleObj.getIssue()}, Article ID: ${articleObj.getId()}</p> 
</c:forEach>

<aui:form cssClass="content-selector-form">
    <aui:fieldset cssClass="selector-fieldset">
        <aui:select label="" id="volumeOptions" name="volume" showEmptyOption="false" cssClass="dropdown" helpMessage="Select a volume." onChange="getIssues()">

			<aui:option value="selectAnIssue">Select a volume</aui:option>

		    <c:forEach items="${volumeArray}" var = "volumeObj" varStatus="i">
		    	<aui:option value="${volumeObj.getNumber()}">Volume ${volumeObj.getNumber()}</aui:option>
	        </c:forEach>
			
        </aui:select>
        
        <aui:select label="" id="issueOptions" name="issue" showEmptyOption="false" cssClass="dropdown" helpMessage="Select an issue." disabled="true">

			<aui:option value="selectAnIssue">Select an issue</aui:option>

		    <c:forEach items="${volumeArray}" var = "volumeObj" varStatus="i">
		    	<aui:option value="${volumeObj.getNumber()}">Volume ${volumeObj.getNumber()}</aui:option>
	        </c:forEach>
			
        </aui:select>

        <aui:button value=">" id="btnSubmit" cssClass="btn btn-primary"/>
    </aui:fieldset>
    
</aui:form>


<liferay-ui:journal-article showTitle="false" articleId="${articleId}" groupId="${groupId}" />



<aui:script use="aui-base, event, node">
    var btn = A.one('#btnSubmit');
    
    
    console.log(Liferay);

    Liferay.contentselectorsearchportlet.init(
        {
            namespace: '<portlet:namespace/>'
        }
    );

    getIssues = function(){
    	var volumeOptions = A.one('#<portlet:namespace/>volumeOptions');
    	var issueOptions = A.one('#<portlet:namespace/>issueOptions');
    	
    	volNo = ${volumeArray.get(0).getNumber() };
        var fs = Liferay.contentselectorsearchportlet.getIssues(volNo);
        //console.log(fs);
        
        console.log("option: " + ${volumeArray.get(0).getIssueList() });
    }
    
    ///console.log("Group id: ");
    //console.log(${volumeArray.get(0).getNumber() });
</aui:script>  --%>