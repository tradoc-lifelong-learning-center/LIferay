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
	
	public Article(String title, long articleId, String createDate, String modifiedDate, String type) {
		this.title = title;
		this.articleId = articleId;
		this.createDate = createDate;
		this.modifiedDate = modifiedDate;
		this.type = type;
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
	
}