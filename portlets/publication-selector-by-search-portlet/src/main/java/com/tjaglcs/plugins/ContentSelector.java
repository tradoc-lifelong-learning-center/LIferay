package com.tjaglcs.plugins;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.BooleanQueryFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchEngineUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journal.service.JournalContentSearchLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.tjaglcs.plugins.CustomField;
import com.tjaglcs.plugins.Publication;

public class ContentSelector extends MVCPortlet { 
	
	public Publication fetchPublication(RenderRequest request) throws Exception {
		
		//ArrayList<Volume> volumeArray = fetchVolumes(request);
		
		//System.out.println("fetch vol arraylist size: " + volumeArray.size());
		
		Publication pub = new Publication("Military Law Review", request);
		
		//TO DO:
		//get most recent vol/issue and display if no query string
		//if query string, get that vol/issue and display
		//How do I display ACTUAL article title (from content) versions shortened title field?
		//How to I link to custom page URLs? Can I get a display page set up?
		System.out.println("volume 1: " + pub.getVolume(1).getIssue(1));
		System.out.println("latest volume: " + pub.getMostRecentVolume());
		
		
		
		//trying to find parent page URL. from https://stackoverflow.com/questions/8397679/get-portlet-page-containing-web-content-in-liferay
		long groupId = getGroupId(request);
		ThemeDisplay themeDisplay = getThemeDisplay(request);
		List<Article> articles = pub.getVolume(1).getIssue(1).getArticles();
		long articleId = articles.get(0).getId();
		
		//JournalArticle art = JournalArticleLocalServiceUtil.getArticle(articleId);
		
		//System.out.println("art: " + art);
		
		List<Long> layoutIds = JournalContentSearchLocalServiceUtil.getLayoutIds(groupId, false, Long.toString(articleId));
		
		System.out.println("layoutIds: " + layoutIds);
		

		
		if (!layoutIds.isEmpty()) {
			  long layoutId = layoutIds.get(0).longValue();
			  Layout layout = LayoutLocalServiceUtil.getLayout(groupId, false, layoutId);
			  String url = PortalUtil.getLayoutURL(layout, themeDisplay);
			  //String url = PortalUtil.getLayoutFriendlyURL(layout, themeDisplay);
			  System.out.println("url: " + url);
			}
		
		return pub;
	}
	
	public long getGroupId(RenderRequest req) {
		ThemeDisplay themeDisplay = getThemeDisplay(req);
		long portletGroupId = themeDisplay.getScopeGroupId();
		
		return portletGroupId;
	}
	
	private ThemeDisplay getThemeDisplay(RenderRequest req) {
		return (ThemeDisplay) req.getAttribute(WebKeys.THEME_DISPLAY);
	}
	
	/*public ArrayList<Volume> fetchVolumes(RenderRequest request) throws Exception {
	//public ArrayList<Volume> fetchVolumes(RenderRequest request) throws Exception {
	//public void fetchVolumes(RenderRequest request) throws Exception {
		
		ArrayList<Issue> issueArray = fetchIssues(request);
		
		HashMap<String, List<Issue>> volumeMap = new HashMap<>();
		
		for(int i = 0; i<issueArray.size(); i++) {
			
			String currentVol = Integer.toString(issueArray.get(i).getVolume());
			//int currentVol = issueArray.get(i).getVolume();
			Issue currentIssue = issueArray.get(i);
			//Article currentArticle = articlesArray[i];
		
			if (!volumeMap.containsKey(currentVol)) {
			    List<Issue> list = new ArrayList<Issue>();
			    list.add(currentIssue);

			    volumeMap.put(currentVol, list);
			} else {
				volumeMap.get(currentVol).add(currentIssue);
			}
		}
		 
		System.out.println("volumeMap");
		System.out.println(volumeMap);
		//System.out.println(volumeMap.get(999).get(0).getArticles().get(0).getTitle());
		//System.out.println(volumeMap.get(999).get(index));
		//return null;
		
		ArrayList<Volume> volumeArray = new ArrayList<>();
		
		volumeMap.forEach((k,v) -> volumeArray.add(new Volume(Integer.parseInt(k),v)));
		//volumeMap.forEach((k,v) -> System.out.println(k));
		
		//return volumeMap;
		return volumeArray;
	}*/
	
	/*public ArrayList<Issue> fetchIssues(RenderRequest request) throws Exception {
		
		Article[] articlesArray = fetchArticlesArray(request);
		//System.out.println("total articles: " + articlesArray.length);
		
		HashMap<Integer, List<Article>> issuesMap = new HashMap<>();
		
		

		for(int i = 0; i<articlesArray.length; i++) {
			//System.out.println("title: " + articlesArray[i].getTitle());
			//System.out.println("vol: " + articlesArray[i].getVolume());
			
			//int currentVol = articlesArray[i].getVolume();
			int currentIssue = articlesArray[i].getIssue();
			Article currentArticle = articlesArray[i];
		
			if (!issuesMap.containsKey(currentIssue)) {
			    List<Article> list = new ArrayList<Article>();
			    list.add(currentArticle);

			    issuesMap.put(currentIssue, list);
			} else {
				issuesMap.get(currentIssue).add(currentArticle);
			}
		}
		
		
		
		//System.out.println(issuesMap);
		//System.out.println(issuesMap.size());
		
		ArrayList<Issue> issueArray = new ArrayList<>();
		//Issue[] issueArray = new Issue[issuesMap.size()];
		
		issuesMap.forEach((k,v) -> issueArray.add(new Issue(k,v)));
		
		//System.out.println("issue year: " + issueArray.get(1).getYear());
		
		System.out.println("issuesMap");
		System.out.println(issuesMap);
		 
		return issueArray;
	 
	}*/

	
		//Remember, need to do two things here:
		//First, fetch a list of ALL articles for a certain pub and get list of vol/issue
		//Then, need to generate TOC for all articles in selected issue

	
	
//	public Article[] fetchArticlesArray(RenderRequest request) throws Exception {
//			
//		//TO DO: this should filter down from the Publication class at some point
//			String pubName = "Military Law Review";
//			
//			HttpServletRequest httpRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
//			SearchContext searchContext = SearchContextFactory.getInstance(httpRequest);
//		
//			BooleanQuery searchQuery = BooleanQueryFactoryUtil.create(searchContext);
//			searchQuery.addRequiredTerm(CustomField.PUBLICATION_NAME, pubName);
//
//			//searchQuery.addRequiredTerm(CustomField.PUBLICATION_VOLUME, 999);
//			//NOTE: This is finding all versions. Restrict to the latest.
//			//Tried to do this with portal.properties hook but still getting all versions
//			
//			Hits hits = SearchEngineUtil.search(searchContext,searchQuery);
//			
//			List<Document> hitsDocs = hits.toList();
//			
//			Article[] articles = new Article[hitsDocs.size()];
//			
//			System.out.println("Total hits: " + hits.getLength());
//			
//			for(int i = 0; i<hitsDocs.size(); i++) {
//				//Think about error checking here. What happens if there's an error getting data? What happens after catch?
//				
//				Document currentDoc = hitsDocs.get(i);
//				
//				//System.out.println(currentDoc.getField(Field.ENTRY_CLASS_NAME).getValue());
//				
//				String title = "Title not found";
//				long articleId = -1;
//				double version = -1;
//				int volume = -1;
//				int issue = -1;
//				String type = "Type not found";
//				LocalDate articleDate = null;
//				
//				
//				//Do I like how this is set up with IFs? Should I just split each into its own try/catch? Or is there a better way to loop these?
//				
//			
//				try {
//					if(currentDoc.getField(Field.TITLE) != null) {
//						//System.out.println("string: " + currentDoc.getField(Field.TITLE).getValue());
//						title = currentDoc.getField(Field.TITLE).getValue();
//						
//					} 
//					
//					if(currentDoc.getField(CustomField.VERSION) != null) {
//						//System.out.println("double: " + currentDoc.getField(CustomField.VERSION).getValue());
//						version = Double.parseDouble(currentDoc.getField(CustomField.VERSION).getValue());
//					} 
//					
//					if(currentDoc.getField(CustomField.PUBLICATION_VOLUME) != null) {
//						//System.out.println("int: " + currentDoc.getField(CustomField.PUBLICATION_VOLUME).getValue());
//						volume = Integer.parseInt(currentDoc.getField(CustomField.PUBLICATION_VOLUME).getValue());
//					} 
//					
//					if(currentDoc.getField(CustomField.PUBLICATION_ISSUE) != null) {
//						//System.out.println("int: " + currentDoc.getField(CustomField.PUBLICATION_ISSUE).getValue());
//						issue = Integer.parseInt(currentDoc.getField(CustomField.PUBLICATION_ISSUE).getValue());
//					} 
//					
//					if(currentDoc.getField(CustomField.ENTRY_CLASS_NAME) != null) {
//						//System.out.println("string: " + currentDoc.getField(Field.ENTRY_CLASS_NAME).getValue());
//						type = currentDoc.getField(CustomField.ENTRY_CLASS_NAME).getValue();
//					} 
//					
//					if(currentDoc.getField(CustomField.PUBLICATION_DATE) != null) {
//						long fieldValue = Long.parseLong(currentDoc.getField(CustomField.PUBLICATION_DATE).getValue());
//						articleDate = Instant.ofEpochMilli(fieldValue).atZone(ZoneId.systemDefault()).toLocalDate();
//					} 
//					
//					//if(currentDoc.getField(CustomField.PUBLICATION_AUTHORS) != null) {
//					//	System.out.println("pub authors: " + currentDoc.getField(Field.ENTRY_CLASS_NAME).getValue());
//					//	type = currentDoc.getField(CustomField.PUBLICATION_AUTHORS).getValue();
//					//} 
//				
//					if(type.contains("JournalArticle")) {
//						if(currentDoc.get("articleId") != null) {
//							//System.out.println("long: " + Long.parseLong(currentDoc.get("articleId")));
//							articleId = Long.parseLong(currentDoc.get("articleId"));
//						}
//					} else if(type.contains("DLFileEntry")) {
//						//Need to do something different here for PDF docs... don't know what yet
//						articleId = -999;
//					} 
//					
//					
//					
//				} catch(Exception e) {
//					System.out.println(e);
//				}
//				
//				
//				try {
//					//testing for now - figure out something better of date is null
//					if(articleDate == null) {
//						articleDate = Instant.ofEpochMilli(1234567890).atZone(ZoneId.systemDefault()).toLocalDate();
//					}
//					System.out.println("articleDate from 226: " + articleDate);
//					Article article = new Article(title, pubName, articleId, version, volume, issue, type, articleDate);
//					articles[i] = article;
//					
//				} catch(Exception e) {
//					System.out.println(e);
//				}
//			}
//			
//			return articles;
//		}
		
}
