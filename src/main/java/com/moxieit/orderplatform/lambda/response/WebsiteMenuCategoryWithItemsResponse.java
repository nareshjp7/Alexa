package com.moxieit.orderplatform.lambda.response;

import java.util.List;
import java.util.Map;

public class WebsiteMenuCategoryWithItemsResponse {
	List<Map<String, List<MenuItemResponse>>> menuCategoryItems;

	public List<Map<String, List<MenuItemResponse>>> getMenuCategoryItems() {
		return menuCategoryItems;
	}

	public void setMenuCategoryItems(List<Map<String, List<MenuItemResponse>>> menuCategoryItems) {
		this.menuCategoryItems = menuCategoryItems;
	}

}
