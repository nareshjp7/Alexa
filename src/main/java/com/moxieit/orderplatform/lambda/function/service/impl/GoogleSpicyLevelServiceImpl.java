package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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


public class GoogleSpicyLevelServiceImpl implements GoogleService{

	@Override
	public BaseResponse serveLex(GoogleDTO googleDTO, Context context) {
		// TODO Auto-generated method stub

		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		Table orderTable = dynamoDB.getTable("Order");
		
		List<Date> dates = new ArrayList<Date>();
		Calendar calendar = Calendar.getInstance();
		
		ScanExpressionSpec xspec1 = new ExpressionSpecBuilder().withCondition(S("userId").eq(googleDTO.getUserId())
				.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("false")))
				.buildForScan();
		
		ItemCollection<ScanOutcome> scan1 = orderItemTable.scan(xspec1);

		Consumer<Item> action1 = new Consumer<Item>() {
			public void accept(Item t1) {
			
			Number creationDate = t1.getNumber("creationDate");
			String x = creationDate.toString();				
			long milliSeconds = Long.parseLong(x);					
			calendar.setTimeInMillis(milliSeconds);		
			Date recentDate = null;		
				//recentDate = (Date) formatter1.parse(orderDate);
				recentDate = (Date) calendar.getTime();				
			dates.add(recentDate);
			

			}

		};
		scan1.forEach(action1);
		Date latest = Collections.max(dates);		
		long itemdateMilliSec = latest.getTime();
		System.out.println("itemdateMilliSec date :"+itemdateMilliSec);
		
		ScanExpressionSpec xspec = new ExpressionSpecBuilder().withCondition(S("userId").eq(googleDTO.getUserId())
				.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("false"))
				.and(N("creationDate").eq((Number)itemdateMilliSec)))
				.buildForScan();
		System.out.println(googleDTO.getUserId());
		ItemCollection<ScanOutcome> scan = orderItemTable.scan(xspec);
		Item order = null;

		Page<Item, ScanOutcome> firstPage = scan.firstPage();
		System.out.println(firstPage);
		if (firstPage.iterator().hasNext()) {
			order = firstPage.iterator().next();
			System.out.println(order);
			UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uuid", order.get("uuid"))
					.withUpdateExpression("set spiceyLevel = :val,itemQuantityAdd = :add")
					.withValueMap(new ValueMap().withString(":val", googleDTO.getRequest()).withString(":add", "true"));
			UpdateItemOutcome outcome = orderItemTable.updateItem(updateItemSpec);
			outcome.getItem();
		
			GoogleResponse googleResponse = new GoogleResponse();
			googleResponse.setSpeech("If you want to add More Items speak Menu or itemname, otherwise confirm this Order for delivery or Pickup.");
			return googleResponse;
			
			}
		return null;
		
	}
	

}
