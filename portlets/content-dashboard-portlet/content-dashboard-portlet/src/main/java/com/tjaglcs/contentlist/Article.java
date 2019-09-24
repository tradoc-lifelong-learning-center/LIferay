package com.tjaglcs.contentlist;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.journal.service.JournalContentSearchLocalServiceUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;

import javax.portlet.RenderRequest;

public class Article {
	private String title;
	private long articleId;
	private String createDate;
	private String modifiedDate;
	private String type;
	private long id;
	private long groupId;
	private String url;
	
	
	public Article(RenderRequest request, String title, String createDate, String modifiedDate, String type, long id) throws SystemException, PortalException, UnsupportedEncodingException {
		this.title = title;
		this.articleId = articleId;
		this.createDate = createDate;
		this.modifiedDate = modifiedDate;
		this.type = type;
		this.id = id;
		this.groupId = this.setGroupId(request);
		
		setURL(request);
		//System.out.println("url: " + this.url);
	}

	public String getTitle() {
		return title;
	}

	public long getArticleId() {
		return articleId;
	}

	public String getCreateDate() {
		return createDate;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public String getType() {
		return type;
	}
	
	public long getGroupId() {
		return this.groupId;
	}
	
	public long getId() {
		return this.id;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public void setURL(RenderRequest request) throws SystemException, PortalException, UnsupportedEncodingException {
		
		//System.out.println("type: " + type);
		
		String documentClassName = "Document";
		String journalClassName = "Web Content Article";

		if(this.type.contains(journalClassName)) {
			//System.out.println("Journal!");

			//trying to find parent page URL. from https://stackoverflow.com/questions/8397679/get-portlet-page-containing-web-content-in-liferay
			long groupId = getGroupId();
			
			//System.out.println("groupId: " + groupId);
			
			ThemeDisplay themeDisplay = getThemeDisplay(request);

			long articleId = this.getId();
			
			//System.out.println("articleId: " + articleId);

			
			List<Long> layoutIds = JournalContentSearchLocalServiceUtil.getLayoutIds(groupId, false, Long.toString(articleId));
			
			if (!layoutIds.isEmpty()) {
				  long layoutId = layoutIds.get(0).longValue();
				  Layout layout = LayoutLocalServiceUtil.getLayout(groupId, false, layoutId);
				  String url = PortalUtil.getLayoutURL(layout, themeDisplay);
				  //String url = PortalUtil.getLayoutFriendlyURL(layout, themeDisplay);
				  //System.out.println("url: " + url);
				  this.url = url;
				}
			
		} else if(this.type.contains(documentClassName)) {
			DLFileEntry entry = DLFileEntryLocalServiceUtil.fetchDLFileEntry(this.id);
			
			String urlTitle = URLEncoder.encode(this.title, "UTF-8");
			long folderId = entry.getFolderId();
			String uuid = entry.getUuid();

			this.url = "/documents/" + this.groupId + "/" + folderId + "/" + urlTitle + "/" + uuid;

		}

		
		
	}

	private ThemeDisplay getThemeDisplay(RenderRequest req) {
		return (ThemeDisplay) req.getAttribute(WebKeys.THEME_DISPLAY);
	}
	
	private long setGroupId(RenderRequest req) {
		ThemeDisplay themeDisplay = getThemeDisplay(req);
		long portletGroupId = themeDisplay.getScopeGroupId();
		
		return portletGroupId;
	}
	
}