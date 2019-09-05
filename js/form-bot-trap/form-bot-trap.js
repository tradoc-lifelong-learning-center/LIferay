(function(){

  setBotTrap();

  function setBotTrap(){
    //this is the ID of the <form> element you'd like to protect
    var formId = "_1_WAR_webformportlet_INSTANCE_whYYfcHck9i5_fm";
    //this is the ID if the <input> you're using as a check
    var trapFieldId = "_1_WAR_webformportlet_INSTANCE_whYYfcHck9i5_field6";



    var form = document.getElementById(formId);
    var field = document.getElementById(trapFieldId);
    field.parentNode.style.display = "none";
    var submit = form.getElementsByTagName("button")[0];

    submit.addEventListener('click', function (event) {

      if(field.value!=""){
        submit.setAttribute("disabled","true")

        submit.insertAdjacentHTML('beforebegin', "<div class='alert alert-danger'><p>Thank you for attempting to contact us. If you are receiving this message, there is an issue with your submission. </p><p>Please use another web browser or contact the <a href='https://athd.army.mil/app/ask'>JAGU Support Desk</a> for further assistance.</p></div>");



        return false;
      }


    }, false);
  }

})();
