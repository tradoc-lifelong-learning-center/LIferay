package com.tjaglcs.pubsearch;

import com.liferay.portal.kernel.dao.search.SearchContainer;
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
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalUtil;

import java.io.Serializable;
import java.util.List;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

public class PublicationSearch implements Serializable {

	//eventually make this configurable
	private RenderRequest request;
	private String pubName = "Military Law Review";
	private String keywords;
	private SearchContext searchContext;
	private BooleanQuery booleanQuery;
	private Hits hits;
	private List<Document> results;
	private PortletURL portletURL;
	private SearchContainer searchContainer;
	


	public void setRequest(RenderRequest request) {
		this.request = request;
	}
	
	public void setFields() throws ParseException, SearchException {
		setKeywords();
		setSearchContainer();
		setSearchContext();
		setBooleanQuery();
		setHits();
		setResults();
	}
	
	public void setSearchContainer() {
		this.searchContainer = new SearchContainer(this.request, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, this.portletURL, null, null);
		System.out.println("search container url: " + this.portletURL);
	}
	
	public void setPortletUrl(PortletURL url) {
		System.out.println("url: " + url);
		this.portletURL = url;
	}
	
	public PortletURL getPortletURL() {
		return this.portletURL;
	}
	
	public SearchContainer getSearchContainer() {
		return this.searchContainer;
	}

	public String hello() {
		return "Hello world!";
	}

	public String getKeywords() {
		return HtmlUtil.escape(keywords);
	}

	public void setKeywords() {
		this.keywords = ParamUtil.getString(this.request, "keywords");
	}
	
	public Hits getHits() {
		return this.hits;
	}
	
	public void setHits() throws SearchException {
		this.hits = SearchEngineUtil.search(this.searchContext,this.booleanQuery);
	}
	
	public void setResults() throws SearchException {
		
		List<Document> hitsDocs = this.hits.toList();
		
		this.results = hitsDocs;
		
	}
	
	

	public List<Document> getResults() {
		return results;
	}



	public BooleanQuery getBooleanQuery() {
		return booleanQuery;
	}

	public void setBooleanQuery() throws ParseException {
		BooleanQuery query = BooleanQueryFactoryUtil.create(this.searchContext);
		
		Query stringQuery = StringQueryFactoryUtil.create("(content:'"+ keywords + "' OR title:'" + keywords + "') AND ((entryClassName:com.liferay.portlet.journal.model.JournalArticle AND head:true) OR entryClassName:com.liferay.portlet.documentlibrary.model.DLFileEntry)");
		query.addRequiredTerm("publicationName", pubName);
		
		if(this.keywords==""){
			query.addTerm(Field.CONTENT, keywords);
		} else{
			query.add(stringQuery,BooleanClauseOccur.MUST);
		}
		
		this.booleanQuery = query;
	}



	public void setSearchContext() {
		HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
		SearchContext context = SearchContextFactory.getInstance(httpServletRequest);
		context.setKeywords(getKeywords());
		context.setAttribute("paginationType", "more");
		context.setStart(0);
		context.setEnd(100);
		
		this.searchContext = context;
	}

	public SearchContext getSearchContext() {
		return this.searchContext;
		
	}
}
