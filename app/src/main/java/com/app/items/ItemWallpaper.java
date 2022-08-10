package com.app.items;

public class ItemWallpaper {

	private String id, CId, CName, image, imageThumb, wallColors, totalViews, totalRate, averageRate, totalDownloads, tags, wallUrl,app1Url, app2Name, app2Url, app3Name, app3Url,app4Name,app4Url,app5Name,app5Url,type, resolution="", size="", userRating = "0";
	private boolean isFav = false;

	public ItemWallpaper(String id, String cId, String cName, String image, String imageThumb, String wallColors, String totalViews, String totalRate, String averageRate, String totalDownloads, String tags, String wallUrl,String app1Url, String app2Name, String app2Url, String app3Name, String app3Url,String app4Name,String app4Url,String app5Name,String app5Url,String type, boolean isFav) {
		this.id = id;
		this.CId = cId;
		this.CName = cName;
		this.image = image;
		this.imageThumb = imageThumb;
		this.wallColors = wallColors;
		this.totalViews = totalViews;
		this.totalRate = totalRate;
		this.averageRate = averageRate;
		this.totalDownloads = totalDownloads;
		this.tags = tags;
		this.type = type;
		this.isFav = isFav;
		this.wallUrl=wallUrl;
		this.app1Url = app1Url;
		this.app2Name = app2Name;
		this.app2Url = app2Url;
		this.app3Name = app3Name;
		this.app3Url = app3Url;
		this.app4Name = app4Name;
		this.app4Url = app4Url;
		this.app5Name = app5Name;
		this.app5Url = app5Url;
	}

	public String getId() {
		return id;
	}

	public String getCId() {
		return CId;
	}

	public String getCName() {
		return CName;
	}

	public String getImage() {
		return image;
	}

	public String getImageThumb() {
		return imageThumb;
	}

	public String getWallColors() {
		return wallColors;
	}

	public String getTotalViews() {
		return totalViews;
	}

	public String getTotalRate() {
		return totalRate;
	}

	public String getAverageRate() {
		return averageRate;
	}

	public void setTotalViews(String totalViews) {
		this.totalViews = totalViews;
	}

	public void setTotalRate(String totalRate) {
		this.totalRate = totalRate;
	}

	public void setAverageRate(String averageRate) {
		this.averageRate = averageRate;
	}

	public String getTotalDownloads() {
		return totalDownloads;
	}

	public void setTotalDownloads(String totalDownloads) {
		this.totalDownloads = totalDownloads;
	}

	public String getTags() {
		return tags;
	}

	public String getType() {
		return type;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getWallUrl() {
		return wallUrl;
	}

	public String getApp1Url() {
		return app1Url;
	}

	public void setApp1Url(String app1Url) {
		this.app1Url = app1Url;
	}

	public String getApp2Name() {
		return app2Name;
	}

	public void setApp2Name(String app2Name) {
		this.app2Name = app2Name;
	}

	public String getApp2Url() {
		return app2Url;
	}

	public void setApp2Url(String app2Url) {
		this.app2Url = app2Url;
	}

	public String getApp3Name() {
		return app3Name;
	}

	public void setApp3Name(String app3Name) {
		this.app3Name = app3Name;
	}

	public String getApp3Url() {
		return app3Url;
	}

	public void setApp3Url(String app3Url) {
		this.app3Url = app3Url;
	}

	public String getApp4Name() {
		return app4Name;
	}

	public void setApp4Name(String app4Name) {
		this.app4Name = app4Name;
	}

	public String getApp4Url() {
		return app4Url;
	}

	public void setApp4Url(String app4Url) {
		this.app4Url = app4Url;
	}

	public String getApp5Name() {
		return app5Name;
	}

	public void setApp5Name(String app5Name) {
		this.app5Name = app5Name;
	}

	public String getApp5Url() {
		return app5Url;
	}

	public void setApp5Url(String app5Url) {
		this.app5Url = app5Url;
	}

	public String getUserRating() {
		return userRating;
	}

	public void setUserRating(String userRating) {
		this.userRating = userRating;
	}

	public boolean getIsFav() {
		return isFav;
	}

	public void setIsFav(boolean fav) {
		isFav = fav;
	}
}
