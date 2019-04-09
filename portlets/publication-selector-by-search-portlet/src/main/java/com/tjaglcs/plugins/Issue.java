package com.tjaglcs.plugins;

import java.util.List;

public class Issue {
	private int number;
	private List<Article> articles;
	private int volume;
	private int year;
	
	public Issue(int number, List<Article> articles) {
		this.number = number;
		this.articles = articles;
		setVolume();
		setYear();
		//System.out.println("building issue " + this.number);
	}

	public int getNumber() {
		return number;
	}

	public List<Article> getArticles() {
		return articles;
	}
	
	

	public void setYear() {
		
		int[] years = new int[articles.size()];
		
		for(int i = 0; i<articles.size(); i++) {
			//System.out.println("article year is " + articles.get(i).getArticleDate().getYear());
			
			try {
				//System.out.println("article year: " + articles.get(i).getArticleDate().getYear());
				years[i] = articles.get(i).getArticleDate().getYear();
			} catch (Exception e) {
				System.out.println("no date");
				//e.printStackTrace();
			}
		}
		
		Average average = new Average(years);
		List<Integer> yearMode = average.getMode();
		//System.out.println("mode: " + average.getMode());
		
		this.year = yearMode.get(0);
	}
	
	public int getYear() {
		return year;
	}

	public int getVolume() {
		return this.volume;
	}

	private void setVolume(){
		//need to determine the volume based on article list. SHOULD all be the same
		
		int volume = this.articles.get(0).getVolume();
		
		for(int i = 0; i<this.articles.size(); i++) {
			if(volume==this.articles.get(i).getVolume()) {
				continue;
			} else {
				System.out.println("Error: volumes in issue " + this.number + " don't match.");
			}
		}
		
		//System.out.println("volume from setVolume in issue: " + volume);
		this.volume = volume;
	}

}
