package com.tjaglcs.plugins;

import java.util.Date;
import java.util.List;

public class Volume {
	private String journalName;
	private int number;
	private List<Issue> issues;
	private Date publishDate;
	private int year;
	
	public Volume(int number, List<Issue> issues) {
		this.journalName = journalName;
		this.number = number;
		this.issues = issues;
		//System.out.println("building volume " + this.number);
	}

	public String getJournalName() {
		return journalName;
	}

	public int getNumber() {
		return number;
	}

	public List<Issue> getIssues() {
		return issues;
	}
	
	public String getIssueList() {
		
		//int[] issueArray = new int[]{1,2,3,4};
		String issueString = "'1,2,3,4'";
		
		return issueString;
	}
	
	
	
}
