/*
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
AUI().ready(
	function(A) {
		/*//instead of making button and wrapper, can I intercept existing button IF the search is not expanded?
	  var showSearchButton = A.one('#showSearchButton');
		//var searchBarContainer = A.one('#search-bar-container');
		//var showSearchButton = document.getElementById("showSearchButton");
	  showSearchButton._node.onclick = function(){
			console.log("click!");
		};

		console.log("showSearchButton: ")
		console.log(showSearchButton)
		console.log(showSearchButton._node)

		console.log("A: ")
		console.log(A)
*/

		var siteSearch = A.one('#siteSearch');
		console.log("siteSearch")
		console.log(siteSearch)

		if (siteSearch) {
			var btnSearch = siteSearch.one('button');

			var searchForm = siteSearch.one('form');
			//var searchField = searchForm.one('.site-search-field');
			//TODO: figure out a better way to get this. Instance id will change.
			var searchField = searchForm.one('.search-bar-keywords-input');



			btnSearch.on(
				'click',
				function(event) {
					event.preventDefault();

					//alert("click!")
					console.log("searchForm: ");
					console.log(searchForm);
					console.log("width: " + searchForm.getStyle('width'));
					console.log("searchField: ");
					console.log(searchField);
					console.log("width: " + searchField.getStyle('width'));

					if (searchField.getStyle('width') == '0px') {
						//siteSearch.removeClass('site-search-collapsed');
						searchField.setStyle('width', '160px');
						searchField.setStyle('padding-left', '.5rem');
						searchField.setStyle('padding-right', '.5rem');
						searchField.focus();
						//console.log("hey!")

						return;
					}
					else {
						if (searchField.get('value') == '') {
							siteSearch.addClass('site-search-collapsed');


							return;
						}
					}

					searchForm.submit();
				}
			);
		}

	}
);
