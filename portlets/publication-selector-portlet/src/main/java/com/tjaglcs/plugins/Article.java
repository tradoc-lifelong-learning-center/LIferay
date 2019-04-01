package com.tjaglcs.plugins;

public class Article {
	private long articleId;
	private int volume;
	
	public Article(long articleId, int volume) {
		this.articleId = articleId;
		this.volume = volume;
	}

	public long getArticleId() {
		return articleId;
	}

	public int getVolume() {
		return volume;
	}

}
