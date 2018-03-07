package com.moxieit.orderplatform.lambda.function.service.impl;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.N;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.moxieit.orderplatform.DB.DBService;
import com.moxieit.orderplatform.function.service.api.GoogleDTO;
import com.moxieit.orderplatform.lambda.request.SesRequest;
import com.moxieit.orderplatform.lambda.response.GoogleResponse;

public class GoogleTransactionRequirementsCheckServiceImpl {

	String itemName= "";
	String quantity= "";
	String itemCost= "";
	String categoryId= "";
	String itemId= "";
	boolean isSpicy;
	@SuppressWarnings("unchecked")
	public String serveLex(GoogleDTO googleDTO, Context context) {
		DynamoDB dynamoDB = DBService.getDBConnection();
		Table orderItemTable = dynamoDB.getTable("OrderItems");
		StringBuilder BODY = new StringBuilder();	
        JSONObject responseJson = new JSONObject(); 
        JSONObject cash = new JSONObject();   
        JSONObject responseJson1 = new JSONObject(); 
        JSONObject paymentOptions = new JSONObject();
        JSONObject systemIntent = new JSONObject();
        JSONObject google = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject orderOptions = new JSONObject();
        JSONObject googleProvidedOptions = new JSONObject();
        
        ArrayList<String> obj = new ArrayList<String>();
        obj.add("VISA");
        obj.add("AMEX");
        obj.add("DISCOVER");
        obj.add("MASTERCARD");
       // obj.add("JCB");
        String orderuuid ="";
        String totalBillWithTax ="";
        String phoneNumcode = "";
        String 	botName = "";

        try {
        	
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
				Table orderTable = dynamoDB.getTable("Order");	
				Item ordertableuuid = orderTable.getItem("uuid", order.getString("orderuuid"));
				orderuuid = ordertableuuid.getString("uuid");
				totalBillWithTax = ordertableuuid.getString("totalBillWithTax");
				phoneNumcode = ordertableuuid.getString("phoneNumber");
				  botName = ordertableuuid.getString("botName");
			}
			
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
					
				BODY.append(menuItem.getString("itemName")).append(" ,");
				/*.append(",Quantity ")
				.append(t3.getString("quantity")).append(",Total cost is ")
				.append(t3.getString("itemCost")).append(" Dollars");	*/		


				}

			};
			scan4.forEach(action4);
			System.out.println(BODY);
			String itemsbody = BODY.toString();
			  System.out.println("BODY "+ itemsbody);
         
 
            String userOrder = googleDTO.getRequest();
            if (userOrder.toLowerCase().replaceAll("\\s+","").contains("card")){
            	            		
            		Table restaurantTable = dynamoDB.getTable("Restaurant");
            		
            		System.out.println("phoneNumcode"+phoneNumcode);
            	 AmazonSNSClient snsClient = new AmazonSNSClient();
            
                         
     			Item botnameitem = restaurantTable.getItem("botName", botName);
                 String restaurantName = botnameitem.getString("restaurantName");
     			String restaurantphn = botnameitem.getString("phone_no");    			            
     		     String snsmessage = "Your Order was placed."
  		        		+ "If you want to make any changes to this order contact "+restaurantName+" – "
  		        		+ restaurantphn;
  		       String snsmessage1 = restaurantName+" | Online Receipt\n"
  		        		+ "https://izzad1rdrh.execute-api.us-east-1.amazonaws.com/dev/recipt?order="+orderuuid;
 		        //String phoneNumber = "+919493689846";
 		        Map<String, MessageAttributeValue> smsAttributes = 
 		                new HashMap<String, MessageAttributeValue>();
 		        //<set SMS attributes>
 		        sendSMSMessage(snsClient, snsmessage, phoneNumcode, smsAttributes);
                 
 		        SESorderitems sesorderitems = new SESorderitems();
     			SesRequest sesRequest = new SesRequest();
     			sesRequest.setOrdertableuuid(order.getString("orderuuid"));
     			sesRequest.setPhoneNumber(phoneNumcode);
     			try {
 					sesorderitems.getSES(sesRequest);
 				} catch (IOException e) {
 					// TODO Auto-generated catch block
 					e.printStackTrace();
 				}
     			sendSMSMessage(snsClient, snsmessage1, phoneNumcode, smsAttributes);
      
            	orderOptions.put("requestDeliveryAddress", false);
            	googleProvidedOptions.put("prepaidCardDisallowed", false);
                googleProvidedOptions.put("supportedCardNetworks", obj); 
                data.put("@type", "type.googleapis.com/google.actions.v2.TransactionRequirementsCheckSpec");
                data.put("orderOptions", orderOptions); 
                paymentOptions.put("googleProvidedOptions", googleProvidedOptions);
                data.put("paymentOptions", paymentOptions);                  
                systemIntent.put("intent", "actions.intent.TRANSACTION_REQUIREMENTS_CHECK");
                systemIntent.put("data", data);        
                responseJson1.put("systemIntent", systemIntent);
                google.put("google", responseJson1);  
                responseJson.put("data", google); 
            	 responseJson.put("speech", "your order is "+BODY+" and Total cost is "+totalBillWithTax+ " Dollars. would you like to proceed to pay, or check your account balance?");
            	
            }
           
            System.out.println("myString "+ responseJson);


        } catch(Exception pex) {
            responseJson.put("statusCode", "400");
            responseJson.put("exception", pex);
        }
		
		return responseJson.toString();
	        
	    }
	public static void sendSMSMessage(AmazonSNSClient snsClient, String message, 
			String phoneNumcode, Map<String, MessageAttributeValue> smsAttributes) {
	        PublishResult result = snsClient.publish(new PublishRequest()
	                        .withMessage(message)
	                        .withPhoneNumber(phoneNumcode)
	                        .withMessageAttributes(smsAttributes));
	        System.out.println(result);
	}
	public static void main(String[] args) {
		GoogleTransactionRequirementsCheckServiceImpl googleService = new GoogleTransactionRequirementsCheckServiceImpl();
		GoogleDTO googleDTO = new GoogleDTO();	
		googleDTO.setUserId("ABwppHHu5052upleEQrsWad_QHD4CayzF4mk24Gu1Pd3Dxn4JVd7jBSiTi3CXE7k00B7huIZJqu3QIwPAMiIh44b6Q");
		googleDTO.setRequest("card");
		Context context = null ;
		googleService.serveLex(googleDTO, context);
		}
}
