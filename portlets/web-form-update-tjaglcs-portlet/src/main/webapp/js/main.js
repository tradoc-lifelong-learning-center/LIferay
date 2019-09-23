
(function(){
	
	window.onload = bindClickHandlers;

	function bindClickHandlers(){
		var openButton = document.getElementById('modalLauncher');
		var agreeButton = document.getElementById('formSubmit');
		var closeButton = document.getElementById('modalCloser');
		var closeButtonX = document.getElementById('modalCloserX');
		
		var modalId = openButton.dataset.target;
		
		openButton.addEventListener('click', showModal);
		agreeButton.addEventListener('click', closeModal);
		closeButton.addEventListener('click', closeModal);
		closeButtonX.addEventListener('click', closeModal);
		
		function showModal(){
			var modal = document.getElementById(modalId);
			modal.classList.remove("hide");
			modal.classList.remove("fade");
		}

		function closeModal(){
			var modal = document.getElementById(modalId);
			modal.classList.add("hide");
			modal.classList.add("fade");
		}

	}

})();