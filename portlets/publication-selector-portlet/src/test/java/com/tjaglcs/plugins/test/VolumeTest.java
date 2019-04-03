package com.tjaglcs.plugins.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.tjaglcs.plugins.Article;
import com.tjaglcs.plugins.QueryString;
import com.tjaglcs.plugins.Volume;

public class VolumeTest {

	QueryString queryString_single;
	Volume volume_single;
	Article[] articles_single;
	
	
	QueryString queryString_multi;
	Volume volume_multi;
	Article[] articles_multi;
	
	
	
	@org.junit.Before
	public void setup() {
		queryString_single = new QueryString("articleId=12345&vol=200;");
		volume_single = new Volume(queryString_single);
//		articles_single = new Article[] {new Article(queryString_single.getArticleIds()[0], queryString_single.getVolumeNumber())};
		
		
		queryString_multi = new QueryString("articleId=12345-54321-98765&vol=200;");
		volume_multi = new Volume(queryString_multi);
//		articles_multi = new Article[queryString_multi.getArticleIds().length];
//		for(int i = 0; i<queryString_multi.getArticleIds().length; i++) {
//			articles_multi[i] = new Article(queryString_multi.getArticleIds()[i], queryString_multi.getVolumeNumber());
//		}
	}
	
	
	//test get articles
//	@org.junit.Test
//	   public void getArticles_single() {
//		   Article[] result = volume_single.getArticles();
//		   Article[] desiredResults = articles_single;
//
//		   assertArrayEquals(desiredResults,result);
//	   }
//	
//	@org.junit.Test
//	   public void getArticles_multi() {
//		   Article[] result = volume_multi.getArticles();
//		   Article[] desiredResults = articles_multi;
//
//		   assertArrayEquals(desiredResults,result);
//	   }
	
	//test get query string
	@org.junit.Test
	   public void getQueryString_single() {
		   long[] result = volume_single.getQueryString().getArticleIds();
		   long[] desiredResults = new long[] {12345};

		   assertArrayEquals(desiredResults,result);
	   }
	
	@org.junit.Test
	   public void getQueryString_multi() {
		   long[] result = volume_multi.getQueryString().getArticleIds();
		   long[] desiredResults = new long[] {12345,54321,98765};

		   assertArrayEquals(desiredResults,result);
	   }
	
	
	//test get volume number
	@org.junit.Test
	   public void getVolume_single() {
		   int result = volume_single.getVolumeNumber();
		   int desiredResults = 200;

		   assertEquals(desiredResults,result);
	   }
	
	@org.junit.Test
	   public void getVolume_multi() {
		   int result = volume_multi.getVolumeNumber();
		   int desiredResults = 200;

		   assertEquals(desiredResults,result);
	   }
	
	
}
