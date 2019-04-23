package com.tjaglcs.plugins;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.BooleanQueryFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchEngineUtil;
import com.liferay.portal.kernel.search.StringQueryFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFileEntryType;
import com.liferay.portlet.documentlibrary.model.DLFileVersion;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFileVersionServiceUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

public class Publication {
	private String name;
	private List<Article> articles;
	private List<Issue> issues;
	private List<Volume> volumes;
	private RenderRequest request;
	private Volume mostRecentVolume;
	private Issue mostRecentIssue;
	private Volume selectedVolume;
	private List<Issue> selectedIssues = new ArrayList<>();
	private String json;
	private boolean isSingleIssue;
	private int startYear;
	private int endYear;
	
	public Publication(String name, RenderRequest request) throws Exception {
		this.request = request;
		this.name = name;
		setArticles(name, request);
		
		//TODO filter volumes by type = if there's both an article and PDF, only show article
		filterArticlePDFs();
		
		setVolumes();
		
		setMostRecentVolume();
		setMostRecentIssue(this.mostRecentVolume);
		
		//set selected volume and issue (if present)
		setIsSingleIssue(request);
		setSelectedContent();
		setJson();
		System.out.println("JSON: " + json);
		
		System.out.print("setIsSingleIssue: " + this.isSingleIssue);
		
		setStartYear();
		setEndYear();
		
	}
	
	private void filterArticlePDFs() {
		//loop all journal articles, then check all PDF articles and remove any with the same volume/issue
		//hopefully this doesn't turn out to be slow. Is there a better way?
		//long startTime = Calendar.getInstance().getTimeInMillis();
		long articlesLength = this.articles.size();
		
		List<Article> dlFileArticles = new ArrayList<>();
		List<Article> journalArticles = new ArrayList<>();
		List<Long> articlesToFilter = new ArrayList<>();
		
		
		for(int i = 0; i<articlesLength; i++) {
			
			Article currentArticle = this.articles.get(i);
			
			if(currentArticle.getType().contains("DLFileEntry")) {
				dlFileArticles.add(currentArticle);
			} else {
				journalArticles.add(currentArticle);
			}
		}
		
		
		for(int i = 0; i<journalArticles.size(); i++) {
			Article currentJournalArticle = journalArticles.get(i);
			int vol = currentJournalArticle.getVolume();
			int issue = currentJournalArticle.getIssue();
			
			for(int z = 0; z<dlFileArticles.size(); z++) {
				Article currentDlFileArticle = dlFileArticles.get(z);
				
				if(currentDlFileArticle.getVolume()==vol && currentDlFileArticle.getIssue()==issue) {
					articlesToFilter.add(currentDlFileArticle.getId());
				}
			}
		}
		
		for(int i = 0; i<this.articles.size(); i++) {
			Article article = this.articles.get(i);
			
			for(int z = 0; z<articlesToFilter.size(); z++) {
				
				if(article.getId()==articlesToFilter.get(z)) {
					//System.out.println("removing " + articlesToFilter.get(z));
					this.articles.remove(this.articles.get(i));
				}
				
			}
			
			
		}
		
		//long endTime = Calendar.getInstance().getTimeInMillis();
		
		//System.out.println("Start: " + startTime);
		//System.out.println("End: " + endTime);
        //System.out.println("For each loop: " + (endTime - startTime)); 
	}
	
	public void setStartYear() {
		
		List<Integer> years = new ArrayList<>();
		int startYear = 9999;
		
		for(int i = 0; i<this.articles.size(); i++) {
			years.add(this.articles.get(i).getArticleDate().getYear());
		}
		
		for(int i = 0; i<years.size(); i++) {
			if(years.get(i)<startYear) {
				startYear = years.get(i);
			}
		}
		
		this.startYear = startYear;
	}
	
	public void setEndYear() {
		List<Integer> years = new ArrayList<>();
		int endYear = 0;
		
		for(int i = 0; i<this.articles.size(); i++) {
			years.add(this.articles.get(i).getArticleDate().getYear());
		}
		
		for(int i = 0; i<years.size(); i++) {
			if(years.get(i)>endYear) {
				endYear = years.get(i);
			}
		}
		
		this.endYear = endYear;
	}
	
	
	
	
	
	public int getStartYear() {
		return startYear;
	}


	public int getEndYear() {
		return endYear;
	}


	public void setIsSingleIssue(RenderRequest request) {
		PortletPreferences portletPreferences = request.getPreferences();
		String configValue = GetterUtil.getString(portletPreferences.getValue("numberOfIssues", ""));
		
		System.out.println("configValue: " + configValue);
		
		if(configValue.contains("multi")) {
			System.out.println("multi issue!");
			this.isSingleIssue = false;
		} else {
			System.out.println("single issue!");
			this.isSingleIssue = true;
		}
		
	}
	
	public boolean getIsSingleIssue() {
		return this.isSingleIssue;
	}
	
	public String getName() {
		return name;
	}
	public List<Volume> getVolumes() {
		return volumes;
	}
	
	
	
	public Issue getMostRecentIssue() {
		return mostRecentIssue;
	}

	public List<Issue> getSelectedIssue() {
		return selectedIssues;
	}

	public Volume getMostRecentVolume() {
		return mostRecentVolume;
	}

	public Volume getSelectedVolume() {
		return selectedVolume;
	}

	
	
	public String getJson() {
		return json;
	}

	public void setJson() {
		
		//this seems like it might be frail. Figure out a better way, or at least test
		//-special characters in string
		//-escape double/single quotes
		
		String JSON = "{\"publication\":{\"name\":\"Military Law Review\",\"pubCode\":\"mlr\",\"volumes\":{";
		
		List<Volume> volumes = getVolumes();
		
		for(int v = 0; v<volumes.size(); v++) {
			int volNo = volumes.get(v).getNumber();
			int year = volumes.get(v).getYear();
			
			JSON+="\"volume" + volNo + "\":{\"number\":\"" + volNo + "\",";
			JSON+="\"year\":\"" + year + "\"";
			
			JSON += buildIssueJson(volumes.get(v));
			
			//For now, I don't think I need articles in JSON, because JSON just populates list. view pulls TOC from bean
			
			//end of volume
			JSON+="}";

			if(v!=volumes.size()-1) {
				JSON+=",";
			}
			
		}
		
		
		
		JSON +="}}}";
		
		//System.out.println(JSON);
		
		this.json = JSON;
	}
	
	private String buildIssueJson(Volume volume) {
		String JSON = "";
		List<Issue> issues = volume.getIssues();
		
		if(issues.size()<1) {
			return "";
		}
		
		JSON+=",\"issues\":{";
		
		for(int i = 0; i<issues.size();  i++) {
			int issueNo = issues.get(i).getNumber();
			
			JSON+="\"issue" + issueNo + "\":{";
			
			JSON+="\"number\":\"" + issueNo + "\"";
			
			JSON+="}";
			
			if(i!=issues.size()-1) {
				JSON+=",";
			}
		}
		
		
		JSON+="}";

		
		
		
		return JSON;
		
	}
	
	public Volume getVolume(int volumeNumber){
		for(int i = 0; i<this.volumes.size(); i++) {
			if(this.volumes.get(i).getNumber()==volumeNumber) {
				return this.volumes.get(i);
			} 
		}
		
		System.out.println("No volume with the number " + volumeNumber);
		return null;
	}
	
	//TODO: continue working on getting either full volume or volume/issue based on query string (or if no query string, config?)
	//this should grab the selected content
	public boolean setSelectedContent() {
		System.out.println("setting selected content!");
		String pubCode = getQueryStringValue("pub");
		
		System.out.println("pub code: " + pubCode);
		
		String volString = this.getQueryStringValue("vol");
		
		System.out.println("volString: " + volString);
		
		String issueString = this.getQueryStringValue("no");
		
		System.out.println("issueString: " + issueString);
		
		int volNumber=-1;
		int issueNum = -1;
		
		System.out.println("getting volume!");
		if(volString==null) {
			System.out.println("no volume selected by query string. Getting most recent");
			this.selectedVolume = this.mostRecentVolume;
		} else {
			
			try {
				volNumber = Integer.parseInt(volString);
				this.selectedVolume = getVolume(volNumber);
			} catch (NumberFormatException e) {
				System.out.println("couldn't get volume number from query string");
				e.printStackTrace();
				return false;
			}
		}
		
		System.out.println("getting issue!");
		System.out.println("issue string: " + issueString);
		System.out.println("is single issue?: " + this.isSingleIssue);
		if(issueString==null && this.isSingleIssue==false) {
			//if issue string is null, then we want all issues, right?
			System.out.println("no issue selected by query string. Getting all issues.");
			//this.mostRecentIssue = getMostRecentIssue(this.mostRecentVolume);
			this.selectedIssues = this.selectedVolume.getIssues();
		} else if(issueString==null && this.isSingleIssue==true){
			//for single issue without query string, default to most recent
			this.selectedIssues.add(this.mostRecentIssue);
		} else if(issueString!=null){
			//if query string is not null, get that issue
			
			try {
				issueNum = Integer.parseInt(issueString);
				System.out.println("trying to add " + issueNum);
				this.selectedIssues.add(this.selectedVolume.getIssue(issueNum));
			} catch (NumberFormatException e) {
				System.out.println("couldn't get issue number from query string");
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	/*public Volume getSelectedVolume() {
		//If there's a query string, return that volume
		//else, return most recent
		
		//QueryString queryString = new QueryString();

		System.out.println("checking " + this.volumes.size() + " volumes for current volume");
		//System.out.println(PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request)));
		//System.out.println(getQueryStringValue("pub"));

		
		String pubCode = getQueryStringValue("pub");
		String volString = this.getQueryStringValue("vol");
		String issueString = this.getQueryStringValue("no");
		
		int volNumber=-1;
		int issueNum = -1;
		
		if(volString==null) {
			System.out.println("no volume selected by query string. Getting most recent");
			return getMostRecentVolume();
		} else {
			
			try {
				volNumber = Integer.parseInt(volString);
			} catch (NumberFormatException e) {
				System.out.println("couldn't get volume number from query string");
				e.printStackTrace();
			}
		}
		
		if(issueString!=null) {
			try {
				issueNum = Integer.parseInt(issueString);
			} catch (NumberFormatException e) {
				System.out.println("couldn't get issue number from query string");
				e.printStackTrace();
			}
		}

		//System.out.println("params - pub: " + pubCode + ", vol: " + volNumber + ", issue: " + issueNum);
		
		Volume selectedVolume = null;
		
		int selectedVolumeNumber = -1;
		
		for(int i = 0; i<this.volumes.size(); i++) {
			//System.out.println("checking for vol " + this.volumes.get(i).getNumber());
			if(this.volumes.get(i).getNumber()==volNumber) {
				selectedVolume = this.volumes.get(i);
				selectedVolumeNumber = selectedVolume.getNumber();
			} 
		}
		
		if(selectedVolumeNumber>-1) {
			return selectedVolume;
		} else {
			return getMostRecentVolume();
		}
		
		
	}*/
	

	public void setMostRecentVolume(){
		int latestVolumeNumber = 0;
		Volume latestVolume = null;
		
		for(int i = 0; i<this.volumes.size(); i++) {
			if(this.volumes.get(i).getNumber()>latestVolumeNumber) {
				latestVolume = this.volumes.get(i);
				latestVolumeNumber = latestVolume.getNumber();
			} 
		}
		
		//System.out.println("Latest volume: " + latestVolumeNumber);
		this.mostRecentVolume = latestVolume;
	}
	
	public void setMostRecentIssue(Volume volume) {
		int latestIssueNumber = 0;
		Issue latestIssue = null;
		
		List<Issue> currentIssues = volume.getIssues();
		
		for(int i = 0; i<currentIssues.size(); i++) {
			if(currentIssues.get(i).getNumber()>latestIssueNumber) {
				latestIssue = currentIssues.get(i);
				latestIssueNumber = latestIssue.getNumber();
			} 
		}
		
		//System.out.println("Latest volume: " + latestVolumeNumber);
		this.mostRecentIssue = latestIssue;
	}
	
	public void setVolumes() throws Exception {
		//public ArrayList<Volume> fetchVolumes(RenderRequest request) throws Exception {
		//public void fetchVolumes(RenderRequest request) throws Exception {
			
			//List<Issue> issueArray = this.issues;
			
			HashMap<String, List<Article>> volumeMap = new HashMap<>();
			
			for(int i = 0; i<this.articles.size(); i++) {
				Article currentArticle = this.articles.get(i);
				
				String currentVol = Integer.toString(this.articles.get(i).getVolume());
				
				//System.out.println("setVolumes currentVol: " + currentVol);
				
				//int currentVol = issueArray.get(i).getVolume();
				//Issue currentIssue = issueArray.get(i);
				//Article currentArticle = articlesArray[i];
			
				if (!volumeMap.containsKey(currentVol)) {
				    List<Article> list = new ArrayList<Article>();
				    list.add(currentArticle);

				    volumeMap.put(currentVol, list);
				    //System.out.println("IF: adding " + currentIssue.getVolume() + " to " + currentVol);
				} else {
					volumeMap.get(currentVol).add(currentArticle);
					//System.out.println("ELSE: adding " + currentIssue.getVolume() + " to " + currentVol);
				}
			}
			 
			//System.out.println("volumeMap");
			//System.out.println(volumeMap);
			//System.out.println(volumeMap.get(999).get(0).getArticles().get(0).getTitle());
			//System.out.println(volumeMap.get(999).get(index));
			//return null;
			
			ArrayList<Volume> volumeArray = new ArrayList<>();
			
			volumeMap.forEach((k,v) -> {
				try {
					//System.out.println("K: " + k);
					//System.out.println("v: " + v);
					//System.out.println("this.name: " + this.name);
					volumeArray.add(new Volume(this.name, Integer.parseInt(k),v));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("NumberFormatException in volumeMap");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Exception in volumeMap");
				}
			});
			//volumeMap.forEach((k,v) -> System.out.println(k));
			
			//return volumeMap;
			this.volumes = volumeArray;
		}
	
	public List<Issue> getIssues() {
		return this.issues;
	}
	
	
	private String getQueryStringValue(String stringParam) {
		System.out.print("checking " + stringParam);
		
		HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(this.request));
		String queryString = httpReq.getParameter(stringParam);
		System.out.println(queryString);
		
		return queryString;
	}
	
	/*public void setIssues() throws Exception {
		
		//Article[] articlesArray = fetchArticlesArray(request);
		//System.out.println("total articles: " + articlesArray.length);
		//List<Volume> volumeList = this.volumes;
		Article[] articlesArray = this.articles;
		HashMap<Integer, List<Article>> issuesMap = new HashMap<>();
		
		//for(int z = 0; z<volumeList.size(); z++) {
			//System.out.println("ARTICLES IN VOL: " + volumeList.get(z).get);
		//}

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
	 
	}*/
	
	public List<Article> getArticles() {
		return this.articles;
	}
	
	public void setArticles(String pubName, RenderRequest request) throws Exception {

			
			HttpServletRequest httpRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
			SearchContext searchContext = SearchContextFactory.getInstance(httpRequest);
		
			
			
			BooleanQuery searchQuery = BooleanQueryFactoryUtil.create(searchContext);
			
			//Query stringQuery = StringQueryFactoryUtil.create("(publicationName: + " + pubName + ") AND (field:0) AND ((entryClassName:com.liferay.portlet.journal.model.JournalArticle AND head:true) OR entryClassName:com.liferay.portlet.documentlibrary.model.DLFileEntry)");
			Query stringQuery = StringQueryFactoryUtil.create("(publicationName: " + pubName + ") AND (status:0) AND ((entryClassName:com.liferay.portlet.journal.model.JournalArticle AND head:true) OR entryClassName:com.liferay.portlet.documentlibrary.model.DLFileEntry)");
			//searchQuery.addRequiredTerm("publicationName", pubName);
			
			
			searchQuery.add(stringQuery,BooleanClauseOccur.MUST);
			
			
			
			//BooleanQuery searchQuery = BooleanQueryFactoryUtil.create(searchContext);
			
			//searchQuery.addRequiredTerm(CustomField.PUBLICATION_NAME, pubName);
			//show only "Active" status articles (Active=0)
			//searchQuery.addRequiredTerm(Field.STATUS, 0);

			//searchQuery.addRequiredTerm(CustomField.PUBLICATION_VOLUME, 999);
			//NOTE: This is finding all versions. Restrict to the latest.
			//Tried to do this with portal.properties hook but still getting all versions
			
			Hits hits = SearchEngineUtil.search(searchContext,searchQuery);
			
			List<Document> hitsDocs = hits.toList();
			
			List<Article> articles = new ArrayList<>();
			
			System.out.println("Total hits: " + hits.getLength());
			
			for(int i = 0; i<hitsDocs.size(); i++) {
				//TODO Think about error checking here. What happens if there's an error getting data? What happens after catch?
				
				Document currentDoc = hitsDocs.get(i);
				
				//System.out.println(currentDoc.getField(Field.ENTRY_CLASS_NAME).getValue());
				
				String title = "Title not found";
				long articleId = -1;
				double version = -1;
				int volume = -1;
				int issue = -1;
				String type = "Type not found";
				LocalDate articleDate = null;
				int status = -1;
				
				
				try {
					if(currentDoc.getField(Field.TITLE) != null) {
						//System.out.println("string: " + currentDoc.getField(Field.TITLE).getValue());
						title = currentDoc.getField(Field.TITLE).getValue();
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
					System.out.println("title error");
				} 
				
				try {
					if(currentDoc.getField(CustomField.VERSION) != null) {
						//System.out.println("double: " + currentDoc.getField(CustomField.VERSION).getValue());
						version = Double.parseDouble(currentDoc.getField(CustomField.VERSION).getValue());
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					System.out.println("version error");
				} 
				
				try {
					if(currentDoc.getField(CustomField.PUBLICATION_VOLUME) != null) {
						//System.out.println("int: " + currentDoc.getField(CustomField.PUBLICATION_VOLUME).getValue());
						volume = Integer.parseInt(currentDoc.getField(CustomField.PUBLICATION_VOLUME).getValue());
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					System.out.println("volume error");
				} 
				
				try {
					if(currentDoc.getField(CustomField.PUBLICATION_ISSUE) != null) {
						//System.out.println("int: " + currentDoc.getField(CustomField.PUBLICATION_ISSUE).getValue());
						issue = Integer.parseInt(currentDoc.getField(CustomField.PUBLICATION_ISSUE).getValue());
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					System.out.println("issue error");
				} 
				
				try { //does this really need to be custom field?
					if(currentDoc.getField(CustomField.ENTRY_CLASS_NAME) != null) {
						//System.out.println("string: " + currentDoc.getField(Field.ENTRY_CLASS_NAME).getValue());
						type = currentDoc.getField(CustomField.ENTRY_CLASS_NAME).getValue();
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					System.out.println("class name error");
				} 
				
				try {
					if(currentDoc.getField(CustomField.PUBLICATION_DATE) != null) {
						long fieldValue = Long.parseLong(currentDoc.getField(CustomField.PUBLICATION_DATE).getValue());
						articleDate = Instant.ofEpochMilli(fieldValue).atZone(ZoneId.systemDefault()).toLocalDate();
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					System.out.println("pub date error");
				} 
				
				try {
					if(currentDoc.getField(Field.STATUS) != null) {
						status = Integer.parseInt(currentDoc.getField(Field.STATUS).getValue());
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					System.out.println("status error");
				} 
				
				
				//TODO: this failing for dlfile for some reason. Obviously need this to work before I can get PDF URL
				//Do I like how this is set up with IFs? Should I just split each into its own try/catch? Or is there a better way to loop these?
				try {
					
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
						//getting fileEntryId (NOT PK)
						long groupId = Long.parseLong(currentDoc.getField("groupId").getValue());						
						long folderId = Long.parseLong(currentDoc.getField("folderId").getValue());
						String docTitle = currentDoc.getField("title").getValue();
						
						DLFileEntry entry = DLFileEntryLocalServiceUtil.fetchFileEntry(groupId, folderId, docTitle);
					
		                articleId = entry.getFileEntryId();
					} 
					
					
					
				} catch(Exception e) {
					System.out.println("article id error");
					System.out.println(e);
				}
				
				
				try {
					// TODO testing for now - figure out something better of date is null
					if(articleDate == null) {
						articleDate = Instant.ofEpochMilli(1234567890).atZone(ZoneId.systemDefault()).toLocalDate();
					}
					
					Article article = new Article(title, pubName, articleId, version, volume, issue, type, status, articleDate, request);
					System.out.println("title: " + article.getTitle());
					System.out.println("status: " + article.getStatus());
					System.out.println("ID: " + article.getId());
					articles.add(article);
					
				} catch(Exception e) {
					System.out.println(e);
				}
			}
			
			this.articles = articles;
		}
}
