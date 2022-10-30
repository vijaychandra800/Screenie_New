package com.vpapps.items;

public class ItemColors {

	private String id, colorName, colorHex;

	public ItemColors(String id, String colorName, String colorHex) {
		this.id = id;
		this.colorName = colorName;
		this.colorHex = colorHex;
	}

	public String getId() {
		return id;
	}

	public String getColorName() {
		return colorName;
	}

	public String getColorHex() {
		return colorHex;
	}
}