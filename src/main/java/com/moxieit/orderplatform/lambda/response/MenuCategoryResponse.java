package com.moxieit.orderplatform.lambda.response;

import java.util.List;
import java.util.Map;

public class MenuCategoryResponse {

	private String categoryId;
	private String categoryName;
	private String image;
	private String message;
	private String status;
	private String restaurantId;
	Map<String, List<MenuItemResponse>> menuItems;

	public MenuCategoryResponse() {
		// TODO Auto-generated constructor stub
	}

	public MenuCategoryResponse(String restaurantId, String categoryId, String categoryName) {
		this.restaurantId = restaurantId;
		this.categoryId = categoryId;
		this.categoryName = categoryName;
	}

	public MenuCategoryResponse(String message, String status) {
		super();
		this.message = message;
		this.status = status;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Map<String, List<MenuItemResponse>> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(Map<String, List<MenuItemResponse>> menuItems) {
		this.menuItems = menuItems;
	}

}
