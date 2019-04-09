package com.tjaglcs.plugins;

import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.BooleanQueryFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchEngineUtil;
import com.liferay.portal.util.PortalUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

public class Publication {
	private String name;
	private Article[] articles;
	private List<Issue> issues;
	private List<Volume> volumes;
	
	
	
	
	public Publication(String name, RenderRequest request) throws Exception {
		this.name = name;
		setArticles(name, request);
		setIssues();
		setVolumes();
		//System.out.println("vols: " + this.volumes);
	}
	
	public String getName() {
		return name;
	}
	public List<Volume> getVolumes() {
		return volumes;
	}
	
	public String getJson() {
		
		//this seems like it might be frail. Figure out a better way, or at least test
		//-special characters in string
		//-escape double/single quotes
		
		String JSON = "{'publication':{'name':'Military Law Review','volumes':{";
		
		List<Volume> volumes = getVolumes();
		
		for(int v = 0; v<volumes.size(); v++) {
			int volNo = volumes.get(v).getNumber();
			
			JSON+="'volume" + volNo + "':{'volumeNumber':'" + volNo + "',";
			
			List<Issue> issues = volumes.get(v).getIssues();
			
			for(int i = 0; i<issues.size(); i++) {
				int issueNo = issues.get(i).getNumber();
				
				List<Article> articles = issues.get(i).getArticles();
				
				JSON+="'issues':{";
				
				JSON+="'issue" + issueNo + "':{";
				
				JSON+="'issueNumber':'" + issueNo + "',";
				
				JSON+="'articles':{";
				
				for(int a = 0; a<articles.size(); a++) {
					
					JSON+="'article" + a + "':{";
					
					JSON+="'articleNumber':'" + articles.get(a).getId() + "',";
					
					JSON+="'articleTitle':'" + articles.get(a).getTitle() + "'},";
					
				}
				
				JSON+="}}},";  
			}
		
			JSON+="},"; 
			
		}
		
		
		
		JSON +="}}}";
		
		System.out.println(JSON);
		
		return JSON;
	}
	
	
	public List<Volume> getVolume(){
		return this.volumes;
	}
	
	public void setVolumes() throws Exception {
		//public ArrayList<Volume> fetchVolumes(RenderRequest request) throws Exception {
		//public void fetchVolumes(RenderRequest request) throws Exception {
			
			List<Issue> issueArray = this.issues;
			
			HashMap<String, List<Issue>> volumeMap = new HashMap<>();
			
			for(int i = 0; i<issueArray.size(); i++) {
				
				String currentVol = Integer.toString(issueArray.get(i).getVolume());
				
				System.out.println(issueArray.get(i));
				
				//int currentVol = issueArray.get(i).getVolume();
				Issue currentIssue = issueArray.get(i);
				//Article currentArticle = articlesArray[i];
			
				if (!volumeMap.containsKey(currentVol)) {
				    List<Issue> list = new ArrayList<Issue>();
				    list.add(currentIssue);

				    volumeMap.put(currentVol, list);
				    //System.out.println("IF: adding " + currentIssue.getVolume() + " to " + currentVol);
				} else {
					volumeMap.get(currentVol).add(currentIssue);
					//System.out.println("ELSE: adding " + currentIssue.getVolume() + " to " + currentVol);
				}
			}
			 
			//System.out.println("volumeMap");
			//System.out.println(volumeMap);
			//System.out.println(volumeMap.get(999).get(0).getArticles().get(0).getTitle());
			//System.out.println(volumeMap.get(999).get(index));
			//return null;
			
			ArrayList<Volume> volumeArray = new ArrayList<>();
			
			volumeMap.forEach((k,v) -> volumeArray.add(new Volume(this.name, Integer.parseInt(k),v)));
			//volumeMap.forEach((k,v) -> System.out.println(k));
			
			//return volumeMap;
			this.volumes = volumeArray;
		}
	
	public List<Issue> getIssues() {
		return this.issues;
	}
	
	public void setIssues() throws Exception {
		
		//Article[] articlesArray = fetchArticlesArray(request);
		//System.out.println("total articles: " + articlesArray.length);
		Article[] articlesArray = this.articles;
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
		
		issuesMap.forEach((k,v) -> issueArray.add(new Issue(this.name,k,v)));
		
		//System.out.println("issue year: " + issueArray.get(1).getYear());
		
		System.out.println("issuesMap");
		System.out.println(issuesMap);
		 
		this.issues = issueArray;
	 
	}
	
	public Article[] getArticles() {
		return this.articles;
	}
	
	public void setArticles(String pubName, RenderRequest request) throws Exception {

			
			HttpServletRequest httpRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
			SearchContext searchContext = SearchContextFactory.getInstance(httpRequest);
		
			BooleanQuery searchQuery = BooleanQueryFactoryUtil.create(searchContext);
			searchQuery.addRequiredTerm(CustomField.PUBLICATION_NAME, pubName);

			//searchQuery.addRequiredTerm(CustomField.PUBLICATION_VOLUME, 999);
			//NOTE: This is finding all versions. Restrict to the latest.
			//Tried to do this with portal.properties hook but still getting all versions
			
			Hits hits = SearchEngineUtil.search(searchContext,searchQuery);
			
			List<Document> hitsDocs = hits.toList();
			
			Article[] articles = new Article[hitsDocs.size()];
			
			System.out.println("Total hits: " + hits.getLength());
			
			for(int i = 0; i<hitsDocs.size(); i++) {
				//Think about error checking here. What happens if there's an error getting data? What happens after catch?
				
				Document currentDoc = hitsDocs.get(i);
				
				//System.out.println(currentDoc.getField(Field.ENTRY_CLASS_NAME).getValue());
				
				String title = "Title not found";
				long articleId = -1;
				double version = -1;
				int volume = -1;
				int issue = -1;
				String type = "Type not found";
				LocalDate articleDate = null;
				
				
				//Do I like how this is set up with IFs? Should I just split each into its own try/catch? Or is there a better way to loop these?
				
			
				try {
					if(currentDoc.getField(Field.TITLE) != null) {
						//System.out.println("string: " + currentDoc.getField(Field.TITLE).getValue());
						title = currentDoc.getField(Field.TITLE).getValue();
						
					} 
					
					if(currentDoc.getField(CustomField.VERSION) != null) {
						//System.out.println("double: " + currentDoc.getField(CustomField.VERSION).getValue());
						version = Double.parseDouble(currentDoc.getField(CustomField.VERSION).getValue());
					} 
					
					if(currentDoc.getField(CustomField.PUBLICATION_VOLUME) != null) {
						//System.out.println("int: " + currentDoc.getField(CustomField.PUBLICATION_VOLUME).getValue());
						volume = Integer.parseInt(currentDoc.getField(CustomField.PUBLICATION_VOLUME).getValue());
					} 
					
					if(currentDoc.getField(CustomField.PUBLICATION_ISSUE) != null) {
						//System.out.println("int: " + currentDoc.getField(CustomField.PUBLICATION_ISSUE).getValue());
						issue = Integer.parseInt(currentDoc.getField(CustomField.PUBLICATION_ISSUE).getValue());
					} 
					
					if(currentDoc.getField(CustomField.ENTRY_CLASS_NAME) != null) {
						//System.out.println("string: " + currentDoc.getField(Field.ENTRY_CLASS_NAME).getValue());
						type = currentDoc.getField(CustomField.ENTRY_CLASS_NAME).getValue();
					} 
					
					if(currentDoc.getField(CustomField.PUBLICATION_DATE) != null) {
						long fieldValue = Long.parseLong(currentDoc.getField(CustomField.PUBLICATION_DATE).getValue());
						articleDate = Instant.ofEpochMilli(fieldValue).atZone(ZoneId.systemDefault()).toLocalDate();
					} 
					
					//if(currentDoc.getField(CustomField.PUBLICATION_AUTHORS) != null) {
					//	System.out.println("pub authors: " + currentDoc.getField(Field.ENTRY_CLASS_NAME).getValue());
					//	type = currentDoc.getField(CustomField.PUBLICATION_AUTHORS).getValue();
					//} 
				
					if(type.contains("JournalArticle")) {
						if(currentDoc.get("articleId") != null) {
							//System.out.println("long: " + Long.parseLong(currentDoc.get("articleId")));
							articleId = Long.parseLong(currentDoc.get("articleId"));
						}
					} else if(type.contains("DLFileEntry")) {
						//Need to do something different here for PDF docs... don't know what yet
						articleId = -999;
					} 
					
					
					
				} catch(Exception e) {
					System.out.println(e);
				}
				
				
				try {
					//testing for now - figure out something better of date is null
					if(articleDate == null) {
						articleDate = Instant.ofEpochMilli(1234567890).atZone(ZoneId.systemDefault()).toLocalDate();
					}
					//System.out.println("articleDate from 226: " + articleDate);
					Article article = new Article(title, pubName, articleId, version, volume, issue, type, articleDate);
					articles[i] = article;
					
				} catch(Exception e) {
					System.out.println(e);
				}
			}
			
			this.articles = articles;
		}
}
