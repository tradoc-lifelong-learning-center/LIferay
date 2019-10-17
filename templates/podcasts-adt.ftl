<#--
Application display templates can be used to modify the look of a
specific application.

Please use the left panel to quickly add commonly used variables.
Autocomplete is also available and can be invoked by typing "${".
-->


<style>

table.podcasts {
  font-family: "Open Sans", sans-serif;
  line-height: 1.25;
}

table.podcasts {
  border: 1px solid #ccc;
  border-collapse: collapse;
  margin: 0;
  padding: 0;
  width: 100%;
  /*table-layout: fixed;*/
}

table.podcasts caption {
  font-size: 1.5em;
  margin: .5em 0 .75em;
}

table.podcasts tr {
  /*background-color: #f8f8f8;*/
  border: 1px solid #ddd;
  padding: .35em;
}

table.podcasts th,
table.podcasts td {
  padding: .625em;
  text-align: center;
}

table.podcasts th {
  font-size: .85em;
  letter-spacing: .1em;
  text-transform: uppercase;
}

.podcasts__stream-container{
  /*min-width:300px;*/
}

.podcasts__audio-container{
  display:none;
}

.podcast__desc-title{
  font-weight:bold;
}

@media screen and (max-width: 1100px) {
  table.podcasts {
    border: 0;
  }

  table.podcasts caption {
    font-size: 1.3em;
  }

  table.podcasts thead {
    border: none;
    clip: rect(0 0 0 0);
    height: 1px;
    margin: -1px;
    overflow: hidden;
    padding: 0;
    position: absolute;
    width: 1px;
  }

  table.podcasts tr {
    border-bottom: 3px solid #ddd;
    display: block;
    margin-bottom: .625em;
  }

  table.podcasts td {
    border-bottom: 1px solid #ddd;
    display: block;
    font-size: .8em;
    text-align:left;
  }

  table.podcasts td.podcasts__stream-container{
   text-align:center;
  }

  table.podcasts td.podcasts__download-container{
   text-align:right;
  }

  table.podcasts td:last-child {
    border-bottom: 0;
  }

  audio{
    /*transform: scale(.75);*/
    max-width:100%;
  }

}


</style>

<table class="podcasts">
  <thead>
    <tr>
      <th scope="col">Description</th>
      <th scope="col">Stream</th>
      <th scope="col">Download</th>
    </tr>
  </thead>
  <tbody>



<#if entries?has_content>
	<#list entries as curEntry>

<#attempt>
  <#assign url>${curEntry.getAssetRenderer().getURLDownload(themeDisplay)}</#assign>
<#recover>
  <#assign url>https://www.tjaglcspublic.army.mil</#assign>
</#attempt>

<#assign podcastId>podcast-${curEntry.getEntryId()}</#assign>



  <tr>
    <td scope="row" data-label="Description" class="podcasts__description-container">

      <p class="podcast__desc-title">${curEntry.getTitle(locale)}</p>
      <div id="${podcastId}-desc-container" data-podcastid="${podcastId}">
          <p id="${podcastId}-desc-para" class="podcast__desc-para" data-fulldescription="${curEntry.getDescription()}" data-togglestate="partial">${curEntry.getDescription()}</p>
          <button data-podcastid="${podcastId}" data-togglebutton="true">More</button>
      </div>
    </td>
    <td data-label="Stream" class="podcasts__stream-container">
      <a href="javascript:;" data-podcastLink="${podcastId}">Audio</a>
      <div class="podcasts__audio-container" data-podcastAudio="${podcastId}">
        <audio controls>
          <source src="${url}" type="audio/mp3">
          Sorry, your browser doesn't support the HTML5 audio element.
        </audio>
      </div>

      </td>
    <td data-label="Download" class="podcasts__download-container">
      <a href="${url}">Download</a>
    </td>
  </tr>


	</#list>
</#if>

    </tbody>
</table>

<script>
(function(){
  var audioLinks = document.querySelectorAll('[data-podcastlink]');

  for(var i = 0; i<audioLinks.length; i++){
    audioLinks[i].onclick = newTabFunction;
  }


  function newTabFunction(e) {
    var linkId = e.target.dataset.podcastlink;
    if(!linkId) return false;

    var html = document.querySelector("[data-podcastaudio=" + linkId + "]").outerHTML;

    if(!html) return false;

    var w = window.open();
    w.document.body.innerHTML = html;

  }


})();
</script>

<script>
    (function(){
  var toggleButtons = document.querySelectorAll("[data-togglebutton='true']");


  for(var i = 0; i<toggleButtons.length; i++){
    toggleButtons[i].onclick = toggleDescription;
  }

  function toggleDescription(e){
    var id = e.currentTarget.dataset.podcastid;
    console.log("podcastid: "  + id)
    var descParaId = id + "-desc-para";
    var descriptionElement = document.getElementById(descParaId);
    var currentState = descriptionElement.dataset.togglestate;
    var fullDescription = descriptionElement.dataset.fulldescription;
    var partialDescription = getPartialDescription(fullDescription);
    console.log("currentState: "  + currentState)

    if(currentState=="partial"){
      descriptionElement.innerHTML = fullDescription;
      descriptionElement.dataset.togglestate="full";
      e.currentTarget.innerHTML = "More";
    } else if(currentState=="full"){
      descriptionElement.innerHTML = partialDescription;
      descriptionElement.dataset.togglestate="partial";
      e.currentTarget.innerHTML = "Less";
    }
  }

  function getPartialDescription(fullDesc){
    var wordLimit = 30;

    var descriptionArray = fullDesc.split(' ');

    for(var i = descriptionArray.length - wordLimit; i>0; i--){
      descriptionArray.pop();
    }

    partialDescription = descriptionArray.join(' ') + '...';

    return partialDescription;
  }




})();


</script>
