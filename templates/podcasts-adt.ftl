<#--
Application display templates can be used to modify the look of a
specific application.

Please use the left panel to quickly add commonly used variables.
Autocomplete is also available and can be invoked by typing "${".
-->


<style>

body {
  font-family: "Open Sans", sans-serif;
  line-height: 1.25;
}

table {
  border: 1px solid #ccc;
  border-collapse: collapse;
  margin: 0;
  padding: 0;
  width: 100%;
  /*table-layout: fixed;*/
}

table caption {
  font-size: 1.5em;
  margin: .5em 0 .75em;
}

table tr {
  /*background-color: #f8f8f8;*/
  border: 1px solid #ddd;
  padding: .35em;
}

table th,
table td {
  padding: .625em;
  text-align: center;
}

table th {
  font-size: .85em;
  letter-spacing: .1em;
  text-transform: uppercase;
}

.podcasts__stream-container{
  min-width:300px;
}

.podcast__desc-title{
  font-weight:bold;
}

@media screen and (max-width: 1100px) {
  table {
    border: 0;
  }

  table caption {
    font-size: 1.3em;
  }

  table thead {
    border: none;
    clip: rect(0 0 0 0);
    height: 1px;
    margin: -1px;
    overflow: hidden;
    padding: 0;
    position: absolute;
    width: 1px;
  }

  table tr {
    border-bottom: 3px solid #ddd;
    display: block;
    margin-bottom: .625em;
  }

  table td {
    border-bottom: 1px solid #ddd;
    display: block;
    font-size: .8em;
    text-align: right;
  }

  table td .podcast__desc-para{
   text-align:left;
  }

  table td::before {
    /*
    * aria-label has no advantage, it won't be read inside a table
    content: attr(aria-label);
    */
    content: attr(data-label);
    float: left;
    font-weight: bold;
    text-transform: uppercase;
  }

  table td:last-child {
    border-bottom: 0;
  }
}


</style>

<table>
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

	<#assign url>${curEntry.getAssetRenderer().getURLDownload(themeDisplay)}</#assign>

  <tr>
    <td scope="row" data-label="Description" class="podcasts__description-container">
      <p class="podcast__desc-title">${curEntry.getTitle(locale)}</p>
      <p class="podcast__desc-para">${curEntry.getDescription()}</p>
    </td>
    <td data-label="Stream" class="podcasts__stream-container">
      <audio controls>
          <source src="${url}" type="audio/mp3">
          You browser doesn't support the HTML5 audio tag!
        </audio>
      </td>
    <td data-label="Download" class="podcasts__download-container">
      <a href="${url}">Download</a>
    </td>
  </tr>


	</#list>
</#if>

    </tbody>
</table>
