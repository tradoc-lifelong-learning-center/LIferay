package com.tjaglcs.plugins;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Volume {
	private String publicationName;
	private int number;
	private List<Issue> issues;
	private List<Integer> selectedIssues = new ArrayList<>();
	private Issue mostRecentIssue;
	private List<Article> articles;
	private Date publishDate;
	private int year;
	private String editionType;
	
	public Volume(String  publicationName, int number, List<Article> articles) throws Exception {
		this.publicationName = publicationName;
		this.number = number;
		this.articles = articles;

		setIssues();
		setMostRecentIssue();
		//by default, include all issues in selection
		setSelectedIssues();

		setYear();
		setEditionType();

	}
	
	public void setSelectedIssues() {
		for(int i = 0; i<issues.size(); i++) {
			this.selectedIssues.add(issues.get(i).getNumber());
		}
	}
	
	//meant to replace list of issues with a single issue when there's one selected
	public void setSelectedIssue(int issueNum) {
		this.selectedIssues.clear();
		this.selectedIssues.add(issueNum);
	}
	
	public void setMostRecentIssue() {
		int latestIssueNumber = 0;
		Issue latestIssue = null;

		for(int i = 0; i<issues.size(); i++) {
			if(issues.get(i).getNumber()>latestIssueNumber) {
				latestIssue = issues.get(i);
				latestIssueNumber = latestIssue.getNumber();
			} 
		}
		
		this.mostRecentIssue = latestIssue;
	}

	public Issue getMostRecentIssue() {
		return mostRecentIssue;
	}

	public List<Integer> getSelectedIssues() {
		return this.selectedIssues;
	}
	

	public void setEditionType() {
		String issueType = "Online"; //default
		
		for(int i = 0; i<issues.size(); i++) {
			//if there's a single online, I'm assuming this is or will soon be an online edition
			if(issues.get(i).getEditionType().contains("PDF")) {
				issueType = issues.get(i).getEditionType();
			} else if(issues.get(i).getEditionType().contains("Online")) {
				issueType = issues.get(i).getEditionType();
				break;
			}
		}
		this.editionType = issueType;
	}

	public String getEditionType() {
		return editionType;
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
	
	public Issue getIssue(int issueNumber){
		for(int i = 0; i<this.issues.size(); i++) {
			if(this.issues.get(i).getNumber()==issueNumber) {
				return this.issues.get(i);
			} 
		}
		
		System.out.println("No issue with the number " + issueNumber);
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
	
	public void setIssues() throws Exception {
		

		List<Article> articlesArray = this.articles;
		HashMap<Integer, List<Article>> issuesMap = new HashMap<>();


		for(int i = 0; i<articlesArray.size(); i++) {

			int currentIssue = articlesArray.get(i).getIssue();
			Article currentArticle = articlesArray.get(i);
		
			if (!issuesMap.containsKey(currentIssue)) {
			    List<Article> list = new ArrayList<Article>();
			    list.add(currentArticle);

			    issuesMap.put(currentIssue, list);
			} else {
				issuesMap.get(currentIssue).add(currentArticle);
			}
		}

		
		ArrayList<Issue> issueArray = new ArrayList<>();
		
		issuesMap.forEach((k,v) -> issueArray.add(new Issue(this.publicationName,k,v.get(0).getIssueName(),v)));
		 
		
		//SORT
		List<Issue> issueArraySorted = new IssueSorter(issueArray).getSortedIssues();
		
		this.issues = issueArraySorted;
	 
	}
	
}
