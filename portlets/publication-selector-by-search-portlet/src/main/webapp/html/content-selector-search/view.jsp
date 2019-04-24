<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<portlet:defineObjects />

<jsp:useBean id="st" class="com.tjaglcs.plugins.ContentSelector"/>

<c:set var="pubData" value="${st.fetchPublication(renderRequest) }" />
<c:set var="currentVolume" value="${pubData.getSelectedVolume() }" />
<c:set var="currentIssue" value="${pubData.getSelectedIssue() }" />
<c:set var="isSingleIssue" value="${pubData.getIsSingleIssue() }" />







<aui:form cssClass="content-selector-form">

	<div id="noUiSlider"  class="noUi--slider-outer-container">
		<!-- TODO get values from bean -->
		<span class="noUi--year-label" id="noUiSliderMin">${pubData.getStartYear() }</span>
		<div class="noUi--slider-inner-container">
			<div id="noUiSliderRange" data-min-year="${pubData.getStartYear() }" data-max-year="${pubData.getEndYear() }"></div>
		</div>
		
		<span class="noUi--year-label" id="noUiSliderMax">${pubData.getStartYear() }</span>
	</div>
		
    <aui:fieldset cssClass="selector-fieldset">

        <aui:select label="" id="volumeOptions" name="volume" showEmptyOption="false" cssClass="dropdown" helpMessage="Select a volume.">

			<aui:option value="selectAVolume">Select a volume</aui:option>
			
			<%-- populated by JSON from Java bean --%>
		    
			
        </aui:select>
        
        <c:if test="${isSingleIssue }">
	        <aui:select label="" id="issueOptions" name="issue" showEmptyOption="false" cssClass="dropdown" helpMessage="Select an issue." disabled="true">
	
				<aui:option value="selectAnIssue">Select an issue</aui:option>
			    <%-- populated by JSON from Java bean --%>
				
	        </aui:select>
        </c:if>

		
		
		<aui:button value=" > " id="btnSubmit" cssClass="btn btn-primary" aria-label="Submit"/>

        
    </aui:fieldset>
    
</aui:form>

<div>
	

	
	
	<c:forEach items="${currentIssue }" var = "issue" varStatus="i">
		<section class="volume-container">
			<h2 id="volume${currentVolume.getNumber() }">Volume <c:out value="${currentVolume.getNumber() }"/>, Issue <c:out value="${issue.getNumber() }"/></h2>
			<p class="year-label"><c:out value="${issue.getYear() }"/> Online Edition</p>
			 
			 <nav aria-labelledby="volume${currentVolume.getNumber() }">
				 <c:forEach items="${issue.getArticles()}" var = "article" varStatus="i">
				 	<p class="toc-entry"><a href="${article.getURL() }"><c:out value="${article.getTitle() }"/></a></p>
				 	<p class="author-names">${article.getAuthors() }</p>
				 </c:forEach>
			 </nav>
			 
		</section>
		
		 
		    	
	</c:forEach>


</div>

<aui:script use="aui-base, event, node">
    
    (function(){
    	var config = {
        		'namespace': '<portlet:namespace/>',
                'jsonData': ${pubData.getJson() },
                'volumeDropdown':document.getElementById('<portlet:namespace/>' + 'volumeOptions'),
                'issueDropdown':document.getElementById('<portlet:namespace/>' + 'issueOptions'),
                'submitButton':document.getElementById('btnSubmit'),
                'isSingleIssue':${isSingleIssue }
        } //does submit button need a namespace?
        		
        //populate volume menu		
        //populateMenu(config.volumeDropdown, config.jsonData.publication.volumes,undefined,undefined);		
    	buildSlider();		
    	
    	
        
        function getIssues(){
        	var volumeDropdown = config.volumeDropdown;
        	var issueDropdown = config.issueDropdown;
        	var jsonData = ${pubData.getJson() };
			
        	if(volumeDropdown.value=="selectAVolume"){
        		disableMenu(issueDropdown);
        		issueDropdown.value="selectAnIssue"; 
        		return false;
        	}
        	
        	issueDropdown.removeAttribute("disabled");
        	
        	var issues = jsonData.publication.volumes["volume" + volumeDropdown.value].issues;
        	
        	populateMenu(issueDropdown, issues, undefined,undefined);

        } 
        
        
        function clearMenu(menu){
        	console.log("attempting to clear " + menu);
        	//clear existing options, skipping the first
			while (menu.children.length > 1) {
				menu.removeChild(menu.lastChild);
			}
        }
        
        
        function populateMenu(menu, items, startYear, endYear){
        	if(!startYear){
        		startYear=0;
        	}
        	
        	if(!endYear){
        		endYear=9999;
        	}
        	
        	//if single issue, clear sub (issue) menu
        	clearMenu(menu);	

        	
        	
        	var fragment = document.createDocumentFragment();
        	
        	for(var prop in items){
        		
        		if(parseInt(items[prop].year)<startYear || parseInt(items[prop].year)>endYear){
        			continue;
        		}
        		
        		var option = document.createElement("option");
        		option.innerHTML = items[prop].number + ", year " + items[prop].year;
        		option.setAttribute("value",items[prop].number);
            	fragment.appendChild(option);

            	for(var artProp in items[prop].articles){
            		console.log(items[prop].articles[artProp].title);
            		
            	}
            	
            }
        	
        	menu.appendChild(fragment);
        } 
        
        if(config.isSingleIssue){		
	        config.volumeDropdown.addEventListener('change', function(event){
	        	getIssues();
	         }); 
        }
        
        config.submitButton.addEventListener('click', function(event){
        	var jsonData = ${pubData.getJson() };
        	var volumeDropdown = config.volumeDropdown;
        	
        	if(config.isSingleIssue){		
        		var issueDropdown = config.issueDropdown;
        		} else{
        		var issueDropdown = null;
        		}
        	
        	
        	var pubCode = jsonData.publication.pubCode;
        	var volumeNumber = volumeDropdown.value;
        	if(config.isSingleIssue){		
        		var issueNumber = issueDropdown.value;
        		} else{
        			var issueNumber=-1;
        		}
        	
        	
        	var queryString = getQueryString(pubCode,volumeNumber,issueNumber);
        	
         	if(volumeDropdown.value=="selectAVolume" || (issueDropdown && issueDropdown.value=="selectAnIssue")) {
             	return false;
             } else {
            	var baseUrl = window.location.href.split('#')[0];
             	var url = baseUrl.split('?')[0] + queryString;
             	console.log("navigating to " + url);
             }

             window.location.href = url;
         }); 
        
        
        function getQueryString(pubCode,volumeNumber,issueNumber){
            
        	var queryString = "";
        	queryString+="?pub=" + pubCode + "&vol=" + volumeNumber;
        	if(issueNumber>0){
        	queryString+= "&no=" + issueNumber;
        	}
        	
        	return queryString;
        }
        
        
        function buildSlider() {
        	var instance = this;
        	
        	var slider = document.querySelector('#noUiSliderRange');
        	var min = parseInt(slider.dataset.minYear);
        	var max = parseInt(slider.dataset.maxYear);
        	
        	var minInput = document.querySelector('#noUiSliderMin');
        	var maxInput = document.querySelector('#noUiSliderMax');
        	
        	noUiSlider.create(slider, {
        	    start: [min, max],
        	    connect: true,
        	    padding:0,
        	    step:1,
        	    range: {
        	        'min': min,
        	        'max': max
        	    },
        	    format:{
        	    	to: function(value){
        	    		return parseInt(value);
        	    	},
        	    	from: function(value){
        	    		return parseInt(value);
        	    	},
        	    }
        	});
	
        	slider.noUiSlider.on('update', function( values, handle ) {
        		minInput.innerHTML = values[0];
        		maxInput.innerHTML = values[1];
        		
        		//re-populate volume selector, clear and disable issue selector
        		populateMenu(config.volumeDropdown, config.jsonData.publication.volumes, values[0],values[1]);
        		
        		if(config.isSingleIssue){
        			clearMenu(config.issueDropdown);
        			disableMenu(config.issueDropdown);
            	}
        		
        		
        	});
        	

        		
        }
        
        function disableMenu(menu){
        	menu.setAttribute("disabled", "disabled");
        }
        
    })();
 	
    

    

</aui:script>




<h2 style="margin-top:6em;">Testing data</h2>
<p>is single issue? <c:out value="${isSingleIssue }"/></p>
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

