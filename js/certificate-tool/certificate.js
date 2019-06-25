//TODO
//check for text that's too long for cert and scale
//show cert only after click
//--done, but should this be a fade in or modal?
//printing/downloading


(function(){
  
  window.onload = bindClickHandler;
  
  function bindClickHandler(e){
    e.preventDefault();
    var submitButton = document.getElementById("form-submit");
    submitButton.onclick = createCertificate;
    return false;
  }
  
  function createCertificate(){
    //get and validate name, certificate SVG
    var container = getElement("certificate-container");
    var svg = getElement("certificate");
    var nameInput = getInput("name-input");
    var nameField = getElement("name-output");
    var dateField = getElement("date-output");
    if(!container||!svg||!nameInput||!nameField||!dateField){return false}
    
    //show cert
    //fadeIn(container);
    container.style.display = "block";
    
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
    var month = getMonthName(rawDate.getMonth());
    var day = rawDate.getDate();
    
    return month + " " + day + ", " + year;

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
        console.log("output: " + output)
        return output;
      }
  }
  
function getInput(id){
  //get input (return false if empty)
    var input = document.getElementById(id).value;
    
    if(!input || input==""){
      console.log("Input is empty: " + id);
      return false;
    } else {
      return input;
    }
}

function setCertElement(element, content, svg){
    element.innerHTML = content;
    centerSvgElement(element, svg);
}

function centerSvgElement(element, parentSVG){
    var svgDimensions = parentSVG.getAttribute("viewBox").split(" ");
    var svgWidth = svgDimensions[2];
    var textWidth = element.getBoundingClientRect().width;
    //var textWidth = svgWidth - textX * 2;
    var textXNew = (svgWidth - textWidth) / 2;
    
    element.setAttribute("x", textXNew);
}
  
})();

