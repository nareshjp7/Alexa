package com.moxieit.orderplatform.lambda.function.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;
import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.AlexaDTO;
import com.moxieit.orderplatform.function.service.api.AlexaService;
import com.moxieit.orderplatform.lambda.response.AlexaResponse;
import com.moxieit.orderplatform.lambda.response.BaseResponse;
import com.moxieit.orderplatform.lambda.response.Card;
import com.moxieit.orderplatform.lambda.response.MenuCategoryResponse;
import com.moxieit.orderplatform.lambda.response.MenuItemResponse;
import com.moxieit.orderplatform.lambda.response.OutputSpeech;
import com.moxieit.orderplatform.lambda.response.Reprompt;
import com.moxieit.orderplatform.lambda.response.Response;
import com.moxieit.orderplatform.lambda.response.WebsiteMenuCategoryWithItemsResponse;

public class AlexaBiryaniServiceImpl implements AlexaService {
	String menuCategoryId1 ;
	@Override
	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context) {
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
		valueMap1.put(":letter1", alexaDTO.getIntentName());
		
		ScanExpressionSpec xspec1 = new ExpressionSpecBuilder().withCondition(S("restaurantId").eq(restaurantId)
				.and(S("categoryName").beginsWith(alexaDTO.getIntentName())))
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
					menuItem.append(menuItemResponse.getItemName()).append(" cost ").append(itemcost).append(" dollors, ");				
					
				}
			};
			scan.forEach(action);
					
		AlexaResponse alexaResponse = new AlexaResponse();
		Response response = new Response();
		OutputSpeech outputSpeech = new OutputSpeech();
		outputSpeech.setSsml(
				"<speak>We have "+menuItem+" What would you like.</speak>");
		outputSpeech.setType("SSML");
		Card card = new Card();
		card.setTitle("Audio");
		card.setType("Simple");
		card.setContent("We have "+menuItem+" What would you like.");
		Reprompt reprompt = new Reprompt();
		OutputSpeech outputSpeech1 = new OutputSpeech();
		outputSpeech1.setText("Please select your option");
		outputSpeech1.setType("PlainText");
		reprompt.setOutputSpeech(outputSpeech1);
		response.setReprompt(reprompt);
		response.setShouldEndSession(false);
		
		response.setCard(card);
		response.setOutputSpeech(outputSpeech);
		alexaResponse.setVersion("1.0");
		alexaResponse.setResponse(response);
		return alexaResponse;

	}
	/*public static void main(String[] args) {
		AlexaBiryaniServiceImpl alexaChikuItemServiceImpl=new AlexaBiryaniServiceImpl();
		AlexaDTO alexaDTO=new AlexaDTO();
		alexaDTO.setUserId("AFHFYXC47SSN4DSEXYTINPFRSEGCVEN3X5RX5BNA342NRCA73VZX4G43FCQF5TI7L5RFEJD6HHQ2VXPLUSQSSHRJSIBOBZIIFYUWUFOR7Z7PVLX27NJYJYURDFOMJHVWWYE2MPXASCBCNRQUNSJPOYQKAS7IBXAYDKIWIBUIKE3WISM6OD25DFXX344QDDPLOVIUGZCVU2Y5A3A");
		alexaDTO.setApplicationId("sample");
		alexaDTO.setIntentName("SideOrders");
		Context context=null;
		alexaChikuItemServiceImpl.serveLex(alexaDTO, context);
	}*/
}
