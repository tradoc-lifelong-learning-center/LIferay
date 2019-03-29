package com.tjaglcs.plugins;

public class Article {
	private long articleId;
	private int volume;
	private int issue;
	
	
	
	public Article(long articleId, int volume, int issue) {
		this.articleId = articleId;
		this.volume = volume;
		this.issue = issue;
	}
	
	
	public String getQueryString() {
		
		String queryString = "";
		queryString+="?articleId=";
		queryString+=articleId;
		
		queryString+="&vol=";
		queryString+=volume;
		
		queryString+="&no=";
		queryString+=issue;
		
		return queryString;
	}


	public long getArticleId() {
		return articleId;
	}





	public int getVolume() {
		return volume;
	}





	public int getIssue() {
		return issue;
	}





	public static void main(String[] args) {
		

	}

}
