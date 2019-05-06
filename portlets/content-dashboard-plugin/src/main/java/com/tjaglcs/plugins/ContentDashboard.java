package com.tjaglcs.plugins;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.liferay.portlet.layoutsadmin.util.Sitemap;
import com.liferay.portlet.layoutsadmin.util.SitemapUtil;

import java.util.List;

import javax.portlet.RenderRequest;

/**
 * Portlet implementation class ContentDashboard
 */
public class ContentDashboard extends MVCPortlet {

	public List<Layout> doStuff(RenderRequest request) throws Exception {
		System.out.print("hello world");
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
		long groupId = themeDisplay.getScopeGroupId();
		List<Layout> layouts=LayoutLocalServiceUtil.getLayouts(groupId, false);
        System.out.print("page 0:"+layouts.get(0).getFriendlyURL()+" \n");
        
        
        
        
        
        return layouts;
	}
	
	public String fetchSiteMap(RenderRequest request) throws PortalException, SystemException {
		
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
		long groupId = themeDisplay.getScopeGroupId();
		
		Sitemap mapObj = SitemapUtil.getSitemap(); 
		
		String sitemap = SitemapUtil.getSitemap(groupId, true, themeDisplay); 
		
		System.out.println(sitemap);
		
		
		return sitemap;
		
	}
	
}
