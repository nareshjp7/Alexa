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

public class AlexaConfirmOrderServiceImpl implements AlexaService {

	@Override
	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context) {
		// TODO Auto-generated method stub
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		Table orderTable = dynamoDB.getTable("Order");
	
		
		ScanExpressionSpec xspec = new ExpressionSpecBuilder().withCondition(S("userId").eq(alexaDTO.getUserId())
				.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("orderFrom").eq("Alexa")))
				.buildForScan();
		System.out.println(alexaDTO.getUserId());
		ItemCollection<ScanOutcome> scan = orderTable.scan(xspec);
		Item order = null;
		Page<Item, ScanOutcome> firstPage = scan.firstPage();	
		if (firstPage.iterator().hasNext()) {
			order = firstPage.iterator().next();			
			UpdateItemSpec updateItemSpec2 = new UpdateItemSpec().withPrimaryKey("uuid", order.getString("uuid"))
					.withUpdateExpression("set paymentDone =:pd")
					.withValueMap(new ValueMap().withString(":pd", "true"));
			UpdateItemOutcome outcome2 = orderTable.updateItem(updateItemSpec2);
			outcome2.getItem();
			
			AlexaResponse alexaResponse = new AlexaResponse();
			Response response = new Response();
			OutputSpeech outputSpeech = new OutputSpeech();
			outputSpeech.setSsml(
					"<speak>Thank you, Is this Order for a delivery or a Pickup.</speak>");
			outputSpeech.setType("SSML");
			Card card = new Card();
			card.setTitle("Add Delivery Method");
			card.setType("Simple");
			card.setContent(
					"Thank you, Is this Order for a delivery or a Pickup.");
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
		return null;
	}
	public static void main(String[] args) {
		AlexaConfirmOrderServiceImpl FbLoginServiveImpl = new AlexaConfirmOrderServiceImpl();
		Context context = null;
		AlexaDTO alexaDTO= new AlexaDTO();
		alexaDTO.setRequest("9493689846");
		alexaDTO.setDeviceId("123456");
		alexaDTO.setUserId("AFP6QTFEMSGJB6ATYY3OU6U6X7Y7QQCDVPZWBB7VTH2UOAOMKUULVGJHILIS2FBEBPFST4HJPOW4PMJY3BWWVTUJZKEOQ6JJOKME5O2ILIT2X5P7KHMDDKQVRQLN22BW72QCRWU72ILHFE5GJXYAKBCCWFO7LUN3XVITM3NLNGOD6MNTMCIP5II5BX2XTD6XPQBJEGAEDGESQ3I");
		FbLoginServiveImpl.serveLex(alexaDTO, context);
		}

}
