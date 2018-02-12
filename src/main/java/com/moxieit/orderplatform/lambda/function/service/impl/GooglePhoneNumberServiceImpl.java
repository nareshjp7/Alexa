package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

	@Override
	public BaseResponse serveLex(GoogleDTO googleDTO, Context context) {
		// TODO Auto-generated method stub
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		Table orderTable = dynamoDB.getTable("Order");
		Table restaurantTable = dynamoDB.getTable("Restaurant");
		
		ScanExpressionSpec xspec1 = new ExpressionSpecBuilder().withCondition(S("userId").eq(googleDTO.getUserId())
				.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("true")))
				.buildForScan();
			
		ItemCollection<ScanOutcome> scan1 = orderItemTable.scan(xspec1);
		Item order = null;        
		Page<Item, ScanOutcome> firstPage = scan1.firstPage();	
		
		if (firstPage.iterator().hasNext()) {
			order = firstPage.iterator().next();
			System.out.println("order :"+order);
			/*UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uuid", order.getString("uuid"))
					.withUpdateExpression("set itemQuantityAdd = :itQu")
					.withValueMap(new ValueMap().withString(":itQu", "true"));
			UpdateItemOutcome outcome = orderItemTable.updateItem(updateItemSpec);
			outcome.getItem();*/
			
			Item ordertableuuid = orderTable.getItem("uuid", order.getString("orderuuid"));
			String botName1 = ordertableuuid.getString("botName");	
			Item restaurantItem = restaurantTable.getItem("botName", botName1);
			String countryName = restaurantItem.getString("country");
			String phoneNumcode = null;
			if(countryName.contains("US")) {
				phoneNumcode = "+1"+googleDTO.getRequest();
			       System.out.println("Us phonenumber:" + phoneNumcode);
			}else if (countryName.contains("India")){
				phoneNumcode = "+91"+googleDTO.getRequest();
			       System.out.println("India phonenumber:" + phoneNumcode);
			} 
			
			UpdateItemSpec updateItemSpec2 = new UpdateItemSpec().withPrimaryKey("uuid", order.getString("orderuuid"))
					.withUpdateExpression("set phoneNumber = :val,orderStatus = :sta")
					.withValueMap(new ValueMap().withString(":val",phoneNumcode).withString(":sta", "ACCEPTED"));					
			UpdateItemOutcome outcome2 = orderTable.updateItem(updateItemSpec2);
			outcome2.getItem();	
			

           
            AmazonSNSClient snsClient = new AmazonSNSClient();
            Item ordertableitem = orderTable.getItem("uuid", order.getString("orderuuid"));
            String 	botName = ordertableitem.getString("botName");          
			Item botnameitem = restaurantTable.getItem("botName", botName);
            String restaurantName = botnameitem.getString("restaurantName");
			String restaurantphn = botnameitem.getString("phone_no");    			            
		     String snsmessage = "Your Order was placed."
		        		+ "If you want to make any changes to this order contact "+restaurantName+" – "
		        		+ restaurantphn;
		       String snsmessage1 = restaurantName+" | Online Receipt\n"
		        		+ "https://vhnnifn9o0.execute-api.us-east-1.amazonaws.com/Lexpayment/receipt?order="+ordertableuuid;
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
			 sendSMSMessage(snsClient, snsmessage1, phoneNumcode, smsAttributes);
		GoogleResponse googleResponse = new GoogleResponse();
		googleResponse.setSpeech("Your order was confirmed. Thank you.");
		return googleResponse;
		}
		return null;
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
