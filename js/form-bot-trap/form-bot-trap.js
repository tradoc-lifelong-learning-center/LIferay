(function(){

  window.onload = setBotTrap;

  function setBotTrap(){
    //this is the ID of the <form> element you'd like to protect
    var formId = "_1_WAR_webformportlet_INSTANCE_tAhXRgkTfBlm_fm";
    //this is the ID if the <input> you're using as a check
    var trapFieldId = "_1_WAR_webformportlet_INSTANCE_tAhXRgkTfBlm_field4";


    //var formIds = ['_1_WAR_webformportlet_INSTANCE_whYYfcHck9i5_fm', '_1_WAR_webformupdatetjaglcsportlet_INSTANCE_JXx1vdNCNDYY_fm'];
    //var forms = document.getElementsByTagName("form");


    var form = document.getElementById(formId);
    var field = document.getElementById(trapFieldId);
    field.parentNode.style.display = "none";
    var submit = form.getElementsByTagName("button")[0];
    var description = form.getElementsByClassName("description")[0];


    submit.addEventListener('click', function (event) {

      if(field.value!=""){
        submit.setAttribute("disabled","true")

        //form.innerHTML = "<div class='alert alert-danger'>Form Error!</div>"

        description.insertAdjacentHTML('afterend', "<div class='alert alert-danger'><p>Thank you for attempting to contact us. If you are receiving this message, there is an issue with your submission. </p><p>Please use another web browser or contact the <a href='https://athd.army.mil/app/ask'>JAGU Support Desk</a> for further assistance.</p></div>");



        return false;
      }


    }, false);
  }




  /*form.addEventListener("submit",function(e) {
      e.preventDefault();

      e.target.setAttribute("disabled","true")

  	if(field.value!=""){
  		form.setAttribute("action","/web/guest/support-error");
  		return false;
  	}
  	return true
  });*/
})();
