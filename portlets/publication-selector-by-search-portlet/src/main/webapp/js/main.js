/*
Liferay.on(
    'allPortletsReady',

    
    This function gets loaded when everything, including the portlets, is on
    the page.
    

    function() {
    	var instance = this;
    	console.log("all portlets ready!");
    	console.log(instance._jsonData)
    }
);

AUI().use(
    'aui-base',
    function (A){
        Liferay.namespace('content-selector-search-portlet');
        

        Liferay.contentselectorsearchportlet = {
        	
        	
        		
            init: function(config){
                var instance = this;
                instance._namespace = config.namespace;
                instance._jsonData = config.jsonData;
                //instance._volumeDropdown = document.getElementById(config.volumeDropdownId);
                instance._volumeDropdown = config.volumeDropdownId;
                instance._issueDropdown = document.getElementById(config.issueDropdownId);
                instance._submitButton = document.getElementById(config.buttonId);
                
                console.log("vol: " + instance._volumeDropdown)
                
                //instance.populateMenu(instance._volumeDropdown, instance._jsonData.publication.volumes, 1970,5000)
            },
            
            helloWorld: function(){
            	var instance = this;
            	
        		console.log("hello world!");
        		console.log("JSON from hello:");
                console.log(instance._jsonData);
        	},
        	
        	populateMenu: function(menu, items, startYear=0, endYear=9999){
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
        	},
        	
        	getVolumes: function(){
        		
        	},
            
            getIssues: function(fs){
            	var instance = this;
            	//alert("hello world");
            	
            	var issueSelector = document.getElementById(instance._namespace + 'issueOptions');
            	issueSelector.removeAttribute('disabled');
            	
            	console.log("FS: " + fs);
            	
            	return("return!");
            }

        };
    }
);*/