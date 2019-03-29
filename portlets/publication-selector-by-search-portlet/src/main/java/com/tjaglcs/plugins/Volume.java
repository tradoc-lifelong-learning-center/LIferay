package com.tjaglcs.plugins;

import java.util.List;

public class Volume {
	private String journalName;
	private int number;
	private List<Issue> issues;
	
	public Volume(int number, List<Issue> issues) {
		super();
		this.journalName = journalName;
		this.number = number;
		this.issues = issues;
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
