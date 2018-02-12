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

import com.moxieit.orderplatform.function.service.api.GoogleDTO;
import com.moxieit.orderplatform.function.service.api.GoogleService;
import com.moxieit.orderplatform.lambda.response.BaseResponse;
import com.moxieit.orderplatform.lambda.response.GoogleResponse;


public class GoogleConfirmOrderServiceImpl implements GoogleService{

	@Override
	public BaseResponse serveLex(GoogleDTO googleDTO, Context context) {
		// TODO Auto-generated method stub
		DynamoDB dynamoDB = DBService.getDBConnection();
	
		Table orderTable = dynamoDB.getTable("Order");
	
		
		ScanExpressionSpec xspec = new ExpressionSpecBuilder().withCondition(S("userId").eq(googleDTO.getUserId())
				.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("orderFrom").eq("GoogleHome")))
				.buildForScan();
		System.out.println(googleDTO.getUserId());
		ItemCollection<ScanOutcome> scan = orderTable.scan(xspec);
		Item order = null;
		Page<Item, ScanOutcome> firstPage = scan.firstPage();	
		if (firstPage.iterator().hasNext()) {
			order = firstPage.iterator().next();	
			System.out.println(order);
			UpdateItemSpec updateItemSpec2 = new UpdateItemSpec().withPrimaryKey("uuid", order.getString("uuid"))
					.withUpdateExpression("set paymentDone =:pd")
					.withValueMap(new ValueMap().withString(":pd", "true"));
			UpdateItemOutcome outcome2 = orderTable.updateItem(updateItemSpec2);
			outcome2.getItem();
			
			GoogleResponse googleResponse = new GoogleResponse();
			googleResponse.setSpeech("Thank you, Is this Order for a delivery or a Pickup.");
			System.out.println(googleResponse);
			return googleResponse;	
			

		}
		return null;
	}
	public static void main(String[] args) {
		GoogleConfirmOrderServiceImpl FbLoginServiveImpl = new GoogleConfirmOrderServiceImpl();
		Context context = null;
		GoogleDTO alexaDTO= new GoogleDTO();
		alexaDTO.setRequest("confirm");
		
		alexaDTO.setUserId("ABwppHHu5052upleEQrsWad_QHD4CayzF4mk24Gu1Pd3Dxn4JVd7jBSiTi3CXE7k00B7huIZJqu3QIwPAMiIh44b6Q");
		FbLoginServiveImpl.serveLex(alexaDTO, context);
		}
}
