package com.tjaglcs.plugins;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;
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
	private String publicationName;
	private long id;
	private double version;
	private int volume;
	private int issue;
	private String type;
	int status;
	private String url;
	private LocalDate articleDate;
	private RenderRequest request;
	private long groupId;
	private String[] authors;
	
	
	public Article(String title, String publicationName, long id, double version, int volume, int issue, String type, int status, LocalDate articleDate, RenderRequest request, String[] authors) throws SystemException, PortalException, UnsupportedEncodingException {
		this.request = request;
		this.groupId = this.setGroupId(request);
		this.title = title;
		this.publicationName = publicationName;
		this.id = id;
		this.version = version;
		this.volume = volume;
		this.issue = issue;
		this.type = type;
		this.status = status;
		setURL(request);
		this.articleDate = articleDate;
		this.authors = authors;
		
		
		//System.out.println("building article " + this.title);
	}
	public String getTitle() {
		return title;
	}
	public String getPublicationName() {
		return publicationName;
	}
	public long getId() {
		return id;
	}
	public double getVersion() {
		return version;
	}
	public int getVolume() {
		return volume;
	}
	public int getIssue() {
		return issue;
	}
	public String getType() {
		return type;
	}
	public LocalDate getArticleDate() {
		return articleDate;
	}
	public void setArticleDate(LocalDate articleDate) {
		this.articleDate = articleDate;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getURL() {
		return this.url;
	}
	public long getGroupId() {
		return this.groupId;
	}
	
	public String getAuthors() {
		return formatAuthors(this.authors);
	}
	
	private String formatAuthors(String[] authorList) {
		String authorString = "";
		
		if(authorList.length>0) {
			authorString += "by ";
		}
		
		//by Joe Smith
		//by Joe Smith and Bill Smith
		//by Joe Smith, Bill Smith, and Phil Smith
		//by Joe Smith, Bill Smith, Biff Smith, and Phil Smith
		
		
		for(int i = 0; i<authorList.length; i++) {
			
			if(authorList.length-1==i && authorList.length>2) {
				authorString += ", and ";
			} else if(authorList.length-1==i && authorList.length==2) {
				authorString += " and ";
			} else if(authorList.length>1) {
				authorString += ", ";
			}
			
			authorString += authorList[i];
			//WHY am I geting a string with the array ID instead of the actual array?
			
		}
		
		System.out.println("author string: " + authorString);
		
		return authorString;
	}
	
	
	public void setURL(RenderRequest request) throws SystemException, PortalException, UnsupportedEncodingException {
		// TODO:
		//get most recent vol/issue and display if no query string
		//if query string, get that vol/issue and display
		//How do I display ACTUAL article title (from content) versions shortened title field?
		//How to I link to custom page URLs? Can I get a display page set up?
		//System.out.println("volume 1: " + pub.getVolume(1).getIssue(1));
		//System.out.println("latest volume: " + pub.getMostRecentVolume());

		
		
		

		
		
		String documentClassName = "DLFileEntry";
		String journalClassName = "JournalArticle";
		String objectClass="";

		if(this.type.contains(journalClassName)) {
			//System.out.println("class name: " + objectClass);
			System.out.println("Journal!");
			//objectClass = journalClassName;

			//trying to find parent page URL. from https://stackoverflow.com/questions/8397679/get-portlet-page-containing-web-content-in-liferay
			long groupId = getGroupId();
			
			System.out.println(groupId);
			
			ThemeDisplay themeDisplay = getThemeDisplay(request);

			long articleId = this.getId();

			
			List<Long> layoutIds = JournalContentSearchLocalServiceUtil.getLayoutIds(groupId, false, Long.toString(articleId));
			
			if (!layoutIds.isEmpty()) {
				  long layoutId = layoutIds.get(0).longValue();
				  Layout layout = LayoutLocalServiceUtil.getLayout(groupId, false, layoutId);
				  String url = PortalUtil.getLayoutURL(layout, themeDisplay);
				  //String url = PortalUtil.getLayoutFriendlyURL(layout, themeDisplay);
				  System.out.println("url: " + url);
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
	
	private long setGroupId(RenderRequest req) {
		ThemeDisplay themeDisplay = getThemeDisplay(req);
		long portletGroupId = themeDisplay.getScopeGroupId();
		
		return portletGroupId;
	}
	
	private ThemeDisplay getThemeDisplay(RenderRequest req) {
		return (ThemeDisplay) req.getAttribute(WebKeys.THEME_DISPLAY);
	}
}
