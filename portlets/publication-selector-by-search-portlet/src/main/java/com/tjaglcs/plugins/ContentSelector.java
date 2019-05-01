package com.tjaglcs.plugins;

import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.tjaglcs.plugins.Publication;

public class ContentSelector extends MVCPortlet { 
	
	public Publication fetchPublication(RenderRequest request) throws Exception {
		
		PortletPreferences portletPreferences = request.getPreferences();
		String pubName = GetterUtil.getString(portletPreferences.getValue("publicationName", ""));
		
		Publication pub = new Publication(pubName, request);
		
		return pub;
	}
	
		
}
