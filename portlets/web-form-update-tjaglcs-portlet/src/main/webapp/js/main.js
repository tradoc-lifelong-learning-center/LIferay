
(function(){
	
	window.onload = bindClickHandlers;

	function bindClickHandlers(){
		//buttons
		//#modalCloser
		//#modalLauncher
		
		var openButton = document.getElementById('modalLauncher');
		var closeButton = document.getElementById('modalCloser');
		
		var modalId = openButton.dataset.target;
		
		console.log("modalId: " + modalId)
		
		openButton.addEventListener('click', showModal);
		closeButton.addEventListener('click', closeModal);
		
		function showModal(){
			var modal = document.getElementById(modalId);
			modal.classList.remove("hide");
			modal.classList.remove("fade");
			console.log("click");
		}

		function closeModal(){
			var modal = document.getElementById(modalId);
			modal.classList.add("hide");
			modal.classList.add("fade");
		}
		
		
		//modal
		//#formModal
	}

	
	
})();