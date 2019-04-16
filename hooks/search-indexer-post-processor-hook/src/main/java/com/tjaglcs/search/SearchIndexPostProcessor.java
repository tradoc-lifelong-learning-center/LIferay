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
			
			/*if(className=="JournalArticleImpl") {
				fieldVal = getJournalArticleMeta(object, fieldsToIndex[i].getFieldValue());
			} else if(className=="DLFileEntryImpl") {
				fieldVal = getDlFileMeta(object, fieldsToIndex[i].getFieldValue());
			} else {
				continue;
			}*/

			//TODO
			if(className == "DLFileEntryImpl") {
				fieldVal = getDLFileMeta(object, fieldsToIndex[i].getFieldValue());
				
				//System.out.println("indexing dlfile");
				//long classPK = GetterUtil.getLong((String) workflowContext.get(WorkflowConstants.CONTEXT_ENTRY_CLASS_PK)); 
				//long fileEntryTypeId = DLFileVersionLocalServiceUtil.getFileVersion(classPK).getFileEntry().getFileEntryTypeId();                                                              
				//DLFileEntryType dlFileEntryType = DLFileEntryTypeLocalServiceUtil.getFileEntryType(fileEntryTypeId);
				//List<DDMStructure> ddmStructures = dlFileEntryType.getDDMStructures();
				//DDMStructure ddmStructure = ddmStructures.get(0);    
				
				//DLFileEntryMetadata dlFileEntryMetadata = DLFileEntryMetadataLocalServiceUtil.getdl
				

				
				
				
				//System.out.println("version: " + article.getFileVersion().getFileVersionId());
				
				/*
				if(document.getFields().get("title")!=null) {
					System.out.println("doc title: " + document.getFields().get("title").getValue());
				}
				
				if(document.getFields().get("publicationVolume")!=null) {
					System.out.println("volume: " + document.getFields().get("publicationVolume").getValue());
				}
				
				if(document.getFields().get("publicationIssue")!=null) {
					System.out.println("publicationIssue: " + document.getFields().get("publicationIssue").getValue());
				}
				
				if(document.getFields().get("publicationAuthors")!=null) {
					System.out.println("publicationAuthors: " + document.getFields().get("publicationAuthors").getValue());
				}
				
				if(document.getFields().get("publishDate")!=null) {
					System.out.println("doc publish date: " + document.getFields().get("publishDate").getValue());
				}*/

			} else {
				//System.out.println("indexing journal");
				fieldVal = getJournalArticleMeta(object, fieldsToIndex[i].getFieldValue());
			}
			
			String fieldName = fieldsToIndex[i].getFieldName();
			
			System.out.println("indexing " + fieldName + " with the value " + fieldVal);
			 
			if(fieldVal.length() > 0 && fieldName.length()>0) {
	        	document.addText(fieldName, fieldVal);
	        }
			
			
		}
	}
	
	
	/*private String getDlFileMeta(Object object, String fieldName) {
		DLFileEntry article = (DLFileEntry) object;
		
		try {
			String xmlContent = article.get;
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
	}*/
	
	private String getDLFileMeta(Object object, String fieldName) {
		DLFileEntry article = (DLFileEntry) object;
		String fieldVal = "";
		

		try {
			Map<String,Fields> fieldMap = article.getFieldsMap(article.getFileVersion().getFileVersionId());
			
			System.out.println("title: " + article.getTitle());
			
			for (Map.Entry<String,Fields> entry : fieldMap.entrySet()) {  
	            //System.out.println("Key: " + entry.getKey() + ", Value = " + entry.getValue()); 
	            //System.out.println("looking for meta " + entry.getValue().getNames());
	            
	            
	            
	            if(entry.getValue().get(fieldName).getValue()!=null) {
	            	//System.out.println("current fieldname: " + entry.getValue().get(fieldName).getName());
	            	//System.out.println("current fieldval: " + entry.getValue().get(fieldName).getValue());
	            	fieldVal = (String) entry.getValue().get(fieldName).getValue().toString();
	            	//entry.getValue();
	            	
	            	
	            	//System.out.print("fields: " + entry.getValue().getNames());
	            }
	            //System.out.println("k: " + k + ", v: " + v.get("publicationAuthors").getValue()));
	            
	            
	    	} 
			
			//System.out.println("doc meta: " + fieldVal);
			return fieldVal;	
			
		} catch(Exception e) {
			System.out.println("dlfileentry index error");
			System.out.println(e);
			return "";
		}
	}
	
	private String getJournalArticleMeta(Object object, String fieldName) {
		JournalArticle article = (JournalArticle) object;
		
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
	
	
	
	
	/*private void indexDocument(Document document, Object object) throws PortalException, SystemException {
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
        		  
        		  try {
					System.out.println("date: " + fields.get("publicationPublishDate").getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		  
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
	}*/

	


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
