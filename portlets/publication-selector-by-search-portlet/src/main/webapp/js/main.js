AUI().use(
    'aui-base',
    function (A){
        Liferay.namespace('content-selector-search-portlet');
        

        Liferay.contentselectorsearchportlet = {

            init: function(config){
                var instance = this;
                instance._namespace = config.namespace;
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
);