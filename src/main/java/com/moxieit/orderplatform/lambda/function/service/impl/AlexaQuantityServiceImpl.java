package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.amazonaws.services.dynamodbv2.xspec.N;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.util.NumberUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.AlexaDTO;
import com.moxieit.orderplatform.function.service.api.AlexaService;
import com.moxieit.orderplatform.lambda.request.SesRequest;
import com.moxieit.orderplatform.lambda.response.Address;
import com.moxieit.orderplatform.lambda.response.AlexaResponse;
import com.moxieit.orderplatform.lambda.response.BaseResponse;
import com.moxieit.orderplatform.lambda.response.Card;
import com.moxieit.orderplatform.lambda.response.OutputSpeech;
import com.moxieit.orderplatform.lambda.response.Reprompt;
import com.moxieit.orderplatform.lambda.response.Response;

public class AlexaQuantityServiceImpl implements AlexaService {

	

	@Override
	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context) {
		
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		Table orderTable = dynamoDB.getTable("Order");
		Table restaurantTable = dynamoDB.getTable("Restaurant");
		Double phonenumber =Double.parseDouble(alexaDTO.getRequest());
		int nDigits = (int) (Math.floor(Math.log10(Math.abs(phonenumber))) + 1);		
		//System.out.println(nDigits);
		if (nDigits >= 4)
		{
			
			//try{
				
				ScanExpressionSpec xspec1 = new ExpressionSpecBuilder().withCondition(S("userId").eq(alexaDTO.getUserId())
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
					phoneNumcode = "+1"+alexaDTO.getRequest();
				       System.out.println("Us phonenumber:" + phoneNumcode);
				}else if (countryName.contains("India")){
					phoneNumcode = "+91"+alexaDTO.getRequest();
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
                String snsmessage = "Thank you for your order! "+restaurantName +" confirmed your order."
    	        		+ " Call us at "+restaurantphn+" if you need anything else. Order #"+order.getString("orderuuid");
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
    			
		        AlexaResponse alexaResponse = new AlexaResponse();
				Response response = new Response();
				OutputSpeech outputSpeech = new OutputSpeech();
				outputSpeech
						.setSsml("<speak>Your order was confirmed. Thank you.</speak>");
				outputSpeech.setType("SSML");
				Card card = new Card();
				card.setTitle("Taken an Order");
				card.setType("Simple");
				//card.setPermissions("read::alexa:device:all:address");
				card.setContent("Your order was confirmed. Thank you.");
				Reprompt reprompt = new Reprompt();
				OutputSpeech outputSpeech1 = new OutputSpeech();
				outputSpeech1.setText("Please select your option.");
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

		
		} else{
		
		
		Number request = Integer.parseInt(alexaDTO.getRequest());	
		
		ScanExpressionSpec xspec = new ExpressionSpecBuilder().withCondition(S("userId").eq(alexaDTO.getUserId())
				.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("false")))
				.buildForScan();

		ItemCollection<ScanOutcome> scan = orderItemTable.scan(xspec);
		Item order = null;        
		Page<Item, ScanOutcome> firstPage = scan.firstPage();	
		System.out.println(firstPage);
		if (firstPage.iterator().hasNext()) {
			order = firstPage.iterator().next();
			System.out.println(order);
			String itemPrice = order.getString("itemCost");			
			String isSpicy = order.getString("isSpicy");	
			Double itemCost = (double)((Integer) request).intValue() * Double.parseDouble(itemPrice);		
						
			UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uuid", order.getString("uuid"))
					.withUpdateExpression("set itemCost = :val,quantity = :qua")					
					.withValueMap(new ValueMap().withNumber(":val",  (Number) itemCost).withNumber(":qua",(Number) request));					
			UpdateItemOutcome outcome = orderItemTable.updateItem(updateItemSpec);
			outcome.getItem();	
			System.out.println(order.getString("uuid"));
			Item orderItem = orderTable.getItem("uuid", order.getString("orderuuid"));
			Number totalcost = orderItem.getNumber("totalBillWithTax");	
			Double bill = totalcost.doubleValue();			
			Double taxper = .06;
			Double tax = (double) itemCost * taxper.doubleValue();			
			Double totalValueWithTax = bill + tax + (double) itemCost ;
			System.out.println(totalValueWithTax);
			
			//Number total = totalcost.doubleValue() + itemCost.doubleValue();
			UpdateItemSpec updateItemSpec1 = new UpdateItemSpec().withPrimaryKey("uuid", order.getString("orderuuid"))
					.withUpdateExpression("set totalBillWithTax = :val")					
					.withValueMap(new ValueMap().withNumber(":val", (Number) totalValueWithTax));					
			UpdateItemOutcome outcome1 = orderTable.updateItem(updateItemSpec1);
			outcome1.getItem();				
			AlexaResponse alexaResponse = new AlexaResponse();
			Response response = new Response();
			OutputSpeech outputSpeech = new OutputSpeech();
			Card card = new Card();
			if (isSpicy.equalsIgnoreCase("true")){						
			outputSpeech.setSsml(
					"<speak>How much Spicy Level do u want to add for  this order We have Mild , Medium and Spicy. </speak>");
			outputSpeech.setType("SSML");
			
			card.setTitle("Add Spicy Level");
			card.setType("Simple");
			card.setContent("How much Spicy Level do u want to add for  this order We have Mild , Medium and Spicy.");
		} else{		
			UpdateItemSpec updateItemSpec2 = new UpdateItemSpec().withPrimaryKey("uuid", order.getString("orderuuid"))
					.withUpdateExpression("set paymentDone =:pd")
					.withValueMap(new ValueMap().withString(":pd", "true"));
			UpdateItemOutcome outcome2 = orderTable.updateItem(updateItemSpec2);
			outcome2.getItem();
			UpdateItemSpec updateItemSpec4 = new UpdateItemSpec().withPrimaryKey("uuid", order.get("uuid"))
					.withUpdateExpression("set itemQuantityAdd = :add")
					.withValueMap(new ValueMap().withString(":add", "true"));
			UpdateItemOutcome outcome4 = orderItemTable.updateItem(updateItemSpec4);
			outcome4.getItem();
			outputSpeech.setSsml(
					"<speak>If you want to add More Items speak Menu or itemname, otherwise confirm this Order for a delivery or a Pickup.</speak>");
			outputSpeech.setType("SSML");			
			card.setTitle("Add Delivery Method");
			card.setType("Simple");
			card.setContent(
					"If you want to add More Items speak Menu or itemname, otherwise confirm this Order for a delivery or a Pickup.");
		}
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
	public static void main(String[] args) {
		AlexaQuantityServiceImpl FbLoginServiveImpl = new AlexaQuantityServiceImpl();
		Context context = null;
		AlexaDTO alexaDTO= new AlexaDTO();
		alexaDTO.setRequest("1");
		alexaDTO.setUserId("AHQF3TKAP2QZJYJS524AHAU3M6PWTQ7RX4HKWTCZ22T3M4GHXLPTW2ZEI3XU4IHOZOWQ5WG5WL3YHICSWEDQUQV2KZME7ROON5O6VFWQHGN6X4JUYENRRWUXQBC4HUUKJMZ4URNHJFJ3F4TBMI5GFD4HYMWPKDPTXGHTHV5HKKTARK3UWGYNH7K4SMT4GRGTIZBMRPDYGOI3DFQ");
		FbLoginServiveImpl.serveLex(alexaDTO, context);
		}

}
