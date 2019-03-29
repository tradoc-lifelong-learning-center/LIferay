package com.tjaglcs.plugins.test;

import junit.framework.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import com.tjaglcs.plugins.Article;

public class ArticleTest {
	
	Article article;
	
	//@before will run before each test. Instantiate object here
	@org.junit.Before
	public void setup() {
		article = new Article(12345,10,5);
	}
   
   @org.junit.Test
   public void getQueryString() {
	   String result = article.getQueryString();
	   String desiredResults = "?articleId=12345&vol=10&no=5";

	   assertEquals(desiredResults,result);
   }
   
   @org.junit.Test
   public void getArticleId() {
	   long result = article.getArticleId();
	   long desiredResults = 12345;

	   assertEquals(desiredResults,result);
   }
   
   @org.junit.Test
   public void getVolume() {
	   int result = article.getVolume();
	   int desiredResults = 10;

	   assertEquals(desiredResults,result);
   }
   
   @org.junit.Test
   public void getIssue() {
	   int result = article.getIssue();
	   int desiredResults = 5;

	   assertEquals(desiredResults,result);
   }
   
   
}
