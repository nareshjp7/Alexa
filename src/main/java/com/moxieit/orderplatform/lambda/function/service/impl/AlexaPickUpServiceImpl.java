package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

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
import com.moxieit.orderplatform.lambda.response.AlexaResponse;
import com.moxieit.orderplatform.lambda.response.BaseResponse;
import com.moxieit.orderplatform.lambda.response.Card;
import com.moxieit.orderplatform.lambda.response.OutputSpeech;
import com.moxieit.orderplatform.lambda.response.Reprompt;
import com.moxieit.orderplatform.lambda.response.Response;

public class AlexaPickUpServiceImpl implements AlexaService {

	@Override
	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context) {
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		Table orderTable = dynamoDB.getTable("Order");
		 ScanExpressionSpec xspec2 = new ExpressionSpecBuilder().withCondition(S("userId").eq(alexaDTO.getUserId())
					.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("true")))
					.buildForScan();

			ItemCollection<ScanOutcome> scan2 = orderItemTable.scan(xspec2);
			Item order1 = null;        
			Page<Item, ScanOutcome> firstPage1 = scan2.firstPage();	
			
			if (firstPage1.iterator().hasNext()) {
				order1 = firstPage1.iterator().next();
     	 UpdateItemSpec updateItemSpec1 = new UpdateItemSpec().withPrimaryKey("uuid", order1.getString("orderuuid"))
					.withUpdateExpression("set pickUp = :val,address = :add")					
					.withValueMap(new ValueMap().withString(":val", "30 Mins").withString(":add", "NULL"));					
			UpdateItemOutcome outcome1 = orderTable.updateItem(updateItemSpec1);
			outcome1.getItem();
		
			
		AlexaResponse alexaResponse = new AlexaResponse();
		Response response = new Response();
		OutputSpeech outputSpeech = new OutputSpeech();
		outputSpeech
				.setSsml("<speak>Please provide your phone number for this order and collect you order within 30 minutes.</speak>");
		outputSpeech.setType("SSML");
		Card card = new Card();
		card.setTitle("Add Phone Number");
		card.setType("Simple");
		card.setContent("Please provide your phone number for this order and collect you order within 30 minutes.");
		Reprompt reprompt = new Reprompt();
		OutputSpeech outputSpeech1 = new OutputSpeech();
		outputSpeech1.setText("Please add your phone number.");
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
			return null;
	}

}
