package com.tjaglcs.plugins.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.tjaglcs.plugins.QueryString;

public class QueryStringTest {

	QueryString queryString_single;
	QueryString queryString_double;
	QueryString queryString_multi;
	//pub is currently hardcoded because YAGNI until v2 of this portlet
	String pub = "mlr";
	
	@org.junit.Before
	public void setup() {
		queryString_single = new QueryString("articleId=12345&vol=200;");
		queryString_double = new QueryString("articleId=12345-54321&vol=200;");
		queryString_multi = new QueryString("articleId=12345-54321-98765&vol=200;");
		
	}
	
	//test get query string
	@org.junit.Test
   public void getQueryString_single() {
	   String result = queryString_single.getQueryString();
	   String desiredResults = "?pub=mlr&vol=200";

	   assertEquals(desiredResults,result);
   }
	
	@org.junit.Test
   public void getQueryString_double() {
	   String result = queryString_double.getQueryString();
	   String desiredResults = "?pub=mlr&vol=200";

	   assertEquals(desiredResults,result);
   }
	
	@org.junit.Test
	   public void getQueryString_multi() {
		   String result = queryString_multi.getQueryString();
		   String desiredResults = "?pub=mlr&vol=200";

		   assertEquals(desiredResults,result);
	   }
	
	//test get volume number
	@org.junit.Test
	   public void getVolumeNumber_single() {
		   int result = queryString_single.getVolumeNumber();
		   int desiredResults = 200;

		   assertEquals(desiredResults,result);
	   }
	
	@org.junit.Test
	   public void getVolumeNumber_double() {
		   int result = queryString_double.getVolumeNumber();
		   int desiredResults = 200;

		   assertEquals(desiredResults,result);
	   }
	
	@org.junit.Test
	   public void getVolumeNumber_multi() {
		   int result = queryString_single.getVolumeNumber();
		   int desiredResults = 200;

		   assertEquals(desiredResults,result);
	   }
	
	//test get article IDs
	@org.junit.Test
	   public void getArticleId_single() {
		   long[] result = queryString_single.getArticleIds();
		   long[] desiredResults = new long[] {12345};

		   assertArrayEquals(desiredResults,result);
	   }
	
	@org.junit.Test
	   public void getArticleId_double() {
		   long[] result = queryString_double.getArticleIds();
		   long[] desiredResults = new long[] {12345,54321};

		   assertArrayEquals(desiredResults,result);
	   }
	
	@org.junit.Test
	   public void getArticleId_multi() {
		   long[] result = queryString_multi.getArticleIds();
		   long[] desiredResults = new long[] {12345,54321,98765};

		   assertArrayEquals(desiredResults,result);
	   }
}
