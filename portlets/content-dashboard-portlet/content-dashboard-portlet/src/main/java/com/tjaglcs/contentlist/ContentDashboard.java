package com.tjaglcs.contentlist;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.BooleanQueryFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.SearchEngineUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.StringQueryFactoryUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.tjaglcs.contentlist.Article;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Portlet implementation class ContentList
 */
public class ContentDashboard extends MVCPortlet {
	
	private Articles articles;
	
	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse)
	    throws PortletException, IOException {
		
		this.articles = new Articles();
		
		try {
			getContent(renderRequest);
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		renderRequest.setAttribute("entries", this.articles);


	    super.render(renderRequest, renderResponse);
	}



	public void getContent(RenderRequest request) throws ParseException, SearchException {
		String pubName = "Military Law Review";
		
		//System.out.print("hello again, world!");
		
		HttpServletRequest httpRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
		SearchContext searchContext = SearchContextFactory.getInstance(httpRequest);

		BooleanQuery searchQuery = BooleanQueryFactoryUtil.create(searchContext);
		
		Query stringQuery = StringQueryFactoryUtil.create("(status:0) AND ((entryClassName:com.liferay.portlet.journal.model.JournalArticle AND head:true) OR entryClassName:com.liferay.portlet.documentlibrary.model.DLFileEntry)");
		
		searchQuery.add(stringQuery,BooleanClauseOccur.MUST);
		
		Hits hits = SearchEngineUtil.search(searchContext,searchQuery);
		
		List<Document> hitsDocs = hits.toList();
		
		List<Article> articles = new ArrayList<>();
		
		System.out.println("hits: " + hitsDocs.size());
		//System.out.println("articles: " + articles.size());
		
		for(int i = 0; i<hitsDocs.size(); i++) {
			//TODO Think about error checking here. What happens if there's an error getting data? What happens after catch?
			
			Document currentDoc = hitsDocs.get(i);
			
			
			String title = "";
			long articleId = 0;
			String createDate = "";
			String modifiedDate = "";
			String type = "";
			
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
				if(currentDoc.getField(Field.ENTRY_CLASS_NAME) != null) {
					String typeString = currentDoc.getField(Field.ENTRY_CLASS_NAME).getValue();
					//System.out.println("ENTRY_CLASS_NAME: " + currentDoc.getField(Field.ENTRY_CLASS_NAME).getValue());
					
					if(typeString.contains("JournalArticle")) {
						type = "Web Content Article";
					} else if(typeString.contains("DLFileEntry")){
						type = "Document";
					} else {
						type="undefined";
					}
					
					//type = currentDoc.getField(Field.ENTRY_CLASS_NAME).getValue();
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			try {
				if(currentDoc.getField(Field.CREATE_DATE) != null) {
					System.out.println("CREATE_DATE: " + currentDoc.getField(Field.CREATE_DATE).getValue());
					createDate = formatDate(currentDoc.getField(Field.CREATE_DATE).getValue());
				}
				
				if(currentDoc.getField(Field.MODIFIED_DATE) != null) {
					System.out.println("MODIFIED_DATE: " + currentDoc.getField(Field.MODIFIED_DATE).getValue());
					modifiedDate = formatDate(currentDoc.getField(Field.MODIFIED_DATE).getValue());
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			

			
			try {
				
				if(type.contains("Web Content Article")) {
					if(currentDoc.get("articleId") != null) {
						System.out.println("articleId, journal: " + Long.parseLong(currentDoc.get("articleId")));
						articleId = Long.parseLong(currentDoc.get("articleId"));
						//journalArticleCount++;
					}
				} else if(type.contains("Document")) {
					//getting fileEntryId (NOT PK)
					System.out.println("dlfile article id: " + currentDoc);
					long groupId = Long.parseLong(currentDoc.getField("groupId").getValue());						
					long folderId = Long.parseLong(currentDoc.getField("folderId").getValue());
					String docTitle = currentDoc.getField("title").getValue();
					
					DLFileEntry entry = DLFileEntryLocalServiceUtil.fetchFileEntry(groupId, folderId, docTitle);
				
	                articleId = entry.getFileEntryId();
	                
	                //System.out.println("articleId, DLFile: " + articleId);
				} 
			

			} catch(Exception e) {
				System.out.println("article id error");
				e.printStackTrace();
			}
			
			try {
				Article article = new Article(title, articleId, createDate, modifiedDate, type);
				//System.out.println(article.getViewCount() + " views for " + article.getTitle());
				this.articles.addArticle(article);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String formatDate(String inputDateString) {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.US);
		LocalDate date1 = LocalDate.parse(inputDateString, formatter);
		
		//System.out.println(date1.toString());
		
		//2019-08-27-1213-57
		return date1.toString();
	}
}
