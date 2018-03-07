package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.io.IOException;
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
import com.moxieit.orderplatform.function.service.api.GoogleDTO;
import com.moxieit.orderplatform.function.service.api.GoogleService;
import com.moxieit.orderplatform.lambda.request.SesRequest;
import com.moxieit.orderplatform.lambda.response.BaseResponse;
import com.moxieit.orderplatform.lambda.response.GoogleResponse;

public class GooglePhoneNumberServiceImpl implements GoogleService{
	String itemName= "";
	String quantity= "";
	String itemCost= "";
	String categoryId= "";
	String itemId= "";
	boolean isSpicy;
	@Override
	public BaseResponse serveLex(GoogleDTO googleDTO, Context context) {
		// TODO Auto-generated method stub
		DynamoDB dynamoDB = DBService.getDBConnection();
		String botName = googleDTO.getBotName();
		StringBuilder BODY = new StringBuilder();	
		GoogleResponse googleResponse = new GoogleResponse();
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		Table orderTable = dynamoDB.getTable("Order");
		Table restaurantTable = dynamoDB.getTable("Restaurant");
		   String orderuuid ="";
	        String totalBillWithTax ="";
	        String phoneNumcode = "";
		ScanExpressionSpec xspec1 ;
		try{
			List<Date> dates = new ArrayList<Date>();
			Calendar calendar = Calendar.getInstance();
		
			ScanExpressionSpec xspec = new ExpressionSpecBuilder().withCondition(S("userId").eq(googleDTO.getUserId())
					.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("true")))
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
			
			xspec1 = new ExpressionSpecBuilder().withCondition(S("userId").eq(googleDTO.getUserId())
					.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("true"))
					.and(N("creationDate").eq((Number)itemdateMilliSec)))
					.buildForScan();
			} catch(Exception e) {
				xspec1 = new ExpressionSpecBuilder().withCondition(S("userId").eq(googleDTO.getUserId())
						.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("true")))
						
						.buildForScan();
			}
			
		
			
		ItemCollection<ScanOutcome> scan1 = orderItemTable.scan(xspec1);
		Item order = null;        
		Page<Item, ScanOutcome> firstPage = scan1.firstPage();	
		
		if (firstPage.iterator().hasNext()) {
			order = firstPage.iterator().next();
			System.out.println("order :"+order);
			
			Item ordertableuuid = orderTable.getItem("uuid", order.getString("orderuuid"));
			orderuuid = ordertableuuid.getString("uuid");
			totalBillWithTax = ordertableuuid.getString("totalBillWithTax");
			phoneNumcode = ordertableuuid.getString("phoneNumber");
			  botName = ordertableuuid.getString("botName");
	
						
			Item restaurantItem = restaurantTable.getItem("botName", botName);
			String countryName = restaurantItem.getString("country");
		
			if(countryName.contains("US")) {
				phoneNumcode = "+1"+googleDTO.getRequest().replaceAll("-", "").replaceAll("\\s+","");
			       System.out.println("Us phonenumber:" + phoneNumcode);
			}else if (countryName.contains("India")){
				phoneNumcode = "+91"+googleDTO.getRequest().replaceAll("-", "").replaceAll("\\s+","");
			       System.out.println("India phonenumber:" + phoneNumcode);
			} 
			
			UpdateItemSpec updateItemSpec2 = new UpdateItemSpec().withPrimaryKey("uuid", order.getString("orderuuid"))
					.withUpdateExpression("set phoneNumber = :val")
					.withValueMap(new ValueMap().withString(":val",phoneNumcode));					
			UpdateItemOutcome outcome2 = orderTable.updateItem(updateItemSpec2);
			outcome2.getItem();	
		
       }
		ScanExpressionSpec xspec4 = new ExpressionSpecBuilder().withCondition(S("orderuuid").eq(orderuuid).and(S("itemQuantityAdd").eq("true")))
				.buildForScan();
		ItemCollection<ScanOutcome> scan4 = orderItemTable.scan(xspec4);

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
				
			BODY.append(t3.getString("quantity")).append(" ").append(menuItem.getString("itemName")).append(" ,");
			/*.append(",Quantity ")
			.append(t3.getString("quantity")).append(",Total cost is ")
			.append(t3.getString("itemCost")).append(" Dollars");	*/		


			}

		};
		scan4.forEach(action4);		
		String itemsbody = BODY.toString();
		  System.out.println("BODY "+ itemsbody);
     

        googleResponse.setSpeech("your order was confirmed. You are ordered "+BODY+" and Total cost is "+totalBillWithTax+ " Dollars.");
        	
        	 AmazonSNSClient snsClient = new AmazonSNSClient();           
                     
 			Item botnameitem = restaurantTable.getItem("botName", botName);
             String restaurantName = botnameitem.getString("restaurantName");
 			String restaurantphn = botnameitem.getString("phone_no");    			            
 		     String snsmessage = "Your Order was placed."
		        		+ "If you want to make any changes to this order contact "+restaurantName+" – "
		        		+ restaurantphn;
		       String snsmessage1 = restaurantName+" | Online Receipt\n"
		        		+ "https://izzad1rdrh.execute-api.us-east-1.amazonaws.com/dev/recipt?order="+orderuuid;
		        //String phoneNumber = "+919493689846";
		        Map<String, MessageAttributeValue> smsAttributes = 
		                new HashMap<String, MessageAttributeValue>();
		        //<set SMS attributes>
		        sendSMSMessage(snsClient, snsmessage, phoneNumcode, smsAttributes);
             
		        SESorderitems sesorderitems = new SESorderitems();
 			SesRequest sesRequest = new SesRequest();
 			sesRequest.setOrdertableuuid(order.getString("orderuuid"));
 			sesRequest.setPhoneNumber(phoneNumcode);
 			try {
					sesorderitems.getSES(sesRequest);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
 		
                 
        UpdateItemSpec updateItemSpec2 = new UpdateItemSpec().withPrimaryKey("uuid", orderuuid)
				.withUpdateExpression("set orderStatus = :sta, paymentMethod = :pay, paymentDone =:pd")
				.withValueMap(new ValueMap().withString(":sta", "ACCEPTED").withString(":pay", "Cash On Delivery").withString(":pd", "true"));					
		UpdateItemOutcome outcome2 = orderTable.updateItem(updateItemSpec2);
		outcome2.getItem();	
		sendSMSMessage(snsClient, snsmessage1, phoneNumcode, smsAttributes);	
		return googleResponse;
		
	}
	public static void sendSMSMessage(AmazonSNSClient snsClient, String message, 
			String phoneNumcode, Map<String, MessageAttributeValue> smsAttributes) {
	        PublishResult result = snsClient.publish(new PublishRequest()
	                        .withMessage(message)
	                        .withPhoneNumber(phoneNumcode)
	                        .withMessageAttributes(smsAttributes));
	        System.out.println(result);
	}
}
