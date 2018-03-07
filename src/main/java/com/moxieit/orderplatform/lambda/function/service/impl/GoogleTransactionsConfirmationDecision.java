package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import org.json.simple.JSONObject;

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
import com.moxieit.orderplatform.function.service.api.GoogleDTO;
import com.moxieit.orderplatform.lambda.function.service.impl.GoogleTransactionDecision.priceType;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;


public class GoogleTransactionsConfirmationDecision {

	public enum type {CALL, CUSTOMER_SERVICE, CANCEL, MODIFY};
	
		@SuppressWarnings({ "unused", "unchecked" })
		public String serveLex(GoogleDTO googleDTO, Context context) {
		
			String stripeToken = "";
				
	        JSONObject responseJson = new JSONObject(); 
	        JSONObject orderState = new JSONObject(); 
	        ArrayList<Object> orderManagementActions = new ArrayList<Object>();
	    	JSONObject Action = new JSONObject();
	        JSONObject button = new JSONObject(); 
	        JSONObject openUrlAction = new JSONObject();
	        JSONObject receipt = new JSONObject();	        
	        JSONObject responseJson1 = new JSONObject();	        
	        JSONObject data = new JSONObject();
	        JSONObject google = new JSONObject();
	        JSONObject systemIntent = new JSONObject();
	        JSONObject responseJson2 = new JSONObject();
	        
	        DynamoDB dynamoDB = DBService.getDBConnection();
	    	Table orderItemTable = dynamoDB.getTable("OrderItems");
	    	Table orderTable = dynamoDB.getTable("Order");	
	        String orderuuid ="";
	        String totalBillWithTax = "";
	        String totalBill = "";
	        String tax = "";
	        String creationDate = "";
	        
			List<Date> dates = new ArrayList<Date>();
			Calendar calendar = Calendar.getInstance();

			DateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");		
			ScanExpressionSpec xspec = new ExpressionSpecBuilder().withCondition(S("userId").eq(googleDTO.getUserId())
					.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("true")))
					.buildForScan();
			
			ItemCollection<ScanOutcome> scan = orderItemTable.scan(xspec);

			Consumer<Item> action = new Consumer<Item>() {
				public void accept(Item t) {
				
				Number creationDate = t.getNumber("creationDate");
				String x = creationDate.toString();				
				long milliSeconds = Long.parseLong(x);					
				calendar.setTimeInMillis(milliSeconds);		
				Date recentDate = null;		
					//recentDate = (Date) formatter1.parse(orderDate);
					recentDate = (Date) calendar.getTime();				
					System.out.println("recentDate date :"+recentDate);			
				dates.add(recentDate);
				System.out.println("dates date :"+dates);

				}

			};
			scan.forEach(action);
			Date latest = Collections.max(dates);
			System.out.println("latest date :"+latest);
			long itemdateMilliSec = latest.getTime();
			System.out.println("itemdateMilliSec date :"+itemdateMilliSec);
			
	        String phoneNumcode = "";
	        String 	botName = "";
	    	ScanExpressionSpec xspec1 = new ExpressionSpecBuilder().withCondition(S("userId").eq(googleDTO.getUserId())
					.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("itemQuantityAdd").eq("true"))
					.and(N("creationDate").eq((Number)itemdateMilliSec)))
					.buildForScan();
				
			ItemCollection<ScanOutcome> scan1 = orderItemTable.scan(xspec1);
			Item order = null;        
			Page<Item, ScanOutcome> firstPage = scan1.firstPage();	
			System.out.println("firstPage :"+firstPage);
			if (firstPage.iterator().hasNext()) {
				order = firstPage.iterator().next();
				System.out.println("order :"+order);				
			
				Item ordertableuuid = orderTable.getItem("uuid", order.getString("orderuuid"));
				orderuuid = ordertableuuid.getString("uuid");
				totalBillWithTax = ordertableuuid.getString("totalBillWithTax");
				totalBill = ordertableuuid.getString("totalBill");
				tax = ordertableuuid.getString("tax");
				phoneNumcode = ordertableuuid.getString("phoneNumber");
				  botName = ordertableuuid.getString("botName");
				  creationDate = ordertableuuid.getString("creationDate");
			}
			
			Number totalBill1 =  Double.parseDouble((String)totalBillWithTax);
			Double totalBillDouble = totalBill1.doubleValue() * 100;
			Integer totalBillInteger = totalBillDouble.intValue();
			String totalBillString = totalBillInteger.toString();
			Stripe.apiKey = "sk_test_h8l3zo52N4fKamsxtuTBQ0lH";
			Map<String, Object> chargeParams = new HashMap<String, Object>();
			chargeParams.put("amount", totalBillString);
			chargeParams.put("currency", "usd");
			chargeParams.put("description", "Charge for ");
			chargeParams.put("source", stripeToken);
			// ^ obtained with Stripe.js
			try {
				Charge.create(chargeParams);
			} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException
					| APIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		String googleOrderId = "1234";
		String actionOrderId = orderuuid;
		String updateTime = "";
		
		orderState.put("state", "CONFIRMED");
		orderState.put("label", "Your order was CONFIRMED");
		
		button.put("title", "Call");
		openUrlAction.put("url", "http://www.sitaracuisine.com/contact.php");
		button.put("openUrlAction", openUrlAction);
		Action.put("type", type.CALL);
		Action.put("button", button);
		orderManagementActions.add(Action);
		receipt.put("confirmedActionOrderId", orderuuid);
		
		DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		long milliSeconds = Long.parseLong(creationDate);					
		calendar.setTimeInMillis(milliSeconds);
		//f.setTimeZone(Long.parseLong(creationDate));
		try {
			Date recentDate = (Date) f.parse(f.format(calendar.getTime()));
			System.out.println("recentDate :"+recentDate);	
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		 double d = Double.parseDouble((String) totalBillWithTax);
		 int units = (int) d;
		 double nanos = d - (long) d;
		 
		 Locale locale = Locale.ENGLISH;
		   NumberFormat nf = NumberFormat.getNumberInstance(locale);
		   // for trailing zeros:
		   nf.setMinimumFractionDigits(9);
		   // round to 2 digits:
		   nf.setMaximumFractionDigits(9);
					   
		   double nano1 = Double.parseDouble(nf.format(nanos)) * 100;
		   int nano = (int) nano1;
		
			// double nanos = Double.parseDouble(tax) * 100;
					 
	   JSONObject price = new JSONObject();
   	JSONObject amount = new JSONObject();
   	amount.put("currencyCode","USD");
   	amount.put("units", units);
   	amount.put("nanos", nano);
   	price.put("type",priceType.ACTUAL);
   	price.put("amount",amount);
		

   	
		responseJson1.put("googleOrderId", googleOrderId);
		responseJson1.put("actionOrderId", actionOrderId);
		responseJson1.put("orderState", orderState);
		responseJson1.put("orderManagementActions", orderManagementActions);
		responseJson1.put("receipt", receipt);
		responseJson1.put("totalPrice", price);
		
		 systemIntent.put("data", responseJson1);        
         responseJson2.put("systemIntent", systemIntent); 
         google.put("google", responseJson2);  
         responseJson.put("data", google); 
         
		responseJson.put("speech", "Transaction completed! your order was confirmed.");
		   System.out.println("result "+responseJson.toString());
		return responseJson.toString();
		
	}
		public static void main(String[] args) {
			GoogleTransactionsConfirmationDecision googleService = new GoogleTransactionsConfirmationDecision();
			GoogleDTO googleDTO = new GoogleDTO();	
			googleDTO.setUserId("ABwppHHu5052upleEQrsWad_QHD4CayzF4mk24Gu1Pd3Dxn4JVd7jBSiTi3CXE7k00B7huIZJqu3QIwPAMiIh44b6Q");
			googleDTO.setRequest("card");
			Context context = null ;
			googleService.serveLex(googleDTO, context);
			}
}
