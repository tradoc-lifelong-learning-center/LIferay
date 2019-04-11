<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<portlet:defineObjects />

<jsp:useBean id="st" class="com.tjaglcs.plugins.ContentSelector"/>

<c:set var="pubData" value="${st.fetchPublication(renderRequest) }" />
<c:set var="currentVolume" value="${pubData.getSelectedVolume() }" />

<aui:form cssClass="content-selector-form">
    <aui:fieldset cssClass="selector-fieldset">
    
    
    	<aui:input name="year-input" type="range" min="1950" max="2019"></aui:input>
    	
    	<input type="range" multiple min="1950" max="2019"/> 
    	
    	<input type="number" multiple min="1950" max="2019"/>
    
        <aui:select label="" id="volumeOptions" name="volume" showEmptyOption="false" cssClass="dropdown" helpMessage="Select a volume.">

			<aui:option value="selectAVolume">Select a volume</aui:option>
			
			<%-- populated by JSON from Java bean --%>
		    
			
        </aui:select>

		<aui:select label="" id="issueOptions" name="issue" showEmptyOption="false" cssClass="dropdown" helpMessage="Select an issue." disabled="true">

			<aui:option value="selectAnIssue">Select an issue</aui:option>
		    <%-- populated by JSON from Java bean --%>
			
        </aui:select>

        <aui:button value=">" id="btnSubmit" cssClass="btn btn-primary"/>
    </aui:fieldset>
    
</aui:form>

<div>
	
	<h3>Volume <c:out value="${currentVolume.getNumber() }"/></h3>
	
	<c:forEach items="${currentVolume.getIssues()}" var = "issue" varStatus="i">
		 <h4>Issue <c:out value="${issue.getNumber() }"/></h4>
		 
		 <c:forEach items="${issue.getArticles()}" var = "article" varStatus="i">
		 	<h5><a href="${article.getURL() }"><c:out value="${article.getTitle() }"/></a></h5>
		 

		</c:forEach>
		 
		    	
	</c:forEach>


</div>

<aui:script use="aui-base, event, node">
    
    (function(){
    	var config = {
        		'namespace': '<portlet:namespace/>',
                'jsonData': ${pubData.getJson() },
                'volumeDropdown':document.getElementById('<portlet:namespace/>' + 'volumeOptions'),
                'issueDropdown':document.getElementById('<portlet:namespace/>' + 'issueOptions'),
                'submitButton':document.getElementById('btnSubmit')
        } //does submit button need a namespace?
        		
        //populate volume menu		
        populateMenu(config.volumeDropdown, config.jsonData.publication.volumes, 1970,5000)		
        		
        
        function getIssues(){
        	var volumeDropdown = A.one('#<portlet:namespace/>volumeOptions');
        	var issueDropdown = A.one('#<portlet:namespace/>issueOptions');
        	var jsonData = ${pubData.getJson() };
			
        	if(volumeDropdown.val()=="selectAVolume"){
        		issueDropdown.setAttribute("disabled");
        		issueDropdown.value=="selectAnIssue"; //TO DO: this isn't working. I'd like it to go back to "select an issue" if for some reason a user selects "select a volume"
        		return false;
        	}
        	
        	console.log("getting issues! You chose volume " + volumeDropdown.val());
        	issueDropdown.removeAttribute("disabled");
        	
        	var issues = jsonData.publication.volumes["volume" + volumeDropdown.val()].issues;
        	
        	populateMenu(issueDropdown, issues);

        } 
        
        
        function populateMenu(menu, items, startYear=0, endYear=9999){
        	console.log("will be working with: ")
        	console.log(items)
        	console.log("menu: ")
        	console.log(menu)
        	
        	//var menu = document.getElementById(menuId);
        	var fragment = document.createDocumentFragment();
        	
        	for(var prop in items){
        		
        		if(items[prop].year<startYear || items[prop].year > endYear){
        			continue;
        		}
        		
        		var option = document.createElement("option");
        		option.innerHTML = items[prop].number + ", year " + items[prop].year;
        		option.setAttribute("value",items[prop].number);
        		//consider changing to "number" for easier reuse
            	console.log(items[prop].number);
            	fragment.appendChild(option);

            	for(var artProp in items[prop].articles){
            		console.log(items[prop].articles[artProp].title)
            		
            	}
            	
            }
        	
        	menu.appendChild(fragment);
        } 
        		
        config.volumeDropdown.addEventListener('change', function(event){
        	getIssues();
         }); 
        
        config.submitButton.addEventListener('click', function(event){
        	var jsonData = ${pubData.getJson() };
        	var volumeDropdown = A.one('#<portlet:namespace/>volumeOptions');
        	var issueDropdown = A.one('#<portlet:namespace/>issueOptions');
        	
        	console.log("click!")
        	
        	
        	var pubCode = jsonData.publication.pubCode;
        	var volumeNumber = volumeDropdown.val();
        	var issueNumber = issueDropdown.val();
        	
        	
        	var queryString = getQueryString(pubCode,volumeNumber,issueNumber);
        	
         	if(issueDropdown.val()=="selectAnIssue" || volumeDropdown.val()=="selectAVolume") {
             	return false;
             } else {
            	var baseUrl = window.location.href.split('#')[0];
             	var url = baseUrl.split('?')[0] + queryString;
             	console.log("navigating to " + url);
             }

             window.location.href = url;
         }); 
    })();
 	
    function getQueryString(pubCode,volumeNumber,issueNumber){
    	return "?pub=" + pubCode + "&vol=" + volumeNumber + "&no=" + issueNumber;
    }
    
    //console.log("ns: " + config.namespace)
    //console.log("btn: " + config.submitButton)
    //console.log("vol: " + '<portlet:namespace/>' + 'volumeOptions')
    //console.log("issue: " + config.issueDropdown)
    

    /* Liferay.contentselectorsearchportlet.init(
        {
            namespace: '<portlet:namespace/>',
            jsonData: ${pubData.getJson() },
            volumeDropdownId:'volumeOptions',
            issueDropdownId:'issueOptions',
            buttonId:'btnSubmit'
        }
    ); */
    
    //Liferay.contentselectorsearchportlet.helloWorld();
    //console.log(Liferay.contentselectorsearchportlet)

    
    
    
    

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
	<p>${article.getTitle() }, volume ${article.getVolume() }, issue ${article.getIssue() }</p>
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