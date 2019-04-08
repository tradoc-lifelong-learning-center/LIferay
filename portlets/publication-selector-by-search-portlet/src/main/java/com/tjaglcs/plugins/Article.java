package com.tjaglcs.plugins;

public class Article {
	private String title;
	private String publicationName;
	private long id;
	private double version;
	private int volume;
	private int issue;
	private String type;
	
	
	
	
	public Article(String title, String publicationName, long id, double version, int volume, int issue, String type) {
		super();
		this.title = title;
		this.publicationName = publicationName;
		this.id = id;
		this.version = version;
		this.volume = volume;
		this.issue = issue;
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public String getPublicationName() {
		return publicationName;
	}
	public long getId() {
		return id;
	}
	public double getVersion() {
		return version;
	}
	public int getVolume() {
		return volume;
	}
	public int getIssue() {
		return issue;
	}
	public String getType() {
		return type;
	}
	
	
}
