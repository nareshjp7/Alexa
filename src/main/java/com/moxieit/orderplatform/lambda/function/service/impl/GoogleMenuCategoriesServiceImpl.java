package com.moxieit.orderplatform.lambda.function.service.impl;

import java.util.HashMap;
import java.util.Iterator;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.AlexaDTO;
import com.moxieit.orderplatform.function.service.api.AlexaService;
import com.moxieit.orderplatform.function.service.api.GoogleDTO;
import com.moxieit.orderplatform.function.service.api.GoogleService;
import com.moxieit.orderplatform.lambda.response.BaseResponse;
import com.moxieit.orderplatform.lambda.response.GoogleResponse;

public class GoogleMenuCategoriesServiceImpl implements GoogleService{

	@Override
	public BaseResponse serveLex(GoogleDTO googleDTO, Context context) {
		// TODO Auto-generated method stub
		StringBuilder menuCategory = new StringBuilder();
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table menuCategoriesTable = dynamoDB.getTable("Menu_Categories");
	
		String restaurantId = googleDTO.getRestaurantId();
		
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
		GoogleResponse googleResponse = new GoogleResponse();
		googleResponse.setSpeech("We have "+menuCategory+" What would you like.");
		
		return googleResponse;
		
	}
	

}
