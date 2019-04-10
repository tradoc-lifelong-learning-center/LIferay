package com.tjaglcs.plugins;

import java.util.Date;
import java.util.List;

public class Volume {
	private String publicationName;
	private int number;
	private List<Issue> issues;
	private Date publishDate;
	private int year;
	
	public Volume(String  publicationName, int number, List<Issue> issues) {
		this.publicationName = publicationName;
		this.number = number;
		this.issues = issues;
		setYear();
		//System.out.println("building volume " + this.number);
	}

	public String getPublicationName() {
		return publicationName;
	}

	public int getNumber() {
		return number;
	}

	public List<Issue> getIssues() {
		return issues;
	}
	
	public Issue getIssue(int volumeNumber){
		for(int i = 0; i<this.issues.size(); i++) {
			if(this.issues.get(i).getNumber()==volumeNumber) {
				return this.issues.get(i);
			} 
		}
		
		System.out.println("No issue with the number " + volumeNumber);
		return null;
	}
	
	public int getYear() {
		return this.year;
	}
	
	public void setYear() {
		
		int[] years = new int[issues.size()];
		
		for(int i = 0; i<issues.size(); i++) {
			//System.out.println("article year is " + articles.get(i).getArticleDate().getYear());
			
			try {
				//System.out.println("article year: " + articles.get(i).getArticleDate().getYear());
				years[i] = issues.get(i).getYear();
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
	
	
}
