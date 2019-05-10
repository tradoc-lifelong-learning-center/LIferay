package com.tjaglcs.plugins;

import java.util.List;

public class Issue implements Comparable<Issue>{
	private String publicationName;
	private int number;
	private String name;
	private List<Article> articles;
	private int volume;
	private int year;
	
	
	public Issue(String publicationName, int number, String name, List<Article> articles) {
		this.publicationName = publicationName;
		this.number = number;
		this.name = name;
		this.articles = articles;
		setVolume();
		setYear();
		//System.out.println("building issue " + this.number);
	}
	
	//allow issues to be sorted by number
	public int compareTo(Issue compareIssue) {
		//ascending
		//return (this.getNumber() < compareIssue.getNumber() ? -1 : 
        //    (this.getNumber() == compareIssue.getNumber() ? 0 : 1));
		
		//descending
		return (this.getNumber() < compareIssue.getNumber() ? 1 : 
            (this.getNumber() == compareIssue.getNumber() ? 0 : -1)); 
		
	}	
	
	public String getPublicationName() {
		return this.publicationName;
	}
	
		public int getNumber() {
		return number;
	}

	public List<Article> getArticles() {
		return articles;
	}
	
	

	public String getName() {
		return name;
	}

	public void setYear() {
		
		int[] years = new int[articles.size()];
		
		for(int i = 0; i<articles.size(); i++) {
			//System.out.println("article year is " + articles.get(i).getArticleDate().getYear());
			
			try {
				//System.out.println("article year: " + articles.get(i).getArticleDate().getYear());
				years[i] = articles.get(i).getArticleDate().getYear();
			} catch (Exception e) {
				//System.out.println("no date");
				e.printStackTrace();
			}
		}
		
		Average average = new Average(years);
		List<Integer> yearMode = average.getMode();
		
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
		//System.out.println("Set  volume in issue: " + this.articles.get(0).getVolume());
		int volume = this.articles.get(0).getVolume();
		
		
		for(int i = 0; i<this.articles.size(); i++) {
			if(volume==this.articles.get(i).getVolume()) {
				//System.out.println(this.articles.get(i).getTitle() + " in volume " + this.articles.get(i).getVolume() + " with issue " + this.articles.get(i).getIssue());
				continue;
			} else {
				System.out.println("Error: volumes in issue " + this.number + " don't match.");
			}
		}

		this.volume = volume;
	}

}
