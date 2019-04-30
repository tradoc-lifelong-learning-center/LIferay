package com.tjaglcs.search;

import com.liferay.portal.kernel.search.Field;

import java.util.Locale;
import java.util.Map;

public class CustomField extends Field {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String PUBLICATION_NAME = "publicationName";
	
	public static final String PUBLICATION_VOLUME = "publicationVolume";
	
	public static final String PUBLICATION_ISSUE = "publicationIssue";
	
	public static final String PUBLICATION_ISSUE_NAME = "publicationIssueName";
	
	public static final String PUBLICATION_AUTHORS = "publicationAuthors";
	
	public static final String PUBLICATION_DATE = "publicationPublishDate";
	
	public static final String PUBLICATION_PDF_TYPE = "publicationPdfType";
		
	
	public CustomField(String name, Map<Locale, String> localizedValues) {
		super(name, localizedValues);
		// TODO Auto-generated constructor stub
	}

	public CustomField(String name, String value) {
		super(name, value);
		// TODO Auto-generated constructor stub
	}

	public CustomField(String name, String[] values) {
		super(name, values);
		// TODO Auto-generated constructor stub
	}

}
