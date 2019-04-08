package com.tjaglcs.plugins.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.tjaglcs.plugins.Average;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AverageTest {

	
	@org.junit.Test
	   public void Mode_single() {
			int[] numbers = new int[] {1,1,1,2};
			Average average = new Average(numbers);
		
		   List<Integer> result = average.getMode();
		   List<Integer> desiredResults = Arrays.asList(1);
		   

		   assertEquals(desiredResults,result);
	   }
	
	@org.junit.Test
	   public void Mode_multi() {
			int[] numbers = new int[] {1,1,1,2,2,2};
			Average average = new Average(numbers);
		
		   List<Integer> result = average.getMode();
		   List<Integer> desiredResults = Arrays.asList(1,2);
		   

		   assertEquals(desiredResults,result);
	   }
	
	@org.junit.Test
	   public void Mode_noMode() {
			int[] numbers = new int[] {1,2,3};
			Average average = new Average(numbers);
		
		   List<Integer> result = average.getMode();
		   List<Integer> desiredResults = Arrays.asList(1,2,3);
		   
		   assertEquals(desiredResults,result);
	   }
	
	@org.junit.Test(expected = AssertionError.class)
	   public void Mode_null() {
			int[] numbers = new int[] {};
			Average average = new Average(numbers);
		
		   List<Integer> result = average.getMode();
		   List<Integer> desiredResults = Arrays.asList(1,2,3);

		   assertEquals(desiredResults,result);
	   }
}
