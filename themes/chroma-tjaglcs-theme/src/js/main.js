
AUI().ready(
	function(A) {


		//Show site search bar on click
		var siteSearch = A.one('#siteSearch');

		if (siteSearch) {
			var btnSearch = siteSearch.one('button');

			var searchForm = siteSearch.one('form');
			var searchField = searchForm.one('.search-bar-keywords-input');

			btnSearch.on(
				'click',
				function(event) {
					event.preventDefault();

					if (searchField.getStyle('width') == '0px') {
						searchField.setStyle('width', '160px');
						searchField.setStyle('padding-left', '.5rem');
						searchField.setStyle('padding-right', '.5rem');
						searchField.focus();

						return;
					}
					else {
						if (searchField.get('value') == '') {
							searchField.setStyle('width', '0px');
							searchField.setStyle('padding-left', '0');
							searchField.setStyle('padding-right', '0');


							return;
						}
					}

					searchField.set('value','');
					searchForm.submit();
				}
			);
		}

	}
);
