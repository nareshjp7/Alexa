package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.text.ParseException;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.lang3.text.WordUtils;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.Page;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
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

public class AlexaChikuItemServiceImpl extends AbstractAlexaOrderServiceImpl {

	@Override
	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context) {
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		Table menuItemsTable = dynamoDB.getTable("Menu_Items");
		String itemPrice=null;
		String categoryId=null;
		String itemId=null;
		String itemName=null;
		Boolean isSpicy=null;		
		String MenuItemName = WordUtils.capitalize(alexaDTO.getRequest());
		
		try {
		ScanExpressionSpec xspec = new ExpressionSpecBuilder().withCondition(S("itemName").eq(MenuItemName))
				.buildForScan();

		ItemCollection<ScanOutcome> scan = menuItemsTable.scan(xspec);
		Item menuItem = null;
		Page<Item, ScanOutcome> firstPage = scan.firstPage();		
		if (firstPage.iterator().hasNext()) {
			menuItem = firstPage.iterator().next();
			itemPrice=(String) menuItem.get("price");
			categoryId=(String) menuItem.get("categoryId");
			itemId=(String) menuItem.get("itemId");
			itemName=(String) menuItem.get("itemName");
			isSpicy= (Boolean) menuItem.get("isSpicy");
			
		}
		Item order = getOrder(alexaDTO.getUserId());
		String orderuuid = order.getString("uuid");
		String uuid = UUID.randomUUID().toString();
		Item orderItem = new Item();
		orderItem.withString("uuid", uuid).withString("orderuuid", orderuuid).withString("itemName", itemName)
		.withNumber("creationDate", System.currentTimeMillis()).withString("categoryId", categoryId).withString("menuItemId", itemId)	
		.withString("userId", alexaDTO.getUserId()).withString("itemCost", itemPrice).withBoolean("isSpicy", isSpicy)
		.withString("itemQuantityAdd", "false");
		orderItemTable.putItem(orderItem);
		AlexaResponse alexaResponse = new AlexaResponse();
		Response response = new Response();
		OutputSpeech outputSpeech = new OutputSpeech();
		outputSpeech.setSsml("<speak>How much quantity do you want for this order.</speak>");
		outputSpeech.setType("SSML");
		Card card = new Card();
		card.setTitle("Audio");
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
			AlexaResponse alexaResponse = new AlexaResponse();
			Response response = new Response();
			OutputSpeech outputSpeech = new OutputSpeech();
			outputSpeech.setSsml("<speak>Sorry.I am not sure,please speck from the items "+itemsbody+" or speck menu.</speak>");
			outputSpeech.setType("SSML");
			Card card = new Card();
			card.setTitle("Audio");
			card.setType("Simple");
			card.setContent("Sorry.I am not sure,please speck from the items "+itemsbody+" or speck menu.");
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

/*public static void main(String[] args) {
		AlexaChikuItemServiceImpl alexaChikuItemServiceImpl=new AlexaChikuItemServiceImpl();
		AlexaDTO alexaDTO=new AlexaDTO();
		alexaDTO.setUserId("12345");
		alexaDTO.setApplicationId("sample");
		alexaDTO.setRequest("veg dum biryani");
		Context context=null;
		alexaChikuItemServiceImpl.serveLex(alexaDTO, context);
	}*/
}
