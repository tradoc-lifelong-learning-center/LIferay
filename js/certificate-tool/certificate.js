(function(){
  String.prototype.replaceAll = function(target, replacement) {
    return this.split(target).join(replacement);
  };

  window.onload = function(){
    //IF there's a name param, generate certificate
    var params = getParamsObj();
    var formDiv = getElement("form-container");
    var errorDiv = getElement("error-container");

    if(params.name){
      createCertificate();
      bindClickHandler();
    } else if(params.completed=="true"){
      formDiv.style.display = "block";
    } else {
      //getElement("generateCertForm").style.display = "block";
      errorDiv.style.display = "block";
    }

    //getElement("loading-animation").style.display = "none";
    getElement("loading-animation").remove();

  };

  function bindClickHandler(){
    var printButton = getElement("printCertButton");
    printButton.onclick = print;
  }

  function print(){
    window.print();
  }

  function createCertificate(){
    //get and validate name, certificate SVG
    var container = getElement("certificate-container");
    var svg = getElement("certificate");
    var params = getParamsObj();
    var nameInput = params.name;
    var nameField = getElement("name-output");
    var printForm = getElement("printCertForm");
    var dateField = getElement("date-output");
    if(!container||!svg||!nameInput||!nameField||!printForm){return false}

    //show cert
    container.style.display = "flex";
    container.style.opacity = "100";
    printForm.style.display = "block";

    //get date
    var date = getFormattedDate();

    //set name
    setCertElement(nameField, nameInput, svg);

    //set date
    setCertElement(dateField, date, svg);


  }

  /*
  function fadeIn(element){
    element.style.display = "block";
    element.style.opacity = 100;
  }
  */

  function getFormattedDate(){
    var rawDate = new Date();

    var year = rawDate.getFullYear();
    var monthNumber = rawDate.getMonth() + 1;
    var monthText = getMonthName(rawDate.getMonth());
    var day = rawDate.getDate();

    //return  day + " " + monthText + " " + year;
    return  monthNumber + "/" + day + "/" + year;

  }

  function getMonthName(num){
    var monthNames = {
      "0":"January",
      "1":"February",
      "2":"March",
      "3":"April",
      "4":"May",
      "5":"June",
      "6":"July",
      "7":"August",
      "8":"September",
      "9":"October",
      "10":"November",
      "11":"December",
    }

    return monthNames[num];
  }


  function getElement(id){
    var output = document.getElementById(id);

    if(!output){
        console.log("Can't find output the id " + id)
        return false;
      } else{
        //console.log("output: " + output)
        return output;
      }
  }

/*function getInput(id){
  //get input (return false if empty)
    var input = document.getElementById(id).value;

    if(!input || input==""){
      console.log("Input is empty: " + id);
      return false;
    } else {
      return input;
    }
}*/

function setCertElement(element, content, svg){
    element.innerHTML = content;
    centerSvgElement(element, svg);
}

function centerSvgElement(element, parentSVG){
    var svgDimensions = parentSVG.getAttribute("viewBox").split(" ");
    var svgWidth = svgDimensions[2];
    var svgClientWidth = parentSVG.getBoundingClientRect().width;
    var textWidth = element.getBoundingClientRect().width;

    //var ratio = svgClientWidth / svgWidth;
    var ratio = svgWidth / svgClientWidth;

    var textXNew = (svgWidth - (textWidth * ratio)) / 2;

    element.setAttribute("x", textXNew);

    if(textWidth>svgClientWidth){
        //check if text doesn't fit and resize if needed
        resizeTextElement(element, parentSVG);
    }

}

function resizeTextElement(element, parentSVG){
    var ptSize = element.getAttribute("font-size");

    if(!ptSize || ptSize<=10){
        return false;
    }

    element.setAttribute("font-size", ptSize - 1);
    centerSvgElement(element, parentSVG)
}


function toObject(arr, separator) {
  var rv = {};
  for (var i = 0; i < arr.length; ++i)
    if (arr[i] !== undefined) {
      //split param key/value into object key/value, and replace + with space
      var rvKey = arr[i].split(separator)[0];

      try{
        var rvValue = decodeURIComponent(arr[i].split(separator)[1].replaceAll("\+"," "));

      } catch(e){
        var rvValue = "ERROR";
      }

      rv[rvKey] = rvValue;
    }
    return rv;
    }

function getParamsObj(){
  var paramsArr = window.location.search.replace("?","").split("&");

  return toObject(paramsArr, "=");

}

})();
