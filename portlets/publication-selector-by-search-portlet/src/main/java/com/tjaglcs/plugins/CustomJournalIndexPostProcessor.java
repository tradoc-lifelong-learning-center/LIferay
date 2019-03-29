package com.tjaglcs.plugins;

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
import com.tjaglcs.plugins.CustomField;
import com.tjaglcs.plugins.FieldToIndex;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomJournalIndexPostProcessor extends BaseIndexerPostProcessor {
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
			new FieldToIndex("publication-name",CustomField.PUBLICATION_NAME),
			new FieldToIndex("volume",CustomField.PUBLICATION_VOLUME),
			new FieldToIndex("issue-number",CustomField.PUBLICATION_ISSUE)
		};
		
		for(int i = 0; i<fieldsToIndex.length; i++) {
			String fieldVal = getExpandoVal(article, fieldsToIndex[i].getFieldValue());
			String fieldName = fieldsToIndex[i].getFieldName();
			 
			if(fieldVal.length() > 0 && fieldName.length()>0) {
	        	document.addText(fieldName, fieldVal);
	        }
		}
	}
	
	
	
	private String getExpandoVal(JournalArticle article, String fieldName) {
		
		//working on trying to get custom meta values from structure
		//http://liferaytutorial.blogspot.com/2013/08/parsing-in-liferayusing-sax-parser.html
		
		try {
			
			String title = "publication-name";
			String xmlContent = article.getContent();
			com.liferay.portal.kernel.xml.Document document = SAXReaderUtil.read(xmlContent);
			
			
			
			Node node = document .selectSingleNode("/root/dynamic-element[@name='"  + title + "']/dynamic-content");
			
			if(node != null) {
				System.out.println(node.getText());
			}
			
			
			//String textToSearch = article.toXmlString();
			
			//String pattern = "<root.+?</root>";
			//Pattern r = Pattern.compile(pattern);
			
			//Matcher m = r.matcher(textToSearch);
		    //  if (m.find( )) {
		    //     System.out.println("Found value 0: " + m.group(0) );
		         //System.out.println("Found value 1: " + m.group(1) );
		         //System.out.println("Found value: " + m.group(2) );
		    //  }else {
		    //     System.out.println("NO MATCH");
		    //  }
			
			//System.out.println(textToSearch);
			//String xmlContent = article.toXmlString();
			//Document document = (Document) SAXReaderUtil.read(xmlContent);
			//System.out.println(SAXReaderUtil.read(xmlContent));
			
			
			if(article.getExpandoBridge().getAttribute(fieldName) instanceof Integer) {
				int fieldInt = (int) article.getExpandoBridge().getAttribute(fieldName);
				
				return Integer.toString(fieldInt);
			} else {
				String[] fieldArray = (String[]) article.getExpandoBridge().getAttribute(fieldName);
				
				if(fieldArray.length>0) {
					return fieldArray[0];
	            } else {
	            	return "";
	            }
			}
			
		} catch(Exception e) {
			System.out.println("journal pub index error");
			System.out.println(e);
			return "";
		}
		
		
		
	}

	
	private void indexDocument(Document document, Object object) throws PortalException, SystemException{
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
        		  publicationName = (String) fields.get("publication-name").getValue();
        		  
        		  System.out.println("doc pub: " + fields.get("publication-name").getValue());
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
        	document.addText(CustomField.PUBLICATION_NAME, publicationName);
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
	
	
	
	
	private static Log _log = LogFactoryUtil.getLog(CustomJournalIndexPostProcessor.class);
}
