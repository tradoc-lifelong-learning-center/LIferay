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
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFileEntryType;
import com.liferay.portlet.documentlibrary.model.DLFileVersion;
import com.liferay.portlet.documentlibrary.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFileVersionServiceUtil;

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
	private List<Article> articles;
	private List<Issue> issues;
	private List<Volume> volumes;
	private RenderRequest request;
	
	
	
	public Publication(String name, RenderRequest request) throws Exception {
		this.name = name;
		setArticles(name, request);
		
		//BUG: this is grouping issues cross-volume, which is not what we want
		//group volume, then issue?
		
		//setIssues();
		setVolumes();
		this.request = request;
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
		
		String JSON = "{'publication':{'name':'Military Law Review','pubCode':'mlr','volumes':{";
		
		List<Volume> volumes = getVolumes();
		
		for(int v = 0; v<volumes.size(); v++) {
			int volNo = volumes.get(v).getNumber();
			int year = volumes.get(v).getYear();
			
			JSON+="'volume" + volNo + "':{'number':'" + volNo + "',";
			JSON+="'year':'" + year + "',";
			
			List<Issue> issues = volumes.get(v).getIssues();
			
			for(int i = 0; i<issues.size(); i++) {
				int issueNo = issues.get(i).getNumber();
				
				List<Article> articles = issues.get(i).getArticles();
				
				JSON+="'issues':{";
				
				JSON+="'issue" + issueNo + "':{";
				
				JSON+="'number':'" + issueNo + "',";
				
				JSON+="'articles':{";
				
				for(int a = 0; a<articles.size(); a++) {
					
					JSON+="'article" + a + "':{";
					
					JSON+="'number':'" + articles.get(a).getId() + "',";
					
					JSON+="'title':'" + articles.get(a).getTitle() + "'},";
					
				}
				
				JSON+="}}},";  
			}
		
			JSON+="},"; 
			
		}
		
		
		
		JSON +="}}}";
		
		//System.out.println(JSON);
		
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
	
	public Volume getSelectedVolume() {
		//If there's a query string, return that volume
		//else, return most recent
		
		//QueryString queryString = new QueryString();

		System.out.println("get selected volume: " + this.volumes.size());
		//System.out.println(PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request)));
		//System.out.println(getQueryStringValue("pub"));

		
		String pubCode = getQueryStringValue("pub");
		System.out.println("pubCode: " + pubCode);
		
		int volNumber = -1;
		try {
			volNumber = Integer.parseInt(this.getQueryStringValue("vol"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		int issueNumber = -1;
		try {
			issueNumber = Integer.parseInt(this.getQueryStringValue("no"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		Volume selectedVolume = null;
		int selectedVolumeNumber = -1;
		
		for(int i = 0; i<this.volumes.size(); i++) {
			System.out.println("checking for vol " + this.volumes.get(i).getNumber());
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
		
		
	}
	
	public Volume getMostRecentVolume(){
		int latestVolumeNumber = 0;
		Volume latestVolume = null;
		
		for(int i = 0; i<this.volumes.size(); i++) {
			if(this.volumes.get(i).getNumber()>latestVolumeNumber) {
				latestVolume = this.volumes.get(i);
				latestVolumeNumber = latestVolume.getNumber();
			} 
		}
		
		//System.out.println("Latest volume: " + latestVolumeNumber);
		return latestVolume;
	}
	
	public Issue getMostRecentIssue() {
		//TODO
		return null;
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
		HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(this.request));
		String queryString = httpReq.getParameter(stringParam);
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
						//Need to do something different here for PDF docs... don't know what yet
						//articleId = -999;
						//articleId = Long.parseLong(currentDoc.getUID());
						//System.out.println("fileEntryId: " + currentDoc.get("fileEntryId"));
						
						//DLFileEntry articleEntity = (DLFileEntry) currentDoc;
						//System.out.println("file entry id: " + articleEntity.getFileEntryId());
						
						
						//TODO: I think you want id, not class PK. fix?
						//System.out.println("fileEntryId: " + currentDoc.getFields());
						//System.out.println("fileEntryId: " + currentDoc.getField("fileEntryId").getValue());
						System.out.println("entryClassPK: " + currentDoc.getField("entryClassPK").getValue());
						//System.out.println("uid: " + currentDoc.getField("uid").getValue());
						//System.out.println("path: " + currentDoc.getField("path").getValue());
						//articleId = articleEntity.getFileEntryId();
						
						DLFileVersion fileVersion = DLFileVersionServiceUtil.getFileVersion(Long.parseLong(currentDoc.getField("entryClassPK").getValue()));
		                DLFileEntryType fileEntryType = DLFileEntryTypeLocalServiceUtil.getDLFileEntryType(fileVersion.getFileEntryTypeId());
		                DLFileEntry fileEntry = fileVersion.getFileEntry();
		                System.out.println("file ID really: " + fileEntry);
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
