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
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.tjaglcs.search.CustomField;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
		
		//filter volumes by type: if there's both an article and PDF, only show article
		filterArticlePDFs();
		
		setVolumes();

		setMostRecentVolume();
		setMostRecentIssue(this.mostRecentVolume);
		
		//set selected volume and issue (if present)
		setIsSingleIssue(request);
		setSelectedContent(this.request);
		setJson();
		//System.out.println("JSON: " + json);
		
		//System.out.print("setIsSingleIssue: " + this.isSingleIssue);
		
		setStartYear();
		setEndYear();
		
	}
	
	private void filterArticlePDFs() {
		//loop all journal articles, then check all PDF articles and remove any with the same volume/issue
		//long startTime = Calendar.getInstance().getTimeInMillis();
		long articlesLength = this.articles.size();
		
		List<Article> dlFileArticles = new ArrayList<>();
		List<Article> journalArticles = new ArrayList<>();
		List<Long> articlesToFilter = new ArrayList<>();
		
		//split articles into categories
		for(int i = 0; i<articlesLength; i++) {
			
			Article currentArticle = this.articles.get(i);
			
			if(currentArticle.getType().contains("DLFileEntry")) {
				dlFileArticles.add(currentArticle);
			} else {
				journalArticles.add(currentArticle);
			}
		}
		
		//loop journal articles and look for matching PDFs
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
		
		//System.out.println("article filter list: " + articlesToFilter);
		
		//remove out duplicate IDs from articlesToFilter list
		List<Long> articlesToFilterConsolidated = articlesToFilter.stream().distinct().collect(Collectors.toList());
		
		//System.out.println("article filter list consolidated: " + articlesToFilterConsolidated);
		
		List<Article> articlesToRemove = new ArrayList<>();
		
		//build array of duplicate article objects
		for(int i = 0; i<this.articles.size(); i++) {
			Article article = this.articles.get(i);
			
			for(int z = 0; z<articlesToFilterConsolidated.size(); z++) {
				
				//System.out.println("checking article " + article.getId() + " against " + articlesToFilterConsolidated.get(z));
				
				if(article.getId()==articlesToFilterConsolidated.get(z)) {
					//System.out.println("filtering PDF " + this.articles.get(i).getId() + " with title " + this.articles.get(i).getTitle());
					//this.articles.remove(this.articles.get(i));
					articlesToRemove.add(this.articles.get(i));
				}
				
			}
			
			
		}
		
		//remove articles
		for(int i = 0; i<articlesToRemove.size(); i++) {
			this.articles.remove(articlesToRemove.get(i));
		}
		
		//long endTime = Calendar.getInstance().getTimeInMillis();
		
		//System.out.println("Start: " + startTime);
		//System.out.println("End: " + endTime);
        //System.out.println("For each loop: " + (endTime - startTime)); 
		//System.out.println("end: " + this.articles.size());
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
		
		//System.out.println("configValue: " + configValue);
		
		if(configValue.contains("multi")) {
			//System.out.println("multi issue!");
			this.isSingleIssue = false;
		} else {
			//System.out.println("single issue!");
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
		
		String JSON = "{\"publication\":{\"name\":\"" + this.name + "\",\"pubCode\":\"mlr\",\"volumes\":{";
		
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
			String issueName = escapeJSON(issues.get(i).getName());
			
			JSON+="\"issue" + issueNo + "\":{";
			
			JSON+="\"number\":\"" + issueNo + "\",";
			
			JSON+="\"name\":\"" + issueName + "\"";
			
			JSON+="}";
			
			if(i!=issues.size()-1) {
				JSON+=",";
			}
		}
		
		
		JSON+="}";

		
		
		
		return JSON;
		
	}
	
	private String escapeJSON(String str) {
		return str.replaceAll("(\"|\\\\)", "\\\\$1");
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
	
	//this should grab the selected content
	public boolean setSelectedContent(RenderRequest request) {

		//System.out.println("setting selected content!");
		//String pubCode = getQueryStringValue("pub");
		
		//System.out.println("pub code: " + pubCode);
		
		String volString = this.getQueryStringValue("vol");
		
		//System.out.println("volString: " + volString);
		
		String issueString = this.getQueryStringValue("no");
		
		//System.out.println("issueString: " + issueString);
		
		int volNumber=-1;
		int issueNum = -1;
		
		//System.out.println("getting volume!");
		if(volString==null) {
			//System.out.println("no volume selected by query string. Getting most recent");
			this.selectedVolume = this.mostRecentVolume;
		} else {
			
			try {
				
				volNumber = Integer.parseInt(volString);
				//System.out.println("trying to get volume " + volNumber);
				
				Volume selectedVolume = getVolume(volNumber);
				
				if(selectedVolume==null) {
					SessionErrors.add(request, "no-volume-found");
					this.selectedVolume = this.mostRecentVolume;
				} else {
					this.selectedVolume = selectedVolume;
				}
				
				
			} catch (NumberFormatException e) {
				
				e.printStackTrace();
				//System.out.println("couldn't get volume number from query string");
				this.selectedVolume = this.mostRecentVolume;
				return false;
			}
		}
		
		//System.out.println("getting issue!");
		//System.out.println("issue string: " + issueString);
		//System.out.println("is single issue?: " + this.isSingleIssue);
		//if(issueString==null && this.isSingleIssue==false) {
		if(issueString==null && !this.isSingleIssue) {
			//if issue string is null AND multi, get all issues
			this.selectedIssues = this.selectedVolume.getIssues();
		} else if(issueString==null && this.isSingleIssue) { 
			//if issue string is null AND a single issue, only get most recent single issue
			this.selectedIssues.add(this.getMostRecentIssue());
		} else if(issueString!=null){
			//if query string is not null, get that issue
			
			try {
				issueNum = Integer.parseInt(issueString);
				
				Issue selectedIssue = this.selectedVolume.getIssue(issueNum);
				
				//if selected issue doesn't exist, present error message and get all from selected volume
				if(selectedIssue==null) {
					SessionErrors.add(request, "no-issue-found");
					this.selectedIssues = this.selectedVolume.getIssues();
				} else {
					this.selectedIssues.add(selectedIssue);
				}
			} catch (NumberFormatException e) {
				//System.out.println("couldn't get issue number from query string");
				e.printStackTrace();
			}
		}
		
		return true;
	}
	

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
			
			HashMap<String, List<Article>> volumeMap = new HashMap<>();
			
			for(int i = 0; i<this.articles.size(); i++) {
				Article currentArticle = this.articles.get(i);
				
				String currentVol = Integer.toString(this.articles.get(i).getVolume());
			
				if (!volumeMap.containsKey(currentVol)) {
				    List<Article> list = new ArrayList<Article>();
				    list.add(currentArticle);

				    volumeMap.put(currentVol, list);
				} else {
					volumeMap.get(currentVol).add(currentArticle);
				}
			}

			
			ArrayList<Volume> volumeArray = new ArrayList<>();
			
			volumeMap.forEach((k,v) -> {
				try {
					volumeArray.add(new Volume(this.name, Integer.parseInt(k),v));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					System.out.println("NumberFormatException in volumeMap");
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Exception in volumeMap");
				}
			});

			this.volumes = volumeArray;
		}
	
	public List<Issue> getIssues() {
		return this.issues;
	}
	
	
	private String getQueryStringValue(String stringParam) {
		//System.out.print("checking " + stringParam);
		
		HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(this.request));
		String queryString = httpReq.getParameter(stringParam);
		//System.out.println(queryString);
		
		return queryString;
	}
	
		
	public List<Article> getArticles() {
		return this.articles;
	}
	
	public void setArticles(String pubName, RenderRequest request) throws Exception {

			HttpServletRequest httpRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
			SearchContext searchContext = SearchContextFactory.getInstance(httpRequest);

			BooleanQuery searchQuery = BooleanQueryFactoryUtil.create(searchContext);
			
			Query stringQuery = StringQueryFactoryUtil.create("(publicationName: " + pubName + ") AND (status:0) AND ((entryClassName:com.liferay.portlet.journal.model.JournalArticle AND head:true) OR entryClassName:com.liferay.portlet.documentlibrary.model.DLFileEntry)");
			
			searchQuery.add(stringQuery,BooleanClauseOccur.MUST);
			
			Hits hits = SearchEngineUtil.search(searchContext,searchQuery);
			
			List<Document> hitsDocs = hits.toList();
			
			List<Article> articles = new ArrayList<>();
			
			//System.out.println("Total hits: " + hits.getLength());
			
			for(int i = 0; i<hitsDocs.size(); i++) {
				//TODO Think about error checking here. What happens if there's an error getting data? What happens after catch?
				
				Document currentDoc = hitsDocs.get(i);
				
				
				String title = "Title not found";
				long articleId = -1;
				double version = -1;
				int volume = -1;
				int issue = -1;
				String issueName = "";
				String type = "Type not found";
				LocalDate articleDate = null;
				int status = -1;
				String authors = "";
				String pdfType = "";
				
				
				try {
					if(currentDoc.getField(Field.TITLE) != null) {
						//System.out.println("string: " + currentDoc.getField(Field.TITLE).getValue());
						title = currentDoc.getField(Field.TITLE).getValue();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					System.out.println("title error");
				} 
				
				try {
					if(currentDoc.getField(CustomField.PUBLICATION_SUBTITLE) != null) {
						//System.out.println("string: " + currentDoc.getField(Field.TITLE).getValue());
						title = title + ": " + currentDoc.getField(CustomField.PUBLICATION_SUBTITLE).getValue();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					System.out.println("subtitle error");
				} 
				
				try {
					if(currentDoc.getField(CustomField.VERSION) != null) {
						//System.out.println("double: " + currentDoc.getField(CustomField.VERSION).getValue());
						version = Double.parseDouble(currentDoc.getField(CustomField.VERSION).getValue());
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					System.out.println("version error");
				} 
				
				try {
					if(currentDoc.getField(CustomField.PUBLICATION_VOLUME) != null) {
						//System.out.println("int: " + currentDoc.getField(CustomField.PUBLICATION_VOLUME).getValue());
						volume = Integer.parseInt(currentDoc.getField(CustomField.PUBLICATION_VOLUME).getValue());
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					System.out.println("volume error");
				} 
				
				try {
					if(currentDoc.getField(CustomField.PUBLICATION_ISSUE) != null) {
						//System.out.println("int: " + currentDoc.getField(CustomField.PUBLICATION_ISSUE).getValue());
						issue = Integer.parseInt(currentDoc.getField(CustomField.PUBLICATION_ISSUE).getValue());
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					System.out.println("issue error");
				} 
				
				try {
					if(currentDoc.getField(CustomField.PUBLICATION_ISSUE_NAME) != null) {
						//System.out.println("int: " + currentDoc.getField(CustomField.PUBLICATION_ISSUE).getValue());
						issueName = currentDoc.getField(CustomField.PUBLICATION_ISSUE_NAME).getValue();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					System.out.println("issue name error");
				} 
				
				try { //does this really need to be custom field?
					if(currentDoc.getField(CustomField.ENTRY_CLASS_NAME) != null) {
						//System.out.println("string: " + currentDoc.getField(Field.ENTRY_CLASS_NAME).getValue());
						type = currentDoc.getField(CustomField.ENTRY_CLASS_NAME).getValue();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					System.out.println("class name error");
				} 
				
				try {
					if(currentDoc.getField(CustomField.PUBLICATION_DATE) != null) {
						//System.out.println("Pub date field: " + currentDoc.getField(CustomField.PUBLICATION_DATE).getValue());
						//System.out.println("is long? " + currentDoc.getField(CustomField.PUBLICATION_DATE).getValue() instanceof Long);
						String fieldValue = currentDoc.getField(CustomField.PUBLICATION_DATE).getValue();
						articleDate = parseDate(fieldValue);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					System.out.println("pub date error");
				} 
				
				try {
					if(currentDoc.getField(Field.STATUS) != null) {
						status = Integer.parseInt(currentDoc.getField(Field.STATUS).getValue());
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					System.out.println("status error");
				} 
				
				try {
					if(currentDoc.getField(CustomField.PUBLICATION_AUTHORS) != null) {
						authors = currentDoc.getField(CustomField.PUBLICATION_AUTHORS).getValue();
						boolean is = currentDoc.getField(CustomField.PUBLICATION_AUTHORS).getValues() instanceof String[];
						String [] aString = currentDoc.getField(CustomField.PUBLICATION_AUTHORS).getValues();

					} 
				} catch (Exception e1) {
					e1.printStackTrace();
					System.out.println("author error");
				} 

				try {
					//System.out.println("pdf type field: " + currentDoc.getField(CustomField.PUBLICATION_PDF_TYPE));
					if(currentDoc.getField(CustomField.PUBLICATION_PDF_TYPE) != null) {
						pdfType = currentDoc.getField(CustomField.PUBLICATION_PDF_TYPE).getValues()[0];
						
					}
				} catch (Exception ePDFType) {
					System.out.println("PDF type error");
					ePDFType.printStackTrace();
					
				} 

				
				try {
					
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
					e.printStackTrace();
				}
				
				try {
					LocalDate now = LocalDate.now();
					
					//skip articles with invalid meta, or with publish dates that are sometime in the future
					if(volume==-1 || issue==-1 || articleDate == null) {
						//System.out.println("skipping " + articleId + " due to invalid vol/issue/year");
						continue;
					} else if(pdfType.contains("Article")){
						//System.out.println("Skipping article PDF");
						continue;
					} else if(articleDate.isAfter(now)) {
						//System.out.println("article " + title + " will not be published yet");
						continue;
					}
					Article article = new Article(title, pubName, articleId, version, volume, issue, issueName, type, status, articleDate, request, authors);
					articles.add(article);
					
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			this.articles = articles;
		}
	
	private LocalDate parseDate(String dateString) {
		//set a default date which will show for null dates
		LocalDate date = null;
		
		//if this will parse to long, it's epoch
		try {
			long fieldValue = Long.parseLong(dateString);
			date = Instant.ofEpochMilli(fieldValue).atZone(ZoneId.systemDefault()).toLocalDate();
		} catch (NumberFormatException e) {
			//e.printStackTrace();
		}
		
		//otherwise, try to parse string
        try {
        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
			date = LocalDate.parse(dateString, formatter);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		return date;
	}
}
