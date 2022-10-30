package com.app.items;

public class ItemGIF {

	private String id, image, views, totalRate, aveargeRate, totalDownload, tags, type, resolution="", size="", userRating = "0";
	private boolean isFav = false;

	public ItemGIF(String id, String image, String views, String totalRate, String aveargeRate, String totalDownload, String tags, boolean isFav) {
		this.id = id;
		this.image = image;
		this.views = views;
		this.tags = tags;
		this.totalRate = totalRate;
		this.aveargeRate = aveargeRate;
		this.totalDownload = totalDownload;
		this.isFav = isFav;
	}

	public String getId() {
		return id;
	}

	public String getImage() {
		return image;
	}

	public String getTotalViews() {
		return views;
	}

	public void setTotalViews(String views) {
		this.views = views;
	}

	public String getTags() {
		return tags;
	}

	public String getTotalRate() {
		return totalRate;
	}

	public String getAveargeRate() {
		return aveargeRate;
	}

	public void setAveargeRate(String aveargeRate) {
		this.aveargeRate = aveargeRate;
	}

	public String getTotalDownload() {
		return totalDownload;
	}

	public void setTotalDownload(String totalDownload) {
		this.totalDownload = totalDownload;
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