package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.GoogleDTO;
import com.moxieit.orderplatform.lambda.response.GoogleResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.repackaged.com.google.api.client.util.Data;
import com.google.gson.Gson;

public class GoogleTransactionDecision {
	public enum priceType {ACTUAL, ESTIMATE, UNKNOWN};
	public enum type {REGULAR, TAX, SUBTOTAL, FEE};

	String itemName= "";
	String quantity= "";
	String itemCost= "";
	String categoryId= "";
	String itemId= "";
	boolean isSpicy;
	 static int i=1;
	@SuppressWarnings("unchecked")
	public String serveLex(GoogleDTO googleDTO, Context context) {
		
		
		 DynamoDB dynamoDB = DBService.getDBConnection();
        JSONObject responseJson = new JSONObject();   
        JSONObject responseJson1 = new JSONObject(); 
        JSONObject paymentOptions = new JSONObject();
        JSONObject systemIntent = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject orderOptions = new JSONObject();
        JSONObject tokenizationParameters = new JSONObject();
        JSONObject tokenizationParameters1 = new JSONObject();
        JSONObject parameters = new JSONObject();
        JSONObject customerInfoOptions = new JSONObject();
        JSONObject customerInfoProperties = new JSONObject();
        JSONObject google = new JSONObject();
        JSONObject proposedOrder = new JSONObject();     
        JSONObject finalproposedOrder = new JSONObject();
        JSONObject merchant = new JSONObject();
    	ObjectMapper mapper = new ObjectMapper();
    	
       
    	JSONObject cart = new JSONObject(); 
    	JSONObject lineItems = new JSONObject(); 
    	JSONObject lineItems1 = new JSONObject();
    	JSONObject otherItem = new JSONObject(); 
    	
    	
    	
    	JSONObject image = new JSONObject(); 
    	image.put("url","https://s3.amazonaws.com/restaurantimages2017/Menu_Items/Veg+Dum+Biryani.jpg");
    	image.put("accessibilityText","JPG");
    	
    	Table orderItemTable = dynamoDB.getTable("OrderItems");
    	Table orderTable = dynamoDB.getTable("Order");	
        String orderuuid ="";
        String totalBillWithTax = "";
        String totalBill = "";
        String tax = "";
       
   	/*ScanExpressionSpec xspec1 = new ExpressionSpecBuilder().withCondition(S("userId").eq(googleDTO.getUserId())
				.and(N("creationDate").ge(System.currentTimeMillis() - 900000)).and(S("orderFrom").eq("GoogleHome"))
				.and(S("orderStatus").eq("Initiated")))
				.buildForScan();
			
		ItemCollection<ScanOutcome> scan1 = orderTable.scan(xspec1);
		Item order = null;        
		Page<Item, ScanOutcome> firstPage = scan1.firstPage();	
		System.out.println("firstPage :"+firstPage);
		if (firstPage.iterator().hasNext()) {
			order = firstPage.iterator().next();
			System.out.println("order :"+order);				
		
			Item ordertableuuid = orderTable.getItem("uuid", order.getString("uuid"));
			orderuuid = ordertableuuid.getString("uuid");
			totalBillWithTax = ordertableuuid.getString("totalBillWithTax");
		}*/
        
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
			dates.add(recentDate);
		
			}

		};
		scan.forEach(action);
		Date latest = Collections.max(dates);	
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
		}

			 
		 
	      ArrayList<Object> obj = new ArrayList<Object>();
	    	
	    	 ScanExpressionSpec xspec4 = new ExpressionSpecBuilder().withCondition(S("orderuuid").eq(orderuuid).and(S("itemQuantityAdd").eq("true")))
				.buildForScan();
		ItemCollection<ScanOutcome> scan4 = orderItemTable.scan(xspec4);

		Consumer<Item> action4 = new Consumer<Item>() {
			@Override
			public void accept(Item t3) {
				
				Table menuItemTable = dynamoDB.getTable("Menu_Items");
				Object categoryObject = t3.getString("categoryId");
				Object itemObject = t3.getString("menuItemId");
				Item menuItem = menuItemTable.getItem("categoryId", categoryObject, "itemId", itemObject);						
				itemName = menuItem.getString("itemName");
				quantity = t3.getString("quantity");
				itemCost = t3.getString("itemCost");
				categoryId = t3.getString("categoryId");
				itemId = t3.getString("menuItemId");
				isSpicy = t3.getBoolean("isSpicy");
		String name = menuItem.getString("itemName");
		String price = t3.getString("itemCost");
		String quantity = t3.getString("quantity");
		String image1 = menuItem.getString("image");
		String orderuuid = t3.getString("orderuuid");
		
		Double taxper = .06;
		Double tax = Double.parseDouble(price) * taxper.doubleValue();
		Double totalValueWithTax =  Double.parseDouble(price) + tax;
	
		// double d = totalValueWithTax;
		 double d =  Double.parseDouble(price);
		 int units = (int) d;
		 double nanos = d - (long) d;
		 
		 Locale locale = Locale.ENGLISH;
		   NumberFormat nf = NumberFormat.getNumberInstance(locale);
		   // for trailing zeros:
		   nf.setMinimumFractionDigits(9);
		   // round to 2 digits:
		   nf.setMaximumFractionDigits(9);
					   
		   double nano1 = Double.parseDouble(nf.format(nanos)) * 1000000000;
		   int nano = (int) nano1;
		   System.out.println("nano value "+nano);
		   System.out.println("units value "+units);
		
		JSONObject image = new JSONObject(); 
    	image.put("url",image1);
    	image.put("accessibilityText","JPG");
    	
		   JSONObject price1 = new JSONObject(); 
	    	JSONObject amount = new JSONObject();
	    	amount.put("currencyCode","USD");
	    	amount.put("units", units);
	    	amount.put("nanos", nano);
	    	price1.put("type",priceType.ACTUAL);
	    	price1.put("amount",amount);
		
		JSONObject newobj = new JSONObject();
		newobj.put("name", name);
		newobj.put("price", price1);
		newobj.put("quantity", quantity);
		newobj.put("image", image);
		newobj.put("id", orderuuid);
		newobj.put("type",type.REGULAR);
		
				i++;
				obj.add(newobj);
			/*BODY.append(menuItem.getString("itemName"))
			.append(",Quantity ")
			.append(t3.getString("quantity")).append(",Total cost is ")
			.append(t3.getString("itemCost")).append(" Dollars");	*/	


			}

		};
		scan4.forEach(action4);
    
    	 ArrayList<Object> otherItems = new ArrayList<Object>();
    	 
    	 double totalBillvalue =  Double.parseDouble(totalBill);
    	 int totalunits = (int) totalBillvalue;
		 double totalnanos = totalBillvalue - (long) totalBillvalue;
		 
		 Locale locale = Locale.ENGLISH;
		   NumberFormat nf = NumberFormat.getNumberInstance(locale);
		   // for trailing zeros:
		   nf.setMinimumFractionDigits(9);
		   // round to 2 digits:
		   nf.setMaximumFractionDigits(9);
					   
		   double totalnano1 = Double.parseDouble(nf.format(totalnanos)) * 1000000000;
		   int totalnano = (int) totalnano1;
		   
		   JSONObject otherItemsPrice = new JSONObject(); 
	    	JSONObject amount = new JSONObject();
	    	amount.put("currencyCode","USD");
	    	amount.put("units", totalunits);
	    	amount.put("nanos", totalnano);
	    	otherItemsPrice.put("type",priceType.ESTIMATE);
	    	otherItemsPrice.put("amount",amount);
    	 JSONObject otherItems1 = new JSONObject();
    	 otherItems1.put("name", "Subtotal");
    	 otherItems1.put("price", otherItemsPrice);    	   		
    	 otherItems1.put("type",type.SUBTOTAL);
    	 
    	 double totaltaxvalue =  Double.parseDouble(tax);
    	 int totaltaxunits = (int) totaltaxvalue;
		 double totaltaxnanos = totaltaxvalue - (long) totaltaxvalue;
						   
		   double totaltaxnano1 = Double.parseDouble(nf.format(totaltaxnanos)) * 1000000000;
		   int totaltaxnano = (int) totaltaxnano1;
		   
		   JSONObject otherItemsPrice2 = new JSONObject(); 
	    	JSONObject amount2 = new JSONObject();
	    	amount2.put("currencyCode","USD");
	    	amount2.put("units", totaltaxunits);
	    	amount2.put("nanos", totaltaxnano);
	    	otherItemsPrice2.put("type",priceType.ESTIMATE);
	    	otherItemsPrice2.put("amount",amount2);
    	 JSONObject otherItems2 = new JSONObject();
    	 otherItems2.put("name", "Subtotal");
    	 otherItems2.put("price", otherItemsPrice2);       		
    	 otherItems2.put("type",type.TAX);
    	 
    	 otherItems.add(otherItems1);
    	 otherItems.add(otherItems2);
		
			 
			 double d = Double.parseDouble((String) totalBillWithTax);
			 int units = (int) d;
			 double nanos = d - (long) d;
			 
								   
			   double nano1 = Double.parseDouble(nf.format(nanos)) * 1000000000;
			   int nano = (int) nano1;
			  	// double nanos = Double.parseDouble(tax) * 100;
						 
		   JSONObject price = new JSONObject();
	    	JSONObject finalamount = new JSONObject();
	    	finalamount.put("currencyCode","USD");
	    	finalamount.put("units", units);
	    	finalamount.put("nanos", nano);
	    	price.put("type",priceType.ACTUAL);
	    	price.put("amount",finalamount);
		   
    
        ArrayList<String> customerInfoProperties1 = new ArrayList<String>();
        customerInfoProperties1.add("EMAIL");
      String result = googleDTO.getTransactionCheckResult();
     if (result.equals("OK")){

        try {
        	merchant.put("name", "Sitara Indian Cuisine");
        	proposedOrder.put("otherItems", otherItems);
        	proposedOrder.put("lineItems", obj);
        	proposedOrder.put("merchant", merchant);
        	finalproposedOrder.put("cart", proposedOrder);        	
        	finalproposedOrder.put("totalPrice", price);
        	parameters.put("gateway", "stripe"); 
        	parameters.put("stripe:publishableKey", "pk_test_V7nawTDc9yKNRCEUEF3eMARZ"); 
        	parameters.put("stripe:version", "2017-05-25"); 
        	tokenizationParameters.put("tokenizationType", "PAYMENT_GATEWAY"); 
        	tokenizationParameters.put("parameters", parameters); 
        	orderOptions.put("requestDeliveryAddress", false);
        	tokenizationParameters1.put("tokenizationParameters", tokenizationParameters);
            data.put("@type", "type.googleapis.com/google.actions.v2.TransactionDecisionValueSpec");
            data.put("proposedOrder", finalproposedOrder);
            customerInfoProperties.put("customerInfoProperties", customerInfoProperties1);
            customerInfoOptions.put("customerInfoOptions", customerInfoProperties); 
            data.put("orderOptions", customerInfoOptions); 
           
            paymentOptions.put("googleProvidedOptions", tokenizationParameters1);
            data.put("paymentOptions", paymentOptions);                  
            systemIntent.put("intent", "actions.intent.TRANSACTION_DECISION");
            systemIntent.put("data", data);        
            responseJson1.put("systemIntent", systemIntent); 
            google.put("google", responseJson1);  
            responseJson.put("data", google);       
          
            responseJson.put("speech", "your payment was successful.");
            System.out.println("myString "+ responseJson);

          	UpdateItemSpec updateItemSpec2 = new UpdateItemSpec().withPrimaryKey("uuid", orderuuid)
    				.withUpdateExpression("set paymentDone =:pd,  paymentMethod = :pay,  orderStatus = :sta")
    				.withValueMap(new ValueMap().withString(":pd", "true").withString(":pay", "Card").withString(":sta", "ACCEPTED"));
    		UpdateItemOutcome outcome2 = orderTable.updateItem(updateItemSpec2);
    		outcome2.getItem();
   
        } catch(Exception pex) {
            responseJson.put("statusCode", "400");
            responseJson.put("exception", pex);
        }
        

        
 } else {
	  responseJson.put("speech", "Transaction was cancelled.");
	  UpdateItemSpec updateItemSpec2 = new UpdateItemSpec().withPrimaryKey("uuid", orderuuid)
				.withUpdateExpression("set paymentDone =:pd, paymentMethod = :pay")
				.withValueMap(new ValueMap().withString(":pd", "false").withString(":pay", "Card"));
		UpdateItemOutcome outcome2 = orderTable.updateItem(updateItemSpec2);
		outcome2.getItem();
		
      UpdateItemSpec updateItemSpec3 = new UpdateItemSpec().withPrimaryKey("uuid", orderuuid)
				.withUpdateExpression("set orderStatus = :sta")
				.withValueMap(new ValueMap().withString(":sta", "CANCELLED"));					
		UpdateItemOutcome outcome3 = orderTable.updateItem(updateItemSpec3);
		outcome3.getItem();	
  }
        
		GoogleResponse googleResponse = new GoogleResponse();		  
		googleResponse.setSpeech(responseJson.toString());
		return responseJson.toString();
	        
	    }
	public static void main(String[] args) {
		GoogleTransactionDecision googleService = new GoogleTransactionDecision();
		GoogleDTO googleDTO = new GoogleDTO();	
		googleDTO.setTransactionCheckResult("OK");
		googleDTO.setUserId("ABwppHHu5052upleEQrsWad_QHD4CayzF4mk24Gu1Pd3Dxn4JVd7jBSiTi3CXE7k00B7huIZJqu3QIwPAMiIh44b6Q");
		Context context = null ;
		googleService.serveLex(googleDTO, context);
		}
}
