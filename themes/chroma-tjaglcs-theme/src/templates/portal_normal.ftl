<!DOCTYPE html>

<#include init />

<html class="${root_css_class}" dir="<@liferay.language key="lang.dir" />" lang="${w3c_language_id}">

<head>
	<title>${the_title} - ${company_name}</title>

	<meta content="initial-scale=1.0, width=device-width" name="viewport" />

	<@liferay_util["include"] page=top_head_include />
</head>

<body class="${css_class} color-scheme-default">

<#-- TODO: I'm assuming these are the same as vm declarations, but check -->

<@liferay_ui["quick-access"] contentId="#main-content" />

<@liferay_util["include"] page=body_top_include />

<@liferay.control_menu />

<#-- -->

<div id="wrapper">

		<header id="banner">
			<div class="navbar navbar-classic navbar-top py-3">
				<div class="container user-personal-bar">
					<div class="align-items-center autofit-row">
						<a class="${logo_css_class} align-items-center d-md-inline-flex d-sm-none d-none logo-md" href="${site_default_url}" title="<@liferay.language_format arguments="" key="go-to-x" />">
							<img alt="${logo_description}" class="mr-2" height="56" src="${site_logo}" />

							<#if show_site_name>
								<h1 class="font-weight-bold h2 mb-0 text-dark">${site_name}</h1>
							</#if>
						</a>

						<#assign preferences = freeMarkerPortletPreferences.getPreferences({"portletSetupPortletDecoratorId": "barebone", "destination": "/search"}) />

						<div class="autofit-col autofit-col-expand">
							<#if show_header_search>
								<div class="justify-content-md-end mr-4 navbar-form" role="search">
									<@liferay.search_bar default_preferences="${preferences}" />
								</div>
							</#if>
						</div>

						<div class="autofit-col">
							<@liferay.user_personal_bar />
						</div>
					</div>
				</div>
			</div>

			<div class="navbar navbar-classic navbar-expand-md navbar-light pb-3">
				<div class="container">
					<a class="${logo_css_class} align-items-center d-inline-flex d-md-none logo-xs" href="${site_default_url}" rel="nofollow">
						<img alt="${logo_description}" class="mr-2" height="56" src="${site_logo}" />

						<#if show_site_name>
							<h1 class="font-weight-bold h2 mb-0 text-dark">${site_name}</h1>
						</#if>
					</a>

					<#include "${full_templates_path}/navigation.ftl" />
				</div>
			</div>
		</header>



	<#-- TODO: previous template has a div wrap here, then subbanner. Do we need either? -->


	<section id="content">

		<#-- TODO: add breadcrumbs -->

		<h1 class="hide-accessible">${the_title}</h1>

		<#if selectable>
			<@liferay_util["include"] page=content_include />
		<#else>
			${portletDisplay.recycle()}

			${portletDisplay.setTitle(the_title)}

			<@liferay_theme["wrap-portlet"] page="portlet.ftl">
				<@liferay_util["include"] page=content_include />
			</@>
		</#if>
	</section>


	<footer id="footer" role="contentinfo" class="footer">

		<#-- TODO: Add footer
				 TODO: Move footer content into web article. Don't want to have to update theme for minor edits
	   -->

		 <#--Looks like footer will have address on left and contact on right
		 		 Probably a 70/30 split
				 Use flexbox to make responsive
	  -->

		 <div class="container">
		 		<div class="row">
				 <div class="col-sm">
					 <p>The Judge Advocate General's Legal Center & School</p>
					 <p>600 Massie Rd</p>
					 <p>Charlottesville, VA 22903</p>
					 <p>Main Reception Desk: <a href="tel:1-434-971-3300">(434) 971-3300</a></p>
					 <p><a href="/lodging">Lodging:</a> <a href="tel:1-434-972-6450">(434) 972-6450</a></p>
				 </div>
				 <div class="col-sm-auto">
					 <p><a href="/documents/27431/135130/Directory+2016-2017/24975c80-c9e5-4797-8659-bef0abd022dc">Directory</a></p>
					 <p><a href="/support">Contact Us</a></p>
				 </div>
			 </div>
		 </div>
	</footer>
</div>

<@liferay_util["include"] page=body_bottom_include />

<@liferay_util["include"] page=bottom_include />

</body>

</html>
