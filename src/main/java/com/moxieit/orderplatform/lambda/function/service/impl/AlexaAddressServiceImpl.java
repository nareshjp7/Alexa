package com.moxieit.orderplatform.lambda.function.service.impl;

import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
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

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;


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

public class AlexaAddressServiceImpl implements AlexaService{

	@Override
	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context) {
		
		/*final Logger log = LoggerFactory.getLogger(AlexaAddressServiceImpl.class);*/
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderTable = dynamoDB.getTable("Order");
		Table orderItemTable = dynamoDB.getTable("OrderItems");
	    String deviceId = alexaDTO.getDeviceId();
	    String consentToken = alexaDTO.getConsentToken();
	    String apiEndpoint = "https://api.amazonalexa.com";

	    final String BASE_API_PATH = "/v1/devices/";
	    final String SETTINGS_PATH = "/settings/";
	    final String FULL_ADDRESS_PATH = "address";
	    //final String COUNTRY_AND_POSTAL_CODE_PATH = "address/countryAndPostalCode";
	   /* public AlexaAddressServiceImpl (String requestDeviceId, String requestConsentToken, String requestApiEndpoint) {
	        deviceId = requestDeviceId;
	        consentToken = requestConsentToken;
	        apiEndpoint = requestApiEndpoint;
	    }*/
	   /*public Address getFullAddress()  {*/
	        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

	        String requestUrl = apiEndpoint + BASE_API_PATH + deviceId + SETTINGS_PATH + FULL_ADDRESS_PATH;
	       /* log.info("Request will be made to the following URL: {}", requestUrl);*/

	        HttpGet httpGet = new HttpGet(requestUrl);

	        httpGet.addHeader("Authorization", "Bearer " + consentToken);

	        /*log.info("Sending request to Device Address API");*/
	        Address address = null;
	        try {
	            HttpResponse addressResponse = closeableHttpClient.execute(httpGet);
	            int statusCode = addressResponse.getStatusLine().getStatusCode();

	           /* log.info("The Device Address API responded with a status code of {}", statusCode);*/

	            if (statusCode == HttpStatus.SC_OK) {
	                HttpEntity httpEntity = addressResponse.getEntity();
	                String responseBody = EntityUtils.toString(httpEntity);

	                ObjectMapper objectMapper = new ObjectMapper();
	                address = objectMapper.readValue(responseBody, Address.class);
	                System.out.println("address is:"+address);
	            } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
	                /*log.info("Failed to authorize with a status code of {}", statusCode);*/
	            	System.out.println("Failed to authorize,"+statusCode);
	            } else {
	                String errorMessage = "Device Address API query failed with status code of " + statusCode;
	                /*log.info(errorMessage);*/
	                System.out.println(errorMessage);
	            }
	        }  catch (IOException e) {
	        	
	        } finally {
	        	System.out.println("Request to Address Device API completed.");
	        String FullAddress =address.getAddressLine1() +","+	address.getStateOrRegion()+","+	address.getPostalCode()+","+        	address.getCity();
	        	address.getAddressLine2();
	        	 System.out.println("Final address is:"+address);
	        	 
	        	 ScanExpressionSpec xspec1 = new ExpressionSpecBuilder().withCondition(S("userId").eq(alexaDTO.getUserId())
	 					.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("true")))
	 					.buildForScan();

	 			ItemCollection<ScanOutcome> scan1 = orderItemTable.scan(xspec1);
	 			Item order = null;        
	 			Page<Item, ScanOutcome> firstPage = scan1.firstPage();	
	 			
	 			if (firstPage.iterator().hasNext()) {
	 				order = firstPage.iterator().next();
	        	 UpdateItemSpec updateItemSpec1 = new UpdateItemSpec().withPrimaryKey("uuid", order.getString("orderuuid"))
	 					.withUpdateExpression("set address = :val")					
	 					.withValueMap(new ValueMap().withString(":sta", FullAddress));					
	 			UpdateItemOutcome outcome1 = orderTable.updateItem(updateItemSpec1);
	 			outcome1.getItem();
	 			}
	        }

	        /*  return address;
	    }*/
	
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
	}

}
