package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.AlexaService;



public abstract class AbstractAlexaOrderServiceImpl implements AlexaService {
	static DynamoDB dynamoDB = DBService.getDBConnection();
	static Table orderTable = dynamoDB.getTable("Order");

	protected Item getOrder(String userId) {
		List<String> orderStatuses = new ArrayList<>();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		long milliSeconds = System.currentTimeMillis();
		System.out.println(milliSeconds);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		String date = formatter.format(calendar.getTime());
		orderStatuses.add("Initiated");
		orderStatuses.add("Payment Pending");
		ScanExpressionSpec xspec = new ExpressionSpecBuilder().withCondition(S("userId").eq(userId)
				.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("orderStatus").in(orderStatuses)))
				.buildForScan();

		ItemCollection<ScanOutcome> scan = orderTable.scan(xspec);
		Item order = null;

		Page<Item, ScanOutcome> firstPage = scan.firstPage();
		if (firstPage.iterator().hasNext()) {
			order = firstPage.iterator().next();
			UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uuid", order.get("uuid"))
					.withUpdateExpression("set creationDate = :val")
					.withValueMap(new ValueMap().withNumber(":val", System.currentTimeMillis()));
			UpdateItemOutcome outcome = orderTable.updateItem(updateItemSpec);
			outcome.getItem();
		}
		if (order == null) {
			String uuid = UUID.randomUUID().toString();
			order = new Item();
			order.withString("uuid", uuid).withString("userId", userId).withString("orderStatus", "Initiated")
					.withNumber("creationDate", System.currentTimeMillis()).withNumber("totalBill", 0)
					.withNumber("tax", 0).withNumber("totalBillWithTax", 0).withString("orderTracking", "ACCEPTED")
					.withString("orderDate", date).withString("returnMessage", "true")
					.withString("paymentDone", "false").withString("orderFrom","Alexa")
					.withString("restaurantId","23").withString("botName", "SITARA");
				
			orderTable.putItem(order);
		}
		return order;
	}

	public static void updateCreationDate(String uuid) {

		UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("uuid", uuid)
				.withUpdateExpression("set creationDate = :val")
				.withValueMap(new ValueMap().withNumber(":val", System.currentTimeMillis()));
		orderTable.updateItem(updateItemSpec);

	}
	

}
