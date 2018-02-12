package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.GoogleDTO;
import com.moxieit.orderplatform.function.service.api.GoogleService;
import com.moxieit.orderplatform.lambda.response.BaseResponse;
import com.moxieit.orderplatform.lambda.response.GoogleResponse;
import com.moxieit.orderplatform.lambda.response.MenuCategoryResponse;
import com.moxieit.orderplatform.lambda.response.MenuItemResponse;

public class GoogleCategoriesServiceImpl implements GoogleService{
	String menuCategoryId1 ;
	@Override
	public BaseResponse serveLex(GoogleDTO googleDTO, Context context) {
		// TODO Auto-generated method stub
		StringBuilder menuItem = new StringBuilder();
		String restaurantId = "1";		
		
		MenuCategoryResponse menuCategoryResponse1 = new MenuCategoryResponse();
		RestaurantMenuImpl restaurantMenuImpl = new RestaurantMenuImpl();
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table menuItemTable = dynamoDB.getTable("Menu_Items");
		Table menuCategoriesTable = dynamoDB.getTable("Menu_Categories");
		List<MenuCategoryResponse> menuItemResponse = restaurantMenuImpl.getMenuCategories(restaurantId);
		Map<String, MenuCategoryResponse> mapCategory = new HashMap<>();
		for (MenuCategoryResponse menuCategoryResponse : menuItemResponse) {
			mapCategory.put(menuCategoryResponse.getCategoryId(), menuCategoryResponse);
		}
		Map<String, List<MenuItemResponse>> menuItems = new HashMap<>();
		
		HashMap<String, Object> valueMap1 = new HashMap<String, Object>();
		valueMap1.put(":v_id", restaurantId);
		valueMap1.put(":letter1", googleDTO.getIntentName());
		
		ScanExpressionSpec xspec1 = new ExpressionSpecBuilder().withCondition(S("restaurantId").eq(restaurantId)
				.and(S("categoryName").beginsWith(googleDTO.getIntentName())))
				.buildForScan();
		
		ItemCollection<ScanOutcome> scan1 = menuCategoriesTable.scan(xspec1);		
		Consumer<Item> action1 = new Consumer<Item>() {
			@Override
			public void accept(Item t1) {		
		
			menuCategoryId1 = t1.getString("categoryId");
			
		}
		};
		scan1.forEach(action1);
			HashMap<String, Object> valueMap = new HashMap<String, Object>();
			valueMap.put(":v_id", restaurantId);
			valueMap.put(":letter1", menuCategoryId1);

			ScanExpressionSpec xspec = new ExpressionSpecBuilder()
					.withCondition(S("itemId").beginsWith(menuCategoryId1)).buildForScan();

			ItemCollection<ScanOutcome> scan = menuItemTable.scan(xspec);

			menuCategoryResponse1.setMenuItems(menuItems);
			Consumer<Item> action = new Consumer<Item>() {
				@Override
				public void accept(Item t) {
					String menuItemId = t.getString("itemId");
					MenuItemResponse menuItemResponse = restaurantMenuImpl.getMenuItem(menuItemId);				
					int itemcost = menuItemResponse.getPrice().intValue();					
					menuItem.append(menuItemResponse.getItemName()).append(" cost ").append(itemcost).append(" dollars, ");				
					
				}
			};
			scan.forEach(action);
			GoogleResponse googleResponse = new GoogleResponse();
			googleResponse.setSpeech("We have "+menuItem+" What would you like.");
			return googleResponse;
	}
	

	
}
