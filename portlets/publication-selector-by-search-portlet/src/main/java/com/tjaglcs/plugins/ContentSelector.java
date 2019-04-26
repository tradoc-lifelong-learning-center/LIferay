package com.tjaglcs.plugins;

import javax.portlet.RenderRequest;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.tjaglcs.plugins.Publication;

public class ContentSelector extends MVCPortlet { 
	
	public Publication fetchPublication(RenderRequest request) throws Exception {
		
		Publication pub = new Publication("Military Law Review", request);
		
		return pub;
	}
		
}
