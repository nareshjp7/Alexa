package com.moxieit.orderplatform.lambda.function.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.IRestaurantMenu;
import com.moxieit.orderplatform.lambda.response.MainMenuCategoryResponse;
import com.moxieit.orderplatform.lambda.response.MenuCategoryResponse;
import com.moxieit.orderplatform.lambda.response.MenuItemResponse;
import com.moxieit.orderplatform.lambda.response.WebsiteMenuCategoryWithItemsResponse;

public class RestaurantMenuImpl implements IRestaurantMenu {
	String monToFri = "";
	String satToSun = "";
	@Override
	public List<MenuCategoryResponse> getMenuCategories(String restaurantId) {
		List<MenuCategoryResponse> menuCategoryResponses = new ArrayList<MenuCategoryResponse>();
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table table = dynamoDB.getTable("Menu_Categories");
		HashMap<String, Object> valueMap = new HashMap<String, Object>();
		valueMap.put(":v_id", restaurantId);
		valueMap.put(":letter1", restaurantId + "_");
		QuerySpec spec = new QuerySpec()
				.withKeyConditionExpression("restaurantId = :v_id and begins_with(categoryId,:letter1)")
				.withValueMap(valueMap);

		ItemCollection<QueryOutcome> items = table.query(spec);

		Iterator<Item> iterator = items.iterator();
		while (iterator.hasNext()) {
			// System.out.println(iterator.next().toJSONPretty());
			Item item = iterator.next();
			MenuCategoryResponse menuCategoryResponse = new MenuCategoryResponse();
			menuCategoryResponse.setCategoryId(item.getString("categoryId"));
			menuCategoryResponse.setCategoryName(item.getString("categoryName"));
			menuCategoryResponse.setImage(item.getString("image"));
			menuCategoryResponses.add(menuCategoryResponse);
		}
		return menuCategoryResponses;

	}

	@Override
	public List<MenuItemResponse> getMenuItems(String restaurantIdwithCategoryId) {
		List<MenuItemResponse> menuItemResponses = new ArrayList<MenuItemResponse>();
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table table = dynamoDB.getTable("Menu_Items");
		HashMap<String, Object> valueMap = new HashMap<String, Object>();
		valueMap.put(":v_id", restaurantIdwithCategoryId);
		valueMap.put(":letter1", restaurantIdwithCategoryId + "_");
		QuerySpec spec = new QuerySpec()
				.withKeyConditionExpression("categoryId = :v_id and begins_with(itemId,:letter1)")
				.withValueMap(valueMap);

		ItemCollection<QueryOutcome> items = table.query(spec);

		Iterator<Item> iterator = items.iterator();
		while (iterator.hasNext()) {
			Item item = iterator.next();
			MenuItemResponse menuItemResponse = new MenuItemResponse();
			menuItemResponse.setItemId(item.getString("itemId"));
			menuItemResponse.setItemName(item.getString("itemName"));
			menuItemResponse.setImage(item.getString("image"));
			menuItemResponse.setPrice(item.getNumber("price"));
			menuItemResponse.setItemType(item.getString("itemType"));
			menuItemResponse.setIsSpicy(item.getString("isSpicy"));
			menuItemResponses.add(menuItemResponse);
		}
		return menuItemResponses;

	}

	@Override
	public MenuItemResponse getMenuItem(String restaurantIdWithCategoryIdWithMenuItemId) {
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table table = dynamoDB.getTable("Menu_Items");
		HashMap<String, Object> valueMap = new HashMap<String, Object>();
		String split[] = restaurantIdWithCategoryIdWithMenuItemId.split("_");
		valueMap.put(":v_id", split[0] + "_" + split[1]);
		valueMap.put(":letter1", restaurantIdWithCategoryIdWithMenuItemId);
		QuerySpec spec = new QuerySpec().withKeyConditionExpression("categoryId = :v_id and itemId = :letter1")
				.withValueMap(valueMap);

		ItemCollection<QueryOutcome> items = table.query(spec);
		Iterator<Item> iterator = items.iterator();
		while (iterator.hasNext()) {
			Item item = iterator.next();
			MenuItemResponse menuItemResponse = new MenuItemResponse();
			menuItemResponse.setItemId(item.getString("itemId"));
			menuItemResponse.setItemName(item.getString("itemName"));
			menuItemResponse.setImage(item.getString("image"));
			menuItemResponse.setIsSpicy(item.getString("isSpicy"));
			menuItemResponse.setPrice(item.getNumber("price"));
			menuItemResponse.setItemType(item.getString("itemType"));
			return menuItemResponse;
		}
		return null;

	}

	@Override
	public WebsiteMenuCategoryWithItemsResponse getMenuItemsWithRestaurantId(String restaurantId) {
		WebsiteMenuCategoryWithItemsResponse websiteMenuCategoryWithItemsResponse = new WebsiteMenuCategoryWithItemsResponse();
		List<Map<String, List<MenuItemResponse>>> menuCategoryItems = new ArrayList<>();
		MenuCategoryResponse menuCategoryResponse1 = new MenuCategoryResponse();
		RestaurantMenuImpl restaurantMenuImpl = new RestaurantMenuImpl();
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table menuItemTable = dynamoDB.getTable("Menu_Items");
		Table restaurantTable = dynamoDB.getTable("Restaurant");
		ScanExpressionSpec xspec4 = new ExpressionSpecBuilder().withCondition(S("id").eq(restaurantId))
				.buildForScan();
		ItemCollection<ScanOutcome> scan4 = restaurantTable.scan(xspec4);

		Consumer<Item> action4 = new Consumer<Item>() {
			@Override
			public void accept(Item t3) {				
			 monToFri = t3.getString("monToFri");	
				satToSun = t3.getString("satToSun");	
				
			}

		};
		scan4.forEach(action4);
		
		List<MenuCategoryResponse> menuItemResponse = restaurantMenuImpl.getMenuCategories(restaurantId);
		Map<String, MenuCategoryResponse> mapCategory = new HashMap<>();
		for (MenuCategoryResponse menuCategoryResponse : menuItemResponse) {
			mapCategory.put(menuCategoryResponse.getCategoryId(), menuCategoryResponse);
		}
		Map<String, List<MenuItemResponse>> menuItems = new HashMap<>();
		for (MenuCategoryResponse menuCategoryId : menuItemResponse) {
			HashMap<String, Object> valueMap = new HashMap<String, Object>();
			valueMap.put(":v_id", restaurantId);
			valueMap.put(":letter1", menuCategoryId.getCategoryId());

			ScanExpressionSpec xspec = new ExpressionSpecBuilder()
					.withCondition(S("itemId").beginsWith(menuCategoryId.getCategoryId())).buildForScan();

			ItemCollection<ScanOutcome> scan = menuItemTable.scan(xspec);

			menuCategoryResponse1.setMenuItems(menuItems);
			Consumer<Item> action = new Consumer<Item>() {
				@Override
				public void accept(Item t) {
					String menuItemId = t.getString("itemId");
					MenuItemResponse menuItemResponse = restaurantMenuImpl.getMenuItem(menuItemId);
					String[] menuItemSplit = menuItemId.split("_");
					MenuCategoryResponse menuCategoryResponse = mapCategory
							.get(menuItemSplit[0] + "_" + menuItemSplit[1]);

					MenuItemResponse menuItemResponse1 = new MenuItemResponse();
					menuItemResponse1.setItemId(menuItemId);
					menuItemResponse1.setIsSpicy(menuItemResponse.getIsSpicy());
					menuItemResponse1.setItemName(menuItemResponse.getItemName());
					menuItemResponse1.setImage(menuItemResponse.getImage());
					menuItemResponse1.setItemType(menuItemResponse.getItemType());
					menuItemResponse1.setPrice(menuItemResponse.getPrice());
					menuItemResponse1.setMonToFri(monToFri);
					menuItemResponse1.setSatToSun(satToSun);
					List<MenuItemResponse> list = menuItems.get(menuCategoryResponse.getCategoryName());
					if (list == null) {
						list = new ArrayList<>();
					}
					list.add(menuItemResponse1);
					menuItems.put(menuCategoryResponse.getCategoryName(), list);
				}
			};
			scan.forEach(action);

			menuCategoryResponse1.setMenuItems(menuItems);
			
			System.out.println(menuCategoryItems);
		}
		menuCategoryItems.add(menuItems);
		websiteMenuCategoryWithItemsResponse.setMenuCategoryItems(menuCategoryItems);
		System.out.println(menuCategoryItems);
		return websiteMenuCategoryWithItemsResponse;
	}

	/*public static void main(String[] args) {
		RestaurantMenuImpl restaurantMenuImpl = new RestaurantMenuImpl();
		restaurantMenuImpl.getMenuItemsWithRestaurantId("6");
	}*/

}
