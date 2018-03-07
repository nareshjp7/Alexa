package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.AlexaDTO;
import com.moxieit.orderplatform.function.service.api.GoogleDTO;
import com.moxieit.orderplatform.function.service.api.GoogleService;
import com.moxieit.orderplatform.lambda.request.SesRequest;
import com.moxieit.orderplatform.lambda.response.BaseResponse;
import com.moxieit.orderplatform.lambda.response.GoogleResponse;



public class GoogleQuantityServiceImpl implements GoogleService {


	public BaseResponse serveLex(GoogleDTO googleDTO, Context context) {
		// TODO Auto-generated method stub
	
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		Table orderTable = dynamoDB.getTable("Order");
	
		GoogleResponse googleResponse = new GoogleResponse();
		/*Double phonenumber =Double.parseDouble((googleDTO.getRequest().replaceAll("-", "")).replaceAll("\\s+",""));
		
		int nDigits = (int) (Math.floor(Math.log10(Math.abs(phonenumber))) + 1);		
		System.out.println(nDigits);
		   String orderuuid ="";
	        String totalBillWithTax ="";
	        String phoneNumcode = "";
	     
		if (nDigits >= 4)
		{	} else {*/
		Number request = 0;
		try{
		 request = Integer.parseInt(googleDTO.getRequest());	
		} catch (Exception e) {
		 request = Integer.parseInt(googleDTO.getQuantityValue());
		}
		
		ScanExpressionSpec xspec2 ;
		try{
			List<Date> dates = new ArrayList<Date>();
			Calendar calendar = Calendar.getInstance();
			
			ScanExpressionSpec xspec = new ExpressionSpecBuilder().withCondition(S("userId").eq(googleDTO.getUserId())
					.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("false")))
					.buildForScan();
			
			ItemCollection<ScanOutcome> scan = orderItemTable.scan(xspec);

			Consumer<Item> action = new Consumer<Item>() {
				public void accept(Item t) {
				
				Number creationDate = t.getNumber("creationDate");
				String x = creationDate.toString();				
				long milliSeconds = Long.parseLong(x);					
				calendar.setTimeInMillis(milliSeconds);		
				Date recentDate = null;		
					//recentDate = (Date) formatter1.parse(orderDate);
					recentDate = (Date) calendar.getTime();				
					
				dates.add(recentDate);
			

				}

			};
			scan.forEach(action);	
			Date latest = Collections.max(dates);			
			long itemdateMilliSec = latest.getTime();
			System.out.println("itemdateMilliSec date :"+itemdateMilliSec);
			xspec2 = new ExpressionSpecBuilder().withCondition(S("userId").eq(googleDTO.getUserId())
					.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("false"))
					.and(N("creationDate").eq((Number)itemdateMilliSec)))
					.buildForScan();
		
			} catch(Exception e) {
				 xspec2 = new ExpressionSpecBuilder().withCondition(S("userId").eq(googleDTO.getUserId())
						.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("false")))
					
						.buildForScan();
					
			}
	
			
		ItemCollection<ScanOutcome> scan2 = orderItemTable.scan(xspec2);
		Item order = null;        
		Page<Item, ScanOutcome> firstPage2 = scan2.firstPage();	
		System.out.println(firstPage2);
		
		
		if (firstPage2.iterator().hasNext()) {
			order = firstPage2.iterator().next();
			System.out.println(order);
			String itemPrice = order.getString("itemCost");			
			String isSpicy = order.getString("isSpicy");	
			System.out.println("isSpicy "+ isSpicy);
			Double itemCost = (double)((Integer) request).intValue() * Double.parseDouble(itemPrice);		
						
			UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uuid", order.getString("uuid"))
					.withUpdateExpression("set itemCost = :val,quantity = :qua")					
					.withValueMap(new ValueMap().withNumber(":val",  (Number) itemCost).withNumber(":qua",(Number) request));					
			UpdateItemOutcome outcome = orderItemTable.updateItem(updateItemSpec);
			outcome.getItem();	
			System.out.println(order.getString("uuid"));
			Item orderItem = orderTable.getItem("uuid", order.getString("orderuuid"));
			Number totalcostwithtax = orderItem.getNumber("totalBillWithTax");	
			Number totalcost = orderItem.getNumber("totalBill");	
			Number totaltaxvaue = orderItem.getNumber("tax");	
			Double bill = totalcostwithtax.doubleValue();			
			Double taxper = .06;
			Double tax = (double) itemCost * taxper.doubleValue();			
			Double totalValueWithTax = bill + tax + (double) itemCost ;
			Double totalBill1 = totalcost.doubleValue();
			Double tax1 = totaltaxvaue.doubleValue();
			Number totalBill = totalBill1 +(double) itemCost;
			Number totaltax = tax1 + tax;
			System.out.println(totalValueWithTax);
			
			//Number total = totalcost.doubleValue() + itemCost.doubleValue();
			UpdateItemSpec updateItemSpec1 = new UpdateItemSpec().withPrimaryKey("uuid", order.getString("orderuuid"))
					.withUpdateExpression("set totalBillWithTax = :val, totalBill = :bill, tax = :tax")					
					.withValueMap(new ValueMap().withNumber(":val", (Number) totalValueWithTax).withNumber(":bill", (Number) totalBill)
							.withNumber(":tax", (Number) totaltax));					
			UpdateItemOutcome outcome1 = orderTable.updateItem(updateItemSpec1);
			outcome1.getItem();				
			
			
		if (isSpicy.equalsIgnoreCase("true")){		
			System.out.println("isSpicy true");
			googleResponse.setSpeech("How much Spicy Level do u want to add for  this order We have Mild , Medium and Spicy.");
		
		} else{	
			
					
		
			UpdateItemSpec updateItemSpec4 = new UpdateItemSpec().withPrimaryKey("uuid", order.get("uuid"))
					.withUpdateExpression("set itemQuantityAdd = :add, spiceyLevel = :val")
					.withValueMap(new ValueMap().withString(":add", "true").withString(":val", "NULL"));
			UpdateItemOutcome outcome4 = orderItemTable.updateItem(updateItemSpec4);
			outcome4.getItem();
		
			googleResponse.setSpeech("If you want to add More Items speak Menu or itemname, otherwise confirm this Order for a delivery or a Pickup.");
						
		}			
		
		}
		return googleResponse;
	}

	public static void main(String[] args) {
		GoogleQuantityServiceImpl googleService = new GoogleQuantityServiceImpl();
		GoogleDTO googleDTO = new GoogleDTO();
		googleDTO.setUserId("ABwppHHu5052upleEQrsWad_QHD4CayzF4mk24Gu1Pd3Dxn4JVd7jBSiTi3CXE7k00B7huIZJqu3QIwPAMiIh44b6Q");
		googleDTO.setRequest("2");
		googleDTO.setIntentName("Quantity");
		Context context = null ;
		googleService.serveLex(googleDTO, context);
		}

}
