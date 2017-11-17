package com.moxieit.orderplatform.lambda.function.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
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

public class AlexaMenuCategoriesServiceImpl implements AlexaService{

	@Override
	public BaseResponse serveLex(AlexaDTO alexaDTO, Context context) {
		StringBuilder menuCategory = new StringBuilder();
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table menuCategoriesTable = dynamoDB.getTable("Menu_Categories");
		String restaurantId = "1";
		
		HashMap<String, Object> valueMap = new HashMap<String, Object>();
		valueMap.put(":v_id", restaurantId);
		valueMap.put(":letter1", restaurantId + "_");
		QuerySpec spec = new QuerySpec()
				.withKeyConditionExpression("restaurantId = :v_id and begins_with(categoryId,:letter1)")
				.withValueMap(valueMap);

		ItemCollection<QueryOutcome> items = menuCategoriesTable.query(spec);

		Iterator<Item> iterator = items.iterator();
		while (iterator.hasNext()) {
			// System.out.println(iterator.next().toJSONPretty());
			Item item = iterator.next();
		
		menuCategory.append(item.getString("categoryName")).append(",");
			
		}
		AlexaResponse alexaResponse = new AlexaResponse();
		Response response = new Response();
		OutputSpeech outputSpeech = new OutputSpeech();
		outputSpeech
				.setSsml("<speak>We have "+menuCategory+" What would you like.</speak>");
		outputSpeech.setType("SSML");
		Card card = new Card();
		card.setTitle("Audio");
		card.setType("Simple");
		card.setContent("We have "+menuCategory+" What would you like.");
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
