package com.moxieit.orderplatform.function.service.api;

import java.util.List;

import com.moxieit.orderplatform.lambda.response.MenuCategoryResponse;
import com.moxieit.orderplatform.lambda.response.MenuItemResponse;
import com.moxieit.orderplatform.lambda.response.WebsiteMenuCategoryWithItemsResponse;

public interface IRestaurantMenu {

	public List<MenuCategoryResponse> getMenuCategories(String restaurantId);

	public List<MenuItemResponse> getMenuItems(String restaurantIdWithCategoryId);

	public MenuItemResponse getMenuItem(String restaurantIdWithCategoryIdWithMenuItemId);

	WebsiteMenuCategoryWithItemsResponse getMenuItemsWithRestaurantId(String restaurantId);

}
