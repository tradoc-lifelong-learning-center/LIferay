(function(){
  //this is the ID of the <form> element you'd like to protect
  var form = document.getElementById('_1_WAR_webformportlet_INSTANCE_XW4DVjZBFV8G_fm');
  //this is the ID if the <input> you're using as a check
  var field = document.getElementById('_1_WAR_webformportlet_INSTANCE_XW4DVjZBFV8G_field4');

  //field will be hidding with look and field advanced styling:
  //#_1_WAR_webformportlet_INSTANCE_XW4DVjZBFV8G_fm .control-group:last-of-type {	display:none;}

  form.addEventListener("submit",function(e) {
      e.preventDefault();

  	if(field.value!=""){
  		form.setAttribute("action","/web/guest/support-error");
  		return false;
  	}
  	return true
    });
})();
