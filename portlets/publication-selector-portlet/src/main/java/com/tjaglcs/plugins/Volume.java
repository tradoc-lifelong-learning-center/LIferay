package com.tjaglcs.plugins;

public class Volume {
	private long[] articleIds;
	private Article[] articles;
	private int volumeNumber;
	private QueryString queryString; 
	
	
	public Volume(long[] articleIds, int volumeNumber, QueryString queryString) {
		this.articleIds = articleIds;
		setArticles(articleIds);
		this.volumeNumber = volumeNumber;
		this.queryString = queryString;
	}



	public Article[] getArticles() {
		return articles;
	}


	public QueryString getQueryString() {
		return queryString;
	}

	public void setArticles(long[] articleIds) {
		
		Article[] articles = new Article[articleIds.length];
		//System.out.println("article ids: " + articleIds );
		//System.out.println("article ids: " + articleIds.length );
		
		for(int i = 0; i<articleIds.length; i++) {
			//System.out.println("adding " + articleIds[i] + " to array");
			articles[i] = new Article(articleIds[i], this.volumeNumber);
		}
		
		this.articles = articles;
		
	}



	public int getVolumeNumber() {
		return volumeNumber;
	}



	public void setVolumeNumber(int volumeNumber) {
		this.volumeNumber = volumeNumber;
	}
	
	
	
	
	
	
}
