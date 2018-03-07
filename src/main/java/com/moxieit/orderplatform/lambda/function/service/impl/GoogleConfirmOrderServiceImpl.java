package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.Page;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.DB.DBService;

import com.moxieit.orderplatform.function.service.api.GoogleDTO;
import com.moxieit.orderplatform.function.service.api.GoogleService;
import com.moxieit.orderplatform.lambda.response.BaseResponse;
import com.moxieit.orderplatform.lambda.response.GoogleResponse;


public class GoogleConfirmOrderServiceImpl implements GoogleService{
	String orderuuid = "";
	String itemName= "";
	Number quantity= 0;
	Number itemCost= 0;
	String categoryId= "";
	String itemId= "";
	boolean isSpicy;
	String spiceyLevel = "";
	Number totalBillWithTax= 0;
	Number tax= 0;
	Number totalBill= 0;
	@Override
	public BaseResponse serveLex(GoogleDTO googleDTO, Context context) {
		// TODO Auto-generated method stub
		DynamoDB dynamoDB = DBService.getDBConnection();
	
		Table orderTable = dynamoDB.getTable("Order");
		String userId = googleDTO.getUserId();
		String botName = googleDTO.getBotName();
		String restaurantId = googleDTO.getRestaurantId();
	
		List<Date> dates = new ArrayList<Date>();
		Calendar calendar = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		ScanExpressionSpec xspec1 = new ExpressionSpecBuilder().withCondition(S("userId").eq(userId)
				.and(S("orderFrom").eq("GoogleHome")))
				.buildForScan();
		
		ItemCollection<ScanOutcome> scan1 = orderTable.scan(xspec1);

		Consumer<Item> action1 = new Consumer<Item>() {
			public void accept(Item t1) {
												
			Number creationDate = t1.getNumber("creationDate");
			String x = creationDate.toString();				
			long milliSeconds = Long.parseLong(x);					
			calendar.setTimeInMillis(milliSeconds);
			//System.out.println("time"+formatter1.format(calendar.getTime()));			
			Date recentDate = null;
			try {
				//recentDate = (Date) formatter1.parse(orderDate);
				recentDate = (Date) formatter1.parse(formatter1.format(calendar.getTime()));
				
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
		ScanExpressionSpec xspec3 = new ExpressionSpecBuilder().withCondition(S("userId").eq(userId)
				.and(S("orderFrom").eq("GoogleHome")).and(S("orderDate").eq(latestDate)))
				.buildForScan();
		ItemCollection<ScanOutcome> scan3 = orderTable.scan(xspec3);

		Consumer<Item> action3 = new Consumer<Item>() {
			@Override
			public void accept(Item t2) {
				Object order1 = t2.getString("uuid");
				Item itemuuid = orderTable.getItem("uuid", order1);
				String orderuuid1 = itemuuid.getString("uuid");	
				totalBillWithTax = itemuuid.getNumber("totalBillWithTax");	
				tax = itemuuid.getNumber("tax");	
				totalBill = itemuuid.getNumber("totalBill");	
				orderuuid = orderuuid1;		
						

			}

		};
		scan3.forEach(action3);	
		System.out.println(orderuuid+"orderuuid");
		String orderuuidnew = UUID.randomUUID().toString();
		ScanExpressionSpec xspec4 = new ExpressionSpecBuilder().withCondition(S("orderuuid").eq(orderuuid))
				.buildForScan();
		ItemCollection<ScanOutcome> scan4 = orderItemsTable.scan(xspec4);

		Consumer<Item> action4 = new Consumer<Item>() {
			@Override
			public void accept(Item t3) {
				System.out.println("H");
				Table menuItemTable = dynamoDB.getTable("Menu_Items");
				Object categoryObject = t3.getString("categoryId");
				Object itemObject = t3.getString("menuItemId");
				Item menuItem = menuItemTable.getItem("categoryId", categoryObject, "itemId", itemObject);						
				itemName = menuItem.getString("itemName");
				quantity = t3.getNumber("quantity");
				itemCost = t3.getNumber("itemCost");
				categoryId = t3.getString("categoryId");
				itemId = t3.getString("menuItemId");
				isSpicy = t3.getBoolean("isSpicy");
				spiceyLevel = t3.getString("spiceyLevel");
				
				
			String uuid = UUID.randomUUID().toString();
			
			Item orderItem = new Item();
			orderItem.withString("uuid", uuid).withString("orderuuid", orderuuidnew).withString("itemName", itemName)
			.withNumber("creationDate", System.currentTimeMillis()).withString("categoryId", categoryId).withString("menuItemId", itemId)	
			.withString("userId", googleDTO.getUserId()).withNumber("itemCost", itemCost).withBoolean("isSpicy", isSpicy)
			.withString("itemQuantityAdd", "true").withNumber("quantity", quantity);
			orderItemsTable.putItem(orderItem);
			System.out.println(orderItem);
			}

		};
		scan4.forEach(action4);
	
		

		Calendar calendar1 = Calendar.getInstance();
		String date = formatter.format(calendar1.getTime());
		
			
		Item order = new Item();
		order.withString("uuid", orderuuidnew).withString("userId", googleDTO.getUserId()).withString("orderStatus", "Initiated")
				.withNumber("creationDate", System.currentTimeMillis()).withNumber("totalBill",totalBill)
				.withNumber("tax", tax).withNumber("totalBillWithTax", totalBillWithTax).withString("orderTracking", "ACCEPTED")
				.withString("orderDate", date).withString("returnMessage", "true")
				.withString("paymentDone", "false").withString("orderFrom","GoogleHome")
				.withString("restaurantId",restaurantId).withString("botName", botName);
			
		orderTable.putItem(order);
		
				
			GoogleResponse googleResponse = new GoogleResponse();
			googleResponse.setSpeech("Thank you, Is this Order for a delivery or a Pickup.");
			System.out.println(googleResponse);
			return googleResponse;	
			

	}
	public static void main(String[] args) {
		GoogleConfirmOrderServiceImpl FbLoginServiveImpl = new GoogleConfirmOrderServiceImpl();
		Context context = null;
		GoogleDTO alexaDTO= new GoogleDTO();
		alexaDTO.setRequest("confirm");
		
		alexaDTO.setUserId("ABwppHHu5052upleEQrsWad_QHD4CayzF4mk24Gu1Pd3Dxn4JVd7jBSiTi3CXE7k00B7huIZJqu3QIwPAMiIh44b6Q");
		FbLoginServiveImpl.serveLex(alexaDTO, context);
		}
}
