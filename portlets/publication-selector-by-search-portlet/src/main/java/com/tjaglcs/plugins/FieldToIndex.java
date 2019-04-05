package com.tjaglcs.plugins;

import com.tjaglcs.plugins.CustomField;

public class FieldToIndex {
	private String fieldValue;
	private String fieldNameString ="";
	private int fieldNameInt = -1;
	
	
	public FieldToIndex(String fieldValue, String fieldNameString) {
		this.fieldValue = fieldValue;
		this.fieldNameString = fieldNameString;
	}
	
	public FieldToIndex(String fieldValue, int fieldNameInt) {
		this.fieldValue = fieldValue;
		this.fieldNameInt = fieldNameInt;
	}
	

	public String getFieldName() {
		
		if(this.fieldNameInt>0) {
			return Integer.toString(this.fieldNameInt);
		} else {
			return fieldNameString;
		}
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public String getFieldNameString() {
		return fieldNameString;
	}

	public int getFieldNameInt() {
		return fieldNameInt;
	}


	
	
	
}
