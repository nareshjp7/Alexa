package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.lang3.text.WordUtils;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.Page;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.AlexaDTO;
import com.moxieit.orderplatform.lambda.response.AlexaResponse;
import com.moxieit.orderplatform.lambda.response.BaseResponse;
import com.moxieit.orderplatform.lambda.response.Card;
import com.moxieit.orderplatform.lambda.response.OutputSpeech;
import com.moxieit.orderplatform.lambda.response.Reprompt;
import com.moxieit.orderplatform.lambda.response.Response;

public class AlexaMenuItemServiceImpl extends AbstractAlexaOrderServiceImpl {

	@Override
	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context) {
		DynamoDB dynamoDB = DBService.getDBConnection();
		String botName = alexaDTO.getBotName();
		String restaurantId = alexaDTO.getRestaurantId();
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		Table menuItemsTable = dynamoDB.getTable("Menu_Items");
		String itemPrice=null;
		String categoryId=null;
		String itemId=null;
		String itemName=null;
		Boolean isSpicy=null;	
		ArrayList<String> obj = new ArrayList<String>();
		String MenuItemName = WordUtils.capitalize(alexaDTO.getRequest());
		System.out.println("my request of item name: "+MenuItemName);
			
		try {
			HashMap<String, Object> valueMap = new HashMap<String, Object>();
			valueMap.put(":v_id", restaurantId);
			valueMap.put(":letter1", restaurantId);
			AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
			
		
				
			ScanRequest scanRequest = new ScanRequest()
			    .withTableName("Menu_Items").withAttributesToGet("itemName","price","categoryId","itemId","isSpicy");
			
			ScanResult result = client.scan(scanRequest);
		
			for (Map<String, AttributeValue> item : result.getItems()){
				
				
				String x = item.get("itemName").getS();
				String categoryid = item.get("categoryId").getS();
								
				 if(x.replaceAll("\\s+","").equalsIgnoreCase(MenuItemName.replaceAll("\\s+","")) ||
						 MenuItemName.toLowerCase().replaceAll("\\s+","").contains(x.replaceAll("\\s+","").toLowerCase())){
					 System.out.println("my categoryid: "+categoryid);
					 if(categoryid.startsWith(restaurantId)){
						 						
						 obj.add(item.get("itemName").getS().replaceAll("\\s+",""));
					
					// itemName = item.get("itemName").getS().replaceAll("\\s+","");				
						itemPrice=(String) item.get("price").getN();					
						categoryId=(String) item.get("categoryId").getS();					
						itemId=(String) item.get("itemId").getS();						
						isSpicy= (Boolean) item.get("isSpicy").getBOOL();
								
						//break;
					 }
				 }
				
				 
			}
			  String longestString = getLongestString(obj);
			  itemName = longestString;
		
		Item order = getOrder(alexaDTO.getUserId());
		String orderuuid = order.getString("uuid");
		System.out.println("1st order: "+order);
		String uuid = UUID.randomUUID().toString();
		Item orderItem = new Item();
		orderItem.withString("uuid", uuid).withString("orderuuid", orderuuid).withString("itemName", itemName)
		.withNumber("creationDate", System.currentTimeMillis()).withString("categoryId", categoryId).withString("menuItemId", itemId)	
		.withString("userId", alexaDTO.getUserId()).withNumber("quantity", 0).withString("itemCost", itemPrice).withBoolean("isSpicy", isSpicy)
		.withString("itemQuantityAdd", "false");
		orderItemTable.putItem(orderItem);
		AlexaResponse alexaResponse = new AlexaResponse();
		Response response = new Response();
		OutputSpeech outputSpeech = new OutputSpeech();
		outputSpeech.setSsml("<speak>How much quantity do you want for this order.</speak>");
		outputSpeech.setType("SSML");
		Card card = new Card();
		card.setTitle("Add Quantity");
		card.setType("Simple");
		card.setContent("How much quantity do you want for this order.");
		Reprompt reprompt = new Reprompt();
		OutputSpeech outputSpeech1 = new OutputSpeech();
		outputSpeech1.setText("Please provide how much quantity do you want for this order.");
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
		catch (Exception e)
		{
			
			StringBuilder BODY = new StringBuilder();
			ScanExpressionSpec xspec4 = new ExpressionSpecBuilder().withCondition(S("itemName").contains(MenuItemName))
					.buildForScan();
			ItemCollection<ScanOutcome> scan4 = menuItemsTable.scan(xspec4);

			Consumer<Item> action4 = new Consumer<Item>() {
				@Override
				public void accept(Item t3) {
					
					String item =(String) t3.get("itemName");						
				BODY.append(item).append(", ");						

				}

			};
			scan4.forEach(action4);

			String itemsbody = BODY.toString();
			System.out.println(itemsbody);
			AlexaResponse alexaResponse = new AlexaResponse();
			Response response = new Response();
			OutputSpeech outputSpeech = new OutputSpeech();
			outputSpeech.setSsml("<speak>Sorry. I am not sure,please speck from the items "+itemsbody+" or speck menu.</speak>");
			outputSpeech.setType("SSML");
			Card card = new Card();
			card.setTitle("Select Item from Menu");
			card.setType("Simple");
			card.setContent("Sorry. I am not sure,please speck from the items "+itemsbody+" or speck menu.");
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

	
	}
	public static String getLongestString(ArrayList<String> array) {
	      int maxLength = 0;
	      String longestString = null;
	      for (String s : array) {
	          if (s.length() > maxLength) {
	              maxLength = s.length();
	              longestString = s;
	          }
	      }
	      return longestString;
	  }

public static void main(String[] args) {
		AlexaMenuItemServiceImpl alexaChikuItemServiceImpl=new AlexaMenuItemServiceImpl();
		AlexaDTO alexaDTO=new AlexaDTO();
		alexaDTO.setUserId("AFHFYXC47SSN4DSEXYTINPFRSEGCVEN3X5RX5BNA342NRCA73VZX4G43FCQF5TI7L5RFEJD6HHQ2VXPLUSQSSHRJSIBOBZIIFYUWUFOR7Z7PVLX27NJYJYURDFOMJHVWWYE2MPXASCBCNRQUNSJPOYQKAS7IBXAYDKIWIBUIKE3WISM6OD25DFXX344QDDPLOVIUGZCVU2Y5A3A");
		alexaDTO.setApplicationId("sample");
		alexaDTO.setRequest("paratha");
		Context context=null;
		alexaChikuItemServiceImpl.serveLex(alexaDTO, context);
	}
}
