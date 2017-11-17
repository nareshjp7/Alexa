package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.util.UUID;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.Page;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.AlexaDTO;
import com.moxieit.orderplatform.lambda.response.AlexaResponse;
import com.moxieit.orderplatform.lambda.response.BaseResponse;
import com.moxieit.orderplatform.lambda.response.Card;
import com.moxieit.orderplatform.lambda.response.OutputSpeech;
import com.moxieit.orderplatform.lambda.response.Reprompt;
import com.moxieit.orderplatform.lambda.response.Response;

public class AlexaMangolassitemServiceImpl extends AbstractAlexaOrderServiceImpl {

	@Override
	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context) {
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		Table menuItemsTable = dynamoDB.getTable("Menu_Items");
		String itemPrice=null;
		String categoryId=null;
		String itemId=null;
		ScanExpressionSpec xspec = new ExpressionSpecBuilder().withCondition(S("itemName").eq("Mango Lassi"))
				.buildForScan();

		ItemCollection<ScanOutcome> scan = menuItemsTable.scan(xspec);
		Item menuItem = null;
		Page<Item, ScanOutcome> firstPage = scan.firstPage();
		if (firstPage.iterator().hasNext()) {
			menuItem = firstPage.iterator().next();
			itemPrice=(String) menuItem.get("price");
			categoryId=(String) menuItem.get("categoryId");
			itemId=(String) menuItem.get("itemId");
		}
		Item order = getOrder(alexaDTO.getUserId());
		String orderuuid = order.getString("uuid");
		String uuid = UUID.randomUUID().toString();
		Item orderItem = new Item();
		orderItem.withString("uuid", uuid).withString("orderuuid", orderuuid).withString("itemName", "Mango Lassi")
		.withNumber("creationDate", System.currentTimeMillis()).withString("categoryId", categoryId).withString("menuItemId", itemId)
		.withString("userId", alexaDTO.getUserId()).withString("itemCost", itemPrice).withString("itemQuantityAdd", "false");
		orderItemTable.putItem(orderItem);
		AlexaResponse alexaResponse = new AlexaResponse();
		Response response = new Response();
		OutputSpeech outputSpeech = new OutputSpeech();
		outputSpeech.setSsml("<speak>How much quantity do you want for this order.</speak>");
		outputSpeech.setType("SSML");
		Card card = new Card();
		card.setTitle("Audio");
		card.setType("Simple");
		card.setContent("How much quantity do you want for this order.");
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
