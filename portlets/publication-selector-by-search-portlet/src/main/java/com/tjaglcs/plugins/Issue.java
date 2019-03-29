package com.tjaglcs.plugins;

import java.util.List;

public class Issue {
	private int number;
	private List<Article> articles;
	private int volume;
	
	public Issue(int number, List<Article> articles) {
		this.number = number;
		this.articles = articles;
	}

	public int getNumber() {
		return number;
	}

	public List<Article> getArticles() {
		return articles;
	}
	
	public int getVolume() {
		setVolume(fetchVolume());
		return this.volume;
	}
	
	public void setVolume(int volume) {
		this.volume = volume;
	}



	private int fetchVolume(){
		//need to determine the volume based on article list. SHOULD all be the same
		
		int volume = this.articles.get(0).getVolume();
		
		for(int i = 0; i<this.articles.size(); i++) {
			if(volume==this.articles.get(i).getVolume()) {
				continue;
			} else {
				System.out.println("Error: volumes in issue " + this.number + " don't match.");
			}
		}
		
		return volume;
	}

}
