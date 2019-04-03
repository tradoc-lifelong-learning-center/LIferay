package com.tjaglcs.plugins;

import com.tjaglcs.plugins.Article;

import javax.portlet.RenderRequest;
import javax.portlet.PortletPreferences;
import javax.servlet.http.HttpServletRequest;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;



/**
 * Portlet implementation class ContentSelector
 */
public class ContentSelector extends MVCPortlet {
	
	private RenderRequest globalReq;
	private Volume[] volumes;
	
	
	
	
	public Volume[] getVolumes() {
		return volumes;
	}

	public void setVolumes(RenderRequest req) throws Exception {
		
		String volumeConfig = getArticleIdsConfig(req);
		String[] volumeConfigStrings = volumeConfig.split(";");
		
		Volume[] volumeConfigs = new Volume[volumeConfigStrings.length];
		
		for(int i = 0; i<volumeConfigStrings.length; i++) {
			//using -1 to skip the last item, which is archive link
			String volumeString = volumeConfigStrings[i];
			
			QueryString queryString = new QueryString(volumeString);
			
			Volume volume = new Volume(queryString);
			volumeConfigs[i] = volume;
		} 
		
		//update class variables for later use
		this.volumes = volumeConfigs;
		this.globalReq = req;

	}
	
	//fetch "current" volume (the volume the user has selected)
	public Volume fetchCurrentVolume(RenderRequest req) throws Exception {
		//get the query string value from browser (not queryString object)
		String queryStringValue = getQueryStringValue("vol");
		
		
		if(volumes.length==0) {
			//if there are no articles in the config, return an error and prevent from crashing
			System.out.println("no article in config. Please add using the article ID, volume, and issue number (separated by semi-colons): articleId=25147&vol=225&no=4;articleId=25167&vol=225&no=3.");
			return null;
		} else if(queryStringValue==null) {
			//this is if there's no query string, so
			//return most recent
			System.out.println("No article in query string. Using most recent.");
			return volumes[0];
		} else {
			System.out.println("Fetching volume: " + queryStringValue);
			
			Volume currentVolume = new Volume(getEmptyArticleIdConfig());
			
			for(int i=0; i<this.volumes.length; i++) {
				
				if(this.volumes[i].getVolumeNumber()==Integer.parseInt(queryStringValue)) {
					System.out.println("found volume " + volumes[i].getVolumeNumber());
					currentVolume = volumes[i];
				} else {
					System.out.println("didn't find volume " + volumes[i].getVolumeNumber());
				}
			}
			System.out.print(currentVolume.getArticles());
			return currentVolume;
		}
		
		
		
	}
 
	public boolean isMostRecent() throws Exception {
		Volume currentVol = fetchCurrentVolume(globalReq);
		Volume mostRecentVol = volumes[0];
		
		if(currentVol.equals(mostRecentVol)) {
			return true;
		} else {
			return false;
		}
		
	}
	
	private String getQueryStringValue(String stringParam) {
		HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(globalReq));
		String queryString = httpReq.getParameter(stringParam);
		return queryString;
	}
	
	public long getGroupId(RenderRequest req) {
		ThemeDisplay themeDisplay = getThemeDisplay(req);
		long portletGroupId = themeDisplay.getScopeGroupId();
		
		return portletGroupId;
	}
	
	private ThemeDisplay getThemeDisplay(RenderRequest req) {
		return (ThemeDisplay) req.getAttribute(WebKeys.THEME_DISPLAY);
	}
	
	
	private String getArticleIdsConfig(RenderRequest req) throws Exception {
		PortletPreferences portletPreferences = req.getPreferences();
		String articleIDs = GetterUtil.getString(portletPreferences.getValue("contentSelectorIncludeArticles", "-1"));
		
		return articleIDs;
	}
	
	private long[] getEmptyArticleIdConfig() throws Exception {
		PortletPreferences portletPreferences = globalReq.getPreferences();
		String emptyArticleString = GetterUtil.getString(portletPreferences.getValue("contentSelectorArticleNotFound", "-1"));
		
		long[] emptyArticle = new long[1];
		
		try {
			emptyArticle[0] = Long.parseLong(emptyArticleString);
		} catch (Exception e) {
			emptyArticle[0]=-1;
			e.printStackTrace();
		}
		
		return emptyArticle;
	}

	public String getArchiveUrl() {
		PortletPreferences portletPreferences = globalReq.getPreferences();
		String archiveUrlString = GetterUtil.getString(portletPreferences.getValue("contentSelectorArchiveUrl", "https://tjaglcspublic.army.mil/mlr-archives"));
		return archiveUrlString;
	}
	
}
