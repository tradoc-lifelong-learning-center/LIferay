package com.tjaglcs.plugins;

import com.tjaglcs.plugins.Article;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.liferay.util.portlet.PortletRequestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portlet.documentlibrary.NoSuchFileException;
import com.liferay.portlet.documentlibrary.NoSuchFolderException;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalFolder;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.repository.model.FileEntry;



/**
 * Portlet implementation class ContentSelector
 */
public class ContentSelector extends MVCPortlet {
	
	private RenderRequest globalReq;
	private Article[] articles;
	
	public Article[] getArticleObjs(RenderRequest req) throws Exception {
		//method to build an array of article objects based on the portlet config
		//objects will be used to populate dropdown, check against for query string, and select article
		
		String articleConfig = getArticleIdsConfig(req);
		String[] articleConfigStrings = articleConfig.split(";");
		
		Article[] articleConfigs = new Article[articleConfigStrings.length];
		
		for(int i = 0; i<articleConfigStrings.length; i++) {
			//using -1 to skip the last item, which is archive link
			String articleString = articleConfigStrings[i];
		
			long articleId = Long.parseLong(extractQueryStringVals(articleString,"articleId"));
			int volume = Integer.parseInt(extractQueryStringVals(articleString,"vol"));
			int issue = Integer.parseInt(extractQueryStringVals(articleString,"no"));
			
			Article article = new Article(articleId,volume,issue);
			articleConfigs[i] = article;
		} 
		
		//update class variables for later use
		this.articles = articleConfigs;
		this.globalReq = req;
		
		return articleConfigs;

	}
	
	public Long fetchCurrentArticleId(RenderRequest req) throws Exception{
		//method to determin which article will be displayed
		//based on query string and list of articles in config
		
		String articleIdFromString = getQueryStringValue("articleId");
		
		//does the article from the query string match the articles in the config?
		boolean isArticleListed = checkArticleList(articles, articleIdFromString);
		
		System.out.println("art length: " + articles[0].getQueryString());

		if(articles[0].getArticleId()==-1) {
			//if there are no articles in the config, return an error and prevent from crashing
			System.out.println("no article in config. Please add using the article ID, volume, and issue number (separated by semi-colons): articleId=25147&vol=225&no=4;articleId=25167&vol=225&no=3.");
			//TO DO: need to make this configurable
			return 0L;
		} else if(articleIdFromString==null) {
			//this is if there's no query string, so
			//return most recent
			System.out.println("No article in query string. Using most recent.");
			return articles[0].getArticleId();
		} else if(!isArticleListed) {
			//if the query string doesn't match what's in the config, show a not found
			//don't really intend for this to be able to view any article in the database
			long articleNotFound = getEmptyArticleIdConfig();
			System.out.println("Query string doesn't match. Article not found. Using: " + articleNotFound);
			return articleNotFound;
		} else if(articleIdFromString=="browseArchive" || articleIdFromString=="selectAnIssue") {
			return 0L;
		} else {
			System.out.println("Fetching article from query string: " + articleIdFromString);
			return Long.parseLong(articleIdFromString);
		}

	}
	
 
	public boolean isMostRecent() throws Exception {
		long articleId = fetchCurrentArticleId(globalReq);
		long mostRecentArticle = articles[0].getArticleId();
		
		if(mostRecentArticle==articleId) {
			return true;
		} else {
			return false;
		}
		
	}
	
	private String getQueryStringValue(String stringParam) {
		HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(globalReq));
		String queryString = httpReq.getParameter(stringParam);
		System.out.println("queryString: " + queryString);
		return queryString;
	}
	
	public long getGroupId(RenderRequest req) {
		ThemeDisplay themeDisplay = getThemeDisplay(req);
		long portletGroupId = themeDisplay.getScopeGroupId();
		
		return portletGroupId;
	}
	
	private ThemeDisplay getThemeDisplay(RenderRequest req) {
		return (ThemeDisplay) req.getAttribute(WebKeys.THEME_DISPLAY);
	}
	
	
	private String getArticleIdsConfig(RenderRequest req) throws Exception {
		PortletPreferences portletPreferences = req.getPreferences();
		String articleIDs = GetterUtil.getString(portletPreferences.getValue("contentSelectorIncludeArticles", "-1"));
		
		return articleIDs;
	}
	
	private long getEmptyArticleIdConfig() throws Exception {
		PortletPreferences portletPreferences = globalReq.getPreferences();
		String emptyArticleString = GetterUtil.getString(portletPreferences.getValue("contentSelectorArticleNotFound", "-1"));
		//System.out.println("emptyArticleString: " + emptyArticleString);
		
		long emptyArticle;
		
		try {
			emptyArticle = Long.parseLong(emptyArticleString);
		} catch (Exception e) {
			emptyArticle=-1;
			e.printStackTrace();
		}
		
		//System.out.println("emptyArticle: " + emptyArticle);
		return emptyArticle;
	}

	public String getArchiveUrl() {
		PortletPreferences portletPreferences = globalReq.getPreferences();
		String archiveUrlString = GetterUtil.getString(portletPreferences.getValue("contentSelectorArchiveUrl", "https://tjaglcspublic.army.mil/mlr-archives"));
		return archiveUrlString;
	}
	
	
	public String fetchArticleList() {
		String articleList = "";
		
		for(int i=0; i<articles.length; i++) {
			String currentArticle = articles[i].getArticleId() + ";"; 
			articleList += currentArticle;
		}
		
		return articleList;
	}
	
	private boolean checkArticleList(Article[] articles, String queryString) {
		//method to check if articleId from query string exists in list of articles in portlet
		
		boolean isArticleListed = false;
		
		if(queryString==null) {
			return false;
		}
		
		for(int i = 0; i<articles.length; i++) {
			
			try {
				Long.parseLong(queryString);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				System.out.print("Invalid query string");
				e.printStackTrace();
				continue;
			}
			
			if(articles[i].getArticleId()==Long.parseLong(queryString)) {
				isArticleListed=true;
				
				break;
			}

		} 
		
		return isArticleListed;
		
	}
	
	private String extractQueryStringVals(String textToSearch, String paramName) {
		//method to extract certain query string values
		
		String pattern = "(" + paramName + "=)(\\d+)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(textToSearch);
		
		if(m.find()) {
			return m.group(2);
		} else {
			return "-1";
		}
		
	}

	
}
