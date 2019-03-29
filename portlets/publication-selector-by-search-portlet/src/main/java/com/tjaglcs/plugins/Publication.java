package com.tjaglcs.plugins;

import java.util.List;

public class Publication {
	private String name;
	private List<Volume> volumes;
	
	
	public Publication(String name, List<Volume> volumes) {
		this.name = name;
		this.volumes = volumes;
	}
	
	public String getName() {
		return name;
	}
	public List<Volume> getVolumes() {
		return volumes;
	}
	
	public String getJson() {
		
		String JSON = "{'publication':{'name':'Military Law Review','volumes':{";
		
		List<Volume> volumes = getVolumes();
		
		for(int v = 0; v<volumes.size(); v++) {
			int volNo = volumes.get(v).getNumber();
			
			JSON+="'volume" + volNo + "':{'volumeNumber':'" + volNo + "',";
			
			List<Issue> issues = volumes.get(v).getIssues();
			
			for(int i = 0; i<issues.size(); i++) {
				int issueNo = issues.get(i).getNumber();
				
				List<Article> articles = issues.get(i).getArticles();
				
				JSON+="'issues':{";
				
				JSON+="'issue" + issueNo + "':{";
				
				JSON+="'issueNumber':'" + issueNo + "',";
				
				JSON+="'articles':{";
				
				for(int a = 0; a<articles.size(); a++) {
					
					JSON+="'article" + a + "':{";
					
					JSON+="'articleNumber':'" + articles.get(a).getId() + "',";
					
					JSON+="'articleTitle':'" + articles.get(a).getTitle() + "'},";
					
				}
				
				JSON+="}}},";  
			}
		
			JSON+="},"; 
			
		}
		
		
		
		JSON +="}}}";
		
		System.out.println(JSON);
		
		return JSON;
	}
	
	
}
