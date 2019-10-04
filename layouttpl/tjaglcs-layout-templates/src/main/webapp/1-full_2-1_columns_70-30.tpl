<!-- note: styleshould be in theme -->

<script>

//window.onload = setScrollbarWidth;
setScrollbarWidth()
function setScrollbarWidth(){
document.documentElement.style.setProperty('--scrollbar-width', (window.innerWidth - document.documentElement.clientWidth) + "px");
}

</script>

<style> 


@media (min-width: 1080px) {

	/*.columns-1__full-2-1 .full-width{
		margin-left: calc(-100vw / 2 + 1080px / 2);
	    margin-right: calc(-100vw / 2 + 1080px / 2);
	    width: 100vw !important;
	}*/
	
	
	.columns-1__full-2-1 .full-width {
	width: 100vw !important;
	  min-height: 100px;
	  box-sizing: border-box;
	  margin: 0 auto;
	  width: calc(100vw - var(--scrollbar-width));
	  /*margin-left: calc(-100vw / 2 + 500px / 2);
	  margin-right: calc(-100vw / 2 + 500px / 2);*/
	  
	  margin-left: calc(-100vw / 2 + 1080px / 2 + var(--scrollbar-width)/2);
	  margin-right: calc(-100vw / 2 + 1080px / 2 + var(--scrollbar-width)/2);

	}
	
}

/*
@media (max-width: 1080px) {

	
	.columns-1__full-2-1 .full-width {
	  min-height: 100px;
	  box-sizing: border-box;
	  margin: 0 auto;
	  width: calc(100vw - var(--scrollbar-width));
	  
	  margin-left: calc(-100vw / 2 + 100vw / 2 + var(--scrollbar-width)/2);
	  margin-right: calc(-100vw / 2 + 100vw / 2 + var(--scrollbar-width)/2);

	}
	
}*/


</style>

<div class="columns-1__full-2-1" id="main-content" role="main">
	
	

	<div class="portlet-layout row-fluid full-width">
		<div class="portlet-column portlet-column-only span12" id="column-1">
			$processor.processColumn("column-1", "portlet-column-content portlet-column-content-only")
		</div>
	</div>

	<div class="portlet-layout row-fluid">
		<div class="portlet-column portlet-column-first span8" id="column-2">
			$processor.processColumn("column-2", "portlet-column-content portlet-column-content-first")
		</div>

		<div class="portlet-column portlet-column-last span4" id="column-3">
			$processor.processColumn("column-3", "portlet-column-content portlet-column-content-last")
		</div>
	</div>

	<div class="portlet-layout row-fluid">
		<div class="portlet-column portlet-column-only span12" id="column-4">
			$processor.processColumn("column-4", "portlet-column-content portlet-column-content-only")
		</div>
	</div>
</div>

