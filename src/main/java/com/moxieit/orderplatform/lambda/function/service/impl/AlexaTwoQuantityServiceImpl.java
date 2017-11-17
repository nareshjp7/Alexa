package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.math.BigDecimal;
import java.math.BigInteger;

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

public class AlexaTwoQuantityServiceImpl implements AlexaService {

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
			System.out.println(order);
			String itemPrice = order.getString("itemCost");
			Number itemCost = 2 * Double.parseDouble(itemPrice);
			System.out.println(itemCost);
			UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uuid", order.getString("uuid"))
					.withUpdateExpression("set itemCost = :val,quantity = :qua")					
					.withValueMap(new ValueMap().withNumber(":val", (Number) itemCost).withNumber(":qua", (Number)2));
					
			UpdateItemOutcome outcome = orderItemTable.updateItem(updateItemSpec);
			outcome.getItem();
			Item orderItem = orderTable.getItem("uuid", order.getString("orderuuid"));
			Number totalcost = orderItem.getNumber("totalBillWithTax");			
			Double bill = totalcost.doubleValue();
			System.out.println(bill);
			Double taxper = .06;
			Double tax = (double) itemCost * taxper.doubleValue();
			System.out.println(tax);
			Double totalValueWithTax = bill + tax + (double) itemCost ;
			System.out.println(totalValueWithTax);

			Number total = totalcost.doubleValue() + itemCost.doubleValue();
			UpdateItemSpec updateItemSpec1 = new UpdateItemSpec().withPrimaryKey("uuid", order.getString("orderuuid"))
					.withUpdateExpression("set totalBillWithTax = :val, orderStatus = :ord")
					.withValueMap(new ValueMap().withNumber(":val", (Number) totalValueWithTax).withString(":ord", "ACCEPTED"));
					
			UpdateItemOutcome outcome1 = orderTable.updateItem(updateItemSpec1);
			outcome1.getItem();
			AlexaResponse alexaResponse = new AlexaResponse();
			Response response = new Response();
			OutputSpeech outputSpeech = new OutputSpeech();
			outputSpeech.setSsml(
					"<speak>How much Spicy Level do u want to add for  this order We have Mild , Medium and Spicy. </speak>");
			outputSpeech.setType("SSML");
			Card card = new Card();
			card.setTitle("Audio");
			card.setType("Simple");
			card.setContent("How much Spicy Level do u want to add for  this order We have Mild , Medium and Spicy.");
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
	/*public static void main(String[] args) {
		AlexaTwoQuantityServiceImpl FbLoginServiveImpl = new AlexaTwoQuantityServiceImpl();
		Context context = null;
		AlexaDTO alexaDTO= new AlexaDTO();
		
		alexaDTO.setUserId("AFHFYXC47SSN4DSEXYTINPFRSEGCVEN3X5RX5BNA342NRCA73VZX4G43FCQF5TI7L5RFEJD6HHQ2VXPLUSQSSHRJSIBOBZIIFYUWUFOR7Z7PVLX27NJYJYURDFOMJHVWWYE2MPXASCBCNRQUNSJPOYQKAS7IBXAYDKIWIBUIKE3WISM6OD25DFXX344QDDPLOVIUGZCVU2Y5A3A");
		FbLoginServiveImpl.serveLex(alexaDTO, context);
		}*/

}
