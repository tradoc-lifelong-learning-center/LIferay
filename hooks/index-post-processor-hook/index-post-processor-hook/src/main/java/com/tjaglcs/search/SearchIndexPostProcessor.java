package com.tjaglcs.search;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexerPostProcessor;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.StringQueryFactoryUtil;
import com.liferay.portal.kernel.search.Summary;

import java.util.Locale;

import javax.portlet.PortletURL;


public class SearchIndexPostProcessor extends BaseIndexerPostProcessor {
	public void postProcessContextQuery(BooleanQuery booleanQuery, SearchContext searchcontext)
            throws Exception {
        if(_log.isDebugEnabled())
            _log.debug(" postProcessContextQuery()");
    }
	
	public void postProcessDocument(Document document, Object object)
            throws Exception {

		if(_log.isDebugEnabled())
            _log.debug(" postProcessDocument()"); 
    }
	

    public void postProcessFullQuery(BooleanQuery fullQuery, SearchContext searchcontext)
            throws Exception {
    	
    	System.out.println("fullQuery: " + fullQuery);
    	
        if(_log.isDebugEnabled())
            _log.debug(" postProcessFullQuery()");
    }

    public void postProcessSearchQuery(BooleanQuery searchquery, SearchContext searchcontext)
            throws Exception {
    	
		//Query stringQuery = StringQueryFactoryUtil.create("title:secret");
    	Query stringQuery = StringQueryFactoryUtil.create("assetTagNames:hidden");

		searchquery.add(stringQuery, BooleanClauseOccur.MUST_NOT);
    	
    	//searchquery.addExactTerm("title", "hello world");
    	
    	System.out.println("searchquery: " + searchquery);
    	
        if(_log.isDebugEnabled())
            _log.debug(" postProcessSearchQuery()");
    }

    public void postProcessSummary(Summary summary, Document document, Locale locale,
            String snippet, PortletURL portletURL) {
        if(_log.isDebugEnabled())
            _log.debug("postProcessSummary()");
    }
	
	
	
	
	private static Log _log = LogFactoryUtil.getLog(SearchIndexPostProcessor.class);
}
