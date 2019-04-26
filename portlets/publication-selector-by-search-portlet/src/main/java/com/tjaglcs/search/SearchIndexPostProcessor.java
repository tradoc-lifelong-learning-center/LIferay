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
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFileEntryMetadata;
import com.liferay.portlet.documentlibrary.model.DLFileEntryType;
import com.liferay.portlet.documentlibrary.model.DLFileVersion;
import com.liferay.portlet.documentlibrary.service.DLFileEntryMetadataLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFileVersionLocalServiceUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.storage.Fields;
import com.tjaglcs.search.CustomField;
import com.tjaglcs.search.FieldToIndex;

import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
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
			//System.out.println("class name: " + objectClass);
			//System.out.println("Journal!");
			objectClass = journalClassName;

			
		} else if(objectClass.contains(documentClassName)) {
			//System.out.println("class name: " + objectClass);
			//System.out.println("Document!");
			objectClass = documentClassName;
		}
		
		indexDocument(document, object, objectClass);
            
    }
	
	
	//these are pretty much exactly the same but I'm not sure how to combine because of static types. Fix later?
	private void indexDocument(Document document, Object object, String className) throws NumberFormatException, PortalException, SystemException {
		//JournalArticle article = (JournalArticle) object;
		
		FieldToIndex[] fieldsToIndex = new FieldToIndex[] {
			new FieldToIndex("publicationName",CustomField.PUBLICATION_NAME),
			new FieldToIndex("publicationVolume",CustomField.PUBLICATION_VOLUME),
			new FieldToIndex("publicationIssue",CustomField.PUBLICATION_ISSUE),
			new FieldToIndex("publicationAuthors",CustomField.PUBLICATION_AUTHORS),
			new FieldToIndex("publicationPublishDate",CustomField.PUBLICATION_DATE)
		};
		
		for(int i = 0; i<fieldsToIndex.length; i++) {
			
			String fieldVal;

			if(className == "DLFileEntryImpl") {
				fieldVal = getDLFileMeta(object, fieldsToIndex[i].getFieldValue());

			} else {
				//System.out.println("indexing journal");
				fieldVal = getJournalArticleMeta(object, fieldsToIndex[i].getFieldValue());
			}
			
			String fieldName = fieldsToIndex[i].getFieldName();
			 
			if(fieldVal.length() > 0 && fieldName.length()>0) {
	        	document.addText(fieldName, fieldVal);
	        }
			
			
		}
	}
	
	
	private String getDLFileMeta(Object object, String fieldName) throws PortalException, SystemException {
		DLFileEntry article = (DLFileEntry) object;
		String fieldVal = "";

		try {
			Map<String,Fields> fieldMap = article.getFieldsMap(article.getFileVersion().getFileVersionId());
			
			for (Map.Entry<String,Fields> entry : fieldMap.entrySet()) {  
	            //if(entry.getValue().get(fieldName).getValue()!=null) {
				if(entry.getValue().get(fieldName)!=null) {

	            	if(fieldName=="publicationAuthors") {
	            		//author names need to come back as an array in order to handle multiple names
	            		String[] authorsArray = (String[]) entry.getValue().get(fieldName).getValue();
	            		String authorsString = "";

	            		for(int i=0; i<authorsArray.length; i++) {
	            			System.out.println(i + ": " + authorsArray[i]);
	            			if(i>0) {
	            				authorsString+="|";
	            			}
	            			authorsString+=authorsArray[i];
	            			
	            		}
	            		
	            		fieldVal = authorsString;
	            	} else {
		            	fieldVal = (String) entry.getValue().get(fieldName).getValue().toString();
	            	}
	            	
	            	
	            	
	            }
	            
	            
	            
	    	} 
			
			return fieldVal;	
			
		} catch(Exception e) {
			System.out.println("dlfileentry index error");
			System.out.println(e);
			//e.printStackTrace();
			return "";
		}
	}
	
	private String getJournalArticleMeta(Object object, String fieldName) {
		JournalArticle article = (JournalArticle) object;
		
		try {
			String xmlContent = article.getContent();
			com.liferay.portal.kernel.xml.Document document = SAXReaderUtil.read(xmlContent);

			
			
			//get all nodes with field name (all in case it's a repeatable field like author
			List<Node> nodes = document.selectNodes("/root/dynamic-element[@name='"  + fieldName + "']/dynamic-content");
			
			
			if(nodes.size()>0) {
				String str = "";

				for(int i = 0; i<nodes.size(); i++) {
					System.out.println(nodes.get(i).getText());
					if(i>0) {
						str += "|";
					}
					
					str += nodes.get(i).getText();
					
				}
				
				return str;
			} else {
				return "";
			}

			
		} catch(Exception e) {
			System.out.println("journal pub index error");
			System.out.println(e);
			return "";
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
