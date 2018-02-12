package com.moxieit.orderplatform.lambda.function.service.impl;

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


public class GoogleRecentOrderServiceImpl implements GoogleService{
	String orderuuid = "";
	String itemName= "";
	String quantity= "";
	String itemCost= "";
	String categoryId= "";
	String itemId= "";
	boolean isSpicy;
	@Override
	public BaseResponse serveLex(GoogleDTO googleDTO, Context context) {
		// TODO Auto-generated method stub
		try{
		String userId = googleDTO.getUserId();
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderTable = dynamoDB.getTable("Order");			 
		StringBuilder BODY = new StringBuilder();	
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
				Table orderTable = dynamoDB.getTable("Order");
				Object order1 = t1.getString("uuid");
				Item itemuuid = orderTable.getItem("uuid", order1);
				String orderuuid = itemuuid.getString("uuid");				
				
				
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
		.withString("userId", googleDTO.getUserId()).withString("itemCost", itemCost).withBoolean("isSpicy", isSpicy)
		.withString("itemQuantityAdd", "true");
		orderItemsTable.putItem(orderItem);
		System.out.println(orderItem);
		Calendar calendar1 = Calendar.getInstance();
		String date = formatter.format(calendar1.getTime());
		
		Item order = new Item();
		order.withString("uuid", orderuuid).withString("userId", googleDTO.getUserId()).withString("orderStatus", "Initiated")
				.withNumber("creationDate", System.currentTimeMillis()).withNumber("totalBill", 0)
				.withNumber("tax", 0).withNumber("totalBillWithTax", 0).withString("orderTracking", "ACCEPTED")
				.withString("orderDate", date).withString("returnMessage", "true")
				.withString("paymentDone", "false").withString("orderFrom","GoogleHome")
				.withString("restaurantId","1").withString("botName", "Test");
			
		orderTable.putItem(order);
		System.out.println(order);
		GoogleResponse googleResponse = new GoogleResponse();
		googleResponse.setSpeech(""+itemsbody +" ,to confirm order speak Confirm, if not tell your item name or speak menu.");
		System.out.println(googleResponse);
		return googleResponse;
		} catch (Exception e) {
			System.out.println("exception "+e);
			System.out.println("Don't have Recent orders from this device");
			GoogleResponse googleResponse1 = new GoogleResponse();
			googleResponse1.setSpeech("Sorry, you don't have any recent orders from last 30 Days. Please speak New order.");
			return googleResponse1;
		}
	
	}
	public static void main(String[] args) {
		GoogleRecentOrderServiceImpl FbLoginServiveImpl = new GoogleRecentOrderServiceImpl();
		Context context = null;
		GoogleDTO alexaDTO= new GoogleDTO();
		alexaDTO.setRequest("recentorder");
		
		alexaDTO.setUserId("ABwppHHu5052upleEQrsWad_QHD4CayzF4mk24Gu1Pd3Dxn4JVd7jBSiTi3CXE7k00B7huIZJqu3QIwPAMiIh44b6Q");
		FbLoginServiveImpl.serveLex(alexaDTO, context);
		}
}
