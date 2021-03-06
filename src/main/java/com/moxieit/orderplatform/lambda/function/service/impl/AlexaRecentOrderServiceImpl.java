package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.util.Date;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.AlexaDTO;
import com.moxieit.orderplatform.function.service.api.AlexaService;

import com.moxieit.orderplatform.lambda.response.AlexaResponse;
import com.moxieit.orderplatform.lambda.response.BaseResponse;
import com.moxieit.orderplatform.lambda.response.Card;
import com.moxieit.orderplatform.lambda.response.OutputSpeech;
import com.moxieit.orderplatform.lambda.response.Reprompt;
import com.moxieit.orderplatform.lambda.response.Response;



public class AlexaRecentOrderServiceImpl implements AlexaService {
	String orderuuid = "";
	String itemName= "";
	String quantity= "";
	String itemCost= "";
	String categoryId= "";
	String itemId= "";
	boolean isSpicy;
	String totalBillWithTax="";
	@Override
	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context) {
		try{
		String deviceId = alexaDTO.getDeviceId();
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderTable = dynamoDB.getTable("Order");			 
		StringBuilder BODY = new StringBuilder();	
		List<Date> dates = new ArrayList<Date>();
		Calendar calendar = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		ScanExpressionSpec xspec1 = new ExpressionSpecBuilder().withCondition(S("deviceId").eq(deviceId)
				.and(S("orderFrom").eq("Alexa")))
				.buildForScan();
		
		ItemCollection<ScanOutcome> scan1 = orderTable.scan(xspec1);

		Consumer<Item> action1 = new Consumer<Item>() {
			public void accept(Item t1) {
				Table orderTable = dynamoDB.getTable("Order");
				Object order1 = t1.getString("uuid");
				Item itemuuid = orderTable.getItem("uuid", order1);
				String orderuuid = itemuuid.getString("uuid");				
				
			System.out.println("order with phone number:"+orderuuid);		
			Number creationDate = t1.getNumber("creationDate");
			String x = creationDate.toString();				
			long milliSeconds = Long.parseLong(x);					
			calendar.setTimeInMillis(milliSeconds);
			//System.out.println("time"+formatter1.format(calendar.getTime()));			
			Date recentDate = null;
			try {
				//recentDate = (Date) formatter1.parse(orderDate);
				recentDate = (Date) formatter1.parse(formatter1.format(calendar.getTime()));
				System.out.println("orderDate is:"+recentDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			dates.add(recentDate);

			}

		};
		scan1.forEach(action1);
		Date latest = Collections.max(dates);
		String latestDate = formatter.format(latest);
		System.out.println("latest date :"+latestDate);		
		Table orderItemsTable = dynamoDB.getTable("OrderItems");
		ScanExpressionSpec xspec3 = new ExpressionSpecBuilder().withCondition(S("deviceId").eq(deviceId)
				.and(S("orderFrom").eq("Alexa")).and(S("orderDate").eq(latestDate)))
				.buildForScan();
		ItemCollection<ScanOutcome> scan3 = orderTable.scan(xspec3);

		Consumer<Item> action3 = new Consumer<Item>() {
			@Override
			public void accept(Item t2) {
				Object order1 = t2.getString("uuid");
				Item itemuuid = orderTable.getItem("uuid", order1);
				String orderuuid1 = itemuuid.getString("uuid");				
				orderuuid = orderuuid1;		
						

			}

		};
		scan3.forEach(action3);	
		ScanExpressionSpec xspec4 = new ExpressionSpecBuilder().withCondition(S("orderuuid").eq(orderuuid))
				.buildForScan();
		ItemCollection<ScanOutcome> scan4 = orderItemsTable.scan(xspec4);

		Consumer<Item> action4 = new Consumer<Item>() {
			@Override
			public void accept(Item t3) {
				
				Table menuItemTable = dynamoDB.getTable("Menu_Items");
				Object categoryObject = t3.getString("categoryId");
				Object itemObject = t3.getString("menuItemId");
				Item menuItem = menuItemTable.getItem("categoryId", categoryObject, "itemId", itemObject);						
				itemName = menuItem.getString("itemName");
				quantity = t3.getString("quantity");
				itemCost = t3.getString("itemCost");
				categoryId = t3.getString("categoryId");
				itemId = t3.getString("menuItemId");
				isSpicy = t3.getBoolean("isSpicy");
				
			BODY.append("Your Recent order is ").append(menuItem.getString("itemName"))
			.append(",Quantity ")
			.append(t3.getString("quantity")).append(",Total cost is ")
			.append(t3.getString("itemCost")).append(" Dollars");			


			}

		};
		scan4.forEach(action4);
		System.out.println(BODY);
		String itemsbody = BODY.toString();
		
		String uuid = UUID.randomUUID().toString();
		String orderuuid = UUID.randomUUID().toString();
		Item orderItem = new Item();
		orderItem.withString("uuid", uuid).withString("orderuuid", orderuuid).withString("itemName", itemName)
		.withNumber("creationDate", System.currentTimeMillis()).withString("categoryId", categoryId).withString("menuItemId", itemId)	
		.withString("userId", alexaDTO.getUserId()).withString("itemCost", itemCost).withBoolean("isSpicy", isSpicy)
		.withString("itemQuantityAdd", "true");
		orderItemsTable.putItem(orderItem);
		System.out.println(orderItem);
		Calendar calendar1 = Calendar.getInstance();
		String date = formatter.format(calendar1.getTime());
		
		Item order = new Item();
		order.withString("uuid", orderuuid).withString("userId", alexaDTO.getUserId()).withString("orderStatus", "Initiated")
				.withNumber("creationDate", System.currentTimeMillis()).withNumber("totalBill", 0)
				.withNumber("tax", 0).withNumber("totalBillWithTax", 0).withString("orderTracking", "ACCEPTED")
				.withString("orderDate", date).withString("returnMessage", "true")
				.withString("paymentDone", "false").withString("orderFrom","Alexa")
				.withString("restaurantId","1").withString("botName", "Test");
			
		orderTable.putItem(order);
		System.out.println(order);
		AlexaResponse alexaResponse = new AlexaResponse();
		Response response = new Response();
		OutputSpeech outputSpeech = new OutputSpeech();
		outputSpeech.setSsml("<speak>Your Recent order is " + itemsbody+",to confirm order speak Confirm, if not tell your item name or speak menu.</speak>");
		outputSpeech.setType("SSML");
		Card card = new Card();
		card.setTitle("Confirm your Recent order");
		card.setType("Simple");
		card.setContent("Your Recent order is " +itemsbody+",to confirm order speak Confirm or tell your item name or speak menu.");
		Reprompt reprompt = new Reprompt();
		OutputSpeech outputSpeech1 = new OutputSpeech();
		outputSpeech1.setText("Please speak CheckOut to confirm order or tell your item name or speak menu.");
		outputSpeech1.setType("PlainText");
		reprompt.setOutputSpeech(outputSpeech1);
		response.setReprompt(reprompt);
		response.setShouldEndSession(false);
		
		response.setCard(card);
		response.setOutputSpeech(outputSpeech);
		alexaResponse.setVersion("1.0");
		alexaResponse.setResponse(response);
		return alexaResponse;
		} catch (Exception e) {
			System.out.println("Don't have Recent orders from this device");
			AlexaResponse alexaResponse = new AlexaResponse();
			Response response = new Response();
			OutputSpeech outputSpeech = new OutputSpeech();
			outputSpeech.setSsml("<speak>Sorry, you don't have any recent orders from this device. Please speak New order.</speak>");
			outputSpeech.setType("SSML");
			Card card = new Card();
			card.setTitle("No Recent order");
			card.setType("Simple");
			card.setContent("Sorry, you don't have any recent orders from this device. Please speak New order.");
			Reprompt reprompt = new Reprompt();
			OutputSpeech outputSpeech1 = new OutputSpeech();
			outputSpeech1.setText("Please speak new order or menu.");
			outputSpeech1.setType("PlainText");
			reprompt.setOutputSpeech(outputSpeech1);
			response.setShouldEndSession(false);
			response.setReprompt(reprompt);
			response.setCard(card);
			response.setOutputSpeech(outputSpeech);
			alexaResponse.setVersion("1.0");
			alexaResponse.setResponse(response);
			return alexaResponse;
		}

	}
	public static void main(String[] args) {
		AlexaRecentOrderServiceImpl FbLoginServiveImpl = new AlexaRecentOrderServiceImpl();
		Context context = null;
		AlexaDTO alexaDTO= new AlexaDTO();
		alexaDTO.setRequest("9493689846");
		alexaDTO.setDeviceId("123456");
		alexaDTO.setUserId("AFP6QTFEMSGJB6ATYY3OU6U6X7Y7QQCDVPZWBB7VTH2UOAOMKUULVGJHILIS2FBEBPFST4HJPOW4PMJY3BWWVTUJZKEOQ6JJOKME5O2ILIT2X5P7KHMDDKQVRQLN22BW72QCRWU72ILHFE5GJXYAKBCCWFO7LUN3XVITM3NLNGOD6MNTMCIP5II5BX2XTD6XPQBJEGAEDGESQ3I");
		FbLoginServiveImpl.serveLex(alexaDTO, context);
		}
}
