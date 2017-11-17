package com.moxieit.orderplatform.lambda.request;

import java.util.List;

public class MainMenuRequest {

	List<MenuCategoriesRequest> menuCategoriesRequest;

	List<MenuItemsRequest> menuItemsRequest;

	List<String> categoryIds;

	private String type;

	private String botName;

	public List<String> getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(List<String> categoryIds) {
		this.categoryIds = categoryIds;
	}

	public String getBotName() {
		return botName;
	}

	public void setBotName(String botName) {
		this.botName = botName;
	}

	public List<MenuItemsRequest> getMenuItemsRequest() {
		return menuItemsRequest;
	}

	public void setMenuItemsRequest(List<MenuItemsRequest> menuItemsRequest) {
		this.menuItemsRequest = menuItemsRequest;
	}

	public List<MenuCategoriesRequest> getMenuCategoriesRequest() {
		return menuCategoriesRequest;
	}

	public void setMenuCategoriesRequest(List<MenuCategoriesRequest> menuCategoriesRequest) {
		this.menuCategoriesRequest = menuCategoriesRequest;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
