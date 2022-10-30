package com.vpapps.items;

public class ItemWallpaper {

	private String id, CId, CName, image, imageThumb, wallColors, totalViews, totalRate, averageRate, totalDownloads, tags, type, resolution="", size="", userRating = "0";
	private boolean isFav = false;

	public ItemWallpaper(String id, String cId, String cName, String image, String imageThumb, String wallColors, String totalViews, String totalRate, String averageRate, String totalDownloads, String tags, String type, boolean isFav) {
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
