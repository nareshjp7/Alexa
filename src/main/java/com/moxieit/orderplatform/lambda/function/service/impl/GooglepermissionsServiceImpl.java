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

public class GooglepermissionsServiceImpl implements GoogleService{

	@Override
	public BaseResponse serveLex(GoogleDTO googleDTO, Context context) {
		// TODO Auto-generated method stub
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		Table orderTable = dynamoDB.getTable("Order");
		String isPermissionGranted = googleDTO.getPermissionsGranted();
		System.out.println(googleDTO.getPermissionsGranted());
		
		List<Date> dates = new ArrayList<Date>();
		Calendar calendar = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		ScanExpressionSpec xspec1 = new ExpressionSpecBuilder().withCondition(S("userId").eq(googleDTO.getUserId())
				.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("true")))
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
				System.out.println("recentDate date :"+recentDate);			
			dates.add(recentDate);
			System.out.println("dates date :"+dates);

			}

		};
		scan1.forEach(action1);
		Date latest = Collections.max(dates);
		System.out.println("latest date :"+latest);
		long itemdateMilliSec = latest.getTime();
		System.out.println("itemdateMilliSec date :"+itemdateMilliSec);
		
		if(isPermissionGranted.equals("true")){
	        String fullAddress = googleDTO.getFormattedAddress();
	        System.out.println(fullAddress);
	        
	        ScanExpressionSpec xspec2 = new ExpressionSpecBuilder().withCondition(S("userId").eq(googleDTO.getUserId())
						.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("true"))
						.and(N("creationDate").eq((Number)itemdateMilliSec)))
						.buildForScan();

				ItemCollection<ScanOutcome> scan2 = orderItemTable.scan(xspec2);
				Item order1 = null;        
				Page<Item, ScanOutcome> firstPage1 = scan2.firstPage();	
				
				if (firstPage1.iterator().hasNext()) {
					order1 = firstPage1.iterator().next();
					 System.out.println(order1);
	     	 UpdateItemSpec updateItemSpec1 = new UpdateItemSpec().withPrimaryKey("uuid", order1.getString("orderuuid"))
						.withUpdateExpression("set address = :val,pickUp = :pic")					
						.withValueMap(new ValueMap().withString(":val", fullAddress).withString(":pic", "NULL"));					
				UpdateItemOutcome outcome1 = orderTable.updateItem(updateItemSpec1);
				outcome1.getItem();
				 System.out.println(outcome1);
				}
			GoogleResponse googleResponse = new GoogleResponse();		  
			googleResponse.setSpeech("It's Sounds Good, Which PHONENUMBER  Would you like to add for this order.");
			return googleResponse;
			
		} else {
			GoogleResponse googleResponse = new GoogleResponse();		  
			googleResponse.setSpeech("It's ok, Please provide your phone number for this order and collect you order within 30 minutes.");
			return googleResponse;
			
		}
	
	}

	public static void main(String[] args) {
		GoogleService googleService = new GooglepermissionsServiceImpl();
		GoogleDTO googleDTO = new GoogleDTO();
		googleDTO.setFormattedAddress("nizampet");
		googleDTO.setPermissionsGranted("false");
		googleDTO.setUserId("ABwppHHu5052upleEQrsWad_QHD4CayzF4mk24Gu1Pd3Dxn4JVd7jBSiTi3CXE7k00B7huIZJqu3QIwPAMiIh44b6Q");
		Context context = null ;
		googleService.serveLex(googleDTO, context);
		}

}
