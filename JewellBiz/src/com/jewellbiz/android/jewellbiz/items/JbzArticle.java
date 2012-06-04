package com.jewellbiz.android.jewellbiz.items;

public class JbzArticle {
	
	private String id;
	
	private String articleId;
	
	private String articleTitle;
	
	private String articleUrl;
	
	private String articleDate;
	
	private int articleDateInt;
	
	private String articleDesc;
	
	private int status;
	
	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getJbzArticleId() {
        return articleId;
    }

    public void setJbzArticleId(String articleId) {
        this.articleId = articleId;
    }
    
    public String getJbzArticleName() {
        return articleTitle;
    }

    public void setJbzArticleName(String articleTitle) {
        this.articleTitle = articleTitle;
    }
    
    public String getJbzArticleUrl() {
    	return articleUrl;
    }
    
    public void setJbzArticleUrl(String articleUrl) {
    	this.articleUrl = articleUrl;
    }
    
    public String getJbzArticleDate() {
    	return articleDate;
    }
    
    public void setJbzArticleDate(String articleDate) {
    	this.articleDate = articleDate;
    }
    
    public String getOverview() {
        return articleDesc;
    }

    public void setJbzArticleDescription(String articleDesc) {
        this.articleDesc = articleDesc;
    }
    
    public int getJbzArticleDateInt() {
    	return articleDateInt;
    }

	public void setJbzArticleDateInt(int articleDateInt) {
		this.articleDateInt = articleDateInt;
		
	}

}
