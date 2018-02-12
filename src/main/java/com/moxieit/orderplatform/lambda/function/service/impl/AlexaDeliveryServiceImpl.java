package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

public class AlexaDeliveryServiceImpl implements AlexaService {

	private static final String ALL_ADDRESS_PERMISSION = "read::alexa:device:all:address";
	private static final String ADDRESS_CARD_TITLE = "Get_DeviceAddress";
	private static final String ERROR_TEXT = "There was an error with the skill. Please try again.";
	private static final Logger log = LoggerFactory.getLogger(AlexaDeliveryServiceImpl.class);
	@Override
	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context) {
		
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		Table orderTable = dynamoDB.getTable("Order");
        /* Permissions permission = session.getUser().getPermissions();
        if (permission == null) {
            log.info("The user hasn't authorized the skill. Sending a permissions card.");
            return getPermissionsResponse();
        }*/

        try {
        	String deviceId = alexaDTO.getDeviceId();
		    String apiAccessToken = alexaDTO.getConsentToken();
		    String apiEndpoint = "https://api.amazonalexa.com";

		    AlexaGetDeviceAddress alexaDeviceAddressClient = new AlexaGetDeviceAddress(
                deviceId, apiAccessToken, apiEndpoint);

            Address addressObject = alexaDeviceAddressClient.getFullAddress();

            if (addressObject == null) {
                //return getAskResponse(ADDRESS_CARD_TITLE, ERROR_TEXT);
                AlexaResponse alexaResponse = new AlexaResponse();
				Response response = new Response();
				OutputSpeech outputSpeech = new OutputSpeech();
				outputSpeech
						.setSsml("<speak>ERROR_TEXT</speak>");
				outputSpeech.setType("SSML");
				Card card = new Card();
				card.setTitle(ADDRESS_CARD_TITLE);
				card.setType("Simple");
				card.setContent(ERROR_TEXT);
				Reprompt reprompt = new Reprompt();
				OutputSpeech outputSpeech1 = new OutputSpeech();
				outputSpeech1.setText("Please change permissions and get back to place order.");
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

            
               String  AddressLine1 = addressObject.getAddressLine1();
               String  StateOrRegion =  addressObject.getStateOrRegion();
               String  PostalCode = addressObject.getPostalCode();
               String fullAddress = "Your address is " + AddressLine1 + " " + StateOrRegion + ", " + PostalCode;
               ScanExpressionSpec xspec2 = new ExpressionSpecBuilder().withCondition(S("userId").eq(alexaDTO.getUserId())
	 					.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("true")))
	 					.buildForScan();

	 			ItemCollection<ScanOutcome> scan2 = orderItemTable.scan(xspec2);
	 			Item order1 = null;        
	 			Page<Item, ScanOutcome> firstPage1 = scan2.firstPage();	
	 			
	 			if (firstPage1.iterator().hasNext()) {
	 				order1 = firstPage1.iterator().next();
	        	 UpdateItemSpec updateItemSpec1 = new UpdateItemSpec().withPrimaryKey("uuid", order1.getString("orderuuid"))
	 					.withUpdateExpression("set address = :val,deviceId = :id,pickUp = :pic")					
	 					.withValueMap(new ValueMap().withString(":val", fullAddress).withString(":id", deviceId).withString(":pic", "NULL"));					
	 			UpdateItemOutcome outcome1 = orderTable.updateItem(updateItemSpec1);
	 			outcome1.getItem();
	 			}
        } catch (UnauthorizedException e) {
        	
            	  AlexaResponse alexaResponse = new AlexaResponse();
				Response response = new Response();
				OutputSpeech outputSpeech = new OutputSpeech();
				outputSpeech
						.setSsml("<speak>You have not given this skill permissions to access your address. Please give this skill permissions to access your address.</speak>");
				outputSpeech.setType("SSML");
				Card card = new Card();
				card.setTitle(ADDRESS_CARD_TITLE);
				card.setType("Simple");
				card.setContent("You have not given this skill permissions to access your address. Please give this skill permissions to access your address.");
				Set<String> permissions = new HashSet<>();
		        permissions.add(ALL_ADDRESS_PERMISSION);
		        card.setPermissions(permissions);
				Reprompt reprompt = new Reprompt();
				OutputSpeech outputSpeech1 = new OutputSpeech();
				outputSpeech1.setText("Please provide required permissions and get back to place order.");
				outputSpeech1.setType("PlainText");
				reprompt.setOutputSpeech(outputSpeech1);
				response.setShouldEndSession(true);
				response.setReprompt(reprompt);
				response.setCard(card);
				response.setOutputSpeech(outputSpeech);
				alexaResponse.setVersion("1.0");
				alexaResponse.setResponse(response);
				return alexaResponse;
        } catch (DeviceAddressClientException e) {
        	 AlexaResponse alexaResponse = new AlexaResponse();
				Response response = new Response();
				OutputSpeech outputSpeech = new OutputSpeech();
				outputSpeech
						.setSsml("<speak>ERROR_TEXT</speak>");
				outputSpeech.setType("SSML");
				Card card = new Card();
				card.setTitle(ADDRESS_CARD_TITLE);
				card.setType("Simple");
				card.setContent(ERROR_TEXT);
				Reprompt reprompt = new Reprompt();
				OutputSpeech outputSpeech1 = new OutputSpeech();
				outputSpeech1.setText("Please provide required permissions and get back to place order.");
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
        
		AlexaResponse alexaResponse = new AlexaResponse();
		Response response = new Response();
		OutputSpeech outputSpeech = new OutputSpeech();
		outputSpeech
				.setSsml("<speak>It's Sounds Good, Which PHONENUMBER  Would you like to add for this order.</speak>");
		outputSpeech.setType("SSML");
		Card card = new Card();
		card.setTitle("Add Phone Number");
		card.setType("Simple");
		card.setContent("It's Sounds Good, Which phone number would you like to add for this order.");
		Reprompt reprompt = new Reprompt();
		OutputSpeech outputSpeech1 = new OutputSpeech();
		outputSpeech1.setText("Please provide your phone number.");
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
