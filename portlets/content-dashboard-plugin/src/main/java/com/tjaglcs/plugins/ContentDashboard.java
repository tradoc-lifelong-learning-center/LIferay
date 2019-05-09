package com.tjaglcs.plugins;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
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
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.layoutsadmin.util.Sitemap;
import com.liferay.portlet.layoutsadmin.util.SitemapUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Portlet implementation class ContentDashboard
 */
public class ContentDashboard extends MVCPortlet {
	
	private List<Article> articles = new ArrayList<>();
	public static final String CSV_SEPARATOR = ",";
	
	public void exportCSV(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException {
	//http://www.liferaysavvy.com/2015/04/data-export-as-csv-format-in-liferay.html
		
		//String portletResource = ParamUtil.getString(request, "portletResource");
		
		//System.out.println("hey!");
		//System.out.println(resourceRequest);
		
		//StringBundler sb = new StringBundler();
		
		//sb.append(getCSVFormattedValue("hello world"));
		//sb.append(CharPool.NEW_LINE);
		//sb.append(getCSVFormattedValue("hello again, world!"));
		
		
		
		String fileName = "helloworld.csv";
		
		String content = articlesToCsvString(this.articles);
		
		byte[] bytes = content.getBytes();
		
		String contentType = ContentTypes.APPLICATION_TEXT;
		
		PortletResponseUtil.sendFile(resourceRequest, resourceResponse, fileName, bytes, contentType);
	}
	
	
	
	@Override
	public void serveResource(ResourceRequest resourceRequest,
	ResourceResponse resourceResponse) {

		
		/*
		try {
			//RenderRequest request = (RenderRequest)PortalUtil.getHttpServletRequest(resourceRequest);
			List<Article> arts = getContent(request);
			//System.out.println("articles: " + arts);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SearchException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		
		
		
	String cmd = ParamUtil.getString(resourceRequest, Constants.CMD);
	//System.out.println("cmd"+cmd);

	try {
		//System.out.println("articles from serveResource: " + this.articles.size());
		exportCSV(resourceRequest, resourceResponse);
		System.out.println("click!");
	} catch (Exception e) {
	
	}
	}
	
	public List<Layout> doStuff(RenderRequest request) throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
		long groupId = themeDisplay.getScopeGroupId();
		List<Layout> layouts=LayoutLocalServiceUtil.getLayouts(groupId, false);
        System.out.print("page 0:"+layouts.get(0).getFriendlyURL()+" \n");
        
        
        getContent(request);
        //System.out.println("atricles: " + this.articles.size());
        
        return layouts;
	}
	
	public String fetchSiteMap(RenderRequest request) throws PortalException, SystemException {
		
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
		long groupId = themeDisplay.getScopeGroupId();
		
		Sitemap mapObj = SitemapUtil.getSitemap(); 
		
		String sitemap = SitemapUtil.getSitemap(groupId, true, themeDisplay); 
		
		//System.out.println(sitemap);
		
		
		return sitemap;
		
	}
	
	public List<Article> getContent(RenderRequest request) throws ParseException, SearchException {
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
		
		//System.out.println("hits: " + hitsDocs.size());
		//System.out.println("articles: " + articles.size());
		
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
			long viewCount = 0;
			
			
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
				if(currentDoc.getField(Field.STATUS) != null) {
					status = Integer.parseInt(currentDoc.getField(Field.STATUS).getValue());
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				System.out.println("status error");
			} 
			
			try {
				if(currentDoc.getField(Field.VIEW_COUNT) != null) {
					viewCount = Long.parseLong(currentDoc.getField(Field.VIEW_COUNT).getValue());
					//System.out.println(Long.parseLong(currentDoc.getField(Field.VIEW_COUNT).getValue()));
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				System.out.println("view count error");
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
				Article article = new Article(title, pubName, articleId, version, volume, issue, issueName, type, status, articleDate, request, authors, viewCount);
				//System.out.println(article.getViewCount() + " views for " + article.getTitle());
				this.articles.add(article);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return this.articles;
	}
	
	private String articlesToCsvString(List<Article> articles) {
		System.out.println("trying to build stuff");
		
		StringBundler str = new StringBundler();
		
		//build headers
		str.append("Article Name");
		str.append(CSV_SEPARATOR);
		str.append("ID");
		str.append(CSV_SEPARATOR);
		str.append("View Count");
		str.append(CharPool.NEW_LINE);
		
		System.out.println("num of atricles: " + articles.size());
		
		for(int i = 0; i<articles.size(); i++) {
			Article article = articles.get(i);
			
			str.append(article.getTitle());
			str.append(CSV_SEPARATOR);
			str.append(article.getId());
			str.append(CSV_SEPARATOR);
			str.append(article.getViewCount());
			str.append(CharPool.NEW_LINE);
		}
		
		System.out.println(str.toString());
		return str.toString();
	}
	
	protected String getCSVFormattedValue(String value) {
		//http://www.liferaysavvy.com/2015/04/data-export-as-csv-format-in-liferay.html
		StringBundler sb = new StringBundler(3);
		sb.append(CharPool.QUOTE);
		sb.append(StringUtil.replace(value, CharPool.QUOTE,
		StringPool.DOUBLE_QUOTE));
		sb.append(CharPool.QUOTE);
		
		
		return sb.toString();
		}
}
