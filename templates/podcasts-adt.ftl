<#--
Application display templates can be used to modify the look of a
specific application.

Please use the left panel to quickly add commonly used variables.
Autocomplete is also available and can be invoked by typing "${".
-->


<style>

.podcasts{
width:100%;
}

.podcasts__title-description-container{
text-align:left;
}

.podcasts__stream-container,
.podcasts__download-container{
text-align:center;

}


</style>

<table class="podcasts">
  <thead>
    <tr>
      <th>Description</th>
      <th>Stream</th>
      <th>Download</th>
    </tr>
  </thead>
  <tbody>

<#if entries?has_content>
	<#list entries as curEntry>

	<#assign url>${curEntry.getAssetRenderer().getURLDownload(themeDisplay)}</#assign>

	<tr>
	    <td class="podcasts__title-description-container">
	        <p><b>${curEntry.getTitle(locale)}</b></p>
	        <p>${curEntry.getDescription()}</p>
	    </td>

	    <td class="podcasts__stream-container">
    	    <audio controls>
              <source src="${url}" type="audio/mp3">
            </audio>
	    </td>

	    <td class="podcasts__download-container">
	        <a href="${url}">Download</a>
	    </td>

	</tr>




	</#list>
</#if>

    </tbody>
</table>
