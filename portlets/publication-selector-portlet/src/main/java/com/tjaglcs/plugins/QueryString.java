package com.tjaglcs.plugins;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryString {
	String queryString;
	long[] articleIds;
	int volumeNumber;
	
	public QueryString(String queryString) {
		this.queryString = "?" + queryString;
		this.volumeNumber = Integer.parseInt(this.extractQueryStringVals(queryString, "vol"));
		setArticleIds(queryString);
	}
	
	private String extractQueryStringVals(String textToSearch, String paramName) {
		//method to extract certain query string values
		
		//String pattern = "(" + paramName + "=)(\\d+-*)";
		String pattern = "(" + paramName + "=)([0-9|-]+)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(textToSearch);
		
		if(m.find()) {
			//System.out.println("found " + m.group(2) + " for " + paramName);
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
		
		
		//queryString = "articleId=27423-27413-27403-25147&vol=225";
		//System.out.println("queryString:  " + queryString);
		
		String idString = this.extractQueryStringVals(queryString, "articleId");
		
		//System.out.println("idString:  " + idString);
		
		String ids[] = idString.split("-");
		
		long[] idNumbers = new long[ids.length]; 
		
		//System.out.println("ids length: " + ids.length);
		
		for(int i = 0; i<ids.length; i++) {
			idNumbers[i] = Long.parseLong(ids[i]);
			//System.out.println("added: " + idNumbers[i]);
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
