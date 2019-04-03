package com.tjaglcs.plugins;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryString {
	String configString;
	String queryString;
	String pub;
	long[] articleIds;
	int volumeNumber;
	
	public QueryString(String configString) {
		//this.pub = this.extractQueryStringVals(configString, "pub");
		this.pub = "mlr";
		this.volumeNumber = Integer.parseInt(this.extractQueryStringVals(configString, "vol"));
		setArticleIds(configString);
		this.queryString = "?" + "pub=" + this.pub + "&vol=" + volumeNumber;
	}
	
	private String extractQueryStringVals(String textToSearch, String paramName) {
		//method to extract certain query string values
		String pattern = "(" + paramName + "=)([0-9|-]+)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(textToSearch);
		
		if(m.find()) {
			return m.group(2);
		} else {
			return "-1";
		}
		
	}	
	
	public String getQueryString() {
		return queryString;
	}

	public long[] getArticleIds() {
		return articleIds;
	}
	
	public void setArticleIds(String queryString) {

		String idString = this.extractQueryStringVals(queryString, "articleId");
		
		String ids[] = idString.split("-");
		
		long[] idNumbers = new long[ids.length]; 
		
		for(int i = 0; i<ids.length; i++) {
			idNumbers[i] = Long.parseLong(ids[i]);
 		}
		
		this.articleIds = idNumbers;
	}
	
	public int getVolumeNumber() {
		return volumeNumber;
	}
	
	public void setVolumeNumber(int volumeNumber) {
		this.volumeNumber = volumeNumber;
	}
	
	

}
