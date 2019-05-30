(function(){
/*
This script reveals items in an SVG on button click
Items in SVG need to be grouped and given IDs
The map object defines the button IDs and contains an array of IDs of the elements that will be revealed on clicking that button
Hidden items will need to have an opacity:0 applied in the graphic software, and the opacity will need to be an inline attribute to work in IE11
*/
var map = getMapping();

for(var buttonId in map.flowchart){
	

	//loop buttons, add data element (to pass values onclick) and add click function
	var buttonElement = document.getElementById(buttonId);
	
	if(!buttonElement){
		console.log("couldn't get element for " + buttonId);
		continue;
	}
	
	//these are the IDs of elements that will be revealed onclick. Apply them as data attribute.
	var targetIds = map.flowchart[buttonId].join('|');
	buttonElement.setAttribute("data-target-ids",targetIds);
	buttonElement.onclick = revealItems;

}

//set up reveal all button
var showAllButton = document.getElementById("button--show-all");
showAllButton.onclick = showAll;

function showAll(){
	var allItems = [];

	for(var buttonId in map.flowchart){
	
		//buttons
		allItems.push(buttonId);
		var buttonElement = document.getElementById(buttonId);
		if(!buttonElement){continue}
		buttonElement.setAttribute("opacity",1);
		
		//other items
		for(var i = 0; i<map.flowchart[buttonId].length; i++){
			var element = document.getElementById(map.flowchart[buttonId][i]);
			if(!element){continue}
			element.setAttribute("opacity",1);
			}
	}
	
	
	
}



function revealItems(e){
	//getting attribute instead of dataset for IE11
	var targetIds = e.currentTarget.getAttribute("data-target-ids").split('|');
	for(var i = 0; i<targetIds.length; i++){
		var element = document.getElementById(targetIds[i]);
		
		if(!element){
			continue;
			}
			
		element.setAttribute("opacity",1);
	}
}


function getMapping(){

var json = JSON.parse('{   "flowchart": {     "button--yes--01": [       "flowbox--question--01", 	  "button--yes--02", 	  "button--no--01"     ], 	"button--yes--02": [       "flowbox--question--02", 	  "answer--no--01", 	  "button--yes--03"     ], 	"button--yes--03": [       "answer--yes--02", 	  "flowbox--question--04", 	  "answer--no--03", 	  "button--no--02"     ], 	"button--yes--04": [       "arrow--straight--01", 	  "flowbox--question--04", 	  "answer--no--03", 	  "button--no--02"     ], 	"button--no--01": [       "answer--yes--01", 	  "flowbox--question--04", 	  "answer--no--03", 	  "button--no--02"     ], 	"button--no--02": [       "flowbox--question--03", 	  "answer--no--02", 	  "answer--yes--03"     ], 	"button--no--03": [       "flowbox--question--05", 	  "button--yes--04", 	  "answer--yes--04"     ]   } }');
return json;

}



})();