
(function(){
	
	window.onload = bindClickHandlers;

	function bindClickHandlers(){
		var openButton = document.getElementById('modalLauncher');
		var agreeButton = document.getElementById('formSubmit');
		var closeButton = document.getElementById('modalCloser');
		var closeButtonX = document.getElementById('modalCloserX');
		
		var modalId = openButton.dataset.target;
		var modalIdBackground = "formModalBackground";
		
		openButton.addEventListener('click', showModal);
		agreeButton.addEventListener('click', closeModal);
		closeButton.addEventListener('click', closeModal);
		closeButtonX.addEventListener('click', closeModal);
		
		function showModal(){
			var modal = document.getElementById(modalId);
			var modalBackground = document.getElementById(modalIdBackground);
			modal.classList.remove("hide");
			modal.classList.remove("fade");
			modalBackground.classList.add("modal-background--open");
		}

		function closeModal(){
			var modal = document.getElementById(modalId);
			var modalBackground = document.getElementById(modalIdBackground);
			modal.classList.add("hide");
			modal.classList.add("fade");
			modalBackground.classList.remove("modal-background--open");
		}

	}

})();