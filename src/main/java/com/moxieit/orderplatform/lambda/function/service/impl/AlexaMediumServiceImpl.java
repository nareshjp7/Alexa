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

public class AlexaMediumServiceImpl implements AlexaService {

	@Override
	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context) {

		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		Table orderTable = dynamoDB.getTable("Order");
		ScanExpressionSpec xspec = new ExpressionSpecBuilder().withCondition(S("userId").eq(alexaDTO.getUserId())
				.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("false")))
				.buildForScan();

		ItemCollection<ScanOutcome> scan = orderItemTable.scan(xspec);
		Item order = null;

		Page<Item, ScanOutcome> firstPage = scan.firstPage();
		if (firstPage.iterator().hasNext()) {
			order = firstPage.iterator().next();
			UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uuid", order.get("uuid"))
					.withUpdateExpression("set spiceyLevel = :val,itemQuantityAdd = :itQu")
					.withValueMap(new ValueMap().withString(":val", "Medium").withString(":itQu", "true"));
			UpdateItemOutcome outcome = orderItemTable.updateItem(updateItemSpec);
			outcome.getItem();
			UpdateItemSpec updateItemSpec2 = new UpdateItemSpec().withPrimaryKey("uuid", order.getString("orderuuid"))
					.withUpdateExpression("set orderStatus = :val,paymentDone =:pd")
					.withValueMap(new ValueMap().withString(":val", "ACCEPTED").withString(":pd", "true"));
			UpdateItemOutcome outcome2 = orderTable.updateItem(updateItemSpec2);
			outcome2.getItem();
			AlexaResponse alexaResponse = new AlexaResponse();
			Response response = new Response();
			OutputSpeech outputSpeech = new OutputSpeech();
			outputSpeech.setSsml(
					"<speak>Thank you for you order If you want to add More Items speak Menu if not you can speak Checkout Or Order is Done. </speak>");
			outputSpeech.setType("SSML");
			Card card = new Card();
			card.setTitle("Audio");
			card.setType("Simple");
			card.setContent(
					"Thank you for you order If you want to add More Items speak Menu if not you can speak Checkout Or Order is Done.");
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
		return null;

	}

}
