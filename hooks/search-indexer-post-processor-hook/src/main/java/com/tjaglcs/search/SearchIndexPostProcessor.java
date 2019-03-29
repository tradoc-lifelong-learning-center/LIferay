package com.tjaglcs.search;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletURL;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexerPostProcessor;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFileVersion;
import com.liferay.portlet.dynamicdatamapping.storage.Fields;
import com.tjaglcs.search.CustomField;
import com.tjaglcs.search.FieldToIndex;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchIndexPostProcessor extends BaseIndexerPostProcessor {
	public void postProcessContextQuery(BooleanQuery booleanQuery, SearchContext searchcontext)
            throws Exception {
        if(_log.isDebugEnabled())
            _log.debug(" postProcessContextQuery()");
    }
	
	public void postProcessDocument(Document document, Object object)
            throws Exception {
		
		String objectClass = object.getClass().toString();
		
		String documentClassName = "DLFileEntryImpl";
		String journalClassName = "JournalArticleImpl";
		
		if(objectClass.contains(journalClassName)) {
			//System.out.println("Journal!");
			indexJournal(document, object);
			
		} else if(objectClass.contains(documentClassName)) {
			//System.out.println("Document!");
			indexDocument(document, object);
		}
		
		//trying to figure out how to tell when I have a PDF and when I have an article
    	//System.out.println(System.getProperty("line.separator"));
    	//System.out.print("Class: " + objectClass);
    	//System.out.println(System.getProperty("line.separator"));
    	//System.out.print(objectClass.contains("JournalArticleImpl"));
    	//System.out.println(System.getProperty("line.separator"));
    	
    	//JournalArticleImpl
    	//DLFileEntryImpl
    	
            
    }
	
	
	//these are pretty much exactly the same but I'm not sure how to combine because of static types. Fix later?
	private void indexJournal(Document document, Object object) {
		JournalArticle article = (JournalArticle) object;
		
		FieldToIndex[] fieldsToIndex = new FieldToIndex[] {
			new FieldToIndex("publicationName",CustomField.PUBLICATION_NAME),
			new FieldToIndex("publicationVolume",CustomField.PUBLICATION_VOLUME),
			new FieldToIndex("publicationIssue",CustomField.PUBLICATION_ISSUE),
			new FieldToIndex("publicationAuthors",CustomField.PUBLICATION_AUTHORS),
			new FieldToIndex("publicationPublishDate",CustomField.PUBLICATION_DATE)
		};
		
		for(int i = 0; i<fieldsToIndex.length; i++) {
			String fieldVal = getJournalMeta(article, fieldsToIndex[i].getFieldValue());
			String fieldName = fieldsToIndex[i].getFieldName();
			 
			if(fieldVal.length() > 0 && fieldName.length()>0) {
	        	document.addText(fieldName, fieldVal);
	        }
		}
	}
	
	
	
	private String getJournalMeta(JournalArticle article, String fieldName) {
		
		//working on trying to get custom meta values from structure
		//http://liferaytutorial.blogspot.com/2013/08/parsing-in-liferayusing-sax-parser.html
		
		try {
			String xmlContent = article.getContent();
			com.liferay.portal.kernel.xml.Document document = SAXReaderUtil.read(xmlContent);
			
			
			
			Node node = document.selectSingleNode("/root/dynamic-element[@name='"  + fieldName + "']/dynamic-content");
			
			if(node != null) {
				System.out.println(node.getText());
				return node.getText();
			} else {
				return "";
			}
			
		} catch(Exception e) {
			System.out.println("journal pub index error");
			System.out.println(e);
			return "";
		}
		
		
		
	}
	
	private void indexDocument(Document document, Object object) throws PortalException, SystemException {
		DLFileEntry articleEntity = (DLFileEntry) object;
		long fileVersion = articleEntity.getLatestFileVersion(true).getFileVersionId();
		
		String publicationName = "";
        try {
        	Map<String,Fields> fieldsMap = articleEntity.getFieldsMap(fileVersion);
        		//not sure what happened here. May have messed something up when updating field through UI
        	

        		Set<String> keys = fieldsMap.keySet();
        		
        		Fields fields;
        		Field field;
        		for (String key : keys) {
        		  fields = fieldsMap.get(key);
        		  //////****this needs to be cleaned up and do some error checking
        		  publicationName = (String) fields.get("publicationName").getValue();
        		  
        		  System.out.println("doc pub: " + fields.get("publicationName").getValue());
        		  System.out.println("doc pub: " + fields.get("publicationAuthors").getValue());
        		  //field = fields.get("publication");
        		  //if (field != null) {
        			//  indexerUserTitle = (String) fields.get("publication").getValue();
        		  //}
        		}

        	

        } catch (Exception e) {
        	
        	System.out.print("Caught doc error");
        	System.out.println(e);
        }
        if(publicationName.length() > 0) {
        	System.out.println("indexed doc name: " + publicationName);
        	document.addText(CustomField.PUBLICATION_NAME, publicationName);
        } else {
        	System.out.println("didn't index doc");
        }
	}

	


    public void postProcessFullQuery(BooleanQuery fullQuery, SearchContext searchcontext)
            throws Exception {
        if(_log.isDebugEnabled())
            _log.debug(" postProcessFullQuery()");
    }

    public void postProcessSearchQuery(BooleanQuery searchquery, SearchContext searchcontext)
            throws Exception {
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
