package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Consumer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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
import com.amazonaws.util.NumberUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.AlexaDTO;
import com.moxieit.orderplatform.function.service.api.AlexaService;
import com.moxieit.orderplatform.lambda.response.Address;
import com.moxieit.orderplatform.lambda.response.AlexaResponse;
import com.moxieit.orderplatform.lambda.response.BaseResponse;
import com.moxieit.orderplatform.lambda.response.Card;
import com.moxieit.orderplatform.lambda.response.OutputSpeech;
import com.moxieit.orderplatform.lambda.response.Reprompt;
import com.moxieit.orderplatform.lambda.response.Response;

public class AlexaOneQuantityServiceImpl implements AlexaService {

	@Override
	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context) {
		
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		Table orderTable = dynamoDB.getTable("Order");
		Double phonenumber =Double.parseDouble(alexaDTO.getRequest());
		int nDigits = (int) (Math.floor(Math.log10(Math.abs(phonenumber))) + 1);		
		//System.out.println(nDigits);
		if (nDigits >= 4)
		{
			
			//try{
				System.out.println("firstPage :");
				ScanExpressionSpec xspec1 = new ExpressionSpecBuilder().withCondition(S("userId").eq(alexaDTO.getUserId())
					.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("false")))
					.buildForScan();
				
			ItemCollection<ScanOutcome> scan1 = orderItemTable.scan(xspec1);
			Item order = null;        
			Page<Item, ScanOutcome> firstPage = scan1.firstPage();	
			
			if (firstPage.iterator().hasNext()) {
				order = firstPage.iterator().next();
				System.out.println("order :"+order);
				UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uuid", order.getString("uuid"))
						.withUpdateExpression("set itemQuantityAdd = :itQu")
						.withValueMap(new ValueMap().withString(":itQu", "true"));
				UpdateItemOutcome outcome = orderItemTable.updateItem(updateItemSpec);
				outcome.getItem();
				UpdateItemSpec updateItemSpec2 = new UpdateItemSpec().withPrimaryKey("uuid", order.getString("orderuuid"))
						.withUpdateExpression("set phoneNumber = :val,orderStatus = :sta")
						.withValueMap(new ValueMap().withNumber(":val",(Number)phonenumber).withString(":sta", "ACCEPTED"));					
				UpdateItemOutcome outcome2 = orderTable.updateItem(updateItemSpec2);
				outcome2.getItem();	
				
			try{
				
			   String deviceId = alexaDTO.getDeviceId();
			    String consentToken = alexaDTO.getConsentToken();
			    String apiEndpoint = "https://api.amazonalexa.com";

			    final String BASE_API_PATH = "/v1/devices/";
			    final String SETTINGS_PATH = "/settings/";
			    final String FULL_ADDRESS_PATH = "address";
			    //final String COUNTRY_AND_POSTAL_CODE_PATH = "address/countryAndPostalCode";
			  
			        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

			        String requestUrl = apiEndpoint + BASE_API_PATH + deviceId + SETTINGS_PATH + FULL_ADDRESS_PATH;
			       
			        HttpGet httpGet = new HttpGet(requestUrl);

			        httpGet.addHeader("Authorization", "Bearer " + consentToken);

			       
			        Address address = null;
			        try {
			            HttpResponse addressResponse = closeableHttpClient.execute(httpGet);
			            int statusCode = addressResponse.getStatusLine().getStatusCode();

			           
			            if (statusCode == HttpStatus.SC_OK) {
			                HttpEntity httpEntity = addressResponse.getEntity();
			                String responseBody = EntityUtils.toString(httpEntity);

			                ObjectMapper objectMapper = new ObjectMapper();
			                address = objectMapper.readValue(responseBody, Address.class);
			                System.out.println("address is:"+address);
			            } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
			                
			            	System.out.println("Failed to authorize,"+statusCode);
			            } else {
			                String errorMessage = "Device Address API query failed with status code of " + statusCode;
			                
			                System.out.println(errorMessage);
			            }
			        }  catch (IOException e) {
			        	
			        } finally {
			        	System.out.println("Request to Address Device API completed.");
			        String FullAddress =address.getAddressLine1() +","+	address.getStateOrRegion()+","+	address.getPostalCode()+","+        	address.getCity();
			        	address.getAddressLine2();
			        	 System.out.println("Final address is:"+address);
			        	 
			        	 ScanExpressionSpec xspec2 = new ExpressionSpecBuilder().withCondition(S("userId").eq(alexaDTO.getUserId())
			 					.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("true")))
			 					.buildForScan();

			 			ItemCollection<ScanOutcome> scan2 = orderItemTable.scan(xspec2);
			 			Item order1 = null;        
			 			Page<Item, ScanOutcome> firstPage1 = scan2.firstPage();	
			 			
			 			if (firstPage1.iterator().hasNext()) {
			 				order1 = firstPage1.iterator().next();
			        	 UpdateItemSpec updateItemSpec1 = new UpdateItemSpec().withPrimaryKey("uuid", order1.getString("orderuuid"))
			 					.withUpdateExpression("set address = :val,deviceId = :id")					
			 					.withValueMap(new ValueMap().withString(":sta", FullAddress).withString(":id", deviceId));					
			 			UpdateItemOutcome outcome1 = orderTable.updateItem(updateItemSpec1);
			 			outcome1.getItem();
			 			}
			        }
			        
		} catch (Exception e){
			System.out.println("Exception in getting deviceId."+e);
	
		}
			       
			
				AlexaResponse alexaResponse = new AlexaResponse();
				Response response = new Response();
				OutputSpeech outputSpeech = new OutputSpeech();
				outputSpeech
						.setSsml("<speak>Thank you for you order If you want to add More Items speak Menu if not you can speak Checkout Or Order is Done.</speak>");
				outputSpeech.setType("SSML");
				Card card = new Card();
				card.setTitle("Audio");
				card.setType("Simple");
				card.setContent("Thank you for you order If you want to add More Items speak Menu if not you can speak Checkout Or Order is Done.");
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
			/*} catch (Exception e) {
	   		StringBuilder BODY = new StringBuilder();	
	    	   long phonenumber1 =(long) phonenumber.doubleValue();
	    	   
	        	ScanExpressionSpec xspec2 = new ExpressionSpecBuilder()
						.withCondition(S("phoneNumber").eq("9493689846"))
						.buildForScan();

				ItemCollection<ScanOutcome> scan2 = orderTable.scan(xspec2);
				      
			
				Consumer<Item> action1 = new Consumer<Item>() {
					@Override
					public void accept(Item t1) {
						Table orderTable = dynamoDB.getTable("Order");
						Object order1 = t1.getString("uuid");
						Item itemuuid = orderTable.getItem("uuid", order1);
						String orderuuid = itemuuid.getString("uuid");
						String orderDate = itemuuid.getString("orderDate");
					System.out.println("order with phone number:"+orderuuid);
					System.out.println("orderDate is:"+orderDate);
					
					Table orderItemsTable = dynamoDB.getTable("OrderItems");
					ScanExpressionSpec xspec3 = new ExpressionSpecBuilder().withCondition(S("orderuuid").eq(orderuuid))
							.buildForScan();
					ItemCollection<ScanOutcome> scan3 = orderItemsTable.scan(xspec3);

					Consumer<Item> action3 = new Consumer<Item>() {
						@Override
						public void accept(Item t2) {
							Table menuItemTable = dynamoDB.getTable("Menu_Items");
							Object categoryObject = t2.getString("categoryId");
							Object itemObject = t2.getString("menuItemId");
							Item menuItem = menuItemTable.getItem("categoryId", categoryObject, "itemId", itemObject);						

							
						BODY.append("Your Recent order is ").append(menuItem.getString("itemName"))
						.append("Quantity is")
						.append(t2.getString("quantity")).append("Total cost is ")
						.append(t2.getString("itemCost")).append(" Dollars");			


						}

					};
					scan3.forEach(action3);
				}
				};
				scan2.forEach(action1);
				
				String itemsbody = BODY.toString();
				
			   String deviceId = alexaDTO.getDeviceId();
			    String consentToken = alexaDTO.getConsentToken();
			    String apiEndpoint = "https://api.amazonalexa.com";

			    final String BASE_API_PATH = "/v1/devices/";
			    final String SETTINGS_PATH = "/settings/";
			    final String FULL_ADDRESS_PATH = "address";
			    //final String COUNTRY_AND_POSTAL_CODE_PATH = "address/countryAndPostalCode";
			  
			        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

			        String requestUrl = apiEndpoint + BASE_API_PATH + deviceId + SETTINGS_PATH + FULL_ADDRESS_PATH;
			    

			        HttpGet httpGet = new HttpGet(requestUrl);

			        httpGet.addHeader("Authorization", "Bearer " + consentToken);

			  
			        Address address = null;
			        try {
			            HttpResponse addressResponse = closeableHttpClient.execute(httpGet);
			            int statusCode = addressResponse.getStatusLine().getStatusCode();
			    

			            if (statusCode == HttpStatus.SC_OK) {
			                HttpEntity httpEntity = addressResponse.getEntity();
			                String responseBody = EntityUtils.toString(httpEntity);

			                ObjectMapper objectMapper = new ObjectMapper();
			                address = objectMapper.readValue(responseBody, Address.class);
			                System.out.println("address is:"+address);
			            } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
			              
			            	System.out.println("Failed to authorize,"+statusCode);
			            } else {
			                String errorMessage = "Device Address API query failed with status code of " + statusCode;
			                
			                System.out.println(errorMessage);
			            }
			        }  catch (IOException e1) {
			        	
			        } finally {
			        	System.out.println("Request to Address Device API completed.");
			        String FullAddress =address.getAddressLine1() +","+	address.getStateOrRegion()+","+	address.getPostalCode()+","+        	address.getCity();
			        	address.getAddressLine2();
			        	 System.out.println("Final address is:"+address);
			        	 
			        	 ScanExpressionSpec xspec4 = new ExpressionSpecBuilder().withCondition(S("userId").eq(alexaDTO.getUserId())
			 					.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("true")))
			 					.buildForScan();

			 			ItemCollection<ScanOutcome> scan4 = orderItemTable.scan(xspec4);
			 			Item order4 = null;        
			 			Page<Item, ScanOutcome> firstPage4 = scan4.firstPage();	
			 			
			 			if (firstPage4.iterator().hasNext()) {
			 				order4 = firstPage4.iterator().next();
			        	 UpdateItemSpec updateItemSpec4 = new UpdateItemSpec().withPrimaryKey("uuid", order4.getString("orderuuid"))
			 					.withUpdateExpression("set address = :val")					
			 					.withValueMap(new ValueMap().withString(":sta", FullAddress));					
			 			UpdateItemOutcome outcome4 = orderTable.updateItem(updateItemSpec4);
			 			outcome4.getItem();
			 			}
			        }

			       
				AlexaResponse alexaResponse = new AlexaResponse();
				Response response = new Response();
				OutputSpeech outputSpeech = new OutputSpeech();
				outputSpeech
						.setSsml("<speak>"+ itemsbody+",to confirm order speak CheckOut or tell your item name or speak menu.</speak>");
				outputSpeech.setType("SSML");
				Card card = new Card();
				card.setTitle("Audio");
				card.setType("Simple");
				card.setContent(itemsbody+",to confirm order speak CheckOut or tell your item name or speak menu.");
				Reprompt reprompt = new Reprompt();
				OutputSpeech outputSpeech1 = new OutputSpeech();
				outputSpeech1.setText("Can I help you with anything else?");
				outputSpeech1.setType("PlainText");
				reprompt.setOutputSpeech(outputSpeech1);
				response.setShouldEndSession(true);
				response.setReprompt(reprompt);
				response.setCard(card);
				response.setOutputSpeech(outputSpeech);
				alexaResponse.setVersion("1.0");
				alexaResponse.setResponse(response);
				return alexaResponse;
				
			}*/
		
		} else{
		
		
		Number request = Integer.parseInt(alexaDTO.getRequest());	
		
		ScanExpressionSpec xspec = new ExpressionSpecBuilder().withCondition(S("userId").eq(alexaDTO.getUserId())
				.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("false")))
				.buildForScan();

		ItemCollection<ScanOutcome> scan = orderItemTable.scan(xspec);
		Item order = null;        
		Page<Item, ScanOutcome> firstPage = scan.firstPage();	
		
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
					.withUpdateExpression("set totalBillWithTax = :val,orderStatus = :sta")					
					.withValueMap(new ValueMap().withNumber(":val", (Number) totalValueWithTax).withString(":sta", "ACCEPTED"));					
			UpdateItemOutcome outcome1 = orderTable.updateItem(updateItemSpec1);
			outcome1.getItem();				
			AlexaResponse alexaResponse = new AlexaResponse();
			Response response = new Response();
			OutputSpeech outputSpeech = new OutputSpeech();
			Card card = new Card();
			if (isSpicy.equals(true)){						
			outputSpeech.setSsml(
					"<speak>How much Spicy Level do u want to add for  this order We have Mild , Medium and Spicy. </speak>");
			outputSpeech.setType("SSML");
			
			card.setTitle("Audio");
			card.setType("Simple");
			card.setContent("How much Spicy Level do u want to add for  this order We have Mild , Medium and Spicy.");
		} else{		
			UpdateItemSpec updateItemSpec2 = new UpdateItemSpec().withPrimaryKey("uuid", order.getString("orderuuid"))
					.withUpdateExpression("set paymentDone =:pd")
					.withValueMap(new ValueMap().withString(":pd", "true"));
			UpdateItemOutcome outcome2 = orderTable.updateItem(updateItemSpec2);
			outcome2.getItem();
			outputSpeech.setSsml(
					"<speak>Thank you,Is this Order for a delivery or a Pickup.</speak>");
			outputSpeech.setType("SSML");			
			card.setTitle("Audio");
			card.setType("Simple");
			card.setContent(
					"Thank you,Is this Order for a delivery or a Pickup.");
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
	/*public static void main(String[] args) {
		AlexaOneQuantityServiceImpl FbLoginServiveImpl = new AlexaOneQuantityServiceImpl();
		Context context = null;
		AlexaDTO alexaDTO= new AlexaDTO();
		alexaDTO.setRequest("9493689846");
		alexaDTO.setUserId("AFHFYXC47SSN4DSEXYTINPFRSEGCVEN3X5RX5BNA342NRCA73VZX4G43FCQF5TI7L5RFEJD6HHQ2VXPLUSQSSHRJSIBOBZIIFYUWUFOR7Z7PVLX27NJYJYURDFOMJHVWWYE2MPXASCBCNRQUNSJPOYQKAS7IBXAYDKIWIBUIKE3WISM6OD25DFXX344QDDPLOVIUGZCVU2Y5A3A");
		FbLoginServiveImpl.serveLex(alexaDTO, context);
		}*/

}
